package net.robot_inc.watertracking;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static net.robot_inc.watertracking.R.color.grey;

/**
 * Created by mohan on 18/2/17.
 */

public class customerDataAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> userID;
    ArrayList<String> Date;
    ArrayList<String> No_of_cans;
    ArrayList<String> Price ;
    ArrayList<String> Paid;
    String year= "";
    String month = "";
    String Sdate = "";
    public customerDataAdapter(
            Context context2,
            ArrayList<String> userID,
            ArrayList<String> Date,
            ArrayList<String> No_of_cans,
            ArrayList<String> Price,
            ArrayList<String> Paid   )
    {

        this.context = context2;
        this.userID = userID;
        this.Date= Date;
        this.No_of_cans = No_of_cans;
        this.Price = Price;
        this.Paid = Paid;
    }
    public String getId(int position){ return userID.get(position);}
    public String getDate(int position){ return Date.get(position);}
    public String getNo_of_cans(int position){ return No_of_cans.get(position);}
    public String getPrice(int position){ return Price.get(position);}
    public String getPaid(int position) { return Paid.get(position);}
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
        customerDataAdapter.Holder holder;

        LayoutInflater layoutInflater;

        if (child == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            child = layoutInflater.inflate(R.layout.records_layout, null);

            holder = new customerDataAdapter.Holder();


            holder.date= (TextView) child.findViewById(R.id.viewDate);
            holder.No_of_cans= (TextView) child.findViewById(R.id.viewNo_of_cans);
            holder.price= (TextView) child.findViewById(R.id.viewPrice);
            holder.paid= (TextView) child.findViewById(R.id.viewPaid);

            child.setTag(holder);

        } else {

            holder = (customerDataAdapter.Holder) child.getTag();
        }
        String actualDate = Date.get(position);
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
        java.util.Date actualdate = new Date(c.getTimeInMillis());
        String adate = sdf.format(actualdate);
        year = "";
        month = "";
        Sdate = "";


        holder.date.setText(adate);
        holder.No_of_cans.setText(No_of_cans.get(position));
        holder.price.setText(Price.get(position));
        holder.paid.setText(Paid.get(position));
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
        if (position % 2 == 1) {
            child.setBackgroundColor(Color.GREEN);
        } else {
            child.setBackgroundColor(Color.YELLOW);
        }
        return child;
    }

    public class Holder {

        TextView date;
        TextView No_of_cans;
        TextView price;
        TextView paid;
    }
}
