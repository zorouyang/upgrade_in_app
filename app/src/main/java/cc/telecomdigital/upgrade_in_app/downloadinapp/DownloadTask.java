package cc.telecomdigital.upgrade_in_app.downloadinapp;

import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import cc.telecomdigital.upgrade_in_app.util.LogUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadTask extends AsyncTask<String, Integer, DownloadTask.ResponseState> {

    private static final int TYPE_SUCCESS = 0;
    private static final int TYPE_FAILED = 1;
    private static final int TYPE_PAUSED = 2;
    private static final int TYPE_CANCELED = 3;
    private static final int TYPE_EXCEPTION = 4;
    private static final int TYPE_HAS_EXISTS = 5;

    static public class ResponseState {
        public int state;
        public String msg;

        public ResponseState(int state) {
            this.state = state;
        }

        public ResponseState(int state, String msg) {
            this.state = state;
            this.msg = msg;
        }
    }


    private OnDownloadListener mOnDownloadListener;
    private OnDownloadTaskFinishedListener mOnDownloadTaskFinishedListener;

    private boolean isCanceled = false;

    private boolean isPaused = false;

    private int lastProgress;

    private File mDownloadFile = null;

    private long mContentLength; // 记录url下载文件的长度

    public DownloadTask(OnDownloadListener onDownloadListener) {
        mOnDownloadListener = onDownloadListener;
    }

    public void setOnDownloadTaskFinishedListener(OnDownloadTaskFinishedListener
                                                         onDownloadTaskFinishedListener) {
        mOnDownloadTaskFinishedListener = onDownloadTaskFinishedListener;
    }

    @Override
    protected ResponseState doInBackground(String... params) {
        InputStream is = null;
        RandomAccessFile savedFile = null;
        try {

            long downloadLength = 0; // 记录已下载的文件长度
            String downloadUrl = params[0];
            String fileDirPath = params[1];
            String fileName = params[2];

            mDownloadFile = new File(fileDirPath, fileName);
            if (mDownloadFile.exists()) {
                downloadLength = mDownloadFile.length();
            }
            mContentLength = getContentLength(downloadUrl);
            LogUtil.d("content length: " + mContentLength + ", has download length: " + downloadLength);

            if (mContentLength == 0) {
                return new ResponseState(TYPE_FAILED);
            } else if (mContentLength == downloadLength) {
                // 已下载字节和文件总字节相等，说明已经下载完成了
                return new ResponseState(TYPE_HAS_EXISTS);
            }
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    // 断点下载，指定从哪个字节开始下载
                    .addHeader("RANGE", "bytes=" + downloadLength + "-")
                    .url(downloadUrl)
                    .build();
            Response response = client.newCall(request).execute();
            if (response != null) {
                is = response.body().byteStream();
                savedFile = new RandomAccessFile(mDownloadFile, "rw");
                savedFile.seek(downloadLength); // 跳过已下载的字节
                byte[] b = new byte[1024];
                int total = 0;
                int len;
                while ((len = is.read(b)) != -1) {
                    if (isCanceled) {
                        return new ResponseState(TYPE_CANCELED);
                    } else if (isPaused) {
                        LogUtil.d("content length: " + mContentLength + ", has download length: " + (total + downloadLength));
                        return new ResponseState(TYPE_PAUSED);
                    } else {
                        total += len;
                        savedFile.write(b, 0, len);
                        // 计算已下载的百分比
                        //int progress = (int) ((total + downloadLength) * 100 / mContentLength);
                        if (mContentLength > Integer.MAX_VALUE)
                            publishProgress((int)mContentLength/1024, (int)(total + downloadLength)/1024);
                        else
                            publishProgress((int)mContentLength, (int)(total + downloadLength));
                    }
                }
                response.body().close();
                return new ResponseState(TYPE_SUCCESS);
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (is != null) {
                    is.close();
                }
                if (savedFile != null) {
                    savedFile.close();
                }
                if (isCanceled && mDownloadFile != null) {
                    mDownloadFile.delete();
                }
            } catch (Exception ex) {
            }
            return new ResponseState(TYPE_EXCEPTION, e.getMessage());
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (savedFile != null) {
                    savedFile.close();
                }
                if (isCanceled && mDownloadFile != null) {
                    mDownloadFile.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ResponseState(TYPE_FAILED);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int progressMax = values[0];
        int progressValue = values[1];
        if (progressValue > lastProgress) {
            mOnDownloadListener.onProgress(progressMax, progressValue);
            lastProgress = progressValue;
        }
    }

    @Override
    protected void onPostExecute(ResponseState status) {
        switch (status.state) {
            case TYPE_HAS_EXISTS:
            case TYPE_SUCCESS:
                if (mContentLength != mDownloadFile.length()) {
                    if (mOnDownloadListener != null)
                        mOnDownloadListener.onException("文件下載錯誤");

                    //下载数据异常，告知 DownloadManager 下载任务已失败
                    if (mOnDownloadTaskFinishedListener != null)
                        mOnDownloadTaskFinishedListener.onException("文件下載錯誤");
                } else {
                    if (mOnDownloadListener != null)
                        mOnDownloadListener.onSuccess();
                }
                break;
            case TYPE_FAILED:
                if (mOnDownloadListener != null)
                    mOnDownloadListener.onFailed();
                break;
            case TYPE_EXCEPTION:
                if (mOnDownloadListener != null)
                    mOnDownloadListener.onException(status.msg);
                //下载数据异常，告知 DownloadManager 下载任务已失败
                if (mOnDownloadTaskFinishedListener != null)
                    mOnDownloadTaskFinishedListener.onException(status.msg);
                break;
            case TYPE_PAUSED:
                if (mOnDownloadListener != null)
                    mOnDownloadListener.onPaused();
                break;
            case TYPE_CANCELED:
                if (mOnDownloadListener != null)
                    mOnDownloadListener.onCanceled();

                if (mOnDownloadTaskFinishedListener != null)
                    mOnDownloadTaskFinishedListener.onCanceled();
            default:
                break;
        }

        if (mOnDownloadTaskFinishedListener != null)
            mOnDownloadTaskFinishedListener.onFinished();
    }

    /**
     * 暂停下载任务
     */
    public void pauseDownload() {
        isPaused = true;
    }

    /**
     * 取消下载任务
     */
    public void cancelDownload() {
        isCanceled = true;
    }

    /**
     * 获取下载文件长度
     * @param downloadUrl 下载文件url
     * @return 下载文件长度
     * @throws IOException IOException
     */
    private long getContentLength(String downloadUrl) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(downloadUrl)
                .build();
        Response response = client.newCall(request).execute();

        if (response != null && response.isSuccessful()) {
            long contentLength = response.body().contentLength();
            response.close();
            return contentLength;
        }
        return 0;
    }

    public interface OnDownloadTaskFinishedListener {
        /**
         * 下载任务已结束
         */
        void onFinished();

        /**
         * 下载任务已取消
         */
        void onCanceled();

        /**
         * 下载文件异常，不是完整的文件或者文件包异常
         */
        void onException(String error);
    }
}