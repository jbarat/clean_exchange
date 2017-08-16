package barat.jozsef.domain.rates;

import java.util.Currency;
import java.util.Map;

public class RatesEntity {
    public static final RatesEntity EMPTY = new RatesEntity.Builder().build();

    private Currency base;
    private long lastUpdated;
    private Map<Currency, Double> rates;

    private RatesEntity(Builder builder) {
        base = builder.base;
        lastUpdated = builder.lastUpdated;
        rates = builder.rates;
    }

    public Currency getBase() {
        return base;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public Map<Currency, Double> getRates() {
        return rates;
    }

    public boolean isEmpty() {
        return base == null;
    }

    public static final class Builder {
        private Currency base;
        private long lastUpdated;
        private Map<Currency, Double> rates;

        public Builder() {
        }

        public Builder base(Currency val) {
            base = val;
            return this;
        }

        public Builder lastUpdated(long val) {
            lastUpdated = val;
            return this;
        }

        public Builder rates(Map<Currency, Double> val) {
            rates = val;
            return this;
        }

        public RatesEntity build() {
            return new RatesEntity(this);
        }
    }
}
