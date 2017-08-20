package barat.jozsef.revolutexchange.presentation.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
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

import static barat.jozsef.domain.DomainConstants.supportedCurrencies;

/**
 * The Exchange screen. The activity is a dumb view it's only jobs are to inflate the views,
 * set up the observables on every user changeable view and to set view data when the presenter asks it.
 */
public class MainActivity extends AppCompatActivity implements MainView {

    @Inject MainPresenter mainPresenter;
    @Named("time") @Inject DateFormat lastUpdateFormat;
    @Named("currency") @Inject DecimalFormat currencyFormat;

    private Spinner baseSpinner;
    private Spinner targetSpinner;
    private TextView rateText;
    private TextView lastUpdatedText;
    private TextView targetInputEditText;
    private EditText baseInputEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_container);

        ((ExchangeApplication) getApplication()).getApplicationComponent().inject(this);

        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();

        attachViewObservables();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mainPresenter.deAttach();
    }

    @Override
    public void updateRate(double rate, Currency base, Currency target) {
        this.rateText.setText(String.format(getString(R.string.main_rate),
                base.getSymbol(), target.getSymbol(), rate));
    }

    @Override
    public void updateLastUpdated(long lastUpdated) {
        this.lastUpdatedText.setText(lastUpdateFormat.format(lastUpdated));
    }

    @Override
    public void updateTargetInput(double output) {
        this.targetInputEditText.setText(currencyFormat.format(output));
    }

    @Override
    public void updateBaseInput(double input) {
        this.baseInputEditText.setText(currencyFormat.format(input));
    }

    private void initViews() {
        baseSpinner = (Spinner) findViewById(R.id.base);
        targetSpinner = (Spinner) findViewById(R.id.target);
        rateText = (TextView) findViewById(R.id.rate);
        lastUpdatedText = (TextView) findViewById(R.id.updated);
        targetInputEditText = (TextView) findViewById(R.id.target_input);
        baseInputEditText = (EditText) findViewById(R.id.base_input);

        //generate the selectable currencies based on the supported currencies constant
        baseSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.currency_spinner_item, supportedCurrencies));
        targetSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.currency_spinner_item, supportedCurrencies));
    }

    private void attachViewObservables() {
        ConnectableObservable<Currency> baseObservable = RxAdapterView.itemSelections(baseSpinner)
                .map(supportedCurrencies::get)
                .publish();

        ConnectableObservable<Currency> targetObservable = RxAdapterView.itemSelections(targetSpinner)
                .map(supportedCurrencies::get)
                .publish();

        ConnectableObservable<Double> baseInputObservable = RxTextView.textChanges(baseInputEditText)
                .map(DoubleUtil::parseDouble)
                .publish();

        ConnectableObservable<Double> targetInputObservable = RxTextView.textChanges(targetInputEditText)
                .map(DoubleUtil::parseDouble)
                .publish();

        ConnectableObservable<Boolean> baseInputFocusObservable = RxView.focusChanges(baseInputEditText)
                .publish();

        mainPresenter.attach(baseObservable, targetObservable, baseInputObservable, targetInputObservable,
                baseInputFocusObservable, this);
    }
}
