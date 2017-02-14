package net.robot_inc.watertracking;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Mohan on 2/5/2017.
 */

public class stockAdapter extends BaseAdapter implements Filterable{



    Context context;
    ArrayList<String> userID;
    ArrayList<String> date;
    ArrayList<String> no_of_cans;
    ArrayList<String> price ;
    String year= "";
    String month = "";
    String Sdate = "";

    public stockAdapter(
            Context context2,
            ArrayList<String> id,
            ArrayList<String> date,
            ArrayList<String> no_of_cans,
            ArrayList<String> price
    )
    {

        this.context = context2;
        this.userID = id;
        this.date = date;
        this.no_of_cans = no_of_cans;
        this.price = price;
    }
    @Override
    public int getCount() {
        return userID.size();

    }
    public String getId(int position){ return userID.get(position);}
    public String getDate(int position){ return date.get(position);}
    public String getNo_of_cans(int position){ return no_of_cans.get(position);}
    public String getPrice(int position){ return price.get(position);}

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
            child = layoutInflater.inflate(R.layout.stock_view_helper, null);

            holder = new Holder();


            holder.textViewDate = (TextView) child.findViewById(R.id.textViewDate);
            holder.textviewNo_of_cans = (TextView) child.findViewById(R.id.textViewNo_of_cans);
            holder.textviewPrice = (TextView) child.findViewById(R.id.textViewPrice);

            child.setTag(holder);

        } else {

            holder = (Holder) child.getTag();
        }

        String actualDate = date.get(position);
        Log.i("length",String.valueOf(actualDate.length()));


        for (int a = 0;a<actualDate.length()-1;a++){
            if (a<4){
                year = year+actualDate.charAt(a);
            }else if(a == 4&&actualDate.charAt(a)=='-'){
                if(actualDate.charAt(a+2)=='-'){
                    month = "0"+actualDate.charAt(a+1);
                    if(a+4==actualDate.length()){
                        Sdate = "0"+actualDate.charAt(a+3);

                    }else if(a+5==actualDate.length()){
                        Sdate = String.valueOf(actualDate.charAt(a+3))+String.valueOf(actualDate.charAt(a+4));

                    }

                } else if (actualDate.charAt(a + 3) == '-') {
                    month = String.valueOf(actualDate.charAt(a+1))+String.valueOf(actualDate.charAt(a+2));
                    if(a+5==actualDate.length()){
                        Sdate = "0"+actualDate.charAt(a+4);

                    }else if(a+6==actualDate.length()){
                        Sdate = String.valueOf(actualDate.charAt(a+4))+String.valueOf(actualDate.charAt(a+5));

                    }
                }
            }

        }
        Log.i("date",year+"-"+month+"-"+Sdate);
        Calendar c = Calendar.getInstance();
        c.set(Integer.parseInt(year),Integer.parseInt(month)-1,Integer.parseInt(Sdate));
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM yyyy");
        Date actualdate = new Date(c.getTimeInMillis());
        String adate = sdf.format(actualdate);
        year = "";
        month = "";
        Sdate = "";

        holder.textViewDate.setText(adate);
        holder.textviewNo_of_cans.setText(no_of_cans.get(position));
        holder.textviewPrice.setText(price.get(position));

        return child;
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    public class Holder {

        TextView textViewDate;
        TextView textviewNo_of_cans;
        TextView textviewPrice;
    }
}
