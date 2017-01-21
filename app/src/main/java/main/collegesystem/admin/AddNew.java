package main.collegesystem.admin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import main.collegesystem.R;

public class AddNew extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner usertype;
    EditText user, fname, lname, branch, phone, email;
    String usertypev, fnamev, lnamev, branchv, phonev, emailv;
    Button addnewbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        addnewbtn = (Button) findViewById(R.id.buttonaddnew);
        fname = (EditText) findViewById(R.id.fname);
        lname = (EditText) findViewById(R.id.lname);
        branch = (EditText) findViewById(R.id.Branch);
        phone = (EditText) findViewById(R.id.phone);
        email = (EditText) findViewById(R.id.Email);
        usertype = (Spinner) findViewById(R.id.addnewusertype);
        String types[] = {"Student", "Staff"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        usertype.setAdapter(adapter);
        usertype.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String tmp = parent.getItemAtPosition(position).toString();
        usertypev = tmp;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void addUser(View v) {
        fnamev = fname.getText().toString();
        lnamev = lname.getText().toString();
        branchv = branch.getText().toString();
        phonev = phone.getText().toString();
        emailv = email.getText().toString();
        ParseUser usr = new ParseUser();
        usr.setUsername(fnamev);
        usr.setEmail(emailv);
        usr.setPassword("123456");
        usr.put("Branch", branchv);
        usr.put("PhoneNo", phonev);
        usr.put("Type", usertypev);
        usr.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(AddNew.this, "User Added", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i("New User Add :", e.getMessage());
                    Toast.makeText(AddNew.this, "Error User@new :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        /*usr.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(AddNew.this, "User Added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddNew.this, "Error New User :"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });*/
        Toast.makeText(AddNew.this, "Added User", Toast.LENGTH_SHORT).show();
    }
}
