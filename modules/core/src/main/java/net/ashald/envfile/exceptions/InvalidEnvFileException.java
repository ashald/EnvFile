package net.ashald.envfile.exceptions;


public class InvalidEnvFileException extends EnvFileException {
    public InvalidEnvFileException(String message) {
        super(message);
    }

    public InvalidEnvFileException(Throwable cause) {
        super(cause);
    }

    public InvalidEnvFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public static InvalidEnvFileException format(String message, Object... args) {
        return new InvalidEnvFileException(String.format(message, args));
    }
}
