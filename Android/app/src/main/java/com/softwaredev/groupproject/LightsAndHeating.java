package com.softwaredev.groupproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LightsAndHeating extends AppCompatActivity {

    Switch lights;
    Switch heating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lights_and_heating);

        lights = (Switch)findViewById(R.id.LightSwitch);
        heating = (Switch)findViewById(R.id.HeatSwitch);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference patientRef = database.getReference("users/Patients/Patient2");

        lights.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                patientRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child("Lights").getValue(String.class).equals("off")) {
                            patientRef.child("Lights").setValue("on");
                        } else {
                            patientRef.child("Lights").setValue("off");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println(databaseError);
                    }
                });
            }
        });

        heating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                patientRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child("Heating").getValue(String.class).equals("off")) {
                            patientRef.child("Heating").setValue("on");
                        } else {
                            patientRef.child("Heating").setValue("off");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println(databaseError);
                    }
                });
            }
        });
    }
}
