package net.robot_inc.watertracking;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.database.sqlite.SQLiteDatabase;

import java.util.*;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class addStock extends AppCompatActivity {
    EditText dateField, price,cans;
    Calendar myCalendar;
    SQLiteDatabase db;
    stockHellper stock;
    int year;
    int month;
    int date;
    Button add;
    String myFormat = "MM/dd/yy"; //In which you need put here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_stock);

        //       and use variable actionBar instead

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
      getSupportActionBar().setTitle("Add Stock");

        myCalendar = Calendar.getInstance();
        add = (Button) findViewById(R.id.add);
        stock = new stockHellper(getApplicationContext());
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertData(dateField.getText().toString(),cans.getText().toString(),price.getText().toString());
            }
        });

        dateField = (EditText) findViewById(R.id.date);
        price = (EditText) findViewById(R.id.price);
        cans = (EditText) findViewById(R.id.cans);
        dateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }

        });
    }
    public void insertData(String stockDate,String noOfcans, String stockPrice){

            String m = (String.valueOf(myCalendar.get(Calendar.MONTH)+1).length()>1) ? String.valueOf(myCalendar.get(Calendar.MONTH)+1) : "0"+String.valueOf(myCalendar.get(Calendar.MONTH)+1);
            String d = (String.valueOf(myCalendar.get(Calendar.DATE)).length()>1) ? String.valueOf(myCalendar.get(Calendar.DATE)) : "0"+String.valueOf(myCalendar.get(Calendar.DATE));

        String sdate = String.valueOf(myCalendar.get(Calendar.YEAR))+"-"+m+"-"+d;
            Log.i("addStock",sdate);
            
        if(TextUtils.isEmpty(stockDate)||TextUtils.isEmpty(noOfcans)||TextUtils.isEmpty(stockPrice)){

            Toast.makeText(getApplicationContext(),"All fields required",Toast.LENGTH_LONG).show();
        }
        else {
            int inCans = Integer.parseInt(noOfcans);
            int inPrice = Integer.parseInt(stockPrice);
            db = stock.getWritableDatabase();
            try {
                db.execSQL("INSERT INTO stock(date, number_of_cans, price) VALUES('" + sdate + "'," + inCans + "," + inPrice + ")");
                db.close();
                Toast.makeText(getApplicationContext(),"Successfully added!",Toast.LENGTH_SHORT).show();
                dateField.setText("");
                cans.setText("");
                price.setText("");
                finish();

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }

    }
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);


            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            addStock a = (addStock) getActivity();
            a.myCalendar.set(year,month,day);
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMMM yyyy");
            Date actualdate = new Date(a.myCalendar.getTimeInMillis());
            String adate = sdf.format(actualdate);
            ((EditText) getActivity().findViewById(R.id.date)).setText(adate);

        }


    }


}
