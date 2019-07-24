package cc.telecomdigital.upgrade_in_app.downloadinapp;

public interface OnDownloadListener {
    /**
     * 下载文件异常，不是完整的文件或者文件包异常
     */
    void onException(String error);

    /**
     * 下载进度变化 (progressValue/progressMax)/100%
     * @param progressMax
     * @param progressValue
     */
    void onProgress(int progressMax, int progressValue);

    /**
     * 下载成功
     */
    void onSuccess();

    /**
     * 下载失败
     */
    void onFailed();

    /**
     * 下载已暂停
     */
    void onPaused();

    /**
     * 下载已取消
     */
    void onCanceled();

}
