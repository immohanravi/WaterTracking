package net.robot_inc.watertracking;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class updateRecords extends AppCompatActivity {
    SQLiteDatabase db;
    customerDataHelper dh;
    EditText datefield, canfield, pricefield, amountfield;
    Button update;
    Bundle values;
    String Syear = "";
    String Smonth = "";
    String Sdate = "";
    String table_name;
    int year;
    int month;
    int date;
    Calendar myCalendar;
    int AvailableStock = 0;
    int presentStock = 0;
    int stock = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_records);
        getSupportActionBar().setTitle("update records");

        dh = new customerDataHelper(getApplicationContext());
        datefield = (EditText) findViewById(R.id.etdate);
        canfield = (EditText) findViewById(R.id.etnoofcans);
        pricefield = (EditText) findViewById(R.id.etprice);
        amountfield = (EditText) findViewById(R.id.etpaid);

        values = getIntent().getExtras();
        presentStock = Integer.parseInt(values.getString("cans"));
        AvailableStock = getAvailableStock() + presentStock;
        table_name = values.getString("name");
        myCalendar = Calendar.getInstance();
        datefield.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }

        });
        update = (Button) findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int a = 0;
                int b = 0;
                int c = 0;
                if (!TextUtils.isEmpty(canfield.getText().toString())) {
                    a = Integer.parseInt(canfield.getText().toString());
                }
                if (!TextUtils.isEmpty(pricefield.getText().toString())) {
                    b = Integer.parseInt(pricefield.getText().toString());
                }
                if (!TextUtils.isEmpty(amountfield.getText().toString())) {
                    c = Integer.parseInt(amountfield.getText().toString());
                }
                String m = (String.valueOf(myCalendar.get(Calendar.MONTH) + 1).length() > 1) ? String.valueOf(myCalendar.get(Calendar.MONTH) + 1) : "0" + String.valueOf(myCalendar.get(Calendar.MONTH) + 1);
                String d = (String.valueOf(myCalendar.get(Calendar.DATE)).length() > 1) ? String.valueOf(myCalendar.get(Calendar.DATE)) : "0" + String.valueOf(myCalendar.get(Calendar.DATE));
                String sdate = String.valueOf(myCalendar.get(Calendar.YEAR)) + "-" + m + "-" + d;
                Log.i("matching data", sdate + ":" + values.get("date"));
                Log.i("matching data", canfield.getText() + ":" + values.get("number_of_cans"));
                Log.i("matching data", pricefield.getText() + ":" + values.get("price"));



                if (a <= AvailableStock) {
                    if ((sdate.equals(values.getString("date")) == false)) {

                        try {
                            db = dh.getWritableDatabase();
                            db.execSQL("UPDATE " + table_name + " SET Date ='" + sdate + "' WHERE id='" + values.getString("id") + "'");

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        Log.i("update", "updated date");
                    }
                    if (a == 0) {

                        if (b == 0) {
                            if (c == 0) {
                                Toast.makeText(getApplicationContext(), "Nothing to add record", Toast.LENGTH_LONG).show();
                            } else {
                                updateDatabase(a, b, c);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Please check your no. of cans before entering price", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        if (b == 0) {
                            Toast.makeText(getApplicationContext(), "Please enter the price", Toast.LENGTH_LONG).show();

                        } else {
                            updateDatabase(a, b, c);
                        }

                    }


                } else {
                    Toast.makeText(getApplicationContext(), "There is no enough cans\nAvailable Stock = " + AvailableStock, Toast.LENGTH_LONG).show();

                }

            }
        });
        canfield.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!TextUtils.isEmpty((String.valueOf(s)))) {

                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int incans = 0;
                int inprice = 0;
                if (!TextUtils.isEmpty(String.valueOf(s))) {
                    incans = Integer.parseInt(String.valueOf(s));
                    if (!TextUtils.isEmpty(pricefield.getText().toString())) {
                        inprice = Integer.parseInt(pricefield.getText().toString());

                    }
                }


                amountfield.setHint(String.valueOf("Payable Amount = " + incans * inprice));

            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });

        pricefield.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int incans = 0;
                int inprice = 0;
                if (!TextUtils.isEmpty(String.valueOf(s))) {
                    inprice = Integer.parseInt(String.valueOf(s));
                    if (!TextUtils.isEmpty(canfield.getText().toString())) {
                        incans = Integer.parseInt(canfield.getText().toString());
                    }
                }
                amountfield.setHint(String.valueOf("Payable Amount = " + incans * inprice));

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        populateValues();


    }

    public void updateDatabase(int inCans, int inPrice, int inAmount) {
        Log.i("update", "updating database");
        Boolean updated = false;

        if ((canfield.getText().toString().equals(values.getString("number_of_cans")) == false)) {


            try {
                db = dh.getWritableDatabase();
                db.execSQL("UPDATE " + table_name + " SET No_of_cans ='" + inCans + "' WHERE id='" + values.getString("id") + "'");
                updated = true;
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            Log.i("update", "updated cans");
        }
        if (pricefield.getText().toString().equals(values.getString("price")) == false) {

            try {
                db = dh.getWritableDatabase();
                db.execSQL("UPDATE " + table_name + " SET Price ='" + inPrice + "' WHERE id='" + values.getString("id") + "'");
                updated = true;
                Log.i("update", "updated price");
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }

        if (amountfield.getText().toString().equals(values.getString("paid")) == false) {

            try {
                db = dh.getWritableDatabase();
                db.execSQL("UPDATE " + table_name + " SET Paid ='" + inAmount + "' WHERE id='" + values.getString("id") + "'");
                updated = true;
                Log.i("update", "updated price");
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
        Log.i("check", String.valueOf(updated));
        if (updated != true) {
            Toast.makeText(getApplicationContext(), "nothing to update", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Successfully updated", Toast.LENGTH_SHORT).show();

        }
    }

    private void populateValues() {

        pricefield.setText(values.getString("price"));
        canfield.setText(values.getString("cans"));
        amountfield.setText(values.getString("paid"));
        String dateString = values.getString("date");

        for (int a = 0; a < dateString.length() - 1; a++) {
            if (a < 4) {
                Syear = Syear + dateString.charAt(a);
            } else if (a == 4 && dateString.charAt(a) == '-') {
                if (dateString.charAt(a + 2) == '-') {
                    Smonth = "0" + dateString.charAt(a + 1);
                    if (a + 4 == dateString.length()) {
                        Sdate = "0" + dateString.charAt(a + 3);

                    } else if (a + 5 == dateString.length()) {
                        Sdate = String.valueOf(dateString.charAt(a + 3)) + String.valueOf(dateString.charAt(a + 4));

                    }

                } else if (dateString.charAt(a + 3) == '-') {
                    Smonth = String.valueOf(dateString.charAt(a + 1)) + String.valueOf(dateString.charAt(a + 2));
                    if (a + 5 == dateString.length()) {
                        Sdate = "0" + dateString.charAt(a + 4);
                    } else if (a + 6 == dateString.length()) {
                        Sdate = String.valueOf(dateString.charAt(a + 4)) + String.valueOf(dateString.charAt(a + 5));

                    }
                }
            }

        }
        year = Integer.parseInt(Syear);
        month = Integer.parseInt(Smonth);
        date = Integer.parseInt(Sdate);
        Log.i("date", Syear + "-" + Smonth + "-" + Sdate);

        myCalendar.set(Integer.parseInt(Syear), Integer.parseInt(Smonth) - 1, Integer.parseInt(Sdate));
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM yyyy");
        Date dateS = new Date(myCalendar.getTimeInMillis());
        String adate = sdf.format(dateS);
        Syear = "";
        Smonth = "";
        Sdate = "";
        datefield.setText(adate);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        Bundle dates = new Bundle();
        dates.putInt("year", year);
        dates.putInt("month", month);
        dates.putInt("date", date);
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
            int month = getArguments().getInt("month") - 1;
            int day = getArguments().getInt("date");


            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            updateRecords a = (updateRecords) getActivity();
            a.myCalendar.set(year, month, day);
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMMM yyyy");
            Date dateString = new Date(a.myCalendar.getTimeInMillis());
            String adate = sdf.format(dateString);
            ((EditText) getActivity().findViewById(R.id.etdate)).setText(adate);

        }


    }

    private int getAvailableStock() {
        int answer = 0;
        ArrayList<String> Name_ArrayList = new ArrayList<>();
        SQLiteDatabase database;
        stockHellper sh;
        sh = new stockHellper(getApplicationContext());
        database = sh.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM stock", null);
        if (cursor.moveToFirst()) {
            do {
                answer = answer + cursor.getInt(2);

            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        customerDbHelper ch = new customerDbHelper(getApplicationContext());
        database = ch.getWritableDatabase();
        cursor = database.rawQuery("SELECT Name FROM customers", null);
        if (cursor.moveToFirst()) {
            do {
                Name_ArrayList.add(cursor.getString(cursor.getColumnIndex(customerDbHelper.KEY_Name)));
            } while (cursor.moveToNext());

        }

        cursor.close();
        database.close();
        int totalSold = 0;
        customerDataHelper cdh = new customerDataHelper(getApplicationContext());
        database = cdh.getWritableDatabase();
        for (String name : Name_ArrayList) {
            cursor = database.rawQuery("SELECT * FROM " + name.replaceAll(" ",""), null);
            if (cursor.moveToFirst()) {
                do {
                    totalSold = totalSold + Integer.parseInt(cursor.getString(cursor.getColumnIndex(customerDataHelper.KEY_No_of_cans)));

                } while (cursor.moveToNext());
            }

        }
        cursor.close();
        database.close();


        return answer - totalSold;

    }
}
