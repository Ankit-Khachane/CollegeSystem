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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.LinkedList;

import main.collegesystem.About;
import main.collegesystem.Login;
import main.collegesystem.Profile;
import main.collegesystem.R;
import main.collegesystem.staff.Staff_Notice_List;

public class Notice extends AppCompatActivity {
    public static String nm,mail,utype;
    EditText titl,content;
    CheckBox staff,stud;
    boolean staff_flag,stud_flag;
    String titlev,contentv;
    Button uplnot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_notice);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        titl=(EditText)findViewById(R.id.notice_title);
        content=(EditText)findViewById(R.id.notice_content);
        uplnot=(Button)findViewById(R.id.upl_notice);
        staff=(CheckBox)findViewById(R.id.chk_staff);
        stud=(CheckBox)findViewById(R.id.chk_stud);
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
                SharedPreferences pref=this.getSharedPreferences("Login_state", MODE_PRIVATE);
                String sessionToken=pref.getString("sessionToken", "");
                try {
                    ParseUser.become(sessionToken);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                ParseUser user = ParseUser.getCurrentUser();
                if(user!=null) {
                    nm = user.getUsername();
                    mail = user.getEmail();
                    utype=user.get("Type").toString();
                    Intent i = new Intent(Notice.this, Profile.class);
                    Bundle detail=new Bundle();
                    detail.putString("uname",nm);
                    detail.putString("mail", mail);
                    detail.putString("utype",utype);
                    i.putExtras(detail);
                    startActivity(i);
//                    Toast.makeText(Admin.this, "Email :"+mail, Toast.LENGTH_SHORT).show();
                    Log.i("Current User :--", "user :" + nm);
                }else {
                    Log.i("Current User :--", "user null");
                    Toast.makeText(Notice.this, "Profile isn't initialized", Toast.LENGTH_SHORT).show();
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
                Intent t=new Intent(this, About.class);
                startActivity(t);
                Toast.makeText(getApplicationContext(),"About Selected", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void selectStaff(View v){
        if(staff.isChecked()) {
            staff_flag = true;
        }
        else {
            staff_flag=false;
        }
    }
    public void selectStud(View v) {
        if (stud.isChecked()) {
            stud_flag = true;
        }else {
            stud_flag=false;
        }
    }
    public void uplnotic(View v){
        titlev=titl.getText().toString();
        contentv=content.getText().toString();
        ParseObject notice=new ParseObject("Notice");
        notice.put("Title",titlev);
        notice.put("Content", contentv);
        notice.put("From",ParseUser.getCurrentUser().getUsername());
        notice.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    //TODO :send push to selcted user staff / stud
                    ParsePush pushnotice = new ParsePush();
                    long weekInterval = 60*60*24*7;
                    if (staff_flag) {
                        //if staff is checked
                        pushnotice.setChannel("Staff");
                        pushnotice.setMessage(titlev);
                        pushnotice.setExpirationTimeInterval(weekInterval);
                        pushnotice.sendInBackground();
                    }
                    if (stud_flag) {
                        //if stud is checked
                        pushnotice.setChannel("Student");
                        pushnotice.setMessage(titlev);
                        pushnotice.setExpirationTimeInterval(weekInterval);
                        pushnotice.sendInBackground();
                    }
                    if ((stud_flag && staff_flag)) {
                        //if stud and staff both are selected
                        LinkedList<String> channels = new LinkedList<String>();
                        channels.add("Student");
                        channels.add("Staff");
                        pushnotice.setChannels(channels);
                        pushnotice.setMessage(titlev);
                        pushnotice.setExpirationTimeInterval(weekInterval);
                        pushnotice.sendInBackground();
                    }
                    titl.setText("");
                    content.setText("");
                    Toast.makeText(Notice.this, "Notice Uploaded", Toast.LENGTH_SHORT).show();
                } else {
                    //
                    Toast.makeText(Notice.this, "Notice Upload Fail", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //Toast.makeText(this,"Uploaded Notice",Toast.LENGTH_SHORT).show();
    }
    public void gotoNoticlist(View view){
        Intent i=new Intent(getApplicationContext(), Admin_Notice_List.class);
        startActivity(i);
    }
}
