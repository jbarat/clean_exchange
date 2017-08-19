package barat.jozsef.revolutexchange.presentation.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.jakewharton.rxbinding2.widget.RxAdapterView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Currency;

import javax.inject.Inject;
import javax.inject.Named;

import barat.jozsef.revolutexchange.R;
import barat.jozsef.revolutexchange.presentation.application.ExchangeApplication;
import barat.jozsef.revolutexchange.presentation.util.DoubleUtil;
import io.reactivex.observables.ConnectableObservable;

public class MainActivity extends AppCompatActivity implements MainView {

    @Inject MainPresenter mainPresenter;
    @Named("time") @Inject DateFormat lastUpdateFormat;
    @Named("currency") @Inject DecimalFormat currencyFormat;

    private Spinner baseSpinner;
    private Spinner targetSpinner;
    private TextView rateTextView;
    private TextView lastUpdatedTextView;
    private TextView outputTextView;
    private EditText inputEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((ExchangeApplication) getApplication()).getApplicationComponent().inject(this);

        baseSpinner = (Spinner) findViewById(R.id.base);
        targetSpinner = (Spinner) findViewById(R.id.target);
        rateTextView = (TextView) findViewById(R.id.rate);
        lastUpdatedTextView = (TextView) findViewById(R.id.lastUpdated);
        outputTextView = (TextView) findViewById(R.id.output);
        inputEditText = (EditText) findViewById(R.id.input);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ConnectableObservable<Currency> baseObservable = RxAdapterView.itemSelections(baseSpinner).map(
                integer -> {
                    if (integer == 0) {
                        return Currency.getInstance("GBP");
                    } else if (integer == 1) {
                        return Currency.getInstance("USD");
                    } else {
                        return Currency.getInstance("EUR");
                    }
                }
        ).publish();

        ConnectableObservable<Currency> targetObservable = RxAdapterView.itemSelections(targetSpinner).map(
                integer -> {
                    if (integer == 0) {
                        return Currency.getInstance("GBP");
                    } else if (integer == 1) {
                        return Currency.getInstance("USD");
                    } else {
                        return Currency.getInstance("EUR");
                    }
                }
        ).publish();

        ConnectableObservable<Double> inputObservable = RxTextView.textChanges(inputEditText)
                .map(DoubleUtil::parseDouble)
                .publish();

        mainPresenter.attach(baseObservable, targetObservable, inputObservable, this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mainPresenter.deAttach();
    }

    @Override
    public void updateRate(double rate) {
        this.rateTextView.setText(String.valueOf(rate));
    }

    @Override
    public void updateLastUpdated(long lastUpdated) {
        this.lastUpdatedTextView.setText(lastUpdateFormat.format(lastUpdated));
    }

    @Override
    public void updateOutput(double output) {
        this.outputTextView.setText(currencyFormat.format(output));
    }
}
