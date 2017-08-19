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
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.subjects.BehaviorSubject;

import static barat.jozsef.domain.rates.RatesConstants.supportedCurrencies;

/**
 * This should be activity scoped not application scoped, but because this app has only one activity
 * these two scopes are actually the same.
 */
@Singleton
class MainPresenter {

    private final BehaviorSubject<CollectedRates> dataState = BehaviorSubject.createDefault(CollectedRates.EMPTY);

    private final RatesUseCase ratesUseCase;
    private final Scheduler scheduler;

    private CompositeDisposable compositeDisposable;

    @Inject
    MainPresenter(RatesUseCase ratesUseCase, @Named("main") Scheduler scheduler) {
        this.ratesUseCase = ratesUseCase;
        this.scheduler = scheduler;
    }

    void attach(ConnectableObservable<Currency> baseObservable,
                ConnectableObservable<Currency> targetObservable,
                ConnectableObservable<Double> input,
                MainView mainView) {
        compositeDisposable = new CompositeDisposable();

        compositeDisposable.add(Observable.combineLatest(baseObservable, targetObservable, input, dataState, ViewState::new)
                .observeOn(scheduler)
                .subscribe(viewState -> updateView(mainView, viewState))
        );

        compositeDisposable.add(ratesUseCase.getLatestRate()
                .zipWith(dataState, (ratesEntity, collectedRates) -> {
                    collectedRates.currencies.put(ratesEntity.getBase(), ratesEntity);
                    return collectedRates;
                })
                .subscribe(dataState::onNext));


        compositeDisposable.add(baseObservable
                .subscribe(ratesUseCase::startPollingRate));

        baseObservable.connect();
        targetObservable.connect();
        input.connect();
    }

    void deAttach() {
        compositeDisposable.dispose();
    }

    private void updateView(MainView mainView, ViewState viewState) {
        double rate = viewState.latestRates.currencies.get(viewState.base).getRates().getOrDefault(viewState.target, 0.0);
        mainView.updateRate(rate);
        mainView.updateLastUpdated(viewState.latestRates.currencies.get(viewState.base).getTimestamp());
        mainView.updateOutput(viewState.inputNumber * rate);
    }

    private static class CollectedRates {
        static final CollectedRates EMPTY = new CollectedRates();

        static {
            for (Currency currency : supportedCurrencies) {
                Map<Currency, Double> currencyRate = new HashMap<>();
                for (Currency ra : supportedCurrencies) {
                    if (ra != currency) {
                        currencyRate.put(ra, 0.0);
                    }
                }

                EMPTY.currencies.put(currency, new Rate(currency, currencyRate, 0));
            }
        }

        Map<Currency, Rate> currencies = new HashMap<>();
    }

    private static class ViewState {
        private final Currency base;
        private final Currency target;
        private final double inputNumber;
        private final CollectedRates latestRates;

        ViewState(Currency base, Currency target, double inputNumber, CollectedRates latestRates) {
            Log.d("cat", "ViewState() called with: base = [" + base + "], target = [" + target + "], inputNumber = [" + inputNumber + "], latestRates = [" + latestRates + "]");
            this.base = base;
            this.target = target;
            this.inputNumber = inputNumber;
            this.latestRates = latestRates;
        }
    }
}
