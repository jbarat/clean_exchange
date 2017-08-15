package barat.jozsef.domain;

import java.util.Currency;

import io.reactivex.Single;

public interface RatesDataSource {
    Single<Boolean> getLatestRates(Currency currency);
}
