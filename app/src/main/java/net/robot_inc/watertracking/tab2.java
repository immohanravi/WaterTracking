package net.robot_inc.watertracking;


import android.app.AlertDialog;
import android.content.DialogInterface;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class tab2 extends Fragment {



    Drawable defaultImg;
    Button cancelBtn;
    EditText filterEdit;
    CustomListView listView;
    Adapter adapter;
    List<Data> list;
    HashMap<String, ArrayList<String>> map;
    ArrayList<String> values;
    Map<String, byte[]> imageList;
    Map<String, Integer> pendingAmount;
    Parcelable savingState;
    SharedPreferences customerCustomPosition;
    SharedPreferences.Editor edit;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_tab2, container, false);
        list = new ArrayList<>();
        listView = (CustomListView) view.findViewById(R.id.listView1);
        customerCustomPosition = getActivity().getSharedPreferences("customersList", getActivity().MODE_PRIVATE);
        edit = customerCustomPosition.edit();
        ShowSQLiteDBdata();
        filterEdit = (EditText) view.findViewById(R.id.searchcustomer);
        filterEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (TextUtils.isEmpty(filterEdit.getText().toString())) {
                    edit.clear();
                    for (int a = 0; a < listView.getAdapter().getCount(); a++) {

                        edit.putString(String.valueOf(a), ((Data) listView.getAdapter().getItem(a)).id);
                        Log.i(String.valueOf(a), ((Data) listView.getAdapter().getItem(a)).id);
                        edit.commit();
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCustomerDatabase(String.valueOf(s));
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(String.valueOf(s))) {
                    ShowSQLiteDBdata();
                }

            }
        });
        cancelBtn = (Button) view.findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterEdit.setText("");
                ShowSQLiteDBdata();
            }
        });
        return view;


    }

    @Override
    public void onResume() {
        super.onResume();

        ShowSQLiteDBdata();
        filterEdit.setText("");


    }


    private void ShowSQLiteDBdata() {
        SQLiteDatabase SQLITEDATABASE;
        customerDbHelper SQLITEHELPER;
        SQLITEHELPER = new customerDbHelper(getContext());
        Cursor cursor;

        SQLITEDATABASE = SQLITEHELPER.getWritableDatabase();


        cursor = SQLITEDATABASE.rawQuery("SELECT * FROM customers", null);
        map = new HashMap<>();
        values = new ArrayList<>();
        imageList = new HashMap<>();


        if (cursor.moveToFirst()) {
            do {
                values.clear();

                values.add(cursor.getString(cursor.getColumnIndex(customerDbHelper.KEY_Name)));


                values.add(cursor.getString(cursor.getColumnIndex(customerDbHelper.KEY_Number)));
                values.add(cursor.getString(cursor.getColumnIndex(customerDbHelper.KEY_Address)));

                imageList.put(cursor.getString(cursor.getColumnIndex(customerDbHelper.KEY_ID)), cursor.getBlob(cursor.getColumnIndex(customerDbHelper.KEY_Image)));
                map.put(cursor.getString(cursor.getColumnIndex(customerDbHelper.KEY_ID)), (ArrayList<String>) values.clone());
            } while (cursor.moveToNext());
        }
        setPendingAmount();
        setList();
        Log.i("changed", "");
        adapter = new Adapter(getContext(), list, new Adapter.Listener() {
            @Override
            public void onGrab(int position, RelativeLayout row) {

                listView.onGrab(position, row);
            }
        });

        listView.setAdapter(adapter);
        listView.setListener(new CustomListView.Listener() {
            @Override
            public void swapElements(int indexOne, int indexTwo) {
                Data temp = list.get(indexOne);
                list.set(indexOne, list.get(indexTwo));
                list.set(indexTwo, temp);

            }
        });
        cursor.close();
    }




    public void setPendingAmount() {
        pendingAmount = new HashMap<>();
        try {
            for (String id : map.keySet()) {
                customerDataHelper customerDataHelper = new customerDataHelper(getContext());
                pendingAmount.put(id, customerDataHelper.getPendingAmount(map.get(id).get(0).replaceAll(" ", "")));
                Log.i("pending amount", String.valueOf(pendingAmount.get(id)));
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "tab 2 \n" + e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }
    private void setList() {
        list.clear();
        ArrayList<String> addedIds = new ArrayList<>();

        for (int a = 0; a < map.size(); a++) {

            if (customerCustomPosition.getString(String.valueOf(a), "") != "") {

                String id = customerCustomPosition.getString(String.valueOf(a), "");
                if (map.containsKey(id)) {
                    addedIds.add(id);
                    list.add(new Data(getImage(imageList.get(id)), id, map.get(id).get(0), map.get(id).get(1), map.get(id).get(2), pendingAmount.get(id)));
                } else {
                    edit.remove(String.valueOf(a));
                }
            }

        }

        for (String id : map.keySet()) {
            if (!addedIds.contains(id)) {
                list.add(new Data(getImage(imageList.get(id)), id, map.get(id).get(0), map.get(id).get(1), map.get(id).get(2), pendingAmount.get(id)));

            }
        }

    }

    private void setListBySearch() {
        list.clear();

        for (String id : map.keySet()) {
            list.add(new Data(getImage(imageList.get(id)), id, map.get(id).get(0), map.get(id).get(1), map.get(id).get(2), pendingAmount.get(id)));

        }


    }

    private Bitmap getImage(byte[] imageByte) {
        return BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
    }

    private void filterCustomerDatabase(String filterText) {
        int position = 0;
        SQLiteDatabase SQLITEDATABASE;
        customerDbHelper SQLITEHELPER;
        SQLITEHELPER = new customerDbHelper(getContext());
        SQLITEDATABASE = SQLITEHELPER.getWritableDatabase();
        Cursor cursor = SQLITEDATABASE.rawQuery("SELECT * FROM customers", null);

        SQLITEDATABASE = SQLITEHELPER.getWritableDatabase();

        try {
            Long x = Long.parseLong(filterText);
            position = 2;
        } catch (NumberFormatException e) {

            position = 1;
        }
        if (position == 1) {
            cursor = SQLITEDATABASE.rawQuery("SELECT * FROM customers WHERE Name LIKE \'%" + filterText + "%\' OR Address LIKE \'%" + filterText + "%\'", null);
        } else if (position == 2) {
            cursor = SQLITEDATABASE.rawQuery("SELECT * FROM customers WHERE Number  LIKE \'%" + filterText + "%\'", null);
        }

        map = new HashMap<>();
        values = new ArrayList<>();
        imageList = new HashMap<>();


        if (cursor.moveToFirst()) {
            do {
                values.clear();

                values.add(cursor.getString(cursor.getColumnIndex(customerDbHelper.KEY_Name)));


                values.add(cursor.getString(cursor.getColumnIndex(customerDbHelper.KEY_Number)));
                values.add(cursor.getString(cursor.getColumnIndex(customerDbHelper.KEY_Address)));

                imageList.put(cursor.getString(cursor.getColumnIndex(customerDbHelper.KEY_ID)), cursor.getBlob(cursor.getColumnIndex(customerDbHelper.KEY_Image)));
                map.put(cursor.getString(cursor.getColumnIndex(customerDbHelper.KEY_ID)), (ArrayList<String>) values.clone());
            } while (cursor.moveToNext());
        }

        setPendingAmount();
        setListBySearch();
        adapter = new Adapter(getContext(), list, new Adapter.Listener() {
            @Override
            public void onGrab(int position, RelativeLayout row) {

                listView.onGrab(position, row);
            }
        });

        listView.setAdapter(adapter);
        listView.setListener(new CustomListView.Listener() {
            @Override
            public void swapElements(int indexOne, int indexTwo) {
                Data temp = list.get(indexOne);
                list.set(indexOne, list.get(indexTwo));
                list.set(indexTwo, temp);

            }
        });
        cursor.close();
    }
    @Override
    public void onStop() {
        super.onStop();
        if (TextUtils.isEmpty(filterEdit.getText().toString())) {
            edit.clear();
            for (int a = 0; a < listView.getAdapter().getCount(); a++) {

                edit.putString(String.valueOf(a), ((Data) listView.getAdapter().getItem(a)).id);
                Log.i(String.valueOf(a), ((Data) listView.getAdapter().getItem(a)).id);
                edit.commit();
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        if (TextUtils.isEmpty(filterEdit.getText().toString())) {
            edit.clear();
            for (int a = 0; a < listView.getAdapter().getCount(); a++) {

                edit.putString(String.valueOf(a), ((Data) listView.getAdapter().getItem(a)).id);
                Log.i(String.valueOf(a), ((Data) listView.getAdapter().getItem(a)).id);
                edit.commit();
            }
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        ShowSQLiteDBdata();
    }


}
