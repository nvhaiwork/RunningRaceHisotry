package com.runningracehisotry.adapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.runningracehisotry.RunningRaceApplication;

/**
 * Created by ngocm on 05/23/15.
 */
public class MessageDatabaseController extends SQLiteOpenHelper {
    private static MessageDatabaseController instance;
    public static final String DB_NAME = "MessageDB";

    public static MessageDatabaseController getInstance() {
        if(instance == null) {
            instance = new MessageDatabaseController();
        }

        return instance;
    }

    public MessageDatabaseController() {
        super(RunningRaceApplication.getInstance(), DB_NAME, null, 0);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        final String
//        db.execSQL();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
