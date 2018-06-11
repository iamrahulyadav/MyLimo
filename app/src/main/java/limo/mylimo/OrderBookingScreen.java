package limo.mylimo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.LoginFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.security.auth.login.LoginException;

import limo.mylimo.Adapters.SelectCityAdapter;
import limo.mylimo.Constant.APIURLs;
import limo.mylimo.VolleyLibraryFiles.AppSingleton;

public class OrderBookingScreen extends BaseActvitvityForDrawer {


    RelativeLayout rl_tickmark;
    ImageView iv_tic;

    TextView tv_date_text;
    TextView tv_time_text;

    // RelativeLayout rl_select_seats;
    // TextView tv_select_seat;
    LinearLayout ll_car_checkbox;

    LinearLayout ll_pickup_city, ll_destination_city,ll_car_type, ll_seats_selection, ll_date_selection;

    RelativeLayout rl_select_car_type;


    ImageView iv_search_pickup, iv_search_dropoff;

    TextView tv_poraname;

    private Button bt_fare_estimate;
    private Button bt_later;

    private int mlatLngPickUp = 1001;
    private int mlatLngDrop = 1002;
    private LatLng mPickupLatLng = null;
    private LatLng mDropoffLatLng = null;

    private LinearLayout ll_full_seat, ll_seats;

    private Spinner sp_pickup, sp_cartype, sp_seates, sp_dropoff ;
    // private Spinner sp_time_slots;
    private EditText et_detail_pickup, et_detail_dropoff;
    private Button bt_booknow;
    private ProgressBar progress_bar;
    private String orderTosend;

    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;

