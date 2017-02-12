package net.robot_inc.watertracking;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class tab1 extends Fragment {

    TextView stock, availableStock;
    int stockAvilable = 0;
    SearchView searchView;
    SQLiteDatabase SQLITEDATABASE;
    SqlStock SQLITEHELPER;
    Spinner spinner3;
    Cursor cursor;
    stockAdapter ListAdapter ;
    ListView listView;
    ArrayList<String> ID_ArrayList = new ArrayList<String>();
    ArrayList<String> DATE_ArrayList = new ArrayList<String>();
    ArrayList<String> NO_OF_CANS_ArrayList = new ArrayList<String>();
    ArrayList<String> PRICE_ArrayList = new ArrayList<String>();

    public tab1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab1,container,false);

        // Inflate the layout for this fragment

        listView = (ListView) view.findViewById(R.id.listView);
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle values = new Bundle();
                values.putString("id",ListAdapter.getId(position));
                values.putString("date",ListAdapter.getDate(position));
                values.putString("number_of_cans",ListAdapter.getNo_of_cans(position));
                values.putString("price",ListAdapter.getPrice(position));
                Intent intent = new Intent(getActivity(),updateStock.class);
                intent.putExtras(values);
                startActivityForResult(intent, 0);

            }
        });


        searchView = (SearchView) view.findViewById(R.id.search);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)){
                    ShowSQLiteDBdata();
                }else {
                    filterDatabase(newText);
                }


                return true;

            }
        });
        SQLITEHELPER = new SqlStock(getActivity());
        stock = (TextView) view.findViewById(R.id.textView3);
        availableStock = (TextView) view.findViewById(R.id.stockavailable);
        availableStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), stockView.class);
                startActivity(intent);
            }
        });
        spinner3 = (Spinner) view.findViewById(R.id.spinner3);

        stockAvilable = getAvailableStock();
        stock.setText(String.valueOf(stockAvilable));
        ShowSQLiteDBdata();
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        stockAvilable = getAvailableStock();
        stock.setText(String.valueOf(stockAvilable));
        ShowSQLiteDBdata();

    }
    private int getAvailableStock() {
        int answer = 0;
        SQLITEDATABASE = SQLITEHELPER.getReadableDatabase();
        Cursor cursor = SQLITEDATABASE.rawQuery("SELECT * FROM stock",null);
        if(cursor.moveToFirst()){
            do{
                answer = answer + cursor.getInt(2);
                Log.i("date",String.valueOf(cursor.getString(1)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        Log.i("answer",String.valueOf(answer));
        return answer;

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

        ListAdapter = new stockAdapter(getActivity(),

                ID_ArrayList,
                DATE_ArrayList,
                NO_OF_CANS_ArrayList,
                PRICE_ArrayList

        );

        listView.setAdapter(ListAdapter);

        cursor.close();
    }
    private void filterDatabase(String filterText) {

        String item = spinner3.getSelectedItem().toString();
        SQLITEDATABASE = SQLITEHELPER.getWritableDatabase();
        try{

            //cursor = SQLITEDATABASE.rawQuery("SELECT * FROM stock WHERE "+item+" LIKE \""+filterText+"%\"", null);
            switch (item){
                case "date":{
                    if(Integer.parseInt(getMonthNo(filterText))>0){
                        cursor = SQLITEDATABASE.rawQuery("SELECT * FROM stock WHERE strftime('%m', date) = '"+getMonthNo(filterText)+"'",null);
                        Log.i("select test","SELECT * FROM stock WHERE strftime('%m', date) = '"+getMonthNo(filterText)+"'");
                    }else if (filterText.length()==4){
                        cursor = SQLITEDATABASE.rawQuery("SELECT * FROM stock WHERE strftime('%Y', date) = '"+filterText+"'",null);
                    }else {
                        cursor = SQLITEDATABASE.rawQuery("SELECT * FROM stock WHERE strftime('%d', date) = '"+filterText+"'",null);
                    }
                    break;
                }
                case "number_of_cans":{
                    cursor = SQLITEDATABASE.rawQuery("SELECT * FROM stock WHERE number_of_cans LIKE \""+filterText+"%\"", null);
                    break;
                }case "price":{
                    cursor = SQLITEDATABASE.rawQuery("SELECT * FROM stock WHERE price LIKE \""+filterText+"%\"", null);
                    break;
                }
            }
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

            ListAdapter = new stockAdapter(getActivity(),

                    ID_ArrayList,
                    DATE_ArrayList,
                    NO_OF_CANS_ArrayList,
                    PRICE_ArrayList

            );

            listView.setAdapter(ListAdapter);

            cursor.close();
        }catch (Exception e){
            Toast.makeText(getActivity(),"Not a valid query",Toast.LENGTH_SHORT).show();
        }

    }

    public String getMonthNo(String monthString){
        monthString = monthString.toLowerCase();
        if("january".contains(monthString)){
            return "01";
        }else if("february".contains(monthString)){
            return "02";
        }else if("march".contains(monthString)){
            return "03";
        }else if("april".contains(monthString)){
            return "04";
        }else if("may".contains(monthString)){
            return "05";
        }else if("june".contains(monthString)){
            return "06";
        }else if("july".contains(monthString)){
            return "07";
        }else if("august".contains(monthString)){
            return "08";
        }else if("september".contains(monthString)){
            return "09";
        }else if("october".contains(monthString)){
            return "10";
        }else if("november".contains(monthString)){
            return "11";
        }else if("december".contains(monthString)){
            return "12";
        }
        return "0";
    }

    public void updateDatabase(String id,String date,String number_of_cans, String price){

    }

}
