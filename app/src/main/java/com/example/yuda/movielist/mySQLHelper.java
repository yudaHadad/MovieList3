package com.example.yuda.movielist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by yuda on 06/02/2017.
 */

    public class mySQLHelper extends SQLiteOpenHelper {

        Context context;

        public mySQLHelper(Context context) {
            super(context, "movie.db", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            // make a new table on data base
            String SQLCreate = "CREATE TABLE "+DBconstants.tableName+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + DBconstants.titleColumn+" TEXT,  " + DBconstants.descColumn + " TEXT, " + DBconstants.posterColumn
                    +" TEXT, "+ DBconstants.isWatchedColumn+" INTEGER " + DBconstants.ratingColumn+ " INTEGER )";
            db.execSQL(SQLCreate);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
}
