package net.robot_inc.watertracking;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class tab3 extends Fragment {
    SQLiteDatabase db;
    HashMap<String, Integer> map = new HashMap<>();
    stockHellper sh;
    customerDataHelper cdh;
    customerDbHelper ch;
    TextView soldcans, profit;
    Cursor cursor;
    protected String[] mMonths = new String[]{
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };
    public BarChart barchart;
    ArrayList<String> Stock_DATE_ArrayList = new ArrayList<String>();
    ArrayList<String> Stock_NO_OF_CANS_ArrayList = new ArrayList<String>();
    ArrayList<String> Stock_PRICE_ArrayList = new ArrayList<String>();
    ArrayList<String> Name_ArrayList = new ArrayList<>();
    ArrayList<String> Number_ArrayList = new ArrayList<String>();
    ArrayList<String> Address_ArrayList = new ArrayList<String>();
    ArrayList<String> Pending_ArrayList = new ArrayList<>();
    HashMap<String, HashMap<String, String>> records = new HashMap<>();
    HashMap<String, HashMap<String, Integer>> StockyearMap = new HashMap<>();
    HashMap<String, HashMap<String, Integer>> RecordyearMap = new HashMap<>();
    int count = 0;


    public tab3() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab3, container, false);
        soldcans = (TextView) view.findViewById(R.id.soldCans);
        profit = (TextView) view.findViewById(R.id.profit);
        sh = new stockHellper(getContext());
        cdh = new customerDataHelper(getContext());
        ch = new customerDbHelper(getContext());
        barchart = (BarChart) view.findViewById(R.id.barchart);
        barchart.getDescription().setEnabled(false);
        barchart.setBackgroundColor(Color.WHITE);
        barchart.setDrawGridBackground(false);
        barchart.setDrawBarShadow(false);
        barchart.setHighlightFullBarEnabled(false);
        barchart.setMinimumWidth(50);
        Legend l = barchart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);


        YAxis leftAxis = barchart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setLabelCount(5);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        XAxis xAxis = barchart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(0f);
        xAxis.setAxisMinimum(0f);
        xAxis.setLabelCount(12);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                return mMonths[(int) value];
            }
        });
        collectData();

        setData(12, 100);

        return view;
    }

    private void setData(int count, float range) {

        float start = 0f;

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = (int) start; i < start + count; i++) {
            float mult = (range + 1);
            float val = (float) (Math.random() * mult);

            yVals1.add(new BarEntry(i, val));
        }


        BarDataSet set1;

        if (barchart.getData() != null &&
                barchart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) barchart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            barchart.getData().notifyDataChanged();
            barchart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "The year 2017");

            //set1.setDrawIcons(true);

            set1.setColors(ColorTemplate.MATERIAL_COLORS);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);

            data.setBarWidth(0.9f);

            barchart.setData(data);
        }
    }

    protected float getRandom(float range, float startsfrom) {
        return (float) (Math.random() * range) + startsfrom;
    }

    public void collectData() {
        try {
            db = sh.getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM stock", null);

            Stock_DATE_ArrayList.clear();
            Stock_NO_OF_CANS_ArrayList.clear();
            Stock_PRICE_ArrayList.clear();


            if (cursor.moveToFirst()) {
                do {
                    Stock_DATE_ArrayList.add(cursor.getString(cursor.getColumnIndex(stockHellper.KEY_Date)));

                    Stock_NO_OF_CANS_ArrayList.add(cursor.getString(cursor.getColumnIndex(stockHellper.KEY_Number_Of_Cans)));

                    Stock_PRICE_ArrayList.add(cursor.getString(cursor.getColumnIndex(stockHellper.KEY_Price)));

                } while (cursor.moveToNext());
            }


            cursor.close();
            db.close();

        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        try {
            db = ch.getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM customers", null);

            Name_ArrayList.clear();
            Address_ArrayList.clear();
            Number_ArrayList.clear();


            if (cursor.moveToFirst()) {
                do {
                    Name_ArrayList.add(cursor.getString(cursor.getColumnIndex(customerDbHelper.KEY_Name)));

                    Address_ArrayList.add(cursor.getString(cursor.getColumnIndex(customerDbHelper.KEY_Address)));

                    Number_ArrayList.add(cursor.getString(cursor.getColumnIndex(customerDbHelper.KEY_Number)));

                } while (cursor.moveToNext());
            }


            cursor.close();
            db.close();

        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        try {
            HashMap<String, String> values = new HashMap<>();
            db = cdh.getWritableDatabase();
            for (String name : Name_ArrayList) {
                cursor = db.rawQuery("SELECT * FROM " + name, null);
                if (cursor.moveToFirst()) {
                    do {

                        values.put(customerDataHelper.KEY_No_of_cans, cursor.getString(cursor.getColumnIndex(customerDataHelper.KEY_No_of_cans)));
                        values.put(customerDataHelper.KEY_Date, cursor.getString(cursor.getColumnIndex(customerDataHelper.KEY_Date)));
                        values.put(customerDataHelper.KEY_Price, cursor.getString(cursor.getColumnIndex(customerDataHelper.KEY_Price)));
                        values.put(customerDataHelper.KEY_Paid, cursor.getString(cursor.getColumnIndex(customerDataHelper.KEY_Paid)));


                    } while (cursor.moveToNext());
                }
                records.put(name, values);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        count = 0;
        HashMap<String, HashSet<String>> Stockmonthsmap = new HashMap<>();
        HashSet<String> stcyearslist = new HashSet();
        HashSet<String> stcmonthlist = new HashSet();
        HashMap<String, Integer> values = new HashMap<>();

        for (String dates : Stock_DATE_ArrayList) {
            String year = dates.replaceAll("-", "").substring(0, 4);
            stcyearslist.add(year);
        }
        for (String year : stcyearslist) {
            stcmonthlist.clear();
            for (String date : Stock_DATE_ArrayList) {
                if (date.contains(year)) {
                    String month = date.replaceAll("-", "").substring(4, 6);
                    stcmonthlist.add(month);
                }

            }
            Stockmonthsmap.put(year, (HashSet<String>) stcmonthlist.clone());
        }
        int cans = 0;
        for (String key : Stockmonthsmap.keySet()) {
            for (String month : Stockmonthsmap.get(key)){
                cans = 0;
                for(String date : Stock_DATE_ArrayList){
                    String yearMonth = date.substring(0,7);
                    if(yearMonth.contains(month) && yearMonth.contains(key)){
                        cans = cans + Integer.parseInt(Stock_NO_OF_CANS_ArrayList.get(Stock_DATE_ArrayList.indexOf(date)));
                    }
                }
                values.put(month,cans);
            }
            StockyearMap.put(key, (HashMap<String, Integer>) values.clone());
        }





        soldcans.setText("No of Cans Sold = " + String.valueOf(StockyearMap.get("2018").get("03")));
    }


    public String getMonthName(int month) {

        switch (month) {
            case 1: {
                return "jan";
            }
            case 2: {
                return "feb";
            }
            case 3: {
                return "mar";
            }
            case 4: {
                return "apr";
            }
            case 5: {
                return "may";
            }
            case 6: {
                return "jun";
            }
            case 7: {
                return "jul";
            }
            case 8: {
                return "aug";
            }
            case 9: {
                return "sep";
            }
            case 10: {
                return "oct";
            }
            case 11: {
                return "nov";
            }
            case 12: {
                return "dec";
            }

        }
        return "";
    }
}
