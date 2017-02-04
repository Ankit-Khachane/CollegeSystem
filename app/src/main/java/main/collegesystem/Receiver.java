package main.collegesystem;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

public class Receiver extends ParsePushBroadcastReceiver {
    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);
        Bundle extra_data = intent.getExtras();
        Log.i("onReciver Block : ", " onPushReceive: " + extra_data.getString("com.parse.Data") + intent.getAction().toString());
    }

    @Override
    public void onPushOpen(Context context, Intent intent) {
        Log.e("Push", "Clicked");
        Intent i = new Intent(context, Splash.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            try {
                JSONObject job = new JSONObject(intent.getExtras().getString("com.parse.Data"));
                String uritemp = job.getString("alert");
                Log.i("push data :++ ", uritemp);
                if (uritemp.equals("noticestud")) {
                    Toast.makeText(context, "" + uritemp, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            context.startActivity(i);
        }
    }

}