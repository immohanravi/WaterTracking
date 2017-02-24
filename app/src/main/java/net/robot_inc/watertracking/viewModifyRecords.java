package net.robot_inc.watertracking;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

public class viewModifyRecords extends AppCompatActivity {


    private ListView listView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_modify_records);
        listView=(ListView) findViewById(R.id.tablelayout);



    }
}
