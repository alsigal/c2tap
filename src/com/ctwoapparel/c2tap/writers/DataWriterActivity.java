package com.ctwoapparel.c2tap.writers;

import android.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.ctwoapparel.c2tap.R;

/**
 * Class to program tag to store text.
 */
public class DataWriterActivity extends WriterActivity {

    private String m_data; // what we want to store
    private static int CHAR_LIMIT = 120; // limit of the num. of characters allowed to store

    @Override
    public void setUpLayout() {
        setContentView(R.layout.data);
        ActionBar actionBar = getActionBar();
        actionBar.setTitle("Hold data");

        final EditText editTextBox = (EditText) findViewById(R.id.dataBox);
        final Button saveBn = (Button) findViewById(R.id.saveButton);
        final TextView countShow = (TextView) findViewById(R.id.tvCount);

        m_data = getPrefs("data");
        if (!m_data.isEmpty()) {
            editTextBox.setText(m_data);
        }


        editTextBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editTextBox.getText() == null) {
                    count = CHAR_LIMIT;
                } else {
                    count = CHAR_LIMIT - editTextBox.getText().toString().getBytes().length;
                }
                // show user how much space is left for them to store
                countShow.setText(String.valueOf(count) + " bytes left");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        saveBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextBox.getText() != null) {
                    m_data = editTextBox.getText().toString();
                    setPrefs("data", m_data);
                    setPayload(new String[] { m_data });
                }
                if (m_data.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Nothing to save",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                setWriteModeOn();
                showDialog();

            }
        });
    }

    @Override
    public void setUpActivity() {
        setMimeType("text/plain");
    }
}
