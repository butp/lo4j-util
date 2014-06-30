import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.LocationAwareLogger;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

/**
 * Created by yangxuehua on 2014/6/30.
 * 包装的org.slf4j.Logger，主要功能是兼容LogUtil
 */
public class MyLogger implements Logger {
    private final static String LINE_SPERATOR = System.getProperty("line.separator");
    private final static String SPACE = " ";
    private static boolean removeLine = false;
    private final Logger internalSlf4jLogger;

    private LocationAwareLogger locationAwareLogger = null;
    private String fqcn = null;

    MyLogger(Logger internalLogger) {
        this(internalLogger, MyLogger.class);
    }

    MyLogger(Logger internalLogger, Class<?> fqcn) {
        this.internalSlf4jLogger = internalLogger;
        if (this.internalSlf4jLogger != null && this.internalSlf4jLogger instanceof LocationAwareLogger) {
            locationAwareLogger = (LocationAwareLogger) this.internalSlf4jLogger;
            if (fqcn != null) {
                this.fqcn = fqcn.getName();
            } else {
                this.fqcn = MyLogger.class.getName();
            }
        }
    }

    private static String removeLineFeed(String s) {
        if (s != null) {
            return s.replace(LINE_SPERATOR, " ");
        }
        return "";
    }

    public static String getStackTrace(Throwable e) {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        e.printStackTrace(new PrintWriter(buf, true));
        return buf.toString();
    }

    /**
     * Return the name of this <code>Logger</code> instance.
     */
    @Override
    public String getName() {
        return this.internalSlf4jLogger.getName();
    }

    /**
     * Is the logger instance enabled for the TRACE level?
     *
     * @return True if this Logger is enabled for the TRACE level,
     * false otherwise.
     * @since 1.4
     */
    @Override
    public boolean isTraceEnabled() {
        return this.internalSlf4jLogger.isTraceEnabled();
    }

    private String reformat(String msgOrFormat) {
        if (locationAwareLogger != null) {
            return removeLine ? removeLineFeed(msgOrFormat) : msgOrFormat;
        }
        StackTraceElement[] stackTraceElements = Thread.currentThread()
                .getStackTrace();
        String classMethodLine = "";
        String threadName = Thread.currentThread().getName();
        long threadId = Thread.currentThread().getId();
        if (stackTraceElements.length >= 4) {
            classMethodLine = stackTraceElements[3].getClassName()
                    + "." + stackTraceElements[3].getMethodName()
                    + "(" + stackTraceElements[3].getLineNumber() + ")";
        }
        String logPrefix = "[" + threadName + " " + threadId + "]-" + "["
                + classMethodLine + "]-";
        return logPrefix + (removeLine ? removeLineFeed(msgOrFormat) : msgOrFormat);
    }

    /**
     * Log a message at the TRACE level.
     *
     * @param msg the message string to be logged
     * @since 1.4
     */
    @Override
    public void trace(String msg) {
        msg = reformat(msg);
        if (this.locationAwareLogger != null) {
            this.locationAwareLogger.log(null, fqcn, LocationAwareLogger.TRACE_INT, msg, null, null);
            return;
        }
        this.internalSlf4jLogger.trace(msg);
    }

    /**
     * Log a message at the TRACE level according to the specified format
     * and argument.
     * <p>
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the TRACE level.
     * </p>
     *
     * @param format the format string
     * @param arg    the argument
     * @since 1.4
     */
    @Override
    public void trace(String format, Object arg) {
        format = reformat(format);
        if (this.locationAwareLogger != null) {
            FormattingTuple ft = MessageFormatter.format(format, arg);
            this.locationAwareLogger.log(null, fqcn, LocationAwareLogger.TRACE_INT, ft.getMessage(), ft.getArgArray(), ft.getThrowable());
            return;
        }
        this.internalSlf4jLogger.trace(format, arg);
    }

