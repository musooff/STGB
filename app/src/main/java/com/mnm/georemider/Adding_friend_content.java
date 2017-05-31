package com.mnm.georemider;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class Adding_friend_content extends AppCompatActivity {

    RecyclerView rv_friends;
    ArrayList<FriendsData> friendsDatas;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mClients = mRootRef.child("list_clients");
    DatabaseReference mFriend = mRootRef.child("clients");
    DatabaseReference mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_friend_content);

        rv_friends = (RecyclerView) findViewById(R.id.rv_friends);
        friendsDatas = new ArrayList<>();
        Adding_friend_content.FriendAdapter friendAdapter = new Adding_friend_content.FriendAdapter(this, friendsDatas);
        rv_friends.setLayoutManager(new LinearLayoutManager(this));
        rv_friends.setAdapter(friendAdapter);

        mUser = mFriend.child("Test"); // Will replace by the name of friend with the input ID and Name
        mUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot mFrindNames = dataSnapshot.child("name");
                String frindIDs = "Test";
                String frindNames = mFrindNames.getValue(String.class);
                friendsDatas.add(new FriendsData(frindNames, "@" + frindIDs));
                rv_friends.getAdapter().notifyItemInserted(1);
                Toast.makeText(getApplicationContext(),frindNames,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Input ID is not exist in our system",Toast.LENGTH_SHORT).show();
            }
        });

    }
    public class FriendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


        private final Context mContext;
        private final ArrayList<FriendsData> mRecyclerViewItems;
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mClients = mRootRef.child("list_clients");
        DatabaseReference mFriend = mRootRef.child("clients");
        DatabaseReference mUser;

        public FriendAdapter(Context context, ArrayList<FriendsData> recyclerViewItems) {
            this.mContext = context;
            this.mRecyclerViewItems = recyclerViewItems;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View adItemLayoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.friend_item, parent, false);
            return new Adding_friend_content.FriendAdapter.FriendViewHolder(adItemLayoutView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Adding_friend_content.FriendAdapter.FriendViewHolder friendViewHolder = (Adding_friend_content.FriendAdapter.FriendViewHolder) holder;
            final FriendsData friendData = (FriendsData) mRecyclerViewItems.get(position);
            friendViewHolder.getName().setText(friendData.getName());
            friendViewHolder.getUsername().setText(friendData.getUsername());


            ((FriendViewHolder) holder).add_friend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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
        }

    }
}
