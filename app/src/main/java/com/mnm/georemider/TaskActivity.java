package com.mnm.georemider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Service;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.IntegerRes;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by moshe on 13/05/2017.
 */

public class TaskActivity extends AppCompatActivity {

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mClients = mRootRef.child("clients");
    DatabaseReference mUser ;

    TextInputEditText et_name, et_description;
    CardView cv_current, cv_map, cv_edit, cv_minus, cv_plus;
    SeekBar sb_radius;

    TextView tv_radius,tv_everyTime, tv_locationName,tv_logLocation;
    static TextView tv_date_from;
    static TextView tv_date_to;
    static TextView tv_hour_from;
    static TextView tv_hour_to;
    ImageView iv_everyTime;
    RadioButton rb_entry, rb_exit, rb_private, rb_friends;
    Switch sw_everyTime;

    int radius = 30;
    boolean isFriends;
    boolean isEntry = true;
    boolean hasName = false;

    String taskName = "";
    String taskDescription = "";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String str_dates = "Always";
    String str_times = "Always";

    LinearLayout ll_date,ll_locationText;

    GPSTracker gps;
    double latitude,longitude;
    String str_locName,str_logLocation;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        Toolbar task_toolbar = (Toolbar) findViewById(R.id.task_toolbar);
        setSupportActionBar(task_toolbar);

        et_name = (TextInputEditText) findViewById(R.id.et_name);
        et_description = (TextInputEditText) findViewById(R.id.et_description);

        cv_current = (CardView) findViewById(R.id.cv_current);
        cv_map = (CardView) findViewById(R.id.cv_map);
        cv_edit = (CardView) findViewById(R.id.cv_edit);
        cv_minus = (CardView) findViewById(R.id.cv_minus);
        cv_plus = (CardView) findViewById(R.id.cv_plus);

        sb_radius = (SeekBar) findViewById(R.id.sb_radius);

        tv_radius = (TextView) findViewById(R.id.tv_radius);
        tv_everyTime = (TextView) findViewById(R.id.tv_everytime);
        tv_date_from = (TextView) findViewById(R.id.tv_date_from);
        tv_date_to = (TextView) findViewById(R.id.tv_date_to);
        tv_hour_from = (TextView) findViewById(R.id.tv_hours_from);
        tv_hour_to = (TextView) findViewById(R.id.tv_hours_to);
        tv_locationName = (TextView) findViewById(R.id.tv_location);
        tv_logLocation = (TextView) findViewById(R.id.tv_logLocation);


        rb_entry = (RadioButton) findViewById(R.id.rb_entry);
        rb_exit = (RadioButton) findViewById(R.id.rb_exit);
        rb_private = (RadioButton) findViewById(R.id.rb_private);
        rb_friends = (RadioButton) findViewById(R.id.rb_friends);

        sw_everyTime = (Switch) findViewById(R.id.sw_everytime);

        ll_date = (LinearLayout) findViewById(R.id.ll_date);
        ll_locationText = (LinearLayout) findViewById(R.id.ll_locationText);

        iv_everyTime = (ImageView) findViewById(R.id.iv_everytime);


        final FloatingActionButton task_fab = (FloatingActionButton) findViewById(R.id.task_fab);

