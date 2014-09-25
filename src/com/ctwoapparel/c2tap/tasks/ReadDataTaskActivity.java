package com.ctwoapparel.c2tap.tasks;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

/**
 * Activity to read text stored in tag and display in a dialog to user.
 */
public class ReadDataTaskActivity extends TaskActivity {
    @Override
    public void setUpActivity() {
        readTag(this.getIntent());
        activateTask();
    }

    @Override
    public void activateTask() {
        String[] pl = getPayload();
        try {
            String data = pl[0];
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("NFC tag text");
            dialog.setMessage(data);
            dialog.setNegativeButton("Done reading",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    }
            );
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Could not read data.",
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
