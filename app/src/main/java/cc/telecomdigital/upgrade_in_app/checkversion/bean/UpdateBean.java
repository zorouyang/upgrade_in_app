package cc.telecomdigital.upgrade_in_app.checkversion.bean;

/*
{statusCode: 0, message: "",
upgradeInfo: {versionCode: 31, versionName: "3.2.2", downloadUrl: "http://dl.mango.cc/test/android/SUNMobile_v3.2.1_30_b254_20190531_P.apk",title: "有新版本", changelog: "新版本v3.2.2\n更多內容\n更穩定\n建議立即更新", forceUpgrade: true, updateButton: "立即更新", continueButton: "暫不更新"}}
 */
public class UpdateBean {
    private UpgradeInfo upgradeInfo;
    private String message;
    private int statusCode;

    @Override
    public String toString() {
        return "UpdateBean{" + "upgradeInfo=" + upgradeInfo + ", message='" + message + '\'' + ", statusCode=" + statusCode + '}';
    }

    public UpgradeInfo getUpgradeInfo() {
        return upgradeInfo;
    }

    public void setUpgradeInfo(UpgradeInfo upgradeInfo) {
        this.upgradeInfo = upgradeInfo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
