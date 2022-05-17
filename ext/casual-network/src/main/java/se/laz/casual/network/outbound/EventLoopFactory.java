/*
 * Copyright (c) 2021, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */
package se.laz.casual.network.outbound;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import se.laz.casual.config.ConfigurationService;
import se.laz.casual.config.Outbound;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public final class EventLoopFactory
{
    private static final Logger LOG = Logger.getLogger(NettyNetworkConnection.class.getName());
    private static final EventLoopGroup INSTANCE = createEventLoopGroup();
    private EventLoopFactory()
    {}
    public static synchronized EventLoopGroup getInstance()
    {
        return INSTANCE;
    }

    private static EventLoopGroup createEventLoopGroup()
    {
        Outbound outbound = ConfigurationService.getInstance().getConfiguration().getOutbound();
        return new NioEventLoopGroup(outbound.getNumberOfThreads());
    }

    public static ExecutorService getExecutorService()
    {
        return Executors.newFixedThreadPool(Math.max(1, Runtime.getRuntime().availableProcessors() * 2));
    }
}
