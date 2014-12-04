/**
 * @Title LogUtils.java
 * @Description: TODO
 * @author Eden lee
 * @date Apr 15, 2013
 * @version
 */
package com.github.lisicnu.log4android;

import android.content.Context;
import android.util.Log;

import com.github.lisicnu.log4android.config.Configurator;

/**
 * call one of these method to initialize <p/>
 * {@link #init(android.content.Context, String, boolean)}, <p/>
 * {@link #init(android.content.Context)} , <p/>
 * {@link #init(android.content.Context, int)}
 * .<br/>
 * <p/>
 * <br/>
 * <br/>
 * <p/>
 * if {@link #isDebug} was settle to true. then the logs will be output to android logcat window.
 * <p/>
 * <br/>
 * <br/>
 * when publish the APK, suggest to set {@link #isDebug} to false.
 */
public final class LogManager {

    /**
     * if set this to true, will flush logs to adb logcat windows, otherwise
     * will depend on log4android's configuration file.
     */
    public static boolean isDebug = true;
    static Logger logger;

    public static void logMemoryInfo() {

        Log.i("Memory Max=" + Runtime.getRuntime().maxMemory() / 1024f / 1024, "Total/Free= "
                + Runtime.getRuntime().totalMemory() / 1024f / 1024 + "/"
                + Runtime.getRuntime().freeMemory() / 1024f / 1024);
    }

    static {
        logger = LoggerFactory.getLogger();
    }

    public static Logger getLogger() {
        return logger;
    }

    /**
     * use default configuration for the logger.
     *
     * @param context
     */
    public static void init(Context context) {
        Configurator.getConfigurator(context).configure();
        setClientId(context);
    }

    static void setClientId(Context context) {
        if (logger != null && context != null)
            logger.setClientID(context.getPackageName());
    }

    public static void init(Context context, int resID) {
        Configurator.getConfigurator(context).configure(resID);
        setClientId(context);
    }

    /**
     * @param context        context
     * @param fileName       filename in assets folder.
     * @param isExternalFile is external file or not.
     */
    public static void init(Context context, String fileName, boolean isExternalFile) {
        Configurator.getConfigurator(context).configure(fileName, isExternalFile);
        setClientId(context);
    }

    ///////////////////////////////////////////////////////////////////////
    // 下面的方法将是常规的日志写法, 用 android logcat 输出. 不会通过配置文件写入.
    ///////////////////////////////////////////////////////////////////////

    public static void d(String tag, Object msg) {
        log(tag, msg, Log.DEBUG);
    }

    public static void v(String tag, Object msg) {
        log(tag, msg, Log.VERBOSE);
    }

    public static void i(String tag, Object msg) {
        log(tag, msg, Log.INFO);
    }

    public static void w(String tag, Object msg) {
        log(tag, msg, Log.WARN);
    }

    public static void e(String tag, Object msg) {
        log(tag, msg, Log.ERROR);
    }

    public static void fetal(String tag, Object msg) {
        log(tag, msg, Log.ASSERT);
    }

    private static void log(String tag, Object msg, int level) {
        if (msg == null)
            return;

        if (isDebug) {
            consoleLog(tag, msg, level);
            return;
        }

        if (logger != null) {
            Throwable p = null;
            String t = tag;
            if (msg instanceof Throwable) {
                p = (Throwable) msg;
            } else {
                t = tag.concat("   ").concat(msg.toString());
            }
            switch (level) {
                case Log.DEBUG:
                    logger.debug(t, p);
                    break;
                case Log.ERROR:
                    logger.error(t, p);
                    break;
                case Log.WARN:
                    logger.warn(t, p);
                    break;
                case Log.ASSERT:
                    logger.fatal(t, p);
                    break;
                case Log.VERBOSE:
                    logger.debug(t, p);
                    break;
                default:
                    logger.info(t, p);
                    break;
            }
        }
    }

    static void consoleLog(String tag, Object msg, int level) {

        switch (level) {
            case Log.DEBUG:
                if (msg instanceof Throwable)
                    Log.d(tag, "", (Throwable) msg);
                else
                    Log.d(tag, msg.toString());
                break;

            case Log.ERROR:
                if (msg instanceof Throwable)
                    Log.e(tag, "", (Throwable) msg);
                else
                    Log.e(tag, msg.toString());
                break;

            case Log.WARN:
                if (msg instanceof Throwable)
                    Log.w(tag, "", (Throwable) msg);
                else
                    Log.w(tag, msg.toString());
                break;

            case Log.VERBOSE:
                if (msg instanceof Throwable)
                    Log.v(tag, "", (Throwable) msg);
                else
                    Log.v(tag, msg.toString());
                break;

            case Log.ASSERT:
            default:
                if (msg instanceof Throwable)
                    Log.i(tag, "", (Throwable) msg);
                else
                    Log.i(tag, msg.toString());
                break;
        }
    }

}
