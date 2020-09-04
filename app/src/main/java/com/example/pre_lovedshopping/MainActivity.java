package com.example.pre_lovedshopping;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.pre_lovedshopping.Session.SessionManager;

public class MainActivity extends AppCompatActivity {

    Button btn_profile;
    TextView textView;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);

        textView = (TextView) findViewById(R.id.welcome);
        sessionManager = new SessionManager(getApplicationContext());
        sessionManager.checkLogin();
    }
}