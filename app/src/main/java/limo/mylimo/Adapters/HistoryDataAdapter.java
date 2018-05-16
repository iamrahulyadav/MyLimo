package limo.mylimo.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
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

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import limo.mylimo.Constant.APIURLs;
import limo.mylimo.MyHistoryScreen;
import limo.mylimo.OrderBookingScreen;
import limo.mylimo.R;
import limo.mylimo.SetterGetter.HistoryData;
import limo.mylimo.VolleyLibraryFiles.AppSingleton;


/**
 * Created by Shoaib Anwar on 13-Feb-18.
 */

public class HistoryDataAdapter extends RecyclerView.Adapter<HistoryDataAdapter.MyViewHolder>  {

    private ArrayList<HistoryData> userHisotry;
    private Activity activity;

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private boolean isLoadingAdded = false;
    private boolean isIdFojnd = false;

    private JSONArray jsonArray;
    private JSONObject jsonObject;

    private int lastPosition = -1;

    public HistoryDataAdapter(Activity activity, ArrayList<HistoryData> adList ) {
        this.userHisotry = adList;
        this.activity = activity;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {


        protected TextView tvDate;
        protected TextView tvTime;
        protected TextView tvFromCity;
        protected TextView tvToCity;
        protected TextView tvPrice;
        private TextView tv_order_id;

        protected Button bt_cancel_order;
        protected  ProgressBar progress_bar;


        public MyViewHolder(final View view) {
            super(view);

            tvDate =  (TextView) view.findViewById(R.id.tv_date);
            tvTime = (TextView) view.findViewById(R.id.tv_time);
            tvFromCity = (TextView) view.findViewById(R.id.tv_from_city);
            tvToCity = (TextView) view.findViewById(R.id.tv_to_city);
            tvPrice = (TextView) view.findViewById(R.id.tv_price);
            tv_order_id = (TextView) view.findViewById(R.id.tv_order_id);
            bt_cancel_order = (Button) view.findViewById(R.id.bt_cancel_order);
            progress_bar = (ProgressBar) view.findViewById(R.id.progress_bar);

        }
    }




    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_history_screen, null);
        MyViewHolder mh = new MyViewHolder(v);
        return mh;


    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if (holder instanceof MyViewHolder) {


            HistoryData ad = userHisotry.get(position);

            String date = ad.getDate();

            String mTime = ad.getTime();
            mTime = mTime.substring(3);
            Log.e("TAG", "the trim time is: " + mTime);
            String datTime = date+mTime;


            String[] calend = date.split("-");
            int month = Integer.parseInt(calend[1]);
            int dayofWeek = Integer.parseInt(calend[2]);
            String  day = calend[2];

            Calendar cal=Calendar.getInstance();
            SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
            cal.set(Calendar.MONTH, month -1);

            String month_name = month_date.format(cal.getTime());
            


           // Log.e("TAg", "the day is: " + dayname);

            date = month_name + " " + day;


            holder.tvDate.setText(date);

            holder.tvTime.setText(ad.getTime());

            holder.tvFromCity.setText(ad.getPickup_city());
            holder.tvToCity.setText(ad.getDestination_city());
            holder.tvPrice.setText(ad.getPrice());
            holder.tv_order_id.setText(ad.getOrder_id());


            long currentMillis = System.currentTimeMillis();


            long availableMillis = 0;


            String stringDateTime = datTime;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm aa");
            Date d = null;
            try {
                d = format.parse(stringDateTime);
                availableMillis = d.getTime();
                Log.e("TAG", "the order datetime in milli: " + availableMillis);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            Log.e("TAG", "the order time is: " + currentMillis);
            Log.e("TAG", "the order datetime is: " + availableMillis);

            long difference = currentMillis - availableMillis;
            Log.e("TAG", "the order difference is: " + difference);
            //300000 milliseconds for 5 minuts

            if (difference>1800000){
                holder.bt_cancel_order.setVisibility(View.GONE);
            }
            else {
                holder.bt_cancel_order.setVisibility(View.VISIBLE);
            }

            holder.bt_cancel_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    exitScreenAlert(activity, holder.tv_order_id.getText().toString(), holder.progress_bar, position);

                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return userHisotry.size();
    }

    @Override
    public int getItemViewType(int position) {


        return (position == userHisotry.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }


    public String timeFormteIn12Hr(String time){

        DateFormat f1 = new SimpleDateFormat("kk:mm");
        Date d = null;
        try {
            d = f1.parse(time);
            DateFormat f2 = new SimpleDateFormat("h:mm a");
            time = f2.format(d).toUpperCase(); // "12:18am"

        } catch (ParseException e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return time;

    }

    private void exitScreenAlert(final Activity activity, final String orderID, final ProgressBar progressBar, final int itemPosition) {

        final Dialog confirmationDiloag = new Dialog(activity);

        confirmationDiloag.requestWindowFeature(Window.FEATURE_NO_TITLE);
        confirmationDiloag.setContentView(R.layout.order_confirmation_alert);
        TextView tv_description = (TextView) confirmationDiloag.findViewById(R.id.tv_description);
        TextView tv_dialog_title = (TextView) confirmationDiloag.findViewById(R.id.tv_dialog_title);

        tv_dialog_title.setText("Order Cancellation");
        tv_description.setText("Do You Want to Cancel Ride");

        Button bt_no = (Button) confirmationDiloag.findViewById(R.id.bt_no);
        Button bt_yes = (Button) confirmationDiloag.findViewById(R.id.bt_yes);

        bt_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmationDiloag.dismiss();
            }
        });

        //submitting order
        bt_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                confirmationDiloag.dismiss();
                Log.e("TAG", "the order id is salman: " + orderID);

                orderCancelation(orderID, progressBar, activity, itemPosition);


            }
        });

        confirmationDiloag.show();
    }


    //order booking server
    private void orderCancelation(final String orderId, final ProgressBar progressBar, final Activity activity, final int itemPosition){


        // Tag used to cancel the request
        String cancel_req_tag = "register";
        progressBar.setVisibility(View.VISIBLE);

        StringRequest strReq = new StringRequest(Request.Method.POST, APIURLs.CANCEL_ORDER , new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("TAG", "order Cancel Response: " + response.toString());
                //hideDialog();
                progressBar.setVisibility(View.GONE);

                try {

                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {

                        String message = jObj.getString("msg");
                        if (message.equals("Order Has Been Cancelled.")){

                            userHisotry.remove(itemPosition);
                            notifyDataSetChanged();
                            Toast.makeText(activity, "Order Has been Cancelled.", Toast.LENGTH_SHORT).show();

                        }

                    } else {

                        String errorMsg = jObj.getString("msg");

                        Toast.makeText(activity, errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Registration Error: " + error.getMessage());
                Toast.makeText(activity,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                //hideDialog();
                progressBar.setVisibility(View.GONE);
            }
        }
        )
        {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url

                Map<String, String> params = new HashMap<String, String>();

                Log.e("TAG", "the order id is yasir: " + orderId);
                params.put("order_id", orderId);

                return params;
            }
        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppSingleton.getInstance(activity).addToRequestQueue(strReq, cancel_req_tag);

    }//end of order service

    @Override
    public void onViewDetachedFromWindow(MyViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        setFadeAnimation(holder.itemView);
    }

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(2000);
        view.startAnimation(anim);
    }

}