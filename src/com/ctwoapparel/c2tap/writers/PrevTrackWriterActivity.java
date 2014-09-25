package com.ctwoapparel.c2tap.writers;

import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * Class to program the tag to play the next track once tapped.
 */
public class PrevTrackWriterActivity extends WriterActivity {

    @Override
    public void setUpActivity() {

        setMimeType(PREV);
        setWriteModeOn();
        AlertDialog ad = showDialog();
        ad.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });

    }
}
