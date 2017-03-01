package net.robot_inc.watertracking;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class addRecord extends AppCompatActivity {
    EditText dateField, price,cans,amountPaid;
    Button addrecord;
    Calendar myCalendar;
    SQLiteDatabase db;
    String table_name = "";
    customerDataHelper customerHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_record);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("Add Records");
        myCalendar = Calendar.getInstance();
        customerHelper = new customerDataHelper(getApplicationContext());
        dateField = (EditText) findViewById(R.id.et_date);
        price = (EditText) findViewById(R.id.et_price);
        cans = (EditText) findViewById(R.id.et_noofcans);
        amountPaid = (EditText) findViewById(R.id.et_paid);
        table_name = getIntent().getExtras().getString("table_name");
        addrecord = (Button) findViewById(R.id.btn_addrecord);
        addrecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("strings",cans.getText().toString());
                insertData(dateField.getText().toString(),cans.getText().toString(),price.getText().toString(),amountPaid.getText().toString());
            }


        });
        dateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }

        });

    }

    private void insertData(String tdate, String tcans, String tprice, String paid) {
        if(TextUtils.isEmpty(tcans)){
            tcans = "0";
        }if (TextUtils.isEmpty(tprice)){
            tprice = "0";

        }if(TextUtils.isEmpty(paid)){
            paid = "0";
        }
        int inCans = Integer.parseInt(tcans);
        double inPrice = Integer.parseInt(tprice);
        double inamount = Integer.parseInt(paid);
        String m = (String.valueOf(myCalendar.get(Calendar.MONTH)+1).length()>1) ? String.valueOf(myCalendar.get(Calendar.MONTH)+1) : "0"+String.valueOf(myCalendar.get(Calendar.MONTH)+1);
        String d = (String.valueOf(myCalendar.get(Calendar.DATE)).length()>1) ? String.valueOf(myCalendar.get(Calendar.DATE)) : "0"+String.valueOf(myCalendar.get(Calendar.DATE));

        String sdate = String.valueOf(myCalendar.get(Calendar.YEAR))+"-"+m+"-"+d;
        if(TextUtils.isEmpty(tdate)){
            Toast.makeText(getApplicationContext(),"Date is Required",Toast.LENGTH_LONG).show();
        }else if(inCans == 0){
            if(inPrice == 0){
                if(inamount==0){
                    Toast.makeText(getApplicationContext(),"Nothing to add record",Toast.LENGTH_LONG).show();

                }else {
                    db = customerHelper.getWritableDatabase();
                    try {
                        db.execSQL("INSERT INTO "+table_name+" (Date, No_of_cans, Price, Paid) VALUES('" + sdate + "'," + inCans + "," + inPrice + ","+inamount+")");
                        db.close();
                        Toast.makeText(getApplicationContext(),"Successfully added!",Toast.LENGTH_SHORT).show();
                        dateField.setText("");
                        cans.setText("");
                        price.setText("");
                        amountPaid.setText("");

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }else {
                Toast.makeText(getApplicationContext(),"Please check your no. of cans before entering price",Toast.LENGTH_LONG).show();
            }
        }else {
            if (inPrice == 0){
                Toast.makeText(getApplicationContext(),"Pleae enter the price",Toast.LENGTH_LONG).show();

            }else {
                db = customerHelper.getWritableDatabase();
                try {
                    db.execSQL("INSERT INTO "+table_name+" (Date, No_of_cans, Price, Paid) VALUES('" + sdate + "'," + inCans + "," + inPrice + ","+inamount+")");
                    db.close();
                    Toast.makeText(getApplicationContext(),"Successfully added!",Toast.LENGTH_SHORT).show();
                    dateField.setText("");
                    cans.setText("");
                    price.setText("");
                    amountPaid.setText("");

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
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
            addRecord a = (addRecord) getActivity();
            a.myCalendar.set(year,month,day);
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMMM yyyy");
            Date actualdate = new Date(a.myCalendar.getTimeInMillis());
            String adate = sdf.format(actualdate);
            ((EditText) getActivity().findViewById(R.id.et_date)).setText(adate);

        }


    }
}
