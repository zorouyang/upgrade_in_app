package cc.telecomdigital.upgrade_in_app.downloadinapp;

import android.os.Environment;

import androidx.annotation.NonNull;

import java.io.File;

public class DownloadDirectory {

    /**
     * 获取文件下载目录
     *
     * @return 文件下载目录
     */
    public static File getExternalStorageDirectory() {
        File appDir = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        return appDir;
    }

    /**
     * 获取下载文件目录路径
     *
     * @return 下载文件目录路径
     */
    @NonNull
    public static String getDownloadStorageDirectory() {
        return getExternalStorageDirectory().getPath();
    }


    public static File getExternalStorageDirectory(File root) {
        File appDir = new File(root, Environment.DIRECTORY_DOWNLOADS);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        return appDir;
    }
    public static String getDownloadStorageDirectory(File root) {
        return getExternalStorageDirectory(root).getPath();
    }


    /**
     * 删除指定文件
     *
     * @param filePath 文件path
     * @return 删除结果
     */
    public static boolean delFile(String filePath) {
        if (isEmpty(filePath))
            return false;

        File file = new File(filePath);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 删除指定文件夹下所有文件
     *
     * @param path 文件夹完整绝对路径
     * @return 删除结果
     */
    public static boolean delAllFile(String path) {
        if (isEmpty(path))
            return false;

        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 删除文件夹
     *
     * @param folderPath 文件夹完整绝对路径
     */
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); //删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断字符串是否有值，如果为null或者是空字符串或者只有空格或者为"null"字符串，则返回true，否则则返回false
     */
    public static boolean isEmpty(String value) {
        if (value != null && !"".equalsIgnoreCase(value.trim())
                && !"null".equalsIgnoreCase(value.trim())) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 获取下载文件url中的文件名
     *
     * @param url 下载文件的url
     * @return 下载文件url中的文件名
     * 和path组成完整的file path
     */
    @NonNull
    public static String getUrlFileName(String url, String appName) {
        if (url.endsWith(".apk"))
            return url.substring(url.lastIndexOf("/") + 1);
        return appName + ".apk";
    }
}
