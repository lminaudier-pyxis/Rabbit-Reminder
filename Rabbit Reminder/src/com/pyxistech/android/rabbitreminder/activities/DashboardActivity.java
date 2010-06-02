package com.pyxistech.android.rabbitreminder.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.pyxistech.android.rabbitreminder.R;

public class DashboardActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.dashboard);
    	
    	Button tasksButton = (Button) findViewById(R.id.dashboard_tasks_button);
    	tasksButton.setOnClickListener(tasksClickListener);
    	Button listsButton = (Button) findViewById(R.id.dashboard_lists_button);
    	listsButton.setOnClickListener(listsClickListener);
    }
    
    private OnClickListener tasksClickListener = new OnClickListener() {
		public void onClick(View v) {
			Intent i = new Intent(DashboardActivity.this, TaskListActivity.class);
			startActivity(i);
		}
	};
    
    private OnClickListener listsClickListener = new OnClickListener() {
		public void onClick(View v) {
			Intent i = new Intent(DashboardActivity.this, ListsListActivity.class);
			startActivity(i);
		}
	};
}
