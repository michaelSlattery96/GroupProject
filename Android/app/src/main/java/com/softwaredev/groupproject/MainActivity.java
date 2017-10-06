package com.softwaredev.groupproject;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Context myContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");

        //myRef.child("123").child("username").setValue("Michael");

        //Lets the user button take you to the profile screen
        ImageButton imageButton = (ImageButton) findViewById(R.id.UserButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent profileIntent = new Intent(myContext, ProfileActivity.class);
                myContext.startActivity(profileIntent);
            }
        });

        //Used to show the date and time on home screen
        TextView textView = (TextView) findViewById(R.id.dateAndTime);
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy h:mm a");
        String dateString = sdf.format(date);
        textView.setText(dateString);

        //The profile button takes you to the profile screen
        Button profileButton = (Button) findViewById(R.id.ProfileButton);
        profileButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent profileIntent = new Intent(myContext, ProfileActivity.class);
                myContext.startActivity(profileIntent);
            }
        });

    }
}
