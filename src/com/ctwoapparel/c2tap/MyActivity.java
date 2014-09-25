package com.ctwoapparel.c2tap;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.ctwoapparel.c2tap.writers.*;

import java.util.ArrayList;
import java.util.HashMap;

import static android.widget.ExpandableListView.OnItemClickListener;

/**
 * Main activity for the app.
 * Shows user all the tasks that the app can do.
 * Lets user choose said task.
 */
public class MyActivity extends Activity {

    private ArrayList<Integer> m_imageId;
    private ListView m_listView;
    private ArrayList<String> m_listDataHeader;
    private HashMap<String, Class> m_activityToStart;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        // get the listview
        m_listView = (ListView) findViewById(R.id.lv);

        // preparing list data
        prepareListData();

        // setting list adapter
        CustomList listAdapter = new CustomList(this, m_listDataHeader, m_imageId);
        m_listView.setAdapter(listAdapter);

        m_listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String act = (String)parent.getItemAtPosition(position);
                Intent intent = new Intent(MyActivity.this, m_activityToStart.get(act));
                MyActivity.this.startActivity(intent);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        ActionBar actionBar = getActionBar();
        actionBar.setTitle(R.string.app_name);
    }


    /*
     * Populate the list with the tasks the app can do
     */
    private void prepareListData() {

        m_imageId = new ArrayList<Integer>();

        m_imageId.add(R.drawable.ic_action_call);
        m_imageId.add(R.drawable.text);
        m_imageId.add(R.drawable.ic_action_person);
        m_imageId.add(R.drawable.web);
        m_imageId.add(R.drawable.data);
        m_imageId.add(R.drawable.ic_action_play);
        // m_imageId.add(R.drawable.ic_action_next);
        // m_imageId.add(R.drawable.ic_action_previous);
        m_imageId.add(R.drawable.app);

        m_listDataHeader = new ArrayList<String>();
        m_activityToStart = new HashMap<String, Class>();

        m_listDataHeader.add("Program a phone call");
        m_listDataHeader.add("Program an SMS");
        m_listDataHeader.add("Hold contact information");
        m_listDataHeader.add("Hold a URL");
        m_listDataHeader.add("Hold data");
        m_listDataHeader.add("Toggle music");
        // m_listDataHeader.add("Next track");
        // m_listDataHeader.add("Previous track");
        m_listDataHeader.add("Program app launch");

        m_activityToStart.put(m_listDataHeader.get(0), CallWriterActivity.class);
        m_activityToStart.put(m_listDataHeader.get(1), SmsWriterActivity.class);
        m_activityToStart.put(m_listDataHeader.get(2), ContactWriterActivity.class);
        m_activityToStart.put(m_listDataHeader.get(3), UrlWriterActivity.class);
        m_activityToStart.put(m_listDataHeader.get(4), DataWriterActivity.class);
        m_activityToStart.put(m_listDataHeader.get(5), MusicToggleWriterActivity.class);
        // m_activityToStart.put(m_listDataHeader.get(6), NextTrackWriterActivity.class);
        // m_activityToStart.put(m_listDataHeader.get(7), PrevTrackWriterActivity.class);
        m_activityToStart.put(m_listDataHeader.get(6), AppLaunchWriterActivity.class);
    }

    /*
     *
     */
    private class CustomList extends ArrayAdapter<String> {

        private final Activity context;
        private final ArrayList<String> activities;
        private final  ArrayList<Integer> images;

        /**
         * Custom list for our app
         * @param context
         * @param activities
         * @param images
         */
        public CustomList(Activity context, ArrayList<String> activities, ArrayList<Integer> images) {
            super(context, R.layout.list_item, activities);
            this.context = context;
            this.activities = activities;
            this.images = images;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView= inflater.inflate(R.layout.list_item, null, true);
            TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
            txtTitle.setText(activities.get(position));
            imageView.setImageResource(images.get(position));
            return rowView;
        }
    }
}
