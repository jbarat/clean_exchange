package barat.jozsef.revolutexchange.presentation.view;

import android.util.Log;

import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import barat.jozsef.domain.rates.Rate;
import barat.jozsef.domain.rates.RatesUseCase;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.subjects.BehaviorSubject;

import static barat.jozsef.domain.rates.RatesConstants.supportedCurrencies;
import static io.reactivex.Observable.combineLatest;

/**
 * This should be activity scoped not application scoped, but because this app has only one activity
 * these two scopes are actually the same.
 */
@Singleton
class MainPresenter {

    private final BehaviorSubject<CollectedRates> latestCombinedRates = BehaviorSubject.createDefault(CollectedRates.EMPTY);

    private final RatesUseCase ratesUseCase;
    private final Scheduler scheduler;

    private CompositeDisposable compositeDisposable;

    @Inject
    MainPresenter(RatesUseCase ratesUseCase, @Named("main") Scheduler scheduler) {
        this.ratesUseCase = ratesUseCase;
        this.scheduler = scheduler;
    }

    void attach(ConnectableObservable<Currency> base,
                ConnectableObservable<Currency> target,
                ConnectableObservable<Double> baseInput,
                ConnectableObservable<Double> targetInput,
                ConnectableObservable<Boolean> inputFocus,
                MainView mainView) {
        compositeDisposable = new CompositeDisposable();

        compositeDisposable.add(combineLatest(base, target, baseInput, targetInput, inputFocus,
                    latestCombinedRates, CombinedState::new)
                .distinct()
                .observeOn(scheduler)
                .subscribe(combinedState -> updateView(mainView, combinedState))
        );

        compositeDisposable.add(ratesUseCase.getLatestRate()
                .zipWith(latestCombinedRates, (ratesEntity, collectedRates) -> {
                    collectedRates.currencies.put(ratesEntity.getBase(), ratesEntity);
                    return collectedRates;
                })
                .subscribe(latestCombinedRates::onNext));


        compositeDisposable.add(base
                .subscribe(ratesUseCase::startPollingRate));

        base.connect();
        target.connect();
        baseInput.connect();
        targetInput.connect();
        inputFocus.connect();
    }

    void deAttach() {
        compositeDisposable.dispose();
    }

    private void updateView(MainView view, CombinedState combinedState) {
        CollectedRates latestRates = combinedState.latestRates;
        double rate = latestRates.currencies.get(combinedState.base).getRates().getOrDefault(combinedState.target, 0.0);

        updateInfoViews(view, combinedState, latestRates, rate);
        updateInputFields(view, combinedState, rate);
    }

    private void updateInputFields(MainView view, CombinedState combinedState, double rate) {
        if (combinedState.target == combinedState.base) {
            return;
        }

        if (combinedState.inputHasFocus) {
            view.updateTargetInput(combinedState.input * rate);
        } else {
            view.updateBaseInput(combinedState.output / rate);
        }
    }

    private void updateInfoViews(MainView view, CombinedState combinedState, CollectedRates latestRates, double rate) {
        view.updateRate(rate, combinedState.base, combinedState.target);
        view.updateLastUpdated(latestRates.currencies.get(combinedState.base).getTimestamp());
    }

    private static class CollectedRates {
        /*This object has a rate for every currency combination. It's 0. */
        static final CollectedRates EMPTY = new CollectedRates();

        static {
            for (Currency currency : supportedCurrencies) {
                EMPTY.currencies.put(currency, new Rate(currency, buildEmptyCurrencyMap(currency), 0));
            }
        }

        private static Map<Currency, Double> buildEmptyCurrencyMap(Currency currency) {
            Map<Currency, Double> currencyRate = new HashMap<>();
            for (Currency otherCurrency : supportedCurrencies) {
                if (otherCurrency != currency) {
                    currencyRate.put(otherCurrency, 0.0);
                }
            }
            return currencyRate;
        }

        Map<Currency, Rate> currencies = new HashMap<>();
    }

    private static class CombinedState {
        private final Currency base;
        private final Currency target;
        private final double input;
        private final double output;
        private final boolean inputHasFocus;
        private final CollectedRates latestRates;

        CombinedState(Currency base, Currency target, double input, double output,
                      boolean inputHasFocus, CollectedRates latestRates) {
            this.base = base;
            this.target = target;
            this.input = input;
            this.output = output;
            this.inputHasFocus = inputHasFocus;
            this.latestRates = latestRates;
        }

        /**
         * Generated
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CombinedState combinedState = (CombinedState) o;

            if (Double.compare(combinedState.input, input) != 0) return false;
            if (Double.compare(combinedState.output, output) != 0) return false;
            if (inputHasFocus != combinedState.inputHasFocus) return false;
            if (!base.equals(combinedState.base)) return false;
            if (!target.equals(combinedState.target)) return false;
            return latestRates.equals(combinedState.latestRates);

        }

        /**
         * Generated
         */
        @Override
        public int hashCode() {
            int result;
            long temp;
            result = base.hashCode();
            result = 31 * result + target.hashCode();
            temp = Double.doubleToLongBits(input);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(output);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            result = 31 * result + (inputHasFocus ? 1 : 0);
            result = 31 * result + latestRates.hashCode();
            return result;
        }
    }
}
