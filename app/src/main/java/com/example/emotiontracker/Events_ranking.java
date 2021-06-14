package com.example.emotiontracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Instances;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.emotiontracker.Database;
import com.example.emotiontracker.DatabaseItemEvents;
import com.example.emotiontracker.DatabaseItemModel;
import com.example.emotiontracker.R;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Events_ranking extends AppCompatActivity {

    private ArrayList<String> events = new ArrayList<String>();
    private ArrayList<DatabaseItemEvents> events_database = new ArrayList<DatabaseItemEvents>();
    private ListView lw;
    private ListAdapter listAdapter;
    private Database database;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_ranking);
        setupUIViews();
        initToolbar();
        lw = findViewById(R.id.events_list);
        getEvents();
        getDatabaseEvents();
        addNewEvents();
        getDatabaseEvents2(); // second time in case of new entries
        checkForExistingEvents();
        listAdapter = new ListAdapter();
        lw.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();
    }

    public void setupUIViews()
    {
        toolbar = findViewById(R.id.history_toolbar);
    }

    private void initToolbar(){
        //setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Happiness History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home : {
                onBackPressed();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void getEvents() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        Log.w("Date -1", String.valueOf(day));
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        Calendar start = Calendar.getInstance();
        start.set(year, month, day, 0, 0);
        long startMillis = start.getTimeInMillis();
        Log.w("Start time", String.valueOf(startMillis));
        Log.w("daystart", String.valueOf(start.get(Calendar.DAY_OF_MONTH)));
        Log.w("daystart", String.valueOf(start.get(Calendar.MONTH)));
        Log.w("daystart", String.valueOf(start.get(Calendar.YEAR)));
        Calendar end = Calendar.getInstance();
        end.set(year, month, day, 23, 59);
        long endMillis = end.getTimeInMillis();
        Log.w("End time", String.valueOf(endMillis));
        Log.w("dayend", String.valueOf(end.get(Calendar.DAY_OF_MONTH)));
        Log.w("dayend", String.valueOf(end.get(Calendar.MONTH)));
        Log.w("dayend", String.valueOf(end.get(Calendar.YEAR)));
        Cursor cur = null;
        ContentResolver cr = getContentResolver();
        try {
            Uri.Builder builder = Instances.CONTENT_URI.buildUpon();
            ContentUris.appendId(builder, startMillis);
            ContentUris.appendId(builder, endMillis);

            String[] INSTANCE_PROJECTION = new String[]{Instances.EVENT_ID,
                    Instances.TITLE, Instances.BEGIN, Instances.END};

            Uri uri = builder.build();
            cur = cr.query(uri, INSTANCE_PROJECTION, null, null, null);

            if (cur != null) {
                while (cur.moveToNext()) {
                    String title = cur.getString(cur.getColumnIndex(Instances.TITLE));
                    Log.w("eveniment", title);
                    events.add(title);
                }
            }
        } catch (SecurityException e) {
            // no permission to read calendars
        } finally {
            if (cur != null)
                cur.close();
        }
        try{
            Log.w("Calendar events are:" , events.get(0));
        }catch (java.lang.IndexOutOfBoundsException e){
            Log.w("No new calendar events" , "No new events");
        }

    }

    private void getDatabaseEvents() {
        database = new Database(this);
        events_database = database.readEventEntries();
        try {
            Log.w("events database:", events_database.get(events_database.size() - 1).title);
        }catch (java.lang.ArrayIndexOutOfBoundsException e){
            Log.w("events database" , "has 0 events");
        }
    }

    private void getDatabaseEvents2() {
        database = new Database(this);
        events_database = database.readEventEntries();
        try {
            Log.w("2events database:", events_database.get(events_database.size() - 1).title);
        }catch (java.lang.ArrayIndexOutOfBoundsException e){
            Log.w("2events database" , "has 0 events");
        }
    }

    private void addNewEvents(){
        int happy_score_prev_day = database.getPreviousDayEmotionScore();
        if(happy_score_prev_day == 0)
            happy_score_prev_day = 50; //we set it to 50 if the user didn't record his emotions in the previous day so it doesn't affect the activity score
        for( String e : events)
            database.createEntryEvents(e, happy_score_prev_day);
    }

    private void checkForExistingEvents(){
        int happy_score_prev_day = database.getPreviousDayEmotionScore();
        if(happy_score_prev_day == 0)
            happy_score_prev_day = 50; //we set it to 50 if the user didn't record his emotions in the previous day so it doesn't affect the activity score
        for(String e : events)
            for(DatabaseItemEvents ed : events_database){
                if(e.equals(ed.title)){
                    database.edit_events(ed.title, (ed.score + happy_score_prev_day) / 2, ed.times+1); // not updating ok at same activity more than 1 per day
                }
            }
    }

    private class ListAdapter extends BaseAdapter {

        private TextView ItemText;

        @Override
        public int getCount() {
            return events_database.size();
        }

        @Override
        public Object getItem(int position) {
            return events_database.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.single_list_item_events, parent, false);
            }
            ItemText = (TextView) convertView.findViewById(R.id.single_item_events);
            ItemText.setText(events_database.get(position).title + " Score:" + String.valueOf(events_database.get(position).score) + " Times done: " + String.valueOf(events_database.get(position).times));
            return convertView;
        }
    }
}
