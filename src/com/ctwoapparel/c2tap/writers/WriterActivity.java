package com.ctwoapparel.c2tap.writers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Abstract class that all activities that write to a tag extend.
 */
public abstract class WriterActivity extends Activity {

    /**
     * MIME-types for custom activities to be activated
     * when tag is tapped.
     */
    public static final String PREV = "application/c23";
    public static final String NEXT = "application/c22";
    public static final String TOGGLE = "application/c24";
    public static final String CALL = "application/c20";
    public static final String SMS = "application/c21";
    public static final String LAUNCH = "application/c25";


    private String m_mimeType;
    private boolean m_writeModeOn = false;
    private String[] m_payload;
    private String m_uriString;
    private AlertDialog m_ad; // "tap tag now"-type dialog

    protected NfcAdapter m_nfcAdapter;
    protected PendingIntent m_pendingIntent;
    protected IntentFilter[] m_writeTagFilters;
    protected Tag m_detectedTag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setUpLayout();

        m_nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        m_pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter ndefIntent = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);

        m_writeTagFilters = new IntentFilter[]{ndefIntent};

        this.setUpActivity();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (m_nfcAdapter != null) {
            m_nfcAdapter.enableForegroundDispatch(this, m_pendingIntent, m_writeTagFilters, null);
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

        performTagOps(intent);
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (m_ad != null) {
            m_ad.dismiss();
        }
    }

    /**
     * Creates the NDEF message to be written to tag
     * (either MIME or URI, with or without payload)
     * @param uriName name of URI to write on tag
     * @return an NDEF message
     */
    public NdefMessage createMessage(String uriName) {
        NdefRecord[] records;

        // MIME type
        if (uriName == null) {
            String mType = getMimeType();
            String[] pl = getPayload();
            if (pl != null) {
                records = new NdefRecord[pl.length];
                for (int i=0; i<pl.length; i++) {
                    byte[] plBytes = pl[i].getBytes(Charset.forName("UTF-8"));
                    records[i] = NdefRecord.createMime(mType, plBytes);
                }
            } else {
                records = new NdefRecord[1];
                records[0] = NdefRecord.createMime(mType, new byte[0]);
                if (mType.equals(TOGGLE) || mType.equals(NEXT) || mType.equals(PREV)) {
                    Intent intent = new Intent("android.intent.action.MUSIC_PLAYER");
                    startService(intent);
                }
            }
        } else { // URI
            if (uriName.isEmpty()) {
                Toast.makeText(getApplicationContext(), "URL is empty", Toast.LENGTH_LONG).show();
                return null;
            }
            Uri uri = Uri.parse(uriName);
            NdefRecord ndefRecord = NdefRecord.createUri(uri);
            records = new NdefRecord[] {ndefRecord};
        }

        return new NdefMessage(records);
    }

    /**
     * Writes intended message to tag
     * @param tag
     * @return true iff tag was written to successfully.
     */
    public boolean writeTag(Tag tag) {

        String uriName = getUriString();
        NdefMessage message = createMessage(uriName);
        if (message == null) {
            return false;
        }

        try {
            // see if tag is already NDEF formatted
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();

                if (!ndef.isWritable()) {
                    Toast.makeText(getApplicationContext(), "Read-only tag",
                            Toast.LENGTH_LONG).show();
                    return false;
                }

                // figure out how much space we need
                int size = message.toByteArray().length;
                Log.d("msg size", String.valueOf(size));
                Log.d("tag size", String.valueOf(ndef.getMaxSize()));
                if (ndef.getMaxSize() < size) {
                    Toast.makeText(getApplicationContext(), "Not enough space on tag", Toast.LENGTH_LONG).show();
                    return false;
                }

                ndef.writeNdefMessage(message);
                Toast.makeText(getApplicationContext(), "Successfully written to tag", Toast.LENGTH_LONG).show();
                return true;
            } else {
                // try to format tag to NDEF
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        Toast.makeText(getApplicationContext(), "Successfully written to tag", Toast.LENGTH_LONG).show();
                        return true;
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "Unable to format to NDEF", Toast.LENGTH_LONG).show();
                        return false;
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Tag does not support NDEF", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }

        return false;
    }

    /**
     * Writes to tag if tag detected
     * @param intent
     */
    public void performTagOps(Intent intent) {

        if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {

            m_detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (m_detectedTag == null) {
                Toast.makeText(getApplicationContext(), "Tag not found.", Toast.LENGTH_LONG).show();
            } else {
                if (getWriteMode()) {
                    writeTag(m_detectedTag);
                }
                setWriteModeOff();
            }
        }
    }

    /**
     * @return String indicating the MIME-type
     */
    public String getMimeType() {
        return m_mimeType;
    }

    /**
     * Set the MIME-type
     * @param mimeType String indicating the MIME-type
     */
    public void setMimeType(String mimeType) {
        this.m_mimeType = mimeType;
    }

    /**
     * @return true iff write mode is on
     */
    public boolean getWriteMode() {
        return this.m_writeModeOn;
    }

    /**
     * Set write mode to on
     */
    public void setWriteModeOn() {
        this.m_writeModeOn = true;
    }

    /**
     * Set write mode to off
     */
    public void setWriteModeOff() {
        this.m_writeModeOn = false;
    }

    /**
     * Returns payload to be written to tag.
     * @return String[]
     */
    public String[] getPayload() {
        return this.m_payload;
    }

    /**
     * Set the payload to be written to tag.
     * @param payload String array which is the payload
     */
    public void setPayload(String[] payload) {
        this.m_payload = payload;
    }

    /**
     * Returns the URI to be written to tag.
     * @return String
     */
    public String getUriString() {
        return this.m_uriString;
    }

    /**
     * Set the URI to be written to tag.
     * @param uriString
     */
    public void setUriString(String uriString) {
        this.m_uriString = uriString;
    }

    /**
     * Shows the user a dialog which instructs to tap phone on tag.
     * @return the AlertDialog
     */
    public AlertDialog showDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Tap C-II logo on apparel");
        adb.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                setWriteModeOff();
                dialog.dismiss();
            }
        }).setIcon(android.R.drawable.ic_dialog_alert);
        m_ad = adb.create();
        m_ad.show();
        return m_ad;
    }

    /**
     * Sets user preferences for UI text fields.
     * @param key String
     * @param value String
     */
    public void setPrefs(String key, String value) {
        PreferencesHelper prefHelper = new PreferencesHelper(getApplicationContext());
        prefHelper.savePreferences(key, value);
    }

    /**
     * Retrieves preferences for UI text fields.
     * @param key String
     * @return String, the value of the preference
     */
    public String getPrefs(String key) {
        PreferencesHelper prefHelper = new PreferencesHelper(getApplicationContext());
        return prefHelper.getPreferences(key);
    }

    /**
     * Set up the visual layout of the activity
     */
    public void setUpLayout() { }

    /**
     * Set up objects needed for the activity on the onCreate method of child classes.
     */
    public abstract void setUpActivity();
}
