package cc.telecomdigital.upgrade_in_app.downloadinapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

import cc.telecomdigital.upgrade_in_app.util.ApkInstaller;
import cc.telecomdigital.upgrade_in_app.R;
import cc.telecomdigital.upgrade_in_app.util.LogUtil;

public class UpgradeManager {
    private static volatile UpgradeManager manager = null;

    private static final int MSG_ON_START = 1;
    private static final int MSG_ON_DOWNLOAD_PROGRESS = 2;
    private static final int MSG_ON_DOWNLOAD_FINISH = 3;
    private static final int MSG_ON_DOWNLOAD_PAUSE = 4;
    private static final int MSG_ON_DOWNLOAD_CANCEL = 5;
    private static final int MSG_ON_DOWNLOAD_FAILED = 6;
    private static final int MSG_ON_DOWNLOAD_EXCEPTION = 9;

    private OnUpgradeListener mOnUpgradeListener;

    private DownloadManager mDownloadManager;

    private UpgradeManager() {
        mDownloadManager = DownloadManager.getInstance();
    }

    /**
     * 获取updateManager实例
     *
     * @return updateManager实例
     */
    public static UpgradeManager getInstance() {
        if (manager == null) {
            synchronized (UpgradeManager.class) {
                if (manager == null) {
                    manager = new UpgradeManager();
                }
            }
        }
        return manager;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_ON_START:
                    if (mOnUpgradeListener != null) mOnUpgradeListener.onUpgradeStart();
                    break;
                case MSG_ON_DOWNLOAD_PROGRESS:
                    if (mOnUpgradeListener != null) {
                        DownloadProgress progress = (DownloadProgress) msg.obj;
                        mOnUpgradeListener.onProgress(progress.progressMax, progress.progressValue);
                    }
                    break;
                case MSG_ON_DOWNLOAD_FINISH:
                    if (mOnUpgradeListener != null)
                        mOnUpgradeListener.onDownloadFinished((String) msg.obj);
                    // TODO did not ctxWeakRef
                    // ApkInstaller.installApk(null, (String) msg.obj);
                    break;
                case MSG_ON_DOWNLOAD_PAUSE:
                    if (mOnUpgradeListener != null) mOnUpgradeListener.onUpgradePaused();
                    break;
                case MSG_ON_DOWNLOAD_CANCEL:
                    if (mOnUpgradeListener != null) mOnUpgradeListener.onUpgradeCanceled();
                    break;
                case MSG_ON_DOWNLOAD_FAILED:
                    if (mOnUpgradeListener != null) mOnUpgradeListener.onUpgradeFailed();
                    break;
                case MSG_ON_DOWNLOAD_EXCEPTION:
                    if (mOnUpgradeListener != null) mOnUpgradeListener.onUpgradeException((String) msg.obj);
                    break;
            }
        }
    };

    private ProgressDialog mProgressDialog;
    private void createDialog(final Context context, @DrawableRes int ic_launcher) {
        if (mProgressDialog == null && context != null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setTitle(context.getString(R.string.upgrade));
            mProgressDialog.setMessage(context.getString(R.string.downloading));
            //mProgressDialog.setMax(100);
            if (ic_launcher != 0 && ic_launcher != -1)
                mProgressDialog.setIcon(ic_launcher);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            /*mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancelUpgrade();
                }
            });
            mProgressDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.pause), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    pauseUpgrade();
                }
            });*/
        }
    }

    private WeakReference<Context> ctxWeakRef;
    public void upgrade(final Context context, @NonNull final String apkUrl) {
        upgrade(context, apkUrl, null, 0);
    }
    public void upgrade(final Context context, @NonNull final String apkUrl, String versionName, int versionCode) {

        createDialog(context, R.mipmap.ic_launcher);
        ctxWeakRef = new WeakReference<>(context);

        startUpgrade(apkUrl, context.getString(R.string.app_name), versionName, versionCode, new OnUpgradeListener() {
            @Override
            public void onUpgradeStart() {
                showDialog();
            }

            @Override
            public void onProgress(int progressMax, int progress) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.setMax(progressMax);
                    mProgressDialog.setProgress(progress);
                }
            }

            @Override
            public void onDownloadFinished(String apkPath) {
                dismissDialog();
                Toast.makeText(context, "onDownloadFinished: " + apkPath, Toast.LENGTH_SHORT).show();
                
                ApkInstaller.installApk(context, apkPath);
            }

            @Override
            public void onUpgradePaused() {
                dismissDialog();
                Toast.makeText(context, "onUpgradePaused", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUpgradeCanceled() {
                dismissDialog();
                Toast.makeText(context, "onUpgradeCanceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUpgradeFailed() {
                dismissDialog();
                Toast.makeText(context, "onUpgradeFailed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUpgradeException(String error) {
                dismissDialog();
                Toast.makeText(context, "onUpgradeException: " + error, Toast.LENGTH_SHORT).show();
            }

            private void showDialog() {
                if (mProgressDialog != null && !mProgressDialog.isShowing())
                    mProgressDialog.show();
            }

            private void dismissDialog() {
                if (mProgressDialog != null && mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
            }
        });
    }

    /**
     * 开始更新App
     * <p>
     * 此时开始正式下载更新Apk
     *
     * @param apkUrl           服务端最新apk文件url
     * @param onUpgradeListener onUpgradeListener
     */
    public void startUpgrade(@NonNull String apkUrl, String appName, OnUpgradeListener onUpgradeListener) {
        startUpgrade(apkUrl, appName, null, 0, onUpgradeListener);
    }

    public void startUpgrade(@NonNull String apkUrl, String appName, String newestVersionName, int newestVersionCode, OnUpgradeListener onUpgradeListener) {
        mOnUpgradeListener = onUpgradeListener;

        downloadNewestApkFile(apkUrl, appName, newestVersionName, newestVersionCode);
    }

    /**
     * 下载最新版本的APK文件
     *
     * @param url               服务端最新apk文件url
     * @param newestVersionName 最新版本APK版本名称
     * @param newestVersionCode 最新版本APK版本号
     */
    private void downloadNewestApkFile(String url, String appName, String newestVersionName, int newestVersionCode) {

        String apkFileName = DownloadDirectory.getUrlFileName(url, appName);
        if (newestVersionName != null && newestVersionCode > 0)
            apkFileName = getApkNameWithVersion(apkFileName, newestVersionName, newestVersionCode);

        sendMessage(MSG_ON_START, null);

        mDownloadManager.startDownload(url, ctxWeakRef.get().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), apkFileName, new OnDownloadListener() {
            @Override
            public void onException(String error) {
                sendMessage(MSG_ON_DOWNLOAD_EXCEPTION, error);
            }

            @Override
            public void onProgress(int progressMax, int progressValue) {
                sendMessage(MSG_ON_DOWNLOAD_PROGRESS, new DownloadProgress(progressMax, progressValue));
            }

            @Override
            public void onSuccess() {
                sendMessage(MSG_ON_DOWNLOAD_FINISH, mDownloadManager.getDownloadFilePath());
            }

            @Override
            public void onFailed() {
                sendMessage(MSG_ON_DOWNLOAD_FAILED, null);
            }

            @Override
            public void onPaused() {
                //取消升级时，调用download pause，保留已下载的部分apk文件
                sendMessage(MSG_ON_DOWNLOAD_PAUSE, null);
            }

            @Override
            public void onCanceled() {
                //为了保证断点续传，升级时，调用download pause，不使用cancel，onCancel不会被调用
                sendMessage(MSG_ON_DOWNLOAD_CANCEL, null);
            }
        });
    }

    private void sendMessage(int msgWhat, Object o) {
        Message msg = Message.obtain();
        msg.what = msgWhat;
        msg.obj = o;
        mHandler.sendMessage(msg);
    }

    /**
     * 获取带版本名称的apk文件名
     *
     * @param apkName apk原名
     * @return 带版本名称的apk文件名
     */
    private String getApkNameWithVersion(String apkName, String versionName, int versionCode) {
        if (DownloadDirectory.isEmpty(apkName))
            return apkName;

        apkName = apkName.substring(apkName.lastIndexOf("/") + 1, apkName.indexOf("" + ".apk"));
        apkName = apkName + "_v" + versionName + "_" + versionCode + ".apk";
        LogUtil.d("newApkName: " + apkName);
        return apkName;
    }

    /**
     * 暫停更新(區別與取消，將保留已經下載的數據)
     */
    public void pauseUpgrade() {
        //保留下载已完成的部分apk cache文件
        mDownloadManager.pauseDownload();
    }

    /**
     * 取消更新(區別與暫停，將刪除已經下載的數據)
     */
    public void cancelUpgrade() {
        //刪除下载已完成的部分apk cache文件
        mDownloadManager.cancelDownload();
    }

    /**
     * 清除所有已下载的APK缓存
     */
    public void clearCacheApkFile() {
        mDownloadManager.clearAllCacheFile();
    }

    static public class DownloadProgress {
        public int progressMax;
        public int progressValue;

        public DownloadProgress(int progressMax, int progressValue) {
            this.progressMax = progressMax;
            this.progressValue = progressValue;
        }
    }
}
