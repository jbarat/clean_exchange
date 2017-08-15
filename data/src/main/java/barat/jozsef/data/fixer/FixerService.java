package barat.jozsef.data.fixer;


import java.util.Currency;

import javax.inject.Inject;

import barat.jozsef.domain.RatesDataSource;
import io.reactivex.Single;

public class FixerService implements RatesDataSource {

    private final FixerWebService fixerWebService;

    @Inject
    FixerService(FixerWebService fixerWebService) {
        this.fixerWebService = fixerWebService;
    }

    @Override
    public Single<Boolean> getLatestRates(Currency currency) {
        return fixerWebService.getLatest(currency.getCurrencyCode())
                .map(fixerResponseResult -> true);
    }
}
