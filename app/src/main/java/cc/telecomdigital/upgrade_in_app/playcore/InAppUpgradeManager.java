package cc.telecomdigital.upgrade_in_app.playcore;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.Task;

import static com.google.android.play.core.install.model.AppUpdateType.FLEXIBLE;
import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;

public class InAppUpgradeManager {
    final private String TAG = "PlayCore";

    private Activity activity;
    private AppUpdateManager appUpdateManager;

    public InAppUpgradeManager(Activity activity) {
        this.activity = activity;
        this.appUpdateManager = AppUpdateManagerFactory.create(activity);
    }

    final static public int REQUEST_CODE_UPDATE = 9001;

    public void forceUpgrade() {
        Task<AppUpdateInfo> appUpdateInfo = appUpdateManager.getAppUpdateInfo();
        appUpdateInfo.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(Task task) {
                Log.i(TAG, "appUpdateInfo: isSuccessful=" + task.isSuccessful() + ", isComplete=" + task.isComplete());
                if (task.isSuccessful()) {
                    // 监听成功，不一定检测到更新
                    AppUpdateInfo it = (AppUpdateInfo)task.getResult();

                    Log.i(TAG, "appUpdateInfo: updateAvailability=" + it.updateAvailability() + ", IMMEDIATE: " + it.isUpdateTypeAllowed(IMMEDIATE) + ", FLEXIBLE: " + it.isUpdateTypeAllowed(FLEXIBLE));

                    if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                            && it.isUpdateTypeAllowed(IMMEDIATE)) { // 检测到更新可用且支持即时更新
                        try {
                            // 启动即时更新
                            boolean success = appUpdateManager.startUpdateFlowForResult(it, IMMEDIATE, activity, REQUEST_CODE_UPDATE);
                            Log.i(TAG, "appUpdateInfo: startUpdateFlowForResult=" + success);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    // 监听失败
                }
            }
        });
    }

    public void flexibleUpdate() {
        Task<AppUpdateInfo> appUpdateInfo = appUpdateManager.getAppUpdateInfo();
        appUpdateInfo.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(Task task) {
                if (task.isSuccessful()) {
                    // 监听成功，不一定检测到更新
                    AppUpdateInfo it = (AppUpdateInfo)task.getResult();
                    if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                            && it.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) { // 检测到更新可用且支持靈活更新

                        flexibleUpdateAndListener(it);
                    }
                } else {
                    // 监听失败
                }
            }
        });
    }

    InstallStateUpdatedListener listener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(InstallState state) {
            // Show module progress, log state, or install the update.
            popupSnackbarForCompleteUpdate();
        }
    };

    private void flexibleUpdateAndListener(AppUpdateInfo appUpdateInfo) {

        // Create a listener to track request state updates.
        /*InstallStateUpdatedListener listener = new InstallStateUpdatedListener() {
            @Override
            public void onStateUpdate(InstallState state) {
                // Show module progress, log state, or install the update.
                popupSnackbarForCompleteUpdate();
            }
        };*/

        // Before starting an update, register a listener for updates.
        appUpdateManager.registerListener(listener);

        // Start an update.
        try {
            // 启动靈活更新
            appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, activity, REQUEST_CODE_UPDATE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // When status updates are no longer needed, unregister the listener.
        //appUpdateManager.unregisterListener(this);
    }

    public void unregisterState() {
        // When status updates are no longer needed, unregister the listener.
        appUpdateManager.unregisterListener(listener);
    }


    /*@Override
    public void onStateUpdate(InstallState installState) {
        // Show module progress, log state, or install the update.
        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            // After the update is downloaded, show a notification
            // and request user confirmation to restart the app.
            popupSnackbarForCompleteUpdate();
        }
    }*/

    /* Displays the snackbar notification and call to action. */
    private void popupSnackbarForCompleteUpdate() {
        Snackbar snackbar =
                Snackbar.make(
                        activity.findViewById(layoutId),
                        "An update has just been downloaded.",
                        Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("RESTART", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appUpdateManager.completeUpdate();
            }
        });
        snackbar.setActionTextColor(Color.BLUE);
        snackbar.show();
    }

    private int layoutId;
    public void notifyStateToastWithFlexibleUpdate(int inLayoutId) {
        layoutId = inLayoutId;
    }

}
