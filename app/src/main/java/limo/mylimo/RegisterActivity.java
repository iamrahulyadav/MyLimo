package limo.mylimo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        overridePendingTransition(R.anim.right_to_left, R.anim.right_to_left);
    }
}
