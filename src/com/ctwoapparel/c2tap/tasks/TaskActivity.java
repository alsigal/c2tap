package com.ctwoapparel.c2tap.tasks;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Toast;

/**
 * Abstract class that the activities that are launched
 * once a programmed tag is tapped extend.
 */
public abstract class TaskActivity extends Activity {

    private String[] m_payload;

    protected NfcAdapter m_nfcAdapter;
    protected PendingIntent m_pendingIntent;
    protected IntentFilter[] m_readNdefFilters;

    /**
     * Commands for the default music player service
     */
    public static final String CMDTOGGLEPAUSE = "togglepause";
    public static final String CMDPREVIOUS = "previous";
    public static final String CMDNEXT = "next";
    public static final String SERVICECMD = "com.android.music.musicservicecommand";
    public static final String CMDNAME = "command";
    public static final String CMDPLAY = "play";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        m_pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter ndefIntent = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);

        m_readNdefFilters = new IntentFilter[]{ndefIntent};

        setUpActivity();

    }

    @Override
    public void onResume() {
        super.onResume();

        if (m_nfcAdapter != null) {
            m_nfcAdapter.enableForegroundDispatch(this, m_pendingIntent, m_readNdefFilters, null);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (m_nfcAdapter != null) {
            m_nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        readTag(intent);
        activateTask();
    }

    /**
     * Read the contents of the tag and store payload in m_payload
     * @param intent
     */
    public void readTag(Intent intent) {

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            NdefMessage message = null;
            Parcelable[] rawMsg = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsg != null) {
                message = (NdefMessage) rawMsg[0];
            }
            if (message != null) {
                String[] results = new String[message.getRecords().length];
                String result = "";
                for (int i=0; i<message.getRecords().length; i++) {
                    byte[] pl = message.getRecords()[i].getPayload();

                    for (int b = 0; b < pl.length; b++) {
                        result += (char) pl[b];
                    }
                    results[i] = result;
                    result = "";
                }
                setPayload(results);
                Toast.makeText(getApplicationContext(), "Tag was read successfully",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    "Could not read tag", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    /**
     * Set up objects needed for the activity on the onCreate method of child classes.
     */
    public abstract void setUpActivity();

    /**
     * If there is a an activity that the task launches it is done here
     */
    public abstract void activateTask();

    /**
     * @return String array with the payload of the tag
     */
    public String[] getPayload() {
        return m_payload;
    }

    /**
     * Set private member that stores the payload
     * @param payload String array
     */
    public void setPayload(String[] payload) {
        this.m_payload = payload;
    }
}
