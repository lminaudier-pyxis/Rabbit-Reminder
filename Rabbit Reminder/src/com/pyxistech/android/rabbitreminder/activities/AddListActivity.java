package com.pyxistech.android.rabbitreminder.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.pyxistech.android.rabbitreminder.R;

public class AddListActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_list);
		
		Button addListButton = (Button) findViewById(R.id.add_list_button);
		addListButton.setOnClickListener(listener);
	}
	
	private OnClickListener listener = new OnClickListener() {
		public void onClick(View v) {
			Intent data = new Intent();
			
			EditText newListNameText = (EditText) findViewById(R.id.new_list_name_text);
			String listName = newListNameText.getText().toString();
			data.putExtra("newListNameText", listName);
			
			AddListActivity.this.setResult(Activity.RESULT_OK, data);
			AddListActivity.this.finish();
		}
	};
}
