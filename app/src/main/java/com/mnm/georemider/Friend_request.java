package com.mnm.georemider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Friend_request extends AppCompatActivity {
    TextInputEditText req_friend;
    Button accept, reject;
    RecyclerView rv_friends;
    ArrayList<FriendsData> friendsDatas;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mClients = mRootRef.child("list_clients");
    DatabaseReference mFriend = mRootRef.child("clients");
    DatabaseReference mUser;
    SharedPreferences sharedPreferences;
    public static String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);
        sharedPreferences = getSharedPreferences("TaskData",0);
        accept = (Button)findViewById(R.id.friend_req_add_friend);
        reject = (Button)findViewById(R.id.friend_req_reject_friend);
        String from = sharedPreferences.getString("request_friend","0");
        rv_friends = (RecyclerView) findViewById(R.id.rv_friends);
        friendsDatas = new ArrayList<>();
        Friend_request.FriendAdapter friendAdapter = new Friend_request.FriendAdapter(this, friendsDatas);
        rv_friends.setLayoutManager(new LinearLayoutManager(this));
        rv_friends.setAdapter(friendAdapter);
        //mUser = mFriend.child(friend_id[0]); // Will replace by the name of friend with the input ID and Name
        mFriend.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String req_friend = sharedPreferences.getString("request_friend","0");
                Log.d("Friend Request: ",req_friend);
                String user = sharedPreferences.getString("username","");

                DataSnapshot mFrindNames = dataSnapshot.child(req_friend);
                //DataSnapshot userName = dataSnapshot.child(user);
                Log.d("Request UserName: ",user);
                String frindNames = mFrindNames.child("name").getValue(String.class);
                String frindIDs = req_friend;
                Log.d("After Firebase: ",frindNames);
                Log.d("After Firebase ID:",req_friend);
                if(frindNames != null){
                    friendsDatas.add(new FriendsData(frindNames, "@" + frindIDs));
                    rv_friends.getAdapter().notifyItemInserted(1);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Username not found!!!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Input ID is not exist in our system",Toast.LENGTH_SHORT).show();
            }
        });
        /*
        if(from=="0"){
            Log.d("Request Friend", "Reqeust Friend is null");
        }else{
            Log.d("Request Friend", from);
        }*/
    }
    public class FriendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


        private final Context mContext;
        private final ArrayList<FriendsData> mRecyclerViewItems;
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mClients = mRootRef.child("list_clients");
        DatabaseReference mFriend = mRootRef.child("clients");
        SharedPreferences sharedPreferences;

        DatabaseReference mUser;

        public FriendAdapter(Context context, ArrayList<FriendsData> recyclerViewItems) {
            this.mContext = context;
            this.mRecyclerViewItems = recyclerViewItems;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View adItemLayoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_friend_request, parent, false);
            return new Friend_request.FriendAdapter.FriendViewHolder(adItemLayoutView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Friend_request.FriendAdapter.FriendViewHolder friendViewHolder = (Friend_request.FriendAdapter.FriendViewHolder) holder;
            final FriendsData friendData = (FriendsData) mRecyclerViewItems.get(position);
            friendViewHolder.getName().setText(friendData.getName());
            friendViewHolder.getUsername().setText(friendData.getUsername());
            Button add_friend = ((Friend_request.FriendAdapter.FriendViewHolder) holder).getButAddFriend();
            Button reject_friend = ((Friend_request.FriendAdapter.FriendViewHolder) holder).getButRejectFriend();
            sharedPreferences = getSharedPreferences("TaskData",0);

            add_friend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFriend.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Toast.makeText(getApplicationContext(),"Adding Click",Toast.LENGTH_SHORT).show();
                            String req_friend = sharedPreferences.getString("request_friend","0");
                            String user = sharedPreferences.getString("username","");
                            //DataSnapshot friend_request = dataSnapshot.child(req_friend);
                            DataSnapshot userFriendIDs = dataSnapshot.child(user).child("friendsIDs");
                            DataSnapshot reqFriendIDs = dataSnapshot.child(req_friend).child("friendsIDs");
                            String userfrindIDs = userFriendIDs.getValue(String.class);
                            String reqfrindIDs = reqFriendIDs.getValue(String.class);
                            userfrindIDs = userfrindIDs + "," + req_friend;
                            reqfrindIDs = reqfrindIDs + "," + user;
                            mFriend.child(user).child("friendsIDs").setValue(userfrindIDs);
                            mFriend.child(req_friend).child("friendsIDs").setValue(reqfrindIDs);
                            Intent friends = new Intent(getApplicationContext(),Friends.class);
                            startActivity(friends);
                            finish();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });
            reject_friend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String req_friend = sharedPreferences.getString("request_friend","0");
                            String user = sharedPreferences.getString("username","");
                            mUser.child("Reqeust_Friend").child(req_friend).setValue("");
                            mUser.child("Request_Friend").child(user).setValue("");
                            Intent friends = new Intent(getApplicationContext(),Friends.class);
                            startActivity(friends);
                            finish();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });
        }

        @Override
        public int getItemCount() {
            return mRecyclerViewItems.size();
        }

        public class FriendViewHolder extends RecyclerView.ViewHolder {
            private TextView name;
            private TextView username;
            private Button add_friend;
            private Button reject_friend;

            public FriendViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.friend_req_name);
                username = (TextView) itemView.findViewById(R.id.friend_req_username);
                add_friend = (Button) itemView.findViewById(R.id.friend_req_add_friend);
                reject_friend = (Button) itemView.findViewById(R.id.friend_req_reject_friend);
            }
            public TextView getName(){
                return this.name;
            }
            public TextView getUsername(){
                return this.username;
            }
            public Button getButAddFriend(){
                return this.add_friend;
            }
            public Button getButRejectFriend(){
                return this.reject_friend;
            }
        }

    }
}
