/**
 *
 */
package com.github.lisicnu.log4android.config;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.util.Log;

import com.github.lisicnu.log4android.Level;
import com.github.lisicnu.log4android.Logger;
import com.github.lisicnu.log4android.appender.Appender;
import com.github.lisicnu.log4android.appender.FileAppender;
import com.github.lisicnu.log4android.format.Formatter;
import com.github.lisicnu.log4android.format.PatternFormatter;
import com.github.lisicnu.log4android.repository.DefaultLoggerRepository;
import com.github.lisicnu.log4android.repository.LoggerRepository;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * The {@link Configurator} is used for configuration via a properties file. The
 * properties file should be put in one of the following directories: <br/>
 * <br/>
 * <font color='red'>Note: <br/>
 * call this method before your application's first log entity</font>
 * <ol>
 * <li>The assets directory</li>
 * <li>The res/raw directory</li>
 * </ol>
 *
 * @author Johan Karlsson
 */
public class Configurator {
    /**
     * The key for setting the root logger.
     */
    public static final String ROOT_LOGGER_KEY = "microlog.rootLogger";
    public static final String MICROLOG_PREFIX = "microlog";
    /**
     * The key for setting the logger.
     */
    public static final String LOGGER_PREFIX_KEY = "microlog.logger";
    /**
     * The key for setting the formatter.
     */
    public static final String FORMATTER_PREFIX_KEY = "microlog.formatter";
    /**
     * The key for setting the pattern.
     */
    public static final String PATTERN_LAYOUT_PREFIX_KEY = "microlog.formatter.PatternFormatter.pattern";
    /**
     * The key for setting the appender.
     */
    public static final String APPENDER_PREFIX_KEY = "microlog.appender";
    /**
     * The key for setting the file name for FileAppender.
     */
    public static final String FILE_APPENDER_FILE_NAME_KEY = "microlog.appender.FileAppender.File";
    /**
     * The key for setting file's options.
     */
    public static final String FILE_APPENDER_WRAP_PREFIX_KEY = "microlog.appender.FileAppender.Options";
    /**
     * The key for setting the level.
     */
    public static final String LOG_LEVEL_PREFIX_KEY = "microlog.level";
    /**
     * The key for add default logger or not.
     */
    public static final String LOG_ADD_DEFAULT_LOGGER = "microlog.addDefaultLogger";
    /**
     * The key for setting the logging tag.
     */
    public static final String TAG_PREFIX_KEY = "microlog.tag";
    public static final String[] APPENDER_ALIASES = {"LogCatAppender", "FileAppender"};
    public static final String[] APPENDER_CLASS_NAMES = {
            "com.github.lisicnu.log4android.appender.LogCatAppender",
            "com.github.lisicnu.log4android.appender.FileAppender"};
    public static final String[] FORMATTER_ALIASES = {"SimpleFormatter", "PatternFormatter"};
    public static final String[] FORMATTER_CLASS_NAMES = {
            "com.github.lisicnu.log4android.format.SimpleFormatter",
            "com.github.lisicnu.log4android.format.PatternFormatter"};
    private static final String TAG = Configurator.class.getSimpleName();
    private static final HashMap<String, String> appenderAliases = new HashMap<String, String>(43);
    private static final HashMap<String, String> formatterAliases = new HashMap<String, String>(21);
    public static String DEFAULT_PROPERTIES_FILENAME = "microlog.properties";
    private Context context;

    private LoggerRepository loggerRepository;

    {
        for (int index = 0; index < APPENDER_ALIASES.length; index++) {
            appenderAliases.put(APPENDER_ALIASES[index], APPENDER_CLASS_NAMES[index]);
        }

        for (int index = 0; index < FORMATTER_ALIASES.length; index++) {
            formatterAliases.put(FORMATTER_ALIASES[index], FORMATTER_CLASS_NAMES[index]);
        }
    }

    private Configurator(Context context) {
        this.context = context;
        loggerRepository = DefaultLoggerRepository.INSTANCE;
    }

    /**
     * Create a configurator for the specified context.
     *
     * @param context the {@link android.content.Context} to get the configurator for.
     * @return a configurator
     */
    public static Configurator getConfigurator(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("The context must not be null");
        }

