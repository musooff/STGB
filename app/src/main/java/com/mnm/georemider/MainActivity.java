package com.mnm.georemider;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.InterpolatorRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawer;

    RecyclerView rv_tasks;
    ArrayList<TaskData> taskDatas;

    SharedPreferences sh;
    SharedPreferences.Editor editor;

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mClients = mRootRef.child("clients");
    DatabaseReference mUser;

    JSONObject jUser;
    JSONArray jTasks;
    JSONArray jFriendTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sh = getSharedPreferences("TaskData",0);
        editor = sh.edit();
        editor.apply();

        try {
            jUser = new JSONObject(sh.getString("userJsonData",""));
            if (jUser.has("tasks")){
                jTasks = jUser.getJSONArray("tasks");
            }
            if (jUser.has("friendTasks")){
                jTasks = jUser.getJSONArray("friendTasks");
            }
            if (!jUser.has("tasks")){
                jTasks = new JSONArray();
                jUser.put("tasks",jTasks);
            }
            if (!jUser.has("friendTasks")){
                jFriendTasks = new JSONArray();
                jUser.put("friendTasks",jFriendTasks);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        taskDatas = new ArrayList<>();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newTask = new Intent(getApplicationContext(),TaskActivity.class);
                //newTask.putExtra("tasks",taskDatas);
                startActivity(newTask);
            }
        });


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        rv_tasks = (RecyclerView)findViewById(R.id.rv_tasks);


        mUser = mClients.child(sh.getString("username",null));
        mUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                jUser = new JSONObject();
                jTasks = new JSONArray();
                jFriendTasks = new JSONArray();
                try {
                    jUser.put("tasks",jTasks);
                    jUser.put("friendTasks",jFriendTasks);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (DataSnapshot child :dataSnapshot.getChildren()){
                    if (child.getKey().startsWith("TASK: ")){
                        JSONObject jTask = new JSONObject();
                        try {
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

                            taskDatas.add(new TaskData(
                                    jTask.getString("taskName"),
                                    jTask.getString("taskDescription"),
                                    jTask.getBoolean("hasLocName"),
                                    jTask.getString("locName"),
                                    jTask.getString("locAddress"),
                                    new LatLng(jTask.getDouble("locLat"),jTask.getDouble("locLong")),
                                    jTask.getString("time"),
                                    jTask.getBoolean("friends"),
                                    jTask.getInt("radius"),
                                    jTask.getBoolean("entry")



                                    /*
                                    child.child("taskName").getValue(String.class),
                                    child.child("taskDescription").getValue(String.class),
                                    child.child("hasLocName").getValue(Boolean.class),
                                    child.child("locName").getValue(String.class),
                                    child.child("locAddress").getValue(String.class),
                                    new LatLng(child.child("locLat").getValue(Double.class),child.child("locLong").getValue(Double.class)),
                                    child.child("time").getValue(String.class),
                                    child.child("friends").getValue(Boolean.class),
                                    child.child("radius").getValue(Integer.class),
                                    child.child("entry").getValue(Boolean.class)
                                    */

                            ));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        jTasks.put(jTask);
                        rv_tasks.getAdapter().notifyItemInserted(jTasks.length());
                    }
                    else if (child.getKey().startsWith("Friend TASK: ")){
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

                        //Log.e("friT",jFriendTasks.toString());
                    }
                }


                //Log.e("tasks",jUser.toString());
                String userData = jUser.toString();
                editor.putString("userJsonData",userData);
                editor.apply();

                Intent service = new Intent(getApplicationContext(),MyService.class);
                startService(service);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*
        int count = sharedPreferences.getInt("count",0);
        count++;
        editor.putString("task"+count,"Marswewillgetaplusget some cookieswewillgetaplusMejomwewillgetaplus")
        editor.putInt("count",sharedPreferences.getInt("count",0)+1);
        */

        TaskAdapter taskAdapter = new TaskAdapter(this,taskDatas);
        rv_tasks.setLayoutManager(new LinearLayoutManager(this));
        rv_tasks.setAdapter(taskAdapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.nav_friends:
                Intent friends = new Intent(getApplicationContext(),Friends.class);
                startActivity(friends);
                drawer.closeDrawers();
                break;
            case R.id.nav_task:
                Intent new_task = new Intent(getApplicationContext(),TaskActivity.class);
                startActivity(new_task);
                drawer.closeDrawers();
                break;
            case R.id.nav_myprofile:
                Intent my_profile = new Intent(getApplicationContext(),MyProfile.class);
                startActivity(my_profile);
                drawer.closeDrawers();
                break;
            case R.id.nav_logout:
                new android.app.AlertDialog.Builder(this)
                        .setTitle("Loging out")
                        .setMessage("Are you sure, you want to log out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                editor.clear();
                                editor.apply();
                                finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
        }

        return false;
    }


    public class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


        private final Context mContext;
        private final ArrayList<TaskData> mRecyclerViewItems;
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mFriend = mRootRef.child("clients");
        DatabaseReference mUser;
        SharedPreferences sharedPreferences;

        public TaskAdapter(Context context, ArrayList<TaskData> recyclerViewItems){
            this.mContext = context;
            this.mRecyclerViewItems = recyclerViewItems;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View adItemLayoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.task_item,parent,false);

            final TaskViewHolder viewHolder = new TaskViewHolder(adItemLayoutView);
            adItemLayoutView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = viewHolder.getAdapterPosition();
                    if (position!= RecyclerView.NO_POSITION){
                        Intent each_task = new Intent(getApplicationContext(),EachTask.class);
                        each_task.putExtra("taskName",mRecyclerViewItems.get(position).getName());
                        each_task.putExtra("taskDesc",mRecyclerViewItems.get(position).getNeed());
                        each_task.putExtra("locName",mRecyclerViewItems.get(position).getLocationName());
                        each_task.putExtra("locAddress",mRecyclerViewItems.get(position).getLogicalLocation());
                        each_task.putExtra("radius",mRecyclerViewItems.get(position).getRadius());
                        each_task.putExtra("time",mRecyclerViewItems.get(position).getTime());
                        each_task.putExtra("entry",mRecyclerViewItems.get(position).isEntry());
                        each_task.putExtra("friends",mRecyclerViewItems.get(position).isFriends());
                        startActivity(each_task);
                    }
                }
            });

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TaskViewHolder taskViewHolder = (TaskViewHolder)holder;
            final TaskData taskData = (TaskData) mRecyclerViewItems.get(position);
            Button finish_task = ((MainActivity.TaskAdapter.TaskViewHolder) holder).getButtonDone();
            taskViewHolder.name.setText(taskData.getName());
            taskViewHolder.description.setText(taskData.getNeed());
            Log.d("Position: ",Integer.toString(position));
            final int pos_click = position;
            sharedPreferences = getSharedPreferences("TaskData",0);
            final String user_name = sharedPreferences.getString("username","0");
            finish_task.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mUser = mFriend.child(user_name);
                    mUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            DataSnapshot mTask = dataSnapshot;
                            String tasks = mTask.child("list_task").getValue(String.class);
                            String[] list_task = tasks.split(",");
                            int pos = Integer.parseInt(list_task[pos_click+1]);
                            final String task_name = "TASK: "+ Integer.toString(pos);
                            Log.d("Task Name :",task_name);
                            mUser.child(task_name).removeValue();
                            String input = "";
                            // There is a problem ",6,6,6" like string
                            for(int i = 0; i < list_task.length; i++){
                                if((Integer.parseInt(list_task[i]) == pos) || (list_task[i]==null)){
                                    continue;
                                }else{
                                    if(input==""){
                                        input = list_task[i];
                                    }else{
                                        input = input+","+list_task[i];
                                    }
                                }
                            }
                            mUser.child("list_task").setValue(input);
                            Intent main_activity = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(main_activity);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });
            if (taskData.isHasName()){
                taskViewHolder.location.setText(taskData.getLocationName());
            }
            else {
                taskViewHolder.location.setText(taskData.getLogicalLocation());

            }
            taskViewHolder.time.setText(taskData.getTime().toString());
            if (taskData.isFriends()){
                taskViewHolder.iv_friends.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return mRecyclerViewItems.size();
        }

        public class TaskViewHolder extends RecyclerView.ViewHolder{
            private TextView name;
            private TextView description;
            private TextView time;
            private TextView location;
            private ImageView imageView;
            private ImageView iv_friends;
            private Button button_done;
            public TaskViewHolder(View itemView) {
                super(itemView);
                name = (TextView)itemView.findViewById(R.id.tv_task_name);
                description = (TextView)itemView.findViewById(R.id.tv_task_description);
                time = (TextView)itemView.findViewById(R.id.tv_time);
                location = (TextView)itemView.findViewById(R.id.tv_location);
                imageView = (ImageView) itemView.findViewById(R.id.iv_user);
                iv_friends = (ImageView)itemView.findViewById(R.id.iv_friends);
                button_done = (Button)itemView.findViewById(R.id.task_done);

            }
            public Button getButtonDone(){
                return this.button_done;
            }

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sh.getBoolean("newTask",false)){

                JSONObject jTask = null;
                try {
                    jUser = new JSONObject(sh.getString("userJsonData",null));
                    jTasks = jUser.getJSONArray("tasks");
                    jTask = jTasks.getJSONObject(jTasks.length()-1);
                    taskDatas.add(new TaskData(
                            jTask.getString("taskName"),
                            jTask.getString("taskDescription"),
                            jTask.getBoolean("hasLocName"),
                            jTask.getString("locName"),
                            jTask.getString("locAddress"),
                            new LatLng(jTask.getDouble("locLat"),jTask.getDouble("locLong")),
                            jTask.getString("time"),
                            jTask.getBoolean("friends"),
                            jTask.getInt("radius"),
                            jTask.getBoolean("entry")


                    ));
                    rv_tasks.getAdapter().notifyDataSetChanged();
                    editor.putBoolean("newTask",false);
                    editor.apply();
                } catch (JSONException e) {

                    e.printStackTrace();
                }

            /*
            mUser = mClients.child(sh.getString("username",""));
            mUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    DataSnapshot child = dataSnapshot.child(sh.getString("name",null));

                    taskDatas.add(new TaskData(
                            child.child("taskName").getValue(String.class),
                            child.child("taskDescription").getValue(String.class),
                            child.child("hasLocName").getValue(Boolean.class),
                            child.child("locName").getValue(String.class),
                            child.child("locAddress").getValue(String.class),
                            new LatLng(child.child("locLat").getValue(Double.class),child.child("locLong").getValue(Double.class)),
                            child.child("time").getValue(String.class),
                            child.child("friends").getValue(Boolean.class),
                            child.child("radius").getValue(Integer.class),
                            child.child("entry").getValue(Boolean.class)

                    ));
                    rv_tasks.getAdapter().notifyDataSetChanged();
                    editor.putBoolean("newTask",false);
                    editor.apply();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            */

        }


    }
}
