package com.ctwoapparel.c2tap.writers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.util.HashMap;
import java.util.List;

/**
 * Class to program AppLaunchTaskActivity launch onto tag.
 * The user specifies an app they would like to launch when tapping the tag.
 */
public class AppLaunchWriterActivity extends WriterActivity {

    protected HashMap<String, String> appsInfo = new HashMap<String, String>();
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setUpActivity() {
        final String title = "Choose an app";

        final PackageManager pm = getPackageManager();
        // get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        // get info for the app launch to be programmed
        for (ApplicationInfo packageInfo : packages) {
            Intent appIntent = pm.getLaunchIntentForPackage(packageInfo.packageName);
            if (appIntent != null) {
                String appName = pm.getApplicationLabel(packageInfo).toString();
                String pkgName =  packageInfo.packageName;
                appsInfo.put(appName, pkgName);
            }
        }

        String[] appNames = appsInfo.keySet()
                .toArray(new String[appsInfo.keySet().toArray().length]);
        AlertDialog.Builder appsDialog = new AlertDialog.Builder(AppLaunchWriterActivity.this);
        appsDialog.setTitle(title);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.select_dialog_item, appNames);

        /* Show dialog that lets user pick which app they would like to program onto tag */
        appsDialog.setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        AppLaunchWriterActivity.this.finish();
                    }
                }
        );

        appsDialog.setAdapter(arrayAdapter,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String appPicked = arrayAdapter.getItem(which);
                        String pkgName = AppLaunchWriterActivity.this.appsInfo.get(appPicked);
                        setMimeType(AppLaunchWriterActivity.this.LAUNCH);
                        setPayload(new String[] { pkgName });
                        setWriteModeOn();
                        showDialog();
                    }
                }
        );
        appsDialog.show();

    }
}