    private int year, month, day;
    Calendar myCalendar = Calendar.getInstance();
    String mSelectedDate = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_booking);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_booking, null, false);
        mDrawerLayout.addView(contentView, 0);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        init();

        carTimMarkClickListner();
        selectingPicupFromMapHandler();
        selectingDropoffFromMapHandler();
        estaimateFareButtonHanlder();
        btLaterClickHandler();
        spPickupSpinnerClick();
        spCartypeSpinnerClick();
        spSeatesSpinnerClick();
        bookNowButton();
        settingItemClickListnerOnDropoffCity();

        selectDate();

        openSpinerForPickupCity();
        openSpinerForDestinationCity();
        openSpinerForCarType();
        openSpinerForSeats();
        showDateDialog();
    }

    private void init() {


        rl_tickmark = (RelativeLayout) findViewById(R.id.rl_tickmark);
        iv_tic = (ImageView) findViewById(R.id.iv_tic);

        //rl_select_seats = (RelativeLayout) findViewById(R.id.rl_select_seats);
        //tv_select_seat = (TextView) findViewById(R.id.tv_select_seat);
        ll_car_checkbox = (LinearLayout) findViewById(R.id.ll_car_checkbox);
        ll_full_seat = (LinearLayout) findViewById(R.id.ll_full_seat);

        rl_select_car_type = (RelativeLayout) findViewById(R.id.rl_select_car_type);


        iv_search_pickup = (ImageView) findViewById(R.id.iv_search_pickup);
        iv_search_dropoff = (ImageView) findViewById(R.id.iv_search_dropoff);

        tv_poraname = (TextView) findViewById(R.id.tv_poraname);

        ll_seats = (LinearLayout) findViewById(R.id.ll_seats);

        bt_fare_estimate = (Button) findViewById(R.id.bt_fare_estimate);
        bt_later = (Button) findViewById(R.id.bt_later);

        tv_date_text = (TextView) findViewById(R.id.tv_date_text);

        //sp pickup
        sp_pickup = (Spinner) findViewById(R.id.sp_pickup);
        ArrayAdapter adapterPickup = ArrayAdapter.createFromResource(this,
                R.array.citiesname, R.layout.spinner_item);
        adapterPickup.setDropDownViewResource(R.layout.spinner_dropdown_item);
        sp_pickup.setAdapter(adapterPickup);


        //for car type
        sp_cartype = (Spinner) findViewById(R.id.sp_cartype);
        ArrayAdapter adapterCarType = ArrayAdapter.createFromResource(this,
                R.array.cartype, R.layout.spinner_item);
        adapterCarType.setDropDownViewResource(R.layout.spinner_dropdown_item);
        sp_cartype.setAdapter(adapterCarType);

        //for seates
        sp_seates = (Spinner) findViewById(R.id.sp_seates);
        ArrayAdapter adapterSeates = ArrayAdapter.createFromResource(this,
                R.array.seates, R.layout.spinner_item);
        adapterSeates.setDropDownViewResource(R.layout.spinner_dropdown_item);
        sp_seates.setAdapter(adapterSeates);

        sp_dropoff = (Spinner) findViewById(R.id.sp_dropoff);
        ArrayAdapter adapterDropoff = ArrayAdapter.createFromResource(this,
                R.array.emptyselect, R.layout.spinner_item);
        adapterPickup.setDropDownViewResource(R.layout.spinner_dropdown_item);
        sp_dropoff.setAdapter(adapterDropoff);


        et_detail_pickup = (EditText) findViewById(R.id.et_detail_pickup);
        et_detail_dropoff = (EditText) findViewById(R.id.et_detail_dropoff);

        bt_booknow = (Button) findViewById(R.id.bt_booknow);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);

       /* sp_time_slots = (Spinner) findViewById(R.id.sp_time_slots);
        ArrayAdapter adapterTimeSlots = ArrayAdapter.createFromResource(this,
                R.array.timing_slots, R.layout.spinner_item);
        adapterTimeSlots.setDropDownViewResource(R.layout.spinner_dropdown_item);
        sp_time_slots.setAdapter(adapterTimeSlots);*/

        tv_time_text = (TextView) findViewById(R.id.tv_time_text);

        ll_pickup_city = (LinearLayout) findViewById(R.id.ll_pickup_city);
        ll_destination_city = (LinearLayout) findViewById(R.id.ll_destination_city);
        ll_car_type = (LinearLayout) findViewById(R.id.ll_car_type);
        ll_seats_selection = (LinearLayout) findViewById(R.id.ll_seats_selection);
        ll_date_selection = (LinearLayout) findViewById(R.id.ll_date_selection);


    }

    //spinner item click listener
    private void spPickupSpinnerClick() {


        sp_pickup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String selectedCity = adapterView.getItemAtPosition(i).toString();
                Log.e("TAG", "selected item is: " + selectedCity);
                if (selectedCity.equals("Karachi")) {

                    ArrayAdapter adapterPickup = ArrayAdapter.createFromResource(OrderBookingScreen.this,
                            R.array.from_karachi, R.layout.spinner_item);
                    adapterPickup.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    sp_dropoff.setAdapter(adapterPickup);
                }
                if (selectedCity.equals("Hyderabad")) {

                    ArrayAdapter adapterPickup = ArrayAdapter.createFromResource(OrderBookingScreen.this,
                            R.array.from_hydrabad, R.layout.spinner_item);
                    adapterPickup.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    sp_dropoff.setAdapter(adapterPickup);

                }
                if (selectedCity.equals("Lahore")) {
                    ArrayAdapter adapterPickup = ArrayAdapter.createFromResource(OrderBookingScreen.this,
                            R.array.from_lahore, R.layout.spinner_item);
                    adapterPickup.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    sp_dropoff.setAdapter(adapterPickup);
                }

                if (selectedCity.equals("Quetta")) {

                    ArrayAdapter adapterPickup = ArrayAdapter.createFromResource(OrderBookingScreen.this,
                            R.array.from_quetta, R.layout.spinner_item);
                    adapterPickup.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    sp_dropoff.setAdapter(adapterPickup);
                }

                if (selectedCity.equals("Islamabad")) {

                    ArrayAdapter adapterPickup = ArrayAdapter.createFromResource(OrderBookingScreen.this,
                            R.array.from_islamabad, R.layout.spinner_item);
                    adapterPickup.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    sp_dropoff.setAdapter(adapterPickup);
                }
                if (selectedCity.equals("Faisalabad")) {

                    ArrayAdapter adapterPickup = ArrayAdapter.createFromResource(OrderBookingScreen.this,
                            R.array.from_faisalabad, R.layout.spinner_item);
                    adapterPickup.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    sp_dropoff.setAdapter(adapterPickup);
                }
                //
                if (selectedCity.equals("Loralai") || selectedCity.equals("Zhob") || selectedCity.equals("Qila Saifullah")
                        || selectedCity.equals("Chaman") || selectedCity.equals("Kuchlak") || selectedCity.equals("Kalat")
                        || selectedCity.equals("Khuzdar") || selectedCity.equals("Gwadar") || selectedCity.equals("Dera Ismail Khan")
                        || selectedCity.equals("Muslim Bagh") || selectedCity.equals("Mastung")) {


                    ArrayAdapter adapterPickup = ArrayAdapter.createFromResource(OrderBookingScreen.this,
                            R.array.to_quetta, R.layout.spinner_item);
                    adapterPickup.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    sp_dropoff.setAdapter(adapterPickup);
                }
                //

                if (selectedCity.equals("Select")) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void settingItemClickListnerOnDropoffCity() {

        sp_dropoff.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedCity = adapterView.getItemAtPosition(i).toString();
                String picupCity = sp_pickup.getSelectedItem().toString();

                if ((selectedCity.equals("Quetta") && picupCity.equals("Karachi")) || (selectedCity.equals("Karachi") && picupCity.equals("Quetta"))) {

                    sp_cartype.setSelection(1);
                    ArrayAdapter adapterCarType = ArrayAdapter.createFromResource(OrderBookingScreen.this,
                            R.array.carttype_when_quetta, R.layout.spinner_item);
                    adapterCarType.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    sp_cartype.setAdapter(adapterCarType);

                    //setting seats
                    ArrayAdapter adapterSeatsForQuetyKarachi = ArrayAdapter.createFromResource(OrderBookingScreen.this,
                            R.array.seatesForQuetyKarachi, R.layout.spinner_item);
                    adapterSeatsForQuetyKarachi.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    sp_seates.setAdapter(adapterSeatsForQuetyKarachi);

                }

                //
                else if ((selectedCity.equals("Quetta") && picupCity.equals("Dera Ismail Khan")) || (selectedCity.equals("Dera Ismail Khan") && picupCity.equals("Quetta"))) {

                    sp_cartype.setSelection(1);
                    ArrayAdapter adapterCarType = ArrayAdapter.createFromResource(OrderBookingScreen.this,
                            R.array.carttype_when_quetta, R.layout.spinner_item);
                    adapterCarType.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    sp_cartype.setAdapter(adapterCarType);

                }

                else if ((selectedCity.equals("Quetta") && picupCity.equals("Zhob")) || (selectedCity.equals("Zhob") && picupCity.equals("Quetta"))) {

                    sp_cartype.setSelection(1);
                    ArrayAdapter adapterCarType = ArrayAdapter.createFromResource(OrderBookingScreen.this,
                            R.array.carttype_when_quetta, R.layout.spinner_item);
                    adapterCarType.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    sp_cartype.setAdapter(adapterCarType);

                }
                else if ((selectedCity.equals("Quetta") && picupCity.equals("Qila Saifullah")) || (selectedCity.equals("Qila Saifullah") && picupCity.equals("Quetta"))) {

                    sp_cartype.setSelection(1);
                    ArrayAdapter adapterCarType = ArrayAdapter.createFromResource(OrderBookingScreen.this,
                            R.array.carttype_when_quetta, R.layout.spinner_item);
                    adapterCarType.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    sp_cartype.setAdapter(adapterCarType);

                }
                else if ((selectedCity.equals("Quetta") && picupCity.equals("Loralai")) || (selectedCity.equals("Loralai") && picupCity.equals("Quetta"))) {

                    sp_cartype.setSelection(1);
                    ArrayAdapter adapterCarType = ArrayAdapter.createFromResource(OrderBookingScreen.this,
                            R.array.carttype_when_quetta, R.layout.spinner_item);
                    adapterCarType.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    sp_cartype.setAdapter(adapterCarType);

                }

                else if ((selectedCity.equals("Quetta") && picupCity.equals("Chaman")) || (selectedCity.equals("Chaman") && picupCity.equals("Quetta"))) {

                    sp_cartype.setSelection(1);
                    ArrayAdapter adapterCarType = ArrayAdapter.createFromResource(OrderBookingScreen.this,
                            R.array.carttype_when_quetta, R.layout.spinner_item);
                    adapterCarType.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    sp_cartype.setAdapter(adapterCarType);

                }
                else if ((selectedCity.equals("Quetta") && picupCity.equals("Kalat")) || (selectedCity.equals("Kalat") && picupCity.equals("Quetta"))) {

                    sp_cartype.setSelection(1);
                    ArrayAdapter adapterCarType = ArrayAdapter.createFromResource(OrderBookingScreen.this,
                            R.array.carttype_when_quetta, R.layout.spinner_item);
                    adapterCarType.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    sp_cartype.setAdapter(adapterCarType);

                }
                else if ((selectedCity.equals("Quetta") && picupCity.equals("Khuzdar")) || (selectedCity.equals("Khuzdar") && picupCity.equals("Quetta"))) {

                    sp_cartype.setSelection(1);
                    ArrayAdapter adapterCarType = ArrayAdapter.createFromResource(OrderBookingScreen.this,
                            R.array.carttype_when_quetta, R.layout.spinner_item);
                    adapterCarType.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    sp_cartype.setAdapter(adapterCarType);

                }
                else if ((selectedCity.equals("Quetta") && picupCity.equals("Gwadar")) || (selectedCity.equals("Gwadar") && picupCity.equals("Quetta"))) {

                    sp_cartype.setSelection(1);
                    ArrayAdapter adapterCarType = ArrayAdapter.createFromResource(OrderBookingScreen.this,
                            R.array.carttype_when_quetta, R.layout.spinner_item);
                    adapterCarType.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    sp_cartype.setAdapter(adapterCarType);

                }
                else if ((selectedCity.equals("Quetta") && picupCity.equals("Muslim Bagh")) || (selectedCity.equals("Muslim Bagh") && picupCity.equals("Quetta"))) {

                    sp_cartype.setSelection(1);
                    ArrayAdapter adapterCarType = ArrayAdapter.createFromResource(OrderBookingScreen.this,
                            R.array.carttype_when_quetta, R.layout.spinner_item);
                    adapterCarType.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    sp_cartype.setAdapter(adapterCarType);

                }

                //

                else {

                    sp_cartype.setSelection(1);
                    ArrayAdapter adapterCarType = ArrayAdapter.createFromResource(OrderBookingScreen.this,
                            R.array.cartype, R.layout.spinner_item);
                    adapterCarType.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    sp_cartype.setAdapter(adapterCarType);

                    //setting seats
                    ArrayAdapter adapterSeatsForQuetyKarachi = ArrayAdapter.createFromResource(OrderBookingScreen.this,
                            R.array.seates, R.layout.spinner_item);
                    adapterSeatsForQuetyKarachi.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    sp_seates.setAdapter(adapterSeatsForQuetyKarachi);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    //spinner item click cart type
    private void spCartypeSpinnerClick() {


        sp_cartype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                /*String picupCity = sp_pickup.getSelectedItem().toString();
                String dropCity = sp_dropoff.getSelectedItem().toString();


                if (dropCity.equals("Quetta") && picupCity.equals("Karachi")){

                    //sp_cartype.setSelection(1);
                }

                if (picupCity.equals("Karachi") && dropCity.equals("Quetta")){

                   // sp_cartype.setSelection(1);
                }*/

                String selectedCity = adapterView.getItemAtPosition(i).toString();

                Log.e("TAG", "selected item is: " + selectedCity);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


    //spinner item click cart type
    private void spSeatesSpinnerClick() {


        sp_seates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String selectedCity = adapterView.getItemAtPosition(i).toString();
                Log.e("TAG", "selected item is: " + selectedCity);
                if (selectedCity.equals("1")) {
                    ll_car_checkbox.setVisibility(View.GONE);
                }
                if (selectedCity.equals("2")) {
                    ll_car_checkbox.setVisibility(View.GONE);
                }
                if (selectedCity.equals("3")) {
                    ll_car_checkbox.setVisibility(View.GONE);
                }
                if (selectedCity.equals("0")) {
                    ll_car_checkbox.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


    private void carTimMarkClickListner() {

        rl_tickmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (iv_tic.getVisibility() == View.VISIBLE) {
                    iv_tic.setVisibility(View.GONE);
                    ll_seats.setVisibility(View.VISIBLE);
                } else if (iv_tic.getVisibility() == View.GONE) {
                    iv_tic.setVisibility(View.VISIBLE);
                    ll_seats.setVisibility(View.GONE);
                }


            }
        });
    }


    private void selectingPicupFromMapHandler() {

        iv_search_pickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent mapActivity = new Intent(OrderBookingScreen.this, PinSelectingFromMapActivity.class);
                startActivityForResult(mapActivity, mlatLngPickUp);//11 for starting mapActivity for pickup location
            }
        });
    }

    private void selectingDropoffFromMapHandler() {

        iv_search_dropoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent mapActivity = new Intent(OrderBookingScreen.this, PinSelectingFromMapActivity.class);
                startActivityForResult(mapActivity, mlatLngDrop);//11 for starting mapActivity for pickup location
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == mlatLngPickUp) {
            if (resultCode == RESULT_OK) {

                double lat = data.getDoubleExtra("lat", 0);
                double lng = data.getDoubleExtra("lng", 0);
                LatLng latLng = new LatLng(lat, lng);
                Log.e("TAg", "the latlng are 123: " + latLng);
                mPickupLatLng = latLng;


            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        if (requestCode == mlatLngDrop) {
            if (resultCode == RESULT_OK) {

                double lat = data.getDoubleExtra("lat", 0);
                double lng = data.getDoubleExtra("lng", 0);
                LatLng latLng = new LatLng(lat, lng);
                Log.e("TAg", "the latlng are 123: " + latLng);
                mDropoffLatLng = latLng;

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }//end of onActivity Result

    private void estaimateFareButtonHanlder() {
        bt_fare_estimate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Animation animShake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);

                String spPickCity = sp_pickup.getSelectedItem().toString();
                String spDropoff = sp_dropoff.getSelectedItem().toString();
                String spCartype = sp_cartype.getSelectedItem().toString();
                String spSeates = sp_seates.getSelectedItem().toString();
                String mEstimateFare = "";

                if (spPickCity.equals("Select")) {
                    sp_pickup.startAnimation(animShake);
                    Toast.makeText(OrderBookingScreen.this, "Please Select pick city", Toast.LENGTH_SHORT).show();
                } else if (spDropoff.equals("Select")) {
                    sp_dropoff.startAnimation(animShake);
                    Toast.makeText(OrderBookingScreen.this, "Please Select destination city", Toast.LENGTH_SHORT).show();
                } else if (spCartype.equals("Select")) {
                    sp_cartype.startAnimation(animShake);
                    Toast.makeText(OrderBookingScreen.this, "Please Select Car type", Toast.LENGTH_SHORT).show();
                }else if (spSeates.equals("0") && iv_tic.getVisibility() == View.GONE){
                    ll_full_seat.startAnimation(animShake);
                    Toast.makeText(OrderBookingScreen.this, "Please Select Seats", Toast.LENGTH_SHORT).show();
                }
                else {

                    final Dialog fareEstemateDialog = new Dialog(OrderBookingScreen.this);
                    fareEstemateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    fareEstemateDialog.setContentView(R.layout.custom_fare_dialog);
                    TextView tvDescriptionText = (TextView) fareEstemateDialog.findViewById(R.id.tv_description);
                    TextView tvPrice = (TextView) fareEstemateDialog.findViewById(R.id.tv_price);
                    tvDescriptionText.setText("The Total Fare From " + spPickCity + " to " + spDropoff + " is ");

                    if (iv_tic.getVisibility() == View.VISIBLE) {
                        if (spDropoff.equals("Quetta") && spPickCity.equals("Karachi")) {
                            spSeates = "4";
                        } else if (spDropoff.equals("Karachi") && spPickCity.equals("Quetta")) {
                            spSeates = "4";
                        } else {
                            spSeates = "3";

                        }
                    }

                    mEstimateFare = FareEstimation(spPickCity, spDropoff, spCartype, spSeates);

                    tvPrice.setText(mEstimateFare);

                    Button btOk = (Button) fareEstemateDialog.findViewById(R.id.bt_ok);
                    //btOk click handler
                    btOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            fareEstemateDialog.dismiss();
                        }
                    });

                    fareEstemateDialog.show();

                }

            }
        });
    }

    private void btLaterClickHandler() {

        bt_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                exitScreenAlert();

            }
        });
    }

    private void bookNowButton() {

        bt_booknow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Animation animShake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);

                String detailPickup = et_detail_pickup.getText().toString();
                String detailDropoff = et_detail_dropoff.getText().toString();
                String spPickCity = sp_pickup.getSelectedItem().toString();
                String spDropoff = sp_dropoff.getSelectedItem().toString();
                String spCartype = sp_cartype.getSelectedItem().toString();
                String spSeates = sp_seates.getSelectedItem().toString();
                //  String spStimeSlots = sp_time_slots.getSelectedItem().toString();


                /*   if (et_detail_pickup.length()==0){

                 *//* et_detail_pickup.setError("Should not empty");
                    et_detail_pickup.startAnimation(animShake);*//*

                   detailPickup = "";

            }
                else if (et_detail_dropoff.length() == 0){

                    *//* et_detail_dropoff.setError("Should not be empty");
                    et_detail_dropoff.startAnimation(animShake);*//*

                    detailDropoff = "";

                }
                else*/
                if (spPickCity.equals("Select")) {
                    Toast.makeText(OrderBookingScreen.this, "Please Select pick city", Toast.LENGTH_SHORT).show();
                    sp_pickup.startAnimation(animShake);
                } else if (mPickupLatLng == null) {
                    Toast.makeText(OrderBookingScreen.this, "Please Select pickup from map", Toast.LENGTH_SHORT).show();
                    iv_search_pickup.startAnimation(animShake);
                } else if (spDropoff.equals("Select")) {
                    Toast.makeText(OrderBookingScreen.this, "Please Select destination city", Toast.LENGTH_SHORT).show();
                    sp_dropoff.startAnimation(animShake);
                } else if (mDropoffLatLng == null) {
                    Toast.makeText(OrderBookingScreen.this, "Please Select drop off from map", Toast.LENGTH_SHORT).show();
                    iv_search_dropoff.startAnimation(animShake);
                } else if (spCartype.equals("Select")) {
                    Toast.makeText(OrderBookingScreen.this, "Please select car type", Toast.LENGTH_SHORT).show();
                    sp_cartype.startAnimation(animShake);
                } else if (iv_tic.getVisibility() == view.GONE && spSeates.equals("0")) {
                    ll_full_seat.startAnimation(animShake);
                } /*else if (spStimeSlots.equals("Select")) {
                    Toast.makeText(OrderBookingScreen.this, "Please Select Time Slot", Toast.LENGTH_SHORT).show();
                    sp_time_slots.startAnimation(animShake);
                }*/else if (mSelectedDate.length()<1){
                    Toast.makeText(OrderBookingScreen.this, "Please Booking Date", Toast.LENGTH_SHORT).show();
                    tv_date_text.startAnimation(animShake);
                }
                else {
                    if (spCartype.equals("Select")) {
                        Toast.makeText(OrderBookingScreen.this, "Please select car type", Toast.LENGTH_SHORT).show();
                    } else {


                        if (iv_tic.getVisibility() == View.VISIBLE) {
                            if (spDropoff.equals("Quetta") && spPickCity.equals("Karachi")) {

                                spSeates = "4";

                            } else if (spDropoff.equals("Karachi") && spPickCity.equals("Quetta")) {

                                spSeates = "4";

                            } else {

                                spSeates = "3";

                            }

                        }


                        SharedPreferences sharedPreferences = getSharedPreferences("mylimouser", 0);

                        String userId = sharedPreferences.getString("user_id", null);
                        String fullname = sharedPreferences.getString("fullname", null);
                        String email = sharedPreferences.getString("email", null);
                        String phone = sharedPreferences.getString("mobile", null);
                        String cnic = sharedPreferences.getString("cnic", null);
                        double pickupLat = mPickupLatLng.latitude;
                        double pickupLnt = mPickupLatLng.longitude;
                        double destinationLat = mDropoffLatLng.latitude;
                        double destinationLnt = mDropoffLatLng.longitude;

                        String picLat = String.valueOf(pickupLat);
                        String picLng = String.valueOf(pickupLnt);
                        String desLat = String.valueOf(destinationLat);
                        String desLng = String.valueOf(destinationLnt);


                        Log.e("TAG", "the full name: " + fullname);
                        Log.e("TAG", "the email: " + email);
                        Log.e("TAG", "the phone: " + phone);
                        Log.e("TAG", "the cnic: " + cnic);
                        Log.e("TAG", "the latlng pickup: " + mPickupLatLng);
                        Log.e("TAG", "the latlng dropoff: " + mDropoffLatLng);
                        Log.e("TAG", "the detail pick up: " + detailPickup);
                        Log.e("TAG", "the detail dropoff: " + detailDropoff);
                        Log.e("TAG", "the from city: " + spPickCity);
                        Log.e("TAG", "the to city: " + spDropoff);
                        Log.e("TAG", "the car type: " + spCartype);
                        Log.e("TAG", "the seats: " + spSeates);
                        // Log.e("TAG", "the Time Slot " + spStimeSlots);


                        String textToSend = "User Full Name: " + fullname
                                + " \n" + "User Email: " + email
                                + " \n" + "User  Mobile Number: " + phone
                                + " \n" + "User CNIC: " + cnic
                                + " \n" + "User Pick up LatLng : " + mPickupLatLng
                                + " \n" + "User Destination LatLng : " + mDropoffLatLng
                                + " \n" + "User Detail of pickup : " + detailPickup
                                + " \n" + "User Detail of Dropoff : " + detailDropoff
                                + " \n" + "User Pick up City : " + spPickCity
                                + " \n" + "User Destination City : " + spDropoff
                                + " \n" + "Request Car Type : " + spCartype
                                + " \n" + "Seates : " + spSeates;


                        orderTosend = textToSend;
                        // new SendEmail().execute();


                        String mFareEstimation = FareEstimation(spPickCity, spDropoff, spCartype, spSeates);


                        confirmatinDialog(userId, picLat, picLng, detailPickup, desLat, desLng, detailDropoff, spPickCity, spDropoff, spCartype, spSeates, mFareEstimation, "", mSelectedDate);

                       /* String dataTime = mSelectedDate+" " + spStimeSlots;
                        //checkig if selected time and date is past
                       boolean isGrater = isTimeGrater(dataTime);

                        if (isGrater) {

                            Log.e("TAG", "the destination data lats are: " + desLat);
                            Log.e("TAG", "the destination data lngs are: " + desLng);
                            confirmatinDialog(userId, picLat, picLng, detailPickup, desLat, desLng, detailDropoff, spPickCity, spDropoff, spCartype, spSeates, mFareEstimation, spStimeSlots, mSelectedDate);
                        }
                        else {
                          //  sp_time_slots.startAnimation(animShake);
                            tv_date_text.startAnimation(animShake);
                            Toast.makeText(OrderBookingScreen.this, "Your Selected Time or Date Has been Passed", Toast.LENGTH_LONG).show();
                        }*/

                    }
                }

            }
        });
    }


    //
    //sending email

    public class SendEmail extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress_bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            String data = "test";
            try {

                Mail sender = new Mail("emailhere", "passwordhere");
                // sender.addAttachment(Environment.getExternalStorageDirectory()+Imagepath);
                sender.sendMail("New Booking Order",
                        orderTosend,
                        "fromemail",
                        "toemail");


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


            final Dialog exitDialog = new Dialog(OrderBookingScreen.this);
            exitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            exitDialog.setContentView(R.layout.custom_dialog_ordersubmited);
            TextView tv_dialog_title = (TextView) exitDialog.findViewById(R.id.tv_dialog_title);
            TextView tv_description = (TextView) exitDialog.findViewById(R.id.tv_description);

            tv_dialog_title.setText("Ride Submited Successfully");
            SharedPreferences sharedPreferences = getSharedPreferences("mylimouser", 0);
            String fullname = sharedPreferences.getString("fullname", null);
            tv_description.setText("Thank you " + fullname + "! Your Order Has been submitted successfully, Our Agent Will Contact you soon");
            Button btExit = (Button) exitDialog.findViewById(R.id.bt_exit);
            //btOk click handler
            btExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    exitDialog.dismiss();

                    finish();
                }
            });

            exitDialog.show();

        }
    }


    //order booking server
    private void orderBookingService(final String user_id, final String pickup_lat, final String pickup_lng, final String pickup_detail, final String destination_lat,
                                     final String destination_lng, final String destination_detail, final String pickup_city, final String destination_city, final String car_type,
                                     final String seats, final String price, final String timeSlots, final String selectDateSlot) {


        // Tag used to cancel the request
        String cancel_req_tag = "register";
        progress_bar.setVisibility(View.VISIBLE);

        StringRequest strReq = new StringRequest(Request.Method.POST, APIURLs.submitOrder, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("TAG", "order reponse: " + response.toString());
                //hideDialog();
                progress_bar.setVisibility(View.GONE);

                try {

                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {

                        String msg = jObj.getString("msg");
                        if (msg.equals("Order Submited.")) {

                            final Dialog orderSumitedDialog = new Dialog(OrderBookingScreen.this);
                            orderSumitedDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            orderSumitedDialog.setContentView(R.layout.custom_dialog_ordersubmited);
                            TextView tv_dialog_title = (TextView) orderSumitedDialog.findViewById(R.id.tv_dialog_title);
                            TextView tv_description = (TextView) orderSumitedDialog.findViewById(R.id.tv_description);

                            tv_dialog_title.setText("Order Submited Successfully");
                            SharedPreferences sharedPreferences = getSharedPreferences("mylimouser", 0);
                            String fullname = sharedPreferences.getString("fullname", null);
                            tv_description.setText("Thank you " + fullname + "! Your ride has been submitted successfully, Our agent will contact you soon. You can also cancel ride within 30 minutes.");
                            tv_description.setTextSize(15);
                            Button btExit = (Button) orderSumitedDialog.findViewById(R.id.bt_exit);
                            //btOk click handler
                            btExit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    orderSumitedDialog.dismiss();
                                    Intent startAgain = new Intent(OrderBookingScreen.this, OrderBookingScreen.class);
                                    startActivity(startAgain);
                                    finish();
                                }
                            });

                            orderSumitedDialog.setCancelable(false);
                            orderSumitedDialog.show();

                        }


                    } else {

                        String errorMsg = jObj.getString("msg");
                        if(errorMsg.equals("User Not Exist")){

                            AlertDialog.Builder alert = new AlertDialog.Builder(OrderBookingScreen.this);
                            alert.setTitle("Alert");
                            alert.setMessage("The user has been blocked");
                            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    dialogInterface.dismiss();
                                    SharedPreferences sharedPreferences = getSharedPreferences("mylimouser", 0);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.clear();
                                    editor.commit();

                                    finish();
                                    Intent login = new Intent(OrderBookingScreen.this, MyLoginActivity.class);
                                    startActivity(login);

                                }
                            });


                            alert.setCancelable(false);
                            alert.show();
                        }
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

                Log.e("TAG", "Time slots: " + timeSlots);

                params.put("user_id", user_id);
                params.put("pickup_lat", pickup_lat);
                params.put("pickup_lng", pickup_lng);
                params.put("pickup_detail", pickup_detail);
                params.put("destination_lat", destination_lat);
                params.put("destination_lng", destination_lng);
                params.put("destination_detail", destination_detail);
                params.put("pickup_city", pickup_city);
                params.put("destination_city", destination_city);
                params.put("car_type", car_type);
                params.put("seats", seats);
                params.put("price", price);
                params.put("time_slaut", timeSlots);
                params.put("date_slaut", mSelectedDate);


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




    private void confirmatinDialog(final String user_id, final String pickup_lat, final String pickup_lng, final String pickup_detail, final String destination_lat,
                                   final String destination_lng, final String destination_detail, final String pickup_city, final String destination_city, final String car_type,
                                   final String seats, final String price, final String timeSlots, final String selectedDateSlot) {


        final Dialog confirmationDiloag = new Dialog(OrderBookingScreen.this);

        confirmationDiloag.requestWindowFeature(Window.FEATURE_NO_TITLE);
        confirmationDiloag.setContentView(R.layout.order_confirmation_alert);
        TextView tv_description = (TextView) confirmationDiloag.findViewById(R.id.tv_description);
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
                //calling service to send data on server
                Log.e("TAG", "the selected date is: " + mSelectedDate);
                String mFareEstimation  = FareEstimation(pickup_city, destination_city, car_type, seats);


                orderBookingService(user_id, pickup_lat, pickup_lng, pickup_detail, destination_lat, destination_lng, destination_detail, pickup_city, destination_city, car_type, seats, mFareEstimation, timeSlots, mSelectedDate);

            }
        });

        confirmationDiloag.show();

    }

    private void exitScreenAlert() {

        final Dialog exitDialog = new Dialog(OrderBookingScreen.this);
        exitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        exitDialog.setContentView(R.layout.custom_dialog_exit);
        Button btExit = (Button) exitDialog.findViewById(R.id.bt_exit);
        //btOk click handler
        btExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                exitDialog.dismiss();
                finish();
            }
        });

        exitDialog.show();
    }

    private void selectDate() {


        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //preventing user to select future date
                view.setEnabled(true);

                updateLabel();
            }
        };

        tv_date_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(OrderBookingScreen.this,  date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000-1);

                datePickerDialog.show();
            }

        });


    }

    private void updateLabel() {

        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        tv_date_text.setText(sdf.format(myCalendar.getTime()));
        mSelectedDate = sdf.format(myCalendar.getTime());
    }

    @Override
    public void onBackPressed() {


        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT); //CLOSE Nav Drawer!
        }else{
            exitScreenAlert();
        }
    }

    private String FareEstimation(String spPickCity, String spDropoff, String spCartype, String spSeates){

        String mEstimateFare = "";
        //***********************************************************************************//
        //************Karachi to Hydrabad and Hydrabad to Karachi Rates *********************//
        //**********************************************************************************//

        //economy class
        if (spPickCity.equals("Karachi") && spDropoff.equals("Hyderabad") && spCartype.equals("Economy") && spSeates.equals("1")){
            mEstimateFare = "Rs.1100";
        }
        if (spPickCity.equals("Karachi") && spDropoff.equals("Hyderabad") && spCartype.equals("Economy") && spSeates.equals("2")){
            mEstimateFare = "Rs.2200";
        }
        if (spPickCity.equals("Karachi") && spDropoff.equals("Hyderabad") && spCartype.equals("Economy") && spSeates.equals("3")){
            mEstimateFare = "Rs.3300";
        }
        if (spPickCity.equals("Hyderabad") && spDropoff.equals("Karachi") && spCartype.equals("Economy") && spSeates.equals("1")){
            mEstimateFare = "Rs.1100";
        }
        if (spPickCity.equals("Hyderabad") && spDropoff.equals("Karachi") && spCartype.equals("Economy") && spSeates.equals("2")){
            mEstimateFare = "Rs.2200";
        }
        if (spPickCity.equals("Hyderabad") && spDropoff.equals("Karachi") && spCartype.equals("Economy") && spSeates.equals("3")){
            mEstimateFare = "Rs.3300";
        }

        //business class
        if (spPickCity.equals("Karachi") && spDropoff.equals("Hyderabad") && spCartype.equals("Business") && spSeates.equals("1")){
            mEstimateFare = "Rs.1500";
        }
        if (spPickCity.equals("Karachi") && spDropoff.equals("Hyderabad") && spCartype.equals("Business") && spSeates.equals("2")){
            mEstimateFare = "Rs.3000";
        }
        if (spPickCity.equals("Karachi") && spDropoff.equals("Hyderabad") && spCartype.equals("Business") && spSeates.equals("3")){
            mEstimateFare = "Rs.4500";
        }
        if (spPickCity.equals("Hyderabad") && spDropoff.equals("Karachi") && spCartype.equals("Business") && spSeates.equals("1")){
            mEstimateFare = "Rs.1500";
        }
        if (spPickCity.equals("Hyderabad") && spDropoff.equals("Karachi") && spCartype.equals("Business") && spSeates.equals("2")){
            mEstimateFare = "Rs.3000";
        }
        if (spPickCity.equals("Hyderabad") && spDropoff.equals("Karachi") && spCartype.equals("Business") && spSeates.equals("3")){
            mEstimateFare = "Rs.4500";
        }

        //***********************************************************************************//
        //************Karachi to Quetta and Quetta to Karachi Rates *********************//
        //**********************************************************************************//
        //business class
        if (spPickCity.equals("Karachi") && spDropoff.equals("Quetta") && spCartype.equals("Business") && spSeates.equals("1")){
            mEstimateFare = "Rs.3500";
        }
        if (spPickCity.equals("Karachi") && spDropoff.equals("Quetta") && spCartype.equals("Business") && spSeates.equals("2")){
            mEstimateFare = "Rs.7000";
        }
        if (spPickCity.equals("Karachi") && spDropoff.equals("Quetta") && spCartype.equals("Business") && spSeates.equals("3")){
            mEstimateFare = "Rs.10500";
        }
        if (spPickCity.equals("Karachi") && spDropoff.equals("Quetta") && spCartype.equals("Business") && spSeates.equals("4")){
            mEstimateFare = "Rs.14000";
        }
        if (spPickCity.equals("Quetta") && spDropoff.equals("Karachi") && spCartype.equals("Business") && spSeates.equals("1")){
            mEstimateFare = "Rs.3500";
        }
        if (spPickCity.equals("Quetta") && spDropoff.equals("Karachi") && spCartype.equals("Business") && spSeates.equals("2")){
            mEstimateFare = "Rs.7000";
        }
        if (spPickCity.equals("Quetta") && spDropoff.equals("Karachi") && spCartype.equals("Business") && spSeates.equals("3")){
            mEstimateFare = "Rs.10500";
        }
        if (spPickCity.equals("Quetta") && spDropoff.equals("Karachi") && spCartype.equals("Business") && spSeates.equals("4")){
            mEstimateFare = "Rs.14000";
        }


        //***********************************************************************************//
        //************Lahore to Islamabad and Islamabad to Lahore Rates *********************//
        //**********************************************************************************//

        //economy class
        if (spPickCity.equals("Lahore") && spDropoff.equals("Islamabad") && spCartype.equals("Economy") && spSeates.equals("1")){
            mEstimateFare = "Rs.2000";
        }
        if (spPickCity.equals("Lahore") && spDropoff.equals("Islamabad") && spCartype.equals("Economy") && spSeates.equals("2")){
            mEstimateFare = "Rs.4000";
        }
        if (spPickCity.equals("Lahore") && spDropoff.equals("Islamabad") && spCartype.equals("Economy") && spSeates.equals("3")){
            mEstimateFare = "Rs.6000";
        }
        if (spPickCity.equals("Islamabad") && spDropoff.equals("Lahore") && spCartype.equals("Economy") && spSeates.equals("1")){
            mEstimateFare = "Rs.2000";
        }
        if (spPickCity.equals("Islamabad") && spDropoff.equals("Lahore") && spCartype.equals("Economy") && spSeates.equals("2")){
            mEstimateFare = "Rs.4000";
        }
        if (spPickCity.equals("Islamabad") && spDropoff.equals("Lahore") && spCartype.equals("Economy") && spSeates.equals("3")){
            mEstimateFare = "Rs.6000";
        }

        //business class
        if (spPickCity.equals("Lahore") && spDropoff.equals("Islamabad") && spCartype.equals("Business") && spSeates.equals("1")){
            mEstimateFare = "Rs.2500";
        }
        if (spPickCity.equals("Lahore") && spDropoff.equals("Islamabad") && spCartype.equals("Business") && spSeates.equals("2")){
            mEstimateFare = "Rs.5000";
        }
        if (spPickCity.equals("Lahore") && spDropoff.equals("Islamabad") && spCartype.equals("Business") && spSeates.equals("3")){
            mEstimateFare = "Rs.8000";
        }
        if (spPickCity.equals("Islamabad") && spDropoff.equals("Lahore") && spCartype.equals("Business") && spSeates.equals("1")){
            mEstimateFare = "Rs.2500";
        }
        if (spPickCity.equals("Islamabad") && spDropoff.equals("Lahore") && spCartype.equals("Business") && spSeates.equals("2")){
            mEstimateFare = "Rs.5000";
        }
        if (spPickCity.equals("Islamabad") && spDropoff.equals("Lahore") && spCartype.equals("Business") && spSeates.equals("3")){
            mEstimateFare = "Rs.8000";
        }

        //***********************************************************************************//
        //************Lahore to Faisalabad and Faisalabad to Lahore Rates *********************//
        //**********************************************************************************//

        //economy class
        if (spPickCity.equals("Lahore") && spDropoff.equals("Faisalabad") && spCartype.equals("Economy") && spSeates.equals("1")){
            mEstimateFare = "Rs.1300";
        }
        if (spPickCity.equals("Lahore") && spDropoff.equals("Faisalabad") && spCartype.equals("Economy") && spSeates.equals("2")){
            mEstimateFare = "Rs.2600";
        }
        if (spPickCity.equals("Lahore") && spDropoff.equals("Faisalabad") && spCartype.equals("Economy") && spSeates.equals("3")){
            mEstimateFare = "Rs.4000";
        }
        if (spPickCity.equals("Faisalabad") && spDropoff.equals("Lahore") && spCartype.equals("Economy") && spSeates.equals("1")){
            mEstimateFare = "Rs.1300";
        }
        if (spPickCity.equals("Faisalabad") && spDropoff.equals("Lahore") && spCartype.equals("Economy") && spSeates.equals("2")){
            mEstimateFare = "Rs.2600";
        }
        if (spPickCity.equals("Faisalabad") && spDropoff.equals("Lahore") && spCartype.equals("Economy") && spSeates.equals("3")){
            mEstimateFare = "Rs.4000";
        }

        //business class
        if (spPickCity.equals("Lahore") && spDropoff.equals("Faisalabad") && spCartype.equals("Business") && spSeates.equals("1")){
            mEstimateFare = "Rs.1500";
        }
        if (spPickCity.equals("Lahore") && spDropoff.equals("Faisalabad") && spCartype.equals("Business") && spSeates.equals("2")){
            mEstimateFare = "Rs.3000";
        }
        if (spPickCity.equals("Lahore") && spDropoff.equals("Faisalabad") && spCartype.equals("Business") && spSeates.equals("3")){
            mEstimateFare = "Rs.4500";
        }
        if (spPickCity.equals("Faisalabad") && spDropoff.equals("Lahore") && spCartype.equals("Business") && spSeates.equals("1")){
            mEstimateFare = "Rs.1500";
        }
        if (spPickCity.equals("Faisalabad") && spDropoff.equals("Lahore") && spCartype.equals("Business") && spSeates.equals("2")){
            mEstimateFare = "Rs.3000";
        }
        if (spPickCity.equals("Faisalabad") && spDropoff.equals("Lahore") && spCartype.equals("Business") && spSeates.equals("3")){
            mEstimateFare = "Rs.4500";
        }


        //***********************************************************************************//
        //************Loralai to Quetta and Quetta to Loralai Rates *********************//
        //**********************************************************************************//
        //business class
        if (spPickCity.equals("Loralai") && spDropoff.equals("Quetta") && spCartype.equals("Business") && spSeates.equals("1")){
            mEstimateFare = "Rs.1500";
        }
        if (spPickCity.equals("Loralai") && spDropoff.equals("Quetta") && spCartype.equals("Business") && spSeates.equals("2")){
            mEstimateFare = "Rs.3000";
        }
        if (spPickCity.equals("Loralai") && spDropoff.equals("Quetta") && spCartype.equals("Business") && spSeates.equals("3")){
            mEstimateFare = "Rs.4500";
        }

        if (spPickCity.equals("Quetta") && spDropoff.equals("Loralai") && spCartype.equals("Business") && spSeates.equals("1")){
            mEstimateFare = "Rs.1500";
        }
        if (spPickCity.equals("Quetta") && spDropoff.equals("Loralai") && spCartype.equals("Business") && spSeates.equals("2")){
            mEstimateFare = "Rs.3000";
        }
        if (spPickCity.equals("Quetta") && spDropoff.equals("Loralai") && spCartype.equals("Business") && spSeates.equals("3")){
            mEstimateFare = "Rs.4500";
        }

        //***********************************************************************************//
        //************Zhob to Quetta and Quetta to Zhob Rates *********************//
        //**********************************************************************************//
        //business class
        if (spPickCity.equals("Zhob") && spDropoff.equals("Quetta") && spCartype.equals("Business") && spSeates.equals("1")){
            mEstimateFare = "Rs.1500";
        }
        if (spPickCity.equals("Zhob") && spDropoff.equals("Quetta") && spCartype.equals("Business") && spSeates.equals("2")){
            mEstimateFare = "Rs.3000";
        }
        if (spPickCity.equals("Zhob") && spDropoff.equals("Quetta") && spCartype.equals("Business") && spSeates.equals("3")){
            mEstimateFare = "Rs.4500";
        }

        if (spPickCity.equals("Quetta") && spDropoff.equals("Zhob") && spCartype.equals("Business") && spSeates.equals("1")){
            mEstimateFare = "Rs.1500";
        }
        if (spPickCity.equals("Quetta") && spDropoff.equals("Zhob") && spCartype.equals("Business") && spSeates.equals("2")){
            mEstimateFare = "Rs.3000";
        }
        if (spPickCity.equals("Quetta") && spDropoff.equals("Zhob") && spCartype.equals("Business") && spSeates.equals("3")){
            mEstimateFare = "Rs.4500";
        }


        //***********************************************************************************//
        //************Qila Saifullah to Quetta and Quetta to Qila Saifullah Rates *********************//
        //**********************************************************************************//
        //business class
        if (spPickCity.equals("Qila Saifullah") && spDropoff.equals("Quetta") && spCartype.equals("Business") && spSeates.equals("1")){
            mEstimateFare = "Rs.1500";
        }
        if (spPickCity.equals("Qila Saifullah") && spDropoff.equals("Quetta") && spCartype.equals("Business") && spSeates.equals("2")){
            mEstimateFare = "Rs.3000";
        }
        if (spPickCity.equals("Qila Saifullah") && spDropoff.equals("Quetta") && spCartype.equals("Business") && spSeates.equals("3")){
            mEstimateFare = "Rs.4500";
        }

        if (spPickCity.equals("Quetta") && spDropoff.equals("Qila Saifullah") && spCartype.equals("Business") && spSeates.equals("1")){
            mEstimateFare = "Rs.1500";
        }
        if (spPickCity.equals("Quetta") && spDropoff.equals("Qila Saifullah") && spCartype.equals("Business") && spSeates.equals("2")){
            mEstimateFare = "Rs.3000";
        }
        if (spPickCity.equals("Quetta") && spDropoff.equals("Qila Saifullah") && spCartype.equals("Business") && spSeates.equals("3")){
            mEstimateFare = "Rs.4500";
        }

        //***********************************************************************************//
        //************Chaman to Quetta and Quetta to Chaman Rates *********************//
        //**********************************************************************************//
        //business class
        if (spPickCity.equals("Chaman") && spDropoff.equals("Quetta") && spCartype.equals("Business") && spSeates.equals("1")){
            mEstimateFare = "Rs.1300";
        }
        if (spPickCity.equals("Chaman") && spDropoff.equals("Quetta") && spCartype.equals("Business") && spSeates.equals("2")){
            mEstimateFare = "Rs.2600";
        }
        if (spPickCity.equals("Chaman") && spDropoff.equals("Quetta") && spCartype.equals("Business") && spSeates.equals("3")){
            mEstimateFare = "Rs.3900";
        }

        if (spPickCity.equals("Quetta") && spDropoff.equals("Chaman") && spCartype.equals("Business") && spSeates.equals("1")){
            mEstimateFare = "Rs.1300";
        }
        if (spPickCity.equals("Quetta") && spDropoff.equals("Chaman") && spCartype.equals("Business") && spSeates.equals("2")){
            mEstimateFare = "Rs.2600";
        }
        if (spPickCity.equals("Quetta") && spDropoff.equals("Chaman") && spCartype.equals("Business") && spSeates.equals("3")){
            mEstimateFare = "Rs.3900";
        }

        //***********************************************************************************//
        //************Kalat to Quetta and Quetta to Kalat Rates *********************//
        //**********************************************************************************//
        //business class
        if (spPickCity.equals("Kalat") && spDropoff.equals("Quetta") && spCartype.equals("Business") && spSeates.equals("1")){
            mEstimateFare = "Rs.1700";
        }
        if (spPickCity.equals("Kalat") && spDropoff.equals("Quetta") && spCartype.equals("Business") && spSeates.equals("2")){
            mEstimateFare = "Rs.3400";
        }
        if (spPickCity.equals("Kalat") && spDropoff.equals("Quetta") && spCartype.equals("Business") && spSeates.equals("3")){
            mEstimateFare = "Rs.5000";
        }

        if (spPickCity.equals("Quetta") && spDropoff.equals("Kalat") && spCartype.equals("Business") && spSeates.equals("1")){
            mEstimateFare = "Rs.1700";
        }
        if (spPickCity.equals("Quetta") && spDropoff.equals("Kalat") && spCartype.equals("Business") && spSeates.equals("2")){
            mEstimateFare = "Rs.3400";
        }
        if (spPickCity.equals("Quetta") && spDropoff.equals("Kalat") && spCartype.equals("Business") && spSeates.equals("3")){
            mEstimateFare = "Rs.5000";
        }

        //***********************************************************************************//
        //************Khuzdar to Quetta and Quetta to Khuzdar Rates *********************//
        //**********************************************************************************//
        //business class
        if (spPickCity.equals("Khuzdar") && spDropoff.equals("Quetta") && spCartype.equals("Business") && spSeates.equals("1")){
            mEstimateFare = "Rs.3000";
        }
        if (spPickCity.equals("Khuzdar") && spDropoff.equals("Quetta") && spCartype.equals("Business") && spSeates.equals("2")){
            mEstimateFare = "Rs.6000";
        }
        if (spPickCity.equals("Khuzdar") && spDropoff.equals("Quetta") && spCartype.equals("Business") && spSeates.equals("3")){
            mEstimateFare = "Rs.9000";
        }

        if (spPickCity.equals("Quetta") && spDropoff.equals("Khuzdar") && spCartype.equals("Business") && spSeates.equals("1")){
            mEstimateFare = "Rs.3000";
        }
        if (spPickCity.equals("Quetta") && spDropoff.equals("Khuzdar") && spCartype.equals("Business") && spSeates.equals("2")){
            mEstimateFare = "Rs.6000";
        }
        if (spPickCity.equals("Quetta") && spDropoff.equals("Khuzdar") && spCartype.equals("Business") && spSeates.equals("3")){
            mEstimateFare = "Rs.9000";
        }

        //***********************************************************************************//
        //************Gwadar to Quetta and Quetta to Gwadar Rates *********************//
        //**********************************************************************************//
        //business class
        if (spPickCity.equals("Gwadar") && spDropoff.equals("Quetta") && spCartype.equals("Business") && spSeates.equals("1")){
            mEstimateFare = "Rs.5000";
        }
        if (spPickCity.equals("Gwadar") && spDropoff.equals("Quetta") && spCartype.equals("Business") && spSeates.equals("2")){
            mEstimateFare = "Rs.10000";
        }
        if (spPickCity.equals("Gwadar") && spDropoff.equals("Quetta") && spCartype.equals("Business") && spSeates.equals("3")){
            mEstimateFare = "Rs.15000";
        }

        if (spPickCity.equals("Quetta") && spDropoff.equals("Gwadar") && spCartype.equals("Business") && spSeates.equals("1")){
            mEstimateFare = "Rs.5000";
        }
        if (spPickCity.equals("Quetta") && spDropoff.equals("Gwadar") && spCartype.equals("Business") && spSeates.equals("2")){
            mEstimateFare = "Rs.10000";
        }
        if (spPickCity.equals("Quetta") && spDropoff.equals("Gwadar") && spCartype.equals("Business") && spSeates.equals("3")){
            mEstimateFare = "Rs.15000";
        }

        //***********************************************************************************//
        //************Dera Ismail Khan to Quetta and Quetta to Dera Ismail Khan Rates *********************//
        //**********************************************************************************//
        //business class
        if (spPickCity.equals("Dera Ismail Khan") && spDropoff.equals("Quetta") && spCartype.equals("Business") && spSeates.equals("1")){
            mEstimateFare = "Rs.3000";
        }
        if (spPickCity.equals("Dera Ismail Khan") && spDropoff.equals("Quetta") && spCartype.equals("Business") && spSeates.equals("2")){
            mEstimateFare = "Rs.6000";
        }
        if (spPickCity.equals("Dera Ismail Khan") && spDropoff.equals("Quetta") && spCartype.equals("Business") && spSeates.equals("3")){
            mEstimateFare = "Rs.9000";
        }

        if (spPickCity.equals("Quetta") && spDropoff.equals("Dera Ismail Khan") && spCartype.equals("Business") && spSeates.equals("1")){
            mEstimateFare = "Rs.3000";
        }
        if (spPickCity.equals("Quetta") && spDropoff.equals("Dera Ismail Khan") && spCartype.equals("Business") && spSeates.equals("2")){
            mEstimateFare = "Rs.6000";
        }
        if (spPickCity.equals("Quetta") && spDropoff.equals("Dera Ismail Khan") && spCartype.equals("Business") && spSeates.equals("3")){
            mEstimateFare = "Rs.9000";
        }

        //***********************************************************************************//
        //************Muslim Baghto Quetta and Quetta to Muslim Bagh Rates *********************//
        //**********************************************************************************//
        //business class
        if (spPickCity.equals("Muslim Bagh") && spDropoff.equals("Quetta") && spCartype.equals("Business") && spSeates.equals("1")){
            mEstimateFare = "Rs.1500";
        }
        if (spPickCity.equals("Muslim Bagh") && spDropoff.equals("Quetta") && spCartype.equals("Business") && spSeates.equals("2")){
            mEstimateFare = "Rs.3000";
        }
        if (spPickCity.equals("Muslim Bagh") && spDropoff.equals("Quetta") && spCartype.equals("Business") && spSeates.equals("3")){
            mEstimateFare = "Rs.4500";
        }

        if (spPickCity.equals("Quetta") && spDropoff.equals("Muslim Bagh") && spCartype.equals("Business") && spSeates.equals("1")){
            mEstimateFare = "Rs.1500";
        }
        if (spPickCity.equals("Quetta") && spDropoff.equals("Muslim Bagh") && spCartype.equals("Business") && spSeates.equals("2")){
            mEstimateFare = "Rs.3000";
        }
        if (spPickCity.equals("Quetta") && spDropoff.equals("Muslim Bagh") && spCartype.equals("Business") && spSeates.equals("3")){
            mEstimateFare = "Rs.4500";
        }

        //***********************************************************************************//
        //************Mastung to Quetta and Quetta to Mastung Rates *********************//
        //**********************************************************************************//
        //business class
        if (spPickCity.equals("Mastung") && spDropoff.equals("Quetta") && spCartype.equals("Business") && spSeates.equals("1")){
            mEstimateFare = "Rs.800";
        }
        if (spPickCity.equals("Mastung") && spDropoff.equals("Quetta") && spCartype.equals("Business") && spSeates.equals("2")){
            mEstimateFare = "Rs.1600";
        }
        if (spPickCity.equals("Mastung") && spDropoff.equals("Quetta") && spCartype.equals("Business") && spSeates.equals("3")){
            mEstimateFare = "Rs.2400";
        }

        if (spPickCity.equals("Quetta") && spDropoff.equals("Mastung") && spCartype.equals("Business") && spSeates.equals("1")){
            mEstimateFare = "Rs.800";
        }
        if (spPickCity.equals("Quetta") && spDropoff.equals("Mastung") && spCartype.equals("Business") && spSeates.equals("2")){
            mEstimateFare = "Rs.1600";
        }
        if (spPickCity.equals("Quetta") && spDropoff.equals("Mastung") && spCartype.equals("Business") && spSeates.equals("3")){
            mEstimateFare = "Rs.2400";
        }
        //Economy class

        if (spPickCity.equals("Mastung") && spDropoff.equals("Quetta") && spCartype.equals("Economy") && spSeates.equals("1")){
            mEstimateFare = "Rs.600";
        }
        if (spPickCity.equals("Mastung") && spDropoff.equals("Quetta") && spCartype.equals("Economy") && spSeates.equals("2")){
            mEstimateFare = "Rs.1200";
        }
        if (spPickCity.equals("Mastung") && spDropoff.equals("Quetta") && spCartype.equals("Economy") && spSeates.equals("3")){
            mEstimateFare = "Rs.1800";
        }

        if (spPickCity.equals("Quetta") && spDropoff.equals("Mastung") && spCartype.equals("Economy") && spSeates.equals("1")){
            mEstimateFare = "Rs.600";
        }
        if (spPickCity.equals("Quetta") && spDropoff.equals("Mastung") && spCartype.equals("Economy") && spSeates.equals("2")){
            mEstimateFare = "Rs.1200";
        }
        if (spPickCity.equals("Quetta") && spDropoff.equals("Mastung") && spCartype.equals("Economy") && spSeates.equals("3")){
            mEstimateFare = "Rs.1800";
        }

        //***********************************************************************************//
        //************Kuchlak to Quetta and Quetta to Kuchlak Rates *********************//
        //**********************************************************************************//
        //business class
        if (spPickCity.equals("Kuchlak") && spDropoff.equals("Quetta") && spCartype.equals("Business") && spSeates.equals("1")){
            mEstimateFare = "Rs.1000";
        }
        if (spPickCity.equals("Kuchlak") && spDropoff.equals("Quetta") && spCartype.equals("Business") && spSeates.equals("2")){
            mEstimateFare = "Rs.2000";
        }
        if (spPickCity.equals("Kuchlak") && spDropoff.equals("Quetta") && spCartype.equals("Business") && spSeates.equals("3")){
            mEstimateFare = "Rs.3000";
        }

        if (spPickCity.equals("Quetta") && spDropoff.equals("Kuchlak") && spCartype.equals("Business") && spSeates.equals("1")){
            mEstimateFare = "Rs.1000";
        }
        if (spPickCity.equals("Quetta") && spDropoff.equals("Kuchlak") && spCartype.equals("Business") && spSeates.equals("2")){
            mEstimateFare = "Rs.2000";
        }
        if (spPickCity.equals("Quetta") && spDropoff.equals("Kuchlak") && spCartype.equals("Business") && spSeates.equals("3")){
            mEstimateFare = "Rs.3000";
        }
        //Economy class

        if (spPickCity.equals("Kuchlak") && spDropoff.equals("Quetta") && spCartype.equals("Economy") && spSeates.equals("1")){
            mEstimateFare = "Rs.700";
        }
        if (spPickCity.equals("Kuchlak") && spDropoff.equals("Quetta") && spCartype.equals("Economy") && spSeates.equals("2")){
            mEstimateFare = "Rs.1400";
        }
        if (spPickCity.equals("Kuchlak") && spDropoff.equals("Quetta") && spCartype.equals("Economy") && spSeates.equals("3")){
            mEstimateFare = "Rs.2000";
        }

        if (spPickCity.equals("Quetta") && spDropoff.equals("Kuchlak") && spCartype.equals("Economy") && spSeates.equals("1")){
            mEstimateFare = "Rs.700";
        }
        if (spPickCity.equals("Quetta") && spDropoff.equals("Kuchlak") && spCartype.equals("Economy") && spSeates.equals("2")){
            mEstimateFare = "Rs.1400";
        }
        if (spPickCity.equals("Quetta") && spDropoff.equals("Kuchlak") && spCartype.equals("Economy") && spSeates.equals("3")){
            mEstimateFare = "Rs.2000";
        }

        return mEstimateFare;
    }

    public boolean isTimeGrater(String dateTime){


        Log.e("TAG", "Selected Time and date is: " + dateTime);

        boolean isTimeGrater = false;
        long currentMillis = System.currentTimeMillis();
        long availableMillis = 0;

        String stringDateTime = dateTime;
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
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

        if (availableMillis>currentMillis){

            isTimeGrater = true;
        }
        else {
            isTimeGrater = false;
        }

        return  isTimeGrater;

    }


    private void openSpinerForPickupCity(){

        ll_pickup_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sp_pickup.performClick();
            }
        });
    }


    private void openSpinerForDestinationCity(){

        ll_destination_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sp_dropoff.performClick();
            }
        });
    }

    private void openSpinerForCarType(){

        ll_car_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sp_cartype.performClick();
            }
        });
    }

    private void openSpinerForSeats(){

        ll_seats_selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sp_seates.performClick();
            }
        });
    }

    private void showDateDialog(){

        ll_date_selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {

                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        //preventing user to select future date
                        view.setEnabled(true);

                        updateLabel();
                    }
                };

                DatePickerDialog datePickerDialog = new DatePickerDialog(OrderBookingScreen.this,  date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000-1);

                datePickerDialog.show();
            }
        });
    }
}
