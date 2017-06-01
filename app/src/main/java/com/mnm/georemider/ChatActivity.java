package com.mnm.georemider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by moshe on 01/06/2017.
 */

public class ChatActivity extends Activity {

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mChats = mRootRef.child("chats");
    DatabaseReference mCHAT;
    DatabaseReference mChatCount = mChats.child("chatCount");
    DatabaseReference mMessageCount;
    DatabaseReference mMessage;

    SharedPreferences sharedPreferences;

    String username;
    String toWhom;
    
    RecyclerView rv_messages;
    ArrayList<ChatMessage> chatMessages;
    TextInputEditText tv_input;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        sharedPreferences = getSharedPreferences("TaskData",0);
        username = sharedPreferences.getString("username","");

        final Bundle extras = getIntent().getExtras();
        toWhom = extras.getString("toWhom");
        String coming = extras.getString("coming");
        if (coming.equals("private")){
            mChatCount.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long chatCount = dataSnapshot.getValue(Long.class);
                    chatCount++;
                    mChatCount.setValue(chatCount);
                    mCHAT = mChats.child("CHAT "+chatCount);
                    mMessageCount = mCHAT.child("messageCount");
                    newMessage(extras.getString("message"));

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else if (coming.equals("notification")){
            mChatCount.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long chatCount = dataSnapshot.getValue(Long.class);
                    mCHAT = mChats.child("CHAT "+chatCount);
                    mMessageCount = mCHAT.child("messageCount");
                    mMessageCount.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            long messageCount = dataSnapshot.getValue(Long.class);
                            mMessage = mCHAT.child("MESSAGE "+messageCount);
                            mMessage.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String to = dataSnapshot.child("to").getValue(String.class);
                                    String from = dataSnapshot.child("from").getValue(String.class);
                                    String message = dataSnapshot.child("message").getValue(String.class);
                                    long time = dataSnapshot.child("time").getValue(Long.class);

                                    chatMessages.add(new ChatMessage(to,from,message,time));
                                    rv_messages.getAdapter().notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


        FloatingActionButton fab =
                (FloatingActionButton)findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TextInputEditText input = (TextInputEditText)findViewById(R.id.input);

                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                //newMessage(input.getText().toString());
                mMessageCount.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long messageCount = 1;
                        if (!dataSnapshot.exists()){
                            mMessageCount.setValue(1);
                        }
                        else {
                            messageCount = dataSnapshot.getValue(Long.class);
                            messageCount++;
                            mMessageCount.setValue(messageCount);
                        }

                        mCHAT.child("MESSAGE "+ messageCount).child("from").setValue(username);
                        mCHAT.child("MESSAGE "+ messageCount).child("to").setValue(toWhom);
                        mCHAT.child("MESSAGE "+ messageCount).child("message").setValue(input.getText().toString());
                        mCHAT.child("MESSAGE "+ messageCount).child("time").setValue(ServerValue.TIMESTAMP);


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                
            }
        });


        
        rv_messages = (RecyclerView)findViewById(R.id.rv_messages);

        chatMessages = new ArrayList<>();
        MessageAdapter messageAdapter = new MessageAdapter(this,chatMessages);
        rv_messages.setLayoutManager(new LinearLayoutManager(this));
        rv_messages.setAdapter(messageAdapter);
        
        
    }

    private void newMessage(final String s) {
        mMessageCount.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long messageCount = 1;
                if (!dataSnapshot.exists()){
                    mMessageCount.setValue(1);
                }
                else {
                    messageCount = dataSnapshot.getValue(Long.class);
                    messageCount++;
                    mMessageCount.setValue(messageCount);
                }

                mCHAT.child("MESSAGE "+ messageCount).child("from").setValue(username);
                mCHAT.child("MESSAGE "+ messageCount).child("to").setValue(toWhom);
                mCHAT.child("MESSAGE "+ messageCount).child("message").setValue(s);
                mCHAT.child("MESSAGE "+ messageCount).child("time").setValue(ServerValue.TIMESTAMP);

                mMessageCount.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long messageCount = dataSnapshot.getValue(Long.class);
                        mMessage = mCHAT.child("MESSAGE "+messageCount);
                        mMessage.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String to = dataSnapshot.child("to").getValue(String.class);
                                String from = dataSnapshot.child("from").getValue(String.class);
                                String message = dataSnapshot.child("message").getValue(String.class);
                                long time = dataSnapshot.child("time").getValue(Long.class);

                                chatMessages.add(new ChatMessage(to,from,message,time));
                                rv_messages.getAdapter().notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


        private final Context mContext;
        private final ArrayList<ChatMessage> mRecyclerViewItems;

        public MessageAdapter(Context context, ArrayList<ChatMessage> recyclerViewItems){
            this.mContext = context;
            this.mRecyclerViewItems = recyclerViewItems;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View adItemLayoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_item,parent,false);

            final MessageViewHolder viewHolder = new MessageViewHolder(adItemLayoutView);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MessageViewHolder messageViewHolder = (MessageViewHolder)holder;
            final ChatMessage messageData = (ChatMessage) mRecyclerViewItems.get(position);

            if (username.equals(messageData.getFrom())){
                messageViewHolder.ll_recipient.setVisibility(View.INVISIBLE);
                messageViewHolder.tv_message_2.setText(messageData.getMessageText());
                messageViewHolder.tv_time_2.setText(messageData.getMessageTime()+"");
            }
            else {
                messageViewHolder.ll_sender.setVisibility(View.INVISIBLE);
                messageViewHolder.tv_message_1.setText(messageData.getMessageText());
                messageViewHolder.tv_time_1.setText(messageData.getMessageTime()+"");
            }
        }

        @Override
        public int getItemCount() {
            return mRecyclerViewItems.size();
        }

        public class MessageViewHolder extends RecyclerView.ViewHolder{
            private TextView tv_message_1;
            private TextView tv_time_1;
            private TextView tv_message_2;
            private TextView tv_time_2;
            private LinearLayout ll_sender,ll_recipient;
            public MessageViewHolder(View itemView) {
                super(itemView);
                tv_message_1 = (TextView)itemView.findViewById(R.id.tv_message_1);
                tv_message_2 = (TextView)itemView.findViewById(R.id.tv_message_2);
                tv_time_1 = (TextView)itemView.findViewById(R.id.tv_time_1);
                tv_time_2 = (TextView)itemView.findViewById(R.id.tv_time_2);
                ll_sender = (LinearLayout) itemView.findViewById(R.id.ll_right);
                ll_recipient = (LinearLayout) itemView.findViewById(R.id.ll_left);

            }
        }

    }
}
