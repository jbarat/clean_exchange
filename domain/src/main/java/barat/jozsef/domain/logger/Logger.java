package barat.jozsef.domain.logger;

public interface Logger {

    void logThrowable(Throwable e);

    void logInfo(String message);
}
