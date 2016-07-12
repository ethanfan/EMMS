package com.emms.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.emms.R;

import java.sql.ResultSetMetaData;

public class TeamStatusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_status);
        ((TextView)findViewById(R.id.tv_title)).setText(R.string.team_status);
    }
}
