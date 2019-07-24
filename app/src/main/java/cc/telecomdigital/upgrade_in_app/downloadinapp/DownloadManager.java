package cc.telecomdigital.upgrade_in_app.downloadinapp;

import androidx.annotation.NonNull;

public class DownloadManager {
    private static volatile DownloadManager manager = null;

    private DownloadTask downloadTask;

    private String mFileName;

    private String mDownloadDirectory;

    private DownloadManager() {
        //If not init here, not start download before directory value is empty.
        mDownloadDirectory = DownloadDirectory.getDownloadStorageDirectory();
    }

    public static DownloadManager getInstance() {
        if (manager == null) {
            synchronized (DownloadManager.class) {
                if (manager == null) {
                    manager = new DownloadManager();
                }
            }
        }
        return manager;
    }

    /**
     * 开启下载任务
     *
     * @param url                下载链接
     * @param onDownloadListener onDownloadListener
     */
    public void startDownload(@NonNull String url, OnDownloadListener onDownloadListener) {
        startDownload(url, DownloadDirectory.getDownloadStorageDirectory(), DownloadDirectory.getUrlFileName(url, "app"), onDownloadListener);
    }

    /**
     * 开启下载任务
     *
     * @param url                下载链接
     * @param fileName           指定下载文件名
     * @param onDownloadListener onDownloadListener
     */
    public void startDownload(@NonNull String url, @NonNull String fileName, OnDownloadListener onDownloadListener) {
        startDownload(url, DownloadDirectory.getDownloadStorageDirectory(), fileName, onDownloadListener);
    }

    /**
     * 开启下载任务
     *
     * @param url                下载链接
     * @param fileParentPath     指定下载文件目录
     * @param fileName           指定下载文件名
     * @param onDownloadListener onDownloadListener
     */
    public void startDownload(@NonNull String url, @NonNull String fileParentPath, @NonNull String fileName, OnDownloadListener onDownloadListener) {

        mDownloadDirectory = fileParentPath;
        mFileName = fileName;

        if (downloadTask == null) {
            downloadTask = new DownloadTask(onDownloadListener);
            downloadTask.execute(url, fileParentPath, fileName);
            downloadTask.setOnDownloadTaskFinishedListener(new DownloadTask.OnDownloadTaskFinishedListener() {
                @Override
                public void onFinished() {
                    downloadTask = null;
                }

                @Override
                public void onCanceled() {
                    //下载任务取消，删除已下载的文件
                    clearCacheFile(getDownloadFilePath());
                }

                @Override
                public void onException(String error) {
                    //下载任务异常，删除已下载的文件
                    clearCacheFile(getDownloadFilePath());
                }
            });
        }
    }

    /**
     * 暂停下载任务
     */
    public void pauseDownload() {
        if (downloadTask != null) {
            downloadTask.pauseDownload();
        }
    }

    /**
     * 取消下载任务
     */
    public void cancelDownload() {
        if (downloadTask != null) {
            downloadTask.cancelDownload();
        }
    }

    /**
     * 清除下载的全部cache文件
     */
    public void clearAllCacheFile() {
        DownloadDirectory.delAllFile(mDownloadDirectory);
    }

    /**
     * 清除下载的cache文件
     *
     * @param filePath 要删除文件的绝对路径
     */
    public void clearCacheFile(String filePath) {
        DownloadDirectory.delFile(filePath);
    }

    /**
     * 获取下载文件的全路径
     *
     * @return 下载文件的全路径
     */
    public String getDownloadFilePath() {
        if (DownloadDirectory.isEmpty(mDownloadDirectory) || DownloadDirectory.isEmpty(mFileName)) return null;

        return mDownloadDirectory + "/" + mFileName;
    }
}
