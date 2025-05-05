package com.tm.logger;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import com.tm.logger.config.Level;
import com.tm.logger.config.LogConfiguration;
import com.tm.logger.config.LogConfigurator;
import com.tm.utils.Definitions;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.tinylog.Logger;
import org.tinylog.TaggedLogger;
/* loaded from: input.aar:classes.jar:com/tm/logger/Log.class */
public class Log {
    private static Log _instance = null;
    private static File _logFile = null;
    private static Context _context = null;
    static final boolean SHOULD_LOG = false;
    static final boolean SHOULD_LOG_TO_FILE = true;
    private static final String LOG_FILE_NAME = "Log_aa.txt";
    private static final String NETWORK_LOG_FILE_NAME = "BigNetworkLog_aa.txt";
    private static final String CMASSTORAGE_FILE_NAME = "TMCMASStorage_aa.txt";
    public static final int ASSERT = 7;
    public static final int DEBUG = 3;
    public static final int ERROR = 6;
    public static final int INFO = 4;
    public static final int VERBOSE = 2;
    public static final int WARN = 5;
    private static final int LOG_FILE_LEVEL = 2;
    private static final int LOG_LEVEL = 2;
    private static final int MAX_BYTE = 2048;
    private static final long MAX_LOG_FILE = 10485760;
    private static final long MAX_NETWORK_LOG_FILE = 5242880;
    private static TaggedLogger fileLog;
    private static TaggedLogger networkLog;

    private Log() {
    }

    public static Log getIntance() {
        return _instance;
    }

    public static Log createInstance(Context context) {
        if (_instance == null) {
            _instance = new Log();
            _context = context;
            configure();
            fileLog = Logger.tag("main");
            networkLog = Logger.tag("network");
        }
        return _instance;
    }

    private static void configure() {
        File fileDir = _context.getFilesDir();
        LogConfigurator configurator = new LogConfigurator();
        LogConfiguration logConfig = new LogConfiguration("main");
        logConfig.setFileName(new File(_context.getFilesDir(), LOG_FILE_NAME).getAbsolutePath());
        logConfig.setMaxFileSize(MAX_LOG_FILE);
        logConfig.setMaxFileCount(2);
        logConfig.setRootLevel(Level.Trace);
        logConfig.setUseLogcatAppender(false);
        logConfig.setUseFileAppender(true);
        logConfig.setResetConfiguration(true);
        configurator.deleteOldFile(fileDir, LOG_FILE_NAME);
        configurator.configure(logConfig);
        LogConfiguration logConfig2 = new LogConfiguration("network");
        logConfig2.setResetConfiguration(false);
        logConfig2.setFileName(new File(_context.getFilesDir(), NETWORK_LOG_FILE_NAME).getAbsolutePath());
        logConfig2.setMaxFileSize(MAX_NETWORK_LOG_FILE);
        logConfig2.setMaxFileCount(2);
        logConfig2.setRootLevel(Level.Trace);
        logConfig2.setUseLogcatAppender(false);
        logConfig2.setUseFileAppender(true);
        configurator.deleteOldFile(fileDir, NETWORK_LOG_FILE_NAME);
        configurator.configure(logConfig2);
    }

