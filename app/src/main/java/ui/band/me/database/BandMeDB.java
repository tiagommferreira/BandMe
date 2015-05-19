package ui.band.me.database;


import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BandMeDB extends SQLiteOpenHelper{


    public BandMeDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public BandMeDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //creating users table
        String userTable = "Create table if not exists User(id INTEGER PRIMARY KEY AUTOINCREMENT, username varchar UNIQUE, avatar varchar, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP);";

        //creating band table
        String bandTable = "Create table if not exists Band(id INTEGER PRIMARY KEY AUTOINCREMENT, name varchar,followers integer, popularity integer, spotify_uri varchar, image_url varchar, biography text, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP);";

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
}
