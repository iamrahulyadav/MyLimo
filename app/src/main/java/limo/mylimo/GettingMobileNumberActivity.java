package limo.mylimo;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import limo.mylimo.Constant.APIURLs;
import limo.mylimo.VolleyLibraryFiles.AppSingleton;

public class GettingMobileNumberActivity extends AppCompatActivity {

    Button bt_submit;
    TextView tv_mobile_number_darj_kijeye, tv_mobile_number;
    EditText et_mobile_no;
    boolean trackingUser = false; //false for new registration

    ProgressBar progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.getting_mobilenumber_activity);

        init();
        btSubmitClickHandler();

        Intent intent = getIntent();
        String forgot =  intent.getExtras().getString("forgotpass", "1"); // 1 is default value
        String newregis =  intent.getExtras().getString("registeration", "2"); // 2 is default value

        if (!forgot.equals("1")){

            trackingUser = true;
            Log.e("TAG", "User Pressed Forgot Password");
        }



    }//end of onCreate

    private void init(){

        Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/shoaib.ttf");

        et_mobile_no = (EditText) findViewById(R.id.et_mobile_no);
        bt_submit = (Button) findViewById(R.id.bt_submit);
        tv_mobile_number_darj_kijeye = (TextView) findViewById(R.id.tv_mobile_number_darj_kijeye);
        tv_mobile_number_darj_kijeye.setTypeface(tf);
        tv_mobile_number = (TextView) findViewById(R.id.tv_mobile_number);
        tv_mobile_number.setTypeface(tf);

        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);

    }

    private void btSubmitClickHandler(){

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final Animation animShake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                String mobileNumber = et_mobile_no.getText().toString();
                if (mobileNumber.length()<11){
                    et_mobile_no.setError("Invalid Mobile No.");
                    et_mobile_no.setAnimation(animShake);
                }else {

                    String URLFNEWUSER = APIURLs.SMS_REQUEST;
                    String URLFORGOTPASSWORD = APIURLs.FORGOT_PASSWORD;
                    if (!trackingUser) {
                        requestingSMS(URLFNEWUSER, mobileNumber);
                    }
                    if (trackingUser){
                        requestingSMS(URLFORGOTPASSWORD, mobileNumber);
                    }

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(GettingMobileNumberActivity.this, MyLoginActivity.class);
        startActivity(i);
        finish();
        super.onBackPressed();

    }


    private void requestingSMS(final String URL, final String mobileNumber) {
        // Tag used to cancel the request
        String cancel_req_tag = "register";
        progress_bar.setVisibility(View.VISIBLE);

        StringRequest strReq = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("TAG", "Session id response: " + response.toString());
                //hideDialog();
                progress_bar.setVisibility(View.GONE);

                try {

                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {


                        String phone =  jObj.getString("phone");
                        String pinCode = jObj.getString("pin");


                        Intent pinCodeTimerActivity = new Intent(GettingMobileNumberActivity.this, PinCodeTimerActivity.class);
                        if (trackingUser){
                            pinCodeTimerActivity.putExtra("forgotpass", "forgotpass");
                            pinCodeTimerActivity.putExtra("number", phone);
                            pinCodeTimerActivity.putExtra("pincode", pinCode);
                        }
                        {
                            pinCodeTimerActivity.putExtra("registeration", "regi");
                            pinCodeTimerActivity.putExtra("number", phone);
                            pinCodeTimerActivity.putExtra("pincode", pinCode);
                        }

                        startActivity(pinCodeTimerActivity);
                        finish();

                    }
                    else {

                        String erroMessage = jObj.getString("msg");
                        et_mobile_no.setError(erroMessage);
                        Toast.makeText(GettingMobileNumberActivity.this, erroMessage, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }



                // ParseXML(response.toString());


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
                params.put("phone", mobileNumber);
                return params;
            }
        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);

    }

}
