package cc.telecomdigital.upgrade_in_app.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import java.io.File;

public class ApkInstaller {

    public static void installApk(Context pContext, String path) {
        if (path == null)
            return;
        LogUtil.d("path: " + path);
        installApk(pContext, new File(path));
    }

    public static void installApk(Context pContext, File pFile) {
        if (pContext == null) return;
        if (null == pFile) return;
        if (!pFile.exists()) return;

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri = FileProvider.getUriForFile(pContext, pContext.getPackageName() + ".fileProvider", pFile);
        } else {
            uri = Uri.fromFile(pFile);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        pContext.startActivity(intent);
    }

    public static void installApk(Context context, Uri uri) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}
