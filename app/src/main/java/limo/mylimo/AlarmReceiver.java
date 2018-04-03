package limo.mylimo;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Ringtone;
import android.net.Uri;
import android.util.Log;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Shoaib anwar on 21-Nov-17.
 */

public class AlarmReceiver extends BroadcastReceiver {

    int id;


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("TAG", "Allarm Is running");


        int alarmId = intent.getExtras().getInt("alarmId");
        Log.e("TAG", "alarmId: " + alarmId);
        int dbId = getLastId(context);
        id = dbId;


        Notification(context);


    }

    public void Notification(Context context) {



        // Set Notification Title
        String strtitle = "Sample Title";
        // Set Notification Text
        String strtext = "Second Sample";

        // Open NotificationView Class on Notification Click
        Intent intent = new Intent(context, MapsActivity.class);
        // Send data to NotificationView Class
        //   intent.putExtra("title", strtitle);
        intent.putExtra("mid", strtext);


        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT
                        | PendingIntent.FLAG_ONE_SHOT);

        //Create Notification using NotificationCompat.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                // Set Icon
                /*.setSmallIcon(R.drawable.cartype_icon)*/
                // Set Ticker Message
                .setTicker("Time To Ride")
                // Set Title
                .setContentTitle("Time Reach To Ride")
                // Set Text
                .setContentText("Your time to ride has be reach...")
                // Add an Action Button below Notification
                /*.addAction(R.drawable.pickup_icon, "Start Ride", pIntent)*/
                // Set PendingIntent into Notification
                .setContentIntent(pIntent)
                // Dismiss Notification
                .setAutoCancel(true);
                //.setSound(Uri.parse("android.resource://"+context.getPackageName()+"/"+R.raw.notify));



        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        builder.build().flags |= Notification.FLAG_AUTO_CANCEL;
        // Build Notification with Notification Manager
        notificationmanager.notify(0, builder.build());

    }

    public int getLastId(Context context) {
        int _id = 0;
        MyDatabase myDatabase = new MyDatabase(context);
        SQLiteDatabase db = myDatabase.getReadableDatabase();
        Cursor cursor = db.query(myDatabase.NAME_OF_TABLE,  new String[] {myDatabase.Col_1}, null, null, null, null, null);

        if (cursor.moveToLast()) {
            _id = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return _id;
    }

    public LatLng convertStringToLatlng(String latlngString){


        String abcdedf =   "lat/lng: (28.553212,77.174482)";
        String[] latlong =  abcdedf.split(",");
        String latitude = (latlong[0]);
        String longitude = (latlong[1]);

        Log.e("TAG", "latitude: " + latitude);
        Log.e("TAG", "logitue: " + longitude);

        String[] mLat = latitude.split("\\(");
        String latLeft = (mLat[0]);
        String LatRight = (mLat[1]);
        Log.e("TAG", "latleft: " + latLeft);
        Log.e("TAG", "latright: " + LatRight);

        String[] mLng = longitude.split("\\)");
        String lngleft = (mLng[0]);
        //String lngright = (mLng[1]);
        Log.e("TAG", "lngleft: " + lngleft);

        double myLatitude = Double.parseDouble(LatRight);
        double myLongitude = Double.parseDouble(lngleft);
        // Log.e("TAG", "lngright: " + lngright);

        LatLng latLng = new LatLng(myLatitude, myLongitude);

        Log.e("TAG", "final Latlng: " + latLng);

        return latLng;

    }

}