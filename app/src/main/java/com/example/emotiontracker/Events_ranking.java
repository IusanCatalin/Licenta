package com.example.emotiontracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
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
import android.widget.Button;
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
import java.util.Collections;
import java.util.Comparator;

public class Events_ranking extends AppCompatActivity {

    private ListView lw;
    private ListAdapter listAdapter;
    private Toolbar toolbar;
    private ArrayList<DatabaseItemEvents> events_database = new ArrayList<DatabaseItemEvents>();
    private Button sort_score;
    private Button sort_times;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_ranking);
        sort_score = findViewById(R.id.sort_by_score);
        sort_times = findViewById(R.id.sort_by_times);
        setupUIViews();
        initToolbar();
        events_database = MainActivity.events_database;
        lw = findViewById(R.id.events_list);
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

    public void sort_byScore(View view) {
        sort_score.setAlpha(0f);
        sort_score.animate().alpha(1f).setDuration(1500);
        Collections.sort(events_database, new Comparator<DatabaseItemEvents>() {
            public int compare(DatabaseItemEvents c1, DatabaseItemEvents c2) {
                if (c1.score> c2.score) return -1;
                if (c1.score < c2.score ) return 1;
                return 0;
            }});
        listAdapter.notifyDataSetChanged();
    }

    public void sort_byTimes(View view) {
        sort_times.setAlpha(0f);
        sort_times.animate().alpha(1f).setDuration(1500);
        Collections.sort(events_database, new Comparator<DatabaseItemEvents>() {
            public int compare(DatabaseItemEvents c1, DatabaseItemEvents c2) {
                if (c1.times> c2.times) return -1;
                if (c1.times < c2.times ) return 1;
                return 0;
            }});
        listAdapter.notifyDataSetChanged();
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
            ItemText.setText(events_database.get(position).title + " - Happiness Score:" + String.valueOf(events_database.get(position).score) + " Times done:" + String.valueOf(events_database.get(position).times));
            return convertView;
        }
    }
}
