package se.laz.casual.standalone.app;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import se.laz.casual.standalone.app.resource.Casual;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

public class Application
{
    private static final Logger LOG = Logger.getLogger(Application.class.getName());
    private static final URI BASE_URI = URI.create("http://localhost:7575/");
    private static final String CASUAL_PATH = "casual";

    private static void startServer()
    {
        ResourceConfig resourceConfig = new ResourceConfig(Casual.class);
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, resourceConfig, false);
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));
        try
        {
            server.start();
            LOG.info(() ->"Application started");
            LOG.info(() ->"Resource at: " + BASE_URI + CASUAL_PATH);
            LOG.info(() -> "Stop the application using CTRL+C");
            Thread.currentThread().join();
        }
        catch (IOException | InterruptedException e)
        {
            Thread.currentThread().interrupt();
            throw new ApplicationFailedException(e);
        }
    }

    public static void main(String[] args)
    {
        startServer();
    }

}
