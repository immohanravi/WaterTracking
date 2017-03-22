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
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
    String firstLetterCaps = "";
    SQLiteDatabase db;
    customerDbHelper helper;
    customerDataHelper customerdatahelper;
    Drawable defaultImg;
    String nameBefore;
    boolean nameOk = false;
    Button add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_customer);

        //       and use variable actionBar instead
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("Add Customer");
        array = getDefaultImagebyte(R.drawable.person);

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
        nameBefore = "";
        nameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                nameBefore = String.valueOf(s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String value = String.valueOf(s);
                for(int a = 0;a<value.length();a++){
                    int ascii = (int) value.charAt(a);
                    Log.i("ascii value",String.valueOf((value.charAt(a))));
                    if(ascii >=97 && ascii<=122){
                        continue;
                    }else if(ascii >=65 && ascii<=90){
                        continue;
                    }else if(ascii == 32){
                        continue;
                    }else if(ascii >=48 && ascii<=57){
                        continue;
                    }else {
                        Toast.makeText(getApplicationContext(),"Symbols not allowed",Toast.LENGTH_LONG).show();
                        nameField.setText(nameBefore);

                    }
                }
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(TextUtils.isEmpty(nameField.getText().toString())||TextUtils.isEmpty(addressField.getText().toString())||TextUtils.isEmpty(phoneField.getText().toString())){
                    Toast.makeText(getApplicationContext(),"All fields required",Toast.LENGTH_LONG).show();

                }else {
                    try{

                        firstLetterCaps = nameField.getText().toString();
                        firstLetterCaps = firstLetterCaps.replaceFirst(String.valueOf(firstLetterCaps.charAt(0)),String.valueOf(firstLetterCaps.charAt(0)).toUpperCase());

                        Log.i("customer","inside try");
                        db = helper.getWritableDatabase();
                        SQLiteStatement statement = db.compileStatement("INSERT INTO customers(Name,Address,Number,Image) VALUES(?,?,?,?)");
                        statement.bindString(1,firstLetterCaps);
                        statement.bindString(2,addressField.getText().toString());
                        statement.bindLong(3,Long.parseLong(phoneField.getText().toString()));
                        statement.bindBlob(4,array);
                        statement.execute();
                        statement.close();
                        db.close();
                        customerdatahelper.createCustomTables(nameField.getText().toString().toLowerCase().replaceAll(" ",""));
                        nameField.setText("");
                        phoneField.setText("");
                        addressField.setText("");
                        array = getDefaultImagebyte(R.drawable.addimage);
                        cusImage.setImageBitmap(BitmapFactory.decodeByteArray(array,0,array.length));


                    }catch (Exception e){
                        if(e.getMessage().toString().contains("UNIQUE")){

                            db = helper.getReadableDatabase();
                            Cursor c = db.rawQuery("SELECT * FROM customers WHERE Number = '"+phoneField.getText().toString()+"' OR Name = '"+firstLetterCaps+"'",null);
                            if (c.moveToFirst()){
                                String name = c.getString(c.getColumnIndex(helper.KEY_Name));
                                String address = c.getString(c.getColumnIndex(helper.KEY_Address));
                                Toast.makeText(getApplicationContext(),"Name or Phone Number Already Exist for the Customer\nName = "+name+"\nAddress = "+address,Toast.LENGTH_LONG).show();
                            }
                            c.close();

                        }else {
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();

                        }

                                            }
                }




            }
        });

    }
    public byte[] getDefaultImagebyte(int id){
        defaultImg = ContextCompat.getDrawable(getApplicationContext(),id);
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
            Bitmap yourSelectedImage = getResizedBitmap(BitmapFactory.decodeFile(picturePath),500);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            yourSelectedImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            array = stream.toByteArray();
            Log.i("byte array",String.valueOf(array));
            cusImage.setImageBitmap(BitmapFactory.decodeByteArray(array,0,array.length));


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
