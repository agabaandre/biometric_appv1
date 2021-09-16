package ug.app.ihrisbiometric.extra;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseHandler extends SQLiteOpenHelper{

    public DatabaseHandler(Context context) {
        super(context, "ihrisbiometric.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String clockQuery, enrollQuery;

        clockQuery = "CREATE TABLE clock (fingerprint TEXT, timestamp TEXT, facilityId TEXT, updatestatus TEXT)";
        enrollQuery = "CREATE TABLE enroll (ihrispid  PRIMARY KEY, fingerprint TEXT, facilityId TEXT, updatestatus TEXT)";
        db.execSQL(clockQuery);
        db.execSQL(enrollQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String clockQuery, enrollQuery;
        clockQuery = "DROP TABLE IF EXISTS clock";
        enrollQuery = "DROP TABLE IF EXISTS enroll";
        db.execSQL(clockQuery);
        db.execSQL(enrollQuery);
        onCreate(db);
    }

    public void insertClockFingerprint(HashMap<String, String> queryValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("fingerprint", queryValues.get("fingerprint"));
        values.put("timestamp", queryValues.get("timestamp"));
        values.put("facilityId", queryValues.get("facilityId"));
        values.put("updatestatus", "no");
        db.insert("clock", null, values);
        db.close();
    }

    public void insertEnrollFingerprint(HashMap<String, String> queryValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("ihrispid", queryValues.get("ihrispid"));
        values.put("fingerprint", queryValues.get("fingerprint"));
        values.put("facilityId", queryValues.get("facilityId"));
        values.put("updatestatus", "no");
        db.insert("enroll", null, values);
        db.close();
    }

    public ArrayList<HashMap<String, String>> getAllClocks() {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM clock";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("fingerprint", cursor.getString(0));
                map.put("timestamp", cursor.getString(1));
                map.put("facilityId", cursor.getString(2));
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return wordList;
    }

    public ArrayList<HashMap<String, String>> getAllEnroll() {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM enroll";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("ihrispid", cursor.getString(0));
                map.put("fingerprint", cursor.getString(1));
                map.put("facilityId", cursor.getString(2));
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return wordList;
    }

    public String composeClockJSONfromSQLite(){
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM clock where updatestatus = '"+"no"+"'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("fingerprint", cursor.getString(0));
                map.put("timestamp", cursor.getString(1));
                map.put("facilityId", cursor.getString(2));
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        Gson gson = new GsonBuilder().create();
        return gson.toJson(wordList);
    }

    public String composeEnrollSONfromSQLite(){
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM enroll where updatestatus = '"+"no"+"'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("ihrispid", cursor.getString(0));
                map.put("fingerprint", cursor.getString(1));
                map.put("facilityId", cursor.getString(2));
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        Gson gson = new GsonBuilder().create();
        return gson.toJson(wordList);
    }

//    public int getClockCount() {
//        SQLiteDatabase database = this.getReadableDatabase();
//        String query = "SELECT count(*) FROM clock WHERE ihrispid = ?";
//        Cursor cursor = database.rawQuery(query, new String[] {name});
//    }

    public String getClockSyncStatus(){
        String msg = null;
        if(this.clockSyncCount() == 0){
            msg = "Time log records are in Sync!";
        }else{
            msg = "Sync time log records\n";
        }
        return msg;
    }

    public String getEnrollSyncStatus(){
        String msg = null;
        if(this.enrollSyncCount() == 0){
            msg = "Enroll records in sync!";
        }else{
            msg = "Sync enroll records\n";
        }
        return msg;
    }

    public int clockSyncCount(){
        int count = 0;
        String selectQuery = "SELECT  * FROM clock where updatestatus = '"+"no"+"'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        count = cursor.getCount();
        database.close();
        return count;
    }

    public int enrollSyncCount(){
        int count = 0;
        String selectQuery = "SELECT  * FROM enroll where updatestatus = '"+"no"+"'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        count = cursor.getCount();
        database.close();
        return count;
    }

    public void updateClockSyncStatus(String fingerprint, String status){
        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "Update clock set updatestatus = '"+ status +"' where fingerprint="+"'"+ fingerprint +"'";
        Log.d("query",updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }

    public void updateEnrollSyncStatus(String id, String status){
        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "Update enroll set updatestatus = '"+ status +"' where fingerprint="+"'"+ id +"'";
        Log.d("query",updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }

    public void dropDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        String clockQuery, enrollQuery;
        clockQuery = "DROP TABLE IF EXISTS clock";
        enrollQuery = "DROP TABLE IF EXISTS enroll";
        db.execSQL(clockQuery);
        db.execSQL(enrollQuery);
        onCreate(db);
    }
}
