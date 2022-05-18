package se.laz.casual.standalone;

import se.laz.casual.network.api.NetworkConnection;

import javax.transaction.xa.Xid;
import java.util.Objects;

public class CasualConnection
{
    private final NetworkConnection networkConnection;
    private CasualXAResource casualXAResource;
    private int timeout;

    public CasualConnection(NetworkConnection networkConnection)
    {
        this.networkConnection = networkConnection;
    }

    public CasualConnection setCasualXAResource(CasualXAResource casualXAResource)
    {
        Objects.requireNonNull(casualXAResource, "casualXAResource can not be null");
        this.casualXAResource = casualXAResource;
        return this;
    }

    public CasualXAResource getCasualXAResource()
    {
        return casualXAResource;
    }

    public static CasualConnection of(NetworkConnection networkConnection)
    {
        Objects.requireNonNull(networkConnection, "networkConnection can not be null");
        return new CasualConnection(networkConnection);
    }

    public NetworkConnection getNetworkConnection()
    {
        return networkConnection;
    }

    public void setTransactionTimeout(int timeout)
    {
        this.timeout = timeout;
    }

    public int getTransactionTimeout()
    {
        return timeout;
    }

    public Xid getCurrentXid()
    {
        return casualXAResource.getCurrentXid();
    }
}
