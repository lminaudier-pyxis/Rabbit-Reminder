package com.pyxistech.android.rabbitreminder.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.pyxistech.android.rabbitreminder.R;

public class AddTaskActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_task);
		
		Button addTaskButton = (Button) findViewById(R.id.add_task_button);
		addTaskButton.setOnClickListener(listener);
	}
	
	private OnClickListener listener = new OnClickListener() {
		public void onClick(View v) {
			Intent data = new Intent();
			
			EditText editText = (EditText) findViewById(R.id.new_task_text);
			String text = editText.getText().toString();
			data.putExtra("newTaskText", text);
			
			AddTaskActivity.this.setResult(Activity.RESULT_OK, data);
			AddTaskActivity.this.finish();
		}
	};
}
