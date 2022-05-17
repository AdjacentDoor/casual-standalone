package se.laz.casual.standalone.app;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import se.laz.casual.standalone.app.resource.Casual;

import java.io.IOException;
import java.net.URI;

public class Application
{
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
            System.out.println("Application started");
            System.out.println("Stop the application using CTRL+C");
            Thread.currentThread().join();
        }
        catch (IOException | InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args)
    {
        startServer();
    }
}
