package limo.mylimo;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hsalf.smilerating.BaseRating;
import com.hsalf.smilerating.SmileRating;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import limo.mylimo.Constant.APIURLs;
import limo.mylimo.VolleyLibraryFiles.AppSingleton;

public class Feedback extends AppCompatActivity {

    private String TAG = "FeedbackActivity";
    private SmileRating mSmileRating;
    private int level = 0;
    private EditText et_description;
    private Button bt_submit;

    ProgressBar progress_bar;

    String mOrderID = "" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);


        init();
        onRatingSelectListener();
        btSubmitHandler();

    }
    private void init(){

        Intent intent = getIntent();
        mOrderID = intent.getExtras().getString("orderid", "no");
        Log.e("TAG", "The order id is: " + mOrderID);

        et_description = (EditText) findViewById(R.id.et_description);
        mSmileRating = (SmileRating) findViewById(R.id.smile_rating);
        bt_submit = (Button) findViewById(R.id.bt_submit);

        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);

    }

    private void onRatingSelectListener(){
        mSmileRating.setOnSmileySelectionListener(new SmileRating.OnSmileySelectionListener() {
            @Override
            public void onSmileySelected(@BaseRating.Smiley int smiley, boolean reselected) {
                // reselected is false when user selects different smiley that previously selected one
                // true when the same smiley is selected.
                // Except if it first time, then the value will be false.
                switch (smiley) {
                    case SmileRating.BAD:
                        Log.e(TAG, "Bad");

                        break;
                    case SmileRating.GOOD:
                        Log.i(TAG, "Good");

                        break;
                    case SmileRating.GREAT:
                        Log.i(TAG, "Great");

                        break;
                    case SmileRating.OKAY:
                        Log.i(TAG, "Okay");

                        break;
                    case SmileRating.TERRIBLE:
                        Log.i(TAG, "Terrible");

                        break;
                }
            }
        });
    }

    private void btSubmitHandler(){
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                level = mSmileRating.getRating(); // level is from 1 to 5
                Log.e(TAG, "The level of selected rating is: " + level);
                if (level!=0) {
                    String smilName = mSmileRating.getSmileName(level);
                    Log.e(TAG, "Selected Smile Name is: " + smilName);
                }


                String desctiption = et_description.getText().toString();

                if (desctiption.length()==0){

                    Toast.makeText(Feedback.this, "Please Write some text in description", Toast.LENGTH_SHORT).show();
                }
                else if (level == 0){
                    Toast.makeText(Feedback.this, "Please select a rating face", Toast.LENGTH_SHORT).show();
                }
                else {

                    String description = et_description.getText().toString();
                    level = mSmileRating.getRating(); // level is from 1 to 5
                    String smilName = mSmileRating.getSmileName(level-1);

                    SharedPreferences sharedPreferences = getSharedPreferences("mylimouser", 0);
                    String userId = sharedPreferences.getString("user_id", null);


                    Log.e(TAG, "The level of selected rating description: " + description);
                    Log.e(TAG, "The level of selected rating is: " + level);
                    Log.e(TAG, "The level of selected rating name: " + smilName);
                    Log.e(TAG, "The level of selected rating userId: " + userId);

                    //calling api
                    sendingFeedbackToUser(userId, description, String.valueOf(level), mOrderID);

                }
            }
        });
    }

    private void sendingFeedbackToUser(final String userId, final String description, final String ratinglLevel, final String ride_id){

            // Tag used to cancel the request
            String cancel_req_tag = "register";
            progress_bar.setVisibility(View.VISIBLE);


            StringRequest strReq = new StringRequest(Request.Method.POST, APIURLs.FEEDBACK , new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.e("TAG", "Login Response: " + response.toString());
                    //hideDialog();
                    progress_bar.setVisibility(View.GONE);

                    try {

                        JSONObject jObj = new JSONObject(response);
                        boolean error = jObj.getBoolean("error");

                        if (!error) {


                            String errorMsg = jObj.getString("msg");

                            SharedPreferences sharedPreferencesForFeedback = getSharedPreferences("givefeedback", 0);
                            SharedPreferences.Editor editor = sharedPreferencesForFeedback.edit();
                            editor.clear();
                            editor.commit();

                            //calling dialog for exit screen
                            exitScreenAlert();

                        } else {

                            String errorMsg = jObj.getString("msg");


                            Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("TAG", "Registration Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_LONG).show();
                    //hideDialog();
                    progress_bar.setVisibility(View.GONE);
                }
            }) {



                @Override
                protected Map<String, String> getParams() {
                    // Posting params to register url


                    Map<String, String> params = new HashMap<String, String>();

                    Log.e("TAG", "Ride id is here in api user id: " +userId);
                    Log.e("TAG", "Ride id is here in descrioption: " +description);
                    Log.e("TAG", "Ride id is here in rating level: " +ratinglLevel);
                    Log.e("TAG", "Ride id is here in api: " +ride_id);




                    params.put("user_id", userId);
                    params.put("descrition", description);
                    params.put("rating", ratinglLevel);
                    params.put("ride_id", ride_id);


                    return params;
                }
            };

            strReq.setRetryPolicy(new DefaultRetryPolicy(
                    20000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            // Adding request to request queue
            AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);
        }//end of registration service


    private void exitScreenAlert() {

        final Dialog exitDialog = new Dialog(Feedback.this);
        exitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        exitDialog.setContentView(R.layout.custom_dialog_exit);
        Button btExit = (Button) exitDialog.findViewById(R.id.bt_exit);
        TextView tv_dialog_title = (TextView) exitDialog.findViewById(R.id.tv_dialog_title);
        TextView tv_description = (TextView) exitDialog.findViewById(R.id.tv_description);

        btExit.setText("Exit");
        SharedPreferences sharedPreferences  = getSharedPreferences("mylimouser", 0);
        String userName = sharedPreferences.getString("fullname", null);
        tv_dialog_title.setText("Submitted Successfully");
        tv_description.setText("Thank You " + userName + " Your feedback has very importance for us to provide you quality of service");

        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();

        //btOk click handler
        btExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                exitDialog.dismiss();

                Intent i = new Intent(Feedback.this, OrderBookingScreen.class);
                startActivity(i);
                finish();
            }
        });

        exitDialog.setCancelable(false);
        exitDialog.show();

    }

}
