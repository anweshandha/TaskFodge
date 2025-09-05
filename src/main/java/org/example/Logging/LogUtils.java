package org.example.Logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LogUtils {

    private LogUtils() {}

    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
}