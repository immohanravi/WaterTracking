package net.robot_inc.watertracking;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Calendar;

public class customerDetails extends AppCompatActivity{
    ImageView customerPhoto;
    int RESULT_LOAD_IMAGE = 1;
    byte[] array;
    String firstLetterCaps = "";
    Bundle values;
    EditText name, address, number;
    Button update;
    customerDbHelper helper;
    customerDataHelper customerdatahelper;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("View/Update");
        helper = new customerDbHelper(getApplicationContext());
        update = (Button) findViewById(R.id.btnUpdate);

        name = (EditText) findViewById(R.id.et_name);

        address = (EditText) findViewById(R.id.et_address);
        number = (EditText) findViewById(R.id.et_phone);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               updateDatabase();
            }
        });


        customerPhoto = (ImageView) findViewById(R.id.iv_customer_photo);
        customerPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        values = getIntent().getExtras();
        name.setText(values.getString("name"));
        address.setText(values.getString("address"));
        number.setText(values.getString("number"));
       Bitmap yourSelectedImage = BitmapFactory.decodeByteArray(values.getByteArray("image"),0,values.getByteArray("image").length);
       ByteArrayOutputStream stream = new ByteArrayOutputStream();
       yourSelectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
       array = stream.toByteArray();
        customerPhoto.setImageBitmap(BitmapFactory.decodeByteArray(array,0,array.length));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem Item) {
        switch (Item.getItemId()) {
            case R.id.exit:
                finish();
                System.exit(0);
                return true;
            default:
                return super.onOptionsItemSelected(Item);
        }
    }

    public void updateDatabase(){
        Log.i("update","updating database");
        Boolean updated = false;
        firstLetterCaps = name.getText().toString();
        firstLetterCaps = firstLetterCaps.replaceFirst(String.valueOf(firstLetterCaps.charAt(0)),String.valueOf(firstLetterCaps.charAt(0)).toUpperCase());

        if((firstLetterCaps.equals(values.getString("name")) == false)){


            try {
                db = helper.getWritableDatabase();
                db.execSQL("UPDATE customers SET Name ='"+firstLetterCaps+"' WHERE id='"+values.getString("id")+"'");
                customerdatahelper = new customerDataHelper(getApplicationContext());
                customerdatahelper.alterTable(values.getString("name"),firstLetterCaps.toLowerCase().replaceAll(" ",""));
                updated = true;
                Log.i("update","updated Name");
            }catch (Exception e) {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }

        }
        if(address.getText().toString().equals(values.getString("address"))==false){
            String uAddress = address.getText().toString();
            try {
                db = helper.getWritableDatabase();
                db.execSQL("UPDATE customers SET Address ='"+uAddress+"' WHERE id='"+values.getString("id")+"'");
                updated = true;
            }catch (Exception e){
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }

            Log.i("update","updated Name");
        }
        if (number.getText().toString().equals(values.getString("number")) == false){
            Long uNumber = Long.parseLong(number.getText().toString());
            try {
                db = helper.getWritableDatabase();
                db.execSQL("UPDATE customers SET Number ='"+uNumber+"' WHERE id='"+values.getString("id")+"'");
                updated = true;
                Log.i("update","updated uNumber");
            }catch (Exception e){
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }

        }
        if (Arrays.equals(array,values.getByteArray("image"))==false){

            try {
                db = helper.getWritableDatabase();
                SQLiteStatement statement = db.compileStatement("UPDATE customers SET Image = ? WHERE id = ?");
                //db.execSQL("UPDATE customers SET Image ='"+array+"' WHERE id='"+values.getString("id")+"'");
                statement.bindBlob(1,array);
                statement.bindString(2,values.getString("id"));
                statement.execute();
                statement.close();
                updated = true;
                Log.i("update","updated Image");
            }catch (Exception e){
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }

        }
        Log.i("check",String.valueOf(updated));
        if(updated != true){
            Toast.makeText(getApplicationContext(),"nothing to update",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getApplicationContext(),"Successfully updated",Toast.LENGTH_SHORT).show();

        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();

            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getApplicationContext().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap yourSelectedImage = getResizedBitmap(BitmapFactory.decodeFile(picturePath),500);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            yourSelectedImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            array = stream.toByteArray();
            Log.i("byte array",String.valueOf(array));
            customerPhoto.setImageBitmap(BitmapFactory.decodeByteArray(array,0,array.length));


        }


    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }


}
