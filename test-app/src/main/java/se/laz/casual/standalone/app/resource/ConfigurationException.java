package se.laz.casual.standalone.app.resource;

public class ConfigurationException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    public ConfigurationException()
    {
        super();
    }

    public ConfigurationException(String message)
    {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ConfigurationException(Throwable cause)
    {
        super(cause);
    }

    protected ConfigurationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
