package main.collegesystem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

public class Profile extends AppCompatActivity {
    TextView usertyp,name,email,lnm,lem,phn,brnch,addresst;
    public String namevl,emailvl,utypev,phone,branch,address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        usertyp=(TextView)findViewById(R.id.Usertype);
        name=(TextView)findViewById(R.id.namevalue);
        lnm=(TextView)findViewById(R.id.nm);
        lem=(TextView)findViewById(R.id.mail);
//        lnm.setPaintFlags(lnm.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        email=(TextView)findViewById(R.id.mailvalue);
//        lem.setPaintFlags(lem.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        phn=(TextView)findViewById(R.id.phntv2);
        brnch=(TextView)findViewById(R.id.brnchtv2);
        addresst=(TextView)findViewById(R.id.adretv2);
        Bundle extras = getIntent().getExtras();
        if (savedInstanceState == null) {
            if(extras == null) {
                //keep data varibale empty
            } else {
                namevl= extras.getString("uname");
                emailvl= extras.getString("mail");
                utypev=extras.getString("utype");
                phone=extras.getString("phone");
                branch=extras.getString("branch");
                address=extras.getString("address");
            }
        } else {
            namevl= (String) savedInstanceState.getSerializable("uname");
            emailvl= (String) savedInstanceState.getSerializable("mail");
        }
//        namevl=getIntent().getStringExtra("uname");
        name.setText(namevl);
//        emailvl=getIntent().getStringExtra("mail");
        email.setText(emailvl);
        usertyp.setText(utypev);
        phn.setText(phone);
        brnch.setText(branch);
        addresst.setText(address);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.about_profile, menu);//Menu Resource, Menu

        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                Intent t=new Intent(this, About.class);
                startActivity(t);
                Toast.makeText(getApplicationContext(), "About", Toast.LENGTH_LONG).show();
                return true;
            case R.id.Exit:
//                Toast.makeText(getApplicationContext(),"Exit", Toast.LENGTH_LONG).show();
                AlertDialog.Builder exit=new AlertDialog.Builder(this);
                exit.setMessage("Do You Want to Exit ?");
                exit.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Profile.this.finish();
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
            case R.id.Change_Password:
                showChangePassDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void showChangePassDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);
        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);
        dialogBuilder.setTitle("Enter Mail");
        dialogBuilder.setMessage("Enter Valid Email Address");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String tempmail=edt.getText().toString();
                change_pass(tempmail);
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
    public void change_pass(final String mail){
        ParseUser.requestPasswordResetInBackground(mail, new RequestPasswordResetCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // An email was successfully sent with reset instructions.
                    Toast.makeText(Profile.this, "Password Reset Link is Sent To "+mail, Toast.LENGTH_SHORT).show();
                } else {
                    // Something went wrong. Look at the ParseException to see what's up.
                    if(e.getCode()==205){
                        Toast.makeText(Profile.this, "User with thid email not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}
