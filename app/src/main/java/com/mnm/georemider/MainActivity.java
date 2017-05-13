package com.mnm.georemider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{


    RecyclerView rv_tasks;
    ArrayList<TaskData> taskDatas;

    SharedPreferences sh;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sh = getSharedPreferences("TaskData",0);
        editor = sh.edit();
        editor.apply();

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


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        rv_tasks = (RecyclerView)findViewById(R.id.rv_tasks);

        taskDatas.add(new TaskData("Mars","get some cookies",true,"Mejom","Mejom", new LatLng(5,5), "2 hours ago",true,30,true));
/*
        taskDatas.add(new TaskData("Some books","Harry Potter 5","Main Library", new LatLng(5,5), "2 hours ago",false,30,true));
        taskDatas.add(new TaskData("Boots","Gotta buy boots","Adidas Store", new LatLng(5,5), "1.5 hours ago",true,30,true));
        taskDatas.add(new TaskData("Make up","Gotta buy libstrick","Mejon", new LatLng(5,5), "1 hour ago",false,30,true));
        taskDatas.add(new TaskData("Mars","get some cookies","Mejon", new LatLng(5,5), "2 hours ago",false,30,true));
        taskDatas.add(new TaskData("Some books","Harry Potter 5","Main Library", new LatLng(5,5), "2 hours ago",false,30,true));
        taskDatas.add(new TaskData("Boots","Gotta buy boots","Adidas Store", new LatLng(5,5), "1.5 hours ago",false,30,true));
        taskDatas.add(new TaskData("Make up","Gotta buy libstrick","Mejon", new LatLng(5,5), "1 hour ago",false,30,true));
*/

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
        return false;
    }


    public class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


        private final Context mContext;
        private final ArrayList<TaskData> mRecyclerViewItems;

        public TaskAdapter(Context context, ArrayList<TaskData> recyclerViewItems){
            this.mContext = context;
            this.mRecyclerViewItems = recyclerViewItems;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View adItemLayoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.task_item,parent,false);
            return new TaskViewHolder(adItemLayoutView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TaskViewHolder taskViewHolder = (TaskViewHolder)holder;
            final TaskData taskData = (TaskData) mRecyclerViewItems.get(position);
            taskViewHolder.name.setText(taskData.getName());
            taskViewHolder.description.setText(taskData.getNeed());
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
            public TaskViewHolder(View itemView) {
                super(itemView);
                name = (TextView)itemView.findViewById(R.id.tv_task_name);
                description = (TextView)itemView.findViewById(R.id.tv_task_description);
                time = (TextView)itemView.findViewById(R.id.tv_time);
                location = (TextView)itemView.findViewById(R.id.tv_location);
                imageView = (ImageView) itemView.findViewById(R.id.iv_user);
                iv_friends = (ImageView)itemView.findViewById(R.id.iv_friends);

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sh.getBoolean("newTask",false)){
            taskDatas.add(new TaskData(sh.getString("name","None"),
                    sh.getString("description","None"),
                    sh.getBoolean("hasName",false),
                    sh.getString("logName","None"),
                    sh.getString("logLocation","None"),
                    new LatLng(sh.getFloat("lat",0),sh.getFloat("long",0)),
                    sh.getString("date","None"),
                    sh.getBoolean("isFriends",false),
                    sh.getInt("radius",0),
                    sh.getBoolean("isEntry",false)));
            editor.putBoolean("newTask",false);
            editor.apply();

            rv_tasks.getAdapter().notifyItemInserted(taskDatas.size()-1);
        }


    }
}
