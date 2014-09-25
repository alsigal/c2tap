package com.ctwoapparel.c2tap.writers;

import android.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.ctwoapparel.c2tap.R;

import java.util.HashMap;

/**
 * Class to store and write to a tag the users contact information in VCARD format.
 */
public class ContactWriterActivity extends WriterActivity {

    HashMap<EditText, String> map = new HashMap<EditText, String>();

    protected Button saveBtn;
    protected EditText nameBox;
    protected EditText surnameBox;
    protected EditText titleBox;
    protected EditText orgBox;
    protected EditText phoneBox;
    protected EditText emailBox;
    protected EditText addressBox;
    protected EditText websiteBox;

    protected EditText[] boxList;
    protected String[] prefsList = new String[] { "contactsName", "contactsSurname",
            "contactsOrg","contactsTitle", "contactsPhone", "contactsAddress",
            "contactsEmail", "contactsWebsite" };

    @Override
    public void setUpActivity() {

        setMimeType("text/vcard");
        setContentView(R.layout.contact);
        ActionBar actionBar = getActionBar();
        actionBar.setTitle("Store contact information");

        saveBtn = (Button) findViewById(R.id.contButton);
        nameBox = (EditText) findViewById(R.id.contName);
        surnameBox = (EditText) findViewById(R.id.contSurname);
        titleBox = (EditText) findViewById(R.id.contTitle);
        orgBox = (EditText) findViewById(R.id.contOrg);
        phoneBox = (EditText) findViewById(R.id.contPhone);
        emailBox = (EditText) findViewById(R.id.contEmail);
        addressBox = (EditText) findViewById(R.id.contAddress);
        websiteBox = (EditText) findViewById(R.id.contWebsite);

        boxList = new EditText[] { nameBox, surnameBox, orgBox, titleBox, phoneBox, addressBox,
                emailBox, websiteBox
        };

        m_getPrefs();

        // writes to tag here
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String vcard = createVcard();
                Log.d("VCARD\n", vcard);
                setPayload(new String[] { vcard });
                setWriteModeOn();
                showDialog();
            }
        });
    }

    /*
     * creates a string in VCARD format
     */
    private String createVcard() {

        /* populate hash-map to map text boxes to string in those boxes*/
        int i = 0;
        for (EditText box : boxList) {
            if (box.getText() != null && box.getText().toString().length() != 0) {
                map.put(box, box.getText().toString());
                m_storePrefs(prefsList[i], box.getText().toString());
            } else {
                map.put(box, "");
            }

            i++;
        }

        /* vcard fields */
        String begin = "BEGIN:VCARD\r\nVERSION:3.0\r\n";
        String name = "N:" + map.get(surnameBox) + ";" + map.get(nameBox) + ";;;" + "\r\n";
        String org = "ORG:" + map.get(orgBox) + "\r\n";
        String title = "TITLE:" + map.get(titleBox) + "\r\n";
        String phone = "TEL;TYPE=WORK:" + map.get(phoneBox) + "\r\n";
        String address = "ADR;TYPE=WORK:;;" + map.get(addressBox) + "\r\n";
        String email = "EMAIL:" + map.get(emailBox) + "\r\n";
        String url = "URL:" + map.get(websiteBox) + "\r\n";
        String end = "END:VCARD\r\n";

        String[] vcardList = { name, org, title, phone, address, email, url };

        /* create the vcard string */
        String vcard = begin;
        int j = 0;
        for (i = 1; i < boxList.length; i++) {
            EditText box = boxList[i];
            if (!map.get(box).isEmpty()) {
                vcard += vcardList[j];
            } else if (box.equals(surnameBox)) {
                if (!map.get(nameBox).isEmpty()) {
                    vcard += name;
                }
            }
            j++;
        }
        vcard += end;

        return vcard;
    }

    /*
     * Store user preferences
     */
    private void m_storePrefs(String key, String val) {
        setPrefs(key, val);
    }

    /*
     * Fetch user preferences onto the text boxes
     */
    private void m_getPrefs() {
        String val;
        for (int i=0; i<boxList.length; i++) {
            val = getPrefs(prefsList[i]);
            if (!val.isEmpty()) {
                boxList[i].setText(val);
            }
        }
    }

}
