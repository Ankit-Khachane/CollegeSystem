package main.collegesystem.student;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.List;

import main.collegesystem.About;
import main.collegesystem.Login;
import main.collegesystem.Profile;
import main.collegesystem.R;

public class Stud_Schedule extends AppCompatActivity implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
    public static String nm, mail, utype, phon, brnch, addr;
    Spinner daySpinner;
    ListView schedullstvw;
    ArrayAdapter<String> dayspiner, schedlist;
    ArrayList<String> dayspin = new ArrayList<String>();
    ArrayList<String> schedlst = new ArrayList<String>();
    String selectedDay, selectdsched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_schedule_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        daySpinner = (Spinner) findViewById(R.id.dayspiner);
        schedullstvw = (ListView) findViewById(R.id.schedulelist);
        dayspiner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dayspin);
        dayspiner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(dayspiner);
        ParseQuery<ParseObject> pday = ParseQuery.getQuery("Subjects");
        pday.orderByAscending("SEQUENCE");
        pday.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < list.size(); i++) {
                        ParseObject u = list.get(i);
                        String nm = u.get("Day").toString();
                        dayspiner.add(nm);
                    }
                }
            }
        });
        daySpinner.setOnItemSelectedListener(this);

        schedullstvw = (ListView) findViewById(R.id.schedulelist);
        schedlist = new ArrayAdapter<String>(this, R.layout.staffschedrow, R.id.shcedrowonex, schedlst);
        schedlist.setNotifyOnChange(true);
        schedullstvw.setAdapter(schedlist);
        schedullstvw.setOnItemClickListener(this);
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
        getMenuInflater().inflate(R.menu.student_menu, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.studprof:
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedDay = parent.getItemAtPosition(position).toString();
        schedlist.clear();
        if (schedlist.isEmpty()) {
            ParseQuery<ParseObject> psched = ParseQuery.getQuery("schedule");
            psched.orderByAscending("SEQUENCE");
            psched.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {
                        for (int i = 0; i < list.size(); i++) {
                            ParseObject u = list.get(i);
                            String nm = u.get(selectedDay).toString();
                            schedlist.add(nm);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectdsched = parent.getItemAtPosition(position).toString();
        showChangeLangDialog(selectdsched);
        Toast.makeText(getApplicationContext(), "Selcted :" + selectdsched, Toast.LENGTH_SHORT).show();
    }

    public void showChangeLangDialog(final String schedsilected) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.staffschedclick, null);
        final TextView timev = (TextView) dialogView.findViewById(R.id.timevalx);
        final TextView schedtv = (TextView) dialogView.findViewById(R.id.scheditemx);
        ParseQuery<ParseObject> p = ParseQuery.getQuery("schedule");
        p.orderByAscending("SEQUENCE");
        p.whereEqualTo(selectedDay, schedsilected);
        p.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                timev.setText(parseObject.getString("Time"));
                schedtv.setText(selectdsched);
            }
        });

        dialogBuilder.setView(dialogView);

        dialogBuilder.setTitle("Schedule");

        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();

            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

}
