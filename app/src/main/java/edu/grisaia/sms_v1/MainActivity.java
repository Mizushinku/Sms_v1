package edu.grisaia.sms_v1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 9029;
    private static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 5680;
    private static boolean canSendSms = true;
    private static final String SENT = "ACTION_SMS_SENT";
    private static final String DELIVERED = "ACTION_SMS_DELIVERED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // this is not needed if use sendSmsByDefaultApp.
        checkSmsPermission();
    }

    @Override
    public void onClick(View view) {
        int vid = view.getId();
        if(vid == R.id.btn_send) {
            ArrayList<String> receivers = new ArrayList<>();
            receivers.add("521-5556");
            receivers.add("521-5558");
            sendSmsByDefaultApp(receivers);
        }
        else if(vid == R.id.btn_send2) {
            ArrayList<String> receivers = new ArrayList<>();
            receivers.add("521-5556");
            receivers.add("521-5558");
            String message = "天光満つる処に我は在り";
            sendSmsBySelf(receivers, message);
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void sendSmsByDefaultApp(ArrayList<String> receivers)
    {
        // something like : smsto:521-5556,521-5558
        String uri = "smsto:" + String.join(",", receivers);

        String sms_body = String.format("%s\n%s\n%s",
                "BEGIN",
                "エヴァに乗りなさい",
                "END");
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.setData(Uri.parse(uri));
        smsIntent.putExtra("sms_body", sms_body);

        if(smsIntent.resolveActivity(getPackageManager()) != null) {
            Log.d("pcs", "startActivity");
            startActivity(smsIntent);
        }
        else {
            Log.d("pcs", "Can't resolve app for ACTION_SENDTO Intent.");
        }
    }

    private void sendSmsBySelf(ArrayList<String> receivers, String message)
    {
        checkSmsPermission();
        if(!canSendSms) {
            Toast.makeText(this, "請確認權限後再嘗試", Toast.LENGTH_SHORT).show();
            Log.d("pcs", "canSendSms = false");
            return;
        }

        final String sms_body = String.format("%s\n%s\n%s",
                "BEGIN",
                message,
                "END");

        for(String dest : receivers) {
            sendSmsToOne(dest, sms_body);
        }
    }

    private void sendSmsToOne(final String dest, final String sms_body)
    {
        PendingIntent sentPendingIntent =
                PendingIntent.getBroadcast(this, 77, new Intent(SENT),
                        0);

        Intent deliveredIntent = new Intent(DELIVERED);
        //deliveredIntent.putExtra("dest", dest);
        PendingIntent deliveredPendingIntent =
                PendingIntent.getBroadcast(this, 78, deliveredIntent,
                        0);

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int rsCode = getResultCode();
                if(rsCode == Activity.RESULT_OK) {
                    //something u want to do
                    Log.d("pcs", "sent one to SMSC");
                }
                else {
                    //some error occur
                    Log.d("pcs", "error when sending sms");
                }
                unregisterReceiver(this);
            }
        }, new IntentFilter(SENT));

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("pcs", "delivered");
                int rsCode = getResultCode();
                if(rsCode == Activity.RESULT_OK) {
                    Log.d("pcs", "one sms is delivered");
                }
                else {
                    //some error occur
                    Log.d("pcs", "error when delivering sms");
                }
                unregisterReceiver(this);
            }
        }, new IntentFilter(DELIVERED));

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(dest, null, sms_body, sentPendingIntent, deliveredPendingIntent);
    }

    private void checkSmsPermission() {
        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);
        }

        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new  String[]{Manifest.permission.RECEIVE_SMS},
                    MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS) {
            if(permissions[0].equalsIgnoreCase(Manifest.permission.SEND_SMS) &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                canSendSms = true;
                Log.d("pcs", "SMS_SEND 權限許可成功");
                Toast.makeText(this, "send SMS permission confirm", Toast.LENGTH_SHORT).show();
            }
            else {
                //沒有權限
                canSendSms = false;
                Log.d("pcs", "SMS_SEND permission deny");
                Toast.makeText(this, "send SMS permission deny", Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode == MY_PERMISSIONS_REQUEST_RECEIVE_SMS) {
            if(permissions[0].equalsIgnoreCase(Manifest.permission.RECEIVE_SMS) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Log.d("pcs", "RECEIVE_SEND 權限許可成功");
                Toast.makeText(this, "receive SMS permission confirm", Toast.LENGTH_SHORT).show();
            }
            else {
                //沒有權限
                Log.d("pcs", "RECEIVE_SMS permission deny");
                Toast.makeText(this, "receive SMS permission deny", Toast.LENGTH_SHORT).show();
            }
        }
    }
}