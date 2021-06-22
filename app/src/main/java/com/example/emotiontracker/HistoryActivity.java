package com.example.emotiontracker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DirectAction;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;

public class HistoryActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    protected androidx.appcompat.widget.Toolbar toolbar;
    private ListView mListView;
    private ListAdapter mListAdapter;
    private Database mDatabase;
    private ArrayList<DatabaseItemModel> ResultsList;
    private Spinner spinner;
    private TextView date1;
    private TextView date2;
    private DatePickerDialog.OnDateSetListener mOnDateSetListener1;
    private DatePickerDialog.OnDateSetListener mOnDateSetListener2;
    private Button button_graph;
    private Button ok_button;
    public static CustomDate mStringDate = new CustomDate();
    public static ArrayList<DatabaseItemModel> graphItemList = new ArrayList<DatabaseItemModel>();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ok_button = findViewById(R.id.btn_ok);
        button_graph = findViewById(R.id.btn_graph);
        mListView = (ListView)findViewById(R.id.items_list);
        date1 =(TextView)findViewById(R.id.date1);
        date2 =(TextView)findViewById(R.id.date2);
        getDataForList();
        mListAdapter = new ListAdapter();
        mListView.setAdapter(mListAdapter);
        mListAdapter.notifyDataSetChanged();
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.select_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        mOnDateSetListener1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = year + "/" + Database.monthNames[month] + "/" + Database.days[dayOfMonth];
                date1.setText(date);
                String dateToCompare = year + "/" + Database.monthNames[month] + "/" + Database.days[dayOfMonth-1]; //bug? or between not working? solved by -1
                mStringDate.setData1(dateToCompare);
            }
        };
        mOnDateSetListener2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = year + "/" + Database.monthNames[month] + "/" + Database.days[dayOfMonth];
                date2.setText(date);
                String dateToCompare = year + "/" + Database.monthNames[month] + "/" + Database.days[dayOfMonth];
                mStringDate.setData2(dateToCompare);
            }
        };
        setupUIViews();
        initToolbar();
    }
    /* test function for adding data
    public void AddData(){
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isInserted = mStepsDBHelper.insertTest("02.03.04",50);

                        if(isInserted = true)
                            Toast.makeText(StepsListActivity.this, "Data Inserted" , Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(StepsListActivity.this, "Data NOT Inserted" , Toast.LENGTH_LONG).show();
                    }
                }
        );
    }*/

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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

    private void getDataForList()
    {
        mDatabase = new Database(this);
        ResultsList = mDatabase.readEntries();
        extract_date_day();
    }

    private void getDataToday()
    {
        mDatabase = new Database(this);
        ResultsList = mDatabase.readTodayEntries();
        extract_date_time();
    }

    private void getDataMonth()
    {
        mDatabase = new Database(this);
        ResultsList = mDatabase.readMonthEntries();
        extract_date_day();
    }

    private void getDataYear()
    {
        mDatabase = new Database(this);
        ResultsList = mDatabase.readYearEntries();
        extract_date_month();
    }

    private void getDataCustom()
    {
        mDatabase = new Database(this);
        ResultsList = mDatabase.readCustomEntries();
        extract_date_day();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selected = parent.getItemAtPosition(position).toString();
        Log.w("TODAY", selected);
        if (selected.equals("All"))
        {
            getDataForList();
            mListAdapter.notifyDataSetChanged();
        }
        if (selected.equals("Today"))
        {
            getDataToday();
            mListAdapter.notifyDataSetChanged();
        }
        else if (selected.equals("This month"))
        {
            getDataMonth();
            mListAdapter.notifyDataSetChanged();
        }
        else if (selected.equals("This year")){
            getDataYear();
            mListAdapter.notifyDataSetChanged();
        }
        else{
            getDataForList();
            mListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        getDataForList();
        mListAdapter.notifyDataSetChanged();
    }

    public void onClickDate1(View view) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(HistoryActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mOnDateSetListener1, year, month , day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void onClickDate2(View view) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(HistoryActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mOnDateSetListener2, year, month , day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void onClickOkDate(View view) {
        ok_button.setAlpha(0f);
        ok_button.animate().alpha(1f).setDuration(1500);
        getDataCustom();
        mListAdapter.notifyDataSetChanged();
    }

    public void start_graph_activity(View view) {
        button_graph.setAlpha(0f);
        button_graph.animate().alpha(1f).setDuration(1500);
        Intent intent = new Intent (this, GraphActivity.class);
        startActivity(intent);
    }

    private class ListAdapter extends BaseAdapter {

        public TextView ItemText;
        @Override
        public int getCount() {

            return ResultsList.size();
        }

        @Override
        public Object getItem(int position) {
            return ResultsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.single_list_item_history, parent, false);
            }
            ItemText = (TextView) convertView.findViewById(R.id.single_item_details);
            ItemText.setText(ResultsList.get(position).mDate + " - Happiness Score: " + String.valueOf(ResultsList.get(position).mHappy_score) + "%");
            return convertView;
        }

    }
    public static class CustomDate{
        String data1;
        String data2;

        CustomDate()
        {
            data1 = "";
            data2 = "";
        }
        public void setData1(String d)
        {
            data1 = d;
        }
        public void setData2(String d)
        {
            data2 = d;
        }
    }

    private void extract_date_day(){

        graphItemList = new ArrayList<DatabaseItemModel>();
        ArrayList<DatabaseItemModel> mGraphItemList = new ArrayList<DatabaseItemModel>();
        for ( DatabaseItemModel i : ResultsList)
        {
            DatabaseItemModel item = new DatabaseItemModel();
            String date_with_time = i.mDate;
            int index_date = date_with_time.indexOf(" ");
            String date_without_time = "";
            if( index_date != -1)
            {
                date_without_time = date_with_time.substring(8, index_date);
            }
            item.mDate = date_without_time;
            item.mHappy_score = i.mHappy_score;
            mGraphItemList.add(item);
        }
        for (int i = 0; i < mGraphItemList.size()-1; i++)
        {
            int c = 1;
            for( int j = i+1; j < mGraphItemList.size(); j++)
            {
                if(mGraphItemList.get(i).mDate.equals(mGraphItemList.get(j).mDate))
                {
                    ++c;
                    mGraphItemList.get(i).mHappy_score = (mGraphItemList.get(i).mHappy_score + mGraphItemList.get(j).mHappy_score);
                }
            }
            mGraphItemList.get(i).mHappy_score = mGraphItemList.get(i).mHappy_score / c;
        }

        for (int i = mGraphItemList.size()-1; i > 0; i--)
        {
            if(!mGraphItemList.get(i).mDate.equals(mGraphItemList.get(i-1).mDate))
            {
                graphItemList.add(mGraphItemList.get(i));
            }
        }
        try{
            graphItemList.add(mGraphItemList.get(0));
        }catch (IndexOutOfBoundsException e){
            Toast.makeText(this, "No data found", Toast.LENGTH_LONG);
        }

    }

    private void extract_date_month(){

        graphItemList = new ArrayList<DatabaseItemModel>();
        ArrayList<DatabaseItemModel> mGraphItemList = new ArrayList<DatabaseItemModel>();
        for ( DatabaseItemModel i : ResultsList)
        {
            DatabaseItemModel item = new DatabaseItemModel();
            String date_with_time = i.mDate;
            int index_date = date_with_time.indexOf("/");
            String date_without_time = "";
            if( index_date != -1)
            {
                date_without_time = date_with_time.substring(index_date+1, index_date + 3); //extracts the month
            }
            item.mDate = date_without_time;
            item.mHappy_score = i.mHappy_score;
            mGraphItemList.add(item);
        }
        for (int i = 0; i < mGraphItemList.size()-1; i++)
        {
            int c = 1;
            for( int j = i+1; j < mGraphItemList.size(); j++)
            {
                if(mGraphItemList.get(i).mDate.equals(mGraphItemList.get(j).mDate))
                {
                    ++c;
                    mGraphItemList.get(i).mHappy_score = (mGraphItemList.get(i).mHappy_score + mGraphItemList.get(j).mHappy_score);
                }
            }
            mGraphItemList.get(i).mHappy_score = mGraphItemList.get(i).mHappy_score / c;
        }

        for (int i = mGraphItemList.size()-1; i > 0; i--)
        {
            if(!mGraphItemList.get(i).mDate.equals(mGraphItemList.get(i-1).mDate))
            {
                graphItemList.add(mGraphItemList.get(i));
            }
        }
        graphItemList.add(mGraphItemList.get(0));
    }

    private void extract_date_time() {
        graphItemList = new ArrayList<DatabaseItemModel>();
        for( DatabaseItemModel i : ResultsList)
        {
            DatabaseItemModel item = new DatabaseItemModel();
            String date_with_time = i.mDate;
            int index_date = date_with_time.indexOf(":");
            String date_only_time1 = "";
            String date_only_time2 = "";
            if( index_date != -1)
            {
                date_only_time1 = date_with_time.substring(index_date-2, index_date); //hour
                date_only_time2 = date_with_time.substring(index_date+1, index_date+3 ); //minutes
            }
            item.mDate = date_only_time1 +"."+date_only_time2;
            Log.w("DATA", item.mDate);
            item.mHappy_score = i.mHappy_score;
            graphItemList.add(item);
        }
    }
}

