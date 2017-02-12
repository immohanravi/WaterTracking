package net.robot_inc.watertracking;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mohan on 2/4/2017.
 */

public class SqlStock extends SQLiteOpenHelper {

    static String DATABASE_NAME="WaterTracking";
    tab1 one;

    public static final String KEY_ID="id";

    public static final String TABLE_NAME="stock";

    public static final String KEY_Date="date";

    public static final String KEY_Number_Of_Cans="number_of_cans";

    public static final String KEY_Price="price";
    public SqlStock(Context context) {

        super(context, DATABASE_NAME, null, 1);

    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE="CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+KEY_Date+" DATE, "+KEY_Number_Of_Cans+" INTEGER, "+KEY_Price+" INTEGER)";
        db.execSQL(CREATE_TABLE);
        }

@Override
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);

        }
 }
