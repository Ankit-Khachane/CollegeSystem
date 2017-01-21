package main.collegesystem.admin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import main.collegesystem.R;

public class Admin_staff_detail extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

    ArrayList<String> studary = new ArrayList<String>();
    ArrayList<String> attendanceary = new ArrayList<String>(studary);
    ArrayList<String> subjectary = new ArrayList<String>();
    ArrayAdapter<String> studnm, subjectparseary;
    String tempsub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_attendance_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ListView studchklist = (ListView) findViewById(R.id.StudchkList);
        Spinner subspin = (Spinner) findViewById(R.id.subjectspinner);
        studchklist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        studnm = new ArrayAdapter<String>(this, R.layout.rowstud, R.id.txt_ln, studary);
        subjectparseary = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, subjectary);
        subjectparseary.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        studnm.setNotifyOnChange(true);
        studchklist.setAdapter(studnm);
        subspin.setAdapter(subjectparseary);
        //spinner subject data fetch
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Subjects");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                //attendance subject sheet
                if (e == null) {
                    Log.e("ParseObject User:", "-Fetched");
                    for (int i = 0; i < list.size(); i++) {
                        ParseObject u = list.get(i);
                        String nm = u.get("Subject").toString();
                        subjectparseary.add(nm);
                    }
                } else {
                    Log.e("ParseObject User:", e + "-Occure");
                }
            }
        });
        ParseQuery<ParseObject> studob = ParseQuery.getQuery("Attendance");
        studob.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                //fetching user from server
                if (e == null) {
                    Log.e("ParseUser:", "-Fetched");
                    for (int i = 0; i < list.size(); i++) {
                        ParseObject u = list.get(i);
                        String nm = u.get("student").toString();
                        studnm.add(nm);
                        studnm.notifyDataSetChanged();
                    }
                } else {
                    Log.e("ParseUser:", e + "-Occure");
                }
            }
        });
        studchklist.setOnItemClickListener(this);
        subspin.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView tv = (TextView) view.findViewById(R.id.txt_ln);
        String temp = parent.getItemAtPosition(position).toString();
        if (tv.isSelected()) {
            markattendance(temp);
        } else {

        }
        if (attendanceary.contains(temp)) {
            //for uncheck operation remove added value from array
            attendanceary.remove(temp);
            Toast.makeText(Admin_staff_detail.this, "Removed to Array", Toast.LENGTH_SHORT).show();
        } else {
            //for checked value adding it to array
            attendanceary.add(temp);
            markattendance(temp);
            Toast.makeText(Admin_staff_detail.this, "Added to Array", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //select sub that wil got to proper attendance sheet
        tempsub = parent.getSelectedItem().toString();
        Toast.makeText(Admin_staff_detail.this, "Subject Selected :" + tempsub, Toast.LENGTH_SHORT).show();
    }

    public void markattendance(String user) {
        ParseQuery<ParseObject> markattendance = ParseQuery.getQuery("Attendance");
        markattendance.whereEqualTo("student", user);
        markattendance.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
//                int i= (int) parseObject.get(tempsub);
                parseObject.increment(tempsub);
                parseObject.saveInBackground();
                Log.i("Attendance :", "+---+Counted");
                Toast.makeText(Admin_staff_detail.this, "Test :" + parseObject.get(tempsub), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void submitattendance(View view) {
        //selected sub will be redirected to that attendance sheet
        ParseObject sub = new ParseObject(tempsub);

        String man = "";
        for (String tm : attendanceary) { //TODO getting values one by one and save it as a String
            man = " " + man + tm + " ";
        }
        Toast.makeText(this, man, Toast.LENGTH_SHORT).show();
    }
}
