package barat.jozsef.data.fixer;

import java.util.Currency;
import java.util.Map;

class FixerResponse {
    private Currency base;
    private Map<Currency, Double> rates;

    public Currency getBase() {
        return base;
    }

    public void setBase(Currency base) {
        this.base = base;
    }

    public Map<Currency, Double> getRates() {
        return rates;
    }

    public void setRates(Map<Currency, Double> rates) {
        this.rates = rates;
    }
}
