/*
 * Copyright 2010 The Microlog project @sourceforge.net
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.lisicnu.log4android.appender;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.github.lisicnu.log4android.Level;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

/**
 * An appender to log to a file in on the SDCard.
 *
 * @author Johan Karlsson
 * @author Dan Walkes
 */
public class FileAppender extends AbstractAppender {
    private static final String TAG = FileAppender.class.getSimpleName();

    public static final String DEFAULT_FILENAME = "autoGen";
    public static final String DEFUALT_FILE_EXT = ".log";

    /**
     * In configuration file, we have defined : start with "/" means absolute
     * path. otherwise means related to mnt/sdcard/logs/
     */
    private String fileName = DEFAULT_FILENAME + DEFUALT_FILE_EXT;

    private PrintWriter writer;

    private File mLogFile = null;

    Context mContext = null;

    /**
     * e.g. [auto]<br/>
     * %a : append file or create new file. <br/>
     * %f : use date time format. i.e. "yyyyMMddHHmmss".<br/>
     * %p : use prefix and plus index to format.<br/>
     * %s : wrap file size. unit is M. without unit. <br/>
     * usage: %p, when use this parameter, key[File] will be recognized as
     * prefix.<br/>
     * # result: prefix0.log, prefix1.log. <br/>
     * TODO
     */
    class WrapFormatter {
        final static long DEFAULT_WRAP_SIZE = 0;
        /**
         * wrap file's size. in byte. default is 0, means not wrap.
         */
        long wrapSize = DEFAULT_WRAP_SIZE;

        /**
         * file name formatter, only usefull when {@link #nameFormat}'s value is
         * {@link #NAME_FORMAT_DATE}
         */
        String formatter = "";

        /**
         * wrap file or not.
         *
         * @return
         */
        public boolean isWrapFile() {
            return nameFormat != NAME_FORMAT_NONE;
        }

        /**
         * append file or create new.
         */
        boolean appendFile = false;

        final static int NAME_FORMAT_NONE = 0;
        final static int NAME_FORMAT_DATE = 1;
        final static int NAME_FORMAT_PLUSINDEX = 2;
        int nameFormat = NAME_FORMAT_NONE;

        final static char PARA_CHAR = '%';
        final static String SUPPORT_PARA_FORMAT_DATE = "%f"; // date format
        final static String SUPPORT_PARA_WRAP_SIZE = "%s"; // size in M.
        final static String SUPPORT_PARA_FORMAT_PLUSINDEX = "%p"; // auto add.
        final static String SUPPORT_PARA_FILE_APPEND = "%a"; // append file.

        void wrap(String wrapStr) {
            if (wrapStr == null || wrapStr.isEmpty())
                return;

            wrapSize = DEFAULT_WRAP_SIZE;
            nameFormat = NAME_FORMAT_NONE;

            int idx = wrapStr.indexOf(PARA_CHAR);
            int mLen = wrapStr.length();
            while (idx > -1 && idx < mLen) {
                int nextIdx = wrapStr.indexOf(PARA_CHAR, idx + 1);

                if (nextIdx == -1) {
                    nextIdx = mLen;
                }

                String tmp = wrapStr.substring(idx, nextIdx);
                if (tmp.startsWith(SUPPORT_PARA_FORMAT_DATE)) {
                    String tt = tmp.replace(SUPPORT_PARA_FORMAT_DATE, "").replace("-", "").trim();
                    if (tt.isEmpty()) {
                        formatter = "yyyyMMddHHmmss";
                    } else {
                        formatter = tt;
                    }
                    nameFormat = NAME_FORMAT_DATE;
                } else if (tmp.startsWith(SUPPORT_PARA_WRAP_SIZE)) {
                    // size
                    String tt = tmp.replace(SUPPORT_PARA_WRAP_SIZE, "").replace("-", "").trim();
                    if (!tt.isEmpty()) {
                        try {
                            wrapSize = (long) (Float.parseFloat(tt.replace("", "")) * 1024 * 1024);
                        } catch (Exception e) {
                        }
                    }
                } else if (tmp.startsWith(SUPPORT_PARA_FORMAT_PLUSINDEX)) {
                    nameFormat = NAME_FORMAT_PLUSINDEX;
                } else if (tmp.startsWith(SUPPORT_PARA_FILE_APPEND)) {
                    appendFile = true;
                }

                idx = nextIdx;
            }
        }
    }

    final WrapFormatter wraper = new WrapFormatter();

