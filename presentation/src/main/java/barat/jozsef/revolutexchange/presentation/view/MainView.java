package barat.jozsef.revolutexchange.presentation.view;

interface MainView {
    void updateRate(double rate);

    void updateLastUpdated(long lastUpdated);

    void updateOutput(double output);
}
