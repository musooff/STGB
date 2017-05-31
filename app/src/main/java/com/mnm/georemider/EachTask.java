package com.mnm.georemider;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by moshe on 27/05/2017.
 */

public class EachTask extends Activity {

    TextView tv_taskName, tv_name,tv_description,tv_locName, tv_locAddress,tv_radius, tv_time, tv_entry, tv_friends;
    RelativeLayout rl_message;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_task);

        tv_name = (TextView)findViewById(R.id.tv_name);
        tv_taskName = (TextView)findViewById(R.id.tv_task_name);
        tv_description = (TextView)findViewById(R.id.tv_task_description);
        tv_locName = (TextView)findViewById(R.id.tv_location);
        tv_locAddress = (TextView)findViewById(R.id.tv_logLocation);
        tv_radius = (TextView)findViewById(R.id.tv_radius);
        tv_time = (TextView)findViewById(R.id.tv_time);
        tv_entry = (TextView)findViewById(R.id.tv_entry);
        tv_friends = (TextView)findViewById(R.id.tv_friends);

        rl_message = (RelativeLayout)findViewById(R.id.rl_message);

        Bundle extras = getIntent().getExtras();
        tv_taskName.setText(extras.getString("taskName"));
        tv_description.setText(extras.getString("taskDesc"));
        tv_locName.setText(extras.getString("locName"));
        tv_locAddress.setText(extras.getString("locAddress"));
        tv_radius.setText(extras.getInt("radius")+"");
        tv_time.setText(extras.getString("time"));
        if (extras.getBoolean("entry")){
            tv_entry.setText("Entry");
        }
        else if (!extras.getBoolean("entry")){
            tv_entry.setText("Exit");
        }
        if (extras.getBoolean("friends") && extras.getString("name")!=null){
            tv_name.setText("Your friend "+extras.getString("name")+"'s task");
            tv_friends.setText("Friends");
            tv_name.setVisibility(View.VISIBLE);
            rl_message.setVisibility(View.VISIBLE);
        }
        else if (!extras.getBoolean("friends")){
            tv_friends.setText("Private");
        }


    }
}
