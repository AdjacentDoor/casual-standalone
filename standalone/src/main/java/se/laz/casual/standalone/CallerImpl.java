package se.laz.casual.standalone;

import se.laz.casual.api.buffer.CasualBuffer;
import se.laz.casual.api.buffer.ServiceReturn;
import se.laz.casual.api.flags.AtmiFlags;
import se.laz.casual.api.flags.Flag;
import se.laz.casual.api.queue.DequeueReturn;
import se.laz.casual.api.queue.EnqueueReturn;
import se.laz.casual.api.queue.MessageSelector;
import se.laz.casual.api.queue.QueueInfo;
import se.laz.casual.api.queue.QueueMessage;
import se.laz.casual.api.service.ServiceDetails;
import se.laz.casual.network.ProtocolVersion;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CallerImpl implements Caller
{
    private final InetSocketAddress address;
    private final ProtocolVersion protocolVersion;
    private final UUID domainId;
    private final String domainName;

    private CallerImpl(InetSocketAddress address, ProtocolVersion protocolVersion, UUID domainId, String domainName)
    {
        this.address = address;
        this.protocolVersion = protocolVersion;
        this.domainId = domainId;
        this.domainName = domainName;
    }

    public static Caller of(InetSocketAddress address, ProtocolVersion protocolVersion, UUID domainId, String domainName)
    {
        Objects.requireNonNull(address, "address can not be null");
        Objects.requireNonNull(protocolVersion, "protocolVersion can not be null");
        Objects.requireNonNull(domainId, "domainId can not be null");
        Objects.requireNonNull(domainName, "domainName can not be null");
        return new CallerImpl(address, protocolVersion, domainId, domainName);
    }

    @Override
    public EnqueueReturn enqueue(QueueInfo qinfo, QueueMessage msg)
    {
        return null;
    }

    @Override
    public DequeueReturn dequeue(QueueInfo qinfo, MessageSelector selector)
    {
        return null;
    }

    @Override
    public boolean queueExists(QueueInfo qinfo)
    {
        return false;
    }

    @Override
    public ServiceReturn<CasualBuffer> tpcall(String serviceName, CasualBuffer data, Flag<AtmiFlags> flags)
    {
        return null;
    }

    @Override
    public CompletableFuture<ServiceReturn<CasualBuffer>> tpacall(String serviceName, CasualBuffer data, Flag<AtmiFlags> flags)
    {
        return null;
    }

    @Override
    public boolean serviceExists(String serviceName)
    {
        return false;
    }

    @Override
    public List<ServiceDetails> serviceDetails(String serviceName)
    {
        return null;
    }
}
