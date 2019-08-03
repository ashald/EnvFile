package net.ashald.envfile.exceptions;


public class EnvSingleErrorException extends Exception {
    public EnvSingleErrorException(String message) {
        super(message);
    }

    public EnvSingleErrorException(Throwable cause) {
        super(cause);
    }

    public EnvSingleErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
