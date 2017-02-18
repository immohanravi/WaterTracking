package net.robot_inc.watertracking;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

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
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
