package net.robot_inc.watertracking;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.DragEvent;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class dragdrop extends AppCompatActivity {
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dragdrop);

        list = new ArrayList<>();
        listView = (CustomListView) findViewById(R.id.listView1);
        customerCustomPosition = getSharedPreferences("customersList", MODE_PRIVATE);
        edit = customerCustomPosition.edit();
        ShowSQLiteDBdata();
        filterEdit = (EditText) findViewById(R.id.searchcustomer);
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
                if(TextUtils.isEmpty(String.valueOf(s))){
                    ShowSQLiteDBdata();
                }

            }
        });
        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterEdit.setText("");
                ShowSQLiteDBdata();
            }
        });

    }

    private void filterCustomerDatabase(String filterText) {
        int position = 0;
        SQLiteDatabase SQLITEDATABASE;
        customerDbHelper SQLITEHELPER;
        SQLITEHELPER = new customerDbHelper(getApplicationContext());
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
        adapter = new Adapter(this, list, new Adapter.Listener() {
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


    public Bitmap getImagebyte(int id) {
        defaultImg = ContextCompat.getDrawable(getBaseContext(), id);
        Bitmap bitmap = ((BitmapDrawable) defaultImg).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitmapdata = stream.toByteArray();
        return bitmap;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.customer_context_menu, menu);


    }

    @Override
    protected void onResume() {
        super.onResume();

        ShowSQLiteDBdata();
    }

    private void ShowSQLiteDBdata() {
        SQLiteDatabase SQLITEDATABASE;
        customerDbHelper SQLITEHELPER;
        SQLITEHELPER = new customerDbHelper(getApplicationContext());
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
        adapter = new Adapter(this, list, new Adapter.Listener() {
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
                customerDataHelper customerDataHelper = new customerDataHelper(getApplicationContext());
                pendingAmount.put(id, customerDataHelper.getPendingAmount(map.get(id).get(0).replaceAll(" ", "")));
                Log.i("pending amount", String.valueOf(pendingAmount.get(id)));
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "tab 2 \n" + e.getMessage(), Toast.LENGTH_LONG).show();
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

    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        // Save list state
        savingState = listView.onSaveInstanceState();
        state.putParcelable("listview", savingState);

    }

    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

        // Retrieve list state and list/item positions
        if (state != null)
            savingState = state.getParcelable("listview");
    }

    @Override
    protected void onStop() {
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
    protected void onPause() {
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
    protected void onRestart() {
        super.onRestart();
        ShowSQLiteDBdata();
    }
}
