/*
 * Copyright (c) 2022, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */
package se.laz.casual.standalone.app;

public class ServiceCallFailedException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    public ServiceCallFailedException()
    {
        super();
    }

    public ServiceCallFailedException(String message)
    {
        super(message);
    }

    public ServiceCallFailedException(Throwable t)
    {
        super(t);
    }

    public ServiceCallFailedException(String message, Throwable t)
    {
        super(message, t);
    }
}
