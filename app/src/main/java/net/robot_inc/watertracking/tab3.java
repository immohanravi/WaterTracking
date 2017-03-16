package net.robot_inc.watertracking;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.IntegerRes;
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
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class tab3 extends Fragment {
    SQLiteDatabase db;
    HashMap<String, Integer> map = new HashMap<>();
    int cansold;
    int minYear = 0;
    int maxYear = 0;
    String minMonth = "01";
    stockHellper sh;
    customerDataHelper cdh;
    customerDbHelper ch;
    TextView soldcans, txtprofit;
    Cursor cursor;
    protected String[] mMonths = new String[]{
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };
    protected String[] dMonths = new String[]{
            "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    public BarChart barchart, barchart2;
    ArrayList<String> Stock_DATE_ArrayList = new ArrayList<String>();
    ArrayList<String> Stock_NO_OF_CANS_ArrayList = new ArrayList<String>();
    ArrayList<String> Stock_PRICE_ArrayList = new ArrayList<String>();
    ArrayList<String> Name_ArrayList = new ArrayList<>();
    ArrayList<String> Number_ArrayList = new ArrayList<String>();
    ArrayList<String> Address_ArrayList = new ArrayList<String>();
    ArrayList<String> Pending_ArrayList = new ArrayList<>();
    HashMap<String, ArrayList<ArrayList<String>>> records = new HashMap<>();
    HashMap<String, HashMap<String, ArrayList<String>>> StockyearMap = new HashMap<>();
    HashMap<String, HashMap<String, ArrayList<String>>> RecordyearMap = new HashMap<>();
    HashMap<String, HashMap<String, Integer>> profit = new HashMap<>();
    int count = 0;
    int chartMaxValue = 0, profitMaxValue = 0;
    String thisYear = "";
    String thisMonth = "";


    public tab3() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab3, container, false);
        Date date = new Date();

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        thisYear = String.valueOf(c.get(Calendar.YEAR));
        thisMonth = String.valueOf(c.get(Calendar.MONTH) + 1);
        thisMonth = (thisMonth.length() > 1) ? thisMonth : "0" + thisMonth;
        Log.i("date", String.valueOf(thisYear) + " - " + String.valueOf(thisMonth));
        minYear = Integer.parseInt(thisYear);
        soldcans = (TextView) view.findViewById(R.id.soldCans);
        txtprofit = (TextView) view.findViewById(R.id.profit);
        sh = new stockHellper(getContext());
        cdh = new customerDataHelper(getContext());
        ch = new customerDbHelper(getContext());
        barchart = (BarChart) view.findViewById(R.id.barchart);
        barchart.getDescription().setEnabled(false);
        barchart.setBackgroundColor(Color.WHITE);
        barchart.setDrawGridBackground(false);
        barchart.setDrawBarShadow(true);
        barchart.setHighlightFullBarEnabled(true);
        barchart.setMinimumWidth(20);

        Legend l = barchart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);


        YAxis leftAxis = barchart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setLabelCount(10);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        XAxis xAxis = barchart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        // xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(0f);
        //xAxis.setAxisMinimum(0f);
        xAxis.setLabelCount(12);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                return mMonths[(int) value];
            }
        });
        barchart2 = (BarChart) view.findViewById(R.id.barchart2);
        barchart2.getDescription().setEnabled(false);
        barchart2.setBackgroundColor(Color.WHITE);
        barchart2.setDrawGridBackground(false);
        barchart2.setDrawBarShadow(true);
        barchart2.setHighlightFullBarEnabled(true);
        barchart2.setMinimumWidth(20);

        Legend l2 = barchart2.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);


        YAxis leftAxis2 = barchart2.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setLabelCount(10);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        XAxis xAxis2 = barchart2.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        // xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(0f);
        //xAxis.setAxisMinimum(0f);
        xAxis.setLabelCount(12);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                return mMonths[(int) value];
            }
        });
        collectData();


        setData(12, chartMaxValue, profitMaxValue);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        collectData();
        setData(12, chartMaxValue, profitMaxValue);
    }

    private void setData(int count, float range, float rang2) {

        float start = 0f;

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = (int) start; i < start + count; i++) {

            yVals1.add(new BarEntry(i, 0));
        }


        if (RecordyearMap.isEmpty() == false) {
            if (RecordyearMap.containsKey(thisYear)) {
                for (String month : RecordyearMap.get(thisYear).keySet()) {

                    yVals1.add(new BarEntry(Integer.parseInt(month) - 1, Float.parseFloat(RecordyearMap.get(thisYear).get(month).get(0))));


                }
            }


        }

        BarDataSet set1;

        if (barchart.getData() != null &&
                barchart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) barchart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            barchart.getData().notifyDataChanged();
            barchart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "The year " + thisYear);

            //set1.setDrawIcons(true);

            set1.setColors(ColorTemplate.MATERIAL_COLORS);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);

            data.setBarWidth(0.9f);

            barchart.setData(data);
        }

        ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();

        for (int i = (int) start; i < start + count; i++) {

            yVals2.add(new BarEntry(i, 0));
        }


        if (profit.isEmpty() == false) {
            if (profit.containsKey(thisYear)) {
                for (String month : profit.get(thisYear).keySet()) {

                    yVals2.add(new BarEntry(Integer.parseInt(month) - 1, Float.parseFloat(String.valueOf(profit.get(thisYear).get(month)))));


                }
            }


        }

        BarDataSet set2;

        if (barchart2.getData() != null &&
                barchart2.getData().getDataSetCount() > 0) {
            set2 = (BarDataSet) barchart2.getData().getDataSetByIndex(0);
            set2.setValues(yVals2);
            barchart.getData().notifyDataChanged();
            barchart.notifyDataSetChanged();
        } else {
            set2 = new BarDataSet(yVals2, "The year " + thisYear);

            //set1.setDrawIcons(true);

            set2.setColors(ColorTemplate.COLORFUL_COLORS);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set2);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);

            data.setBarWidth(0.9f);

            barchart2.setData(data);
        }
    }


    protected float getRandom(float range, float startsfrom) {
        return (float) (Math.random() * range) + startsfrom;
    }

    public void collectData() {

        records = new HashMap<>();

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
            ArrayList<ArrayList<String>> arrays = new ArrayList<>();
            ArrayList<String> values = new ArrayList<>();
            db = cdh.getWritableDatabase();
            for (String name : Name_ArrayList) {
                arrays.clear();
                cursor = db.rawQuery("SELECT * FROM " + name, null);
                if (cursor.moveToFirst()) {
                    do {
                        values.clear();
                        values.add(cursor.getString(cursor.getColumnIndex(customerDataHelper.KEY_Date)));
                        values.add(cursor.getString(cursor.getColumnIndex(customerDataHelper.KEY_No_of_cans)));
                        values.add(cursor.getString(cursor.getColumnIndex(customerDataHelper.KEY_Price)));
                        values.add(cursor.getString(cursor.getColumnIndex(customerDataHelper.KEY_Paid)));
                        arrays.add((ArrayList<String>) values.clone());

                    } while (cursor.moveToNext());
                }
                records.put(name, (ArrayList<ArrayList<String>>) arrays.clone());
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
        HashMap<String, ArrayList<String>> values = new HashMap<>();
        ArrayList<String> averagePrice = new ArrayList<>();

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
        int amount = 0;

        for (String key : Stockmonthsmap.keySet()) {
            values.clear();
            for (String month : Stockmonthsmap.get(key)) {
                cans = 0;
                averagePrice.clear();
                amount = 0;
                for (int i = 0; i < Stock_DATE_ArrayList.size(); i++) {
                    String yearMonth = Stock_DATE_ArrayList.get(i).substring(0, 7);
                    if (yearMonth.substring(5).equals(month) && yearMonth.substring(0, 4).equals(key)) {
                        amount = amount + Integer.parseInt(Stock_PRICE_ArrayList.get(i)) * Integer.parseInt(Stock_NO_OF_CANS_ArrayList.get(i));
                        cans = cans + Integer.parseInt(Stock_NO_OF_CANS_ArrayList.get(i));
                        Log.i("geting Index", String.valueOf(i));
                    }

                }
                averagePrice.add(String.valueOf(cans));
                averagePrice.add(String.valueOf(amount / cans));

                values.put(month, (ArrayList<String>) averagePrice.clone());

            }
            minYear = Math.min(minYear, Integer.parseInt(key));
            maxYear = Math.max(maxYear, Integer.parseInt(key));
            StockyearMap.put(key, (HashMap<String, ArrayList<String>>) values.clone());
        }


       /* if (StockyearMap.size() > 0) {
            soldcans.setText("No of Cans Sold = " + String.valueOf(StockyearMap.get("2018").get("03")));

        }*/
        HashMap<String, HashSet<String>> recordmonthsmap = new HashMap<>();
        HashSet<String> recyearslist = new HashSet();
        HashSet<String> recmonthlist = new HashSet();
        HashMap<String, ArrayList<String>> recvalues = new HashMap<>();
        ArrayList<String> averageprice = new ArrayList<>();

        for (String name : records.keySet()) {
            ArrayList recordsList = records.get(name);
            for (int a = 0; a < recordsList.size(); a++) {
                ArrayList customerrecords = (ArrayList) recordsList.get(a);
                String year = (String) customerrecords.get(0);
                year = year.replaceAll("-", "").substring(0, 4);
                recyearslist.add(year);
            }
        }

        for (String year : recyearslist) {
            recmonthlist.clear();
            for (String name : records.keySet()) {
                ArrayList recordsList = records.get(name);
                for (int a = 0; a < recordsList.size(); a++) {
                    ArrayList customerrecords = (ArrayList) recordsList.get(a);
                    String month = (String) customerrecords.get(0);
                    if (month.contains(year)) {
                        month = month.replaceAll("-", "").substring(4, 6);
                        recmonthlist.add(month);
                    }

                }
            }
            recordmonthsmap.put(year, (HashSet<String>) recmonthlist.clone());
        }
        int noSoldCans = 0;
        int recamount = 0;
        for (String year : recordmonthsmap.keySet()) {
            recvalues.clear();
            for (String month : recordmonthsmap.get(year)) {
                noSoldCans = 0;
                averageprice.clear();
                recamount = 0;
                for (String name : records.keySet()) {
                    ArrayList recordsList = records.get(name);
                    for (int a = 0; a < recordsList.size(); a++) {
                        ArrayList customerrecords = (ArrayList) recordsList.get(a);
                        String date = ((String) customerrecords.get(0)).replaceAll("-", "");
                        String checkYear = date.substring(0, 4);
                        String checkMonth = date.substring(4, 6);

                        if (checkYear.equals(year) && checkMonth.equals(month)) {
                            recamount = recamount + Integer.parseInt((String) customerrecords.get(1)) * Integer.parseInt((String) customerrecords.get(2));
                            noSoldCans = noSoldCans + Integer.parseInt((String) customerrecords.get(1));
                        }

                    }
                }
                if (noSoldCans != 0 && recamount != 0) {
                    averageprice.add(String.valueOf(noSoldCans));
                    averageprice.add(String.valueOf(recamount / noSoldCans));
                    recvalues.put(month, (ArrayList<String>) averageprice.clone());
                    Log.i("sold", String.valueOf(month) + " = " + String.valueOf(noSoldCans));
                    chartMaxValue = Math.max(chartMaxValue, noSoldCans);

                    Log.i("Max Value", String.valueOf(chartMaxValue));
                }

            }
            RecordyearMap.put(year, (HashMap<String, ArrayList<String>>) recvalues.clone());

        }

        /*
        if (RecordyearMap.containsKey(thisYear)&&StockyearMap.containsKey(thisYear)) {
            if (RecordyearMap.get(thisYear).containsKey(thisMonth)&&StockyearMap.get(thisYear).containsKey(thisMonth)) {
                soldcans.setText("No of cans Sold = " + RecordyearMap.get(thisYear).get(thisMonth).get(0));
                int averageboughtPrice = Integer.parseInt(StockyearMap.get(thisYear).get(thisMonth).get(1));
                float answer = ((Integer.parseInt(RecordyearMap.get(thisYear).get(thisMonth).get(0)))
                        *(Integer.parseInt(RecordyearMap.get(thisYear).get(thisMonth).get(1))-averageboughtPrice));                       ;
                profit.setText("Current Month Profit = "+answer);
            }
        }*/
        HashMap<String, Integer> yearWiseSold = new HashMap<>();
        HashMap<String, Integer> yearWiseBought = new HashMap<>();

        for (String year : StockyearMap.keySet()) {
            int totalBought = 0;
            for (String month : StockyearMap.get(year).keySet()) {
                totalBought = totalBought + Integer.parseInt(StockyearMap.get(year).get(month).get(0));


            }

            yearWiseBought.put(year, totalBought);
        }
        for (String year : RecordyearMap.keySet()) {
            maxYear = Math.max(maxYear, Integer.parseInt(year));
            int totalSold = 0;
            for (String month : RecordyearMap.get(year).keySet()) {
                totalSold = totalSold + Integer.parseInt(RecordyearMap.get(year).get(month).get(0));
            }
            yearWiseSold.put(year, totalSold);
        }
/*

        HashMap<String, Float> profitMonth = new HashMap<>();
        float montlyProfit = 0;
        int pendingCans = 0;
        int avePrice = 0;
        for (int year = minYear; year <= maxYear; year++) {
            profitMonth.clear();
            if (StockyearMap.containsKey(String.valueOf(year))) {
                for (String month : dMonths) {

                    if (RecordyearMap.containsKey(String.valueOf(year))) {
                        if (StockyearMap.get(String.valueOf(year)).containsKey(month) && RecordyearMap.get(String.valueOf(year)).containsKey(month)) {
                            if (pendingCans == 0) {
                                pendingCans = Integer.valueOf(StockyearMap.get(String.valueOf(year)).get(month).get(0)) -
                                        Integer.valueOf(RecordyearMap.get(String.valueOf(year)).get(month).get(0));
                                int averageboughtPrice = Integer.parseInt(StockyearMap.get(String.valueOf(year)).get(month).get(1));
                                montlyProfit = ((Integer.parseInt(RecordyearMap.get(String.valueOf(year)).get(month).get(0)))
                                        * (Integer.parseInt(RecordyearMap.get(String.valueOf(year)).get(month).get(1)) - averageboughtPrice));

                            } else if (pendingCans > 0) {
                                if (Integer.parseInt(month) == 1) {
                                    if (year != minYear) {

                                        int presentMonth = Integer.parseInt(StockyearMap.get(String.valueOf(year)).get(month).get(0)) * Integer.parseInt(StockyearMap.get(String.valueOf(year)).get(month).get(1));

                                        int averageboughtPrice = (avePrice + presentMonth) / (pendingCans + Integer.parseInt(StockyearMap.get(String.valueOf(year)).get(month).get(0)));
                                        pendingCans = pendingCans + (Integer.valueOf(StockyearMap.get(String.valueOf(year)).get(month).get(0)) -
                                                Integer.valueOf(RecordyearMap.get(String.valueOf(year)).get(month).get(0)));

                                        montlyProfit = ((Integer.parseInt(RecordyearMap.get(String.valueOf(year)).get(month).get(0)))
                                                * (Integer.parseInt(RecordyearMap.get(String.valueOf(year)).get(month).get(1)) - averageboughtPrice));

                                    }
                                } else {

                                    int presentMonth = Integer.parseInt(StockyearMap.get(String.valueOf(year)).get(month).get(0)) * Integer.parseInt(StockyearMap.get(String.valueOf(year)).get(month).get(1));

                                    int averageboughtPrice = (avePrice + presentMonth) / (pendingCans + Integer.parseInt(StockyearMap.get(String.valueOf(year)).get(month).get(0)));

                                    pendingCans = pendingCans + (Integer.valueOf(StockyearMap.get(String.valueOf(year)).get(month).get(0)) -
                                            Integer.valueOf(RecordyearMap.get(String.valueOf(year)).get(month).get(0)));
                                    montlyProfit = ((Integer.parseInt(RecordyearMap.get(String.valueOf(year)).get(month).get(0)))
                                            * (Integer.parseInt(RecordyearMap.get(String.valueOf(year)).get(month).get(1)) - averageboughtPrice));

                                }


                            }
                            profitMonth.put(month, montlyProfit);
                        }
                    } else {
                        if (pendingCans < 1) {
                            for (String m : StockyearMap.get(String.valueOf(year)).keySet()) {

                                pendingCans = pendingCans + Integer.parseInt(pendingCans + StockyearMap.get(String.valueOf(year)).get(m).get(0));
                                avePrice = avePrice + pendingCans * Integer.parseInt(pendingCans + StockyearMap.get(String.valueOf(year)).get(m).get(0));

                            }
                        } else {
                            avePrice = avePrice + pendingCans * Integer.parseInt(pendingCans + StockyearMap.get(String.valueOf(year - 1)).get("12").get(0));
                            for (String m : StockyearMap.get(String.valueOf(year)).keySet()) {

                                pendingCans = pendingCans + Integer.parseInt(pendingCans + StockyearMap.get(String.valueOf(year)).get(m).get(0));
                                avePrice = avePrice + pendingCans * Integer.parseInt(pendingCans + StockyearMap.get(String.valueOf(year)).get(m).get(0));

                            }
                        }

                        avePrice = avePrice / pendingCans;
                    }
                }
                profit.put(String.valueOf(year), (HashMap<String, Float>) profitMonth.clone());
            }

        }
        if (RecordyearMap.containsKey(thisYear) && RecordyearMap.get(thisYear).containsKey(thisMonth)) {

            soldcans.setText("No of cans Sold = " + RecordyearMap.get(thisYear).get(thisMonth).get(0));
            txtprofit.setText("Profit this Month = " + profit.get(thisYear).get(thisMonth));
        }
*/

        HashMap<String, HashMap<String, ArrayList<String>>> averageMap = new HashMap<>();
        ArrayList<String> dummy = new ArrayList<>();
        HashMap<String, ArrayList<String>> dummyHash = new HashMap<>();
        int pending = 0;
        int presentStock = 0;
        int presentAverage = 0;

        int count = 0;

     /*   for (String m : dMonths) {
            String year = String.valueOf(minYear);

            if (StockyearMap.containsKey(year)) {
                if (StockyearMap.get(year).containsKey(m)) {
                    presentStock = Integer.parseInt(StockyearMap.get(year).get(m).get(0));
                    presentAverage = Integer.parseInt(StockyearMap.get(year).get(m).get(1));

                    dummy.add(String.valueOf(presentStock));
                    dummy.add(String.valueOf(presentAverage));
                    dummyHash.put(m, (ArrayList<String>) dummy.clone());
                    if (RecordyearMap.containsKey(year)) {
                        pending = Integer.parseInt(StockyearMap.get(year).get(m).get(0)) - Integer.parseInt(RecordyearMap.get(year).get(m).get(0));
                        presentAverage = Integer.parseInt(StockyearMap.get(year).get(m).get(1));

                    } else {
                        pending = Integer.parseInt(StockyearMap.get(year).get(m).get(0));
                        presentAverage = Integer.parseInt(StockyearMap.get(year).get(m).get(1));

                    }
                    break;
                }

            }
        }*/

        for (int a = minYear; a <= maxYear; a++) {
            String year = String.valueOf(a);
            dummyHash.clear();
            if (StockyearMap.containsKey(year)) {
                if (RecordyearMap.containsKey(year)) {
                    for (String month : dMonths) {
                        dummy.clear();
                        if (StockyearMap.get(year).containsKey(month)) {
                            if (RecordyearMap.get(year).containsKey(month)) {
                                if (pending > 0) {
                                    int mul1 = pending * presentAverage;
                                    int mul2 = Integer.parseInt(StockyearMap.get(year).get(month).get(0)) * Integer.parseInt(StockyearMap.get(year).get(month).get(1));
                                    int answer = (mul1 + mul2) / (pending + Integer.parseInt(StockyearMap.get(year).get(month).get(0)));
                                    presentStock = pending + Integer.parseInt(StockyearMap.get(year).get(month).get(0));
                                    presentAverage = answer;
                                    pending = presentStock - Integer.parseInt(RecordyearMap.get(year).get(month).get(0));
                                } else {
                                    presentStock = pending + Integer.parseInt(StockyearMap.get(year).get(month).get(0));
                                    presentAverage = Integer.parseInt(StockyearMap.get(year).get(month).get(1));
                                    pending = presentStock - Integer.parseInt(RecordyearMap.get(year).get(month).get(0));
                                }
                                dummy.add(String.valueOf(presentStock));
                                dummy.add(String.valueOf(presentAverage));
                                dummyHash.put(month, (ArrayList<String>) dummy.clone());
                            } else {
                                if (pending > 0) {
                                    int mul1 = pending * presentAverage;
                                    int mul2 = Integer.parseInt(StockyearMap.get(year).get(month).get(0)) * Integer.parseInt(StockyearMap.get(year).get(month).get(1));
                                    int answer = (mul1 + mul2) / (pending + Integer.parseInt(StockyearMap.get(year).get(month).get(0)));
                                    presentStock = pending + Integer.parseInt(StockyearMap.get(year).get(month).get(0));
                                    presentAverage = answer;
                                    pending = presentStock;
                                } else {
                                    presentStock = presentStock + Integer.parseInt(StockyearMap.get(year).get(month).get(0));
                                    presentAverage = Integer.parseInt(StockyearMap.get(year).get(month).get(1));
                                    pending = presentStock;
                                }
                            }

                        } else {
                            if (RecordyearMap.get(year).containsKey(month)) {

                                presentStock = pending;
                                pending = pending - Integer.parseInt(RecordyearMap.get(year).get(month).get(0));;

                                dummy.add(String.valueOf(presentStock));
                                dummy.add(String.valueOf(presentAverage));
                                dummyHash.put(month, (ArrayList<String>) dummy.clone());
                            }
                        }

                    }

                    averageMap.put(year, (HashMap<String, ArrayList<String>>) dummyHash.clone());
                } else {
                    dummyHash.clear();
                    int totalBought = 0;
                    int avgPrice = 0;
                    for (String month : StockyearMap.get(year).keySet()) {
                        totalBought = totalBought + Integer.parseInt(StockyearMap.get(year).get(month).get(0));
                        avgPrice = avgPrice + Integer.parseInt(StockyearMap.get(year).get(month).get(0)) * Integer.parseInt(StockyearMap.get(year).get(month).get(1));

                    }
                    if (pending > 0 && Integer.parseInt(year) != minYear) {
                        int mul1 = pending * presentAverage;
                        int mul2 = totalBought * (avgPrice / totalBought);
                        int answer = (mul1 + mul2) / (pending + totalBought);
                        presentStock = pending + totalBought;
                        presentAverage = answer;
                        pending = presentStock;
                    } else {
                        presentStock = totalBought;
                        presentAverage = avgPrice / totalBought;
                        pending = presentStock;
                    }


                }


            } else if (RecordyearMap.containsKey(year)) {
                for (String month : dMonths) {
                    dummy.clear();
                    if (RecordyearMap.get(year).containsKey(month)) {

                        pending = presentStock - Integer.parseInt(RecordyearMap.get(year).get(month).get(0));
                        dummy.add(String.valueOf(presentStock));
                        dummy.add(String.valueOf(presentAverage));
                        dummyHash.put(month, (ArrayList<String>) dummy.clone());
                    }

                }
                averageMap.put(year, (HashMap<String, ArrayList<String>>) dummyHash.clone());

            }

        }

        HashMap<String, Integer> dummyMap = new HashMap<>();
        for (String year : averageMap.keySet()) {
            dummyMap.clear();
            Log.i("Year = ", year);
            for (String month : averageMap.get(year).keySet()) {
                int priceDifference = Integer.parseInt(RecordyearMap.get(year).get(month).get(1)) - Integer.parseInt(averageMap.get(year).get(month).get(1));
                int actualProfit = priceDifference * Integer.parseInt(RecordyearMap.get(year).get(month).get(0));
                profitMaxValue = Math.max(profitMaxValue, actualProfit);
                dummyMap.put(month, actualProfit);
            }

            profit.put(year, (HashMap<String, Integer>) dummyMap.clone());
        }
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
