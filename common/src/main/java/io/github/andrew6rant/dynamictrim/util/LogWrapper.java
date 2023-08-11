package io.github.andrew6rant.dynamictrim.util;

import org.slf4j.Logger;

public class LogWrapper {
    private final Logger logger;
    private final String prefix;

    public LogWrapper(Logger logger, String prefix) {
        this.logger = logger;
        this.prefix = prefix;
    }

    public static LogWrapper of(Logger logger, String prefix) {
        return new LogWrapper(logger, prefix);
    }

    public void info(String message, Object... args) {
        logger.info(prefix + " " + message, args);
    }

    public void warn(String message, Object... args) {
        logger.warn(prefix + " " + message, args);
    }

    public void error(String message, Object... args) {
        logger.error(prefix + " " + message, args);
    }

    public void debug(String message, Object... args) {
        logger.debug(prefix + " " + message, args);
    }
}
