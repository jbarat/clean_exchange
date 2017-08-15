package barat.jozsef.data.fixer;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Currency;
import java.util.Locale;

import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.adapter.rxjava2.Result;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FixerServiceTest {
    private static final Currency GPB = Currency.getInstance(Locale.UK);

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock private FixerWebService fixerWebService;

    @Test
    public void shouldBuildQuery_whenCurrencyProvided() {
        FixerService fixerService = givenAFixerService();

        fixerService.getLatestRates(GPB).test();

        verify(fixerWebService).getLatest(GPB.getCurrencyCode());
    }

    private FixerService givenAFixerService() {
        when(fixerWebService.getLatest(GPB.getCurrencyCode())).thenReturn(Single.just(
                Result.response(Response.success(new FixerResponse()))));
        return new FixerService(fixerWebService);
    }
}