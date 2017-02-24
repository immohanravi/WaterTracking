package net.robot_inc.watertracking;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by mohan on 21/2/17.
 */

public class getImage extends AsyncTask<byte[], Void, Bitmap> {
    private final WeakReference<byte[]> imageViewReference;

    public getImage(byte[] imageView) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<byte[]>(imageView);
    }

    // Decode image in background.


    @Override
    protected Bitmap doInBackground(byte[]... params) {

        Bitmap bitmap = null;
        byte[] array = params[0];
        if (params != null) {
            bitmap = BitmapFactory.decodeByteArray(array, 0, array.length);
            return bitmap;
        }
        return bitmap;
    }

    // Once complete, see if ImageView is still around and set bitmap.

}
