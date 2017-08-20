package barat.jozsef.revolutexchange.presentation.util;

/**
 * Util for Double operations.
 */
public class DoubleUtil {

    public static double parseDouble(CharSequence charSequence) {
        try {
            return charSequence != null ? Double.parseDouble(charSequence.toString()) : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
