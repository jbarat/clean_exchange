package barat.jozsef.data.fixer;

import java.util.Currency;
import java.util.Map;

/**
 * The fixer.io response
 */
class FixerResponse {

    private Currency base;
    private Map<Currency, Double> rates;

    FixerResponse(Currency base, Map<Currency, Double> rates) {
        this.base = base;
        this.rates = rates;
    }

    Currency getBase() {
        return base;
    }

    Map<Currency, Double> getRates() {
        return rates;
    }
}