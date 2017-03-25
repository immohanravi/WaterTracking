package net.robot_inc.watertracking;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by takaiwa.net on 2016/04/08.
 */
public class Adapter extends ArrayAdapter<Data> {
    ImageView menuImage;
    final int INVALID_ID = -1;

    Bundle values;
    View view;
    public interface Listener {
        void onGrab(int position, RelativeLayout row);
    }

    final Listener listener;
    final Map<Data, Integer> mIdMap = new HashMap<>();

    public Adapter(Context context, List<Data> list, Listener listener) {
        super(context, 0, list);
        this.listener = listener;
        for (int i = 0; i < list.size(); ++i) {
            mIdMap.put(list.get(i), i);
        }

    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {


        final Context context = getContext();
        final Data data = getItem(position);
        if(null == view) {
            view = LayoutInflater.from(context).inflate(R.layout.customer_view, null);
        }

        final RelativeLayout row = (RelativeLayout) view.findViewById(
                R.id.lytPattern);

        TextView textView = (TextView)view.findViewById(R.id.customerName);
        textView.setText(data.name);

        TextView timerange = (TextView)view.findViewById(R.id.txtNnumber);
        timerange.setText(data.number);
        TextView address = (TextView)view.findViewById(R.id.txtNaddress);
        address.setText(data.address);
        TextView amt = (TextView)view.findViewById(R.id.txtNpendingAmt);
        amt.setText("Pending Amount = "+data.pendingAmount);


        ImageView image = (ImageView) view.findViewById(R.id.customerImage);
        image.setImageBitmap(data.image);
        view.findViewById(R.id.imageViewGrab)
            .setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    listener.onGrab(position, row);

                    return false;
                }
            });
        final View finalView = view;
        view.findViewById(R.id.dotImg)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        values = new Bundle();
                        Bitmap bitmap = data.image;
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] bitmapdata = stream.toByteArray();
                        values.putString("id", data.id);
                        values.putString("name", data.name.replaceAll(" ", ""));
                        values.putString("realname", data.name);
                        values.putString("address", data.address);
                        values.putString("number", data.number);
                        values.putByteArray("image", bitmapdata);
                        PopupMenu menu = new PopupMenu(v.getContext(),v);
                        menu.getMenuInflater().inflate(R.menu.customer_context_menu,menu.getMenu());
                        menu.show();
                        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {


                                switch (item.getItemId()) {
                                    case R.id.viewEdit: {
                                        try {
                                            Intent intent = new Intent(v.getContext(), customerDetails.class);
                                            intent.putExtras(values);
                                           finalView.getContext().startActivity(intent);
                                        } catch (Exception e) {
                                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                        }


                                        return true;
                                    }
                                    case R.id.delete:{
                                        deleteRow();
                                        return true;

                                    }

                                    case R.id.call: {
                                        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + values.getString("number")));
                                        finalView.getContext().startActivity(callIntent);
                                        return true;
                                    }

                                    default:
                                        return false;
                                }


                            }
                        });
                    }
                });


        view.findViewById( R.id.lytPatternText)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        values = new Bundle();
                        Bitmap bitmap = data.image;
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] bitmapdata = stream.toByteArray();
                        values.putString("id", data.id);
                        values.putString("name", data.name.replaceAll(" ", ""));
                        values.putString("realname", data.name);
                        values.putString("address", data.address);
                        values.putString("number", data.number);
                        values.putByteArray("image", bitmapdata);

                        try {
                            Intent intent = new Intent(getContext(), viewModifyRecords.class);
                            intent.putExtras(values);
                            getContext().startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
        return view;
    }

    @Override
    public long getItemId(int position) {
        if (position < 0 || position >= mIdMap.size()) {
            return INVALID_ID;
        }
        Data item = getItem(position);
        return mIdMap.get(item);
    }
    public void deleteRow() {

        final AlertDialog.Builder alert_box = new AlertDialog.Builder(getContext());
        alert_box.setMessage("Are you Sure, Do you want to Remove this customer from list?\nAll the records with be deleted.");
        alert_box.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dia, int which) {
                try {
                    customerDbHelper SQLITEHELPER = new customerDbHelper(getContext());
                    SQLiteDatabase SQLITEDATABASE = SQLITEHELPER.getWritableDatabase();
                    SQLITEDATABASE.execSQL("DELETE FROM customers WHERE id='" + values.getString("id") + "'");
                    customerDataHelper customerDataHelper = new customerDataHelper(getContext());
                    customerDataHelper.dropTable(values.getString("name").toLowerCase().replaceAll(" ", ""));
                    notifyDataSetChanged();

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
    @Override
    public boolean hasStableIds() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    }
}
