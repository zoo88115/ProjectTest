package com.example.zoo88115.projecttest;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zoo88115 on 2015/6/4 0004.
 */
public class FileUtil {
    public static final String APP_DIR = "androidtest";//使用到的目錄夾 專門處理這個程式相關的檔案
    public static boolean isExternalStorageWritable() {
        // 取得目前外部儲存設備的狀態
        String state = Environment.getExternalStorageState();
        // 判斷是否可寫入
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;

    }
    public static boolean isExternalStorageReadable() {
        // 取得目前外部儲存設備的狀態
        String state = Environment.getExternalStorageState();
        // 判斷是否可讀取
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;

    }
    public static File getPublicAlbumStorageDir(String albumName) {
        // 取得應用程式專用的照片路徑
        File pictures = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);     // 準備在照片路徑下建立一個指定的路徑
        File file = new File(pictures, albumName);
        if  (!file.mkdirs()) {    // 如果建立路徑不成功
            Log.e("getAlbumStorageDir","Directory not created");
        }
        return file;

    }
    public static File getAlbumStorageDir(Context context, String albumName) {
        // 取得應用程式專用內的照片路徑
        File pictures = context. getExternalFilesDir(
                Environment.DIRECTORY_PICTURES);     // 準備在照片路徑下建立一個指定的路徑
        File file = new File(pictures, albumName);
        if  (!file.mkdirs()) {    // 如果建立路徑不成功
            Log.e("getAlbumStorageDir", "Directory not created");
        }
        return file;
    }

    public static File getExternalStorageDir(String dir) {
        File result = new File(Environment.getExternalStorageDirectory(), dir);
        if (!isExternalStorageWritable()) {
            return null;
        }
        if (!result.exists() && !result.mkdirs()) {
            return null;
        }
        return result;
    }
    public static String getUniqueFileName() {
        // 使用年月日_時分秒格式為檔案名稱
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return sdf.format(new Date());
    }
}
