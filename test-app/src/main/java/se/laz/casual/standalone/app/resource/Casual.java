package se.laz.casual.standalone.app.resource;

import jakarta.annotation.Resource;
import jakarta.ejb.EJBContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;
import se.laz.casual.api.buffer.CasualBuffer;
import se.laz.casual.api.buffer.ServiceReturn;
import se.laz.casual.api.buffer.type.OctetBuffer;
import se.laz.casual.api.flags.AtmiFlags;
import se.laz.casual.api.flags.Flag;
import se.laz.casual.api.flags.ServiceReturnState;
import se.laz.casual.network.ProtocolVersion;
import se.laz.casual.network.outbound.NetworkListener;
import se.laz.casual.standalone.Caller;
import se.laz.casual.standalone.CallerImpl;
import se.laz.casual.standalone.app.ServiceCallFailedException;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Path("casual")
public class Casual implements NetworkListener
{
    private static final Logger LOG = Logger.getLogger(Casual.class.getName());
    public static String CASUAL_HOST_ENV = "CASUAL_HOST";
    public static String CASUAL_PORT_ENV = "CASUAL_PORT";
    public static String DOMAIN_NAME = "JAVA-TEST_APP-" + UUID.randomUUID();

    @Resource
    private EJBContext ctx;

    private Caller caller = createCaller();

    private Caller createCaller()
    {
        final String hostName = Optional.ofNullable(System.getenv(CASUAL_HOST_ENV)).orElseThrow(() -> new RuntimeException(CASUAL_HOST_ENV + " not set"));
        final int port = Integer.parseInt(Optional.ofNullable(System.getenv(CASUAL_PORT_ENV)).orElseThrow(() -> new RuntimeException(CASUAL_PORT_ENV + " not set")));
        InetSocketAddress address = new InetSocketAddress(hostName, port);
        return CallerImpl.createBuilder()
                .withAddress(address)
                .withDomainId(UUID.randomUUID())
                .withDomainName(DOMAIN_NAME)
                .withNetworkListener(this)
                .withProtocolVersion(ProtocolVersion.VERSION_1_0)
                .withResourceManagerId(42)
                .build();
    }

    @POST
    @Consumes("application/casual-x-octet")
    @Path("{serviceName}")
    public Response echoRequest(@PathParam("serviceName") String serviceName, InputStream inputStream)
    {
        try
        {
            byte[] data = IOUtils.toByteArray(inputStream);
            Flag<AtmiFlags> flags = Flag.of(AtmiFlags.NOFLAG);
            OctetBuffer buffer = OctetBuffer.of(data);
            return Response.ok().entity(makeCasualCall(buffer, serviceName, flags).getBytes().get(0)).build();
        }
        catch (Exception e)
        {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            if (!ctx.getRollbackOnly())
            {
                ctx.setRollbackOnly();
            }
            return Response.serverError().entity(sw.toString()).build();
        }
    }

    private CasualBuffer makeCasualCall(CasualBuffer msg, String serviceName, Flag<AtmiFlags> flags)
    {
        ServiceReturn<CasualBuffer> reply = caller.tpcall(serviceName, msg, flags);
        if(reply.getServiceReturnState() == ServiceReturnState.TPSUCCESS)
        {
            return reply.getReplyBuffer();
        }
        throw new ServiceCallFailedException("tpcall failed: " + reply.getErrorState());
    }

    @Override
    public void disconnected()
    {
        LOG.warning(() -> "casual disconnected!");
    }
}
