package com.softwaredev.groupproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;
import android.content.Context;

public class CreateAccount extends AppCompatActivity {

    private EditText text;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("users/Patients");
    private int i = 1;
    private String name;
    private String address;
    private String dateOfBirth;
    private String phoneNo;
    private String eircode;
    private String patientId;

    Context myContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_create_account);


        Button createPatientButton = (Button) findViewById(R.id.AddPatientButton);
        createPatientButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                text = (EditText) findViewById(R.id.PatientName);
                name = text.getText().toString();

                text = (EditText) findViewById(R.id.PatientAddress);
                address = text.getText().toString();
                text = (EditText) findViewById(R.id.PatientAddress2);
                if(!text.getText().toString().isEmpty()) address += ", " + text.getText().toString();
                text = (EditText) findViewById(R.id.PatientAddressCity);
                if(!text.getText().toString().isEmpty()) address += ", " + text.getText().toString();

                text = (EditText) findViewById(R.id.PatientDOB);
                dateOfBirth = text.getText().toString();

                text = (EditText) findViewById(R.id.PatientPhoneNo);
                phoneNo = text.getText().toString();

                text = (EditText) findViewById(R.id.PatientEircode);
                eircode = text.getText().toString();

                if(!name.isEmpty() && !address.isEmpty() && !phoneNo.isEmpty())
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot objSnapshot: snapshot.getChildren()) {
                                i++;
                            }
                            DatabaseReference patients = myRef.child("Patient" + i);
                            patientId = "Patient" + i;
                            i = 1;
                            patients.child("Name").setValue(name);
                            patients.child("Address").setValue(address);
                            patients.child("Phone Number").setValue(phoneNo);
                            patients.child("Date of Birth").setValue(dateOfBirth);
                            patients.child("Eircode").setValue(eircode);
                            patients.child("patientID").setValue(patientId);
                            patients.child("Left Geo").setValue("false");
                            Intent mainActivity = new Intent(myContext, patientHomeScreenActivity.class);
                            mainActivity.putExtra("PATIENT_ID", patientId);
                            myContext.startActivity(mainActivity);
                        }

                        public void onCancelled(DatabaseError error) {
                            // Failed to read value

                        }
                    });

            }
        });


    }

    public String getPatientId(){
        return patientId;
    }
}
