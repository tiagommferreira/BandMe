package ui.band.me.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import ui.band.me.activities.MainActivity;
import ui.band.me.extras.Band;


public class BandMeDB extends SQLiteOpenHelper implements Serializable{

    public BandMeDB(Context context) {
        super(context, "BandMev2", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //creating users table
        String userTable = "Create table if not exists User(id INTEGER PRIMARY KEY AUTOINCREMENT, username varchar UNIQUE, avatar varchar, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP);";

        //creating band table
        String bandTable = "Create table if not exists Band(id INTEGER PRIMARY KEY AUTOINCREMENT, name varchar UNIQUE,followers integer, popularity integer, spotify_uri varchar, image_url varchar, biography text, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP);";

        //creating genre table
        String genreTable = "create table if not exists Genre(id INTEGER PRIMARY KEY AUTOINCREMENT, name varchar UNIQUE, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP);";

        //creating search table
        String searchTable = "create table if not exists Search(id INTEGER PRIMARY KEY AUTOINCREMENT, information varchar, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP);";

        //creating share table
        String shareTable ="create table if not exists Share (id INTEGER PRIMARY KEY AUTOINCREMENT, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, band_id INTEGER, user_id INTEGER, FOREIGN KEY(band_id) REFERENCES Band(id), FOREIGN KEY(user_id) REFERENCES User(id));";

        //creating band_genre table
        String bandGenreTable ="create table if not exists Band_Genre (band_id INTEGER, genre_id INTEGER, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (band_id) REFERENCES Band(id), FOREIGN KEY (genre_id) REFERENCES Genre(id), CONSTRAINT band_genre PRIMARY KEY(band_id, genre_id));";

        //creating favourite table
        String favouriteTable ="create table if not exists Favourite (band_id INTEGER, user_id INTEGER, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY(band_id) REFERENCES Band(id), FOREIGN KEY(user_id) REFERENCES User(id), CONSTRAINT fav_pk PRIMARY KEY(user_id,band_id));";

        db.execSQL(userTable);
        db.execSQL(bandTable);
        db.execSQL(genreTable);
        db.execSQL(searchTable);
        db.execSQL(shareTable);
        db.execSQL(bandGenreTable);
        db.execSQL(favouriteTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String userTable = "drop table if exists user";
        String bandTable = "drop table if exists band";
        String genreTable = "drop table if exists genre";
        String searchTable = "drop table if exists search";
        String shareTable = "drop table if exists share";
        String bandGenreTable = "drop table if exists band_genre";
        String favouriteTable = "drop table if exists favourite";

        db.execSQL(userTable);
        db.execSQL(bandTable);
        db.execSQL(genreTable);
        db.execSQL(searchTable);
        db.execSQL(shareTable);
        db.execSQL(bandGenreTable);
        db.execSQL(favouriteTable);

        onCreate(db);
    }

    public void insertSearch(String search) {
        //get a writable database
        SQLiteDatabase database = this.getWritableDatabase();

        //using content values in order to easily insert into the SQLite database
        ContentValues values = new ContentValues();

        //creating timestamp
        java.util.Date date= new java.util.Date();

        // insert values
        values.put("information", search);
        values.put("timestamp",new Timestamp(date.getTime()).toString());

        //insert into the database and release the resources
        try{
            database.insert("Search", null, values);
            Log.d("Search saved",values.get("information").toString());
        }catch(SQLiteConstraintException e){
            e.printStackTrace();
        }
        database.close();
    }

    public ArrayList<HashMap<String, String>> getAllSearches() {

        ArrayList<HashMap<String, String>> searchesArrayList = new ArrayList<HashMap<String, String>>();

        String selectQuery = "SELECT * FROM Search";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> searchMap = new HashMap<String, String>();
                searchMap.put("information", cursor.getString(1));
                searchesArrayList.add(searchMap);
            } while (cursor.moveToNext());
        }
        return searchesArrayList;
    }

    public void insertBand(Band band) {
        //get a writable database
        SQLiteDatabase database = this.getWritableDatabase();

        //using content values in order to easily insert into the SQLite database
        ContentValues values = new ContentValues();

        //creating timestamp
        java.util.Date date= new java.util.Date();

        // insert values
        values.put("name", band.getName());
        values.put("followers",band.getFollowers());
        values.put("spotify_uri",band.getUri());
        values.put("image_url",band.getImageLink());
        values.put("biography","No biography");
        values.put("timestamp",new Timestamp(date.getTime()).toString());

        //insert into the database and release the resources
        try{
            database.insert("Band", null , values);
            Log.d("Search saved",values.get("name").toString());
        }catch(SQLiteConstraintException e){
            database.update("Band", values,"name" +" = '"+ values.get("name") + "'", null);
            Log.d("updated",".");
        }
        database.close();
    }

    public ArrayList<HashMap<String, String>> getAllBands() {

        ArrayList<HashMap<String, String>> bandArrayList = new ArrayList<HashMap<String, String>>();

        String selectQuery = "SELECT * FROM Band";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> bandMap = new HashMap<String, String>();
                bandMap.put("name", cursor.getString(1));
                bandArrayList.add(bandMap);
            } while (cursor.moveToNext());
        }
        return bandArrayList;
    }
}
