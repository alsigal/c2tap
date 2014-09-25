package com.ctwoapparel.c2tap.tasks;

import android.app.PendingIntent;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;

/**
 * Activity to send SMS message to recipient once tag is tapped
 * Both the number and message are indicated in the tag.
 */
public class SmsTaskActivity extends TaskActivity {

    @Override
    public void setUpActivity() {
        readTag(this.getIntent());
        activateTask();
        finish();
    }

    @Override
    public void activateTask() {
        String[] pl = getPayload();
        try {
            String message = pl[0];
            String phoneNumber = pl[1];
            sendSmsMessage(message, phoneNumber);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Could not send text message",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Send the message to the recipient
     * @param message the message to send
     * @param phoneNumber the number to send the message to
     */
    public void sendSmsMessage(String message, String phoneNumber) {
        SmsManager smsManager = SmsManager.getDefault();

        PendingIntent sentPI;
        String sent = "SMS_SENT";

        sentPI = PendingIntent.getBroadcast(this, 0,new Intent(sent), 0);
        smsManager.sendTextMessage(phoneNumber, null, message, sentPI, null);
    }
}
