package net.robot_inc.watertracking;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    SQLiteDatabase SQLITEDATABASE;
    SQLiteDatabase CustomerDB;

    static String DATABASE_NAME="WaterTracking";


    public static final String KEY_ID="id";

    public static final String TABLE_NAME="customers";

    public static final String KEY_Name="Name";

    public static final String KEY_Address="Address";

    public static final String KEY_Number="Number";
    public static final String KEY_Image="Image";
    int stockAvailbale = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,addStock.class);
                startActivity(intent);
            }
        });
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0: {

                        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                        fab.setVisibility(View.VISIBLE);
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getApplicationContext(),addStock.class);
                                startActivityForResult(intent, 0);
                            }
                        });
                        break;
                    }
                    case 1: {

                        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                        fab.setVisibility(View.VISIBLE);
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getApplicationContext(),newCustomer.class);
                                startActivityForResult(intent, 0);
                            }
                        });
                        break;
                    }
                    case 2: {
                        /*
                        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Snackbar.make(view, "Tab3", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        });*/
                        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                        fab.setVisibility(View.GONE);
                        break;
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        DBCreate();
}



    private void DBCreate() {
        SQLITEDATABASE = openOrCreateDatabase("WaterTracking", Context.MODE_PRIVATE,null);
        //this.deleteDatabase("customers");
        CustomerDB = openOrCreateDatabase("customers",Context.MODE_PRIVATE,null);
        CustomerDB.close();
        //SQLITEDATABASE.execSQL("DROP TABLE IF EXISTS stock");
        SQLITEDATABASE.execSQL("CREATE TABLE IF NOT EXISTS stock(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, date DATETIME NOT NULL, number_of_cans INTEGER NOT NULL, price INTEGER NOT NULL)");
        //SQLITEDATABASE.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        SQLITEDATABASE.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_NAME+"("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+KEY_Name+" TEXT NOT NULL UNIQUE, "+KEY_Address+" TEXT NOT NULL, "+KEY_Number+" INTEGER NOT NULL UNIQUE, "+KEY_Image+" BLOB NOT NULL)");
        SQLITEDATABASE.close();
        //SQLITEDATABASE.execSQL("INSERT INTO stock (number_of_cans, price) values(200,10)");
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new tab1(), "Main");
        adapter.addFragment(new tab2(), "Customers");
        adapter.addFragment(new tab3(), "Profit");
        viewPager.setAdapter(adapter);

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem Item) {
        switch (Item.getItemId()) {
            case R.id.exit:
                finish();
                System.exit(0);
                return true;
            default:
                return super.onOptionsItemSelected(Item);
        }
    }


}
