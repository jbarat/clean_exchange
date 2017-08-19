package barat.jozsef.data.fixer;

import java.util.Currency;

import javax.inject.Inject;

import barat.jozsef.data.util.CurrentTimeProvider;
import barat.jozsef.domain.logger.Logger;
import barat.jozsef.domain.rates.Rate;
import io.reactivex.Observable;
import retrofit2.adapter.rxjava2.Result;

import static barat.jozsef.domain.rates.Rate.EMPTY;
import static barat.jozsef.domain.rates.RatesUseCase.RatesDataSource;

/**
 * Simple retrofit web service implementation. If there is a problem with the call it will return an
 * empty response.
 */
public class FixerService implements RatesDataSource {

    static final String LOG_HTTP_CODE = "Http Code:";

    private final FixerWebService fixerWebService;
    private final CurrentTimeProvider currentTimeProvider;
    private final Logger logger;

    @Inject
    FixerService(FixerWebService fixerWebService,
                 CurrentTimeProvider currentTimeProvider,
                 Logger logger) {
        this.fixerWebService = fixerWebService;
        this.currentTimeProvider = currentTimeProvider;
        this.logger = logger;
    }

    @Override
    public Observable<Rate> getLatestRates(Currency currency) {
        return fixerWebService.getLatest(currency.getCurrencyCode())
                .map(this::processResponse);
    }

    /**
     * Transform the web service response into a domain layer object.
     */
    @SuppressWarnings("ConstantConditions")
    private Rate processResponse(Result<FixerResponse> fixerResponseResult) {
        Rate rates = EMPTY;
        if (fixerResponseResult.isError()) {
            logger.logThrowable(fixerResponseResult.error());
        } else if (!fixerResponseResult.response().isSuccessful()) {
            logger.logMessage(LOG_HTTP_CODE + fixerResponseResult.response().code());
        } else if (fixerResponseResult.response().isSuccessful()) {
            FixerResponse fixerResponse = fixerResponseResult.response().body();

            rates = new Rate(fixerResponse.getBase(), fixerResponse.getRates(),
                    currentTimeProvider.getCurrentTime());
        }

        return rates;
    }
}
