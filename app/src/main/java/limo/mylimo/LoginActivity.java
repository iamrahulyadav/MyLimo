package limo.mylimo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

import limo.mylimo.R;

public class LoginActivity extends AppCompatActivity {

    TextView tv_signup;
    Button btLogin;
    EditText et_email, et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        openingSignUpScreen();
        loginClick();




    }

    public void init(){

        tv_signup = (TextView) findViewById(R.id.tv_signup);
        btLogin = (Button) findViewById(R.id.bt_login);
        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
    }

    public void openingSignUpScreen()
    {

        tv_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent signUpActivity = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(signUpActivity);

            }
        });
    }

    public void loginClick()
    {

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent signUpActivity = new Intent(LoginActivity.this, OrderBooking.class);
                startActivity(signUpActivity);

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.left_to_right, R.anim.left_to_right);
    }


}
