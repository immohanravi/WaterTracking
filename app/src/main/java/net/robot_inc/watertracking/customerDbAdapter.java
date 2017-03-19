package net.robot_inc.watertracking;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by Mohan on 2/14/2017.
 */

public class customerDbAdapter extends BaseAdapter {
    Holder holder;
    Context context;
    ArrayList<String> userID;
    ArrayList<String> Name;
    ArrayList<String> Address;
    ArrayList<String> Number ;
    ArrayList<Bitmap> Image;
    HashMap<String,Integer> pendingAmount;
    public customerDbAdapter(
            Context context2,
            ArrayList<String> id,
            ArrayList<String> Name,
            ArrayList<String> Address,
            ArrayList<String> Number,
            ArrayList<Bitmap> Image,
            HashMap<String,Integer> pendingAmount)
    {

        this.context = context2;
        this.userID = id;
        this.Name = Name;
        this.Address = Address;
        this.Number = Number;
        this.Image = Image;
        this.pendingAmount = pendingAmount;
    }
    public String getId(int position){ return userID.get(position);}
    public String getName(int position){ return Name.get(position);}
    public String getAddress(int position){ return Address.get(position);}
    public String getNumber(int position){ return Number.get(position);}
    public Bitmap getImage(int position) { return Image.get(position);}

    @Override
    public int getCount() {
        return userID.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View child, ViewGroup parent) {
        Holder holder;

        LayoutInflater layoutInflater;

        if (child == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            child = layoutInflater.inflate(R.layout.customer_view, null);

            holder = new Holder();


            holder.name = (TextView) child.findViewById(R.id.name);
            holder.address= (TextView) child.findViewById(R.id.Address);
            holder.address.setTextColor(Color.BLACK);
            holder.number= (TextView) child.findViewById(R.id.tv_phone_number);
            holder.number.setTextColor(Color.MAGENTA);
            holder.image= (ImageView) child.findViewById(R.id.image);
            holder.amt = (TextView) child.findViewById(R.id.amt);
            holder.amt.setTextColor(Color.BLACK);

            child.setTag(holder);

        } else {

            holder = (Holder) child.getTag();
        }

        holder.name.setText(Name.get(position));
        holder.address.setText(Address.get(position));
        holder.number.setText(Number.get(position));

        holder.image.setImageBitmap(Image.get(position));
        holder.amt.setText("Pending Money : "+String.valueOf(pendingAmount.get(Name.get(position))));
        Log.i("name",Name.get(position));

        /*AsyncTask<byte[], Void, Bitmap> map = gImage.execute();
        try {
            holder.image.setImageBitmap(map.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
*/
        Log.i("customer","got view");
        return child;
    }

    public class Holder {

        TextView name;
        TextView address;
        TextView number;
        ImageView image;
        TextView amt;
    }
}
