package barat.jozsef.revolutexchange.presentation.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Currency;

import javax.inject.Inject;

import barat.jozsef.data.fixer.FixerService;
import barat.jozsef.revolutexchange.R;
import barat.jozsef.revolutexchange.presentation.application.ExchangeApplication;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Inject FixerService fixerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((ExchangeApplication) getApplication()).getApplicationComponent().inject(this);

        fixerService.getLatestRates(Currency.getInstance("GBP"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }
}
