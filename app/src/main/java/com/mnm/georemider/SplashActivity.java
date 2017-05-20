package com.mnm.georemider;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by moshe on 20/05/2017.
 */

public class SplashActivity extends Activity {

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mClients = mRootRef.child("clients");

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        sharedPreferences = getSharedPreferences("TaskData",0);
        if (sharedPreferences.getBoolean("dataExists",false)){
            final String[] str_username = new String[1];
            final String[] str_pass = new String[1];


            mClients.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    str_username[0] = sharedPreferences.getString("username","");
                    str_pass[0] = sharedPreferences.getString("password","");

                    if (str_username[0].equals("") || str_pass[0].equals("")){
                        Toast.makeText(getApplicationContext(),"Please, enter appropriate information",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        for (DataSnapshot username : dataSnapshot.getChildren()){
                            if (username.getKey().equals(str_username[0])){
                                String pass = username.child("password").getValue(String.class);
                                if (str_pass[0].equals(pass)){
                                    Intent main = new Intent(getApplicationContext(),MainActivity.class);
                                    startActivity(main);
                                    finish();
                                }
                                else {
                                    Toast.makeText(getApplicationContext(),"Username or password is incorrect",Toast.LENGTH_SHORT).show();

                                }
                            }
                            else {
                                Toast.makeText(getApplicationContext(),"Username is not found",Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else {
            Intent login = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(login);
            finish();
        }

    }
}
