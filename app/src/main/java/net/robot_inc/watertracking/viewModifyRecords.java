package net.robot_inc.watertracking;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class viewModifyRecords extends AppCompatActivity {


    private ListView listView;
    private Button btnRecordAdd;
    SQLiteDatabase SQLITEDATABASE;
    customerDataHelper SQLITEHELPER;
    customerDataAdapter customerAdapter;
    String table_name = "";
    Cursor cursor;
    ArrayList<String> ID_ArrayList = new ArrayList<String>();
    ArrayList<String> DATE_ArrayList = new ArrayList<String>();
    ArrayList<String> NO_OF_CANS_ArrayList = new ArrayList<String>();
    ArrayList<String> PRICE_ArrayList = new ArrayList<String>();
    ArrayList<String> PAID_ArrayList = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_modify_records);
        table_name = getIntent().getExtras().getString("table_name");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("View Records");
        SQLITEHELPER = new customerDataHelper(getApplicationContext());
        listView=(ListView) findViewById(R.id.tablelayout);
        btnRecordAdd = (Button) findViewById(R.id.btnRecordAdd);
        btnRecordAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), addRecord.class);
                intent.putExtra("table_name",getIntent().getExtras().getString("table_name"));
                startActivity(intent);
            }
        });

        ShowSQLiteDBdata();
    }

    private void ShowSQLiteDBdata() {

        SQLITEDATABASE = SQLITEHELPER.getWritableDatabase();


        cursor = SQLITEDATABASE.rawQuery("SELECT * FROM "+table_name, null);

        ID_ArrayList.clear();
        DATE_ArrayList.clear();
        NO_OF_CANS_ArrayList.clear();
        PRICE_ArrayList.clear();
        PAID_ArrayList.clear();

        if (cursor.moveToFirst()) {
            do {
                ID_ArrayList.add(cursor.getString(cursor.getColumnIndex(customerDataHelper.KEY_ID)));

                DATE_ArrayList.add(cursor.getString(cursor.getColumnIndex(customerDataHelper.KEY_Date)));


                NO_OF_CANS_ArrayList.add(cursor.getString(cursor.getColumnIndex(customerDataHelper.KEY_No_of_cans)));
                PRICE_ArrayList.add(cursor.getString(cursor.getColumnIndex(customerDataHelper.KEY_Price)));

                PAID_ArrayList.add(cursor.getString(cursor.getColumnIndex(customerDataHelper.KEY_Paid)));

            } while (cursor.moveToNext());
        }

        customerAdapter = new customerDataAdapter(getApplicationContext(),

                ID_ArrayList,
                DATE_ArrayList,
                NO_OF_CANS_ArrayList,
                PRICE_ArrayList,
                PAID_ArrayList

        );

        listView.setAdapter(customerAdapter);

        cursor.close();
    }
}
