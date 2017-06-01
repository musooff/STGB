package com.mnm.georemider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import static android.content.ContentValues.TAG;

public class AddingFriend extends AppCompatActivity {
    TextInputEditText search_friend;
    Button search;
    RecyclerView rv_friends;
    ArrayList<FriendsData> friendsDatas;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mClients = mRootRef.child("list_clients");
    DatabaseReference mFriend = mRootRef.child("clients");
    DatabaseReference mUser;
    public static String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_friend);
        search_friend = (TextInputEditText)findViewById(R.id.friend_id);
        search = (Button)findViewById(R.id.search_friend);
        final String[] friend_id = new String[1];
        rv_friends = (RecyclerView) findViewById(R.id.rv_friends);
        friendsDatas = new ArrayList<>();
        AddingFriend.FriendAdapter friendAdapter = new AddingFriend.FriendAdapter(this, friendsDatas);
        rv_friends.setLayoutManager(new LinearLayoutManager(this));
        rv_friends.setAdapter(friendAdapter);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friend_id[0] = search_friend.getText().toString();
                mUser = mFriend.child(friend_id[0]); // Will replace by the name of friend with the input ID and Name
                mUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        DataSnapshot mFrindNames = dataSnapshot.child("name");
                        String frindIDs = friend_id[0];
                        name = friend_id[0];
                        String frindNames = mFrindNames.getValue(String.class);
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
                Searching Database here and restrive the information
                set display information with listView
                with button add friend
                */

            }
        });


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
                    .inflate(R.layout.activity_adding_friend_item, parent, false);
            return new AddingFriend.FriendAdapter.FriendViewHolder(adItemLayoutView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            AddingFriend.FriendAdapter.FriendViewHolder friendViewHolder = (AddingFriend.FriendAdapter.FriendViewHolder) holder;
            final FriendsData friendData = (FriendsData) mRecyclerViewItems.get(position);
            friendViewHolder.getName().setText(friendData.getName());
            friendViewHolder.getUsername().setText(friendData.getUsername());
            Button add_friend = ((FriendViewHolder) holder).add_friend;
            sharedPreferences = getSharedPreferences("TaskData",0);

            add_friend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mUser = mFriend.child("Request_Friend");
                    mUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Toast.makeText(getApplicationContext(),"Adding Click",Toast.LENGTH_SHORT).show();
                            String req_friend = friendData.getUsername().replace("@","");
                            DataSnapshot friend_request = dataSnapshot.child(req_friend);
                            String friend_req_exits = friend_request.getValue(String.class);
                            if(friend_req_exits.equals("")){
                                friend_req_exits = sharedPreferences.getString("username","0");
                            }else{
                                friend_req_exits = friend_req_exits + "," + sharedPreferences.getString("username","0");
                            }
                            mUser.child(req_friend).setValue(friend_req_exits);
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

            public FriendViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.tv_name);
                username = (TextView) itemView.findViewById(R.id.tv_username);
                add_friend = (Button) itemView.findViewById(R.id.add_friend);

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
        }

    }
}
