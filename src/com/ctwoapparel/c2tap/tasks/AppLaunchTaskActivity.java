package com.ctwoapparel.c2tap.tasks;

import android.content.Intent;
import android.content.pm.PackageManager;

/**
 * Activity to launch an app that the user specified
 * on the tag once the tag is tapped.
 */
public class AppLaunchTaskActivity extends TaskActivity {


    @Override
    public void setUpActivity() {
        readTag(this.getIntent());
        activateTask();
        finish();
    }

    @Override
    public void activateTask() {
        String pkgName = getPayload()[0];
        PackageManager pm = getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(pkgName);
        startActivity(intent);
    }
}
