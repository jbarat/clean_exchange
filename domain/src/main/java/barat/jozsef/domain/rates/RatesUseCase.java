package barat.jozsef.domain.rates;

import java.util.Currency;

import javax.inject.Inject;
import javax.inject.Named;

import barat.jozsef.domain.DomainConstants;
import barat.jozsef.domain.logger.Logger;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.subjects.PublishSubject;

import static barat.jozsef.domain.DomainConstants.POLLING_PERIOD;
import static barat.jozsef.domain.rates.Rate.EMPTY;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Use case to poll the data from the data source. It only polls the data if somebody is subscribed
 * to the {@link #getLatestRate} and a currency is selected to be polled {@link #startPollingRate(Currency)}.
 */
public class RatesUseCase {
    static final String POOLING_LOG_MESSAGE = "Pooling ";

    private final PublishSubject<Currency> selectedBase = PublishSubject.create();

    private final RatesDataSource ratesDataSource;
    private final Scheduler ioScheduler;
    private final Scheduler heartbeatScheduler;
    private final Logger logger;

    @Inject
    RatesUseCase(RatesDataSource ratesDataSource,
                 @Named("io") Scheduler ioScheduler,
                 @Named("computational") Scheduler heartbeatScheduler,
                 Logger logger) {
        this.ratesDataSource = ratesDataSource;
        this.ioScheduler = ioScheduler;
        this.heartbeatScheduler = heartbeatScheduler;
        this.logger = logger;
    }

    public void startPollingRate(Currency base) {
        selectedBase.onNext(base);
    }

    /**
     * Polls the data source using the latest currency passed to the subject.
     * It polls every {@link DomainConstants#POLLING_PERIOD} or every time when the polled currency
     * changes.
     */
    public Observable<Rate> getLatestRate() {
        return Observable
                .combineLatest(Observable.interval(0, POLLING_PERIOD, SECONDS, heartbeatScheduler),
                        selectedBase, (aLong, currency) -> currency)
                .observeOn(ioScheduler)
                .doOnNext(this::logPolling)
                .flatMap(ratesDataSource::getLatestRates)
                .filter(ratesEntity -> ratesEntity != EMPTY); //it the result is empty there is
                                                    // no reason to notify the subscriber of change
    }

    private void logPolling(Currency currency) {
        logger.logMessage(POOLING_LOG_MESSAGE + currency);
    }

    /**
     * The data source dependency of this use case. The data layer will fulfill this dependency.
     */
    public interface RatesDataSource {
        Observable<Rate> getLatestRates(Currency currency);
    }
}
