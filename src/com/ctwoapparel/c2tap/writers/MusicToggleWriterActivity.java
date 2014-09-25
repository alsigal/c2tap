package com.ctwoapparel.c2tap.writers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Class to program the tag to toggle play/pause (music track) once tapped.
 */
public class MusicToggleWriterActivity extends WriterActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setUpActivity() {

        setMimeType(TOGGLE);
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
