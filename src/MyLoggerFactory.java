import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyLoggerFactory {

    public static Logger getLogger(Class someclass) {
        if (someclass == null) {
            return getLogger();
        }
        return new MyLogger(LoggerFactory.getLogger(someclass));
    }

    public static Logger getLogger(String classname) {
        if (classname == null || classname.length() == 0) {
            return getLogger();
        }
        return new MyLogger(LoggerFactory.getLogger(classname));
    }

    public static Logger getLogger() {
        return new MyLogger(LoggerFactory.getLogger(getCallerClassName()));
    }

    private static String getCallerClassName() {
        StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
        for (int i = stacks.length - 1; i >= 0; i--) {
            if (stacks[i].getClassName().equals(MyLoggerFactory.class.getName())) {
                int caller = i + 1;
                if (caller < stacks.length) {
                    return stacks[caller].getClassName();
                }
            }
        }
        return MyLogger.class.getName();
    }

    /**
     * 取得一个可被所有调用者使用的logger(继续保全调用者信息)
     *
     * @param fqcn : The fully qualified class name of the <b>caller</b>
     * @return
     */
    public static Logger getLoggerForFullCaller(Class fqcn) {
        return new MyLogger(LoggerFactory.getLogger(fqcn), fqcn);
    }

    /**
     * 取得一个可被所有调用者使用的logger(继续保全调用者信息)
     *
     * @param fqcn : The fully qualified class name of the <b>caller</b>
     * @return
     */
    public static Logger getLoggerForFullCaller(Class fqcn, String loggerName) {
        return new MyLogger(LoggerFactory.getLogger(loggerName), fqcn);
    }

}
