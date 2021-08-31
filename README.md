# Gradle with jitpack [![](https://jitpack.io/v/zorouyang/upgrade_in_app.svg)](https://jitpack.io/#zorouyang/upgrade_in_app)

# Features
- Check versoin with url response
- Force upgrade in app
- Flexible upgrade with DownloadManager API
- Force and flexible upgrade with Play core API for Android 5.0 or later

# Screenshot
<table>
   <tr>
    <td width="30%">
      <h3>Force upgrade</h3>
      (Upgrade in app)
    </td>
    <td width="30%">
      <h3>Flexible/background upgrade</h3>
      (DownloadManager API)
    </td>
    <td width="30%">
      <h3>Force and flexible upgrade with play store</h3>
      (Play core API) Android 5.0 above
    </td>
  </tr>
  <tr>
    <td width="30%">
      <img src="Screenshot/check.png" width="100%" /><br/>
      <img src="Screenshot/download.png" width="100%" /><br/>
    </td>
    <td width="30%">
      <img src="Screenshot/check_d.png" width="100%" /><br/>
      <img src="Screenshot/download_d.png" width="100%" /><br/>
    </td>
    <td width="30%">
      please see:<br/>
	https://developer.android.google.cn/guide/app-bundle/in-app-updates
    </td>
  </tr>
</table>

# How to Usage:

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.zorouyang:upgrade_in_app:0.1'
	}

# Code
Check versoin with url response

	CheckUpdateManager.checkUpdate(context, url)
	Response: 
	{
	  "statusCode": 0,
	  "message": "",
	  "upgradeInfo": {
	    "versionCode": 31,
	    "versionName": "3.2.2",
	    "downloadUrl": "https://domain/url/",
	    "title": "有新版本",
	    "changelog": "新版本v3.2.2\n更多內容\n更穩定\n建議立即更新",
	    "forceUpgrade": true,//true/false
	    "updateButton": "立即更新",
	    "continueButton": "暫不更新"
	  }
	}

Force upgrade

	UpgradeManager.getInstance().upgrade(context, downloadUrl)
	
Background upgrade with DownloadManager API

	DownloadBackgroundManager.download(context, downloadUrl)
	
Play store with Play core API on android 5.0 above https://developer.android.com/guide/app-bundle/in-app-updates

	new InAppUpgradeManager(activity).forceUpgrade()
	new InAppUpgradeManager(activity).flexibleUpdate()

