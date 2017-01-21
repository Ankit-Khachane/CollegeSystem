package main.collegesystem.staff;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import main.collegesystem.About;
import main.collegesystem.Login;
import main.collegesystem.Profile;
import main.collegesystem.R;

public class Staff_Attendance extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
    public static String nm, mail, utype, phon, brnch, addr;
    ArrayList<String> studary = new ArrayList<String>();
    ArrayList<String> attendanceary = new ArrayList<String>(studary);
    ArrayList<String> subjectary = new ArrayList<String>();
    ArrayAdapter<String> studnm, subjectparseary;
    EditText datee;
    String tempsub, datepluse;
    int date, year, month;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_attendance_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        datee = (EditText) findViewById(R.id.Date);
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
        Calendar c = Calendar.getInstance();
        date = c.get(Calendar.DAY_OF_MONTH);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);
        datepluse = String.valueOf(date) + "-" + String.valueOf(month + 1) + "-" + String.valueOf(year);
    }

    public void unsetChannel() {
        List<String> las = ParseInstallation.getCurrentInstallation().getList("channels");
        if (las == null) {
            Log.i("Channels :", "Not Cleared !");
        } else {
            ParseInstallation.getCurrentInstallation().removeAll("channels", las);
            ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    Log.i("Channels :", "Cleared !");
                }
            });
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.staff_menu, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.staffprof:
                SharedPreferences pref = this.getSharedPreferences("Login_state", MODE_PRIVATE);
                String sessionToken = pref.getString("sessionToken", "");
                try {
                    ParseUser.become(sessionToken);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                ParseUser user = ParseUser.getCurrentUser();
                if (user != null) {
                    nm = user.getUsername();
                    mail = user.getEmail();
                    utype = user.get("Type").toString();
                    phon = user.get("PhoneNo").toString();
                    brnch = user.get("Branch").toString();
                    addr = user.get("Address").toString();
                    Intent i = new Intent(getApplicationContext(), Profile.class);
                    Bundle detail = new Bundle();
                    detail.putString("uname", nm);
                    detail.putString("mail", mail);
                    detail.putString("utype", utype);
                    detail.putString("phone", phon);
                    detail.putString("branch", brnch);
                    detail.putString("address", addr);

                    i.putExtras(detail);
                    startActivity(i);
//                    Toast.makeText(Admin.this, "Email :"+mail, Toast.LENGTH_SHORT).show();
                    Log.i("Current User :--", "user :" + nm);
                } else {
                    Log.i("Current User :--", "user null");
                    Toast.makeText(getApplicationContext(), "Profile isn't initialized", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.Logout:
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser != null) {
                    // do stuff with the user
                    ParseUser.logOutInBackground(new LogOutCallback() {
                        @Override
                        public void done(ParseException e) {
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("Login_state", MODE_PRIVATE);
                            SharedPreferences.Editor edi = pref.edit();
                            edi.putString("type", null);
                            edi.putBoolean("firstlogin", true);
                            edi.apply();
                            unsetChannel();
                            Intent i = new Intent(getApplicationContext(), Login.class);
                            startActivity(i);
                            finish();
                            Toast.makeText(getApplicationContext(), "Logout Selected", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                return true;
            case R.id.About:
                Intent t = new Intent(this, About.class);
                startActivity(t);
                Toast.makeText(getApplicationContext(), "About Selected", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
            Toast.makeText(Staff_Attendance.this, "Removed", Toast.LENGTH_SHORT).show();
        } else {
            //for checked value adding it to array
            attendanceary.add(temp);
            markattendance(temp);
            Toast.makeText(Staff_Attendance.this, "Added", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //select sub that wil got to proper attendance sheet
        tempsub = parent.getSelectedItem().toString();
        Toast.makeText(Staff_Attendance.this, "Subject Selected :" + tempsub, Toast.LENGTH_SHORT).show();
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
            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void submitattendance(View view) {
        //selected sub will be redirected to that attendance sheet

        String man = "";
        for (String tm : attendanceary) { //TODO getting values one by one and save it as a String
            man = " " + man + tm + " ";
        }
        Toast.makeText(this, man, Toast.LENGTH_SHORT).show();
    }

    public void datepick(View vc) {
        datee.setText(datepluse);
    }
}
