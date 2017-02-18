package net.robot_inc.watertracking;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

/**
 * Created by Mohan on 2/14/2017.
 */

public class newCustomer extends AppCompatActivity {
    EditText nameField, phoneField,addressField;
    ImageView cusImage;
    byte[] array;
    int RESULT_LOAD_IMAGE = 1;
    SQLiteDatabase db;
    customerDbHelper helper;
    customerDataHelper customerdatahelper;
    Drawable defaultImg;
    Button add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_customer);

        //       and use variable actionBar instead
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Customer");
        array = getDefaultImagebyte();

        nameField = (EditText) findViewById(R.id.et_name);
        phoneField = (EditText) findViewById(R.id.et_phone);
        addressField= (EditText) findViewById(R.id.et_address);
        cusImage = (ImageView) findViewById(R.id.iv_customer_photo);
        cusImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        add = (Button) findViewById(R.id.btn_add);
        helper = new customerDbHelper(getApplicationContext());
        customerdatahelper = new customerDataHelper((getApplicationContext()));
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(nameField.getText().toString())||TextUtils.isEmpty(addressField.getText().toString())||TextUtils.isEmpty(phoneField.getText().toString())){
                    Toast.makeText(getApplicationContext(),"All fields required",Toast.LENGTH_LONG).show();

                }else {
                    try{
                        Log.i("customer","inside try");
                        db = helper.getWritableDatabase();
                        SQLiteStatement statement = db.compileStatement("INSERT INTO customers(Name,Address,Number,Image) VALUES(?,?,?,?)");
                        statement.bindString(1,nameField.getText().toString());
                        statement.bindString(2,addressField.getText().toString());
                        statement.bindLong(3,Long.parseLong(phoneField.getText().toString()));
                        statement.bindBlob(4,array);
                        statement.execute();
                        statement.close();
                        customerdatahelper.createCustomTables(nameField.getText().toString());

                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }




            }
        });

    }
    public byte[] getDefaultImagebyte(){
        defaultImg = ContextCompat.getDrawable(getApplicationContext(),R.drawable.person);
        Bitmap bitmap = ((BitmapDrawable)defaultImg).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitmapdata = stream.toByteArray();
        return bitmapdata;
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
            Bitmap yourSelectedImage = BitmapFactory.decodeFile(picturePath);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            yourSelectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            array = stream.toByteArray();
            cusImage.setImageBitmap(BitmapFactory.decodeByteArray(array,0,array.length));


        }


    }

}
