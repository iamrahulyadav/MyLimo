package limo.mylimo;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import limo.mylimo.Adapters.HistoryDataAdapter;
import limo.mylimo.Constant.APIURLs;
import limo.mylimo.SetterGetter.HistoryData;
import limo.mylimo.VolleyLibraryFiles.AppSingleton;


public class MyHistoryScreen extends AppCompatActivity {

    RecyclerView rv_order_history;
    LinearLayoutManager linearLayoutManager;

    private ProgressBar progress_bar;
    private JSONObject jsonObject;
    private JSONArray jsonArray;

    ArrayList<HistoryData> historyData;
    HistoryDataAdapter historyDataAdapter;

    TextView tv_no_history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_history_screen);


        init();

        SharedPreferences sharedPreferences = getSharedPreferences("mylimouser", 0);
        String userId = sharedPreferences.getString("user_id", null);

        gettingMyOrderHistoryFromServer(userId);

    }//end of onCreate

    private void init(){
        rv_order_history = (RecyclerView) findViewById(R.id.rv_order_history);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext() , LinearLayoutManager.VERTICAL, false);
        rv_order_history.setLayoutManager(linearLayoutManager);

        tv_no_history = (TextView) findViewById(R.id.tv_no_history);

        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        historyData = new ArrayList<>();
    }



    //order booking server
    private void gettingMyOrderHistoryFromServer(final String user_id){


        // Tag used to cancel the request
        String cancel_req_tag = "register";
        progress_bar.setVisibility(View.VISIBLE);

        StringRequest strReq = new StringRequest(Request.Method.POST, APIURLs.userHistory , new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("TAG", "hisptory response: " + response.toString());
                //hideDialog();
                progress_bar.setVisibility(View.GONE);

                try {

                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {


                        jsonObject = new JSONObject(response);
                        jsonArray = jsonObject.getJSONArray("order_detail");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject finalobject = jsonArray.getJSONObject(i);

                           String order_id = finalobject.getString("order_id");
                           String pickup_lat = finalobject.getString("pickup_lat");
                            String pickup_lng = finalobject.getString("pickup_lng");
                            String pickup_detail = finalobject.getString("pickup_detail");
                            String destination_lat = finalobject.getString("destination_lat");
                            String destination_lng = finalobject.getString("destination_lng");
                            String destination_detail = finalobject.getString("destination_detail");
                            String pickup_city = finalobject.getString("pickup_city");
                            String destination_city = finalobject.getString("destination_city");
                            String car_type = finalobject.getString("car_type");
                            String seats = finalobject.getString("seats");
                            String date = finalobject.getString("date");
                            String time = finalobject.getString("time");
                            String fk_user_id = finalobject.getString("fk_user_id");
                            String order_status = finalobject.getString("order_status");
                            String price = finalobject.getString("price");
                            String user_id = finalobject.getString("user_id");
                            String fullname = finalobject.getString("fullname");
                            String email = finalobject.getString("email");
                            String phone = finalobject.getString("phone");
                            String pincode = finalobject.getString("pincode");
                            String cnic = finalobject.getString("cnic");
                            String day = finalobject.getString("day");

                            day = day.substring(0, 3);

                            Log.e("TAG", "the user order id " + i + " " + order_id);

                            historyData.add(new HistoryData(order_id, pickup_lat, pickup_lng, pickup_detail, destination_lat, destination_lng, destination_detail,
                                    pickup_city, destination_city, car_type, seats, date, day+" "+time, price, user_id, fullname, email, phone, pincode, pincode));
                        }
                        //reversing array list to get recent on top
                        Collections.reverse(historyData);
                        historyDataAdapter = new HistoryDataAdapter(MyHistoryScreen.this, historyData);
                        rv_order_history.setAdapter(historyDataAdapter);

                        Log.e("TAG", "the size of history record: " + historyData.size());


                    } else {

                        String errorMsg = jObj.getString("error_message");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                        tv_no_history.setVisibility(View.VISIBLE);
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

                params.put("user_id", user_id);

                return params;
            }
        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);

    }//end of order service


}
