package barat.jozsef.domain;

import java.util.Arrays;
import java.util.Currency;
import java.util.List;

/**
 * Application wide constants.
 */
public class DomainConstants {
    public static final int POLLING_PERIOD = 30;

    private static final String BRITISH_POUND_CODE = "GBP";
    private static final String UNITED_STATES_DOLLAR_CODE = "USD";
    private static final String EURO_CODE = "EUR";

    public static final Currency BRITISH_POUND = Currency.getInstance(BRITISH_POUND_CODE);
    public static final Currency UNITED_STATES_DOLLAR = Currency.getInstance(UNITED_STATES_DOLLAR_CODE);
    public static final Currency EURO = Currency.getInstance(EURO_CODE);

    /**
     * The app is built on this constant. It defines the supported currencies.
     */
    public final static List<Currency> supportedCurrencies = Arrays.asList(
            BRITISH_POUND,
            UNITED_STATES_DOLLAR,
            EURO
    );
}
