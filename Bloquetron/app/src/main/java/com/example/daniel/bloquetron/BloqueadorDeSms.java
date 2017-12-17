package com.example.daniel.bloquetron;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.w3c.dom.Text;

import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by danie on 14/12/2017.
 */

public class BloqueadorDeSms extends BroadcastReceiver {

    private static final  String TAG = "BloqueadorDeSms";
    private String FILENAME = "contatos_file";

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

                    try {
                        FileInputStream fin = context.openFileInput(FILENAME);
                        int c;
                        String temp = "";
                        while ((c = fin.read()) != -1) {
                            if (c == ';') {
                                if(temp.equals("0"+phoneNumber.substring(3))){

                                    abortBroadcast();
                                    Uri uriSMS = Uri.parse("content://sms/inbox");

                                    Cursor cursor = context.getContentResolver().query(uriSMS, null, null, null, null);

                                    cursor.moveToFirst();

                                    if(cursor.getCount() > 0){
                                        do {
                                            Log.e(TAG, cursor.getString(2)+"   "+phoneNumber);
                                            if (cursor.getString(2).equals(phoneNumber)){
                                                Log.e(TAG, cursor.getInt(1)+"   "+cursor.getString(2));
                                                int id = cursor.getInt(1);
                                                context.getContentResolver().delete(Uri.parse("content://sms/"+id), null,null);
                                                return;
                                            }
                                        }while(cursor.moveToNext());
                                    }

                                    Log.e(TAG, temp);
                                    fin.close();
                                    return;
                                }

                                temp = "";
                            } else {
                                temp = temp + Character.toString((char) c);
                            }

                        }
                        fin.close();
                    }catch(Exception e){
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
        }
    }
}
