package com.example.emotiontracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ResultsDatabase";
    private static final String TABLE_RESULT_SUMMARY = "ResultsSummary";
    private static final String ID = "id";
    private static final String HAPPY_SCORE = "happy_score";
    private static final String CREATION_DATE = "creationdate"; //Date format is mm/dd/yyyy
    private static final String CREATE_TABLE_RESULTS_SUMMARY = "CREATE TABLE " + TABLE_RESULT_SUMMARY + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + CREATION_DATE + " TEXT," + HAPPY_SCORE + " INTEGER" + ")";
    private static final String DELETE_TABLE_ON_UPGRADE = "DROP TABLE IF EXISTS " + TABLE_RESULT_SUMMARY;
    //events table
    private static final String TABLE_EVENTS = "Events";
    private static final String EVENT_TITLE = "event_title";
    private static final String EVENT_SCORE = "event_score";
    private static final String EVENT_TIMES = "event_times";
    private static final String CREATE_TABLE_EVENTS = "CREATE TABLE " + TABLE_EVENTS + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + EVENT_TITLE + " TEXT UNIQUE," + EVENT_SCORE + " INTEGER," + EVENT_TIMES + " INTEGER" + ")";
    private static final String DELETE_TABLE_ON_UPGRADE2 = "DROP TABLE IF EXISTS " + TABLE_EVENTS;
    public static String[] monthNames = new String[12];
    public static String[] days = new String[32];
    public static String[] hours = new String[26];
    public static String[] minutes = new String[62];

    Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_RESULTS_SUMMARY);
        db.execSQL(CREATE_TABLE_EVENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_TABLE_ON_UPGRADE);
        db.execSQL(DELETE_TABLE_ON_UPGRADE2);
        onCreate(db);
    }

    @SuppressWarnings({"TooBroadScope", "UnnecessaryLocalVariable", "WeakerAccess"})
    protected boolean createEntry(int happiness) {

        boolean createSuccessful = false;
        int currentDateHappyScore = happiness;
        Calendar mCalendar = Calendar.getInstance();
        setMonths();
        setDays();
        setHours();
        setMinutes();
        String minute = minutes[mCalendar.get(Calendar.MINUTE)];
        String hour = hours[mCalendar.get(Calendar.HOUR_OF_DAY)];
        String month = monthNames[mCalendar.get(Calendar.MONTH)];
        String day = days[mCalendar.get(Calendar.DAY_OF_MONTH)];
        String todayDate = String.valueOf(mCalendar.get(Calendar.YEAR) + "/" + String.valueOf(month) + "/" + String.valueOf(day) + " Time " + String.valueOf(hour) + ":" + String.valueOf(minute));
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(CREATION_DATE, todayDate);
            values.put(HAPPY_SCORE, currentDateHappyScore);
            long row = db.insert(TABLE_RESULT_SUMMARY, null, values);
            if (row != -1) {
                createSuccessful = true;
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return createSuccessful;
    }

    protected ArrayList<DatabaseItemModel> readEntries() {
        setMinutes();
        setHours();
        setDays();
        setMonths();
        ArrayList<DatabaseItemModel> ItemsList = new ArrayList<DatabaseItemModel>();
        String selectQuery = "SELECT * FROM " + TABLE_RESULT_SUMMARY;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                    DatabaseItemModel mDatabaseItemModel = new DatabaseItemModel();
                    mDatabaseItemModel.mDate = c.getString((c.getColumnIndex(CREATION_DATE)));
                    mDatabaseItemModel.mHappy_score = c.getInt((c.getColumnIndex(HAPPY_SCORE)));
                    ItemsList.add(mDatabaseItemModel);
                } while (c.moveToNext());
            }
            c.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ItemsList;
    }

    protected ArrayList<DatabaseItemModel> readTodayEntries() {
        setMonths();
        setDays();
        Calendar mCalendar = Calendar.getInstance();
        String month = monthNames[mCalendar.get(Calendar.MONTH)];
        ;
        String day = days[mCalendar.get(Calendar.DAY_OF_MONTH)];
        String year = String.valueOf(mCalendar.get(Calendar.YEAR));

        ArrayList<DatabaseItemModel> ItemsList = new ArrayList<DatabaseItemModel>();
        String selectQuery = "SELECT * FROM " + TABLE_RESULT_SUMMARY + " WHERE " + CREATION_DATE + " LIKE " + "'" + year + "/" + month + "/" + day + "%" + "'";
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                    DatabaseItemModel mDatabaseItemModel = new DatabaseItemModel();
                    mDatabaseItemModel.mDate = c.getString((c.getColumnIndex(CREATION_DATE)));
                    mDatabaseItemModel.mHappy_score = c.getInt((c.getColumnIndex(HAPPY_SCORE)));
                    ItemsList.add(mDatabaseItemModel);
                } while (c.moveToNext());
            }
            c.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ItemsList;
    }


    protected ArrayList<DatabaseItemModel> readMonthEntries() {
        setMonths();
        setDays();
        Calendar mCalendar = Calendar.getInstance();
        String day = days[mCalendar.get(Calendar.DAY_OF_MONTH)];
        String month = monthNames[mCalendar.get(Calendar.MONTH)];
        String year = String.valueOf(mCalendar.get(Calendar.YEAR));

        ArrayList<DatabaseItemModel> ItemsList = new ArrayList<DatabaseItemModel>();
        String selectQuery = "SELECT * FROM " + TABLE_RESULT_SUMMARY + " WHERE " + CREATION_DATE + " LIKE " + "'" + "%" + year + "/" + month + "%" + "'";
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                    DatabaseItemModel mDatabaseItemModel = new DatabaseItemModel();
                    mDatabaseItemModel.mDate = c.getString((c.getColumnIndex(CREATION_DATE)));
                    mDatabaseItemModel.mHappy_score = c.getInt((c.getColumnIndex(HAPPY_SCORE)));
                    ItemsList.add(mDatabaseItemModel);
                } while (c.moveToNext());
            }
            c.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ItemsList;
    }

    protected ArrayList<DatabaseItemModel> readYearEntries() {
        setMonths();
        setDays();
        Calendar mCalendar = Calendar.getInstance();
        String day = days[mCalendar.get(Calendar.DAY_OF_MONTH)];
        String month = monthNames[mCalendar.get(Calendar.MONTH)];
        ;
        String year = String.valueOf(mCalendar.get(Calendar.YEAR));

        ArrayList<DatabaseItemModel> ItemsList = new ArrayList<DatabaseItemModel>();
        String selectQuery = "SELECT * FROM " + TABLE_RESULT_SUMMARY + " WHERE " + CREATION_DATE + " LIKE " + "'" + "%" + year + "%" + "'";
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                    DatabaseItemModel mDatabaseItemModel = new DatabaseItemModel();
                    mDatabaseItemModel.mDate = c.getString((c.getColumnIndex(CREATION_DATE)));
                    mDatabaseItemModel.mHappy_score = c.getInt((c.getColumnIndex(HAPPY_SCORE)));
                    ItemsList.add(mDatabaseItemModel);
                } while (c.moveToNext());
            }
            c.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ItemsList;
    }

    protected ArrayList<DatabaseItemModel> readCustomEntries() {
        String first_date = HistoryActivity.mStringDate.data1;
        String second_date = HistoryActivity.mStringDate.data2;
        Log.w("Date1", first_date);
        Log.w("Date2", second_date);
        setMonths();
        setDays();
        Calendar mCalendar = Calendar.getInstance();
        String day = days[mCalendar.get(Calendar.DAY_OF_MONTH)];
        String month = monthNames[mCalendar.get(Calendar.MONTH)];
        ;
        String year = String.valueOf(mCalendar.get(Calendar.YEAR));
        ArrayList<DatabaseItemModel> ItemsList = new ArrayList<DatabaseItemModel>();
        String selectQuery = "SELECT * FROM " + TABLE_RESULT_SUMMARY + " WHERE " + CREATION_DATE + " BETWEEN " + "'" + first_date + "%" + "'" + " AND " + "'" + second_date + "%" + "'";
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                    DatabaseItemModel mDatabaseItemModel = new DatabaseItemModel();
                    mDatabaseItemModel.mDate = c.getString((c.getColumnIndex(CREATION_DATE)));
                    mDatabaseItemModel.mHappy_score = c.getInt((c.getColumnIndex(HAPPY_SCORE)));
                    ItemsList.add(mDatabaseItemModel);
                } while (c.moveToNext());
            }
            c.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ItemsList;
    }

    private void setMonths() {
        //calendar displays months from 0-11 so we fixed it
        monthNames[0] = "01";
        monthNames[1] = "02";
        monthNames[2] = "03";
        monthNames[3] = "04";
        monthNames[4] = "05";
        monthNames[5] = "06";
        monthNames[6] = "07";
        monthNames[7] = "08";
        monthNames[8] = "09";
        monthNames[9] = "10";
        monthNames[10] = "11";
        monthNames[11] = "12";
    }

    private void setHours() {
        //calendar displays hours < 10 as a single digit we want them as two, ex. 8 as 08
        hours[0] = "00";
        hours[1] = "01";
        hours[2] = "02";
        hours[3] = "03";
        hours[4] = "04";
        hours[5] = "05";
        hours[6] = "06";
        hours[7] = "07";
        hours[8] = "08";
        hours[9] = "09";
        hours[10] = "10";
        hours[11] = "11";
        hours[12] = "12";
        hours[13] = "13";
        hours[14] = "14";
        hours[15] = "15";
        hours[16] = "16";
        hours[17] = "17";
        hours[18] = "18";
        hours[19] = "19";
        hours[20] = "20";
        hours[21] = "21";
        hours[22] = "22";
        hours[23] = "23";
        hours[24] = "24";
    }

    private void setDays() {
        //calendar displays days without the 0 for date > 10 ( ex. we want to show 09 not 9)
        days[0] = "00";
        days[1] = "01";
        days[2] = "02";
        days[3] = "03";
        days[4] = "04";
        days[5] = "05";
        days[6] = "06";
        days[7] = "07";
        days[8] = "08";
        days[9] = "09";
        days[10] = "10";
        days[11] = "11";
        days[12] = "12";
        days[13] = "13";
        days[14] = "14";
        days[15] = "15";
        days[16] = "16";
        days[17] = "17";
        days[18] = "18";
        days[19] = "19";
        days[20] = "20";
        days[21] = "21";
        days[22] = "22";
        days[23] = "23";
        days[24] = "24";
        days[25] = "25";
        days[26] = "26";
        days[27] = "27";
        days[28] = "28";
        days[29] = "29";
        days[30] = "30";
        days[31] = "31";
    }

    private void setMinutes() {
        //calendar displays days without the 0 for minutes > 10 ( ex. we want to show 09 not 9)
        minutes[0] = "00";
        minutes[1] = "01";
        minutes[2] = "02";
        minutes[3] = "03";
        minutes[4] = "04";
        minutes[5] = "05";
        minutes[6] = "06";
        minutes[7] = "07";
        minutes[8] = "08";
        minutes[9] = "09";
        minutes[10] = "10";
        minutes[11] = "11";
        minutes[12] = "12";
        minutes[13] = "13";
        minutes[14] = "14";
        minutes[15] = "15";
        minutes[16] = "16";
        minutes[17] = "17";
        minutes[18] = "18";
        minutes[19] = "19";
        minutes[20] = "20";
        minutes[21] = "21";
        minutes[22] = "22";
        minutes[23] = "23";
        minutes[24] = "24";
        minutes[25] = "25";
        minutes[26] = "26";
        minutes[27] = "27";
        minutes[28] = "28";
        minutes[29] = "29";
        minutes[30] = "30";
        minutes[31] = "31";
        minutes[32] = "32";
        minutes[33] = "33";
        minutes[34] = "34";
        minutes[35] = "35";
        minutes[36] = "36";
        minutes[37] = "37";
        minutes[38] = "38";
        minutes[39] = "39";
        minutes[40] = "40";
        minutes[41] = "41";
        minutes[42] = "42";
        minutes[43] = "43";
        minutes[44] = "44";
        minutes[45] = "45";
        minutes[46] = "46";
        minutes[47] = "47";
        minutes[48] = "48";
        minutes[49] = "49";
        minutes[50] = "50";
        minutes[51] = "51";
        minutes[52] = "52";
        minutes[53] = "53";
        minutes[54] = "54";
        minutes[55] = "55";
        minutes[56] = "56";
        minutes[57] = "57";
        minutes[58] = "58";
        minutes[59] = "59";
        minutes[60] = "60";
    }

    @SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
    protected boolean createEntryEvents(String event_title, int happy_score) {

        boolean createSuccessful = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(EVENT_TITLE, event_title);
            values.put(EVENT_SCORE, happy_score);
            values.put(EVENT_TIMES, 1);
            long row = db.insert(TABLE_EVENTS, null, values);
            if (row != -1) {
                createSuccessful = true;
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return createSuccessful;
    }

    protected ArrayList<DatabaseItemEvents> readEventEntries() {
        ArrayList<DatabaseItemEvents> itemsList = new ArrayList<DatabaseItemEvents>();
        String selectQuery = "SELECT * FROM " + TABLE_EVENTS;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                    DatabaseItemEvents mDatabaseItemEvents = new DatabaseItemEvents();
                    mDatabaseItemEvents.title = c.getString((c.getColumnIndex(EVENT_TITLE)));
                    mDatabaseItemEvents.score = c.getInt((c.getColumnIndex(EVENT_SCORE)));
                    mDatabaseItemEvents.times = c.getInt((c.getColumnIndex(EVENT_TIMES)));
                    itemsList.add(mDatabaseItemEvents);
                } while (c.moveToNext());
            }
            c.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemsList;
    }

    protected boolean edit_events(String event_title, int event_score, int event_times) {
        boolean editSuccessful = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(EVENT_SCORE, event_score);
            values.put(EVENT_TIMES, event_times);
            long row = db.update(TABLE_EVENTS, values , "event_title = ?" , new String[]{event_title});
            if (row != -1) {
                editSuccessful = true;
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return editSuccessful;
    }

    protected int getPreviousDayEmotionScore() {
        int score_day = -1;
        int contor = 1;
        boolean first = true;
        setMonths();
        setDays();  //it was working without but only if the user used calculate happiness
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.add(Calendar.DATE, -1);
        String month = monthNames[mCalendar.get(Calendar.MONTH)];
        String day = days[mCalendar.get(Calendar.DAY_OF_MONTH)];
        String year = String.valueOf(mCalendar.get(Calendar.YEAR));
        Log.w("database date", "day is" +day);
        Log.w("database date", "month is" +month);
        Log.w("database date", "year is" +year);
        String selectQuery = "SELECT * FROM " + TABLE_RESULT_SUMMARY + " WHERE " + CREATION_DATE + " LIKE " + "'" + year + "/" + month + "/" + day + "%" + "'";
        try {
            Log.w("trying to read", "from database");
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                    Log.w("inside cursor", "curosr");
                    if(!first)
                        contor = contor + 1;
                    score_day = score_day + c.getInt((c.getColumnIndex(HAPPY_SCORE)));
                    Log.w("previous day scores", String.valueOf(c.getInt((c.getColumnIndex(HAPPY_SCORE)))));
                    first = false;
                } while (c.moveToNext());
            }
            else{
                Log.w("no previous day" ,"events");
            }
            score_day = (score_day+1) / contor;
            c.close();
            db.close();
        } catch (java.lang.ArithmeticException e) {
            e.printStackTrace();
            Log.w("no previous day" ,"events");
        }
        Log.w("score of prev day" , String.valueOf(score_day));
        return score_day;
    }
}
