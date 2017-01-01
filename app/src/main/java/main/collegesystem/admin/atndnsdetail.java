package main.collegesystem.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import main.collegesystem.About;
import main.collegesystem.Login;
import main.collegesystem.Profile;
import main.collegesystem.R;

public class atndnsdetail extends AppCompatActivity {
    TextView namedet,maildet,phone,address,branch;
    String name,mail,phonev,addressv,branchv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atndnsdetail_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        namedet=(TextView)findViewById(R.id.nameval);
        maildet=(TextView)findViewById(R.id.mailval);
        phone=(TextView)findViewById(R.id.phonetv);
        address=(TextView)findViewById(R.id.adrestv);
        branch=(TextView)findViewById(R.id.branchtv);
        Bundle extras = getIntent().getExtras();
        if (savedInstanceState == null) {
            if(extras == null) {
                //keep data varibale empty
            } else {
                name= extras.getString("name");
                show_detail(name);
            }
        } else {
            name= (String) savedInstanceState.getSerializable("name");
        }
    }

    public void show_detail(String nm) {
        ParseQuery<ParseUser> detail=ParseUser.getQuery();
        detail.whereEqualTo("username",nm);
        detail.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                name=parseUser.getUsername();
                namedet.setText(name);
                mail=parseUser.getEmail();
                maildet.setText(mail);
                phonev=parseUser.get("PhoneNo").toString();
                phone.setText(phonev);
                branchv=parseUser.get("Branch").toString();
                branch.setText(branchv);
                addressv=parseUser.get("Address").toString();
                address.setText(addressv);
            }
        });
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
                    name= user.getUsername();
                    mail = user.getEmail();
                    Intent i = new Intent(atndnsdetail.this, Profile.class);
                    Bundle detail=new Bundle();
                    detail.putString("uname",name);
                    detail.putString("mail", mail);
                    i.putExtras(detail);
                    startActivity(i);
//                    Toast.makeText(Admin.this, "Email :"+mail, Toast.LENGTH_SHORT).show();
                    Log.i("Current User :--", "user :" + name);
                }else {
                    Log.i("Current User :--", "user null");
                    Toast.makeText(atndnsdetail.this, "Profile isn't initialized", Toast.LENGTH_SHORT).show();
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

}
