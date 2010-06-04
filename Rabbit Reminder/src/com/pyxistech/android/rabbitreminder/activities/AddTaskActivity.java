package com.pyxistech.android.rabbitreminder.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.pyxistech.android.rabbitreminder.R;
import com.pyxistech.android.rabbitreminder.models.TaskItem;

public class AddTaskActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.add_task);
		
		Button addTaskButton = (Button) findViewById(R.id.add_task_button);
		EditText editText = (EditText) findViewById(R.id.new_task_text);
		
		if (savedInstanceState == null) {
			Bundle bundle = getIntent().getExtras();
			if (bundle != null) {
				TaskItem item = bundle.getParcelable("item");
				if( item != null ) {
					editText.setText(item.getText());
					index = bundle.getInt("index");
				}
				
				listId = bundle.getInt("listId");
			}
		}
		else {
			index = savedInstanceState.getInt("index");
			listId = savedInstanceState.getInt("listId");
		}
		
		addTaskButton.setOnClickListener(listener);
	}
	
    @Override
    public void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	
		outState.putInt("index", index);
		outState.putInt("listId", listId);
    }
	
	private OnClickListener listener = new OnClickListener() {
		public void onClick(View v) {
			Intent data = new Intent();
			
			EditText editText = (EditText) findViewById(R.id.new_task_text);
			String text = editText.getText().toString();
			data.putExtra("newTaskText", text);
			data.putExtra("index", index);
			data.putExtra("listId", listId);
			
			AddTaskActivity.this.setResult(Activity.RESULT_OK, data);
			AddTaskActivity.this.finish();
		}
	};
	
	private int index = -1;
	private int listId = -1;
}
