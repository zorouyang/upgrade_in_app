package cc.telecomdigital.upgrade_in_app.downloadinapp;

public interface OnUpgradeListener {
    /**
     * 开始更新
     */
    void onUpgradeStart();

    /**
     * 更新进度变化
     * @param progressMax
     * @param progressValue 当前更新进度
     */
    void onProgress(int progressMax, int progressValue);

    /**
     * 新的Apk下载完成
     *
     * @param apkPath apk 全路径
     */
    void onDownloadFinished(String apkPath);

    /**
     * 更新已暫停
     */
    void onUpgradePaused();

    /**
     * 更新已取消
     */
    void onUpgradeCanceled();

    /**
     * 更新失败
     */
    void onUpgradeFailed();

    /**
     * 更新异常
     *
     * 主要是下载APK文件不完整或者APK包异常
     */
    void onUpgradeException(String error);
}
