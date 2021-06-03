package calendar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Instances;
import android.util.Log;
import android.widget.TextView;

import com.example.emotiontracker.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Events_ranking extends AppCompatActivity {


    public static final String[] INSTANCE_PROJECTION = new String[] {
            Instances.EVENT_ID,      // 0
            Instances.BEGIN,         // 1
            Instances.TITLE          // 2
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_ranking);
        TextView test = findViewById(R.id.test);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        Log.w("Date -1",String.valueOf(day));
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        Calendar start = Calendar.getInstance();
        start.set(year, month , day, 0 , 0);
        long startMillis = start.getTimeInMillis();
        Log.w("Start time",String.valueOf(startMillis));
        test.setText(String.valueOf(startMillis));
        Log.w("daystart" , String.valueOf(start.get(Calendar.DAY_OF_MONTH)));
        Log.w("daystart" , String.valueOf(start.get(Calendar.MONTH)));
        Log.w("daystart" , String.valueOf(start.get(Calendar.YEAR)));
        Calendar end = Calendar.getInstance();
        end.set(year, month , day, 23 , 59);
        long endMillis = end.getTimeInMillis();
        Log.w("End time",String.valueOf(endMillis));
        Log.w("dayend" , String.valueOf(end.get(Calendar.DAY_OF_MONTH)));
        Log.w("dayend" , String.valueOf(end.get(Calendar.MONTH)));
        Log.w("dayend" , String.valueOf(end.get(Calendar.YEAR)));
        Cursor cur = null;
        ContentResolver cr = getContentResolver();
        try {
            Uri.Builder builder = Instances.CONTENT_URI.buildUpon();
            ContentUris.appendId(builder, startMillis);
            ContentUris.appendId(builder, endMillis);

            String[] INSTANCE_PROJECTION = new String[]{Instances.EVENT_ID,
                    Instances.TITLE, Instances.BEGIN, Instances.END};

            Uri uri = builder.build();
            cur = cr.query(uri, INSTANCE_PROJECTION, null, null,null);

            if (cur != null) {
                while (cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(Instances.EVENT_ID));
                    String title = cur.getString(cur.getColumnIndex(Instances.TITLE));
                    Log.w("eveniment" , title);
                }
            }
        } catch (SecurityException e) {
            // no permission to read calendars
        } finally {
            if (cur != null)
                cur.close();
        }
    }
}
