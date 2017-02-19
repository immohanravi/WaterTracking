package net.robot_inc.watertracking;

import android.content.Context;
import android.graphics.BitmapFactory;
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
    ArrayList<byte[]> Image;
    public customerDbAdapter(
            Context context2,
            ArrayList<String> id,
            ArrayList<String> Name,
            ArrayList<String> Address,
            ArrayList<String> Number,
            ArrayList<byte[]> Image    )
    {

        this.context = context2;
        this.userID = id;
        this.Name = Name;
        this.Address = Address;
        this.Number = Number;
        this.Image = Image;
    }
    public String getId(int position){ return userID.get(position);}
    public String getName(int position){ return Name.get(position);}
    public String getAddress(int position){ return Address.get(position);}
    public String getNumber(int position){ return Number.get(position);}
    public byte[] getImage(int position) { return Image.get(position);}

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
            holder.number= (TextView) child.findViewById(R.id.tv_phone_number);
            holder.image= (ImageView) child.findViewById(R.id.image);

            child.setTag(holder);

        } else {

            holder = (Holder) child.getTag();
        }

        holder.name.setText(Name.get(position));
        holder.address.setText(Address.get(position));
        holder.number.setText(Number.get(position));
        holder.image.setImageBitmap(BitmapFactory.decodeByteArray(Image.get(position),0,Image.get(position).length));
        Log.i("customer","got view");
        return child;
    }

    public class Holder {

        TextView name;
        TextView address;
        TextView number;
        ImageView image;
    }
}
