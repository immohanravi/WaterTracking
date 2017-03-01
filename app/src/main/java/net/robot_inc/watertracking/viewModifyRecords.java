package net.robot_inc.watertracking;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class viewModifyRecords extends AppCompatActivity {


    private ListView listView;
    private Button btnRecordAdd;
    SQLiteDatabase SQLITEDATABASE;
    customerDataHelper SQLITEHELPER;
    customerDataAdapter customerAdapter;

    Bundle values;
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
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {


            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                values = new Bundle();
                values.putString("id",customerAdapter.getId(position));
                values.putString("date",customerAdapter.getDate(position));
                values.putString("cans",customerAdapter.getNo_of_cans(position));
                values.putString("price",customerAdapter.getPrice(position));
                values.putString("paid",customerAdapter.getPaid(position));
                values.putString("name",table_name);

                //view.setBackgroundColor(Color.MAGENTA);





                return false;
            }
        });

        registerForContextMenu(listView);

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

    @Override
    protected void onResume() {
        super.onResume();

        ShowSQLiteDBdata();
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.records_context_menu,menu);


    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.viewEdit: {
                try{
                    Intent intent = new Intent(getApplicationContext(), updateRecords.class);
                    intent.putExtras(values);
                    startActivity(intent);
                }catch (Exception e){
                    Log.i("menu", e.getMessage());
                }



                return true;
            }



            case R.id.delete: {
                deleteRow();
                return true;
            }

            default:
                return false;
        }
    }

    private void deleteRow() {
        AlertDialog.Builder alert_box=new AlertDialog.Builder(this);

        alert_box.setMessage("Are you Sure, Do want to delete the data?");
        alert_box.setPositiveButton("Yes",new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                    SQLITEDATABASE = SQLITEHELPER.getWritableDatabase();
                    SQLITEDATABASE.execSQL("DELETE FROM "+table_name+" WHERE id='"+values.getString("id")+"'");
                    ShowSQLiteDBdata();
                    Toast.makeText(getApplicationContext(), "Successfully Deleted the record", Toast.LENGTH_SHORT).show();

                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }



            }
        });
        alert_box.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "No Button Clicked", Toast.LENGTH_LONG).show();
            }
        });
        alert_box.show();
    }


}
