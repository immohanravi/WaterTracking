package net.robot_inc.watertracking;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import net.robot_inc.watertracking.R;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class tab2 extends Fragment {

    Button loadImage;
    ImageView iv;
    private static int RESULT_LOAD_IMAGE = 1;
    SQLiteDatabase SQLITEDATABASE;
    customerDbHelper SQLITEHELPER;
    Spinner spinner3;
    Cursor cursor;
    customerDbAdapter ListAdapter ;
    ListView listView;
    ArrayList<String> ID_ArrayList = new ArrayList<String>();
    ArrayList<String> Name_ArrayList = new ArrayList<String>();
    ArrayList<String> Number_ArrayList = new ArrayList<String>();
    ArrayList<byte[]> Image_ArrayList = new ArrayList<byte[]>();
    ArrayList<String> Address_ArrayList = new ArrayList<String>();


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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               /* Bundle values = new Bundle();
                values.putString("id",ListAdapter.getId(position));
                values.putString("date",ListAdapter.getDate(position));
                values.putString("number_of_cans",ListAdapter.getNo_of_cans(position));
                values.putString("price",ListAdapter.getPrice(position));*/
                Intent intent = new Intent(getActivity(),customerDetails.class);
                //intent.putExtras(values);
                startActivityForResult(intent, 0);

            }
        });
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

        ListAdapter = new customerDbAdapter(getActivity(),

                ID_ArrayList,
                Name_ArrayList,
                Address_ArrayList,
                Number_ArrayList,
                Image_ArrayList

        );

        listView.setAdapter(ListAdapter);

        cursor.close();
    }

}
