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
        Bundle extrasdata = intent.getExtras();
        Log.i("onReciver Block : ", " onPushReceive: " + extrasdata.getString("com.parse.Data"));
    }

    @Override
    public void onPushOpen(Context context, Intent intent) {
        Log.e("Push", "Clicked");
        Intent i = new Intent(context, Splash.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String jsonData = extras.getString("com.parse.Data");
            try {
                JSONObject job = new JSONObject(jsonData);
                String uritemp = job.getString("uri");
                Log.i("push data :++ ", uritemp);
                if (uritemp.equals("noticestud")) {
                    Toast.makeText(context, "" + uritemp, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("Notify Data :+++", jsonData);
            context.startActivity(i);
        }
    }

}