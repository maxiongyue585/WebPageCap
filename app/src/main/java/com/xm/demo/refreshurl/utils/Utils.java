package com.xm.demo.refreshurl.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    public static String exec(String cmd, boolean r) {

        if (TextUtils.isEmpty(cmd))
            return null;

        Process process = null;
        DataOutputStream os = null;
        BufferedReader res = null;
        StringBuffer out = new StringBuffer();

        try {
            process = Runtime.getRuntime().exec(r ? "su" : "sh");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd);
            os.writeBytes("\n");
            os.writeBytes("exit\n");
            os.flush();

            process.waitFor();

            res = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = res.readLine()) != null) {
                out.append(line);
                out.append("\n");
            }

            if (out.length() > 0)
                out.deleteCharAt(out.length() - 1);

            res.close();
            os.close();

            if (process != null) {
                process.destroy();
            }
        } catch (Exception e) {

            Log.e(TAG, "exec: ", e);
        }

        return out.toString();
    }

    /**
     * 配置webview
     *
     * @param webView
     */
    public static void setDefaultWebSettings(WebView webView) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        WebSettings settings = webView.getSettings();

        settings.setUseWideViewPort(true);
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setDatabaseEnabled(true);
        settings.setGeolocationEnabled(false);
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAppCacheEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportZoom(true);
        webView.requestFocus();
        webView.setLongClickable(true);
        webView.setScrollbarFadingEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setDrawingCacheEnabled(true);

        webView.setHorizontalScrollBarEnabled(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

    /**
     * 创建文件夹
     */
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.e(TAG, "makeRootDirectory: " + e.getMessage());
        }

    }

    /**
     * 多级创建文件夹
     *
     * @param folder
     */
    public static void crDirectory(String... folder) {

        int length = folder.length;
        String genFolder = Environment.getExternalStorageDirectory().getPath() + File.separator;
        String str = genFolder;
        File file;
        for (int i = 0; i < length; i++) {
            str = str + folder[i] + File.separator;
            file = new File(str);
            if (!file.exists()) {
                file.mkdir();
            }
        }

    }

    /**
     * 获取当前日期和时间
     *
     * @return
     */
    public static String getCurrentDateTime() {

        SimpleDateFormat format = new SimpleDateFormat("yy_MM_dd");
        return format.format(System.currentTimeMillis());
    }

    /**
     * 时间戳long转成日期格式
     *
     * @param timestamp
     * @return
     */
    public static String formatToNowTime(Long timestamp) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");
        return format.format(new Date(timestamp));
    }

    /**
     * 获取url列表
     *
     * @return
     */
    public static ArrayList<String> getUrls() {
        ArrayList<String> mDatas = new ArrayList<>(3);
//        mDatas.add("http://my.tv.sohu.com/us/332264593/102091846.shtml");
//        mDatas.add("https://tv.sohu.com/20180329/n600460267.shtml");
//        mDatas.add("http://v.youku.com/v_show/id_XMzQ5NjYyNTE5Ng==.html");
        mDatas.add("http://m.iqiyi.com/a_19rrhd232d.html");
        return mDatas;
    }

    /**
     * 获取目录下所有文件(目录按时间排序)
     *
     * @param list
     * @return
     */
    public static List<File> listFileSortByModifyTime(List<File> list) {
        if (list != null && list.size() > 0) {
            Collections.sort(list, new Comparator<File>() {
                public int compare(File file, File newFile) {
                    if (file.lastModified() < newFile.lastModified()) {
                        return -1;
                    } else if (file.lastModified() == newFile.lastModified()) {
                        return 0;
                    } else {
                        return 1;
                    }
                }
            });
        }
        return list;
    }

    /**
     * 获取目录下所有目录
     *
     * @param realFile
     * @return
     */
    public static List<File> getDirs(File realFile) {
        List<File> files = new ArrayList<>();
        if (realFile.isDirectory()) {
            File[] subfiles = realFile.listFiles();
            for (File file : subfiles) {
                if (file.isDirectory()) {
                    files.add(file);
                }
            }
        }
        return files;
    }

    /**
     * 判断字符串是否是数字
     */
    public static boolean isNumber(String value) {
        return isInteger(value) || isDouble(value);
    }

    /**
     * 判断字符串是否是整数
     */
    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 判断字符串是否是浮点数
     */
    public static boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            if (value.contains("."))
                return true;
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 方法描述：判断某一Service是否正在运行
     *
     * @param context
     * @param serviceName Service的全路径： 包名 + service的类名
     * @return true 表示正在运行，false 表示没有运行
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = am.getRunningServices(200);
        if (runningServiceInfos.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo serviceInfo : runningServiceInfos) {
            if (serviceInfo.service.getClassName().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }
}