        return new Configurator(context);
    }

    /**
     * Configure using the default properties filename, i.e
     * "microlog.properties". <br/>
     * <br/>
     * file should be in the /assets directory.
     */
    public void configure() {
        configure(DEFAULT_PROPERTIES_FILENAME, false);
    }

    /**
     * Configure using the specified filename.
     *
     * @param filename       the filename of the properties file used for configuration.
     * @param isExternalFile the filename is from assets or external storage.
     */
    public void configure(String filename, boolean isExternalFile) {
        try {
            Properties properties;
            InputStream inputStream;

            if (!isExternalFile) {
                Resources resources = context.getResources();
                AssetManager assetManager = resources.getAssets();

                inputStream = assetManager.open(filename);
            } else {
                inputStream = new FileInputStream(filename);
            }

            properties = loadProperties(inputStream);
            inputStream.close();

            startConfiguration(properties);
        } catch (IOException e) {
            Log.e(TAG, "Failed to open file. " + filename + " " + e);
        }
    }

    /**
     * Configure using the specified resource Id.
     *
     * @param resId the resource Id where to load the properties from.
     */
    public void configure(int resId) {
        Resources resources = context.getResources();

        try {
            InputStream rawResource = resources.openRawResource(resId);
            Properties properties = loadProperties(rawResource);
            startConfiguration(properties);
        } catch (NotFoundException e) {
            Log.e(TAG, "Did not find  resource." + e);
        } catch (IOException e) {
            Log.e(TAG, "Failed to read the resource." + e);
        }
    }

    /**
     * Load the properties
     *
     * @param inputStream the {@link java.io.InputStream} to read from
     * @return the {@link java.util.Properties} object containing the properties read from
     * the {@link java.io.InputStream}
     * @throws java.io.IOException if the loading fails.
     */
    private Properties loadProperties(InputStream inputStream) throws IOException {
        Properties properties = new Properties();
        properties.load(inputStream);
        return properties;
    }

    /**
     * Start the configuration
     *
     * @param properties
     */
    private void startConfiguration(Properties properties) {

        if (properties.containsKey(Configurator.ROOT_LOGGER_KEY)) {
            Log.i(TAG, "Modern configuration not yet supported");
        } else {
            Log.i(TAG, "Configure using the simple style (aka classic style)");
            configureSimpleStyle(properties);
        }
    }

    synchronized private void configureSimpleStyle(Properties properties) {
        setLevel(properties);

        String appenderString = properties.getProperty(Configurator.APPENDER_PREFIX_KEY,
                "LogCatAppender");

        List<String> appenderList = parseAppenderString(appenderString);
        setAppenders(appenderList, properties);

        setFormatter(properties);

        setAddDefaultLogger(properties);
    }

    private void setAddDefaultLogger(Properties properties) {
        String addOrNot = (String) properties.get(Configurator.LOG_ADD_DEFAULT_LOGGER);
        if (loggerRepository != null && loggerRepository.getRootLogger() != null)
            loggerRepository.getRootLogger().setAddDefaultLogger(Boolean.parseBoolean(addOrNot));
    }


    private void setLevel(Properties properties) {
        String levelString = (String) properties.get(Configurator.LOG_LEVEL_PREFIX_KEY);
        Level level = stringToLevel(levelString);

        if (level != null) {
            loggerRepository.getRootLogger().setLevel(level);
            Log.i(TAG, "Root level: " + loggerRepository.getRootLogger().getLevel());
        }

    }

    private List<String> parseAppenderString(String appenderString) {
        StringTokenizer tokenizer = new StringTokenizer(appenderString, ";,");
        List<String> appenderList = new ArrayList<String>();

        while (tokenizer.hasMoreElements()) {
            String appender = (String) tokenizer.nextElement();
            appenderList.add(appender);
        }

        return appenderList;
    }

    private void setAppenders(List<String> appenderList, Properties properties) {
        for (String string : appenderList) {
            addAppender(string, properties);
        }
    }

    private void addAppender(String string, Properties properties) {

        Logger rootLogger = loggerRepository.getRootLogger();
        String className = appenderAliases.get(string);

        if (className == null) {
            className = string;
        }

        try {
            Class<?> appenderClass = Class.forName(className);
            Appender appender = (Appender) appenderClass.newInstance();

            if (appender != null) {
                if (appender instanceof FileAppender)
                    setPropertiesForFileAppender(appender, properties);

                Log.i(TAG, "Adding appender " + appender.getClass().getName());
                rootLogger.addAppender(appender);
            }

        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Failed to find appender class: " + e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "No access to appender class: " + e);
        } catch (InstantiationException e) {
            Log.e(TAG, "Failed to instantiate appender class: " + e);
        } catch (ClassCastException e) {
            Log.e(TAG, "Specified appender class does not implement the Appender interface: " + e);
        }
    }

    private void setPropertiesForFileAppender(Appender appender, Properties properties) {

        String fileName = properties.getProperty(FILE_APPENDER_FILE_NAME_KEY, "log");
        ((FileAppender) appender).setFileName(fileName);

        // wrap format
        String append_string = properties.getProperty(FILE_APPENDER_WRAP_PREFIX_KEY, "");
        ((FileAppender) appender).setWrapFormat(append_string);
        ((FileAppender) appender).setContext(context);
    }

    private void setFormatter(Properties properties) {

        String formatterString = (String) properties.getProperty(FORMATTER_PREFIX_KEY,
                "PatternFormatter");

        String className = null;

        if (formatterString != null) {
            className = formatterAliases.get(formatterString);
        }

        if (className == null) {
            className = formatterString;
        }

        try {
            Class formatterClass = Class.forName(className);
            Formatter formatter = (Formatter) formatterClass.newInstance();

            // TODO Add property setup of the formatter.
            if (formatter instanceof PatternFormatter) {
                String pattern = (String) properties.getProperty(PATTERN_LAYOUT_PREFIX_KEY,
                        "%r %c{1} [%P] %m %T");
                ((PatternFormatter) formatter).setPattern(pattern);
            }
            if (formatter != null) {
                Logger rootLogger = loggerRepository.getRootLogger();

                int numberOfAppenders = rootLogger.getNumberOfAppenders();
                for (int appenderNo = 0; appenderNo < numberOfAppenders; appenderNo++) {
                    Appender appender = rootLogger.getAppender(appenderNo);
                    appender.setFormatter(formatter);
                }
            }
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Failed to find Formatter class: " + e);
        } catch (InstantiationException e) {
            Log.e(TAG, "Failed to instantiate formtter: " + e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "No access to formatter class: " + e);
        } catch (ClassCastException e) {
            Log.e(TAG, "Specified formatter class does not implement the Formatter interface: " + e);
        }
    }

    /**
     * Convert a <code>String</code> containing a level to a <code>Level</code>
     * object.
     *
     * @return the level that corresponds to the levelString if it was a valid
     * <code>String</code>, <code>null</code> otherwise.
     */
    private Level stringToLevel(String levelString) {
        return Level.valueOf(levelString);
    }

}