    /**
     * when wrap file, this name will be used.
     *
     * @param wrapFormat
     */
    public void setWrapFormat(String wrapFormat) {
        wraper.wrap(wrapFormat);
//        Log.v(TAG, wrapFormat);
    }

    /**
     * Create a file appender using the specified application context. Note:
     * your application must hold android.permission.WRITE_EXTERNAL_STORAGE to
     * be able to access the SDCard.
     *
     * @param c
     */
    public FileAppender(Context c) {
        mContext = c;
    }

    /**
     * Create a file appender without application context. The logging file will
     * be placed in the root folder and will not be removed when your
     * application is removed. Use FileAppender(Context) to create a log that is
     * automatically removed when your application is removed Note: your
     * application must hold android.permission.WRITE_EXTERNAL_STORAGE to be
     * able to access the SDCard.
     */
    public FileAppender() {
    }

    /**
     * @see com.github.lisicnu.log4android.appender.AbstractAppender#open()
     */
    @Override
    public synchronized void open() throws IOException {
        File logFile = getLogFile();

        Log.i(TAG, "log file:" + logFile.getAbsolutePath());
        logOpen = false;

        if (!logFile.exists()) {
            if (!logFile.createNewFile()) {
                Log.e(TAG, "Unable to create new log file");
            }
        }

        Log.v(TAG, mLogFile.getAbsolutePath());

        FileOutputStream fileOutputStream = new FileOutputStream(logFile, wraper.appendFile);

        writer = new PrintWriter(fileOutputStream);
        writer.println("\r\n##########################################################\r\n");
        logOpen = true;
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void close() throws IOException {

        Log.i(TAG, "Closing the FileAppender");

        if (writer != null) {
            writer.close();
        }
    }

    @Override
    public synchronized void doLog(String clientID, String name, long time, Level level,
                                   Object message, Throwable throwable) {
        if (logOpen && formatter != null && writer != null) {
            writer.println(formatter.format(clientID, name, time, level, message, throwable));
            writer.flush();

            if (throwable != null) {
                throwable.printStackTrace();
            }

            if (wraper != null && wraper.isWrapFile()
                    && wraper.wrapSize != WrapFormatter.DEFAULT_WRAP_SIZE
                    && mLogFile.length() > wraper.wrapSize) {
                try {
                    close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                try {
                    open();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else if (formatter == null) {
            Log.e(TAG, "Please set a formatter.");
        }

    }

    public long getLogSize() {
        return Appender.SIZE_UNDEFINED;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    /**
     * Set the filename to be used
     *
     * @param fileName the filename to log to
     */
    public void setFileName(String fileName) {
        // TODO Throw IllegalArgumentException if the filename is null.
        if (fileName != null) {
            this.fileName = fileName;
        }
    }

    /**
     * Android 1.6-2.1 used {@link android.os.Environment#getExternalStorageDirectory()} to
     * return the (root) external storage directory. Folders in this subdir were
     * shared by all applications and were not removed when the application was
     * deleted. Starting with andriod 2.2, Context.getExternalFilesDir() is
     * available. This is an external directory available to the application
     * which is removed when the application is removed.
     * <p/>
     * This implementation uses Context.getExternalFilesDir() if available, if
     * not available uses {@link android.os.Environment#getExternalStorageDirectory()}.
     *
     * @return a File object representing the external storage directory used by
     * this device or null if the subdir could not be created or proven
     * to exist
     */
    protected synchronized File getExternalStorageDirectory() {

        File externalStorageDirectory;
        if (Build.VERSION.SDK_INT >= 8 && mContext != null) {
            externalStorageDirectory = mContext.getExternalCacheDir();
        } else {
            externalStorageDirectory = Environment.getExternalStorageDirectory();
        }

        if (externalStorageDirectory != null) {
            if (!externalStorageDirectory.exists()) {
                if (!externalStorageDirectory.mkdirs()) {
                    externalStorageDirectory = null;
                    Log.e(TAG, "mkdirs failed on externalStorageDirectory "
                            + externalStorageDirectory);
                }
            }
        }
        return externalStorageDirectory;
    }

    /**
     * 非wrap 的情况下, 如果不是以 /mnt/sdcard/, mnt/sdcard/ 开头的直接定位到此
     *
     * @return
     */
    private File getStorageDir() {
        String externalStorageState = Environment.getExternalStorageState();
        if (!externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
            return null;
        }

        // 默认在外置程序包下面.logs 文件夹
        File file = getExternalStorageDirectory();

        if (fileName.startsWith("/")) {
            File tmp = new File(fileName);
            if (tmp.isDirectory())
                return tmp;

            return new File(file, getFileNameWithoutExtension(fileName));
        }

        String tmp = "mnt/sdcard/";
        if (fileName.startsWith(tmp)) {
            file = new File(file, fileName.replace(tmp, ""));
            return file;
        }

        file = new File(file, "logs");
        file.mkdirs();
        return file;
    }

    public static String getFileNameWithoutExtension(String fileName) {

        if (fileName == null)
            return null;
        if (fileName.isEmpty())
            return "";

        String result = fileName;
        int idx = fileName.lastIndexOf(File.separator);
        if (idx != -1) {
            result = result.substring(idx + 1);
        }
        idx = result.lastIndexOf(".");
        if (idx != -1) {
            result = result.substring(0, idx);
        }
        return result;
    }

    public static String getFileName(String fileName) {

        if (fileName == null)
            return null;
        if (fileName.isEmpty())
            return "";

        String result = fileName;
        int idx = fileName.lastIndexOf(File.separator);
        if (idx != -1) {
            result = result.substring(idx + 1);
        }
        return result;
    }

    /**
     * get in parameters date formatted result without file extension.
     *
     * @return
     */
    String getTimeFileName() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(wraper.formatter);
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    /**
     * if the file hasn't been settled, will auto set to external storage.
     *
     * @return the log file used to log to external storage
     */
    public synchronized File getLogFile() {

        File dir = getStorageDir();
        if (dir == null) {
            Log.e(TAG, "Unable to open log file from external storage");
            return null;
        }

        switch (wraper.nameFormat) {
            case WrapFormatter.NAME_FORMAT_DATE: {
                String file = getFormattedNameWithDate(dir);
                mLogFile = new File(dir, file);
            }
            break;
            case WrapFormatter.NAME_FORMAT_PLUSINDEX: {
                String file = getFormattedNameWithPlusIndex(dir);
                mLogFile = new File(dir, file);
            }
            break;
            case WrapFormatter.NAME_FORMAT_NONE: {
                mLogFile = new File(dir, getFileName(fileName).concat(DEFUALT_FILE_EXT));
            }
            break;
            default: {
                mLogFile = new File(dir, getFileName(fileName).concat(DEFUALT_FILE_EXT));
            }
            break;
        }

        try {
            mLogFile.getParentFile().mkdirs();
            mLogFile.createNewFile();
        } catch (IOException e) {
            Log.e(TAG, "创建文件失败了..." + e.toString() + " mLogFile.getParentFile()");
        }

        return mLogFile;
    }

    public static String getRandomStr(int length) {
        String str = "abcdefghigklmnopkrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        int mLen = str.length();
        StringBuffer sf = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(mLen);// 0~61
            sf.append(str.charAt(number));
        }
        return sf.toString();
    }

    private String getFormattedNameWithPlusIndex(File dir) {
        if (fileName == null || fileName.isEmpty()) {
            fileName = getRandomStr(8);
        }

        String prefix = fileName;
        int start = 0;
        File[] files = dir.listFiles();
        String file = "";

        while (true) {
            file = prefix + start + DEFUALT_FILE_EXT;
            ++start;

            for (File tmp : files) {
                if (tmp.getAbsolutePath().endsWith(file)) {
                    continue;
                }
            }

            break;
        }
        return file;
    }

    final static String FILE_APPEND_CHAR = "_";

    private String getFormattedNameWithDate(File dir) {

        String tmp = getTimeFileName();
        // less than zero means do not append index. otherwise plus index.
        int plusIndex = 0;
        if (wraper.wrapSize == WrapFormatter.DEFAULT_WRAP_SIZE) {
            plusIndex = -1;
        }

        File file;
        if (fileName == null || fileName.isEmpty()) {
            if (plusIndex < 0) {
                return tmp.concat(DEFUALT_FILE_EXT);
            }
            String t;

            do {
                t = tmp + FILE_APPEND_CHAR + (++plusIndex) + DEFUALT_FILE_EXT;
                file = new File(dir, t);
            } while (file.exists());

            return t;
        }

        tmp = getFileNameWithoutExtension(fileName) + tmp;
        if (plusIndex < 0) {
            String t;
            do {
                t = tmp + FILE_APPEND_CHAR + (++plusIndex) + DEFUALT_FILE_EXT;
                file = new File(dir, t);
            } while (file.exists());

            tmp = t;
        } else
            tmp = tmp.concat(DEFUALT_FILE_EXT);
        return tmp;
    }
}
