package barat.jozsef.data.fixer;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.IOException;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;

import barat.jozsef.data.CurrentTimeProvider;
import barat.jozsef.domain.logger.Logger;
import barat.jozsef.domain.rates.RatesEntity;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import retrofit2.Response;
import retrofit2.adapter.rxjava2.Result;

import static barat.jozsef.data.fixer.FixerService.LOG_HTTP_CODE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("ThrowableInstanceNeverThrown")
public class FixerServiceTest {

    private static final IOException NETWORK_FAILURE_EXCEPTION = new IOException();
    private static final int HTTP_RESPONSE_NOT_FOUND = 404;

    private static final long TIME = 2002L;

    private static final Currency GPB = Currency.getInstance(Locale.UK);
    private static final Currency EURO = Currency.getInstance(Locale.FRANCE);
    private static final Currency USD = Currency.getInstance(Locale.US);

    private static final double EURO_RATE = 1.1;
    private static final double USD_RATE = 0.5;

    private static final FixerResponse successFixerResponse = new FixerResponse.Builder()
            .base(GPB)
            .rates(new HashMap<Currency, Double>() {{
                put(EURO, EURO_RATE);
                put(USD, USD_RATE);
            }})
            .build();

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock private FixerWebService fixerWebService;
    @Mock private CurrentTimeProvider currentTimeProvider;
    @Mock private Logger logger;

    @Test
    public void shouldBuildQuery_whenCurrencyProvided() {
        FixerService fixerService = givenAFixerService();

        fixerService.getLatestRates(GPB).test();

        verify(fixerWebService).getLatest(GPB.getCurrencyCode());
    }

    @Test
    public void shouldTransformDataLayerResultToDomainLayer_whenCorrectNetworkResultReceived() {
        FixerService fixerService = givenAFixerService();

        TestObserver<RatesEntity> testObserver = fixerService.getLatestRates(GPB).test();

        verifySuccessfulResult(testObserver);
    }

    @Test
    public void shouldGetEmptyResult_whenIoException() {
        FixerService fixerService = givenAFixerServiceWithIoException();

        TestObserver<RatesEntity> testObserver = fixerService.getLatestRates(GPB).test();

        verifyEmptyResult(testObserver);
    }

    @Test
    public void shouldGetEmptyResult_whenResponseHttpCodeIs404() {
        FixerService fixerService = givenAFixerServiceWith404Response();

        TestObserver<RatesEntity> testObserver = fixerService.getLatestRates(GPB).test();

        verifyEmptyResult(testObserver);
    }

    @Test
    public void shouldLogException_whenIoException() {
        FixerService fixerService = givenAFixerServiceWithIoException();

        fixerService.getLatestRates(GPB).test();

        verify(logger).logThrowable(NETWORK_FAILURE_EXCEPTION);
    }

    @Test
    public void shouldLogInfo_whenNon200ResponseReceived() {
        FixerService fixerService = givenAFixerServiceWith404Response();

        fixerService.getLatestRates(GPB).test();

        verify(logger).logInfo(LOG_HTTP_CODE + HTTP_RESPONSE_NOT_FOUND);
    }

    private void verifyEmptyResult(TestObserver<RatesEntity> testObserver) {
        assertThat(testObserver.errorCount()).isEqualTo(0);
        assertThat(testObserver.valueCount()).isEqualTo(1);
        assertThat(testObserver.values().get(0).isEmpty()).isTrue();
    }

    private void verifySuccessfulResult(TestObserver<RatesEntity> testObserver) {
        assertThat(testObserver.errorCount()).isEqualTo(0);
        assertThat(testObserver.valueCount()).isEqualTo(1);

        RatesEntity ratesEntity = testObserver.values().get(0);
        assertThat(ratesEntity.getBase()).isEqualTo(successFixerResponse.getBase());
        assertThat(ratesEntity.getRates().size()).isEqualTo(2);
        assertThat(ratesEntity.getLastUpdated()).isEqualTo(TIME);
        assertThat(ratesEntity.getRates().get(EURO)).isEqualTo(EURO_RATE);
        assertThat(ratesEntity.getRates().get(USD)).isEqualTo(USD_RATE);
    }

    private FixerService givenAFixerServiceWith404Response() {
        when(fixerWebService.getLatest(GPB.getCurrencyCode())).thenReturn(Single.just(
                Result.response(Response.error(HTTP_RESPONSE_NOT_FOUND, new NoContentResponseBody()))));
        return new FixerService(fixerWebService, currentTimeProvider, logger);
    }

    private FixerService givenAFixerServiceWithIoException() {
        when(fixerWebService.getLatest(GPB.getCurrencyCode())).thenReturn(Single.just(
                Result.error(NETWORK_FAILURE_EXCEPTION)));
        return new FixerService(fixerWebService, currentTimeProvider, logger);
    }

    private FixerService givenAFixerService() {
        when(currentTimeProvider.getCurrentTime()).thenReturn(TIME);
        when(fixerWebService.getLatest(GPB.getCurrencyCode())).thenReturn(Single.just(
                Result.response(Response.success(successFixerResponse))));
        return new FixerService(fixerWebService, currentTimeProvider, logger);
    }
}