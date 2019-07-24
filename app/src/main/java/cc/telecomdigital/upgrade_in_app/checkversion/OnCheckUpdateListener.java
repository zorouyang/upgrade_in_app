package cc.telecomdigital.upgrade_in_app.checkversion;

import cc.telecomdigital.upgrade_in_app.checkversion.bean.UpgradeInfo;

public interface OnCheckUpdateListener {
    /**
     * 发现新版本
     *
     * @param upgradeInfo   新版Apk版本信息
     */
    void onFindNewVersion(UpgradeInfo upgradeInfo);

    /**
     * 当前版本已是最新版本
     */
    void onNewest();
}