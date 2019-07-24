package cc.telecomdigital.upgrade_in_app.downloader;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.Toast;

import cc.telecomdigital.upgrade_in_app.util.ApkInstaller;
import cc.telecomdigital.upgrade_in_app.R;
import cc.telecomdigital.upgrade_in_app.util.LogUtil;

public class DownloadCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.d("onReceive: " + intent.getAction());
        //判断是否下载完成的广播
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            //获取下载的文件id
            long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            LogUtil.d("download complete: " + downId);
            //自动安装apk
            installApk(context, downId);
        }
    }

    private void installApk(Context context,long downloadApkId) {
        // 获取存储ID
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        long downId =sp.getLong(DownloadManager.EXTRA_DOWNLOAD_ID,-1L);
        if(downloadApkId == downId){
            DownloadManager downManager= (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadFileUri = downManager.getUriForDownloadedFile(downloadApkId);
            LogUtil.d("downloadFileUri: " + downloadFileUri);
            if (downloadFileUri != null) {
                /*Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
                context.startActivity(intent);*/

                ApkInstaller.installApk(context, downloadFileUri);
            }else{
                Toast.makeText(context, R.string.download_failure, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
