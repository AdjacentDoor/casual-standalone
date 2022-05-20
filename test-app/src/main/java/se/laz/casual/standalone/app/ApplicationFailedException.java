package se.laz.casual.standalone.app;

public class ApplicationFailedException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    public ApplicationFailedException()
    {
        super();
    }

    public ApplicationFailedException(String message)
    {
        super(message);
    }

    public ApplicationFailedException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ApplicationFailedException(Throwable cause)
    {
        super(cause);
    }

    protected ApplicationFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
