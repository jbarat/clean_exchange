package barat.jozsef.data.logger;

import android.util.Log;

import javax.inject.Inject;

import barat.jozsef.domain.logger.Logger;

/**
 * Logger which logs everything to the debug channel.
 */
public class DebugLogger implements Logger {

    private static final String DEBUG_LOG = "DebugLogger";

    @Inject
    DebugLogger() {
    }

    @Override
    public void logThrowable(Throwable e) {
        Log.d(DEBUG_LOG, e.getMessage(), e);
    }

    @Override
    public void logInfo(String message) {
        Log.d(DEBUG_LOG, message);
    }
}
