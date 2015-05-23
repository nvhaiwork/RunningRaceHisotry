package com.runningracehisotry.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by QuyNguyen on 5/23/2015.
 */
public class HistoryConversation  extends SQLiteOpenHelper {

    private static final String TAG                  = "HistoryConversation";
public static final String  DATABASE_FILE_PATH      = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String  DATABASE_PARENT_FOLDER = "com.runningracehisotry";
public static final String  DATABASE_NAME      = "Running";
public static final String  CONVERSATION_TABLE        = "Conversation";
public static final String  SENDER_ID       = "sender_id";
    public static final String  TEXT_CONTENT      = "text_content";

    public static final String CONVERSATION_TABLE_CREATE     = "create table "
        + CONVERSATION_TABLE + " (" + SENDER_ID + " integer, " + TEXT_CONTENT + " text);";

    protected static final int DATABASE_VERSION = 1;



private SQLiteDatabase database;

    private static HistoryConversation mInstance;

       /* public HistoryConversation()
        {
            try
            {
                *//*database = SQLiteDatabase.openDatabase(DATABASE_FILE_PATH + File.separator
                        + DATABASE_PARENT_FOLDER + File.separator + DATABASE_NAME, null,SQLiteDatabase.OPEN_READWRITE);*//*
                database = SQLiteDatabase.openOrCreateDatabase(DATABASE_FILE_PATH + File.separator
                        + DATABASE_PARENT_FOLDER + File.separator + DATABASE_NAME, null);
            }
            catch (SQLiteException se)
            {
                Log.e(TAG, "error -- " + se.getMessage(), se);
                // error means tables does not exits
                createTables();
            }
            catch (Exception ex)
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
        }*/

        private void createTables()
        {
            database.execSQL(CONVERSATION_TABLE_CREATE);

        }

        public void close()
        {
            try{
                if(database !=null && database.isOpen()){
                    database.close();
                }
            }
            catch(Exception e){

            }
            //DBUtil.safeCloseDataBase(database);
        }

    private HistoryConversation(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        // NOTE I prefix the full path of my files directory to 'name'
        /*File myFilesDir = null;
        try{
            myFilesDir = new File(DATABASE_FILE_PATH + File.separator
                    + DATABASE_PARENT_FOLDER);
            myFilesDir.mkdirs();
        }
        catch(Exception e){
            e.printStackTrace();
        }*/

        super(context, DATABASE_FILE_PATH + File.separator
                + DATABASE_PARENT_FOLDER + File.pathSeparator + DATABASE_NAME, factory, version);

    }

    /*public static String getDatabasePath(String name){
        return DATABASE_FILE_PATH + File.separator
                + DATABASE_PARENT_FOLDER + File.pathSeparator + name;
    }*/
    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables();
    }

    /*private HistoryConversation(Context context, String dbName) {
        super(context, dbName, null, DATABASE_VERSION);
        Log.e(TAG, "CREATE DB NAME FOR USER: " + dbName);
    }*/

    public static synchronized HistoryConversation getInstance(Context context, String dbName) {
        if (mInstance == null) {
            Log.e(TAG, "DB ADAPTER NULL, creat DB NAME and set value: " + dbName);
            mInstance = new HistoryConversation(context, dbName, null, DATABASE_VERSION);
            //database = dbName;
        }

        return mInstance;
    }

   /* @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode,
                                               SQLiteDatabase.CursorFactory factory) {
        return super.openOrCreateDatabase(getDatabasePath(name).getAbsolutePath(), mode, factory);
    }*/

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public SQLiteDatabase getReadableDatabase()
        {
            /*database = SQLiteDatabase.openDatabase(DATABASE_FILE_PATH + File.separator
                            + DATABASE_PARENT_FOLDER + File.separator + DATABASE_NAME, null,
                    SQLiteDatabase.OPEN_READONLY);*/
            database = getReadableDatabase();
            return database;
        }

        public SQLiteDatabase getWritableDatabase()
        {
            /*database = SQLiteDatabase.openDatabase(DATABASE_FILE_PATH + File.separator
                            + DATABASE_PARENT_FOLDER + File.separator + DATABASE_NAME, null,
                    SQLiteDatabase.OPEN_READWRITE);*/
            database = getWritableDatabase();
            return database;
        }

    public List<Message> getMessageFromUserId(int userId, int friendId){
        List<Message> list = new ArrayList<Message>();
        try{
            getReadableDatabase();
			Cursor cursor = database.query(CONVERSATION_TABLE,
				new String[] { SENDER_ID, TEXT_CONTENT },
				SENDER_ID + "=? OR " + SENDER_ID + "=?", new String[] { String.valueOf(userId), String.valueOf(friendId) },
				null, null, null, null);
		if (cursor != null) {
			// move to first row
			cursor.moveToFirst();
			list = new ArrayList<Message>();
			Message unread = null;
			// iterate if remain
			while (cursor.isAfterLast() == false) {
				unread = new Message(String.valueOf(cursor.getInt(0)), cursor.getString(1));
				list.add(unread);
				unread = null;
				cursor.moveToNext();
			}
			cursor.close();
		}
		close();
        }
        catch (Exception e){

        }
        return list;
    }


    public long addMessage(String userId, String content){
        long result = 0;
		// set values to insert
		ContentValues values = new ContentValues();
		values.put(SENDER_ID, userId);
		values.put(TEXT_CONTENT, content);
		// Inserting Row		
		try {
			Log.d(TAG, "Add message OPEN");
            //database = getWritableDatabase();
            getWritableDatabase();
			result = database.insert(CONVERSATION_TABLE, null, values);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d(TAG, "Add failed: ");
			// e.printStackTrace();
		}
		finally{			
			close();
		}

		// Log.d(Constant.FUNNY_CHAT, "Close add user: " + result);
		return result;
        
    }
}
