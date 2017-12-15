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
            try
            {
                if (intent.getAction().equals("android.intent.action.PHONE_STATE"))
                {
                    String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                    if (state.equals(TelephonyManager.EXTRA_STATE_RINGING))
                    {
                        String incomingNumber =intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

                        if(getMList().contains(incomingNumber)) {

                            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

                            Class<?> classTelephony = Class.forName(telephonyManager.getClass().getName());
                            Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");

                            methodGetITelephony.setAccessible(true);

                            Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);

                            Class<?> telephonyInterfaceClass = Class.forName(telephonyInterface.getClass().getName());
                            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");

                            methodEndCall.invoke(telephonyInterface);
                        }

                    }

                }
            }
            catch (Exception ex)
            {
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