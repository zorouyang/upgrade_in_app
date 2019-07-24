package cc.telecomdigital.upgrade_in_app.downloader;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

public class FileDownloadManager {
    private DownloadManager downloadManager;
    private Context appContext;
    private static FileDownloadManager instance;

    private FileDownloadManager(Context context) {
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        appContext = context.getApplicationContext();
    }

    public static FileDownloadManager getInstance(Context context) {
        if (instance == null) {
            instance = new FileDownloadManager(context);
        }
        return instance;
    }

    /**
     *
     * @param downloadUrl
     * @param appName
     * @return download id
     */
    public long startDownload(String downloadUrl, String appName, String title, String description) {
        DownloadManager.Request req = new DownloadManager.Request(Uri.parse(downloadUrl));
        //设置用于下载时的网络类型，默认任何网络都可以，提供：NETWORK_BLUETOOTH、NETWORK_MOBILE、NETWORK_WIFI
        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI|DownloadManager.Request.NETWORK_MOBILE);
        //设置漫游状态下是否可以下载
        req.setAllowedOverRoaming(false);
        req.setVisibleInDownloadsUi(true);

       //VISIBILTY_HIDDEN: Notification:将不会显示，如果设置该属性的话，必须要添加权限 。 
                    //Android.permission.DOWNLOAD_WITHOUT_NOTIFICATION.
       //VISIBILITY_VISIBLE： Notification显示，但是只是在下载任务执行的过程中显示，下载完成自动消失。（默认值）
        // VISIBILITY_VISIBLE_NOTIFY_COMPLETED : Notification显示，下载进行时，和完成之后都会显示。
        //VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION ：只有当任务完成时，Notification才会显示。 
       req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //设置文件的保存的位置[三种方式]
        //第一种 file:///storage/emulated/0/Android/data/<your-package>/files/Download/update.apk
        //这个文件是应用专属，软件卸载后，下载的文件也将全部删除
        req.setDestinationInExternalFilesDir(appContext, Environment.DIRECTORY_DOWNLOADS, appName + ".apk");
        //第二种 file:///storage/emulated/0/Download/update.apk
        //req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, appName + ".apk");
        //第三种 自定义文件路径
        //req.setDestinationUri(Uri.parse("file:///storage/emulated/0/Download/update.apk"));

        // 设置一些基本显示信息
        req.setTitle(title);
        req.setDescription(description);
        //req.setMimeType("application/vnd.android.package-archive");
        //dm.openDownloadedFile()
        return downloadManager.enqueue(req);//异步
    }
    /**
     * 获取文件保存的路径
     *
     * @param downloadId an ID for the download, unique across the system.
     *                   This ID is used to make future calls related to this download.
     * @return file path
     * @see FileDownloadManager#getDownloadUri(long)
     */
    public String getDownloadPath(long downloadId) {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor c = downloadManager.query(query);
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    return c.getString(c.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI));
                }
            } finally {
                c.close();
            }
        }
        return null;
    }

    /**
     * 获取保存文件的地址
     *
     * @param downloadId an ID for the download, unique across the system.
     *                   This ID is used to make future calls related to this download.
     * @see FileDownloadManager#getDownloadPath(long)
     */
    public Uri getDownloadUri(long downloadId) {
        return downloadManager.getUriForDownloadedFile(downloadId);
    }

    public DownloadManager getDownloadManager() {
        return downloadManager;
    }

    /**
     * 获取下载状态
     *
     * @param downloadId an ID for the download, unique across the system.
     *                   This ID is used to make future calls related to this download.
     * @return int
     * @see DownloadManager#STATUS_PENDING
     * @see DownloadManager#STATUS_PAUSED
     * @see DownloadManager#STATUS_RUNNING
     * @see DownloadManager#STATUS_SUCCESSFUL
     * @see DownloadManager#STATUS_FAILED
     */
    public int getDownloadStatus(long downloadId) {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor c = downloadManager.query(query);
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    return c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
                }
            } finally {
                c.close();
            }
        }
        return -1;
    }
}