package com.runningracehisotry.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.runningracehisotry.RunningRaceApplication;
import com.runningracehisotry.constants.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by QuyNguyen on 5/23/2015.
 */
public class HistoryConversation extends SQLiteOpenHelper {

    private static final String TAG = "HistoryConversation";
    /*public static final String DATABASE_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String DATABASE_PARENT_FOLDER = "com.runningracehisotry";
    public static final String DATABASE_NAME = "Running";*/
    public static final String CONVERSATION_TABLE = "ConversationChat";
    public static final String MESSAGE_ID = "message_id";
    public static final String SENDER_ID = "user_id";
    public static final String FRIEND_ID = "friend_id";
    public static final String TEXT_CONTENT = "text_content";
    public static final String SENT_TIME = "long_time";
    public static final String OWNER_ID = "owner_id";

    public static final String NEW_NOTIFICATION_TABLE = "NewNotification";
    public static final String NEW_FRIEND_ID = "FriendIdNew";

    public static final String CONVERSATION_TABLE_CREATE = "CREATE TABLE " + CONVERSATION_TABLE + " ( "
            + MESSAGE_ID + " TEXT PRIMARY KEY , "
            + SENDER_ID + " TEXT , "
            + FRIEND_ID + " TEXT , "
            + TEXT_CONTENT + " TEXT , "
            + SENT_TIME + " INTEGER , "
            + OWNER_ID + " TEXT )";

    public static final String NOTIFICATION_TABLE_CREATE = "CREATE TABLE " + NEW_NOTIFICATION_TABLE + " ( "
            + NEW_FRIEND_ID + " TEXT PRIMARY KEY)";

    protected static final int DATABASE_VERSION = 1;


    private volatile SQLiteDatabase database;
    //private volatile SQLiteDatabase mDb = null;

    private volatile AtomicInteger mReferenceCount = new AtomicInteger(0);

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

        /*private void createTables()
        {
            database.execSQL(CONVERSATION_TABLE_CREATE);

        }*/

    public void close() {
        try {
            if (database != null && database.isOpen()) {
                database.close();
            }
        } catch (Exception e) {

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

        /*super(context, DATABASE_FILE_PATH + File.separator
                + DATABASE_PARENT_FOLDER + File.pathSeparator + name, factory, version);*/

        super(context, name, factory, version);

    }

    /*public static String getDatabasePath(String name){
        return DATABASE_FILE_PATH + File.separator
                + DATABASE_PARENT_FOLDER + File.pathSeparator + name;
    }*/
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(Constants.LOG_TAG, "query: " + CONVERSATION_TABLE_CREATE);
        Log.d(Constants.LOG_TAG, "query: " + NOTIFICATION_TABLE_CREATE);
        db.execSQL(CONVERSATION_TABLE_CREATE);
        db.execSQL(NOTIFICATION_TABLE_CREATE);
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
        db.execSQL("DROP TABLE IF EXISTS " + CONVERSATION_TABLE_CREATE);
        db.execSQL("DROP TABLE IF EXISTS " +NOTIFICATION_TABLE_CREATE);

        onCreate(db);
    }

    public synchronized SQLiteDatabase openDatabase() {
        Log.w(TAG, "Thread OPEN: " + Thread.currentThread().getName());
        mReferenceCount.incrementAndGet();
        if (mReferenceCount.get() >= 1) {
            Log.w(TAG, "DatabaseHandler#openDatabase mReferenceCount = " + mReferenceCount);
            database = this.getWritableDatabase();

        }

        return database;
    }

    /**
     * safe to close database
     */
    public synchronized void closeDatabase() {
        Log.w(TAG, "Thread CLOSE: " + Thread.currentThread().getName());
        mReferenceCount.decrementAndGet();
        Log.w(TAG, "DatabaseHandler#closeDatabase mReferenceCount = " + mReferenceCount);
        if (mReferenceCount.get() <= 0 && database != null) {
            mReferenceCount.set(0);
            Log.w(TAG, "DatabaseHandler#closeDatabase mReferenceCount = " + mReferenceCount);
            database.close();
        }
    }

