package com.example.classroom.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.example.classroom.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        auth = FirebaseAuth.getInstance();
//        firebaseUser = auth.getCurrentUser();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                if (firebaseUser != null) {
//                    startActivity(new Intent(getBaseContext(), HomeActivity.class));
//                    finish();

                    startActivity(new Intent(getBaseContext(), SignUpActivity.class));
                    finish();

            }
        }, 2000);
    }
}