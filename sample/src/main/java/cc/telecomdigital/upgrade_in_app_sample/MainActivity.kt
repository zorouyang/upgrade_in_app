package cc.telecomdigital.upgrade_in_app_sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cc.telecomdigital.upgrade_in_app.checkversion.CheckUpdateManager
import cc.telecomdigital.upgrade_in_app.downloader.DownloadBackgroundManager
import cc.telecomdigital.upgrade_in_app.downloadinapp.UpgradeManager
import cc.telecomdigital.upgrade_in_app.playcore.InAppUpgradeManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CheckUpdateManager.testCheck(this, true)
//        CheckUpdateManager.testCheck(this, false)
        //CheckUpdateManager.checkUpdate(this, downloadUrl)

        //UpgradeManager.getInstance().upgrade()
        //DownloadBackgroundManager.download()
        //InAppUpgradeManager(activity).flexibleUpdate()
    }
}
