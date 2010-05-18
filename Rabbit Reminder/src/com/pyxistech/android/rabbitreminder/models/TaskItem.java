package com.pyxistech.android.rabbitreminder.models;

public class TaskItem {
	
	public TaskItem(String text, boolean done) {
		this.text = text;
		this.done = done;
	}
	
	public String getText() {
		return text;
	}
	
	public void setDone(boolean done) {
		this.done = done;
	}
	
	public boolean isDone() {
		return done;
	}
	
	public String toString() {
		return text;
	}
	
	public boolean equals(TaskItem item) {
		return (item.getText().equals(text)) && (item.isDone() == done);
	}
	
	private String text;
	private boolean done;
}
