package net.ashald.envfile.exceptions;


public class EnvFileException extends Exception {
    public EnvFileException(String message) {
        super(message);
    }

    public EnvFileException(Throwable cause) {
        super(cause);
    }

    public EnvFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public static EnvFileException format(String message, Object... args) {
        return new EnvFileException(String.format(message, args));
    }
}
