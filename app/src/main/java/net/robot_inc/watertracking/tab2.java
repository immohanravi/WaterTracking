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
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import net.robot_inc.watertracking.R;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import net.robot_inc.watertracking.customerDataHelper;
import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class tab2 extends Fragment {

    Button loadImage;

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
        Log.i("tab2","called");
        View view = inflater.inflate(R.layout.fragment_tab2,container,false);
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
                values.putString("id",ListAdapter.getId(position));
                values.putString("name",ListAdapter.getName(position));
                values.putString("address",ListAdapter.getAddress(position));
                values.putString("number",ListAdapter.getNumber(position));
                values.putByteArray("image",Image_ArrayList.get(position));

                return false;
            }
        });

        registerForContextMenu(listView);
        SQLITEHELPER = new customerDbHelper(getActivity());

        ShowSQLiteDBdata();


        return view;


    }
    @Override
    public void onResume() {
        super.onResume();

        ShowSQLiteDBdata();


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

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
        Log.i("is next",String.valueOf("called"));
        SQLITEDATABASE = SQLITEHELPER.getWritableDatabase();


        cursor = SQLITEDATABASE.rawQuery("SELECT * FROM customers", null);


        ID_ArrayList.clear();
        Name_ArrayList.clear();
        Number_ArrayList.clear();
        Address_ArrayList.clear();
        Image_ArrayList.clear();
        Log.i("is next",String.valueOf(cursor.moveToFirst()));
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
        ListAdapter = new customerDbAdapter(getActivity(),

                ID_ArrayList,
                Name_ArrayList,
                Address_ArrayList,
                Number_ArrayList,
                imageList

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
                try{
                    Intent intent = new Intent(getActivity(), customerDetails.class);
                    intent.putExtras(values);
                    startActivity(intent);
                }catch (Exception e){
                    Log.i("menu", e.getMessage());
                }



                return true;
            }

            case R.id.viewRecords: {
                Intent intent = new Intent(getActivity(), viewModifyRecords.class);
                intent.putExtra("table_name",values.getString("name").toLowerCase());
                startActivity(intent);
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
    public void deleteRow(){
        AlertDialog.Builder alert_box=new AlertDialog.Builder(getContext());
        alert_box.setMessage("Are you Sure, Do you want to Remove this customer from list?\nAll the records with be deleted.");
        alert_box.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                    SQLITEDATABASE = SQLITEHELPER.getWritableDatabase();
                    SQLITEDATABASE.execSQL("DELETE FROM customers WHERE id='"+values.getString("id")+"'");
                    customerDataHelper = new customerDataHelper(getContext());
                    customerDataHelper.dropTable(values.getString("name").toLowerCase());

                    ShowSQLiteDBdata();
                    Toast.makeText(getContext(), "Successfully Deleted the data", Toast.LENGTH_SHORT).show();

                }catch (Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }



            }
        });
        alert_box.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });
        alert_box.show();
    }
    public void getBitmap(){
        imageList.clear();
        for(byte[] imageByte : Image_ArrayList){
            imageList.add(BitmapFactory.decodeByteArray(imageByte,0,imageByte.length));
        }
    }

}
