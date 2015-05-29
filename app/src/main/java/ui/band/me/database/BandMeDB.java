package ui.band.me.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import ui.band.me.extras.Band;
import ui.band.me.extras.Track;


public class BandMeDB extends SQLiteOpenHelper implements Serializable{

    public BandMeDB(Context context) {
        super(context, "BandMev8", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //creating users table
        String userTable = "Create table if not exists User(id INTEGER PRIMARY KEY AUTOINCREMENT, username varchar UNIQUE, avatar varchar, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP);";

        //creating band table
        String bandTable = "Create table if not exists Band(spotify_id varchar PRIMARY KEY, name varchar,followers integer, popularity integer, spotify_uri varchar, image_url varchar, biography text, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP);";

        //creating genre table
        String genreTable = "create table if not exists Genre(id INTEGER PRIMARY KEY AUTOINCREMENT, name varchar UNIQUE, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP);";

        //creating search table
        String searchTable = "create table if not exists Search(id INTEGER PRIMARY KEY AUTOINCREMENT, information varchar, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP);";

        //creating share table
        String shareTable ="create table if not exists Share (id INTEGER PRIMARY KEY AUTOINCREMENT, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, band_id INTEGER, user_id INTEGER, FOREIGN KEY(band_id) REFERENCES Band(spotify_id), FOREIGN KEY(user_id) REFERENCES User(id));";

        //creating band_genre table
        String bandGenreTable ="create table if not exists Band_Genre (band_id varchar, genre_id INTEGER, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (band_id) REFERENCES Band(spotify_id), FOREIGN KEY (genre_id) REFERENCES Genre(id), CONSTRAINT band_genre PRIMARY KEY(band_id, genre_id));";

        //creating favourite table
        String favouriteTable ="create table if not exists Favourite (band_id varchar, user_id INTEGER, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY(band_id) REFERENCES Band(spotify_id), FOREIGN KEY(user_id) REFERENCES User(id), CONSTRAINT fav_pk PRIMARY KEY(user_id,band_id));";

        //creating song table
        String songTable = "create table if not exists Song ( id INTEGER PRIMARY KEY AUTOINCREMENT,  name varchar,  band_id varchar, image varchar, FOREIGN KEY(band_id) REFERENCES Band(spotify_id) )";

        db.execSQL(userTable);
        db.execSQL(bandTable);
        db.execSQL(genreTable);
        db.execSQL(searchTable);
        db.execSQL(shareTable);
        db.execSQL(bandGenreTable);
        db.execSQL(favouriteTable);
        db.execSQL(songTable);

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
        String songTable = "drop table if exists song";

        db.execSQL(userTable);
        db.execSQL(bandTable);
        db.execSQL(genreTable);
        db.execSQL(searchTable);
        db.execSQL(shareTable);
        db.execSQL(bandGenreTable);
        db.execSQL(favouriteTable);
        db.execSQL(songTable);

        onCreate(db);
    }

    public void insertSearch(String search) {
        //get a writable database
        SQLiteDatabase database = this.getWritableDatabase();

        search = search.replaceAll("\'","");

        Log.d("Search saved: ", search);

        if(!itemExistsInDb(database, "Search", "information", search)){
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
        band.setName(band.getName().replaceAll("\'",""));

        if(!itemExistsInDb(database, "Band", "name", band.getName())) {
            //using content values in order to easily insert into the SQLite database
            ContentValues values = new ContentValues();

            //creating timestamp
            java.util.Date date= new java.util.Date();

            // insert values
            values.put("name", band.getName());
            values.put("spotify_id",band.getId());
            values.put("followers",band.getFollowers());
            values.put("spotify_uri",band.getUri());
            values.put("popularity",band.getPopularity());
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

    public ArrayList<Band> getBandsByName(String name) {

        name = name.replaceAll("\'","");
        if(name.length()>0)
            if(name.charAt(name.length()-1) == ' ') {
                name = name.substring(0,name.length()-1);
            }

        ArrayList<Band> bandArrayList = new ArrayList<Band>();

        String selectQuery = "SELECT * FROM Band where name like " + "'%" + name + "%' COLLATE NOCASE";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Band currentBand = new Band();
                currentBand.setName(cursor.getString(1));
                currentBand.setFollowers(Integer.valueOf(cursor.getString(2)));
                currentBand.setPopularity(Integer.valueOf(cursor.getString(3)));
                currentBand.setUri(cursor.getString(4));
                currentBand.setId(cursor.getString(0));
                currentBand.setImageLink(cursor.getString(5));
                bandArrayList.add(currentBand);
            } while (cursor.moveToNext());
        }
        return bandArrayList;
    }

    public void insertTracks(String bandId, ArrayList<Track> topTracks) {
        //get a writable database
        SQLiteDatabase database = this.getWritableDatabase();

        for(int i=0;i<topTracks.size();i++) {
            //using content values in order to easily insert into the SQLite database
            ContentValues values = new ContentValues();
            topTracks.get(i).setName(topTracks.get(i).getName().replaceAll("\'",""));
            values.put("name", topTracks.get(i).getName());
            values.put("band_id",bandId);
            values.put("image",topTracks.get(i).getAlbum_image_url());

            //insert into the database and release the resources
            try{
                database.insert("Song", null , values);
                System.out.println(values.get("band_id").toString() + " " + values.get("name").toString());
            }catch(SQLiteConstraintException e){
                database.update("Song", values,"name" +" = '"+ values.get("name") + "'", null);
                Log.d("updated",".");
            }
        }
        database.close();

    }

    public ArrayList<Track> getTracksFromBand(String bandId) {
        SQLiteDatabase database = this.getWritableDatabase();

        ArrayList<Track> trackList = new ArrayList<Track>();

        String selectQuery = "SELECT * FROM SONG WHERE band_id = " +  "'" + bandId + "'";

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Track currentTrack = new Track();
                currentTrack.setName(cursor.getString(1));
                currentTrack.setAlbum_image_url(cursor.getString(3));
                trackList.add(currentTrack);
                System.out.println("Current track:" + currentTrack.getName());
            } while (cursor.moveToNext());
        }

        System.out.println("Get tracks");
        for(int i=0;i<trackList.size();i++)
        {
            System.out.println(trackList.get(i).getName());
        }
        return trackList;
    }

