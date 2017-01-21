package main.collegesystem;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import main.collegesystem.admin.Admin;
import main.collegesystem.staff.Staff;
import main.collegesystem.student.Student;

public class Splash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        final SharedPreferences pref = this.getSharedPreferences("Login_state", MODE_PRIVATE);
        Thread background = new Thread() {
            public void run() {
                try {
                    // Thread will sleep for 5 seconds
                    sleep(1000);
                    // After 5 seconds redirect to another intent
                    String type = pref.getString("type", null);
                    boolean firstlogin = pref.getBoolean("firstlogin", true);
                    if ((type == (null)) && (firstlogin)) {
                        Intent i = new Intent(getBaseContext(), Login.class);
                        startActivity(i);
                        finish();
                    }
                    if (type != null) {
                        if (type.equals("Admin")) {
                            Intent i = new Intent(getBaseContext(), Admin.class);
                            startActivity(i);
                            finish();
                        }
                        if (type.equals("Staff")) {
                            Intent i = new Intent(getBaseContext(), Staff.class);
                            startActivity(i);
                            finish();
                        }
                        if (type.equals("Student")) {
                            Intent i = new Intent(getBaseContext(), Student.class);
                            startActivity(i);
                            finish();
                        }
                   /* if(type==null) {
                      Intent i = new Intent(getBaseContext(), Login.class);
                     startActivity(i);

                        }*/
                    }
                    //Remove activity
                } catch (Exception e) {
                    Log.e("Error Login : ", "login error");
                }
            }
        };
        // start thread
        background.start();
    }
}
