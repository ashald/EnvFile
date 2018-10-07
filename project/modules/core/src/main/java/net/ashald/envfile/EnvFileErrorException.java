package net.ashald.envfile;


public class EnvFileErrorException extends Exception {
    public EnvFileErrorException(String message) {
        super(message);
    }

    public EnvFileErrorException(Throwable cause) {
        super(cause);
    }

    public EnvFileErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
