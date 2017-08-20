package barat.jozsef.revolutexchange.presentation.application;

import android.app.Application;

import barat.jozsef.revolutexchange.presentation.di.ApplicationComponent;
import barat.jozsef.revolutexchange.presentation.di.DaggerApplicationComponent;

/**
 * We need to overwrite the Application to start up the dependency injection before anything else.
 */
public class ExchangeApplication extends Application {

    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        initDagger();
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    private void initDagger() {
       applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }
}
