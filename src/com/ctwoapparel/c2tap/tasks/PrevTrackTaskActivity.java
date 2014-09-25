package com.ctwoapparel.c2tap.tasks;

import android.content.Intent;

/**
 * Activity to play the previous music track once tag is tapped.
 */
public class PrevTrackTaskActivity extends TaskActivity {

    @Override
    public void setUpActivity() {
        readTag(this.getIntent());
        activateTask();
        finish();
    }

    @Override
    public void activateTask() {
        Intent i = new Intent(SERVICECMD);
        i.putExtra(CMDNAME, CMDPREVIOUS);
        PrevTrackTaskActivity.this.sendOrderedBroadcast(i, null);
    }
}
