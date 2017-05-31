package com.mnm.georemider;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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


public class Register extends AppCompatActivity {

    TextInputEditText lastName,firstName,username,email,password,con_password;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mClients = mRootRef.child("clients");
    DatabaseReference mUser ;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sharedPreferences = getSharedPreferences("TaskData",0);

        Button regButton = (Button)findViewById(R.id.register_button);
        lastName = (TextInputEditText)findViewById(R.id.lastName);
        firstName = (TextInputEditText)findViewById(R.id.firstName);
        username = (TextInputEditText)findViewById(R.id.reg_username);
        password = (TextInputEditText)findViewById(R.id.reg_password);
        con_password = (TextInputEditText)findViewById(R.id.reg_con_password);
        email = (TextInputEditText)findViewById(R.id.reg_email);
        final String[] str_username = new String[1];
        final String[] str_pass = new String[1];
        final String[] str_email = new String[1];
        final String[] last_name = new String[1];
        final String[] first_name = new String[1];
        final String[] str_con_pass = new String[1];

        regButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mClients.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        str_username[0] = username.getText().toString();
                        str_pass[0] = password.getText().toString();
                        str_email[0] = email.getText().toString();
                        last_name[0] = lastName.getText().toString();
                        first_name[0] = firstName.getText().toString();
                        str_con_pass[0] = con_password.getText().toString();
                        if (!(str_email[0].equals("") ) || !(str_pass[0].equals(""))){
                            if (!(str_username[0].equals("") )){
                                   if(!(str_pass[0].equals(str_con_pass[0]))){
                                       Toast.makeText(getApplicationContext(),"password is not match",Toast.LENGTH_SHORT).show();
                                   }  else{
                                       mUser = mClients.child(str_username[0]);
                                       mUser.child("email").setValue(str_email[0]);
                                       mUser.child("name").setValue(last_name[0]+" "+first_name[0]);
                                       mUser.child("password").setValue(str_pass[0]);
                                       mUser.child("taskCount").setValue(0);
                                       mUser.child("friendsIDs").setValue("");
                                       mUser.child("friendNames").setValue("");

                                       // Adding device token for firebase messaging
                                       mUser.child("deviceToken").setValue(sharedPreferences.getString("deviceToken",""));
                                       DatabaseReference req_friend = mClients.child("Request_Friend");
                                       req_friend.child(str_username[0]).setValue("");
                                       Intent login = new Intent(getApplicationContext(),LoginActivity.class);
                                       startActivity(login);
                                       finish();

                                   }

                            }
                        }else{

                            Toast.makeText(getApplicationContext(),"Please, enter appropriate information",Toast.LENGTH_SHORT).show();
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
