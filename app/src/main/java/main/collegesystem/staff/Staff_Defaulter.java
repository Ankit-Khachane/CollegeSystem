package main.collegesystem.staff;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import main.collegesystem.R;

public class Staff_Defaulter extends AppCompatActivity {
    int total;
    ArrayList<String> prelist=new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_defaulter_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ParseQuery<ParseObject> users=ParseQuery.getQuery("Attendance");
        users.orderByAscending("RollNo");
        users.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    Log.e("ParseObject User:", "-Fetched");
                    for (int i = 0; i < list.size(); i++) {
                        ParseObject u = list.get(i);
                        String nm = u.get("Subject").toString();
                        prelist.add(nm);
                    }
                } else {
                    Log.e("ParseObject User:", e + "-Occure");
                }
            }
        });

    }

}
