package net.robot_inc.watertracking;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

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
        String CREATE_TABLE="CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+KEY_Date+" DATETIME NOT NULL, "+KEY_No_of_cans+" INTEGER, "+KEY_Price+" INTEGER, "+KEY_Paid+" INTEGER)";
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
            String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + KEY_Date + " DATETIME NOT NULL," + KEY_No_of_cans + " INTEGER, " + KEY_Price + " INTEGER, " + KEY_Paid+ " INTEGER)";
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(CREATE_TABLE);
            db.close();
            Toast.makeText(context, "Successfully created "+TABLE_NAME+" Table", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("sqlite" ,e.getMessage().toString());

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

        try {
            String DROP_TABLE = "ALTER TABLE "+OLD_NAME+" RENAME TO "+NEW_NAME;
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(DROP_TABLE);
            db.close();
            Toast.makeText(context, "Successfully RENAMED Table from "+OLD_NAME+" to "+NEW_NAME, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {

        }
    }

    public int getPendingAmount(String table_name){
        this.TABLE_NAME = table_name;
        int amount = 0;
        int paid = 0;
        int pending = 0;

        try{
            String select = "SELECT * FROM "+TABLE_NAME;
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor c = db.rawQuery(select,null);

            if(c.moveToFirst()){
                do{
                    int cans = Integer.parseInt(c.getString(c.getColumnIndex(this.KEY_No_of_cans)));
                    int price = Integer.parseInt(c.getString(c.getColumnIndex(this.KEY_Price)));
                    amount = amount + (cans*price);
                    paid = paid + Integer.parseInt(c.getString(c.getColumnIndex(this.KEY_Paid)));
                }while (c.moveToNext());
                pending = amount - paid;
            }
            c.close();
        }catch (Exception e){

        }

         return pending;
    }


}
