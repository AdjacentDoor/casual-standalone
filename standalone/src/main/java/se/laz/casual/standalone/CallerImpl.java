package se.laz.casual.standalone;

import se.laz.casual.api.buffer.CasualBuffer;
import se.laz.casual.api.buffer.ServiceReturn;
import se.laz.casual.api.flags.AtmiFlags;
import se.laz.casual.api.flags.ErrorState;
import se.laz.casual.api.flags.Flag;
import se.laz.casual.api.flags.ServiceReturnState;
import se.laz.casual.api.queue.DequeueReturn;
import se.laz.casual.api.queue.EnqueueReturn;
import se.laz.casual.api.queue.MessageSelector;
import se.laz.casual.api.queue.QueueInfo;
import se.laz.casual.api.queue.QueueMessage;
import se.laz.casual.api.service.ServiceDetails;
import se.laz.casual.network.api.NetworkConnection;
import se.laz.casual.network.ProtocolVersion;
import se.laz.casual.network.outbound.CorrelatorImpl;
import se.laz.casual.network.outbound.NettyConnectionInformation;
import se.laz.casual.network.outbound.NettyNetworkConnection;
import se.laz.casual.network.outbound.NetworkListener;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class CallerImpl implements Caller
{
    private static final Logger LOG = Logger.getLogger(CallerImpl.class.getName());
    private final CasualConnection casualConnection;
    private final Transactional transactional;
    private final ServiceCaller serviceCaller;
    private final Set<String> serviceCache = new HashSet<>();
    private final Set<String> queueCache = new HashSet<>();

    private CallerImpl(CasualConnection casualConnection, Transactional transactional, ServiceCaller serviceCaller)
    {
        this.casualConnection = casualConnection;
        this.transactional = transactional;
        this.serviceCaller = serviceCaller;
    }

    private static Caller of(InetSocketAddress address, ProtocolVersion protocolVersion, UUID domainId, String domainName, NetworkListener networkListener, int resourceManagerId)
    {
        Objects.requireNonNull(address, "address can not be null");
        Objects.requireNonNull(protocolVersion, "protocolVersion can not be null");
        Objects.requireNonNull(domainId, "domainId can not be null");
        Objects.requireNonNull(domainName, "domainName can not be null");
        Objects.requireNonNull(networkListener, "networkListener can not be null");
        NetworkConnection networkConnection = createNetworkConnection(address, protocolVersion, domainId, domainName, networkListener);
        CasualConnection casualConnection = CasualConnection.of(networkConnection);
        CasualXAResource casualXAResource = CasualXAResource.of(casualConnection, resourceManagerId);
        casualConnection.setCasualXAResource(casualXAResource);
        return new CallerImpl(casualConnection, Transactional.of(), ServiceCallerImpl.of(casualConnection));
    }

    private static NetworkConnection createNetworkConnection(InetSocketAddress address, ProtocolVersion protocolVersion, UUID domainId, String domainName, NetworkListener networkListener)
    {
        NettyConnectionInformation connectionInformation = NettyConnectionInformation.createBuilder()
                                                                                          .withDomainId(domainId)
                                                                                          .withDomainName(domainName)
                                                                                          .withAddress(address)
                                                                                          .withProtocolVersion(protocolVersion)
                                                                                          .withCorrelator(CorrelatorImpl.of())
                                                                                          .build();
        return NettyNetworkConnection.of(connectionInformation, networkListener);
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
        if(!serviceExists(serviceName))
        {
            return createTPENOENTReply(serviceName);
        }
        if(flags.isSet(AtmiFlags.TPNOTRAN))
        {
            LOG.finest(() -> "tpcall TPNOTRAN " + serviceName);
            return serviceCaller.tpcall(serviceName, data, flags);
        }
        LOG.finest(() -> "tpcall " + serviceName);
        transactional.startOrJoinTransaction(casualConnection.getCasualXAResource());
        ServiceReturn<CasualBuffer> reply = serviceCaller.tpcall(serviceName, data, flags);
        transactional.commit();
        return reply;
    }

    private ServiceReturn<CasualBuffer> createTPENOENTReply(String serviceName)
    {
        LOG.warning(() -> "TPENOENT for service name: " + serviceName);
        return new ServiceReturn<>(null, ServiceReturnState.TPFAIL, ErrorState.TPENOENT, 0);
    }

    @Override
    public CompletableFuture<ServiceReturn<CasualBuffer>> tpacall(String serviceName, CasualBuffer data, Flag<AtmiFlags> flags)
    {
        if(!serviceExists(serviceName))
        {
            CompletableFuture<ServiceReturn<CasualBuffer>> future = new CompletableFuture<>();
            future.complete(createTPENOENTReply(serviceName));
            return future;
        }
        if(flags.isSet(AtmiFlags.TPNOTRAN))
        {
            LOG.finest(() -> "tpacall TPNOTRAN " + serviceName);
            return serviceCaller.tpacall(serviceName, data, flags);
        }
        LOG.finest(() -> "tpacall " + serviceName);
        transactional.startOrJoinTransaction(casualConnection.getCasualXAResource());
        CompletableFuture<ServiceReturn<CasualBuffer>> reply = serviceCaller.tpacall(serviceName, data, flags);
        transactional.commit();
        return reply;
    }

    @Override
    public boolean serviceExists(String serviceName)
    {
        if(serviceCache.contains(serviceName))
        {
            return true;
        }
        if(serviceCaller.serviceExists(serviceName))
        {
            serviceCache.add(serviceName);
            return true;
        }
        return false;
    }

    @Override
    public List<ServiceDetails> serviceDetails(String serviceName)
    {
        return serviceCaller.serviceDetails(serviceName);
    }

    public static Builder createBuilder()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private InetSocketAddress address;
        private ProtocolVersion protocolVersion;
        private UUID domainId;
        private String domainName;
        private NetworkListener networkListener;
        private int resourceManagerId;

        private Builder()
        {}

        public Builder withAddress(InetSocketAddress address)
        {
            this.address = address;
            return this;
        }

        public Builder withProtocolVersion(ProtocolVersion protocolVersion)
        {
            this.protocolVersion = protocolVersion;
            return this;
        }

        public Builder withDomainId(UUID domainId)
        {
            this.domainId = domainId;
            return this;
        }

        public Builder withDomainName(String domainName)
        {
            this.domainName = domainName;
            return this;
        }

        public Builder withNetworkListener(NetworkListener networkListener)
        {
            this.networkListener = networkListener;
            return this;
        }

        public Builder withResourceManagerId(int resourceManagerId)
        {
            this.resourceManagerId = resourceManagerId;
            return this;
        }

        public Caller build()
        {
            return CallerImpl.of(address, protocolVersion, domainId, domainName,  networkListener, resourceManagerId);
        }
    }
}
