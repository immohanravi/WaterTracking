package net.robot_inc.watertracking;


import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.util.TimeUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.robot_inc.watertracking.R;

import java.net.InterfaceAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.robot_inc.watertracking.customerDataHelper;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class tab2 extends Fragment {

    Button loadImage, cancelBtn;
    HashMap<String, Integer> pendingAmount = new HashMap<>();
    Bundle values;
    Intent intent;
    long start = 0;
    long end = 0;
    ImageView iv, arrow;
    private static int RESULT_LOAD_IMAGE = 1;
    SQLiteDatabase SQLITEDATABASE;
    String customerName = "";
    customerDbHelper SQLITEHELPER;
    customerDataHelper customerDataHelper;
    Spinner spinner3;
    Cursor cursor;
    customerDbAdapter ListAdapter;
    ListView listView;
    EditText cancelInput;
    ArrayList<String> ID_ArrayList = new ArrayList<String>();
    ArrayList<String> Name_ArrayList = new ArrayList<String>();
    ArrayList<String> Number_ArrayList = new ArrayList<String>();
    ArrayList<byte[]> Image_ArrayList = new ArrayList<byte[]>();
    ArrayList<String> Address_ArrayList = new ArrayList<String>();
    ArrayList<Bitmap> imageList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_tab2, container, false);

        // Creating adapter for spinner
               /*
        iv = (ImageView) view.findViewById(R.id.imageView2);
        iv = (ImageView) view.findViewById(R.id.imageView2);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        */
        listView = (ListView) view.findViewById(R.id.customer_list);
        listView.setTextFilterEnabled(true);
        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              Bundle values = new Bundle();
                values.putString("id",ListAdapter.getId(position));
                values.putString("name",ListAdapter.getName(position));
                values.putString("address",ListAdapter.getAddress(position));
                values.putString("number",ListAdapter.getNumber(position));
                values.putByteArray("image",ListAdapter.Image.get(position));
                Intent intent = new Intent(getActivity(),customerDetails.class);
                intent.putExtras(values);
                startActivity(intent);

            }
        });*/
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                customerName = ListAdapter.getName(position);
                values = new Bundle();
                values.putString("id", ListAdapter.getId(position));
                values.putString("name", ListAdapter.getName(position).replaceAll(" ", ""));
                values.putString("realname", ListAdapter.getName(position));
                values.putString("address", ListAdapter.getAddress(position));
                values.putString("number", ListAdapter.getNumber(position));
                values.putByteArray("image", Image_ArrayList.get(position));

                return false;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                customerName = ListAdapter.getName(position);
                values = new Bundle();
                values.putString("id", ListAdapter.getId(position));
                values.putString("name", ListAdapter.getName(position).replaceAll(" ", ""));
                values.putString("realname", ListAdapter.getName(position));
                values.putString("address", ListAdapter.getAddress(position));
                values.putString("number", ListAdapter.getNumber(position));
                values.putByteArray("image", Image_ArrayList.get(position));
                try {
                    Intent intent = new Intent(getActivity(), viewModifyRecords.class);
                    intent.putExtras(values);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        registerForContextMenu(listView);
        SQLITEHELPER = new customerDbHelper(getActivity());

        ShowSQLiteDBdata();
        cancelInput = (EditText) view.findViewById(R.id.searchcustomer);
        cancelInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCustomerDatabase(String.valueOf(s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        cancelBtn = (Button) view.findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelInput.setText("");
            }
        });
        return view;


    }

    @Override
    public void onResume() {
        super.onResume();

        ShowSQLiteDBdata();
        cancelInput.setText("");


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();


            iv.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        }


    }

    private void ShowSQLiteDBdata() {

        SQLITEDATABASE = SQLITEHELPER.getWritableDatabase();


        cursor = SQLITEDATABASE.rawQuery("SELECT * FROM customers", null);


        ID_ArrayList.clear();
        Name_ArrayList.clear();
        Number_ArrayList.clear();
        Address_ArrayList.clear();
        Image_ArrayList.clear();

        if (cursor.moveToFirst()) {
            do {
                ID_ArrayList.add(cursor.getString(cursor.getColumnIndex(customerDbHelper.KEY_ID)));

                Name_ArrayList.add(cursor.getString(cursor.getColumnIndex(customerDbHelper.KEY_Name)));

                Address_ArrayList.add(cursor.getString(cursor.getColumnIndex(customerDbHelper.KEY_Address)));

                Number_ArrayList.add(cursor.getString(cursor.getColumnIndex(customerDbHelper.KEY_Number)));

                Image_ArrayList.add(cursor.getBlob(cursor.getColumnIndex(customerDbHelper.KEY_Image)));

            } while (cursor.moveToNext());
        }
        getBitmap();
        setPendingAmount();
        ListAdapter = new customerDbAdapter(getActivity(),

                ID_ArrayList,
                Name_ArrayList,
                Address_ArrayList,
                Number_ArrayList,
                imageList,
                pendingAmount

        );

        listView.setAdapter(ListAdapter);

        cursor.close();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.customer_context_menu, menu);


    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.viewEdit: {
                try {
                    Intent intent = new Intent(getActivity(), customerDetails.class);
                    intent.putExtras(values);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }


                return true;
            }


            case R.id.call: {
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + values.getString("number")));
                startActivityForResult(callIntent, 1);
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


    public void deleteRow() {
        AlertDialog.Builder alert_box = new AlertDialog.Builder(getContext());
        alert_box.setMessage("Are you Sure, Do you want to Remove this customer from list?\nAll the records with be deleted.");
        alert_box.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dia, int which) {
                try {
                    SQLITEDATABASE = SQLITEHELPER.getWritableDatabase();
                    SQLITEDATABASE.execSQL("DELETE FROM customers WHERE id='" + values.getString("id") + "'");
                    customerDataHelper = new customerDataHelper(getContext());
                    customerDataHelper.dropTable(values.getString("name").toLowerCase().replaceAll(" ", ""));

                    ShowSQLiteDBdata();
                    Toast.makeText(getContext(), "Successfully Deleted the data", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }


            }
        });
        alert_box.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dia, int which) {
                // TODO Auto-generated method stub

            }
        });
        alert_box.show();
    }

    public void getBitmap() {
        imageList.clear();
        for (byte[] imageByte : Image_ArrayList) {
            imageList.add(BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length));
        }
    }

    public void setPendingAmount() {
        try {
            for (String name : Name_ArrayList) {
                customerDataHelper = new customerDataHelper(getContext());
                pendingAmount.put(name, customerDataHelper.getPendingAmount(name.replaceAll(" ", "")));
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "tab 2 \n" + e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    public void filterCustomerDatabase(String filterText) {
        SQLITEDATABASE = SQLITEHELPER.getWritableDatabase();
        int position = 0;
        Cursor cursor = SQLITEDATABASE.rawQuery("SELECT * FROM customers", null);
        ;
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

        ID_ArrayList.clear();
        Name_ArrayList.clear();
        Number_ArrayList.clear();
        Address_ArrayList.clear();
        Image_ArrayList.clear();

        if (cursor.moveToFirst()) {
            do {
                ID_ArrayList.add(cursor.getString(cursor.getColumnIndex(customerDbHelper.KEY_ID)));

                Name_ArrayList.add(cursor.getString(cursor.getColumnIndex(customerDbHelper.KEY_Name)));

                Address_ArrayList.add(cursor.getString(cursor.getColumnIndex(customerDbHelper.KEY_Address)));

                Number_ArrayList.add(cursor.getString(cursor.getColumnIndex(customerDbHelper.KEY_Number)));

                Image_ArrayList.add(cursor.getBlob(cursor.getColumnIndex(customerDbHelper.KEY_Image)));

            } while (cursor.moveToNext());
        }
        getBitmap();
        setPendingAmount();
        ListAdapter = new customerDbAdapter(getActivity(),

                ID_ArrayList,
                Name_ArrayList,
                Address_ArrayList,
                Number_ArrayList,
                imageList,
                pendingAmount

        );

        listView.setAdapter(ListAdapter);

        cursor.close();
        SQLITEDATABASE.close();

    }

}
