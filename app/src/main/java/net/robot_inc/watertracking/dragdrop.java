package net.robot_inc.watertracking;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

public class dragdrop extends AppCompatActivity {
    Drawable defaultImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dragdrop);

        final List<Data> list = Arrays.asList(
                new Data(getDefaultImagebyte(R.drawable.person),"1,", "Mohan","9505953126","Pamidi"),
                new Data(getDefaultImagebyte(R.drawable.images),"2", "Arun","9505953123","chennai"),
                new Data(getDefaultImagebyte(R.drawable.images),"3", "Arun","9505953123","chennai")


        );

        final CustomListView listView = (CustomListView)findViewById(R.id.listView1);
        Adapter adapter = new Adapter(this, list, new Adapter.Listener() {
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


    }
    public Bitmap getDefaultImagebyte(int id){
       defaultImg = ContextCompat.getDrawable(getBaseContext(),id);
        Bitmap bitmap = ((BitmapDrawable)defaultImg).getBitmap();
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
}
