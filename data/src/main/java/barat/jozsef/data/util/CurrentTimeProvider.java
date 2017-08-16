package barat.jozsef.data.util;

import java.util.Date;

import javax.inject.Inject;

/**
 * This class exist so I can test when we set the date.
 */
public class CurrentTimeProvider {

    @Inject
    CurrentTimeProvider() {
    }

    public long getCurrentTime() {
        return new Date().getTime();
    }
}
