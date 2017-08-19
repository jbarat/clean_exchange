package barat.jozsef.data.logger;

import barat.jozsef.domain.logger.Logger;
import dagger.Module;
import dagger.Provides;

@Module
public class LoggerModule {

    @Provides
    Logger logger(DebugLogger debugLogger) {
        return debugLogger;
    }
}
