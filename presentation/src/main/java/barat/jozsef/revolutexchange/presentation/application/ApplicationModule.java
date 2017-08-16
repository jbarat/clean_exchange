package barat.jozsef.revolutexchange.presentation.application;

import android.content.Context;

import javax.inject.Named;

import barat.jozsef.data.logger.DebugLogger;
import barat.jozsef.domain.logger.Logger;
import barat.jozsef.revolutexchange.BuildConfig;
import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    private final ExchangeApplication application;

    ApplicationModule(ExchangeApplication exchangeApplication) {
        this.application = exchangeApplication;
    }

    @Provides
    Context context() {
        return application;
    }

    @Provides
    Logger logger(DebugLogger debugLogger) {
        return debugLogger;
    }

    @Provides
    @Named("debug")
    Boolean debug() {
        return BuildConfig.DEBUG;
    }
}
