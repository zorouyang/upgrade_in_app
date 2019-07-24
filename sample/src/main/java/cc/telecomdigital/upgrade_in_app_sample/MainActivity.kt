package cc.telecomdigital.upgrade_in_app_sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cc.telecomdigital.upgrade_in_app.checkversion.CheckUpdateManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CheckUpdateManager.testCheck(this);
    }
}
