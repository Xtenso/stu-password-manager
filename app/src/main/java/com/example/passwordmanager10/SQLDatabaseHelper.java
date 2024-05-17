package com.example.passwordmanager10;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class SQLDatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "PasswordManager.db";
    private static final int DATABASE_VERSION = 1;

    //Table One - Passwords
    private static final String TABLE_NAME = "Passwords";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_IMAGE = "image_name";
    private static final String COLUMN_DATE = "last_updated";
    private static final String FAVOURITE = "favourite";

    //Table Two - Login
    private static final String TABLE2_NAME = "Login";
    private static final String COLUMN_ID2 = "_id";
    private static final String COLUMN_USERNAME2 = "username";
    private static final String COLUMN_PASSWORD2 = "password";

    public SQLDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_IMAGE + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                FAVOURITE + " TEXT);";
        db.execSQL(query);

        String query2 = "CREATE TABLE " + TABLE2_NAME + " (" + COLUMN_ID2 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME2 + " TEXT, " +  COLUMN_PASSWORD2 + " TEXT);";
        db.execSQL(query2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE2_NAME);
        onCreate(db);
    }

    void addPassword(String name, String username, String password,
                     String image, String date, String favourite){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_USERNAME, username);
        cv.put(COLUMN_PASSWORD, password);
        cv.put(COLUMN_IMAGE, image);
        cv.put(COLUMN_DATE, date);
        cv.put(FAVOURITE, favourite);
        long result = db.insert(TABLE_NAME, null, cv);
        if(result == -1){
            Toast.makeText(context, "Failed to save!", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Saved Successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    void addLoginCredentials(String username, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_USERNAME2, username);
        cv.put(COLUMN_PASSWORD2, password);
        long result = db.insert(TABLE2_NAME, null, cv);
        if(result == -1){
            Toast.makeText(context, "Unsuccessful registration!", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Registered successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    Cursor readData(){
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    //Table 1
    void updateData(String row_id, String name, String username, String password, String image,
                    String date, String favourite){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_USERNAME, username);
        cv.put(COLUMN_PASSWORD, password);
        cv.put(COLUMN_IMAGE, image);
        cv.put(COLUMN_DATE, date);
        cv.put(FAVOURITE, favourite);

        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{row_id});
        if(result == -1){
            Toast.makeText(context, "Failed to update!", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Successfully updated!!", Toast.LENGTH_SHORT).show();
        }
    }

    void deleteRow(String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, "_id=?", new String[]{row_id});
        if(result == -1){
            Toast.makeText(context, "Failed to delete!", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Successfully deleted!", Toast.LENGTH_SHORT).show();
        }
    }

    void deleteAllData(){
        SQLiteDatabase db =  this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }

    //Table 2
    public Boolean checkUsername(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE2_NAME + " WHERE " + COLUMN_USERNAME2 + " = ? ", new String[] {username});
        if(cursor.getCount() > 0){
            return true;
        }else {
            return false;
        }
    }

    public Boolean checkUserAndPassword(String username, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE2_NAME + " WHERE " + COLUMN_USERNAME2 + " = ? " +
                "AND " + COLUMN_PASSWORD2 + " = ?", new String[] {username, password});
        if(cursor.getCount() > 0){
            return true;
        }else {
            return false;
        }
    }

    public Boolean firstTime() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE2_NAME , new String[]{});
        if(cursor.getCount() == 0){
            return true;
        }else {
            return false;
        }
    }
}
