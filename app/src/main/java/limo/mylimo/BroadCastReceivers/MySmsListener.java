package limo.mylimo.BroadCastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class MySmsListener extends BroadcastReceiver {

    private SharedPreferences preferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msg_from;
            if (bundle != null){
                //---retrieve the SMS message received---
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i=0; i<msgs.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();
                        if (msg_from.equals("My Limo")){

                            String integerFromString = msgBody.replaceAll("\\D+","");
                            Log.e("TGA", "the integer values is: " + integerFromString);
                            Intent smsReceived = new Intent();
                            smsReceived.setAction("pinreceived");
                            smsReceived.putExtra("pincode", integerFromString);
                            context.sendBroadcast(smsReceived);

                            Log.e("TAG", "here is sms from: " + msg_from);
                            Log.e("TAG", "here is sms body: " + msgBody);
                        }


                    }
                }catch(Exception e){
//                            Log.d("Exception caught",e.getMessage());
                }
            }
        }
    }
}