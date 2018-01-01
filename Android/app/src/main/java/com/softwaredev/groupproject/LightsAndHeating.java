package com.softwaredev.groupproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;

import com.integreight.onesheeld.sdk.KnownShield;
import com.integreight.onesheeld.sdk.OneSheeldConnectionCallback;
import com.integreight.onesheeld.sdk.OneSheeldDevice;
import com.integreight.onesheeld.sdk.OneSheeldManager;
import com.integreight.onesheeld.sdk.OneSheeldScanningCallback;
import com.integreight.onesheeld.sdk.OneSheeldSdk;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LightsAndHeating extends AppCompatActivity {

    Switch lights;
    Switch heating;
    Button game;
    boolean light = false;
    boolean heat = false;
    OneSheeldDevice realDevice;
    ImageView homeAuto;
    ImageView profile;
    ImageView calendar;
    ImageView home;
    String patientId;
    //public final KnownShield TEXT_TO_SPEECH_SHIELD =  OneSheeldSdk.getKnownShields().getKnownShield((byte)0x23);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lights_and_heating);

        lights = (Switch)findViewById(R.id.LightSwitch);
        heating = (Switch)findViewById(R.id.HeatSwitch);
        home = (ImageView)findViewById(R.id.home);
        profile = (ImageView)findViewById(R.id.profile);
        homeAuto = (ImageView)findViewById(R.id.homeAuto);
        calendar = (ImageView)findViewById(R.id.calendar);
        //game = (Button)findViewById(R.id.gameListen);

        Intent getPatient = getIntent();
        patientId = getPatient.getStringExtra("PATIENT_ID");
        connectOneSheeld();

        lights.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!light) {
                    light = true;
                } else {
                    light = false;
                }
                realDevice.digitalWrite(13, light);
            }
        });

        heating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!heat) {
                    heat = true;
                } else {
                    heat = false;
                }

                realDevice.digitalWrite(9, false);
                realDevice.digitalWrite(8, heat);
            }
        });

        /*game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i < TEXT_TO_SPEECH_SHIELD.getKnownFunctions().size(); i++){
                    System.out.println(TEXT_TO_SPEECH_SHIELD.getKnownFunctions().get(i).getName() + "\n");
                }
                //TEXT_TO_SPEECH_SHIELD.getKnownFunctions().get(0);
                realDevice.digitalWrite(7, true);
            }
        });*/

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
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.putExtra("PATIENT_ID", patientId);
                startActivity(intent);
            }
        });

        homeAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

    }

    private void connectOneSheeld(){
        OneSheeldSdk.init(this);
        OneSheeldSdk.setDebugging(true);

        OneSheeldManager manager = OneSheeldSdk.getManager();
        manager.setConnectionRetryCount(1);
        manager.setAutomaticConnectingRetriesForClassicConnections(true);

        OneSheeldScanningCallback scanningCallback = new OneSheeldScanningCallback() {
            @Override
            public void onDeviceFind(OneSheeldDevice device) {
                OneSheeldSdk.getManager().cancelScanning();
                device.connect();
            }
        };

        OneSheeldConnectionCallback connectionCallback = new OneSheeldConnectionCallback() {
            @Override
            public void onConnect(OneSheeldDevice device) {
                realDevice = device;
            }
        };
        manager.addScanningCallback(scanningCallback);
        manager.addConnectionCallback(connectionCallback);

        manager.scan();
    }
}