        cv_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radius > 10) {
                    radius -= 10;
                    sb_radius.setProgress(radius);
                    tv_radius.setText("Radius: " + radius + "0 m");

                }
            }
        });

        cv_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radius < 100) {
                    radius += 10;
                    sb_radius.setProgress(radius);
                    if (radius == 100) {
                        tv_radius.setText("Radius: 1 km");

                    } else {
                        tv_radius.setText("Radius: " + radius + "0 m");
                    }

                }
            }
        });


        tv_radius.setText("Radius: " + radius + "0 m");

        sb_radius.setProgress(radius);

        sb_radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 100) {
                    radius = 100;
                    tv_radius.setText("Radius: 1 km");
                } else {
                    if (progress >= 10) {
                        radius = progress - progress % 10;
                        tv_radius.setText("Radius: " + radius + "0 m");
                    } else {
                        sb_radius.setProgress(10);
                        radius = 10;
                        tv_radius.setText("Radius: 100 m");

                    }

                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sw_everyTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    ll_date.setVisibility(View.VISIBLE);
                    iv_everyTime.setImageResource(R.drawable.ic_alltime_off_30_5);
                    tv_everyTime.setAlpha((float) 0.5);
                } else {
                    ll_date.setVisibility(View.GONE);
                    iv_everyTime.setImageResource(R.drawable.ic_alltime_on_30_5);
                    tv_everyTime.setAlpha((float) 1);
                }
            }
        });


        task_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskName = et_name.getText().toString();
                taskDescription = et_description.getText().toString();
                if (!sw_everyTime.isChecked()) {
                    str_dates = tv_date_from.getText().toString();
                }
                if (taskName.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please, enter task name", Toast.LENGTH_SHORT).show();
                } else {


                    sharedPreferences = getSharedPreferences("TaskData", 0);
                    editor = sharedPreferences.edit();

                    mUser = mClients.child(sharedPreferences.getString("username",null));
                    createdTimestamp = ServerValue.TIMESTAMP;
                    DatabaseReference taskCount  = mUser.child("taskCount");
                    taskCount.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            long count = dataSnapshot.getValue(Long.class);
                            count++;
                            DatabaseReference newTask = mUser.child("TASK: "+count);
                            newTask.child("taskName").setValue(taskName);
                            newTask.child("taskDescription").setValue(taskDescription);
                            newTask.child("hasLocName").setValue(hasName);
                            newTask.child("locName").setValue(str_locName);
                            newTask.child("locAddress").setValue(str_logLocation);
                            newTask.child("locLat").setValue(latitude);
                            newTask.child("locLong").setValue(longitude);
                            newTask.child("time").setValue(str_dates);
                            newTask.child("friends").setValue(isFriends);
                            newTask.child("radius").setValue(radius);
                            newTask.child("entry").setValue(isEntry);
                            mUser.child("taskCount").setValue(count);

                            editor.putString("name", "TASK: "+count);

                            editor.putBoolean("newTask", true);
                            editor.apply();


                            // if its amoung friends add to friend tasks as well
                            if (isFriends){

                                DatabaseReference mFriends = mUser.child("friendsIDs");
                                mFriends.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final String[] friendIDs = dataSnapshot.getValue(String.class).split(",");
                                        for (int j = 0; j < friendIDs.length; j++){
                                            final DatabaseReference friend = mClients.child(friendIDs[j]);
                                            final int finalJ = j;
                                            friend.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    long count = dataSnapshot.child("friendTaskCount").getValue(Long.class);
                                                    count++;

                                                    DatabaseReference friendTask = friend.child("Friend TASK: "+count);
                                                    friendTask.child("name").setValue(mUser.getKey());
                                                    friendTask.child("taskName").setValue(taskName);
                                                    friendTask.child("taskDescription").setValue(taskDescription);
                                                    friendTask.child("hasLocName").setValue(hasName);
                                                    friendTask.child("locName").setValue(str_locName);
                                                    friendTask.child("locAddress").setValue(str_logLocation);
                                                    friendTask.child("locLat").setValue(latitude);
                                                    friendTask.child("locLong").setValue(longitude);
                                                    friendTask.child("time").setValue(str_dates);
                                                    friendTask.child("friends").setValue(isFriends);
                                                    friendTask.child("radius").setValue(radius);
                                                    friendTask.child("entry").setValue(isEntry);
                                                    friend.child("friendTaskCount").setValue(count);


                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }











                            Intent service = new Intent(getApplicationContext(),MyService.class);
                            service.putExtra("taskName",taskName);
                            service.putExtra("taskDescription",taskDescription);
                            service.putExtra("hasLocName",hasName);
                            service.putExtra("locName",str_locName);
                            service.putExtra("locAddress",str_logLocation);
                            service.putExtra("locLat",latitude);
                            service.putExtra("locLong",longitude);
                            service.putExtra("time",str_dates);
                            service.putExtra("friends",isFriends);
                            service.putExtra("radius",radius);
                            service.putExtra("entry",isEntry);


                            startService(service);
                            onBackPressed();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
            }
        });

        tv_hour_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                Bundle args = new Bundle();
                args.putString("to_from", "from");
                newFragment.setArguments(args);
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });
        tv_hour_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                Bundle args = new Bundle();
                args.putString("to_from", "to");
                newFragment.setArguments(args);
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });

        tv_date_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                Bundle args = new Bundle();
                args.putString("to_from", "to");
                newFragment.setArguments(args);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        tv_date_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                Bundle args = new Bundle();
                args.putString("to_from", "from");
                newFragment.setArguments(args);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        cv_current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gps = new GPSTracker(TaskActivity.this);

                if(gps.canGetLocation()) {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                    str_locName = "Current Location";
                    str_logLocation = getCompleteAddressString(latitude,longitude);
                    tv_locationName.setText(str_locName);
                    tv_logLocation.setText(str_logLocation);
                    ll_locationText.setVisibility(View.VISIBLE);
                } else {
                    gps.showSettingsAlert();
                }
            }
        });

        cv_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                try {
                    Intent intent = intentBuilder.build(TaskActivity.this);
                    startActivityForResult(intent,1);

                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
        cv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int PLACE_AUTOCOMPLETE_REQUEST_CODE = 2;
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(TaskActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });




    }
    //member variable
    Object createdTimestamp;


    @Exclude
    public long getCreatedTimestampLong(){
        return (long)createdTimestamp;
    }
    @Exclude
    public String getCreatedTimestampString(){
        return (String) createdTimestamp;
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.rb_exit:
                if (!checked)
                    rb_exit.setChecked(true);
                rb_entry.setChecked(false);
                isEntry = false;
                break;
            case R.id.rb_entry:
                if (!checked)
                    rb_entry.setChecked(true);
                rb_exit.setChecked(false);
                isEntry = true;
                break;
            case R.id.rb_friends:
                if (!checked)
                    rb_friends.setChecked(true);
                rb_private.setChecked(false);
                isFriends = true;
                break;
            case R.id.rb_private:
                if (!checked)
                    rb_private.setChecked(true);
                rb_friends.setChecked(false);
                isFriends = false;
                break;

        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            Bundle args = getArguments();
            if (args.getString("to_from").equals("to")) {
                StringBuilder time = new StringBuilder();
                if (hourOfDay > 12) time.append(hourOfDay % 12);
                else time.append(hourOfDay);
                time.append(":");
                if (minute < 10) time.append("0" + minute);
                else time.append(minute);
                if (hourOfDay > 12) time.append(" pm");
                else time.append(" am");
                tv_hour_to.setText(time.toString());
            } else {
                StringBuilder time = new StringBuilder();
                if (hourOfDay > 12) time.append(hourOfDay % 12);
                else time.append(hourOfDay);
                time.append(":");
                if (minute < 10) time.append("0" + minute);
                else time.append(minute);
                if (hourOfDay > 12) time.append(" pm");
                else time.append(" am");
                tv_hour_from.setText(time.toString());
            }
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
            Date date = new Date(year, month, day);
            Bundle args = getArguments();
            if (args.getString("to_from").equals("to")) {
                tv_date_to.setText(dateFormat.format(date));
            } else {
                tv_date_from.setText(dateFormat.format(date));

            }
        }
    }

    public class GPSTracker extends Service implements LocationListener {

        private final Context context;

        boolean isGPSEnabled = false;
        boolean isNetworkEnabled = false;
        boolean canGetLocation = false;

        Location location;

        double latitude;
        double longitude;

        private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
        private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

        protected LocationManager locationManager;

        public GPSTracker(Context context) {
            this.context = context;
            getLocation();
        }

        public Location getLocation() {
            try {
                locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

                isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (!isGPSEnabled && !isNetworkEnabled) {

                } else {
                    this.canGetLocation = true;

                    if (isNetworkEnabled) {

                        if (ActivityCompat.checkSelfPermission(TaskActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TaskActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return null;
                        }
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                            if (location != null) {

                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }

                    }

                    if(isGPSEnabled) {
                        if(location == null) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                            if(locationManager != null) {
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                                if(location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return location;
        }


        public void stopUsingGPS() {
            if(locationManager != null) {
                locationManager.removeUpdates(this);
            }
        }

        public double getLatitude() {
            if(location != null) {
                latitude = location.getLatitude();
            }
            return latitude;
        }

        public double getLongitude() {
            if(location != null) {
                longitude = location.getLongitude();
            }

            return longitude;
        }

        public boolean canGetLocation() {
            return this.canGetLocation;
        }

        public void showSettingsAlert() {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

            alertDialog.setTitle("GPS is settings");

            alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(intent);
                }
            });

            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            alertDialog.show();
        }

        @Override
        public void onLocationChanged(Location arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderDisabled(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public IBinder onBind(Intent intent) {
            // TODO Auto-generated method stub
            return null;
        }

    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i));
                    if (!(i+1 == returnedAddress.getMaxAddressLineIndex())){
                        strReturnedAddress.append(", ");
                    }
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current address", "" + strReturnedAddress.toString());
            } else {
                Log.w("My Current address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current address", "Canont get Address!");
        }
        return strAdd;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK){
            setAddress(data);
        }
        if (requestCode == 2 && resultCode == RESULT_OK){
            setAddress(data);
        }
    }

    private void setAddress(Intent data) {
        Place place = PlacePicker.getPlace(data,this);
        str_locName = place.getName().toString();
        str_logLocation = place.getAddress().toString();
        latitude = place.getLatLng().latitude;
        longitude = place.getLatLng().longitude;
        if (!isInteger(str_locName.substring(0,1))){
            hasName = true;
        }
        tv_locationName.setText(str_locName);
        tv_logLocation.setText(str_logLocation);
        ll_locationText.setVisibility(View.VISIBLE);
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }
}
