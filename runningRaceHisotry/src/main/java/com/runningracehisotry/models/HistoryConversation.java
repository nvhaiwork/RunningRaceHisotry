package com.runningracehisotry.models;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by QuyNguyen on 5/23/2015.
 */
public class HistoryConversation{

    private static final String TAG                  = "HistoryConversation";
public static final String  DATABASE_FILE_PATH      = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String  DATABASE_PARENT_FOLDER = "com.runningracehisotry";
public static final String  DATABASE_NAME      = "Running";
public static final String  CONVERSATION_TABLE        = "Conversation";
public static final String  SENDER_ID       = "sender_id";
    public static final String  TEXT_CONTENT      = "text_content";

private static final String CONVERSATION_TABLE_CREATE     = "create table "
        + CONVERSATION_TABLE + " (" + SENDER_ID + " integer, " + TEXT_CONTENT + " text);";



private SQLiteDatabase database;

        public HistoryConversation()
        {
            try
            {
                database = SQLiteDatabase.openDatabase(DATABASE_FILE_PATH
                        + File.separator + DATABASE_NAME, null,SQLiteDatabase.OPEN_READWRITE);
            }
            catch (SQLiteException ex)
            {
                Log.e(TAG, "error -- " + ex.getMessage(), ex);
                // error means tables does not exits
                createTables();
            }
            finally
            {
                //close DB
                //DBUtil.safeCloseDataBase(database);
            }
        }

        private void createTables()
        {
            database.execSQL(CONVERSATION_TABLE);

        }

        public void close()
        {
            try{
                if(database.isOpen()){
                    database.close();
                }
            }
            catch(Exception e){

            }
            //DBUtil.safeCloseDataBase(database);
        }

        public SQLiteDatabase getReadableDatabase()
        {
            database = SQLiteDatabase.openDatabase(DATABASE_FILE_PATH + File.separator
                            + DATABASE_PARENT_FOLDER + File.separator + DATABASE_NAME, null,
                    SQLiteDatabase.OPEN_READONLY);
            return database;
        }

        public SQLiteDatabase getWritableDatabase()
        {
            database = SQLiteDatabase.openDatabase(DATABASE_FILE_PATH + File.separator
                            + DATABASE_PARENT_FOLDER + File.separator + DATABASE_NAME, null,
                    SQLiteDatabase.OPEN_READWRITE);
            return database;
        }

    public List<Message> getMessageFromUserId(int userId, int friendID){
        List<Message> list = new ArrayList<Message>();
        try{

        }
        catch (Exception e){

        }
        return list;
    }


    public int addMessage(int userId, String content){
        int result = 0 ;
        try{

        }
        catch (Exception e){

        }
        return result;
    }
}
