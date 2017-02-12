package net.robot_inc.watertracking;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class stockView extends AppCompatActivity {
    SqlStock SQLITEHELPER;
    SQLiteDatabase SQLITEDATABASE;
    Cursor cursor;
    stockAdapter ListAdapter ;
    ListView listView;
    ArrayList<String> ID_ArrayList = new ArrayList<String>();
    ArrayList<String> DATE_ArrayList = new ArrayList<>();
    ArrayList<String> NO_OF_CANS_ArrayList = new ArrayList<String>();
    ArrayList<String> PRICE_ArrayList = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_view);
        getSupportActionBar().setTitle("Stock List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView = (ListView) findViewById(R.id.listView);

        SQLITEHELPER = new SqlStock(this);
    }

    @Override
    protected void onResume() {

        ShowSQLiteDBdata() ;

        super.onResume();
    }

    private void ShowSQLiteDBdata() {

        SQLITEDATABASE = SQLITEHELPER.getWritableDatabase();

        cursor = SQLITEDATABASE.rawQuery("SELECT * FROM stock", null);

        ID_ArrayList.clear();
        DATE_ArrayList.clear();
        NO_OF_CANS_ArrayList.clear();
        PRICE_ArrayList.clear();

        if (cursor.moveToFirst()) {
            do {
                ID_ArrayList.add(cursor.getString(cursor.getColumnIndex(SqlStock.KEY_ID)));

                DATE_ArrayList.add(cursor.getString(cursor.getColumnIndex(SqlStock.KEY_Date)));

                NO_OF_CANS_ArrayList.add(cursor.getString(cursor.getColumnIndex(SqlStock.KEY_Number_Of_Cans)));

                PRICE_ArrayList.add(cursor.getString(cursor.getColumnIndex(SqlStock.KEY_Price)));

            } while (cursor.moveToNext());
        }

        ListAdapter = new stockAdapter(stockView.this,

                ID_ArrayList,
                DATE_ArrayList,
                NO_OF_CANS_ArrayList,
                PRICE_ArrayList

        );

        listView.setAdapter(ListAdapter);

        cursor.close();
    }
}
