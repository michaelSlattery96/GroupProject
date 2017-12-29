package com.softwaredev.groupproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private String TAG = patientHomeScreenActivity.class.getSimpleName();
    private ImageView homeAuto;
    private ImageView profile;
    private ImageView calendar;
    private ImageView home;
    private String patientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_profile);

        home = (ImageView)findViewById(R.id.home);
        profile = (ImageView)findViewById(R.id.profile);
        homeAuto = (ImageView)findViewById(R.id.homeAuto);
        calendar = (ImageView)findViewById(R.id.calendar);

        Intent fromCreate = getIntent();
        patientId = fromCreate.getStringExtra("PATIENT_ID");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users/Patients/" + patientId);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), patientHomeScreenActivity.class);
                intent.putExtra("PATIENT_ID", patientId);
                startActivity(intent);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        homeAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LightsAndHeating.class);
                intent.putExtra("PATIENT_ID", patientId);
                startActivity(intent);
            }
        });

        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
                intent.putExtra("PATIENT_ID", patientId);
                startActivity(intent);
            }
        });

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                TextView name = (TextView) findViewById(R.id.name);
                TextView dateOfBirth = (TextView) findViewById(R.id.dateOfBirth);
                TextView phoneNo = (TextView) findViewById(R.id.PhoneNo);
                TextView address = (TextView) findViewById(R.id.Address);

                name.setText(R.string.userName);
                name.append(" " + dataSnapshot.child("Name").getValue(String.class));
                dateOfBirth.setText(R.string.dateOfBirth);
                dateOfBirth.append(" " + dataSnapshot.child("Date of Birth").getValue(String.class));
                phoneNo.setText(R.string.phoneNo);
                phoneNo.append(" " + dataSnapshot.child("Phone Number").getValue(String.class));
                address.setText(R.string.address);
                address.append(" " + dataSnapshot.child("Address").getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
}
