package com.bencorp.papp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hp-pc on 1/9/2019.
 */

public class SqliteHandler extends SQLiteOpenHelper {
    private final static int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "papp.db";
    private static final String TABLE = "user";
    private static final String COLUMN_CUSTOMER_ID = "user_id";
    private static final String COLUMN_CUSTOMER_UNIQUEID = "user_uid";
    private static final String COLUMN_CUSTOMER_NAME = "user_name";
    public static final String COLUMN_EMAIL= "user_email";
    public static final String COLUMN_PHONE_1 = "user_phone_1";
    public static final String COLUMN_PHONE_2 = "user_phone_2";
    public static final String COLUMN_SERVICES = "user_services";
    public static final String COLUMN_ADDRESS = "user_address";
    public static final String COLUMN_COUNTRY = "user_country";
    public static final String COLUMN_STATE = "user_state";
    public static final String COLUMN_DATE = "user_date";

    private String CREATE_JOB_TABLE = "CREATE TABLE " + TABLE + " (" +
            COLUMN_CUSTOMER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_CUSTOMER_UNIQUEID + " TEXT," +
            COLUMN_CUSTOMER_NAME + " TEXT," +
            COLUMN_EMAIL + " TEXT," +
            COLUMN_DATE + " TEXT," +
            COLUMN_PHONE_1 + " TEXT," +
            COLUMN_SERVICES + " TEXT," +
            COLUMN_ADDRESS + " TEXT," +
            COLUMN_COUNTRY + " TEXT," +
            COLUMN_STATE + " TEXT," +
            COLUMN_PHONE_2 + " TEXT )";
    private String DROP_JOB_TABLE = "DROP TABLE IF EXISTS " + TABLE;

    public SqliteHandler(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_JOB_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_JOB_TABLE);
        onCreate(db);
    }
    public Boolean addUser(String uniqueID,String[] details,String[] locationDetails,String date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL,details[0]);
        values.put(COLUMN_CUSTOMER_NAME,details[1]);
        values.put(COLUMN_PHONE_1,details[2]);
        values.put(COLUMN_PHONE_2,details[3]);
        values.put(COLUMN_SERVICES,details[4]);
        values.put(COLUMN_ADDRESS,details[5]);
        values.put(COLUMN_COUNTRY,locationDetails[0]);
        values.put(COLUMN_STATE,locationDetails[1]);
        values.put(COLUMN_CUSTOMER_UNIQUEID,uniqueID);
        values.put(COLUMN_DATE,date);
        long response = db.insert(TABLE,null,values);
        if(response == -1){
            return  false;
        }
        db.close();
        return true;

    }
    public Integer getUser(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM "+TABLE,null);
        return result.getCount();
    }
}
