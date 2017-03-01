package net.robot_inc.watertracking;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class updateStock extends AppCompatActivity {

    EditText dateField, price,cans;
    Calendar myCalendar;
    SQLiteDatabase db;
    stockHellper stock;
    Bundle values;
    int year;
    int month;
    int date;
    Button update,delete;
    String myFormat = "MM/dd/yy"; //In which you need put here
    String Syear= "";
    String Smonth = "";
    String Sdate = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_stock);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Update Stock");
        values  = getIntent().getExtras();
        myCalendar = Calendar.getInstance();
        stock = new stockHellper(getApplicationContext());
        dateField = (EditText) findViewById(R.id.date);
        price = (EditText) findViewById(R.id.price);
        cans = (EditText) findViewById(R.id.cans);
        myCalendar = Calendar.getInstance();
        dateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }

        });
        update = (Button) findViewById(R.id.update);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDatabase();
            }
        });

        populateValues();
    }
    public void updateDatabase(){
        Log.i("update","updating database");
        Boolean updated = false;
        String m = (String.valueOf(myCalendar.get(Calendar.MONTH)+1).length()>1) ? String.valueOf(myCalendar.get(Calendar.MONTH)+1) : "0"+String.valueOf(myCalendar.get(Calendar.MONTH)+1);
        String d = (String.valueOf(myCalendar.get(Calendar.DATE)).length()>1) ? String.valueOf(myCalendar.get(Calendar.DATE)) : "0"+String.valueOf(myCalendar.get(Calendar.DATE));
        String sdate = String.valueOf(myCalendar.get(Calendar.YEAR))+"-"+m+"-"+d;
        Log.i("matching data",sdate+":"+values.get("date"));
        Log.i("matching data",cans.getText()+":"+values.get("number_of_cans"));
        Log.i("matching data",price.getText()+":"+values.get("price"));
        if((sdate.equals(values.getString("date")) == false)){

            try {
                db = stock.getWritableDatabase();
                db.execSQL("UPDATE stock SET date ='"+sdate+"' WHERE id='"+values.getString("id")+"'");
                updated = true;
            }catch (Exception e){
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }
            Log.i("update","updated date");
        }
        if(cans.getText().toString().equals(values.getString("number_of_cans"))==false){
            int inCans = Integer.parseInt(cans.getText().toString());

            try {
                db = stock.getWritableDatabase();
                db.execSQL("UPDATE stock SET number_of_cans ='"+inCans+"' WHERE id='"+values.getString("id")+"'");
                updated = true;
            }catch (Exception e){
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }

            Log.i("update","updated cans");
        }
        if (price.getText().toString().equals(values.getString("price")) == false){
            int inPrice = Integer.parseInt(price.getText().toString());
            try {
                db = stock.getWritableDatabase();
                db.execSQL("UPDATE stock SET price ='"+inPrice+"' WHERE id='"+values.getString("id")+"'");
                updated = true;
                Log.i("update","updated price");
            }catch (Exception e){
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }

        }
        Log.i("check",String.valueOf(updated));
        if(updated != true){
            Toast.makeText(getApplicationContext(),"nothing to update",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getApplicationContext(),"Successfully updated",Toast.LENGTH_SHORT).show();

        }
    }
    public void deleteRow(){
        AlertDialog.Builder alert_box=new AlertDialog.Builder(this);

        alert_box.setMessage("Are you Sure, Do want to delete the data?");
        alert_box.setPositiveButton("Yes",new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                    db = stock.getWritableDatabase();
                    db.execSQL("DELETE FROM stock WHERE id='"+values.getString("id")+"'");
                    Toast.makeText(getApplicationContext(), "Successfully Deleted the data", Toast.LENGTH_SHORT).show();
                    finish();

                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }



            }
        });
        alert_box.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "No Button Clicked", Toast.LENGTH_LONG).show();
            }
        });
        alert_box.show();
    }

    private void populateValues() {

        price.setText(values.getString("price"));
        cans.setText(values.getString("number_of_cans"));
        String dateString = values.getString("date");

        for (int a = 0;a<dateString.length()-1;a++){
            if (a<4){
                Syear = Syear+dateString.charAt(a);
            }else if(a == 4&&dateString.charAt(a)=='-'){
                if(dateString.charAt(a+2)=='-'){
                    Smonth = "0"+dateString.charAt(a+1);
                    if(a+4==dateString.length()){
                        Sdate = "0"+dateString.charAt(a+3);

                    }else if(a+5==dateString.length()){
                        Sdate = String.valueOf(dateString.charAt(a+3))+String.valueOf(dateString.charAt(a+4));

                    }

                } else if (dateString.charAt(a + 3) == '-') {
                    Smonth = String.valueOf(dateString.charAt(a+1))+String.valueOf(dateString.charAt(a+2));
                    if(a+5==dateString.length()){
                        Sdate = "0"+dateString.charAt(a+4);

                    }else if(a+6==dateString.length()){
                        Sdate = String.valueOf(dateString.charAt(a+4))+String.valueOf(dateString.charAt(a+5));

                    }
                }
            }

        }
        year = Integer.parseInt(Syear);
        month = Integer.parseInt(Smonth);
        date = Integer.parseInt(Sdate);
        Log.i("date",Syear+"-"+Smonth+"-"+Sdate);

        myCalendar.set(Integer.parseInt(Syear),Integer.parseInt(Smonth)-1,Integer.parseInt(Sdate));
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM yyyy");
        Date dateS = new Date(myCalendar.getTimeInMillis());
        String adate = sdf.format(dateS);
        Syear = "";
        Smonth = "";
        Sdate = "";
        dateField.setText(adate);


    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new updateStock.DatePickerFragment();
        Bundle dates = new Bundle();
        dates.putInt("year",year);
        dates.putInt("month",month);
        dates.putInt("date",date);
        newFragment.setArguments(dates);
        newFragment.show(getFragmentManager(), "datePicker");
    }
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();

            int year = getArguments().getInt("year");
            int month = getArguments().getInt("month")-1;
            int day = getArguments().getInt("date");


            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            updateStock a = (updateStock) getActivity();
            a.myCalendar.set(year,month,day);
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMMM yyyy");
            Date dateString = new Date(a.myCalendar.getTimeInMillis());
            String adate = sdf.format(dateString);
            ((EditText) getActivity().findViewById(R.id.date)).setText(adate);

        }


    }

}
