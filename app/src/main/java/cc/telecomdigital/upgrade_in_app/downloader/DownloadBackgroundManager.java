package cc.telecomdigital.upgrade_in_app.downloader;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;

import cc.telecomdigital.upgrade_in_app.util.ApkInstaller;
import cc.telecomdigital.upgrade_in_app.R;
import cc.telecomdigital.upgrade_in_app.util.LogUtil;

public class DownloadBackgroundManager {
    public static final String TAG = DownloadBackgroundManager.class.getSimpleName();

    public static void download(Context context, String url) {
        // 获取存储ID
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        long downloadId = sp.getLong(DownloadManager.EXTRA_DOWNLOAD_ID,-1L);
        LogUtil.d("downloadId: " + downloadId);

        if (downloadId != -1L) {
            FileDownloadManager fdm = FileDownloadManager.getInstance(context);
            int status = fdm.getDownloadStatus(downloadId);
            LogUtil.d("status: " + status);

            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                //启动更新界面
                Uri uri = fdm.getDownloadUri(downloadId);
                LogUtil.d("uri: " + uri);
                if (uri != null) {
                    if (compare(getApkInfo(context, uri.getPath()), context)) {
                        ApkInstaller.installApk(context, uri);
                        return;
                    } else {
                        LogUtil.d("remove: " + downloadId);
                        fdm.getDownloadManager().remove(downloadId);
                    }
                }
                startDownload(context, url);
            } else if (status == DownloadManager.STATUS_FAILED) {
                startDownload(context, url);
            } else {
                LogUtil.d("apk is already downloading");
            }
        } else {
            startDownload(context, url);
        }
    }

    private static void startDownload(Context context, String url) {
        String appName = context.getString(R.string.app_name);
        String title = context.getString(R.string.download) + appName;
        String description = context.getString(R.string.download_complete_action);

        long id = FileDownloadManager.getInstance(context).startDownload(url, appName, title, description);

        //IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        //intentFilter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        //context.registerReceiver(new DownloadCompleteReceiver(), intentFilter);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putLong(DownloadManager.EXTRA_DOWNLOAD_ID, id).commit();
        LogUtil.d("apk start download " + id);
    }

    /**
     * 获取apk程序信息[packageName,versionName...]
     *
     * @param context Context
     * @param path    apk path
     */
    private static PackageInfo getApkInfo(Context context, String path) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        LogUtil.d("has downloaded apk info: " + info);
        if (info != null) {
            LogUtil.d("has downloaded apk info: " + info.packageName + ", " + info.versionName);
            return info;
        }
        return null;
    }


    /**
     * 下载的apk和当前程序版本比较
     *
     * @param apkInfo apk file's packageInfo
     * @param context Context
     * @return 如果当前应用版本小于apk的版本则返回true
     */
    private static boolean compare(PackageInfo apkInfo, Context context) {
        if (apkInfo == null) {
            return false;
        }
        String localPackage = context.getPackageName();
        if (apkInfo.packageName.equals(localPackage)) {
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(localPackage, 0);
                LogUtil.d("compare apk: " + apkInfo.packageName + " VS " + packageInfo.versionName);
                LogUtil.d("compare apk: " + apkInfo.versionCode + " VS " + packageInfo.versionCode);
                if (apkInfo.versionCode > packageInfo.versionCode) {
                    return true;
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}