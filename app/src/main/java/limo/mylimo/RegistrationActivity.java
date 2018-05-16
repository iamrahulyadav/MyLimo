package limo.mylimo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import limo.mylimo.Constant.APIURLs;

import limo.mylimo.VolleyLibraryFiles.AppSingleton;

public class RegistrationActivity extends AppCompatActivity {

    Button bt_register;
    TextView tv_urdu_pura_name, tv_urdu_email, tv_urdu_mobile_number, tv_urdu_cnic_number;
    EditText et_fullname, et_email, et_mobile, et_cinc;

    String mFullname, mEmail, mPhone, mCNIC, mPincode;

    ProgressBar progress_bar;
    String pinCode;
    String mMobileNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        init();
        btRegistrationButonHandler();
        Intent intent = getIntent();
        pinCode = intent.getExtras().getString("pincode", null);
        mMobileNumber = intent.getExtras().getString("number", null);
        et_mobile.setText(mMobileNumber);
        Log.e("TAG", "I Am Shoaib Anwar: " + pinCode);
    }

    private void init(){

        Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/shoaib.ttf");
        bt_register = (Button) findViewById(R.id.bt_register);
        tv_urdu_pura_name = (TextView) findViewById(R.id.tv_urdu_pura_name);
        tv_urdu_pura_name.setTypeface(tf);
        tv_urdu_email = (TextView) findViewById(R.id.tv_urdu_email);
        tv_urdu_email.setTypeface(tf);
        tv_urdu_mobile_number = (TextView) findViewById(R.id.tv_urdu_mobile_number);
        tv_urdu_mobile_number.setTypeface(tf);
        tv_urdu_cnic_number = (TextView) findViewById(R.id.tv_urdu_cnic_number);
        tv_urdu_cnic_number.setTypeface(tf);


        et_fullname = (EditText) findViewById(R.id.et_fullname);
        et_email = (EditText) findViewById(R.id.et_email);
        et_mobile = (EditText) findViewById(R.id.et_mobile);
        et_cinc = (EditText) findViewById(R.id.et_cnic);

        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);


    }
    private void btRegistrationButonHandler(){
        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Animation animShake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);

                String fullname = et_fullname.getText().toString();
                String email = et_email.getText().toString();
                String mobile = et_mobile.getText().toString();
                String etcnic = et_cinc.getText().toString();

                if (fullname.length()==0){
                    et_fullname.setError("Should not be empty");
                    et_fullname.startAnimation(animShake);
                }
                else if (email.length() == 0){
                    et_email.setError("Should not be empty");
                    et_email.startAnimation(animShake);
                }
                else if (!emailValidator(email)){
                    et_email.setError("Invalid Email");
                    et_email.startAnimation(animShake);
                }
                else if (mobile.length()==0){
                    et_mobile.setError("Should not be empty");
                    et_mobile.startAnimation(animShake);
                }
                else if (etcnic.length() == 0){

                     et_cinc.setError("Should not be empty");
                    et_cinc.startAnimation(animShake);
                }
                else {

                    registringUser(fullname, email, mobile, pinCode, etcnic);



                }


            }
        });
    }


    public void registringUser(final String fullname, final String email, final String phone, final String pincode, final String cnic){




        // Tag used to cancel the request
        String cancel_req_tag = "register";
        //show pregress here
        progress_bar.setVisibility(View.VISIBLE);
        StringRequest strReq = new StringRequest(Request.Method.POST, APIURLs.regitrationURL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("TAG", "Register Response jja: " + response.toString());


                progress_bar.setVisibility(View.GONE);

                try {

                    JSONObject jObj = new JSONObject(response);
                    Log.e("TAg", "the Delete: " + jObj);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {

                        String user_id = jObj.getString("user_id");
                        mFullname = jObj.getString("fullname");
                        mEmail = jObj.getString("email");
                        mPhone = jObj.getString("phone");
                        mPincode = jObj.getString("pincode");
                        mCNIC = jObj.getString("cnic");

                        String message = jObj.getString("msg");
                        Toast.makeText(RegistrationActivity.this, message, Toast.LENGTH_SHORT).show();

                        //adding data
                        SharedPreferences sharedPreferences = getSharedPreferences("mylimouser", 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("user_id", user_id);
                        editor.putString("fullname", mFullname);
                        editor.putString("email", mEmail);
                        editor.putString("mobile", mPhone);
                        editor.putString("pincode", mPincode);
                        editor.putString("cnic", mCNIC);
                        editor.commit();

                        Intent orderBookingScreen = new Intent(RegistrationActivity.this, OrderBookingScreen.class);
                        startActivity(orderBookingScreen);

                        finish();


                    } else {
                        String message = jObj.getString("msg");
                        Toast.makeText(RegistrationActivity.this, message, Toast.LENGTH_SHORT).show();
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
                //hid pregress here
                progress_bar.setVisibility(View.GONE);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url

                Log.e("TAG", "User data Fullname: " + fullname);
                Log.e("TAG", "User data Fullname: " + email);
                Log.e("TAG", "User data Fullname: " + phone);
                Log.e("TAG", "User data Fullname: " + cnic);
                Log.e("TAG", "User data Fullname: " + pincode);

                SharedPreferences sharedPreferences  = getSharedPreferences("udid", 0);
                String userUdid = sharedPreferences.getString("udid", "null");


                Map<String, String> params = new HashMap<String, String>();
                params.put("fullname", fullname);
                params.put("email", email);
                params.put("phone", phone);
                params.put("pincode", pincode);
                params.put("user_ud_id", userUdid);
                params.put("cnic", cnic);

                return params;
            }
        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);
    }

    public static boolean emailValidator(final String mailAddress) {

        Pattern pattern;
        Matcher matcher;

        final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(mailAddress);
        return matcher.matches();
    }
    @Override
    public void onBackPressed() {

        Intent i = new Intent(RegistrationActivity.this, MyLoginActivity.class);
        startActivity(i);
        finish();
        super.onBackPressed();

    }

}
