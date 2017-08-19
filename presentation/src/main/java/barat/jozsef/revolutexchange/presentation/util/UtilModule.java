package barat.jozsef.revolutexchange.presentation.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Module
public class UtilModule {

    @Named("time")
    @Provides
    DateFormat timeFormat(Locale locale) {
        return new SimpleDateFormat("HH:mm:ss", locale);
    }

    @Named("currency")
    @Provides
    DecimalFormat currencyFormat() {
        return new DecimalFormat("0.00");
    }

    @Named("io")
    @Provides
    Scheduler io() {
        return Schedulers.io();
    }

    @Named("main")
    @Provides
    Scheduler main() {
        return AndroidSchedulers.mainThread();
    }

    @Named("computational")
    @Provides
    Scheduler computational() {
        return Schedulers.computation();
    }
}
