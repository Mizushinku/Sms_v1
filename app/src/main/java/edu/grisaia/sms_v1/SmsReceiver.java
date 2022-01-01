package edu.grisaia.sms_v1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("pcs", "on sms receiver");
        Bundle bundle = intent.getExtras();
        String format = bundle.getString("format");
        Object[] pdus = (Object[]) bundle.get("pdus");
        if(pdus != null) {
            SmsMessage[] messages = new SmsMessage[pdus.length];
            for(int i = 0; i < messages.length; ++i) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);

                Log.d("pcs", String.format("i = %d, from: %s : %s", i,
                        messages[i].getOriginatingAddress(),
                        messages[i].getMessageBody()));
            }
        }
    }
}