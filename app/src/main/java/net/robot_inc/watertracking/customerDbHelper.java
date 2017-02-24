package net.robot_inc.watertracking;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mohan on 2/14/2017.
 */

public class customerDbHelper extends SQLiteOpenHelper {
    static String DATABASE_NAME="WaterTracking";


    public static final String KEY_ID="id";

    public static final String TABLE_NAME="customers";

    public static final String KEY_Name="Name";

    public static final String KEY_Address="Address";

    public static final String KEY_Number="Number";
    public static final String KEY_Image="Image";
    public customerDbHelper(Context context) {

        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE="CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+KEY_Name+" TEXT NOT NULL, "+KEY_Address+" TEXT NOT NULL, "+KEY_Number+" INTEGER NOT NULL UNIQUE, "+KEY_Image+" BLOB NOT NULL)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
}
