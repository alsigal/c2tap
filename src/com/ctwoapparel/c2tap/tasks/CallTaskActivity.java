package com.ctwoapparel.c2tap.tasks;

import android.content.Intent;
import android.net.Uri;

/**
 * Activity to make a phone call once tag is tapped.
 */
public class CallTaskActivity extends TaskActivity {

    @Override
    public void setUpActivity() {
        readTag(this.getIntent());
        activateTask();
        finish();
    }

    @Override
    public void activateTask() {
        dialPhoneNumber(getPayload()[0]);
    }

    /*
     * launch intent to call number
     */
    private void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
