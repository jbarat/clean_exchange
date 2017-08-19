package barat.jozsef.domain.logger;

/**
 * Logger for the domain layer.
 */
public interface Logger {

    void logThrowable(Throwable e);

    void logMessage(String message);
}
