package com.example.daniel.bloquetron;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

public class BloqueadorDeChamadas extends BroadcastReceiver{

    Context context;
    private static List<String> mList;
    private static final String TAG = "BloqueadorDeChamadas";


    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Bundle myBundle = intent.getExtras();
        if (myBundle != null)
        {
            Log.e(TAG,"--------Not null-----");
            try
            {
                if (intent.getAction().equals("android.intent.action.PHONE_STATE"))
                {
                    String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                    Log.e(TAG,"--------in state-----");
                    if (state.equals(TelephonyManager.EXTRA_STATE_RINGING))
                    {
                        // Incoming call
                        String incomingNumber =intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                        

                        if(getMList().contains(incomingNumber)) {


// this is main section of the code,. could also be use for particular number.
                            // Get the boring old TelephonyManager.
                            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

                            // Get the getITelephony() method
                            Class<?> classTelephony = Class.forName(telephonyManager.getClass().getName());
                            Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");

                            // Ignore that the method is supposed to be private
                            methodGetITelephony.setAccessible(true);

                            // Invoke getITelephony() to get the ITelephony interface
                            Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);

                            // Get the endCall method from ITelephony
                            Class<?> telephonyInterfaceClass = Class.forName(telephonyInterface.getClass().getName());
                            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");

                            // Invoke endCall()
                            methodEndCall.invoke(telephonyInterface);
                        }

                    }

                }
            }
            catch (Exception ex)
            { // Many things can go wrong with reflection calls
                ex.printStackTrace();
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