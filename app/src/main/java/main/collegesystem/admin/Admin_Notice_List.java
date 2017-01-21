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

public class Admin_Notice_List extends AppCompatActivity implements AdapterView.OnItemClickListener {
    public static String nm, mail, utype, phon, brnch, addr;
    ListView noticels;
    ArrayAdapter<String> noticeadapter;
    ArrayList<String> notislist = new ArrayList<String>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staffnoticelist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        noticels = (ListView) findViewById(R.id.noticelist);
        noticeadapter = new ArrayAdapter<String>(this, R.layout.staffnoticelist_con, R.id.noticerowone, notislist);
        noticels.setAdapter(noticeadapter);
        noticels.setOnItemClickListener(this);
        ParseQuery<ParseObject> pnoticelist = ParseQuery.getQuery("Notice");
        pnoticelist.orderByDescending("createdAt");
        pnoticelist.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    Log.i("Notice Title ", "Fetched Done");
                    for (int i = 0; i < list.size(); i++) {
                        ParseObject u = list.get(i);
                        String tmp = u.get("Title").toString();
                        noticeadapter.add(tmp);
                    }
                } else {
                    Log.i("Notice Titile ", "Fetched Failed");
                }
            }
        });
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

    public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
        final String selectednotice = parent.getItemAtPosition(position).toString();
        ParseQuery<ParseObject> pdetail = ParseQuery.getQuery("Notice");
        pdetail.whereEqualTo("Title", selectednotice);
        pdetail.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                final String notid = parseObject.getObjectId();
                Intent i = new Intent(getApplicationContext(), Admin_Notice_Detail.class);
                i.putExtra("notidob", notid);
                startActivity(i);
                Toast.makeText(getApplicationContext(), "Parse :" + parseObject.getObjectId(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
