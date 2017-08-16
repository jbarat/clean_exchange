package barat.jozsef.data.fixer;

import java.util.Currency;
import java.util.Map;

class FixerResponse {

    private Currency base;
    private Map<Currency, Double> rates;

    private FixerResponse(Builder builder) {
        base = builder.base;
        rates = builder.rates;
    }

    Currency getBase() {
        return base;
    }

    Map<Currency, Double> getRates() {
        return rates;
    }

    static final class Builder {
        private Currency base;
        private Map<Currency, Double> rates;

        Builder() {
        }

        Builder base(Currency val) {
            base = val;
            return this;
        }

        Builder rates(Map<Currency, Double> val) {
            rates = val;
            return this;
        }

        FixerResponse build() {
            return new FixerResponse(this);
        }
    }
}
