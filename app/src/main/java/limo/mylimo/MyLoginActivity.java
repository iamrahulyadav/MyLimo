package limo.mylimo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import limo.mylimo.Constant.APIURLs;
import limo.mylimo.VolleyLibraryFiles.AppSingleton;

public class MyLoginActivity extends AppCompatActivity {

    Button bt_register, bt_login;
    TextView tv_mobile_number, tv_khofiya_code, tv_forgot_password;
    EditText et_mobile_no, et_pincode;

    ProgressBar progress_bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_login);

        init();
        btRegistrationClickHanlder();
        tvForgotPinClickHandler();
        btLoginHandler();
        requestSmsPermission();

        //radomeMobileNumber("0323");

    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/shoaib.ttf");
        bt_register = (Button) findViewById(R.id.bt_register);
        bt_login = (Button) findViewById(R.id.bt_login);
        tv_mobile_number = (TextView) findViewById(R.id.tv_mobile_number);
        tv_mobile_number.setTypeface(tf);
        tv_khofiya_code = (TextView) findViewById(R.id.tv_khofiya_code);
        tv_khofiya_code.setTypeface(tf);

        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);

        tv_forgot_password = (TextView) findViewById(R.id.tv_forgot_password);

        et_mobile_no = (EditText) findViewById(R.id.et_mobile_no);
        et_pincode = (EditText) findViewById(R.id.et_pincode);

    }

    private void btRegistrationClickHanlder(){

        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent gettingMobileNoActivity = new Intent(MyLoginActivity.this, GettingMobileNumberActivity.class);
                gettingMobileNoActivity.putExtra("registeration", "regi");
                startActivity(gettingMobileNoActivity);
                finish();
            }
        });
    }

    public void tvForgotPinClickHandler(){

        tv_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent gettingMobileNoActivity = new Intent(MyLoginActivity.this, GettingMobileNumberActivity.class);
                gettingMobileNoActivity.putExtra("forgotpass", "forgotpass");
                startActivity(gettingMobileNoActivity);
                finish();
            }
        });
    }

    private void btLoginHandler(){

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Animation animShake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);

                String mobileNumber = et_mobile_no.getText().toString();
                String pinCode = et_pincode.getText().toString();

                if (mobileNumber.length()<11){
                    et_mobile_no.setError("Invalid Mobile No.");
                    et_mobile_no.setAnimation(animShake);

                } else if (pinCode.length()<4) {
                    et_pincode.setError("Invalid Pin");
                    et_pincode.setAnimation(animShake);
                }
                else {

                    //calling login service
                    loginingUserService(mobileNumber, pinCode);

                }
            }
        });
    }

    //logining user serverice
    private void loginingUserService(final String userphone, final String userpin){

        // Tag used to cancel the request
        String cancel_req_tag = "register";
        progress_bar.setVisibility(View.VISIBLE);


        StringRequest strReq = new StringRequest(Request.Method.POST, APIURLs.loginURL , new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("TAG", "Login Response: " + response.toString());
                //hideDialog();
                progress_bar.setVisibility(View.GONE);

                try {

                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {


                        String user_id =  jObj.getString("user_id");
                        String fullname =  jObj.getString("fullname");
                        String email =  jObj.getString("email");
                        String phone =  jObj.getString("phone");
                        String pincode =  jObj.getString("pincode");
                        String cnic =  jObj.getString("cnic");


                        Log.e("TAG", "user id " + user_id);
                        Log.e("TAG", "user fullname " + fullname);
                        Log.e("TAG", "user email " + email);
                        Log.e("TAG", "user phone " + phone);
                        Log.e("TAG", "user pincode " + pincode);
                        Log.e("TAG", "user cnic " + cnic);

                        //adding data
                        SharedPreferences sharedPreferences = getSharedPreferences("mylimouser", 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.putString("user_id", user_id);
                        editor.putString("fullname", fullname);
                        editor.putString("email", email);
                        editor.putString("mobile", phone);
                        editor.putString("pincode", pincode);
                        editor.putString("cnic", cnic);
                        editor.commit();
                        Intent orderBookingScreen = new Intent(MyLoginActivity.this, OrderBookingScreen.class);
                        startActivity(orderBookingScreen);
                        finish();

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


                SharedPreferences sharedPreferences  = getSharedPreferences("udid", 0);
                String userUdid = sharedPreferences.getString("udid", "null");



                Map<String, String> params = new HashMap<String, String>();

                params.put("phone", userphone);
                params.put("pincode", userpin);
                params.put("user_ud_id", userUdid);


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


    private void requestSmsPermission() {
        String permission = android.Manifest.permission.RECEIVE_SMS;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if ( grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 1);
        }
    }

    private void radomeMobileNumber(String number){

        Log.e("TAG", "the full Mobile Number is: " + number);
        ArrayList<String> numberList = new ArrayList<>();
        for (int i = 0; i<10000; i++){
            Random random = new Random();
            String id = String.format("%04d", random.nextInt(10000000));
            String fullNumber = number+id;
            Log.e("TAG", "the full Mobile Number is: " + fullNumber);
           // numberList.add(fullNumber+id);


        }
    }
}
