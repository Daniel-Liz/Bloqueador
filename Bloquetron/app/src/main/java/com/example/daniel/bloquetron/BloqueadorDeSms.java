package com.example.daniel.bloquetron;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by danie on 14/12/2017.
 */

public class BloqueadorDeSms extends BroadcastReceiver {


    private static List<String> mList;
    private static final  String TAG = "BloqueadorDeSms";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null){
            String action = intent.getAction();

            if(action.equals("android.provider.Telephony.SMS_RECEIVED"))  {
                Bundle extras = intent.getExtras();
                if ( extras != null ){
                    String format = extras.getString("format");
                    //read sms
                    final Object[] pdusObj = (Object[]) extras.get("pdus");
                    String phoneNumber;
                    SmsMessage currentMessage;

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        currentMessage = SmsMessage
                                .createFromPdu((byte[]) pdusObj[0], format);
                    }else{
                        currentMessage = SmsMessage
                                .createFromPdu((byte[]) pdusObj[0]);
                    }
                    phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    Log.e(TAG, "0"+phoneNumber.substring(3));

                    if(getMList().contains("0"+phoneNumber.substring(3))) {
                        abortBroadcast();
                        Log.e(TAG, getAbortBroadcast()+"");
                    }
                }
            }
        }
    }

    public void setMList(List<String> mList){
        this.mList = mList;
    }
    public List<String> getMList(){
        return mList;
    }
}
