package com.ctwoapparel.c2tap.tasks;

import android.content.Intent;

/**
 * Activity to play the next music track once tag is tapped.
 */
public class NextTrackTaskActivity extends TaskActivity {

    @Override
    public void setUpActivity() {
        readTag(this.getIntent());
        activateTask();
        finish();
    }

    @Override
    public void activateTask() {
        Intent i = new Intent(SERVICECMD);
        i.putExtra(CMDNAME, CMDNEXT);
        NextTrackTaskActivity.this.sendOrderedBroadcast(i, null);
    }

}