    private boolean itemExistsInDb(SQLiteDatabase database, String table, String param, String value) {
        value=value.replaceAll("\'","");

        String selectQuery = "SELECT * FROM "+ table + "  where " + param + " = " + "'" + value + "'";

        Cursor cursor = database.rawQuery(selectQuery, null);

        return cursor.moveToFirst();

    }

    public void insertFavourite(String bandId) {
        //get a writable database
        SQLiteDatabase database = this.getWritableDatabase();

        //using content values in order to easily insert into the SQLite database
        ContentValues values = new ContentValues();

        //creating timestamp
        java.util.Date date= new java.util.Date();
        // insert values
        values.put("band_id", bandId);
        values.put("user_id", 1);
        values.put("timestamp",new Timestamp(date.getTime()).toString());

        //insert into the database and release the resources
        try{
            database.insert("Favourite", null , values);
            Log.d("Favourite saved",values.get("band_id").toString());
        }catch(SQLiteConstraintException e){
            database.update("Favourite", values,"band_id" +" = '"+ values.get("band_id") + "'", null);
            Log.d("updated",".");
        }

        database.close();
    }

    public boolean existsFavourite(String bandId) {
        String selectQuery = "SELECT * FROM FAVOURITE WHERE band_id = " +  "'" + bandId + "'";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String banda_id = cursor.getString(0);
                Log.d("favourited",banda_id);
            } while (cursor.moveToNext());
        }
        return cursor.moveToFirst();
    }

    public void removeFavourite(String bandId) {

        SQLiteDatabase database = this.getWritableDatabase();
        if(database.delete("Favourite", "band_id="+"'" + bandId + "'", null)>0) {
        }
    }
}
