package com.runningracehisotry.models;

import android.content.ContentValues;
import android.database.Cursor;
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
                if(database !=null && database.isOpen()){
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

    public List<Message> getMessageFromUserId(int userId, int friendId){
        List<Message> list = new ArrayList<Message>();
        try{
			Cursor cursor = getReadableDatabase().query(CONVERSATION_TABLE,
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


    public long addMessage(int userId, String content){
        long result = 0;
		// set values to insert
		ContentValues values = new ContentValues();
		values.put(SENDER_ID, userId);
		values.put(TEXT_CONTENT, content);
		// Inserting Row		
		try {
			Log.d(TAG, "Add message OPEN");
			result = getWritableDatabase().insert(CONVERSATION_TABLE, null, values);
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
