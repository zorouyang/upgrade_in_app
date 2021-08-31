package cc.telecomdigital.upgrade_in_app.checkversion;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cc.telecomdigital.upgrade_in_app.AssetsJson;
import cc.telecomdigital.upgrade_in_app.R;
import cc.telecomdigital.upgrade_in_app.checkversion.bean.UpdateBean;
import cc.telecomdigital.upgrade_in_app.checkversion.bean.UpgradeInfo;
import cc.telecomdigital.upgrade_in_app.downloader.DownloadBackgroundManager;
import cc.telecomdigital.upgrade_in_app.downloadinapp.UpgradeManager;
import cc.telecomdigital.upgrade_in_app.util.LogUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CheckUpdateManager {

    private static OnCheckUpdateListener mOnCheckUpdateListener;

    public static void checkUpdate(final Context context, String url, final OnCheckUpdateListener onCheckUpdateListener) {
        mOnCheckUpdateListener = onCheckUpdateListener;

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);

        Request request = new Request.Builder()
                .url(url)
                .build();
        builder.build().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.d("onFailure IOException: " + e.getMessage() + " with " + call.request().url());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful())
                    return;

                String body = response.body().string();
                LogUtil.i("response.body: " + body);

                UpdateBean bean = new GsonBuilder().setLenient().create().fromJson(body, UpdateBean.class);
                if (bean == null || bean.getStatusCode() != 0)
                    return;

                UpgradeInfo info = bean.getUpgradeInfo();
                if (info.getVersionCode() > getVersionCode(context)) {
                    if (mOnCheckUpdateListener != null)
                        mOnCheckUpdateListener.onFindNewVersion(info);
                } else {
                    if (mOnCheckUpdateListener != null)
                        mOnCheckUpdateListener.onNewest();
                }
            }
        });
    }

    public static void checkUpdate(final Context context, final String url) {
        final Handler handler = new Handler();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);

        Request request = new Request.Builder()
                .url(url)
                .build();
        builder.build().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.d("IOException: " + e.getMessage() + " with " + call.request().url());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful())
                    return;

                String body = response.body().string();
                LogUtil.i("onResponse response.body: " + body);

                UpdateBean bean = new GsonBuilder().setLenient().create().fromJson(body, UpdateBean.class);
                if (bean == null || bean.getStatusCode() != 0)
                    return;

                UpgradeInfo info = bean.getUpgradeInfo();
                if (info.getVersionCode() > getVersionCode(context)) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            buildNewVersionDialog(context, info);
                        }
                    });
                }
            }
        });
    }

    public static void buildNewVersionDialog(final Context context, final UpgradeInfo upgradeInfo) {
        String positive = context.getString(R.string.update);
        if (!TextUtils.isEmpty(upgradeInfo.getUpdateButton())) {
            positive = upgradeInfo.getUpdateButton();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(upgradeInfo.getTitle())
                .setMessage(upgradeInfo.getChangelog())
                .setPositiveButton(positive, null);
        if (!upgradeInfo.isForceUpgrade()) {
            String negative = context.getString(R.string.cancel);
            if (!TextUtils.isEmpty(upgradeInfo.getContinueButton())) {
                negative = upgradeInfo.getContinueButton();
            }
            builder.setNegativeButton(negative, null);
        }

        AlertDialog dialog = builder.create();
        dialog.show();

        //防止自动关闭
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (upgradeInfo.isAllowBackgroundDownload() && !upgradeInfo.isForceUpgrade()) {
                    DownloadBackgroundManager.download(context, upgradeInfo.getDownloadUrl());
                    Toast.makeText(context, "Download in background...", Toast.LENGTH_SHORT).show();
                } else {
                    UpgradeManager.getInstance().upgrade(context, upgradeInfo.getDownloadUrl(), upgradeInfo.isForceUpgrade());
                }
            }
        });
    }

    public static void testCheck(Context context) {
        testCheck(context, true);
    }
    public static void testCheck(Context context, boolean force) {
        UpdateBean bean = AssetsJson.parseAsset(context, force ? "check_version.json" : "check_flexible_version.json");
        LogUtil.d("Sample: " + bean);

        if (bean == null || bean.getStatusCode() != 0)
            return;

        UpgradeInfo info = bean.getUpgradeInfo();
        if (info.getVersionCode() > getVersionCode(context)) {
            buildNewVersionDialog(context, info);
        }
    }

    public static int getVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            return 0;
        }
    }
}
