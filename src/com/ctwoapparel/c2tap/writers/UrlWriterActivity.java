package com.ctwoapparel.c2tap.writers;

import android.app.ActionBar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.ctwoapparel.c2tap.R;

/**
 * This class writes a URL to a tag.
 */
public class UrlWriterActivity extends WriterActivity {

    @Override
    public void setUpLayout() {
        setContentView(R.layout.url);
        ActionBar actionBar = getActionBar();
        actionBar.setTitle("Hold a URL");

        final EditText editTextBox = (EditText) findViewById(R.id.urlBox);
        final Button saveBn = (Button) findViewById(R.id.saveButton);

        String pref = getPrefs("url");
        if (!pref.isEmpty()) {
            editTextBox.setText(pref);
        }

        saveBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = null;
                if (editTextBox.getText() != null) {
                    if (!editTextBox.getText().toString().isEmpty()) {
                        url = editTextBox.getText().toString();
                        setPrefs("url", url);
                    }
                }
                if (url == null || url.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Nothing to save", Toast.LENGTH_LONG).show();
                    return;
                }
                setUriString(url);
                setWriteModeOn();
                showDialog();
            }
        });
    }

    @Override
    public void setUpActivity() {}
}
