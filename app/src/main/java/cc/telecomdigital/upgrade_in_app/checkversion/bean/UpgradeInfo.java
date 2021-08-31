package cc.telecomdigital.upgrade_in_app.checkversion.bean;

/*
{versionCode: 31, versionName: "3.2.2", downloadUrl: "http://dl.mango.cc/test/android/SUNMobile_v3.2.1_30_b254_20190531_P.apk",title: "有新版本", changelog: "新版本v3.2.2\n更多內容\n更穩定\n建議立即更新", forceUpgrade: true, updateButton: "立即更新", continueButton: "暫不更新"}
 */
public class UpgradeInfo {
    private int versionCode;
    private String versionName;
    private String title;
    private String changelog;
    private String downloadUrl;

    private boolean forceUpgrade;
    private boolean allowBackgroundDownload;

    private String updateButton;
    private String continueButton;

    @Override
    public String toString() {
        return "UpgradeInfo{" + "versionCode=" + versionCode + ", versionName='" + versionName + '\'' + ", title='" + title + '\'' + ", changelog='" + changelog + '\'' + ", downloadUrl='" + downloadUrl + '\'' + ", forceUpgrade=" + forceUpgrade + ", allowBackgroundDownload=" + allowBackgroundDownload + ", updateButton='" + updateButton + '\'' + ", continueButton='" + continueButton + '\'' + '}';
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getChangelog() {
        return changelog;
    }

    public void setChangelog(String changelog) {
        this.changelog = changelog;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public boolean isForceUpgrade() {
        return forceUpgrade;
    }

    public void setForceUpgrade(boolean forceUpgrade) {
        this.forceUpgrade = forceUpgrade;
    }

    public boolean isAllowBackgroundDownload() {
        return allowBackgroundDownload;
    }

    public void setAllowBackgroundDownload(boolean allowBackgroundDownload) {
        this.allowBackgroundDownload = allowBackgroundDownload;
    }

    public String getUpdateButton() {
        return updateButton;
    }

    public void setUpdateButton(String updateButton) {
        this.updateButton = updateButton;
    }

    public String getContinueButton() {
        return continueButton;
    }

    public void setContinueButton(String continueButton) {
        this.continueButton = continueButton;
    }
}
