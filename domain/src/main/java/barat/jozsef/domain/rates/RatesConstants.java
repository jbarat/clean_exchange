package barat.jozsef.domain.rates;

import java.util.Arrays;
import java.util.Currency;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class RatesConstants {
    public static final int POLLING_PERIOD = 30;

    public static final String BRITISH_POUND_CODE = "GBP";
    public static final String UNITED_STATES_DOLLAR_CODE = "USD";
    public static final String EURO_CODE = "EUR";

    public static final Currency BRITISH_POUND = Currency.getInstance(BRITISH_POUND_CODE);
    public static final Currency UNITED_STATES_DOLLAR = Currency.getInstance(UNITED_STATES_DOLLAR_CODE);
    public static final Currency EURO = Currency.getInstance(EURO_CODE);

    public final static List<Currency> supportedCurrencies = Arrays.asList(
            BRITISH_POUND,
            UNITED_STATES_DOLLAR,
            EURO
    );
}
