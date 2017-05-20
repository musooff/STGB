package com.mnm.georemider;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by moshe on 20/05/2017.
 */

public class Friends extends AppCompatActivity {

    RecyclerView rv_friends;
    ArrayList<FriendsData> friendsDatas;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mClients = mRootRef.child("clients");
    DatabaseReference mUser;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        Toolbar friend_toolbar = (Toolbar) findViewById(R.id.friend_toolbar);
        setSupportActionBar(friend_toolbar);

        FloatingActionButton friend_fab = (FloatingActionButton) findViewById(R.id.friend_fab);
        rv_friends = (RecyclerView)findViewById(R.id.rv_friends);
        friendsDatas = new ArrayList<>();
        FriendAdapter friendAdapter = new FriendAdapter(this,friendsDatas);
        rv_friends.setLayoutManager(new LinearLayoutManager(this));
        rv_friends.setAdapter(friendAdapter);
        

        mUser = mClients.child("musooff");
        mUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot mFriendIDs = dataSnapshot.child("friendsIDs");
                DataSnapshot mFrindNames = dataSnapshot.child("friendNames");
                String[] frindIDs = mFriendIDs.getValue(String.class).split(",");
                String[] frindNames = mFrindNames.getValue(String.class).split(",");
                for (int i = 0; i< frindIDs.length; i++){
                    friendsDatas.add(new FriendsData(frindNames[i],"@"+frindIDs[i]));
                    rv_friends.getAdapter().notifyItemInserted(i);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });        


    }

    public class FriendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


        private final Context mContext;
        private final ArrayList<FriendsData> mRecyclerViewItems;

        public FriendAdapter(Context context, ArrayList<FriendsData> recyclerViewItems){
            this.mContext = context;
            this.mRecyclerViewItems = recyclerViewItems;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View adItemLayoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.friend_item,parent,false);
            return new FriendViewHolder(adItemLayoutView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            FriendViewHolder friendViewHolder = (FriendViewHolder)holder;
            final FriendsData friendData = (FriendsData) mRecyclerViewItems.get(position);
            friendViewHolder.name.setText(friendData.getName());
            friendViewHolder.username.setText(friendData.getUsername());
        }

        @Override
        public int getItemCount() {
            return mRecyclerViewItems.size();
        }

        public class FriendViewHolder extends RecyclerView.ViewHolder{
            private TextView name;
            private TextView username;
            public FriendViewHolder(View itemView) {
                super(itemView);
                name = (TextView)itemView.findViewById(R.id.tv_name);
                username = (TextView)itemView.findViewById(R.id.tv_username);
                

            }
        }
    }

}
