package com.softwaredev.groupproject;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.integreight.onesheeld.sdk.OneSheeldConnectionCallback;
import com.integreight.onesheeld.sdk.OneSheeldDevice;
import com.integreight.onesheeld.sdk.OneSheeldManager;
import com.integreight.onesheeld.sdk.OneSheeldScanningCallback;
import com.integreight.onesheeld.sdk.OneSheeldSdk;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class patientHomeScreenActivity extends AppCompatActivity{

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private TextView textView;
    private ImageView homeAuto;
    private ImageView profile;
    private ImageView calendar;
    private ImageView home;
    private String patientId;
    private ArrayList<TextView> shops = new ArrayList<>();
    private ArrayList<TextView> taxis = new ArrayList<>();
    private ArrayList<TextView> foods = new ArrayList<>();
    private ArrayList<String> savedDates = new ArrayList<>();
    private ArrayList<String> savedMessages = new ArrayList<>();
    private static final String TAG = patientHomeScreenActivity.class.getSimpleName();
    /**
     * Tracks whether the user requested to add or remove geofences, or to do neither.
     */
    private enum PendingGeofenceTask {
        ADD, REMOVE, NONE
    }

    /**
     * Provides access to the Geofencing API.
     */
    private GeofencingClient mGeofencingClient;

    /**
     * The list of geofences used in this sample.
     */
    private ArrayList<Geofence> mGeofenceList;

    /**
     * Used when requesting to add or remove geofences.
     */
    private PendingIntent mGeofencePendingIntent;

    private PendingGeofenceTask mPendingGeofenceTask = PendingGeofenceTask.NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Virtual Care Assistant");
        setContentView(R.layout.activity_patient_home_screen);
        home = (ImageView)findViewById(R.id.home);
        profile = (ImageView)findViewById(R.id.profile);
        homeAuto = (ImageView)findViewById(R.id.homeAuto);
        calendar = (ImageView)findViewById(R.id.calendar);

        // Empty list for storing geofences.
        mGeofenceList = new ArrayList<>();

        // Initially set the PendingIntent used in addGeofences() and removeGeofences() to null.
        mGeofencePendingIntent = null;

        //populateGeofenceList();

        mGeofencingClient = LocationServices.getGeofencingClient(this);

        Intent fromCreate = getIntent();
        patientId = fromCreate.getStringExtra("PATIENT_ID");

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        shops.add((TextView)findViewById(R.id.shop1));
        shops.add((TextView)findViewById(R.id.shop2));
        shops.add((TextView)findViewById(R.id.shop3));
        shops.add((TextView)findViewById(R.id.shop4));
        shops.add((TextView)findViewById(R.id.shop5));

        taxis.add((TextView)findViewById(R.id.taxi1));
        taxis.add((TextView)findViewById(R.id.taxi2));
        taxis.add((TextView)findViewById(R.id.taxi3));
        taxis.add((TextView)findViewById(R.id.taxi4));
        taxis.add((TextView)findViewById(R.id.taxi5));

        foods.add((TextView)findViewById(R.id.takeAway1));
        foods.add((TextView)findViewById(R.id.takeAway2));
        foods.add((TextView)findViewById(R.id.takeAway3));
        foods.add((TextView)findViewById(R.id.takeAway4));
        foods.add((TextView)findViewById(R.id.takeAway5));

        for(int i = 1; i <= 5; i++) {
            DatabaseReference shopNameRef = database.getReference("Phone No/Shops/Shop" + i + "/Name");
            loadData(shopNameRef, shops.get(i-1));
        }

        for(int i = 1; i <= 5; i++) {
            DatabaseReference taxiNameRef = database.getReference("Phone No/Taxis/Taxi" + i + "/Name");
            loadData(taxiNameRef, taxis.get(i-1));
        }

        for(int i = 1; i <= 5; i++) {
            DatabaseReference foodNameRef = database.getReference("Phone No/Food/Food" + i + "/Name");
            loadData(foodNameRef, foods.get(i-1));
        }

        DatabaseReference writeDateRef = database.getReference("users/Patients/" + patientId
                + "/Dates");
        DatabaseReference writeMessageRef = database.getReference("users/Patients/" + patientId
                + "/Messages");

        writeDateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    savedDates = (ArrayList<String>) dataSnapshot.getValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        writeMessageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    savedMessages = (ArrayList<String>) dataSnapshot.getValue();

                    TextView calendarDate1 = (TextView)findViewById(R.id.calendarDate1);
                    TextView calendarDate2 = (TextView)findViewById(R.id.calendarDate2);
                    TextView calendarDate3 = (TextView)findViewById(R.id.calendarDate3);
                    TextView calendarMessage1 = (TextView)findViewById(R.id.calendarMessage1);
                    TextView calendarMessage2 = (TextView)findViewById(R.id.calendarMessage2);
                    TextView calendarMessage3 = (TextView)findViewById(R.id.calendarMessage3);

                    for(int i = 0; i < 3; i++){
                        if(i == 0 && i < savedDates.size()) {
                            calendarDate1.setText(savedDates.get(i));
                            calendarMessage1.setText(savedMessages.get(i));
                        } else if(i == 1 && i < savedDates.size()){
                            calendarDate2.setText(savedDates.get(i));
                            calendarMessage2.setText(savedMessages.get(i));
                        } else if(i == 2 && i < savedDates.size()){
                            calendarDate3.setText(savedDates.get(i));
                            calendarMessage3.setText(savedMessages.get(i));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


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

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
    }

    private void loadData(DatabaseReference ref, final TextView row){
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                row.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

   // public void onStart() {
    //    super.onStart();

    //    if (!checkPermissions()) {
     //       requestPermissions();
   //     } else {
   //         performPendingGeofenceTask();
    //    }
  //  }

    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }



    /**
     * Adds geofences. This method should be called after the user has granted the location
     * permission.
     */
    /*
    @SuppressWarnings("MissingPermission")
    private void addGeofences() {
        if (!checkPermissions()) {
            //ADD log to say insufficient permissions
            return;
        }

        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnCompleteListener(this);
    }

    /**
     * Removes geofences. This method should be called after the user has granted the location
     * permission.

    @SuppressWarnings("MissingPermission")
    private void removeGeofences() {
        if (!checkPermissions()) {
            //ADD log to say insufficient permissions
            return;
        }

        mGeofencingClient.removeGeofences(getGeofencePendingIntent()).addOnCompleteListener(this);
    }

    /**
     * Runs when the result of calling {@link #addGeofences()} and/or {@link #removeGeofences()}
     * is available.
     * @param task the resulting Task, containing either a result or error.

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        mPendingGeofenceTask = PendingGeofenceTask.NONE;
        if (task.isSuccessful()) {
            updateGeofencesAdded(!getGeofencesAdded());

            Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this, task.getException());
            Log.w(TAG, errorMessage);
        }
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * This sample hard codes geofence data. A real app might dynamically create geofences based on
     * the user's location.
     */
    /*
    private void populateGeofenceList() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref1 = database.getReference("GPS/latitude.json");
        DatabaseReference ref2 = database.getReference("GPS/longitude.json");
        mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(entry.getKey())

                    // Set the circular region of this geofence.
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            Constants.GEOFENCE_RADIUS_IN_METERS
                    )

                    // Set the expiration duration of the geofence. This geofence gets automatically
                    // removed after this period of time.
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                    // Set the transition types of interest. Alerts are only generated for these
                    // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)

                    // Create the geofence.
                    .build());
        }


    /**
     * Returns true if geofences were added, otherwise false.

    private boolean getGeofencesAdded() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                Constants.GEOFENCES_ADDED_KEY, false);
    }


    private void updateGeofencesAdded(boolean added) {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean(Constants.GEOFENCES_ADDED_KEY, added)
                .apply();
    }

    /**
     * Performs the geofencing task that was pending until location permission was granted.

    private void performPendingGeofenceTask() {
        if (mPendingGeofenceTask == PendingGeofenceTask.ADD) {
            addGeofences();
        } else if (mPendingGeofenceTask == PendingGeofenceTask.REMOVE) {
            removeGeofences();
        }
    }

    /**
     * Return the current state of the permissions needed.
     */
   /*
    // private boolean checkPermissions() {
        //int permissionState = ActivityCompat.checkSelfPermission(this,
         //       Manifest.permission.ACCESS_FINE_LOCATION);
        //return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            // Request permission
            ActivityCompat.requestPermissions(patientHomeScreenActivity.this,
             new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(patientHomeScreenActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission granted.");
                performPendingGeofenceTask();
            } else {
                // Permission denied.

                mPendingGeofenceTask = PendingGeofenceTask.NONE;
            }
        }
    } */
}
