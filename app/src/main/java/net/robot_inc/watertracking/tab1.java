package net.robot_inc.watertracking;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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

    TextView stock, availableStock, txtDate, txtNoOfCans, txtPrice;
    int stockAvailble = 0;
    EditText searchView;
    Button cancelSearch;
    Bundle values;
    SQLiteDatabase SQLITEDATABASE;
    stockHellper SQLITEHELPER;
    customerDbHelper dh;
    customerDataHelper customerrecords;
    Spinner spinner3;
    Cursor cursor;
    stockAdapter ListAdapter;
    ListView listView;
    int clicked = 0;
    ArrayList<String> ID_ArrayList = new ArrayList<String>();
    ArrayList<String> DATE_ArrayList = new ArrayList<String>();
    ArrayList<String> NO_OF_CANS_ArrayList = new ArrayList<String>();
    ArrayList<String> PRICE_ArrayList = new ArrayList<String>();
    ArrayList<String> Name_ArrayList = new ArrayList<>();
    Snackbar snack;

    public tab1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab1, container, false);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        // Inflate the layout for this fragment
        snack = Snackbar.make(view, "Available Stock = ", Snackbar.LENGTH_INDEFINITE);
        View Sview = snack.getView();
        TextView tv = (TextView) Sview.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(ContextCompat.getColor(getContext(), R.color.yellow));
        tv.setTextSize(16);

        snack.show();
        listView = (ListView) view.findViewById(R.id.listView);
        listView.setTextFilterEnabled(true);
        txtDate = (TextView) view.findViewById(R.id.txtDate);
        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clicked % 2 == 0) {
                    Orderby("date", "ASC");
                    clicked++;
                } else {
                    Orderby("date", "DESC");
                    clicked++;
                }
            }
        });
        txtNoOfCans = (TextView) view.findViewById(R.id.txtNo_of_cans);
        txtNoOfCans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clicked % 2 == 0) {
                    Orderby("number_of_cans", "ASC");
                    clicked++;
                } else {
                    Orderby("number_of_cans", "DESC");
                    clicked++;
                }
            }
        });
        txtPrice = (TextView) view.findViewById(R.id.txtPrice);
        txtPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clicked % 2 == 0) {
                    Orderby("price", "ASC");
                    clicked++;
                } else {
                    Orderby("price", "DESC");
                    clicked++;
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                values = new Bundle();
                values.putString("id", ListAdapter.getId(position));
                values.putString("date", ListAdapter.getDate(position));
                values.putString("number_of_cans", ListAdapter.getNo_of_cans(position));
                values.putString("price", ListAdapter.getPrice(position));


                return false;
            }
        });
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    snack.dismiss();
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    snack.show();
                }
                return false;
            }
        });

        registerForContextMenu(listView);
        searchView = (EditText) view.findViewById(R.id.searchstock);

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(String.valueOf(s))) {
                    ShowSQLiteDBdata();
                } else {
                    filterData(String.valueOf(s));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        cancelSearch = (Button) view.findViewById(R.id.cancelsearch);
        cancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setText("");
            }
        });
        SQLITEHELPER = new stockHellper(getActivity());
        dh = new customerDbHelper(getActivity());
        customerrecords = new customerDataHelper(getActivity());
        stock = (TextView) view.findViewById(R.id.textView3);

        ShowSQLiteDBdata();
        stockAvailble = getAvailableStock();
        snack.setText("Available Stock = " + stockAvailble);
        listView.requestFocus();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        stockAvailble = getAvailableStock();
        snack.setText("Available Stock = " + stockAvailble);
        ShowSQLiteDBdata();

    }

    private int getAvailableStock() {
        int answer = 0;
        Name_ArrayList.clear();
        SQLITEDATABASE = SQLITEHELPER.getReadableDatabase();
        Cursor cursor = SQLITEDATABASE.rawQuery("SELECT * FROM stock", null);
        if (cursor.moveToFirst()) {
            do {
                answer = answer + cursor.getInt(2);

            } while (cursor.moveToNext());
        }
        cursor.close();
        SQLITEDATABASE.close();

        SQLITEDATABASE = dh.getWritableDatabase();
        cursor = SQLITEDATABASE.rawQuery("SELECT Name FROM customers", null);
        if (cursor.moveToFirst()) {
            do {
                Name_ArrayList.add(cursor.getString(cursor.getColumnIndex(customerDbHelper.KEY_Name)));
            } while (cursor.moveToNext());

        }

        cursor.close();
        SQLITEDATABASE.close();
        int totalSold = 0;
        SQLITEDATABASE = customerrecords.getWritableDatabase();
        for (String name : Name_ArrayList) {
            cursor = SQLITEDATABASE.rawQuery("SELECT * FROM " + name.replaceAll(" ", ""), null);
            if (cursor.moveToFirst()) {
                do {
                    totalSold = totalSold + Integer.parseInt(cursor.getString(cursor.getColumnIndex(customerDataHelper.KEY_No_of_cans)));

                } while (cursor.moveToNext());
            }

        }
        cursor.close();
        SQLITEDATABASE.close();


        return answer - totalSold;

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
                ID_ArrayList.add(cursor.getString(cursor.getColumnIndex(stockHellper.KEY_ID)));

                DATE_ArrayList.add(cursor.getString(cursor.getColumnIndex(stockHellper.KEY_Date)));

                NO_OF_CANS_ArrayList.add(cursor.getString(cursor.getColumnIndex(stockHellper.KEY_Number_Of_Cans)));

                PRICE_ArrayList.add(cursor.getString(cursor.getColumnIndex(stockHellper.KEY_Price)));

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
    }/*
    private void filterDatabase(String filterText) {

        String item = spinner3.getSelectedItem().toString();
        SQLITEDATABASE = SQLITEHELPER.getWritableDatabase();
        try{

            //cursor = SQLITEDATABASE.rawQuery("SELECT * FROM stock WHERE "+item+" LIKE \""+filterText+"%\"", null);
            switch (item){
                case "date":{
                    if(Integer.parseInt(getMonthNo(filterText))>0){
                        cursor = SQLITEDATABASE.rawQuery("SELECT * FROM stock WHERE strftime('%m', date) = '"+getMonthNo(filterText)+"'",null);
                        .i("select test","SELECT * FROM stock WHERE strftime('%m', date) = '"+getMonthNo(filterText)+"'");
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
                    ID_ArrayList.add(cursor.getString(cursor.getColumnIndex(stockHellper.KEY_ID)));

                    DATE_ArrayList.add(cursor.getString(cursor.getColumnIndex(stockHellper.KEY_Date)));

                    NO_OF_CANS_ArrayList.add(cursor.getString(cursor.getColumnIndex(stockHellper.KEY_Number_Of_Cans)));

                    PRICE_ArrayList.add(cursor.getString(cursor.getColumnIndex(stockHellper.KEY_Price)));

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

    }*/

    public String getMonthNo(String monthString) {
        monthString = monthString.toLowerCase();
        if ("january".contains(monthString)) {
            return "01";
        } else if ("february".contains(monthString)) {
            return "02";
        } else if ("march".contains(monthString)) {
            return "03";
        } else if ("april".contains(monthString)) {
            return "04";
        } else if ("may".contains(monthString)) {
            return "05";
        } else if ("june".contains(monthString)) {
            return "06";
        } else if ("july".contains(monthString)) {
            return "07";
        } else if ("august".contains(monthString)) {
            return "08";
        } else if ("september".contains(monthString)) {
            return "09";
        } else if ("october".contains(monthString)) {
            return "10";
        } else if ("november".contains(monthString)) {
            return "11";
        } else if ("december".contains(monthString)) {
            return "12";
        }
        return "0";
    }

    public void filterData(String filterText) {
        try {
            SQLITEDATABASE = SQLITEHELPER.getWritableDatabase();
            cursor = SQLITEDATABASE.rawQuery("SELECT * FROM stock WHERE strftime('%m', date) = '" + getMonthNo(filterText) + "' OR  strftime('%Y', date) = '" + filterText + "' OR strftime('%d', date) = '" + filterText + "' OR number_of_cans LIKE \'%" + filterText + "%\' OR price LIKE \'%" + filterText + "%\'", null);
            ID_ArrayList.clear();
            DATE_ArrayList.clear();
            NO_OF_CANS_ArrayList.clear();
            PRICE_ArrayList.clear();

            if (cursor.moveToFirst()) {
                do {
                    ID_ArrayList.add(cursor.getString(cursor.getColumnIndex(stockHellper.KEY_ID)));

                    DATE_ArrayList.add(cursor.getString(cursor.getColumnIndex(stockHellper.KEY_Date)));

                    NO_OF_CANS_ArrayList.add(cursor.getString(cursor.getColumnIndex(stockHellper.KEY_Number_Of_Cans)));

                    PRICE_ArrayList.add(cursor.getString(cursor.getColumnIndex(stockHellper.KEY_Price)));

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
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.stock_menu, menu);


    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.StkviewEdit: {
                try {
                    Intent intent = new Intent(getActivity(), updateStock.class);
                    intent.putExtras(values);
                    startActivityForResult(intent, 0);
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }


                return true;
            }

            case R.id.Stkdelete: {
                deleteRow();
                return true;
            }

            default:
                return false;
        }
    }

    private void deleteRow() {
        AlertDialog.Builder alert_box = new AlertDialog.Builder(getContext());

        alert_box.setMessage("Are you Sure, Do want to delete the data?");
        alert_box.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dia, int which) {
                try {
                    SQLITEDATABASE = SQLITEHELPER.getWritableDatabase();
                    SQLITEDATABASE.execSQL("DELETE FROM stock WHERE id='" + values.getString("id") + "'");
                    Toast.makeText(getContext(), "Successfully Deleted the data", Toast.LENGTH_SHORT).show();
                    ShowSQLiteDBdata();

                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }


            }
        });
        alert_box.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dia, int which) {
                // TODO Auto-generated method stub
                Toast.makeText(getActivity().getApplicationContext(), "No Button Clicked", Toast.LENGTH_LONG).show();
            }
        });
        alert_box.show();
    }

    public void Orderby(String orderBy, String order) {
        String query = "";
        if (orderBy.equals("date")) {
            query = "SELECT * FROM stock ORDER BY date(" + orderBy + ") " + order;
        } else {
            query = " SELECT * FROM stock ORDER BY " + orderBy + " " + order;
        }
        try {
            SQLITEDATABASE = SQLITEHELPER.getWritableDatabase();
            cursor = SQLITEDATABASE.rawQuery(query, null);
            ID_ArrayList.clear();
            DATE_ArrayList.clear();
            NO_OF_CANS_ArrayList.clear();
            PRICE_ArrayList.clear();

            if (cursor.moveToFirst()) {
                do {
                    ID_ArrayList.add(cursor.getString(cursor.getColumnIndex(stockHellper.KEY_ID)));

                    DATE_ArrayList.add(cursor.getString(cursor.getColumnIndex(stockHellper.KEY_Date)));

                    NO_OF_CANS_ArrayList.add(cursor.getString(cursor.getColumnIndex(stockHellper.KEY_Number_Of_Cans)));

                    PRICE_ArrayList.add(cursor.getString(cursor.getColumnIndex(stockHellper.KEY_Price)));

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
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
}
