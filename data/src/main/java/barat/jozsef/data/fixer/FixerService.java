package barat.jozsef.data.fixer;

import java.util.Currency;

import javax.inject.Inject;

import barat.jozsef.data.util.CurrentTimeProvider;
import barat.jozsef.domain.logger.Logger;
import barat.jozsef.domain.rates.RatesDataSource;
import barat.jozsef.domain.rates.RatesEntity;
import io.reactivex.Single;
import retrofit2.adapter.rxjava2.Result;

import static barat.jozsef.domain.rates.RatesEntity.EMPTY;

@SuppressWarnings("ConstantConditions")
public class FixerService implements RatesDataSource {

    static final String LOG_HTTP_CODE = "Http Code:";

    private final FixerWebService fixerWebService;
    private final CurrentTimeProvider currentTimeProvider;
    private final Logger logger;

    @Inject
    FixerService(FixerWebService fixerWebService, CurrentTimeProvider currentTimeProvider,
                 Logger logger) {
        this.fixerWebService = fixerWebService;
        this.currentTimeProvider = currentTimeProvider;
        this.logger = logger;
    }

    @Override
    public Single<RatesEntity> getLatestRates(Currency currency) {
        return fixerWebService.getLatest(currency.getCurrencyCode())
                .map(this::processResponse);
    }

    private RatesEntity processResponse(Result<FixerResponse> fixerResponseResult) {
        RatesEntity rates = EMPTY;
        if (fixerResponseResult.isError()) {
            logger.logThrowable(fixerResponseResult.error());
        } else if (!fixerResponseResult.response().isSuccessful()) {
            logger.logInfo(LOG_HTTP_CODE + fixerResponseResult.response().code());
        } else if (fixerResponseResult.response().isSuccessful()) {
            FixerResponse fixerResponse = fixerResponseResult.response().body();

            rates = new RatesEntity.Builder()
                    .base(fixerResponse.getBase())
                    .rates(fixerResponse.getRates())
                    .lastUpdated(currentTimeProvider.getCurrentTime())
                    .build();
        }

        return rates;
    }
}
