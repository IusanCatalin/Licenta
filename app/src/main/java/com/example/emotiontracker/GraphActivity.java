package com.example.emotiontracker;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class GraphActivity extends AppCompatActivity {

    protected androidx.appcompat.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        BarChart barChart = findViewById(R.id.bar_chart);
        ArrayList<BarEntry> array_data = new ArrayList<>();
        for( DatabaseItemModel i : HistoryActivity.graphItemList)
        {
            array_data.add(new BarEntry(Float.parseFloat(i.mDate),(int) i.mHappy_score));
        }
        BarDataSet barDataSet = new BarDataSet(array_data, "Happiness");
        barDataSet.setColors(getResources().getColor(R.color.dark_orange));
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(9f);
        barDataSet.setValueFormatter(new MyValueFormatter());
        BarData barData = new BarData(barDataSet);
        barData.setValueFormatter(new MyValueFormatter());
        barData.setBarWidth(0.03f);
        barChart.setData(barData);
        barChart.animateY(2000);
        barChart.getDescription().setText("Happiness Chart");
        setupUIViews();
        initToolbar();
    }

    private class MyValueFormatter implements IValueFormatter {

        private DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("###,###,##0"); //  no decimal
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return mFormat.format(value); // here we can add whatever we want
        }
    }
    public void setupUIViews()
    {
        toolbar = findViewById(R.id.history_toolbar);
    }

    private void initToolbar(){
        //setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Graphs");
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
}
