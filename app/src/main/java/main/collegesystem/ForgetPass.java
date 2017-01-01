package main.collegesystem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

public class ForgetPass extends AppCompatActivity {

    EditText rstmail;
    Button submail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_pass_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        rstmail=(EditText)findViewById(R.id.mailreset);
        submail=(Button)findViewById(R.id.sub_btn);
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
                Toast.makeText(getApplicationContext(), "About", Toast.LENGTH_LONG).show();
                return true;
            case R.id.Exit:
                AlertDialog.Builder exit=new AlertDialog.Builder(this);
                exit.setMessage("Do You Want to Exit ?");
                exit.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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

    public void doForgetpswd(View v){
        final String resetmail=rstmail.getText().toString();
        if((resetmail.matches(""))) {
            Toast.makeText(ForgetPass.this, "Please Enter Email Id", Toast.LENGTH_SHORT).show();
        }else if(resetmail!=null){
            ParseUser.requestPasswordResetInBackground(resetmail, new RequestPasswordResetCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        // An email was successfully sent with reset instructions.
                        Toast.makeText(ForgetPass.this, "Reset Mail Is Sent To :" + resetmail, Toast.LENGTH_SHORT).show();
                    } else {
                        // Something went wrong. Look at the ParseException to see what's up.
                        Toast.makeText(ForgetPass.this, "Give Valid Email ID", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}
