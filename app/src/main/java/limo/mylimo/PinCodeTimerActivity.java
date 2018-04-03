package limo.mylimo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

import java.util.HashMap;
import java.util.Map;

import limo.mylimo.Constant.APIURLs;
import limo.mylimo.VolleyLibraryFiles.AppSingleton;


public class PinCodeTimerActivity extends AppCompatActivity{

    TextView tv_timer, tv_urdu_tasdeeq;
    EditText et_firt_digit, et_second_digit, et_thirld_digit, et_fourth_digit;
    private int timinSecoding = 61;
    private int timeScondReverse = 0;
    Handler mHandler;
    CircularSeekBar seekbar;


    String firstNumber = null;
    String secondNumber = null;
    String thirldNumber = null;
    String foruthNumber = null;

    ProgressBar progress_bar;

    private String myMobileNumber = null;

    XmlPullParserFactory xmlPullParserFactory;
    XmlPullParser parser;

    String genratedPinCode;

    SmsListener smsListener;

    boolean trackingUser = false; //false for new registration

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_code_timer);

        init();
        startTiming();
        focusNextPinText();

        Intent intent = getIntent();
        String forgot =  intent.getExtras().getString("forgotpass", "1"); // 1 is default value
        String newregis =  intent.getExtras().getString("registeration", "2"); // 2 is default value
        myMobileNumber = intent.getExtras().getString("number", null);
        genratedPinCode = intent.getExtras().getString("pincode", null);


        if (!forgot.equals("1")){

            trackingUser = true;
            Log.e("TAG", "User Pressed Forgot Password");
        }

        seekbar.setMax(60);
      //  Random rand = new Random();
        //String code = String.format("%04d", rand.nextInt(10000));
        //Log.e("TAG", "the ramdom 4 digit number is: " + code);

        /*myMobileNumber = myMobileNumber.substring(1);
        myMobileNumber = "92"+myMobileNumber;
        Log.e("TAG", "The mobile number is: " + myMobileNumber);
        genratedPinCode = code;

        gettingSessionIdForSMS(myMobileNumber, code);

*/


       // requestingSMS(myMobileNumber);

    }//end of on creatre



    private void init(){
        Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/shoaib.ttf");
        tv_timer = (TextView) findViewById(R.id.tv_timer);
        tv_urdu_tasdeeq = (TextView) findViewById(R.id.tv_urdu_tasdeeq);
        tv_urdu_tasdeeq.setTypeface(tf);
        et_firt_digit = (EditText) findViewById(R.id.et_firt_digit);
        et_second_digit = (EditText) findViewById(R.id.et_second_digit);
        et_thirld_digit = (EditText) findViewById(R.id.et_thirld_digit);
        et_fourth_digit = (EditText) findViewById(R.id.et_fourth_digit);
        seekbar = (CircularSeekBar) findViewById(R.id.circularSeekBar1);

        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);

    }


    public void startTiming(){

        mHandler = new Handler();
        useHandler();
    }
    //Thread for starting mainActivity
    private Runnable mRunnableStartMainActivity = new Runnable() {
        @Override
        public void run() {
            Log.d("Handler", " Calls");
            timinSecoding--;
            timeScondReverse++;
            mHandler = new Handler();
            mHandler.postDelayed(this, 1000);
            tv_timer.setText(""+timinSecoding);
           /* CircleSeekBarListener circleSeekBarListener  = new CircleSeekBarListener();
            circleSeekBarListener.onProgressChanged(seekbar, timinSecoding, true);*/

            seekbar.getProgress();

            seekbar.setProgress(timeScondReverse);
            seekbar.setOnSeekBarChangeListener(new CircleSeekBarListener());

            if (timinSecoding<10){
                tv_timer.setText("0"+timinSecoding);
            }
            if (timinSecoding == 0){
                mHandler.removeCallbacks(mRunnableStartMainActivity);
                Toast.makeText(PinCodeTimerActivity.this, "Session Expired Please Try Again", Toast.LENGTH_SHORT).show();
            }

        }
    };


    //handler for the starign activity
    Handler newHandler;
    public void useHandler(){

        newHandler = new Handler();
        newHandler.postDelayed(mRunnableStartMainActivity, 1000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnableStartMainActivity);
    }


    private void focusNextPinText(){
        firstTextChangeListener();
        seconTextChange();
        thirldTextChange();
        fourthTextChangeListener();
    }

    private void firstTextChangeListener(){
        et_firt_digit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (et_firt_digit.getText().length()==1){
                    View next = et_firt_digit.focusSearch(View.FOCUS_RIGHT); // or FOCUS_FORWARD
                    if (next != null)
                        next.requestFocus();

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (et_firt_digit.getText().length()!=0
                        && et_second_digit.getText().length()!=0
                        && et_thirld_digit.getText().length()!=0
                        && et_fourth_digit.getText().length()!=0)
                {

                    firstNumber = et_firt_digit.getText().toString();
                    secondNumber = et_second_digit.getText().toString();
                    thirldNumber = et_thirld_digit.getText().toString();
                    foruthNumber = et_fourth_digit.getText().toString();

                    String pincode = firstNumber+secondNumber+thirldNumber+foruthNumber;

                    if (pincode.equals(genratedPinCode)) {
                        registrationScreen(pincode);
                    }
                    else {
                        Toast.makeText(PinCodeTimerActivity.this, "Incorrect Pin code", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void seconTextChange(){
        et_second_digit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (et_second_digit.getText().length()==1){
                    View next = et_second_digit.focusSearch(View.FOCUS_RIGHT); // or FOCUS_FORWARD
                    if (next != null)
                        next.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (et_firt_digit.getText().length()!=0
                        && et_second_digit.getText().length()!=0
                        && et_thirld_digit.getText().length()!=0
                        && et_fourth_digit.getText().length()!=0)
                {

                    firstNumber = et_firt_digit.getText().toString();
                    secondNumber = et_second_digit.getText().toString();
                    thirldNumber = et_thirld_digit.getText().toString();
                    foruthNumber = et_fourth_digit.getText().toString();
                    String pincode = firstNumber+secondNumber+thirldNumber+foruthNumber;


                    if (pincode.equals(genratedPinCode)) {
                        registrationScreen(pincode);
                    }
                    else {
                        Toast.makeText(PinCodeTimerActivity.this, "Incorrect Pin code", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    private void thirldTextChange(){
        et_thirld_digit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (et_thirld_digit.getText().length()==1){
                    View next = et_thirld_digit.focusSearch(View.FOCUS_RIGHT); // or FOCUS_FORWARD
                    if (next != null)
                        next.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (et_firt_digit.getText().length()!=0
                        && et_second_digit.getText().length()!=0
                        && et_thirld_digit.getText().length()!=0
                        && et_fourth_digit.getText().length()!=0)
                {

                    firstNumber = et_firt_digit.getText().toString();
                    secondNumber = et_second_digit.getText().toString();
                    thirldNumber = et_thirld_digit.getText().toString();
                    foruthNumber = et_fourth_digit.getText().toString();
                    String pincode = firstNumber+secondNumber+thirldNumber+foruthNumber;

                    if (pincode.equals(genratedPinCode)) {
                        registrationScreen(pincode);
                    }
                    else {
                        Toast.makeText(PinCodeTimerActivity.this, "Incorrect Pin code", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void fourthTextChangeListener(){

        et_fourth_digit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (et_fourth_digit.getText().length()==1) {

                    String firstNumber = et_firt_digit.getText().toString();
                    String secondNumber = et_second_digit.getText().toString();
                    String thirldNumber = et_thirld_digit.getText().toString();
                    String foruthNumber = et_fourth_digit.getText().toString();

                    if (firstNumber.length()!=0
                            && secondNumber.length()!=0
                            && thirldNumber.length()!=0
                            && foruthNumber.length()!=0)
                    {
                        String finalPincode = firstNumber + secondNumber + thirldNumber + foruthNumber;
                    Log.e("TAG", "The final number of pin code: " + finalPincode);
                        mHandler.removeCallbacks(mRunnableStartMainActivity);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (et_firt_digit.getText().length()!=0
                        && et_second_digit.getText().length()!=0
                        && et_thirld_digit.getText().length()!=0
                        && et_fourth_digit.getText().length()!=0)
                {

                     firstNumber = et_firt_digit.getText().toString();
                     secondNumber = et_second_digit.getText().toString();
                     thirldNumber = et_thirld_digit.getText().toString();
                     foruthNumber = et_fourth_digit.getText().toString();

                    String pincode = firstNumber+secondNumber+thirldNumber+foruthNumber;

                    if (pincode.equals(genratedPinCode)) {
                        registrationScreen(pincode);
                    }
                    else {
                        Toast.makeText(PinCodeTimerActivity.this, "Incorrect Pin code", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void registrationScreen(final String pinCode) {


        if (trackingUser) {

            Intent registrationActivity = new Intent(PinCodeTimerActivity.this, MyLoginActivity.class);
            startActivity(registrationActivity);
            finish();

        } else{

            numberVerifiyDelay();
    }

    }


    private void gettingSessionIdForSMS(final String mobilenumber, final String text){

        // Tag used to cancel the request
        String cancel_req_tag = "register";
        progress_bar.setVisibility(View.VISIBLE);

        StringRequest strReq = new StringRequest(Request.Method.GET, APIURLs.gettingSeesionIdForSMS , new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("TAG", "Session id response: " + response.toString());
                //hideDialog();
                progress_bar.setVisibility(View.GONE);

                ParseXML(response.toString(), mobilenumber, text);


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


    public void ParseXML(String xmlString, final String mobilenumber, final String text){

        try {

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xmlString));
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT){

                if(eventType== XmlPullParser.START_TAG){

                    String name = parser.getName();
                    Log.e("TAG","The name of prase tag is: " + name);
                    if(name.equals("data")){

                        if(parser.next() == XmlPullParser.TEXT) {
                            String SessionId = parser.getText();
                            Log.e("TAG","the session id is: " + SessionId);

                            sendingSMSService(mobilenumber, SessionId, text);
                        }

                    }

                }else if(eventType== XmlPullParser.END_TAG){


                }
                eventType = parser.next();

            }


        }catch (Exception e){
            Log.d("TAG","Error in ParseXML()",e);
        }

    }

    private void sendingSMSService(final String mobilenumber, final String sessionID, final String text){

        // Tag used to cancel the request
        String cancel_req_tag = "register";
        progress_bar.setVisibility(View.VISIBLE);

        String final_url = "https://telenorcsms.com.pk:27677/corporate_sms2/api/sendsms.jsp?session_id="+sessionID+"&to="+mobilenumber+"&text="+text+"&mask=My%20Limo";


        StringRequest strReq = new StringRequest(Request.Method.GET, final_url , new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("TAG", "Session id response: " + response.toString());
                //hideDialog();
                progress_bar.setVisibility(View.GONE);

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


    public class SmsListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub

            Log.e("TAG", "the intnet receiver: " + intent);
            String pincode = intent.getStringExtra("pincode");
            Log.e("TAG", "the sms pin code is: " + pincode);
            char firstChar = pincode.charAt(0);
            char secondChar = pincode.charAt(1);
            char thirldChar = pincode.charAt(2);
            char fourthChar = pincode.charAt(3);

            String frist = String.valueOf(firstChar);
            String second = String.valueOf(secondChar);
            String thirld = String.valueOf(thirldChar);
            String fourth = String.valueOf(fourthChar);

            et_firt_digit.setText(frist);
            et_second_digit.setText(second);
            et_thirld_digit.setText(thirld);
            et_fourth_digit.setText(fourth);


        }

    }//end of broadcast receiver

    @Override
    public void onStart() {

        //Register BroadcastReceiver
        //to receive event from our service
        smsListener = new SmsListener();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("pinreceived");
        registerReceiver(smsListener, intentFilter);

        super.onStart();
    }

    @Override
    public void onStop() {
        unregisterReceiver(smsListener);

        super.onStop();
    }

    private void requestingSMS(final String mobileNumber) {
        // Tag used to cancel the request
        String cancel_req_tag = "register";
        progress_bar.setVisibility(View.VISIBLE);

        StringRequest strReq = new StringRequest(Request.Method.POST, APIURLs.SMS_REQUEST, new Response.Listener<String>() {

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

                        genratedPinCode = pinCode;

                        Log.e("tag", "the pincode is: " + genratedPinCode);


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

    private void numberVerifiyDelay(){

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent registrationActivity = new Intent(PinCodeTimerActivity.this, RegistrationActivity.class);
                registrationActivity.putExtra("pincode", genratedPinCode);
                registrationActivity.putExtra("number", myMobileNumber);
                startActivity(registrationActivity);
                finish();

                //Do something after 100ms
                Toast.makeText(getApplicationContext(), "Number sucessfully verified", Toast.LENGTH_SHORT).show();
            }
        }, 200);
    }

    @Override
    public void onBackPressed() {

            Intent i = new Intent(PinCodeTimerActivity.this, MyLoginActivity.class);
            startActivity(i);
            finish();
            super.onBackPressed();


    }
}
