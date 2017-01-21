package main.collegesystem;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import main.collegesystem.admin.Admin;
import main.collegesystem.staff.Staff;
import main.collegesystem.student.Student;

public class Login extends AppCompatActivity{

    public static boolean userlogedin = false;
    public ParseObject userob;
    public AlertDialog.Builder ex;
    EditText nm, pass;
    String nmv, passv,log_typ;
    Button log_btn,goreg;
    ParseUser loged_user;
    boolean val;
    TextView forgetpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //initialize edittext,TextViewn Buttons for Login Activity
        forgetpass=(TextView)findViewById(R.id.textView18);
        forgetpass.setPaintFlags(forgetpass.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        log_btn=(Button)findViewById(R.id.loginbutton);
        goreg=(Button)findViewById(R.id.registerpage);
        nm = (EditText) findViewById(R.id.login_name);
        pass = (EditText) findViewById(R.id.login_pass);
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
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
    }

    public void gotoforget(View v) {
        Intent i = new Intent(this, ForgetPass.class);
        startActivity(i);
    }
    public void gotoreg(View v){
        Intent i=new Intent(this,Register.class);
        startActivity(i);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.about, menu);//Menu Resource, Menu
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                Intent t=new Intent(this, About.class);
                startActivity(t);
                Toast.makeText(getApplicationContext(),"About",Toast.LENGTH_LONG).show();
                return true;
            case R.id.Exit:
//                Toast.makeText(getApplicationContext(),"Exit", Toast.LENGTH_LONG).show();
                AlertDialog.Builder exit=new AlertDialog.Builder(this);
                exit.setMessage("Do You Want to Exit ?");
                exit.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Login.this.finish();
                    }
                });
                exit.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                   dialog.cancel();
                    }
                });
                AlertDialog exitdialog = exit.create();
                // show alert
                exitdialog.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void doLogin(View v) {
        if(isInternetOn()){
        nmv = nm.getText().toString();
        passv = pass.getText().toString();
        final ProgressDialog pdlg = new ProgressDialog(this);
        pdlg.setTitle("Loging In");
        pdlg.setMessage("Please Wait");
        if ((nmv.matches("")) && (passv.matches(""))) {
            //blank text field toast
            Toast.makeText(this, "Please Enter Detail", Toast.LENGTH_SHORT).show();
        } else if ((nmv != null) && (passv != null)) {
            pdlg.show();
            //fielled textfield action
            ParseUser.logInInBackground(nmv, passv, new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        // Hooray! The user is logged in.
                        pdlg.cancel();
                        String typeuser = user.getString("Type");
                        String sessionToken = user.getSessionToken();
                        Log.e("Login:", "---Done");
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("Login_state", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        if (typeuser.matches("Admin")) {
                            Intent i = new Intent(Login.this, Admin.class);
                            startActivity(i);
                            finish();
                            editor.putBoolean("firstlogin", false);
                            editor.putString("type", typeuser);
                            editor.putString("sessionToken", sessionToken);
                            editor.apply();
                            Log.i("Logout --", "Done");
                            Toast.makeText(Login.this, "admin Preference is stored", Toast.LENGTH_SHORT).show();
                        } else if (typeuser.matches("Staff")) {
                            Intent i = new Intent(Login.this, Staff.class);
                            startActivity(i);
                            finish();
                            editor.putBoolean("firstlogin", false);
                            editor.putString("type", typeuser);
                            editor.putString("sessionToken", sessionToken);
                            editor.apply();

                        } else if (typeuser.matches("Student")) {
                            Intent i = new Intent(Login.this, Student.class);
                            startActivity(i);
                            finish();
                            editor.putBoolean("firstlogin", false);
                            editor.putString("type", typeuser);
                            editor.putString("sessionToken", sessionToken);
                            editor.apply();
                        }
                        Toast.makeText(Login.this, "Login Successfull : " + typeuser, Toast.LENGTH_SHORT).show();
                    } else {
                        pdlg.cancel();
                        // Signup failed. Look at the ParseException to see what happened.
                        Log.e("Login:", "---Failed_" + e.getMessage());
                        Toast.makeText(Login.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
        else {
            Toast.makeText(this, "Please Check Internet Connection ", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        ex.show();
    }
    public final boolean isInternetOn() {
        // get Connectivity Manager object to check connection
        ConnectivityManager connec = (ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
        // Check for network connections
        if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
             connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
             connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
             connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {
            // if connected with internet
            return true;
        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {
            return false;
        }
        return false;
    }
}