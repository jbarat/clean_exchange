package barat.jozsef.domain.rates;

import java.util.Currency;

import io.reactivex.Single;

public interface RatesDataSource {
    Single<RatesEntity> getLatestRates(Currency currency);
}
