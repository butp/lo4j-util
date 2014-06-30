import org.slf4j.Logger;
import sun.misc.SharedSecrets;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yangxuehua on 2014/6/30.
 */
public class LogUtil {
    private final static ConcurrentHashMap<String, Logger> loggersMap = new ConcurrentHashMap<>(1024);
    private static boolean classLoaded = false;
    private static Class baseWireableExceptionClass = null;

    static {
        Log4jConfUtil.autoConf();
    }

    public static void debug(String msg) {
        getLogger().debug(msg);
    }

    public static void debug(String format, Object... arguments) {
        getLogger().debug(format, arguments);
    }

    public static void info(String msg) {
        getLogger().info(msg);
    }

    public static void info(String format, Object... arguments) {
        getLogger().info(format, arguments);
    }

    public static void warn(String msg) {
        getLogger().warn(msg);
    }

    public static void warn(String format, Object... arguments) {
        getLogger().warn(format, arguments);
    }

    public static void error(String msg) {
        getLogger().error(msg);
    }

    public static void error(String format, Object... arguments) {
        getLogger().error(format, arguments);
    }

    public static void error(String msg, Throwable e) {
        String infoContent = convertErrorToInfo(e);
        if (infoContent != null) {
            getLogger().info(msg + " : " + infoContent);
        } else {
            getLogger().error(msg, e);
        }
    }

    public static void error(Throwable e) {
        String infoContent = convertErrorToInfo(e);
        if (infoContent != null) {
            getLogger().info(infoContent);
        } else {
            getLogger().error(e.getMessage(), e);
        }
    }

    /**
     * @param e
     * @return null 不转info, not-null-string 转info content
     */
    private static String convertErrorToInfo(Throwable e) {
        if (!classLoaded) {
            classLoaded = true;
            try {
                baseWireableExceptionClass = Class.forName("com.qlc.iframe.spring.wire.BaseWireableException");
            } catch (ClassNotFoundException e1) {
            }
        }
        if (baseWireableExceptionClass != null && baseWireableExceptionClass.isInstance(e)) {
            return e.toString();
        }
        return null;
    }

    private static Logger getLogger() {
        StackTraceElement ele = SharedSecrets.getJavaLangAccess().getStackTraceElement(new Exception(), 2);
        String callerClass = ele.getClassName();
        Logger logger = loggersMap.get(callerClass);
        if (logger == null) {
            logger = MyLoggerFactory.getLoggerForFullCaller(LogUtil.class, callerClass);
            loggersMap.put(callerClass, logger);
        }
        return logger;
    }
}
