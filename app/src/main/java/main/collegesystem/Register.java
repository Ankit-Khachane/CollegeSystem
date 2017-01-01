package main.collegesystem;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class Register extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    EditText regnm, regpass, regmail;
    Button reg_btn;
    String regnmv, regpassv, regmailv, regtypev;
    Spinner reg_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView tvf=(TextView)findViewById(R.id.careiii);
        tvf.setPaintFlags(tvf.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        String type[]={"Staff","Student"};
        reg_btn=(Button)findViewById(R.id.Reg_btn);
        regnm = (EditText) findViewById(R.id.reg_name);
        regpass = (EditText) findViewById(R.id.reg_pass);
        regmail = (EditText) findViewById(R.id.reg_email);
        reg_spinner=(Spinner)findViewById(R.id.reg_spin);
        reg_spinner.setOnItemSelectedListener(Register.this);
        ArrayAdapter<String> typelist=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, type);
        typelist.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reg_spinner.setAdapter(typelist);
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
                        /*Register.this.finish();
                        System.exit(0);*/
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("EXIT", true);
                        startActivity(intent);
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

    public void doRegister(View view) {
        if (isInternetOn()){
            regnmv = regnm.getText().toString();
            regpassv = regpass.getText().toString();
            regmailv = regmail.getText().toString();
            if (((regnmv.matches("")) && (regpassv.matches(""))) && (regmailv.matches(""))) {
            Toast.makeText(Register.this, "Please Enter Detail", Toast.LENGTH_SHORT).show();
            } else {
            if (((regnmv != null) && (regpassv != null)) && ((regmailv != null)) && (regtypev != null)) {
                if (regpassv.length() >= 8) {
                    final ProgressDialog pdlg = new ProgressDialog(this);
                    pdlg.setTitle("Registering");
                    pdlg.setMessage("Please Wait");
                    pdlg.show();
                    //crosscheck logic
                    final ParseQuery<ParseObject> query = ParseQuery.getQuery("CrossCheckTable");
                    query.whereEqualTo("Email", regmailv);
                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        public void done(final ParseObject object, ParseException e) {
                            if (object == null) {
                                Log.d("score", "The getFirst request failed.");
                                Log.i("Object :", e.getMessage());
                                pdlg.cancel();
                                Toast.makeText(Register.this, "Object Got null", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("score", "Retrieved the object.");
                                final boolean chk = object.getBoolean("OTREG");
                                if (chk) {
                                    pdlg.cancel();
                                    //toast for report admin
                                    Toast.makeText(Register.this, "Contact Administrator", Toast.LENGTH_SHORT).show();

                                } else if (!chk) {
                                    //call register funtion
                                    ParseUser user = new ParseUser();
                                    user.setUsername(regnmv);
                                    user.setPassword(regpassv);
                                    user.setEmail(regmailv);
                                    user.put("Type", regtypev);
                                    user.signUpInBackground(new SignUpCallback() {

                                        public void done(ParseException e) {
                                            if (e == null) {
                                                // Hooray! Let them use the app now.
                                                pdlg.cancel();
                                                object.put("OTREG", true);
                                                object.saveInBackground();
                                                ParsePush.subscribeInBackground(regtypev);
                                                Intent i = new Intent(Register.this, Login.class);
                                                finish();
                                                startActivity(i);
                                                Toast.makeText(Register.this, "Register Successful", Toast.LENGTH_SHORT).show();
                                            } else {
                                                pdlg.cancel();
                                                // Sign up didn't succeed. Look at the ParseException
                                                // to figure out what went wrong
                                                Toast.makeText(Register.this, "Register Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    }
                                    Toast.makeText(Register.this, "Object Got : " + chk, Toast.LENGTH_SHORT).show();
//                                  Log.e("Object got :",e.toString());
                                }
                            }
                        });
                    } else {
                        Toast.makeText(Register.this, "Password Should Be 8 Digit Long", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }else {
            Toast.makeText(this, "Please Check Internet Connection ", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        regtypev=parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(ParseUser.getCurrentUser()!=null){

        }
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