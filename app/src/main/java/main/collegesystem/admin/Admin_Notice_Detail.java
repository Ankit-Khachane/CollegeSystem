package main.collegesystem.admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import main.collegesystem.About;
import main.collegesystem.Login;
import main.collegesystem.Profile;
import main.collegesystem.R;

public class Admin_Notice_Detail extends AppCompatActivity {
    public static String nm, mail, utype, phon, brnch, addr;
    TextView ttl, cont, frm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_noticedetail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ttl = (TextView) findViewById(R.id.titlex);
        cont = (TextView) findViewById(R.id.contentx);
        frm = (TextView) findViewById(R.id.fromx);
        String ttlv = getIntent().getExtras().getString("notidob");
        if (ttlv != null) {
            Log.i("Notice Object Dtl :", ttlv);
            ParseQuery<ParseObject> pob = ParseQuery.getQuery("Notice");
            pob.getInBackground(ttlv, new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if (parseObject != null) {
                        ttl.setText(parseObject.getString("Title"));
                        cont.setText(parseObject.getString("Content"));
                        frm.setText(parseObject.getString("From"));
                    } else {
                        Toast.makeText(getApplicationContext(), "Object null", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ttl.setText("");
        cont.setText("");
        frm.setText("");
    }
}
