package net.robot_inc.watertracking;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by mohan on 18/2/17.
 */


public class customerDataHelper extends SQLiteOpenHelper {

    static String DATABASE_NAME="customers";

    Context context;

    public static final String KEY_ID="id";

    public static String TABLE_NAME="test";

    public static final String KEY_Date="Date";

    public static final String KEY_No_of_cans="No_of_cans";

    public static final String KEY_Price="Price";
    public static final String KEY_Paid="Paid";
    public customerDataHelper(Context context) {

        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE="CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+KEY_Date+" DATETIME, "+KEY_No_of_cans+" INTEGER, "+KEY_Price+" INTEGER, "+KEY_Paid+" INTEGER)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
    public void createCustomTables(String TABLE_NAME) {
        this.TABLE_NAME = TABLE_NAME;
        try {
            String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + KEY_Date + " DATETIME, NOT NULL" + KEY_No_of_cans + " INTEGER, " + KEY_Price + " INTEGER, " + KEY_Paid+ " INTEGER)";
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(CREATE_TABLE);
            db.close();
            Toast.makeText(context, "Successfully created "+TABLE_NAME+" Table", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {

        }
    }
    public void dropTable(String TABLE_NAME) {
        this.TABLE_NAME = TABLE_NAME;
        try {
            String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(DROP_TABLE);
            db.close();
            Toast.makeText(context, "Successfully deleted the Table "+TABLE_NAME, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {

        }
    }
    public void alterTable(String OLD_NAME,String NEW_NAME) {
        this.TABLE_NAME = TABLE_NAME;
        try {
            String DROP_TABLE = "ALTER TABLE "+OLD_NAME+" RENAME TO "+NEW_NAME;
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(DROP_TABLE);
            db.close();
            Toast.makeText(context, "Successfully RENAMED Table from "+OLD_NAME+" to "+NEW_NAME, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {

        }
    }


}
