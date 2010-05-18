package com.pyxistech.android.rabbitreminder;

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
	
	private String text;
	private boolean done;
}
