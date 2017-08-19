package barat.jozsef.revolutexchange.presentation.view;


import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Currency;
import java.util.HashMap;

import barat.jozsef.domain.rates.Rate;
import barat.jozsef.domain.rates.RatesUseCase;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import static barat.jozsef.domain.rates.RatesConstants.BRITISH_POUND;
import static barat.jozsef.domain.rates.RatesConstants.EURO;
import static barat.jozsef.domain.rates.RatesConstants.UNITED_STATES_DOLLAR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainPresenterTest {

    private static final double BASE_INPUT = 12.0;
    private static final double TARGET_INPUT = 0.0;
    private static final Double EURO_RATE = 2.0;
    private static final Double USD_RATE = 2.5;
    private static final int TIMESTAMP = 1;

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock private RatesUseCase ratesUseCase;
    @Mock private MainView mainView;

    private PublishSubject<Currency> base = PublishSubject.create();
    private PublishSubject<Currency> target = PublishSubject.create();
    private PublishSubject<Double> baseInput = PublishSubject.create();
    private PublishSubject<Double> targetInput = PublishSubject.create();
    private PublishSubject<Boolean> baseInputHasFocus = PublishSubject.create();

    @Test
    public void shouldStartPolling_whenBaseCurrencySelected() {
        givenAMainPresenterWithAttachedView();

        base.onNext(BRITISH_POUND);

        verify(ratesUseCase).startPollingRate(BRITISH_POUND);
    }

    @Test
    public void shouldRatesUpdate_whenCombinedStateChanges() {
        givenAMainPresenterWithAttachedView();

        emitViewEvents();

        verify(mainView).updateRate(EURO_RATE, BRITISH_POUND, EURO);
    }

    @Test
    public void shouldLastUpdatedUpdate_whenCombinedStateChanges() {
        givenAMainPresenterWithAttachedView();

        emitViewEvents();

        verify(mainView).updateLastUpdated(TIMESTAMP);
    }

    @Test
    public void shouldTargetInputUpdate_whenCombinedStateChangesWithFocusOnBaseInput() {
        givenAMainPresenterWithAttachedView();

        emitViewEvents();

        verify(mainView).updateTargetInput(EURO_RATE * BASE_INPUT);
    }

    @Test
    public void shouldBaseInputNotUpdate_whenCombinedStateChangesWithFocusOnBaseInput() {
        givenAMainPresenterWithAttachedView();

        emitViewEvents();

        verify(mainView, never()).updateBaseInput(anyDouble());
    }

    @Test
    public void shouldBaseInputUpdate_whenCombinedStateChangesWithFocusOnTargetInput() {
        givenAMainPresenterWithAttachedView();

        emitViewEventsWithTargetFocus();

        verify(mainView).updateBaseInput(EURO_RATE * TARGET_INPUT);
    }

    @Test
    public void shouldTargetInputNotUpdate_whenCombinedStateChangesWithFocusOnTargetInput() {
        givenAMainPresenterWithAttachedView();

        emitViewEventsWithTargetFocus();

        verify(mainView, never()).updateTargetInput(anyDouble());
    }

    @Test
    public void shouldNotUpdateViews_whenCombinedStateIsNotChanging() {
        givenAMainPresenterWithAttachedView();

        emitViewEvents();
        emitViewEvents();
        emitViewEvents();

        verify(mainView).updateTargetInput(anyDouble());
    }

    @Test
    public void shouldNotUpdateInputs_whenCurrencyIsTheSame() {
        givenAMainPresenterWithAttachedView();

        emitViewEventsWithSameCurrency();

        verify(mainView, never()).updateTargetInput(anyDouble());
        verify(mainView, never()).updateBaseInput(anyDouble());
    }

    @Test
    public void shouldUnsubscribeViewObservers_whenDeAttached() {
        MainPresenter mainPresenter = givenAMainPresenterWithAttachedView();

        mainPresenter.deAttach();
        base.onNext(BRITISH_POUND);

        verify(ratesUseCase, never()).startPollingRate(BRITISH_POUND);
    }

    private void emitViewEventsWithSameCurrency() {
        base.onNext(BRITISH_POUND);
        target.onNext(BRITISH_POUND);
        baseInput.onNext(BASE_INPUT);
        targetInput.onNext(TARGET_INPUT);
        baseInputHasFocus.onNext(true);
    }

    private void emitViewEvents() {
        base.onNext(BRITISH_POUND);
        target.onNext(EURO);
        baseInput.onNext(BASE_INPUT);
        targetInput.onNext(TARGET_INPUT);
        baseInputHasFocus.onNext(true);
    }

    private void emitViewEventsWithTargetFocus() {
        base.onNext(BRITISH_POUND);
        target.onNext(EURO);
        baseInput.onNext(BASE_INPUT);
        targetInput.onNext(TARGET_INPUT);
        baseInputHasFocus.onNext(false);
    }

    private MainPresenter givenAMainPresenterWithAttachedView() {
        when(ratesUseCase.getLatestRate()).thenReturn(Observable.just(
                new Rate(BRITISH_POUND, new HashMap<Currency, Double>() {{
                    put(EURO, EURO_RATE);
                    put(UNITED_STATES_DOLLAR, USD_RATE);
                }}, TIMESTAMP)));

        MainPresenter mainPresenter = new MainPresenter(ratesUseCase, Schedulers.trampoline());

        mainPresenter.attach(base.publish(), target.publish(), baseInput.publish(),
                targetInput.publish(), baseInputHasFocus.publish(), mainView);

        return mainPresenter;
    }
}