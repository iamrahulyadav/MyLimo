package limo.mylimo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;

import java.sql.Time;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class OrderBooking extends AppCompatActivity {

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    RelativeLayout rl_tv_pickuplocation, rl_tv_dropofflocation;
    RelativeLayout rl_tv_pickupdate, rl_tv_pickuptime;
    TextView tv_pickuplocation, tv_dropoff;
    TextView tv_pickupdate, tv_pickuptime;


    RelativeLayout rl_tv_submit;
    int indecator = -1;

    private int mYear, mMonth, mDay, mHour, mMinute;

    private int myHour, myMinut, myday, myMonth, myYear;

    private LatLng latlngPickup;
    private LatLng latlngDropoff;

    String orderTosend = null;

    String userPickupLocation = null;
    String userDropoffLocation = null;
    String userPickuplatlng = null;
    String userDropoffLatlng = null;
    String userPickDate = null;
    String userPickTime = null;
    String userDistance = null;
    String userFare = null;

    AlarmManager alarmManager;
    private PendingIntent pendingIntentForNotification;

    ProgressBar progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        getPickupLocation();
        getDropofflocation();
        selectDate();
        selectTime();

        submitclickListener();
    }

    public void init(){
        rl_tv_pickuplocation = (RelativeLayout) findViewById(R.id.rl_tv_pickuplocation);
        rl_tv_dropofflocation = (RelativeLayout) findViewById(R.id.rl_tv_dropofflocation);
        tv_pickuplocation = (TextView) findViewById(R.id.tv_pickuplocation);
        tv_dropoff = (TextView) findViewById(R.id.tv_dropoff);

        rl_tv_pickupdate = (RelativeLayout) findViewById(R.id.rl_tv_pickupdate);
        rl_tv_pickuptime = (RelativeLayout) findViewById(R.id.rl_tv_pickuptime);
        tv_pickupdate = (TextView) findViewById(R.id.tv_pickupdate);
        tv_pickuptime = (TextView) findViewById(R.id.tv_pickuptime);



        rl_tv_submit = (RelativeLayout) findViewById(R.id.rl_tv_submit);


        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        Intent notificiationIntent = new Intent(OrderBooking.this, AlarmReceiver.class);
        notificiationIntent.putExtra("alarmId", 0);

        pendingIntentForNotification = PendingIntent.getBroadcast(OrderBooking.this, 0, notificiationIntent, 0);

        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);

    }//end of init

    public void getPickupLocation(){

        rl_tv_pickuplocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                indecator = 0;
                callingLocationDialog();

            }
        });
    }//end of getting pickup location

    public void getDropofflocation(){

        rl_tv_dropofflocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                indecator = 1;
                callingLocationDialog();

            }
        });
    }//end of getting dropoff location


    public void callingLocationDialog(){


        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).setBoundsBias(new LatLngBounds(new LatLng(23.695,  68.149), new LatLng(35.88250, 76.51333)))//south and north latlong bourdy for pakistan
                            .build(OrderBooking.this);




            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }





    public void selectDate(){

        rl_tv_pickupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(OrderBooking.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {


                                myday = dayOfMonth;
                                myMonth = monthOfYear + 1;
                                myYear = year;
                                tv_pickupdate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                                tv_pickupdate.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis()- 1000); // for setting set start form current date

                datePickerDialog.show();


            }
        });
    }//end of select date

    public void selectTime(){

        rl_tv_pickuptime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(OrderBooking.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {


                                myMinut = minute;
                                myHour = hourOfDay;

                                //tv_pickuptime.setText(hourOfDay + ":" + minute);


                                String hourString = "";
                                if(hourOfDay < 12) {
                                    hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
                                } else {
                                    hourString = (hourOfDay - 12) < 10 ? "0"+(hourOfDay - 12) : ""+(hourOfDay - 12);
                                }
                                String minuteString = minute < 10 ? "0"+minute : ""+minute;
                                //String secondString = second < 10 ? "0"+second : ""+second;
                                String am_pm = (hourOfDay < 12) ? "AM" : "PM";
                                String time = hourString+":"+minuteString + " " + am_pm;

                                tv_pickuptime.setText(time);
                                tv_pickuptime.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));




                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e("TAg", "the code is result: " + resultCode);
        Log.e("TAg", "the code is resquest: " + requestCode);
        Log.e("TAg", "the code is Intent: " + data);

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                String plceName = place.getName().toString();
                String plceAddress = place.getAddress().toString();
                LatLng latlng = place.getLatLng();
                Log.i("TAG", "Place: 123" + place.getName());

                Log.i("TAG", "Place: " + place.getAddress());
                Log.i("TAG", "Place Coordinates: " + place.getLatLng());

                if (indecator==0){

                    tv_pickuplocation.setText(plceAddress);
                    tv_pickuplocation.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                    latlngPickup =latlng;
                }
                else if(indecator==1){
                    tv_dropoff.setText(plceAddress);
                    tv_dropoff.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));

                    latlngDropoff = latlng;
                }


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("TAG", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }//end of onActivity Result





    public void submitclickListener(){

        rl_tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String pickLocation = tv_pickuplocation.getText().toString();
                final String drobOffLocation  = tv_dropoff.getText().toString();
                final String pickupDate = tv_pickupdate.getText().toString();
                final String pickupTime = tv_pickuptime.getText().toString();

                if (pickLocation.length()==0){

                    Toast.makeText(OrderBooking.this, "Please Select your Pickup Location", Toast.LENGTH_SHORT).show();
                }
                else if (drobOffLocation.length()==0){

                    Toast.makeText(OrderBooking.this, "Please Select your Dropoff Location", Toast.LENGTH_SHORT).show();
                }
                else if (pickupDate.length()==0){

                    Toast.makeText(OrderBooking.this, "Please Select your Pickup Date", Toast.LENGTH_SHORT).show();
                }
                else if (pickupTime.length()==0){

                    Toast.makeText(OrderBooking.this, "Please Select your Pickup Time", Toast.LENGTH_SHORT).show();
                }

                else if (isNetworkAvailable()==false){
                    Toast.makeText(OrderBooking.this, "No Internet Connection found", Toast.LENGTH_SHORT).show();
                }
                else {

                    Log.i("TAG", "The Pickup LatLng: " + latlngPickup);
                    Log.i("TAG", "The Droboff LatLng: " + latlngDropoff);

                    Double selctedMarketTime =  SphericalUtil.computeDistanceBetween(latlngPickup, latlngDropoff);
                    Log.i("TAG", "The distance is: " + selctedMarketTime);
                    double estimateDriveTime = selctedMarketTime/1000;
                    double aa =  round(estimateDriveTime, 1);
                    int distace = ((int)aa) + 4;
                    Log.i("TAG", "The distance is: " + distace);



                    //for calculating time

                    double esTime = distace/11;
                    Log.i("TAG", "The time is: " + esTime);
                    double timeInMinuts = esTime/60;

                    Log.i("TAG", "The timeinminut is: " + esTime);
                    double timeroudn = round(timeInMinuts, 0);
                    Double d = new Double(timeroudn);
                    int totalTime = d.intValue();


                    String mdistance = distace + " km";
                    String mFare =  "Rs." + distace*20;


                    userPickupLocation = pickLocation;
                    userDropoffLocation = drobOffLocation;
                    userPickuplatlng = latlngPickup.toString();
                    userDropoffLatlng = latlngDropoff.toString();
                    userPickDate = pickupDate;
                    userPickTime = pickupTime;
                    userDistance = mdistance;
                    userFare = mFare;


                    AlertDialog.Builder dialog = new AlertDialog.Builder(OrderBooking.this);
                    dialog.setTitle("The Estimated Fare");
                    dialog.setMessage("The Total Distance is: " + distace + " Km" +
                            // "\n" + "The Total Estimate Traveling Time: " + totalTime + "minuts" +
                            "\n" + "The Total Estimated Fare is: " + "Rs." + (distace*20));

                    dialog.setPositiveButton("Book Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            String textToSend = "Pick Address: " + pickLocation
                                    + " \n" + "Pickup Date: " + pickupDate
                                    + " \n" + "Pickup time: " + pickupTime
                                    + " \n" + "Dropoff Location: " + drobOffLocation
                                    + " \n" + "Pickup : " + latlngPickup
                                    + " \n" + "Dropoff : " + latlngDropoff;

                            //send mail here
                           /* Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                    "mailto","shoaib.ranglerz@gmail.com", null));
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Cab Booking Order");
                            emailIntent.putExtra(Intent.EXTRA_TEXT, textToSend);
                            startActivityForResult(Intent.createChooser(emailIntent, "Send email..."), 0);*/

                            orderTosend = textToSend;
                            new SendEmail().execute();

                        }
                    });

                    dialog.setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.dismiss();
                        }
                    });

                    dialog.show();
                }


            }
        });
    }
    //rouding double
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }


    //sending email

    public class SendEmail extends AsyncTask<Void,Void,String>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress_bar.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(Void... params) {
            String data = "test";
            try {

                Mail sender = new Mail("niazicaborders@gmail.com", "iamniazicaborder");
                // sender.addAttachment(Environment.getExternalStorageDirectory()+Imagepath);
                sender.sendMail("New Booking Order",
                        orderTosend,
                        "niazicaborders@gmail.com",
                        "niazicaborders@gmail.com");


            } catch (Exception e) {
                Log.d("tag", "Exception Occur" + e.toString());
            }
            return data;
        }
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String data) {
            Log.e("tag", "Post Excute Data is " + data);
            progress_bar.setVisibility(View.GONE);
            //Toast.makeText(getApplicationContext(),"Mail Sent Successfully",Toast.LENGTH_SHORT).show();

            DbHelper helper = new DbHelper();
            MyDatabase db = new MyDatabase(OrderBooking.this);
            helper.setPickupaddress(userPickupLocation);
            helper.setDroboffaddress(userDropoffLocation);
            helper.setPickuplatlng(userPickuplatlng);
            helper.setDrobofflatlng(userDropoffLatlng);;
            helper.setPickupdate(userPickDate);
            helper.setPickuptime(userPickTime);
            helper.setEstimateddistance(userDistance);
            helper.setEstimatedfare(userFare);


            long result =  db.insertDatatoDb(helper);
            if (result > -1){
                Log.i("TAG", "Data Inserted to db");

                //setting Notification Intent
                Log.e("TAG", "The alram Hhours: " + myHour);
                Log.e("TAG", "The alram Minut: " + myMinut);
                Log.e("TAG", "The alram year: " + myYear);
                Log.e("TAG", "The alram month: " + myMonth);
                Log.e("TAG", "The alram day: " + myday);

                startAlarmForMorning(myHour, myMinut, myYear, myMonth, myday);
            }



            AlertDialog.Builder alert = new AlertDialog.Builder(OrderBooking.this);
            alert.setTitle("Order Submitted Sucessfully");
            alert.setMessage("Thank you " + "User Name" + "! Your Order Has been successfully sumitted, Our Agent Will receive you from your provided location at the time and date you provide " + "\n" + "You also can view your traveling route");
            alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            alert.setPositiveButton("Show Map", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {


                    Intent mapActivity = new Intent(OrderBooking.this, MapsActivity.class);


                    mapActivity.putExtra("pickuplatlng", latlngPickup);
                    mapActivity.putExtra("dropofflatlng", latlngDropoff);
                    mapActivity.putExtra("pickupaddress", userPickupLocation);
                    mapActivity.putExtra("droboffaddress", userDropoffLocation);
                    startActivity(mapActivity);

                    //showing google map rout here
                }
            });

            alert.show();

            // finish();
        }
    }
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void startAlarmForMorning(int timeHour, int timeMinute, int year, int month, int day){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month-1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        Log.e("TAG", "Year: " + year);
        Log.e("TAG", "month: " + month);
        Log.e("TAG", "DAY: " + day);
        calendar.set(Calendar.HOUR_OF_DAY, timeHour);
        Log.e("TAG", "TimeHousr: " + timeHour);
        calendar.set(Calendar.MINUTE, timeMinute);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntentForNotification);

    }




}
    
