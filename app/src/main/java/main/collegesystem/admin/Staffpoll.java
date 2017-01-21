package main.collegesystem.admin;

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
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import main.collegesystem.About;
import main.collegesystem.Login;
import main.collegesystem.Profile;
import main.collegesystem.R;

public class Staffpoll extends AppCompatActivity implements AdapterView.OnItemClickListener {
    public static String nm, mail, utype;
    ListView stflist;
    List<String> stflistv;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_staffpoll_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        stflistv = new ArrayList<>();
        stflist = (ListView) findViewById(R.id.staff_poll_list);
//        stflistv=new String[]{"Staff 1","Staff 2","Staff 3","Staff 4","Staff 5","Staff 6","Staff 7","Staff 8","Staff 9","Staff 10",};
        adapter = new ArrayAdapter<String>(this, R.layout.admin_staffpoll_con, R.id.staff_poll_lister, stflistv);
        stflist.setAdapter(adapter);
        ParseQuery<ParseUser> stafusers = ParseUser.getQuery();
        stafusers.whereEqualTo("Type", "Staff");
        stafusers.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if (e == null) {
                    Log.e("ParseUser:", "-Fetched");
                    for (int i = 0; i < list.size(); i++) {
                        ParseUser u = list.get(i);
                        String nm = u.getUsername();
                        adapter.add(nm);
                    }
//                    Toast.makeText(Staffpoll.this, "Getting data", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("ParseUser:", e + "-Occure");
                }
            }
        });

        stflist.setOnItemClickListener(this);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin_menu, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.adminprof:
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
                    Intent i = new Intent(Staffpoll.this, Profile.class);
                    Bundle detail = new Bundle();
                    detail.putString("uname", nm);
                    detail.putString("mail", mail);
                    detail.putString("utype", utype);
                    i.putExtras(detail);
                    startActivity(i);
//                    Toast.makeText(Admin.this, "Email :"+mail, Toast.LENGTH_SHORT).show();
                    Log.i("Current User :--", "user :" + nm);
                } else {
                    Log.i("Current User :--", "user null");
                    Toast.makeText(Staffpoll.this, "Profile isn't initialized", Toast.LENGTH_SHORT).show();
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

    private void refreshStaffList() {
        adapter.clear();
        if (adapter.isEmpty()) {
            ParseQuery<ParseUser> stafusers = ParseUser.getQuery();
            stafusers.whereEqualTo("Type", "Staff");
            stafusers.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> list, ParseException e) {
                    if (e == null) {
                        Log.e("ParseStaffRefresh:", "-Fetched");
                        for (int i = 0; i < list.size(); i++) {
                            ParseUser u = list.get(i);
                            String nm = u.getUsername();
                            adapter.add(nm);
                        }
                        Toast.makeText(Staffpoll.this, "Getting data", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("ParseStaffRefresh:", e + "-Occure");
                    }
                }
            });
        } else {
            Toast.makeText(Staffpoll.this, "Adapter is Not Refreshed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String tempstaff = parent.getItemAtPosition(position).toString();
        Intent i = new Intent(this, atndnsdetail.class);
        i.putExtra("name", tempstaff);
        startActivity(i);
        Toast.makeText(this, "Selected : " + tempstaff, Toast.LENGTH_SHORT).show();
    }
}
