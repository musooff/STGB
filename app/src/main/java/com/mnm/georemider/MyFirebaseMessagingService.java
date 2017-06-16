package com.mnm.georemider;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationBuilderWithBuilderAccessor;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
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
        //Log.d(TAG, "From: " + remoteMessage.getFrom());
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        message = remoteMessage.getNotification().getBody();
        Log.d(TAG, "Notification Message Body: " + message);

        if (message.equals("chatting")){
            String title = remoteMessage.getNotification().getTitle();
            String action_click = remoteMessage.getNotification().getClickAction();
            openChatting(title,action_click);
        }


        else if(message.equals("body")){
            try {
                runa();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        else if (message.equals("request_friend")){
            String to = remoteMessage.getNotification().getTitle();
            String from =remoteMessage.getNotification().getSound();
            Log.d("Request Friend on Log",from);
            if(!from.equals("default")){
                Log.d("Request Friend on Log","Test");
                DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference mClients = mRootRef.child("list_clients");
                DatabaseReference mFriend = mRootRef.child("Request_Friend");
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                builder.setSmallIcon(R.drawable.icon_friends);
                sharedPreferences = getSharedPreferences("TaskData",0);
                editor = sharedPreferences.edit();
                editor.putString("request_friend",from);
                editor.apply();
                builder.setContentTitle("Friend Request");
                builder.setContentText("You have friend request from: "+from);
                Intent friend_req = new Intent(this,Friend_request.class);
                TaskStackBuilder stackbuilder = TaskStackBuilder.create(this);
                stackbuilder.addParentStack(Friend_reqest_items.class);
                stackbuilder.addNextIntent(friend_req);
                PendingIntent pedding = stackbuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pedding);
                builder.setAutoCancel(true);
                NotificationManager NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                NM.notify(0,builder.build());
            }
            else{
                Log.d("Friend Request Name: ","Empty");
            }
        }

    }

    private void openChatting(String title,String click_action) {

        /*Intent chatting = new Intent();
        chatting.setAction(click_action);
        //chatting.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        chatting.putExtra("toWhom",title);
        chatting.putExtra("message","chillin");
        chatting.putExtra("coming","notification");
        startActivity(chatting);
        */
        sharedPreferences = getSharedPreferences("TaskData",0);
        editor = sharedPreferences.edit();
        editor.putString("coming","notification");
        editor.putString("toWhom",title);
        editor.putString("message","chillin");
        editor.apply();

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_task)
                        .setContentTitle("New message")
                        .setContentText(title);

        //Vibration
        mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });

        //LED
        mBuilder.setLights(Color.RED, 3000, 3000);

        //Ton
        mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        Intent each_task = new Intent(getApplicationContext(),ChatActivity.class);
        each_task.putExtra("toWhom",title);
        each_task.putExtra("message","chillin");
        each_task.putExtra("coming","notification");

        each_task.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0, each_task, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(intent);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        startActivity(each_task);

        // notificationID allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());



        //Log.e("kjsnkjdf","kajsndkfjansdkjfnakjsdf");
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