    private static void createLogFile() {
        File fileDir = _context.getFilesDir();
        _logFile = new File(fileDir, LOG_FILE_NAME);
        try {
            if (!_logFile.exists()) {
                _logFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void d(String tag, String msg) {
        fileLog.debug(String.format("%s - %s", tag, msg));
    }

    public static void d(String tag, String msg, Throwable tr) {
        fileLog.debug(String.format("%s - %s", tag, msg), new Object[]{tr});
    }

    public static void e(String tag, String msg) {
        fileLog.error(String.format("%s - %s", tag, msg));
    }

    public static void e(String tag, String msg, Throwable tr) {
        fileLog.error(String.format("%s - %s", tag, msg), new Object[]{tr});
    }

    public static void i(String tag, String msg) {
        try {
            fileLog.info(String.format("%s - %s", tag, msg));
        } catch (Exception e) {
            e.getCause().printStackTrace();
        }
    }

    public static void i(String tag, String msg, Throwable tr) {
        fileLog.info(String.format("%s - %s", tag, msg), new Object[]{tr});
    }

    public static void v(String tag, String msg) {
        fileLog.trace(String.format("%s - %s", tag, msg));
    }

    public static void bigNetworkTrace(String tag, String msg) {
        networkLog.trace(String.format("%s - %s", tag, msg));
    }

    public static void v(String tag, String msg, Throwable tr) {
        fileLog.trace(String.format("%s - %s", tag, msg), new Object[]{tr});
    }

    public static void w(String tag, String msg) {
        fileLog.warn(String.format("%s - %s", tag, msg));
    }

    public static void w(String tag, String msg, Throwable tr) {
        fileLog.warn(String.format("%s - %s", tag, msg), new Object[]{tr});
    }

    public static boolean isLoggable(String tag, int level) {
        return false;
    }

    public static int println(int priority, String tag, String msg) {
        return 0;
    }

    public static String getStackTraceString(Throwable tr) {
        return "";
    }

    public static String getLogFileAsString() {
        try {
            File fileDir = _context.getFilesDir();
            File logFile = new File(fileDir, LOG_FILE_NAME);
            FileInputStream fin = new FileInputStream(logFile);
            long len = logFile.length();
            byte[] fileContent = new byte[(int) (len > 2048 ? 2048L : len)];
            fin.read(fileContent);
            fin.close();
            String strFileContent = new String(fileContent);
            return strFileContent;
        } catch (FileNotFoundException e) {
            return "File not found";
        } catch (IOException e2) {
            return "IOException while reading the file";
        }
    }

    public static String getCMASStorageFileAsString() {
        try {
            File fileDir = _context.getFilesDir();
            File _CMASFile = new File(fileDir, CMASSTORAGE_FILE_NAME);
            FileInputStream fin = new FileInputStream(_CMASFile);
            byte[] fileContent = new byte[(int) _CMASFile.length()];
            fin.read(fileContent);
            String strFileContent = new String(fileContent);
            return strFileContent;
        } catch (FileNotFoundException e) {
            return "File not found";
        } catch (IOException e2) {
            return "IOException while reading the file";
        }
    }

    public static Map<String, File> getAttachmentsLog(String fileName, String username) {
        LogConfigurator configurator = new LogConfigurator();
        int currentapiVersion = Build.VERSION.SDK_INT;
        File fileDir = _context.getFilesDir();
        File zipFile = new File(fileDir, fileName == null ? "zip.zip" : fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(zipFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ZipOutputStream zos = new ZipOutputStream(fos);
        Map<String, File> attachments = new HashMap<>();
        FileWriter output = null;
        byte[] buf = new byte[512000];
        try {
            try {
                i("*****", "****************");
                i("*****", "****************");
                i("", "API version=" + Integer.toString(currentapiVersion));
                i("DEVICE", "Build.MANUFACTURER :" + Build.MANUFACTURER);
                i("DEVICE", "Build.MODEL :" + Build.MODEL);
                i("DEVICE", "Build.VERSION.SDK_INT :" + Build.VERSION.SDK_INT);
                i("DEVICE", "Build.VERSION.BASE_OS :" + Build.VERSION.BASE_OS);
                i("DEVICE", "Build.VERSION.CODENAME :" + Build.VERSION.CODENAME);
                i("DEVICE", "Build.VERSION.RELEASE :" + Build.VERSION.RELEASE);
                i("DEVICE", "Build.DEVICE :" + Build.DEVICE);
                i("*****", "****************");
                i("*****", "****************");
                String appVersionName = _context.getPackageManager().getPackageInfo(_context.getPackageName(), 0).versionName;
                i("", "App version=" + appVersionName + appVersionName);
                synchronized (LOG_FILE_NAME) {
                    i("", "##############CMAS data file###########");
                    i("", getCMASStorageFileAsString());
                }
                i("", "################# Preferences ################");
                i("", getPreferencesFileAsString());
                i("", "################# IMEI ###############");
                int index = 0;
                configurator.deleteOldFile(fileDir, LOG_FILE_NAME);
                List<File> files = configurator.findAllLogFiles(fileDir, LOG_FILE_NAME);
                for (File fileIn : files) {
                    try {
                        ZipEntry zipEntry = new ZipEntry("log_" + username + "_" + (System.currentTimeMillis() / 1000) + "(" + index + ").text");
                        index++;
                        zos.putNextEntry(zipEntry);
                        InputStream in = new FileInputStream(fileIn);
                        in.skip(getBufferStartingPoint(fileIn, MAX_LOG_FILE));
                        while (true) {
                            int len = in.read(buf);
                            if (len <= 0) {
                                break;
                            }
                            zos.write(buf, 0, len);
                        }
                        in.close();
                        zos.closeEntry();
                    } catch (FileNotFoundException e2) {
                    }
                }
                addLogcatCapture(fileDir, username, zos);
                configurator.deleteOldFile(fileDir, NETWORK_LOG_FILE_NAME);
                List<File> files2 = configurator.findAllLogFiles(fileDir, NETWORK_LOG_FILE_NAME);
                int index2 = 0;
                for (File fileIn2 : files2) {
                    try {
                        ZipEntry zipEntry2 = new ZipEntry("network(" + index2 + ").text");
                        index2++;
                        zos.putNextEntry(zipEntry2);
                        InputStream in2 = new FileInputStream(fileIn2);
                        in2.skip(getBufferStartingPoint(fileIn2, MAX_NETWORK_LOG_FILE));
                        while (true) {
                            int len2 = in2.read(buf);
                            if (len2 <= 0) {
                                break;
                            }
                            zos.write(buf, 0, len2);
                        }
                        in2.close();
                        zos.closeEntry();
                    } catch (FileNotFoundException e3) {
                    }
                }
                zos.close();
                attachments.put("zip", zipFile);
                if (0 != 0) {
                    try {
                        output.flush();
                        output.close();
                    } catch (IOException e4) {
                    }
                }
                try {
                    fos.close();
                } catch (IOException e5) {
                    e5.printStackTrace();
                }
                return attachments;
            } catch (Throwable th) {
                if (0 != 0) {
                    try {
                        output.flush();
                        output.close();
                    } catch (IOException e6) {
                    }
                }
                throw th;
            }
        } catch (Exception e7) {
            e7.printStackTrace();
            if (0 != 0) {
                try {
                    output.flush();
                    output.close();
                } catch (IOException e8) {
                }
            }
            return null;
        }
    }

    private static void addLogcatCapture(File fileDir, String userName, ZipOutputStream zos) {
        try {
            File outputFile = new File(fileDir, "logcat_" + userName + "_" + (System.currentTimeMillis() / 1000) + ".text");
            Process process = Runtime.getRuntime().exec("logcat -v time -f " + outputFile.getAbsolutePath());
            Thread.sleep(Definitions.ipRetryInterval);
            process.destroy();
            ZipEntry zipEntry = new ZipEntry("logcat_" + userName + "_" + (System.currentTimeMillis() / 1000) + ".text");
            zos.putNextEntry(zipEntry);
            InputStream in = new FileInputStream(outputFile);
            byte[] buf = new byte[512000];
            while (true) {
                int len = in.read(buf);
                if (len > 0) {
                    zos.write(buf, 0, len);
                } else {
                    in.close();
                    zos.closeEntry();
                    outputFile.delete();
                    return;
                }
            }
        } catch (IOException e) {
        } catch (InterruptedException e2) {
        }
    }

    private static long getBufferStartingPoint(File file, long maxSize) {
        if (!LOG_FILE_NAME.equals(file.getName()) && !NETWORK_LOG_FILE_NAME.equals(file.getName())) {
            return 0L;
        }
        long length = file.length();
        if (length <= maxSize) {
            return 0L;
        }
        return length - maxSize;
    }

    public static String getPreferencesFileAsString() {
        String path = null;
        try {
            path = _context.getApplicationContext().getPackageManager().getPackageInfo(_context.getPackageName(), 0).applicationInfo.dataDir;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (path == null) {
            return "";
        }
        File f = new File(path + "/shared_prefs/" + _context.getPackageName() + "_preferences.xml");
        String fileStr = getFileAsString(f);
        return fileStr.replaceFirst("(?s)<string name=\"signin_password_key[^>]*>.*?</string>", "****");
    }

    public static String getFileAsString(File f) {
        try {
            byte[] fileContent = new byte[(int) f.length()];
            FileInputStream fis = new FileInputStream(f);
            fis.read(fileContent);
            fis.close();
            return new String(fileContent);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e2) {
            e2.printStackTrace();
            return "";
        } catch (Exception e3) {
            e3.printStackTrace();
            return "";
        }
    }

    public static File getLogFile() {
        return _logFile;
    }

    public static void deleteOldFiles(Context context) {
        File fileDir = context.getFilesDir();
        for (int i = 2; i < 6; i++) {
            try {
                File file = new File(fileDir, "Log_aa.txt." + i);
                if (file.exists()) {
                    file.delete();
                }
            } catch (Exception e) {
            }
        }
    }

    static Context context() {
        return _context;
    }
}
