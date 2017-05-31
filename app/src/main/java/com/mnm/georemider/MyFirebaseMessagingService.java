package com.mnm.georemider;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.R.id.message;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    Handler mHandler=new Handler();

    String message;

    JSONObject jUser;
    JSONArray jFriendTasks;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        message = remoteMessage.getNotification().getBody();
        Log.d(TAG, "Notification Message Body: " + message);
        try {
            runa();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    public void  runa() throws Exception{
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MyFirebaseMessagingService.this, "One of your friend shared new task with you", Toast.LENGTH_LONG).show();
                //when message received just get last added task from the friend tasks and add to phone jUserData


                sharedPreferences = getSharedPreferences("TaskData",0);
                editor = sharedPreferences.edit();
                editor.apply();
                DatabaseReference mClients = FirebaseDatabase.getInstance().getReference();
                DatabaseReference mUser = mClients.child(sharedPreferences.getString("username",""));

                try {
                    jUser = new JSONObject(sharedPreferences.getString("userJsonData",""));
                    jFriendTasks = jUser.getJSONArray("friendTasks");
                    mUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            long friendTaskCount = dataSnapshot.child("friendTaskCount").getValue(Long.class);
                            DataSnapshot child = dataSnapshot.child("Friend TASK: "+friendTaskCount);

                            JSONObject jTask = new JSONObject();
                            try {
                                jTask.put("name",child.child("name").getValue(String.class));
                                jTask.put("taskName",child.child("taskName").getValue(String.class));
                                jTask.put("taskDescription",child.child("taskDescription").getValue(String.class));
                                jTask.put("hasLocName",child.child("hasLocName").getValue(Boolean.class));
                                jTask.put("locName",child.child("locName").getValue(String.class));
                                jTask.put("locAddress",child.child("locAddress").getValue(String.class));
                                jTask.put("locLat",child.child("locLat").getValue(Double.class));
                                jTask.put("locLong",child.child("locLong").getValue(Double.class));
                                jTask.put("time",child.child("time").getValue(String.class));
                                jTask.put("friends",child.child("friends").getValue(Boolean.class));
                                jTask.put("radius",child.child("radius").getValue(Integer.class));
                                jTask.put("entry",child.child("entry").getValue(Boolean.class));


                                //for entering
                                jTask.put("entered",false);
                                jTask.put("notified",false);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            jFriendTasks.put(jTask);
                            String userData = jUser.toString();
                            editor.putString("userJsonData",userData);
                            editor.apply();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}