    /**
     * Log a message at the TRACE level according to the specified format
     * and arguments.
     * <p>
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the TRACE level.
     * </p>
     *
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     * @since 1.4
     */
    @Override
    public void trace(String format, Object arg1, Object arg2) {
        format = reformat(format);
        if (this.locationAwareLogger != null) {
            FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
            this.locationAwareLogger.log(null, fqcn, LocationAwareLogger.TRACE_INT, ft.getMessage(), ft.getArgArray(), ft.getThrowable());
            return;
        }
        this.internalSlf4jLogger.trace(format, arg1, arg2);
    }

    /**
     * Log a message at the TRACE level according to the specified format
     * and arguments.
     * <p>
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the TRACE level.
     * </p>
     *
     * @param format   the format string
     * @param argArray an array of arguments
     * @since 1.4
     */
    @Override
    public void trace(String format, Object[] argArray) {
        format = reformat(format);
        if (this.locationAwareLogger != null) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
            this.locationAwareLogger.log(null, fqcn, LocationAwareLogger.TRACE_INT, ft.getMessage(), ft.getArgArray(), ft.getThrowable());
            return;
        }
        this.internalSlf4jLogger.trace(format, argArray);
    }

    /**
     * Log an exception (throwable) at the TRACE level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t   the exception (throwable) to log
     * @since 1.4
     */
    @Override
    public void trace(String msg, Throwable t) {
        msg = reformat(msg);
        if (this.locationAwareLogger != null) {
            this.locationAwareLogger.log(null, fqcn, LocationAwareLogger.TRACE_INT, msg, null, t);
            return;
        }
        this.internalSlf4jLogger.trace(msg, t);
    }

    /**
     * Similar to {@link #isTraceEnabled()} method except that the
     * marker data is also taken into account.
     *
     * @param marker The marker data to take into consideration
     * @since 1.4
     */
    @Override
    public boolean isTraceEnabled(Marker marker) {
        return this.internalSlf4jLogger.isTraceEnabled(marker);
    }

    /**
     * Log a message with the specific Marker at the TRACE level.
     *
     * @param marker the marker data specific to this log statement
     * @param msg    the message string to be logged
     * @since 1.4
     */
    @Override
    public void trace(Marker marker, String msg) {
        msg = reformat(msg);
        if (this.locationAwareLogger != null) {
            this.locationAwareLogger.log(marker, fqcn, LocationAwareLogger.TRACE_INT, msg, null, null);
            return;
        }
        this.internalSlf4jLogger.trace(marker, msg);
    }

    /**
     * This method is similar to {@link #trace(String, Object)} method except
     * that the
     * marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg    the argument
     * @since 1.4
     */
    @Override
    public void trace(Marker marker, String format, Object arg) {
        format = reformat(format);
        if (this.locationAwareLogger != null) {
            FormattingTuple ft = MessageFormatter.format(format, arg);
            this.locationAwareLogger.log(marker, fqcn, LocationAwareLogger.TRACE_INT, ft.getMessage(), ft.getArgArray(), ft.getThrowable());
            return;
        }
        this.internalSlf4jLogger.trace(marker, format, arg);
    }

    /**
     * This method is similar to {@link #trace(String, Object, Object)} method
     * except that the marker data is also taken into
     * consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     * @since 1.4
     */
    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        format = reformat(format);
        if (this.locationAwareLogger != null) {
            FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
            this.locationAwareLogger.log(marker, fqcn, LocationAwareLogger.TRACE_INT, ft.getMessage(), ft.getArgArray(), ft.getThrowable());
            return;
        }
        this.internalSlf4jLogger.trace(marker, format, arg2);
    }

    /**
     * This method is similar to {@link #trace(String, Object[])} method except
     * that the marker data is also taken into
     * consideration.
     *
     * @param marker   the marker data specific to this log statement
     * @param format   the format string
     * @param argArray an array of arguments
     * @since 1.4
     */
    @Override
    public void trace(Marker marker, String format, Object[] argArray) {
        format = reformat(format);
        if (this.locationAwareLogger != null) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
            this.locationAwareLogger.log(marker, fqcn, LocationAwareLogger.TRACE_INT, ft.getMessage(), ft.getArgArray(), ft.getThrowable());
            return;
        }
        this.internalSlf4jLogger.trace(marker, format, argArray);
    }

    /**
     * This method is similar to {@link #trace(String, Throwable)} method except
     * that the
     * marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param msg    the message accompanying the exception
     * @param t      the exception (throwable) to log
     * @since 1.4
     */
    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        msg = reformat(msg);
        if (this.locationAwareLogger != null) {
            this.locationAwareLogger.log(marker, fqcn, LocationAwareLogger.TRACE_INT, msg, null, t);
            return;
        }
        this.internalSlf4jLogger.trace(marker, msg, t);
    }

    /**
     * Is the logger instance enabled for the DEBUG level?
     *
     * @return True if this Logger is enabled for the DEBUG level,
     * false otherwise.
     */
    @Override
    public boolean isDebugEnabled() {
        return this.internalSlf4jLogger.isDebugEnabled();
    }

    /**
     * Log a message at the DEBUG level.
     *
     * @param msg the message string to be logged
     */
    @Override
    public void debug(String msg) {
        msg = reformat(msg);
        if (this.locationAwareLogger != null) {
            this.locationAwareLogger.log(null, fqcn, LocationAwareLogger.DEBUG_INT, msg, null, null);
            return;
        }
        this.internalSlf4jLogger.debug(msg);
    }

    /**
     * Log a message at the DEBUG level according to the specified format
     * and argument.
     * <p>
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the DEBUG level.
     * </p>
     *
     * @param format the format string
     * @param arg    the argument
     */
    @Override
    public void debug(String format, Object arg) {
        format = reformat(format);
        if (this.locationAwareLogger != null) {
            FormattingTuple ft = MessageFormatter.format(format, arg);
            this.locationAwareLogger.log(null, fqcn, LocationAwareLogger.DEBUG_INT, ft.getMessage(), ft.getArgArray(), ft.getThrowable());
            return;
        }
        this.internalSlf4jLogger.debug(format, arg);
    }

    /**
     * Log a message at the DEBUG level according to the specified format
     * and arguments.
     * <p>
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the DEBUG level.
     * </p>
     *
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
    @Override
    public void debug(String format, Object arg1, Object arg2) {
        format = reformat(format);
        if (this.locationAwareLogger != null) {
            FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
            this.locationAwareLogger.log(null, fqcn, LocationAwareLogger.DEBUG_INT, ft.getMessage(), ft.getArgArray(), ft.getThrowable());
            return;
        }
        this.internalSlf4jLogger.debug(format, arg1, arg2);
    }

    /**
     * Log a message at the DEBUG level according to the specified format
     * and arguments.
     * <p>
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the DEBUG level.
     * </p>
     *
     * @param format   the format string
     * @param argArray an array of arguments
     */
    @Override
    public void debug(String format, Object[] argArray) {
        format = reformat(format);
        if (this.locationAwareLogger != null) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
            this.locationAwareLogger.log(null, fqcn, LocationAwareLogger.DEBUG_INT, ft.getMessage(), ft.getArgArray(), ft.getThrowable());
            return;
        }
        this.internalSlf4jLogger.debug(format, argArray);
    }

    /**
     * Log an exception (throwable) at the DEBUG level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t   the exception (throwable) to log
     */
    @Override
    public void debug(String msg, Throwable t) {
        msg = reformat(msg);
        if (this.locationAwareLogger != null) {
            this.locationAwareLogger.log(null, fqcn, LocationAwareLogger.DEBUG_INT, msg, null, t);
            return;
        }
        this.internalSlf4jLogger.debug(msg, t);
    }

    /**
     * Similar to {@link #isDebugEnabled()} method except that the
     * marker data is also taken into account.
     *
     * @param marker The marker data to take into consideration
     */
    @Override
    public boolean isDebugEnabled(Marker marker) {
        return this.internalSlf4jLogger.isDebugEnabled(marker);
    }

    /**
     * Log a message with the specific Marker at the DEBUG level.
     *
     * @param marker the marker data specific to this log statement
     * @param msg    the message string to be logged
     */
    @Override
    public void debug(Marker marker, String msg) {
        msg = reformat(msg);
        if (this.locationAwareLogger != null) {
            this.locationAwareLogger.log(marker, fqcn, LocationAwareLogger.DEBUG_INT, msg, null, null);
            return;
        }
        this.internalSlf4jLogger.debug(marker, msg);
    }

    /**
     * This method is similar to {@link #debug(String, Object)} method except
     * that the
     * marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg    the argument
     */
    @Override
    public void debug(Marker marker, String format, Object arg) {
        format = reformat(format);
        if (this.locationAwareLogger != null) {
            FormattingTuple ft = MessageFormatter.format(format, arg);
            this.locationAwareLogger.log(marker, fqcn, LocationAwareLogger.DEBUG_INT, ft.getMessage(), ft.getArgArray(), ft.getThrowable());
            return;
        }
        this.internalSlf4jLogger.debug(marker, format, arg);
    }

    /**
     * This method is similar to {@link #debug(String, Object, Object)} method
     * except that the marker data is also taken into
     * consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        format = reformat(format);
        if (this.locationAwareLogger != null) {
            FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
            this.locationAwareLogger.log(marker, fqcn, LocationAwareLogger.DEBUG_INT, ft.getMessage(), ft.getArgArray(), ft.getThrowable());
            return;
        }
        this.internalSlf4jLogger.debug(marker, format, arg1, arg2);
    }

    /**
     * This method is similar to {@link #debug(String, Object[])} method except
     * that the marker data is also taken into
     * consideration.
     *
     * @param marker   the marker data specific to this log statement
     * @param format   the format string
     * @param argArray an array of arguments
     */
    @Override
    public void debug(Marker marker, String format, Object[] argArray) {
        format = reformat(format);
        if (this.locationAwareLogger != null) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
            this.locationAwareLogger.log(marker, fqcn, LocationAwareLogger.DEBUG_INT, ft.getMessage(), ft.getArgArray(), ft.getThrowable());
            return;
        }
        this.internalSlf4jLogger.debug(marker, format, argArray);
    }

    /**
     * This method is similar to {@link #debug(String, Throwable)} method except
     * that the
     * marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param msg    the message accompanying the exception
     * @param t      the exception (throwable) to log
     */
    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        msg = reformat(msg);
        if (this.locationAwareLogger != null) {
            this.locationAwareLogger.log(marker, fqcn, LocationAwareLogger.DEBUG_INT, msg, null, t);
            return;
        }
        this.internalSlf4jLogger.debug(marker, msg, t);
    }

    /**
     * Is the logger instance enabled for the INFO level?
     *
     * @return True if this Logger is enabled for the INFO level,
     * false otherwise.
     */
    @Override
    public boolean isInfoEnabled() {
        return this.internalSlf4jLogger.isInfoEnabled();
    }

    /**
     * Log a message at the INFO level.
     *
     * @param msg the message string to be logged
     */
    @Override
    public void info(String msg) {
        msg = reformat(msg);
        if (this.locationAwareLogger != null) {
            this.locationAwareLogger.log(null, fqcn, LocationAwareLogger.INFO_INT, msg, null, null);
            return;
        }
        this.internalSlf4jLogger.info(msg);
    }

    /**
     * Log a message at the INFO level according to the specified format
     * and argument.
     * <p>
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the INFO level.
     * </p>
     *
     * @param format the format string
     * @param arg    the argument
     */
    @Override
    public void info(String format, Object arg) {
        format = reformat(format);
        if (this.locationAwareLogger != null) {
            FormattingTuple ft = MessageFormatter.format(format, arg);
            this.locationAwareLogger.log(null, fqcn, LocationAwareLogger.INFO_INT, ft.getMessage(), ft.getArgArray(), ft.getThrowable());
            return;
        }
        this.internalSlf4jLogger.info(format, arg);
    }

    /**
     * Log a message at the INFO level according to the specified format
     * and arguments.
     * <p>
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the INFO level.
     * </p>
     *
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
    @Override
    public void info(String format, Object arg1, Object arg2) {
        format = reformat(format);
        if (this.locationAwareLogger != null) {
            FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
            this.locationAwareLogger.log(null, fqcn, LocationAwareLogger.INFO_INT, ft.getMessage(), ft.getArgArray(), ft.getThrowable());
            return;
        }
        this.internalSlf4jLogger.info(format, arg1, arg2);
    }

    /**
     * Log a message at the INFO level according to the specified format
     * and arguments.
     * <p>
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the INFO level.
     * </p>
     *
     * @param format   the format string
     * @param argArray an array of arguments
     */
    @Override
    public void info(String format, Object[] argArray) {
        format = reformat(format);
        if (this.locationAwareLogger != null) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
            this.locationAwareLogger.log(null, fqcn, LocationAwareLogger.INFO_INT, ft.getMessage(), ft.getArgArray(), ft.getThrowable());
            return;
        }
        this.internalSlf4jLogger.info(format, argArray);
    }

    /**
     * Log an exception (throwable) at the INFO level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t   the exception (throwable) to log
     */
    @Override
    public void info(String msg, Throwable t) {
        msg = reformat(msg);
        if (this.locationAwareLogger != null) {
            this.locationAwareLogger.log(null, fqcn, LocationAwareLogger.INFO_INT, msg, null, t);
            return;
        }
        this.internalSlf4jLogger.info(msg, t);
    }

    /**
     * Similar to {@link #isInfoEnabled()} method except that the marker
     * data is also taken into consideration.
     *
     * @param marker The marker data to take into consideration
     */
    @Override
    public boolean isInfoEnabled(Marker marker) {
        return this.internalSlf4jLogger.isInfoEnabled(marker);
    }

    /**
     * Log a message with the specific Marker at the INFO level.
     *
     * @param marker The marker specific to this log statement
     * @param msg    the message string to be logged
     */
    @Override
    public void info(Marker marker, String msg) {
        msg = reformat(msg);
        if (this.locationAwareLogger != null) {
            this.locationAwareLogger.log(marker, fqcn, LocationAwareLogger.INFO_INT, msg, null, null);
            return;
        }
        this.internalSlf4jLogger.info(marker, msg);
    }

    /**
     * This method is similar to {@link #info(String, Object)} method except
     * that the
     * marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg    the argument
     */
    @Override
    public void info(Marker marker, String format, Object arg) {
        format = reformat(format);
        if (this.locationAwareLogger != null) {
            FormattingTuple ft = MessageFormatter.format(format, arg);
            this.locationAwareLogger.log(marker, fqcn, LocationAwareLogger.INFO_INT, ft.getMessage(), ft.getArgArray(), ft.getThrowable());
            return;
        }
        this.internalSlf4jLogger.info(marker, format, arg);
    }

    /**
     * This method is similar to {@link #info(String, Object, Object)} method
     * except that the marker data is also taken into
     * consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        format = reformat(format);
        if (this.locationAwareLogger != null) {
            FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
            this.locationAwareLogger.log(marker, fqcn, LocationAwareLogger.INFO_INT, ft.getMessage(), ft.getArgArray(), ft.getThrowable());
            return;
        }
        this.internalSlf4jLogger.info(marker, format, arg1, arg2);
    }

    /**
     * This method is similar to {@link #info(String, Object[])} method except
     * that the marker data is also taken into
     * consideration.
     *
     * @param marker   the marker data specific to this log statement
     * @param format   the format string
     * @param argArray an array of arguments
     */
    @Override
    public void info(Marker marker, String format, Object[] argArray) {
        format = reformat(format);
        if (this.locationAwareLogger != null) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
            this.locationAwareLogger.log(marker, fqcn, LocationAwareLogger.INFO_INT, ft.getMessage(), ft.getArgArray(), ft.getThrowable());
            return;
        }
        this.internalSlf4jLogger.info(marker, format, argArray);
    }

    /**
     * This method is similar to {@link #info(String, Throwable)} method
     * except that the marker data is also taken into consideration.
     *
     * @param marker the marker data for this log statement
     * @param msg    the message accompanying the exception
     * @param t      the exception (throwable) to log
     */
    @Override
    public void info(Marker marker, String msg, Throwable t) {
        msg = reformat(msg);
        if (this.locationAwareLogger != null) {
            this.locationAwareLogger.log(marker, fqcn, LocationAwareLogger.INFO_INT, msg, null, t);
            return;
        }
        this.internalSlf4jLogger.info(marker, msg, t);
    }

    /**
     * Is the logger instance enabled for the WARN level?
     *
     * @return True if this Logger is enabled for the WARN level,
     * false otherwise.
     */
    @Override
    public boolean isWarnEnabled() {
        return this.internalSlf4jLogger.isWarnEnabled();
    }

    /**
     * Log a message at the WARN level.
     *
     * @param msg the message string to be logged
     */
    @Override
    public void warn(String msg) {
        msg = reformat(msg);
        if (this.locationAwareLogger != null) {
            this.locationAwareLogger.log(null, fqcn, LocationAwareLogger.WARN_INT, msg, null, null);
            return;
        }
        this.internalSlf4jLogger.warn(msg);
    }

    /**
     * Log a message at the WARN level according to the specified format
     * and argument.
     * <p>
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the WARN level.
     * </p>
     *
     * @param format the format string
     * @param arg    the argument
     */
    @Override
    public void warn(String format, Object arg) {
        format = reformat(format);
        if (this.locationAwareLogger != null) {
            FormattingTuple ft = MessageFormatter.format(format, arg);
            this.locationAwareLogger.log(null, fqcn, LocationAwareLogger.WARN_INT, ft.getMessage(), ft.getArgArray(), ft.getThrowable());
            return;
        }
        this.internalSlf4jLogger.warn(format, arg);
    }

    /**
     * Log a message at the WARN level according to the specified format
     * and arguments.
     * <p>
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the WARN level.
     * </p>
     *
     * @param format   the format string
     * @param argArray an array of arguments
     */
    @Override
    public void warn(String format, Object[] argArray) {
        format = reformat(format);
        if (this.locationAwareLogger != null) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
            this.locationAwareLogger.log(null, fqcn, LocationAwareLogger.WARN_INT, ft.getMessage(), ft.getArgArray(), ft.getThrowable());
            return;
        }
        this.internalSlf4jLogger.warn(format, argArray);
    }

    /**
     * Log a message at the WARN level according to the specified format
     * and arguments.
     * <p>
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the WARN level.
     * </p>
     *
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
    @Override
    public void warn(String format, Object arg1, Object arg2) {
        format = reformat(format);
        if (this.locationAwareLogger != null) {
            FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
            this.locationAwareLogger.log(null, fqcn, LocationAwareLogger.WARN_INT, ft.getMessage(), ft.getArgArray(), ft.getThrowable());
            return;
        }
        this.internalSlf4jLogger.warn(format, arg1, arg2);
    }

    /**
     * Log an exception (throwable) at the WARN level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t   the exception (throwable) to log
     */
    @Override
    public void warn(String msg, Throwable t) {
        msg = reformat(msg);
        if (this.locationAwareLogger != null) {
            this.locationAwareLogger.log(null, fqcn, LocationAwareLogger.WARN_INT, msg, null, t);
            return;
        }
        this.internalSlf4jLogger.warn(msg, t);
    }

    /**
     * Similar to {@link #isWarnEnabled()} method except that the marker
     * data is also taken into consideration.
     *
     * @param marker The marker data to take into consideration
     */
    @Override
    public boolean isWarnEnabled(Marker marker) {
        return this.internalSlf4jLogger.isWarnEnabled(marker);
    }

    /**
     * Log a message with the specific Marker at the WARN level.
     *
     * @param marker The marker specific to this log statement
     * @param msg    the message string to be logged
     */
    @Override
    public void warn(Marker marker, String msg) {
        msg = reformat(msg);
        if (this.locationAwareLogger != null) {
            this.locationAwareLogger.log(marker, fqcn, LocationAwareLogger.WARN_INT, msg, null, null);
            return;
        }
        this.internalSlf4jLogger.warn(marker, msg);
    }

    /**
     * This method is similar to {@link #warn(String, Object)} method except
     * that the
     * marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg    the argument
     */
    @Override
    public void warn(Marker marker, String format, Object arg) {
        format = reformat(format);
        if (this.locationAwareLogger != null) {
            FormattingTuple ft = MessageFormatter.format(format, arg);
            this.locationAwareLogger.log(marker, fqcn, LocationAwareLogger.WARN_INT, ft.getMessage(), ft.getArgArray(), ft.getThrowable());
            return;
        }
        this.internalSlf4jLogger.warn(marker, format, arg);
    }

    /**
     * This method is similar to {@link #warn(String, Object, Object)} method
     * except that the marker data is also taken into
     * consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        format = reformat(format);
        if (this.locationAwareLogger != null) {
            FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
            this.locationAwareLogger.log(marker, fqcn, LocationAwareLogger.WARN_INT, ft.getMessage(), ft.getArgArray(), ft.getThrowable());
            return;
        }
        this.internalSlf4jLogger.warn(marker, format, arg1, arg2);
    }

    /**
     * This method is similar to {@link #warn(String, Object[])} method except
     * that the marker data is also taken into
     * consideration.
     *
     * @param marker   the marker data specific to this log statement
     * @param format   the format string
     * @param argArray an array of arguments
     */
    @Override
    public void warn(Marker marker, String format, Object[] argArray) {
        format = reformat(format);
        if (this.locationAwareLogger != null) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
            this.locationAwareLogger.log(marker, fqcn, LocationAwareLogger.WARN_INT, ft.getMessage(), ft.getArgArray(), ft.getThrowable());
            return;
        }
        this.internalSlf4jLogger.warn(marker, format, argArray);
    }

    /**
     * This method is similar to {@link #warn(String, Throwable)} method
     * except that the marker data is also taken into consideration.
     *
     * @param marker the marker data for this log statement
     * @param msg    the message accompanying the exception
     * @param t      the exception (throwable) to log
     */
    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        msg = reformat(msg);
        if (this.locationAwareLogger != null) {
            this.locationAwareLogger.log(marker, fqcn, LocationAwareLogger.WARN_INT, msg, null, t);
            return;
        }
        this.internalSlf4jLogger.warn(marker, msg, t);
    }

    /**
     * Is the logger instance enabled for the ERROR level?
     *
     * @return True if this Logger is enabled for the ERROR level,
     * false otherwise.
     */
    @Override
    public boolean isErrorEnabled() {
        return this.internalSlf4jLogger.isErrorEnabled();
    }

    /**
     * Log a message at the ERROR level.
     *
     * @param msg the message string to be logged
     */
    @Override
    public void error(String msg) {
        msg = reformat(msg);
        if (this.locationAwareLogger != null) {
            this.locationAwareLogger.log(null, fqcn, LocationAwareLogger.ERROR_INT, msg, null, null);
            return;
        }
        this.internalSlf4jLogger.error(msg);
    }

    /**
     * Log a message at the ERROR level according to the specified format
     * and argument.
     * <p>
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the ERROR level.
     * </p>
     *
     * @param format the format string
     * @param arg    the argument
     */
    @Override
    public void error(String format, Object arg) {
        format = reformat(format);
        if (this.locationAwareLogger != null) {
            FormattingTuple ft = MessageFormatter.format(format, arg);
            this.locationAwareLogger.log(null, fqcn, LocationAwareLogger.ERROR_INT, ft.getMessage(), ft.getArgArray(), ft.getThrowable());
            return;
        }
        this.internalSlf4jLogger.error(format, arg);
    }

    /**
     * Log a message at the ERROR level according to the specified format
     * and arguments.
     * <p>
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the ERROR level.
     * </p>
     *
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
    @Override
    public void error(String format, Object arg1, Object arg2) {
        format = reformat(format);
        if (this.locationAwareLogger != null) {
            FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
            this.locationAwareLogger.log(null, fqcn, LocationAwareLogger.ERROR_INT, ft.getMessage(), ft.getArgArray(), ft.getThrowable());
            return;
        }
        this.internalSlf4jLogger.error(format, arg1, arg2);
    }

    /**
     * Log a message at the ERROR level according to the specified format
     * and arguments.
     * <p>
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the ERROR level.
     * </p>
     *
     * @param format   the format string
     * @param argArray an array of arguments
     */
    @Override
    public void error(String format, Object[] argArray) {
        format = reformat(format);
        if (this.locationAwareLogger != null) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
            this.locationAwareLogger.log(null, fqcn, LocationAwareLogger.ERROR_INT, ft.getMessage(), ft.getArgArray(), ft.getThrowable());
            return;
        }
        this.internalSlf4jLogger.error(format, argArray);
    }

    /**
     * Log an exception (throwable) at the ERROR level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t   the exception (throwable) to log
     */
    @Override
    public void error(String msg, Throwable t) {
        msg = reformat(msg);
        if (this.locationAwareLogger != null) {
            this.locationAwareLogger.log(null, fqcn, LocationAwareLogger.ERROR_INT, msg, null, t);
            return;
        }
        this.internalSlf4jLogger.error(msg, t);
    }

    /**
     * Similar to {@link #isErrorEnabled()} method except that the
     * marker data is also taken into consideration.
     *
     * @param marker The marker data to take into consideration
     */
    @Override
    public boolean isErrorEnabled(Marker marker) {
        return this.internalSlf4jLogger.isErrorEnabled(marker);
    }

    /**
     * Log a message with the specific Marker at the ERROR level.
     *
     * @param marker The marker specific to this log statement
     * @param msg    the message string to be logged
     */
    @Override
    public void error(Marker marker, String msg) {
        msg = reformat(msg);
        if (this.locationAwareLogger != null) {
            this.locationAwareLogger.log(marker, fqcn, LocationAwareLogger.ERROR_INT, msg, null, null);
            return;
        }
        this.internalSlf4jLogger.error(marker, msg);
    }

    /**
     * This method is similar to {@link #error(String, Object)} method except
     * that the
     * marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg    the argument
     */
    @Override
    public void error(Marker marker, String format, Object arg) {
        format = reformat(format);
        if (this.locationAwareLogger != null) {
            FormattingTuple ft = MessageFormatter.format(format, arg);
            this.locationAwareLogger.log(marker, fqcn, LocationAwareLogger.ERROR_INT, ft.getMessage(), ft.getArgArray(), ft.getThrowable());
            return;
        }
        this.internalSlf4jLogger.error(marker, format, arg);
    }

    /**
     * This method is similar to {@link #error(String, Object, Object)} method
     * except that the marker data is also taken into
     * consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        format = reformat(format);
        if (this.locationAwareLogger != null) {
            FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
            this.locationAwareLogger.log(marker, fqcn, LocationAwareLogger.ERROR_INT, ft.getMessage(), ft.getArgArray(), ft.getThrowable());
            return;
        }
        this.internalSlf4jLogger.error(marker, format, arg1, arg2);
    }

    /**
     * This method is similar to {@link #error(String, Object[])} method except
     * that the marker data is also taken into
     * consideration.
     *
     * @param marker   the marker data specific to this log statement
     * @param format   the format string
     * @param argArray an array of arguments
     */
    @Override
    public void error(Marker marker, String format, Object[] argArray) {
        format = reformat(format);
        if (this.locationAwareLogger != null) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
            this.locationAwareLogger.log(marker, fqcn, LocationAwareLogger.ERROR_INT, ft.getMessage(), ft.getArgArray(), ft.getThrowable());
            return;
        }
        this.internalSlf4jLogger.error(marker, format, argArray);
    }

    /**
     * This method is similar to {@link #error(String, Throwable)} method except
     * that the marker data is also taken into
     * consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param msg    the message accompanying the exception
     * @param t      the exception (throwable) to log
     */
    @Override
    public void error(Marker marker, String msg, Throwable t) {
        msg = reformat(msg);
        if (this.locationAwareLogger != null) {
            this.locationAwareLogger.log(marker, fqcn, LocationAwareLogger.ERROR_INT, msg, null, t);
            return;
        }
        this.internalSlf4jLogger.error(marker, msg, t);
    }
}
