package main.collegesystem.staff;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.parse.LogOutCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import main.collegesystem.About;
import main.collegesystem.Login;
import main.collegesystem.Profile;
import main.collegesystem.R;

public class Staff extends AppCompatActivity {
    public static String nm,mail,utype,phon,brnch,addr;
    AlertDialog.Builder ex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ex=new AlertDialog.Builder(this).setTitle("Exit ?").setMessage("Do You Want To Exit").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        setChannel();
    }
    public void setChannel(){
        ParseInstallation instal=ParseInstallation.getCurrentInstallation();
        String curusrName=ParseUser.getCurrentUser().getUsername();
        List<String> ls=ParseInstallation.getCurrentInstallation().getList("channels");
        if(ls==null){
            instal.put("channels",Arrays.asList("Staff"));
            instal.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {

                }
            });
        }
        else
        {
            instal.removeAll("channels", ls);
            instal.put("channels", Arrays.asList("Staff"));
            instal.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {

                }
            });
        }
        instal.put("username", curusrName);
        instal.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

            }
        });
    }
    public void unsetChannel() {
        List<String> las=ParseInstallation.getCurrentInstallation().getList("channels");
        if(las==null) {
            Log.i("Channels :","Not Cleared !");}
        else {
            ParseInstallation.getCurrentInstallation().removeAll("channels", las);
            ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    Log.i("Channels :", "Cleared !");
                }});}
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
                SharedPreferences pref=this.getSharedPreferences("Login_state", MODE_PRIVATE);
                String sessionToken=pref.getString("sessionToken", "");
                try {
                    ParseUser.become(sessionToken);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                ParseUser user=ParseUser.getCurrentUser();
                if(user!=null) {
                    nm = user.getUsername();
                    mail = user.getEmail();
                    utype=user.get("Type").toString();
                    phon=user.get("PhoneNo").toString();
                    brnch=user.get("Branch").toString();
                    addr=user.get("Address").toString();
                    Intent i = new Intent(getApplicationContext(), Profile.class);
                    Bundle detail=new Bundle();
                    detail.putString("uname",nm);
                    detail.putString("mail", mail);
                    detail.putString("utype",utype);
                    detail.putString("phone",phon);
                    detail.putString("branch",brnch);
                    detail.putString("address",addr);

                    i.putExtras(detail);
                    startActivity(i);
//                    Toast.makeText(Admin.this, "Email :"+mail, Toast.LENGTH_SHORT).show();
                    Log.i("Current User :--", "user :" + nm);
                }else {
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
                Intent t=new Intent(this, About.class);
                startActivity(t);
                Toast.makeText(getApplicationContext(),"About Selected", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        ex.show();
    }
    public void gotoStaffAttendance(View view){
        Intent i=new Intent(this,Staff_Attendance.class);
        startActivity(i);
    }
    public void gotoStaffManual(View view){
        Intent i=new Intent(this,Staff_Manual.class);
        startActivity(i);
    }
    public void gotoStaffNotice(View view){
        Intent i=new Intent(this,Staff_Notice.class);
        startActivity(i);
    }
    public void gotoStaffSchedule(View view){
        Intent i=new Intent(this,Staff_Schedule.class);
        startActivity(i);
    }
    public void gotoStaffAdminPoll(View view){
        Intent i=new Intent(this,Staff_Admin_poll.class);
        startActivity(i);
    }
}