    public List<Message> getMessageFromUserId(String userId, String friendId) {
        List<Message> list = new ArrayList<Message>();
        try {
            //getReadableDatabase();
            Cursor cursor = openDatabase().query(CONVERSATION_TABLE,
                    new String[]{MESSAGE_ID, SENDER_ID, FRIEND_ID, TEXT_CONTENT, SENT_TIME, OWNER_ID},
                    SENDER_ID + "=? AND " + FRIEND_ID + "=?", new String[]{userId, friendId},
                    null, null, SENT_TIME + " ASC ", null);
            if (cursor != null) {
                // move to first row
                cursor.moveToFirst();
                list = new ArrayList<Message>();
                Message unread = null;
                // iterate if remain
                while (cursor.isAfterLast() == false) {
                    String msgId = cursor.getString(0);
                    String userIdDb = cursor.getString(1);
                    String friendIdDb = cursor.getString(2);
                    String content = cursor.getString(3);
                    long sentTime = cursor.getLong(4);
                    String ownerId = cursor.getString(5);

                    unread = new Message(msgId, userIdDb, friendIdDb, content,sentTime,ownerId);
                    list.add(list.size(), unread);
                    unread = null;
                    cursor.moveToNext();
                }
                cursor.close();
            }
            close();
        } catch (Exception e) {

        }
        return list;
    }


    public long addMessage(Message message) {
        long result = 0;
        // set values to insert
        ContentValues values = new ContentValues();
        values.put(MESSAGE_ID, message.getMessageId());
        values.put(SENDER_ID, message.getUserId());
        values.put(FRIEND_ID, message.getFriendId());
        values.put(TEXT_CONTENT, message.getContent());
        values.put(SENT_TIME, message.getTime());
        values.put(OWNER_ID, message.getOwnerId());
        // Inserting Row
        try {
            Log.d(TAG, "Add message OPEN");
            //database = getWritableDatabase();
            //getWritableDatabase();
            result = openDatabase().insert(CONVERSATION_TABLE, null, values);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d(TAG, "Add failed: ");
            // e.printStackTrace();
        } finally {
            closeDatabase();
        }

        // Log.d(Constant.FUNNY_CHAT, "Close add user: " + result);
        return result;
    }


    public long addFriendNewMessage(String friend) {
        long result = 0;
        // set values to insert
        ContentValues values = new ContentValues();
        values.put(NEW_FRIEND_ID, friend);

        // Inserting Row
        try {
            Log.d(TAG, "Add NEW OPEN");
            //database = getWritableDatabase();
            //getWritableDatabase();
            result = openDatabase().insert(NEW_NOTIFICATION_TABLE, null, values);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d(TAG, "Add failed: ");
            // e.printStackTrace();
        } finally {
            closeDatabase();
        }

        // Log.d(Constant.FUNNY_CHAT, "Close add user: " + result);
        return result;
    }

    public long deleteFriendNewMessage(String friend) {
        long result = 0;
        // set values to insert
        ContentValues values = new ContentValues();
        values.put(NEW_FRIEND_ID, friend);

        // Inserting Row
        try {
            Log.d(TAG, "Add NEW OPEN");
            //database = getWritableDatabase();
            //getWritableDatabase();
            result = openDatabase().delete(NEW_NOTIFICATION_TABLE, NEW_FRIEND_ID + " = ?", new String[]{friend});
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d(TAG, "Add failed: ");
            // e.printStackTrace();
        } finally {
            closeDatabase();
        }

        // Log.d(Constant.FUNNY_CHAT, "Close add user: " + result);
        return result;
    }

    public List<String> getListNewMessage() {
        List<String> list = new ArrayList<String>();
        try {
            //getReadableDatabase();
            Cursor cursor = openDatabase().query(NEW_NOTIFICATION_TABLE,
                    new String[]{NEW_FRIEND_ID},null, null,
                    null, null, null, null);
            if (cursor != null) {
                // move to first row
                cursor.moveToFirst();
                list = new ArrayList<String>();
                Message unread = null;
                // iterate if remain
                while (cursor.isAfterLast() == false) {
                    String friend = cursor.getString(0);
                    list.add(list.size(), friend);
                    cursor.moveToNext();
                }
                cursor.close();
            }
            closeDatabase();
        } catch (Exception e) {

        }
        return list;
    }


}
