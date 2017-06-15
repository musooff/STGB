package com.mnm.georemider;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class MyProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView username = (TextView)findViewById(R.id.username_myProfile);
        TextView email = (TextView)findViewById(R.id.email_myProfile);
        TextView name = (TextView)findViewById(R.id.name_myProfile);
        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences("TaskData",0);
        String prof_username = sharedPreferences.getString("username","");
        String prof_name = sharedPreferences.getString("clientName","");
        String prof_eamil = sharedPreferences.getString("clientEmail","");
        username.setText("Username: "+prof_username);
        name.setText("Name: "+prof_name);
        email.setText(prof_eamil);
    }

}
