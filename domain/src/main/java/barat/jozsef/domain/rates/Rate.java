package barat.jozsef.domain.rates;

import java.util.Currency;
import java.util.Map;

/**
 * This entity represents a a currency and it's conversation rates for other currencies.
 */
public class Rate {
    public static final Rate EMPTY = new Rate();

    private Currency base;
    private long timestamp;
    private Map<Currency, Double> rates;

    private Rate() {
    }

    public Rate(Currency base, Map<Currency, Double> rates, long timestamp) {
        this.base = base;
        this.rates = rates;
        this.timestamp = timestamp;
    }

    public Currency getBase() {
        return base;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Map<Currency, Double> getRates() {
        return rates;
    }

    public boolean isEmpty() {
        return base == null;
    }
}
