package com.mnm.georemider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by moshe on 20/05/2017.
 */

public class LoginActivity extends AppCompatActivity {

    TextInputEditText username,password;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mClients = mRootRef.child("clients");

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("TaskData",0);
        editor = sharedPreferences.edit();
        editor.apply();

        Button regButton = (Button)findViewById(R.id.email_sign_in_button);
        Button RegisterButton = (Button)findViewById(R.id.reg_front_page);
        username = (TextInputEditText)findViewById(R.id.email);
        password = (TextInputEditText)findViewById(R.id.password);
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register = new Intent(getApplicationContext(),Register.class);
                startActivity(register);
                finish();
            }
        });
        final String[] str_username = new String[1];
        final String[] str_pass = new String[1];

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClients.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        str_username[0] = username.getText().toString();
                        str_pass[0] = password.getText().toString();

                        if (str_username[0].equals("") || str_pass[0].equals("")){
                            Toast.makeText(getApplicationContext(),"Please, enter appropriate information",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            for (DataSnapshot username : dataSnapshot.getChildren()){
                                if (username.getKey().equals(str_username[0])){
                                    String pass = username.child("password").getValue(String.class);
                                    if (str_pass[0].equals(pass)){
                                        editor.putString("username",str_username[0]);
                                        editor.putString("password",str_pass[0]);
                                        editor.putString("clientName",username.child("name").getValue(String.class));

                                        editor.putBoolean("dataExists",true);
                                        editor.apply();


                                        // refresh device tokens as well
                                        String devTok = username.child("deviceToken").getValue(String.class);
                                        if (!devTok.equals(sharedPreferences.getString("deviceToken",null))){
                                            mClients.child(str_username[0]).child("deviceToken").setValue(sharedPreferences.getString("deviceToken",null));
                                            Toast.makeText(getApplicationContext(),"devideToken was updated",Toast.LENGTH_SHORT).show();
                                        }


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
        });
    }
}
