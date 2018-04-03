package limo.mylimo.Notifications;


/**
 * Created by Shoaib Anwar on 14-Feb-18.
 */

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService{

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.e(TAG, "Refreshed token: " + refreshedToken);
        SharedPreferences sharedPreferences  = getSharedPreferences("udid", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("udid", refreshedToken);
        editor.commit();

    }

    private void sendRegistrationToServer(String token) {
        //You can implement this method to store the token on your server
        //Not required for current project
    }

}
