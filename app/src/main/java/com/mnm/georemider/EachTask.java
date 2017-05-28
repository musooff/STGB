package com.mnm.georemider;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

/**
 * Created by moshe on 27/05/2017.
 */

public class EachTask extends Activity {

    TextView tv_name,tv_description,tv_locName, tv_locAddress,tv_radius, tv_time, tv_entry, tv_friends;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_task);

        tv_name = (TextView)findViewById(R.id.tv_task_name);
        tv_description = (TextView)findViewById(R.id.tv_task_description);
        tv_locName = (TextView)findViewById(R.id.tv_location);
        tv_locAddress = (TextView)findViewById(R.id.tv_logLocation);
        tv_radius = (TextView)findViewById(R.id.tv_radius);
        tv_time = (TextView)findViewById(R.id.tv_time);
        tv_entry = (TextView)findViewById(R.id.tv_entry);
        tv_friends = (TextView)findViewById(R.id.tv_friends);

        Bundle extras = getIntent().getExtras();
        tv_name.setText(extras.getString("taskName"));
        tv_description.setText(extras.getString("taskDesc"));
        tv_locName.setText(extras.getString("locName"));
        tv_locAddress.setText(extras.getString("locAddress"));
        tv_radius.setText(extras.getInt("radius")+"");
        tv_time.setText(extras.getString("time"));
        if (extras.getBoolean("isEntry")){
            tv_entry.setText("Entry");
        }
        else if (!extras.getBoolean("isEntry")){
            tv_entry.setText("Exit");
        }
        if (extras.getBoolean("isFriends")){
            tv_friends.setText("Friends");
        }
        else if (!extras.getBoolean("isFriends")){
            tv_friends.setText("Private");
        }


    }
}
