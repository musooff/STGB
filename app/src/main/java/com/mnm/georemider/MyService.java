package com.mnm.georemider;

/**
 * Created by moshe on 26/05/2017.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyService extends Service
{
    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;

    private static final int LOCATION_INTERVAL = 600000;
    private static final float LOCATION_DISTANCE = 500;


    public String taskName;
    public String taskDescription;
    public boolean hasLocName;
    public String locAddress;
    public String locName;
    public double locLat;
    public double locLong;
    public String time;
    public boolean isFriends;
    public boolean isEntry;
    public int radius;
    public int notCount = 0;

    public boolean notified = false;
    public boolean entered = false;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    JSONObject jUser;
    JSONArray jTasks;
    JSONArray jFriendTasks;
    JSONObject jTask;

    double dis;

    private class LocationListener implements android.location.LocationListener
    {
        Location mLastLocation;

        public LocationListener(String provider)
        {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            Log.e(TAG, notCount+"");

            for (int i = 0; i<jTasks.length(); i++){
                try {
                    jTask = jTasks.getJSONObject(i);
                    dis = distance(location.getLatitude(),jTask.getDouble("locLat"),location.getLongitude(),jTask.getDouble("locLong"));
                    Log.e("dis",dis+"");
                    radius = (10)*jTask.getInt("radius");
                    if (dis <= radius && !jTask.getBoolean("entered")){ // radius becomes within a range for the first time, notify user
                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(getApplicationContext())
                                        .setSmallIcon(R.drawable.ic_task)
                                        .setContentTitle(jTask.getString("taskName"))
                                        .setContentText(jTask.getString("taskDescription"));

                        Intent each_task = new Intent(getApplicationContext(),EachTask.class);
                        each_task.putExtra("taskName",jTask.getString("taskName"));
                        each_task.putExtra("taskDesc",jTask.getString("taskDescription"));
                        each_task.putExtra("locName",jTask.getString("locName"));
                        each_task.putExtra("locAddress",jTask.getString("locAddress"));
                        each_task.putExtra("radius",(10)*jTask.getInt("radius"));
                        each_task.putExtra("time",jTask.getString("time"));
                        each_task.putExtra("entry",jTask.getBoolean("entry"));
                        each_task.putExtra("friends",jTask.getBoolean("friends"));
                        startActivity(each_task);
                        PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0, each_task, PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(intent);

                        mBuilder.setAutoCancel(true);


                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                        // notificationID allows you to update the notification later on.
                        mNotificationManager.notify(0, mBuilder.build());

                        jTask.put("entered",true);
                        Log.e(TAG,"Notified");

                    }
                    else if (dis > radius && !jTask.getBoolean("entered")){  // dis gets outside the range but he wasn't at the location, He is chilling outside
                        Log.e(TAG,"You are still outside");
                    }
                    else if (dis > radius && jTask.getBoolean("entered")){  // dis gets outside the range and he was at location, so mark him got out of location
                        jTask.put("entered",false);
                        Log.e(TAG, "Exit");

                    }
                    // else he is inside range and also has been notified, do nothing

                    String userData = jUser.toString();
                    editor.putString("userJsonData",userData);
                    editor.apply();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // iterate over friend tasks
            if (jFriendTasks.length()!=0){
                for (int j = 0; j<jFriendTasks.length(); j++){
                    try {
                        jTask = jFriendTasks.getJSONObject(j);
                        dis = distance(location.getLatitude(),jTask.getDouble("locLat"),location.getLongitude(),jTask.getDouble("locLong"));
                        Log.e("dis",dis+"");
                        radius = (10)*jTask.getInt("radius");
                        if (dis <= radius && !jTask.getBoolean("entered")){
                            NotificationCompat.Builder mBuilder =
                                    new NotificationCompat.Builder(getApplicationContext())
                                            .setSmallIcon(R.drawable.ic_task)
                                            .setContentTitle(jTask.getString("taskName"))
                                            .setContentText(jTask.getString("taskDescription"));

                            //Vibration
                            mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });

                            //LED
                            mBuilder.setLights(Color.RED, 3000, 3000);

                            //Ton
                            mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

                            Intent each_task = new Intent(getApplicationContext(),EachTask.class);
                            each_task.putExtra("name",jTask.getString("name"));
                            each_task.putExtra("taskName",jTask.getString("taskName"));
                            each_task.putExtra("taskDesc",jTask.getString("taskDescription"));
                            each_task.putExtra("locName",jTask.getString("locName"));
                            each_task.putExtra("locAddress",jTask.getString("locAddress"));
                            each_task.putExtra("" +
                                    "radius",(10)*jTask.getInt("radius"));
                            each_task.putExtra("time",jTask.getString("time"));
                            each_task.putExtra("entry",jTask.getBoolean("entry"));
                            each_task.putExtra("friends",jTask.getBoolean("friends"));
                            startActivity(each_task);
                            PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0, each_task, PendingIntent.FLAG_UPDATE_CURRENT);
                            mBuilder.setContentIntent(intent);

                            mBuilder.setAutoCancel(true);


                            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                            // notificationID allows you to update the notification later on.
                            mNotificationManager.notify(0, mBuilder.build());

                            jTask.put("entered",true);
                            jTask.put("notified",true);

                            Log.e("Notified","True");
                        }

                        else if (dis > radius && !jTask.getBoolean("entered")){  // dis gets outside the range but he wasn't at the location, He is chilling outside
                            Log.e(TAG,"Still outside");
                        }
                        else if (dis > radius && jTask.getBoolean("entered")){  // dis gets outside the range and he was at location, so mark him got out of location
                            jTask.put("entered",false);
                            Log.e(TAG, "Exit");

                        }
                        // else he is inside range and also has been notified, do nothing
                        else{
                            Log.e(TAG,"Got notification already");
                        }
                        String userData = jUser.toString();
                        editor.putString("userJsonData",userData);
                        editor.apply();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            String userData = jUser.toString();
            editor.putString("userJsonData",userData);
            editor.apply();
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);

        /*
        Bundle extras = intent.getExtras();
        taskName = extras.getString("taskName");
        taskDescription = extras.getString("taskDescription");
        hasLocName = extras.getBoolean("hasLocName");
        locName = extras.getString("locName");
        locAddress = extras.getString("locAddress");
        locLat = extras.getDouble("locLat");
        locLong = extras.getDouble("locLong");
        time = extras.getString("time");
        isFriends  = extras.getBoolean("isFriends");
        isEntry = extras.getBoolean("isEntry");
        radius = extras.getInt("radius")*10;
*/

        return START_STICKY;
    }

    @Override
    public void onCreate()
    {
        Log.e(TAG, "onCreate");
        sharedPreferences = getSharedPreferences("TaskData",0);
        editor = sharedPreferences.edit();
        editor.apply();
        try {
            jUser = new JSONObject(sharedPreferences.getString("userJsonData",""));
            jTasks = jUser.getJSONArray("tasks");
            jFriendTasks = jUser.getJSONArray("friendTasks");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2) {

        final int R = 6371000; // Radius of the earth

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // convert to meters

        return distance;
    }
}