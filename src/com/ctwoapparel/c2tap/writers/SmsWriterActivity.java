package com.ctwoapparel.c2tap.writers;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.ctwoapparel.c2tap.R;

/**
 * Class to program an SmsTaskActivity launch to tag.
 * When the programmed tag is tapped, the phone will send an SMS to the intended recipient.
 */
public class SmsWriterActivity extends WriterActivity {

    private static final int REQUEST_SELECT_CONTACT = 1;

    private String m_number; // the phone number for the SMS
    private EditText m_numberBox;

    @Override
    public void setUpLayout() {
        setContentView(R.layout.sms);
        ActionBar actionBar = getActionBar();
        actionBar.setTitle("Program an Sms");

        final EditText msgBox = (EditText)findViewById(R.id.message);
        final Button conBn = (Button)findViewById(R.id.contactButton);
        final Button saveBn = (Button)findViewById(R.id.saveButton);
        m_numberBox = (EditText)findViewById(R.id.contactNumBox);

        String num = getPrefs("sms_number");
        String msg = getPrefs("msg");
        m_numberBox.setText(num);
        m_number = num;
        msgBox.setText(msg);

        // fetch a number from the user's contact list
        conBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_SELECT_CONTACT);
                }
            }
        });

        // once save button is pressed, preferences are stored and tag is written to
        saveBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = "";
                if (m_numberBox.getText() != null) {
                    m_number = m_numberBox.getText().toString();
                }
                if (msgBox.getText() != null) {
                    msg = msgBox.getText().toString();
                }
                if (m_number.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "You must indicate a phone number",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                setPrefs("msg", msg);
                setPrefs("sms_number", m_number);
                setPayload(new String[] {msg, m_number});
                setWriteModeOn();
                showDialog();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_CONTACT && resultCode == RESULT_OK) {
            Uri contactUri = data.getData();
            String[] projection = new String[]{
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER
            };
            Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);

            String contactNumber = "";

            // If the cursor returned is valid, get the phone number
            if (cursor != null && cursor.moveToFirst()) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                String hasPhone = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                if (hasPhone.equalsIgnoreCase("1")) {
                    Cursor phone = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,
                            null, null);
                    if (phone != null && phone.moveToFirst()) {
                        contactNumber = phone.getString(phone.getColumnIndex("data1"));
                    }
                }
                m_number = contactNumber;
                m_numberBox.setText(m_number);
            }
        }
    }

    @Override
    public void setUpActivity() {
        setMimeType(SMS);
    }
}
