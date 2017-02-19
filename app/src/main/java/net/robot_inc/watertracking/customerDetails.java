package net.robot_inc.watertracking;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class customerDetails extends AppCompatActivity {
    ImageView customerPhoto;
    EditText name, address, number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("View/Update");


        name = (EditText) findViewById(R.id.et_name);
        address = (EditText) findViewById(R.id.et_address);
        number = (EditText) findViewById(R.id.et_phone);
        customerPhoto = (ImageView) findViewById(R.id.iv_customer_photo);
        Bundle bundle = getIntent().getExtras();
        name.setText(bundle.getString("name"));
        address.setText(bundle.getString("address"));
        number.setText(bundle.getString("number"));
        Bitmap yourSelectedImage = BitmapFactory.decodeByteArray(bundle.getByteArray("image"),0,bundle.getByteArray("image").length);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        yourSelectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] array = stream.toByteArray();
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
}
