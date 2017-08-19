package barat.jozsef.revolutexchange.presentation.view;

import java.util.Currency;

interface MainView {
    void updateRate(double rate, Currency base, Currency target);

    void updateLastUpdated(long lastUpdated);

    void updateTargetInput(double output);

    void updateBaseInput(double input);
}
