package com.softwaredev.groupproject;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class patientHomeScreenActivity extends AppCompatActivity{

    Context myContext = this;
    TextView textView;
    ImageView homeAuto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Virtual Care Assistant");
        setContentView(R.layout.activity_patient_home_screen);
        homeAuto = (ImageView)findViewById(R.id.homeAuto);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");

        //myRef.child("Carers").child("carer").setValue("Michael");

        //Used to show the date and time on home screen
        textView = (TextView) findViewById(R.id.dateAndTime);

        final Handler someHandler = new Handler(getMainLooper());
        someHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                textView.setText(new SimpleDateFormat("dd/MMM/yyyy h:mm a",
                        Locale.UK).format(new Date()));
                someHandler.postDelayed(this, 1000);
            }
        }, 10);

        homeAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LightsAndHeating.class);
                startActivity(intent);
            }
        });

    }
}
