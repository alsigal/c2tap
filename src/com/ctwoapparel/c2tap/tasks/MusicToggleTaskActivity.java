package com.ctwoapparel.c2tap.tasks;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

/**
 * Activity to toggle default music player tracks between play and pause.
 */
public class MusicToggleTaskActivity extends TaskActivity {

    @Override
    public void setUpActivity() {
        readTag(this.getIntent());
        activateTask();
        finish();
    }

    @Override
    public void activateTask() {
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        Intent i = new Intent(SERVICECMD);
        if (mAudioManager.isMusicActive()) {
            i.putExtra(CMDNAME, CMDTOGGLEPAUSE);
        } else {
            i.putExtra(CMDNAME, CMDPLAY);
        }
        MusicToggleTaskActivity.this.sendOrderedBroadcast(i, null);
    }

}
