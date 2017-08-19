package barat.jozsef.revolutexchange.presentation.di;

import javax.inject.Singleton;

import barat.jozsef.data.NetworkModule;
import barat.jozsef.data.logger.LoggerModule;
import barat.jozsef.revolutexchange.presentation.application.ApplicationModule;
import barat.jozsef.revolutexchange.presentation.util.UtilModule;
import barat.jozsef.revolutexchange.presentation.view.MainActivity;
import dagger.Component;

@Singleton
@Component(modules = {
        ApplicationModule.class,
        NetworkModule.class,
        UtilModule.class,
        LoggerModule.class
})
public interface ApplicationComponent {

    void inject(MainActivity mainActivity);

}
