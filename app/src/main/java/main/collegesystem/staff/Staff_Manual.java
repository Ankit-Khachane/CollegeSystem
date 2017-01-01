package main.collegesystem.staff;

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
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import main.collegesystem.About;
import main.collegesystem.Login;
import main.collegesystem.Profile;
import main.collegesystem.R;

public class Staff_Manual extends AppCompatActivity implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
    public static String nm, mail, utype, phon, brnch, addr;
    String selctedsub, selcedexp, selcedexpid;
    int flag;
    Spinner subspiner;
    ListView subexplst;
    ArrayList<String> sublst = new ArrayList<String>();
    ArrayList<String> explst = new ArrayList<String>();
    ArrayAdapter<String> subexpary, subspinary;

    @Override
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
        getMenuInflater().inflate(R.menu.staff_menu, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.staffprof:
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

    public void showChangeLangDialog(final String expname) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.manualclickeddialog, null);
        dialogBuilder.setView(dialogView);
        final TextView expnm = (TextView) dialogView.findViewById(R.id.expname);
        expnm.setText(expname);
        final CheckBox per = (CheckBox) dialogView.findViewById(R.id.performed);
        per.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if ((isChecked) && (flag == 1)) {
                    setExpPerformed(expname);
                    Toast.makeText(Staff_Manual.this, "Performed " + expname, Toast.LENGTH_SHORT).show();
                }
                if ((isChecked) && (flag == 0)) {
                    Log.i("Exp with End", "P & C So No Changes ");
                }
                if ((!isChecked) && (flag == 1)) {
                    setExpPerformedOut(expname);
                    Toast.makeText(Staff_Manual.this, "Unperformed " + expname, Toast.LENGTH_SHORT).show();
                }
                if ((isChecked) && (flag == 2)) {
                    setExpPerformed(expname);
                    Toast.makeText(Staff_Manual.this, "Performed " + expname, Toast.LENGTH_SHORT).show();
                }
            }
        });
        final CheckBox checki = (CheckBox) dialogView.findViewById(R.id.checked);
        checki.setClickable(false);
        checki.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setExpCheked(expname);
                }
                if (!isChecked) {
                    setExpChekedOut(expname);
                }
                Toast.makeText(Staff_Manual.this, "Checked " + expname, Toast.LENGTH_SHORT).show();
            }
        });
        if (expname.endsWith("P")) {
            flag = 1;
            per.setChecked(true);
            per.setClickable(false);
            checki.setClickable(true);
            Log.i("Exp with End", "P");
        } else {
            flag = 2;
        }
        if (expname.endsWith("C")) {
            int a, b, c;
            if (expname.endsWith("P C")) {
                flag = 0;
                per.setChecked(true);
                checki.setChecked(true);
                per.setClickable(false);
                checki.setClickable(false);
                Log.i("Exp with End", "P C--");
            } else {
                checki.setChecked(true);
                Log.i("Exp with End", "C");
            }
        } else {
            flag = 2;
        }
        final ParseQuery<ParseObject> pselctdexp = ParseQuery.getQuery("Manual");
        pselctdexp.whereEqualTo(selctedsub, selcedexp);
        dialogBuilder.setTitle("Experiment Status");
        dialogBuilder.setMessage("Mark Status For");

        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                updatelist();
                Toast.makeText(Staff_Manual.this, "Status Updated", Toast.LENGTH_SHORT).show();
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

    public void setExpPerformed(final String val) {
        if (val.endsWith("P")) {
            Log.i("Exp Stat That:", "Exp Has Status");
        } else {
            final StringBuilder valpl = new StringBuilder(val);
            valpl.append(" P");
            ParseObject ob = new ParseObject("Manual");
            String tempid = ob.getObjectId();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Manual");
            query.whereEqualTo(selctedsub, val);
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                public void done(ParseObject gameScore, ParseException e) {
                    if (e == null) {
                        gameScore.put(selctedsub, valpl.toString());
                        gameScore.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Log.i("Exp Stat1:", "Updated P");
                            }
                        });
                    } else {
                        Log.i("Exp Stat 1:", "Updated P " + e.getMessage());
                    }
                }
            });
            Log.i("Exp Stat 2:", "Updated P");
        }
    }

    public void setExpCheked(final String val) {
        if (val.endsWith("C")) {
            Log.i("Exp Stat That:", "Exp Has Status");
        } else {
            final StringBuilder valpl = new StringBuilder(val);
            valpl.append(" C");
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Manual");
            query.whereEqualTo(selctedsub, val);
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                public void done(ParseObject gameScore, ParseException e) {
                    if (e == null) {
                        gameScore.put(selctedsub, valpl.toString());
                        gameScore.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Log.i("Exp Stat 1:", "Updated C");
                            }
                        });
                    } else {
                        Log.i("Exp Stat 1:", "Updated C " + e.getMessage());
                    }
                }
            });
            Log.i("Exp Stat 2:", "Updated C");
        }
    }

    public void setExpPerformedOut(final String val) {
        final String finalval = val.substring(0, val.length() - 2);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Manual");
        query.whereEqualTo(selctedsub, val);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject gameScore, ParseException e) {
                if (e == null) {
                    gameScore.put(selctedsub, finalval);
                    gameScore.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Log.i("Exp Stat 1:", "Updated C");
                        }
                    });
                }
            }
        });
        Log.i("Exp Stat 2:", "Updated P Out");
    }

    public void setExpChekedOut(final String val) {
        final String finalval = val.substring(0, val.length() - 2);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Manual");
        query.whereEqualTo(selctedsub, val);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject gameScore, ParseException e) {
                if (e == null) {
                    gameScore.put(selctedsub, finalval);
                    gameScore.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Log.i("Exp Stat 1:", "Updated C");
                        }
                    });
                }
            }
        });
        Log.i("Exp Stat 2:", "Updated C Out");
    }

    public void updatelist() {
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String tempexp = parent.getItemAtPosition(position).toString();
        showChangeLangDialog(tempexp);
    }
}