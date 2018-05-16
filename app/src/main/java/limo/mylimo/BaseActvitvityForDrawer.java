package limo.mylimo;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.design.widget.NavigationView;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import limo.mylimo.R;


public class  BaseActvitvityForDrawer extends AppCompatActivity {

    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    MenuItem navLoginRegister;
    MenuItem navUsername;
    MenuItem navPaymentMethods;
    // MenuItem navChosePlane;
    // MenuItem navFranchiser;
    MenuItem navContactUs;
    MenuItem navViewYourProperties;
    MenuItem navBuyerActvity;
    MenuItem navShowFranchiserList;
    MenuItem navTermsAndCondtion;
    MenuItem navShareApp;
    MenuItem navLiveSupport;
    MenuItem navHelp;
    MenuItem navChatRoom;


    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initialize facebook sdk
       /* FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();*/
        setContentView(R.layout.activity_base_actvitvity_for_drawer);
        view = new View(this);




        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff) ;
        mNavigationView.setItemIconTintList(null);

        // get menu from navigationView
        Menu menu = mNavigationView.getMenu();

        // find MenuItem you want to change
        navUsername = menu.findItem(R.id.nav_item_home);
        navPaymentMethods = menu.findItem(R.id.nav_payment_method);


        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();


                if (menuItem.getItemId() == R.id.nav_item_home) {
                    //home activity
                    //Toast.makeText(getApplicationContext(), "DashBoard", Toast.LENGTH_SHORT).show();

                }

                if (menuItem.getItemId() == R.id.nav_item_my_orders) {

                    Intent i = new Intent(BaseActvitvityForDrawer.this, MyHistoryScreen.class);
                    startActivity(i);


                }
                if (menuItem.getItemId() == R.id.nav_payment_method){

                    //Openning Screen of Payments
                    Intent i = new Intent(BaseActvitvityForDrawer.this, PaymentMethod.class);
                    startActivity(i);

                }
                if (menuItem.getItemId() == R.id.nav_item_about){

                    Intent i = new Intent(BaseActvitvityForDrawer.this, AboutUs.class);
                    startActivity(i);

                }
                if (menuItem.getItemId() == R.id.nav_item_contact_us){

                    Intent i = new Intent(BaseActvitvityForDrawer.this, ContactUs.class);
                    startActivity(i);

                }

             /*   if (menuItem.getItemId() == R.id.feedback){

                    Intent i = new Intent(BaseActvitvityForDrawer.this, Feedback.class);
                    startActivity(i);

                }*/

                return false;
            }

        });

        /**
         * Setup Drawer Toggle of the Toolbar
         */

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, toolbar,R.string.app_name,
                R.string.app_name);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();



    }//end on Create

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getSharedPreferences("mylimouser", 0);
       String fullname  = sharedPreferences.getString("fullname", null);
        navUsername.setTitle(fullname);

    }







}