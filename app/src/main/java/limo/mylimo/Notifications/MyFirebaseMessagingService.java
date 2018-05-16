package limo.mylimo.Notifications;

/**
 * Created by Shoaib Anwar on 14-Feb-18.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import limo.mylimo.BookingActivity;
import limo.mylimo.Feedback;
import limo.mylimo.MyHistoryScreen;
import limo.mylimo.MyLoginActivity;
import limo.mylimo.R;


public class MyFirebaseMessagingService extends FirebaseMessagingService {


    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        Log.e(TAG, "The message is from server " + remoteMessage.getData().get("title") );

        // Log.e(TAG, "From: " + remoteMessage.getFrom());
        //Log.e(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        String message = remoteMessage.getData().get("title");
        Log.e("TAg", "The message from server " + message);
        String finishedText[] = message.split("\\.");
        String firstPart = finishedText[0];
        String secondPart = finishedText[1];

        Log.e("TAg", "The message from server text only " + firstPart);
        Log.e("TAg", "The message from server order id: " + secondPart);


        //Calling method to generate notification
        sendNotification(firstPart, secondPart);


    }


    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);


        String title = intent.getExtras().getString("gcm.notification.title");
        String body = intent.getExtras().getString("gcm.notification.body");

        Log.e("TAG: ", "Key is haree: " + title);
        Log.e("TAG: ", "Key is body: " + body);


        //sendNotification(body);

        for (String key : intent.getExtras().keySet()) {
            Object value = intent.getExtras().get(key);

            Log.e("TAG: ", " test test testr: " + key);
            Log.e("TAG: ", " text text text text: " + value);

            if (key.equals("title")){

                Log.e("TAG: ", " shoaib shoaib shoaib shoaib: " + value);

                String valueString = value.toString();
                Log.e("TAg", "The message from server " + valueString);
                String finishedText[] = valueString.split("\\.");
                String firstPart = finishedText[0];
                String secondPart = finishedText[1];
                //Calling method to generate notification
                NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancelAll();
                sendNotification(firstPart, secondPart);

            }

        }

    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(final String messageBody, final String orderId) {

        PendingIntent pendingIntent;



        SharedPreferences sharedPreferences = getSharedPreferences("mylimouser", 0);
        String fullname  = sharedPreferences.getString("fullname", null);
        if (fullname!=null) {

            SharedPreferences sharedPreferencesForFeedback = getSharedPreferences("givefeedback", 0);
            SharedPreferences.Editor editor = sharedPreferencesForFeedback.edit();
            editor.putString("order_id", orderId);
            editor.commit();

            Intent intent = new Intent(this, Feedback.class);
            intent.putExtra("orderid", orderId);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
             pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);
        }
        else {
            Intent intent = new Intent(this, MyLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
             pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);
        }

        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.drawable.cartype_icon);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.cartype_icon)
                // .setLargeIcon(icon)
                .setContentTitle("Mylimo")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }


}
