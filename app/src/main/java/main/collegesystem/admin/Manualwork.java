package main.collegesystem.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import main.collegesystem.About;
import main.collegesystem.Login;
import main.collegesystem.Profile;
import main.collegesystem.R;

public class Manualwork extends AppCompatActivity implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
    public static String nm, mail, utype;
    String selctedsub, selcedexp;
    Spinner subspiner;
    ListView subexplst;
    ArrayList<String> sublst = new ArrayList<String>();
    ArrayList<String> explst = new ArrayList<String>();
    ArrayAdapter<String> subexpary, subspinary;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_manual_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subspiner = (Spinner) findViewById(R.id.subspiner);
        subspinary = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sublst);
        subspinary.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subspiner.setAdapter(subspinary);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Subjects");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                //attendance subject sheet
                if (e == null) {
                    Log.e("ParseObject Subjects:", "-Fetched");
                    for (int i = 0; i < list.size(); i++) {
                        ParseObject u = list.get(i);
                        String nm = u.get("Subject").toString();
                        subspinary.add(nm);
                    }
                } else {
                    Log.e("ParseObject Subjects:", e + "-Occure");
                }
            }
        });
        subexplst = (ListView) findViewById(R.id.explist);
        subexpary = new ArrayAdapter<String>(getApplicationContext(), R.layout.staffmanualexprow, R.id.exprowone, explst);
        subexpary.setNotifyOnChange(true);
        subexplst.setAdapter(subexpary);
        subspiner.setOnItemSelectedListener(this);
        subexplst.setOnItemClickListener(this);
//        showChangeLangDialog("Exp 1 AJP");
    }

    public void showChangeLangDialog(final String expname) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.manualclickeddialog, null);
        dialogBuilder.setView(dialogView);
        final TextView expnm = (TextView) dialogView.findViewById(R.id.expname);
        expnm.setText(expname);
        final CheckBox per = (CheckBox) dialogView.findViewById(R.id.performed);
        final CheckBox checki = (CheckBox) dialogView.findViewById(R.id.checked);
        per.setVisibility(View.INVISIBLE);
        checki.setVisibility(View.INVISIBLE);
        final ParseQuery<ParseObject> pselctdexp = ParseQuery.getQuery("Manual");
        pselctdexp.whereEqualTo(selctedsub, selcedexp);
        dialogBuilder.setTitle("Experiment Status");
        dialogBuilder.setMessage("Status For");

        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(Manualwork.this, "Status Updated", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selctedsub = parent.getItemAtPosition(position).toString();
        subexpary.clear();
        if (subexpary.isEmpty()) {
            ParseQuery<ParseObject> pexp = ParseQuery.getQuery("Manual");
            pexp.orderByAscending("SEQUENCE");
            pexp.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {
                        Log.e("ParseObject Manual R:", "-Fetched");
                        for (int i = 0; i < list.size(); i++) {
                            ParseObject u = list.get(i);
                            String nm = u.get(selctedsub).toString();
                            if (nm != null) {
                                subexpary.add(nm);
                                subexpary.notifyDataSetChanged();
                            } else {
                                break;
                            }
                        }
                    } else {
                        Log.e("ParseObject Manual R:", e + "-Occure");
                    }
                }
            });
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String tempexp = parent.getItemAtPosition(position).toString();
        showChangeLangDialog(tempexp);
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
                    Intent i = new Intent(Manualwork.this, Profile.class);
                    Bundle detail = new Bundle();
                    detail.putString("uname", nm);
                    detail.putString("mail", mail);
                    detail.putString("utype", utype);
                    i.putExtras(detail);
                    startActivity(i);
//                    Toast.makeText(Admin.this, "Email :"+mail, Toast.LENGTH_SHORT).show();
                    Log.i("Current User :--", "user :" + nm);
                } else {
                    Log.i("Current User :--", "user null");
                    Toast.makeText(Manualwork.this, "Profile isn't initialized", Toast.LENGTH_SHORT).show();
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
                Intent t = new Intent(this, About.class);
                startActivity(t);
                Toast.makeText(getApplicationContext(), "About Selected", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
