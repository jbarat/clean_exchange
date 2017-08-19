package barat.jozsef.domain.rates;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Currency;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import barat.jozsef.domain.logger.Logger;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.TestScheduler;

import static barat.jozsef.domain.rates.RatesConstants.BRITISH_POUND;
import static barat.jozsef.domain.rates.RatesConstants.EURO;
import static barat.jozsef.domain.rates.RatesConstants.UNITED_STATES_DOLLAR;
import static barat.jozsef.domain.rates.RatesUseCase.POOLING_LOG_MESSAGE;
import static barat.jozsef.domain.rates.RatesUseCase.RatesDataSource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RatesUseCaseTest {
    private static final Double EURO_RATE = 1.2;
    private static final Double USD_RATE = 2.5;
    private final TestScheduler heartbeat = new TestScheduler();

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock private RatesDataSource ratesDataSource;
    @Mock private Logger logger;

    @Test
    public void shouldNotPollDataSource_whenNoCurrencySelected() {
        RatesUseCase ratesUseCase = givenARatesUseCase();

        ratesUseCase.getLatestRate().test();
        heartbeat.advanceTimeBy(1, TimeUnit.MINUTES);

        verify(ratesDataSource, never()).getLatestRates(BRITISH_POUND);
    }

    @Test
    public void shouldPollDataSource_whenCurrencySelected() {
        RatesUseCase ratesUseCase = givenARatesUseCase();

        ratesUseCase.getLatestRate().test();
        ratesUseCase.startPollingRate(BRITISH_POUND);
        heartbeat.advanceTimeBy(1, TimeUnit.SECONDS);

        verify(ratesDataSource).getLatestRates(BRITISH_POUND);
    }

    @Test
    public void shouldLogPollingEvents_whenCurrencySelected() {
        RatesUseCase ratesUseCase = givenARatesUseCase();

        ratesUseCase.getLatestRate().test();
        ratesUseCase.startPollingRate(BRITISH_POUND);
        heartbeat.advanceTimeBy(1, TimeUnit.SECONDS);

        verify(logger).logMessage(POOLING_LOG_MESSAGE + BRITISH_POUND);
    }

    @Test
    public void shouldPollDataSource_whenCurrencySelectedAnd30SecondPasses() {
        RatesUseCase ratesUseCase = givenARatesUseCase();

        TestObserver testObserver = ratesUseCase.getLatestRate().test();
        ratesUseCase.startPollingRate(BRITISH_POUND);
        heartbeat.advanceTimeBy(30, TimeUnit.SECONDS);

        assertThat(testObserver.valueCount()).isEqualTo(2);
    }

    @Test
    public void shouldPollDataSourcePeriodically_whenTimePasses() {
        RatesUseCase ratesUseCase = givenARatesUseCase();

        TestObserver testObserver = ratesUseCase.getLatestRate().test();
        ratesUseCase.startPollingRate(BRITISH_POUND);
        heartbeat.advanceTimeBy(30, TimeUnit.MINUTES);

        assertThat(testObserver.valueCount()).isEqualTo(61);
    }

    @Test
    public void shouldPollDataSource_whenCurrencyChanges() {
        RatesUseCase ratesUseCase = givenARatesUseCase();

        TestObserver testObserver = ratesUseCase.getLatestRate().test();
        ratesUseCase.startPollingRate(BRITISH_POUND);
        heartbeat.advanceTimeBy(1, TimeUnit.SECONDS);
        ratesUseCase.startPollingRate(BRITISH_POUND);

        assertThat(testObserver.valueCount()).isEqualTo(2);
    }

    private RatesUseCase givenARatesUseCase() {
        when(ratesDataSource.getLatestRates(BRITISH_POUND)).thenReturn(Observable.just(
                new Rate(BRITISH_POUND, new HashMap<Currency, Double>() {{
                    put(EURO, EURO_RATE);
                    put(UNITED_STATES_DOLLAR, USD_RATE);
                }}, 1)
        ));

        return new RatesUseCase(ratesDataSource, Schedulers.trampoline(), heartbeat, logger);
    }
}