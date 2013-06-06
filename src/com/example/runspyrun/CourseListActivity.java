package com.example.runspyrun;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class CourseListActivity extends Activity {
	
	//Parser to retrieve course data from file
	private CourseReader courseReader;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Sets layout
		setContentView(R.layout.activity_courselist);
		
		//Scrollable list view
		final ListView listView = (ListView) findViewById(R.id.listview);
		
		try {
			courseReader = new CourseReader(this);
			final ArrayList<Course> courseList = courseReader.getCourses();
			
			//Iterates through the courselist and creates a list of course names
			Iterator<Course> iter = courseList.iterator();
			List<String> names = new ArrayList<String>();
			for (int i = 0; i < courseList.size(); ++i) {
				names.add(iter.next().getName());
			}
			
			final StableArrayAdapter adapter = new StableArrayAdapter(
				this, android.R.layout.simple_list_item_1, names);
			
			
			listView.setAdapter(adapter);
			
			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				
				@SuppressLint("NewApi")
				@Override
				public void onItemClick(AdapterView<?> parent, final View view,
						int position, long id) {
					final String item = (String) parent.getItemAtPosition(position);
					
					Intent intent = new Intent(getApplicationContext(), AttackActivity.class);
					intent.putExtra("COURSE", item);
					startActivity(intent);
				}
			});
			
		} catch (IOException e) {
			Toast.makeText(this, "IO Exception", Toast.LENGTH_LONG).show();
			
			e.printStackTrace();
			this.finish();
			return;
		}
	}
	
	private class StableArrayAdapter extends ArrayAdapter<String> {

	    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

	    public StableArrayAdapter(Context context, int textViewResourceId,
	    			List<String> objects) {
	    	super(context, textViewResourceId, objects);
	    	for (int i = 0; i < objects.size(); ++i) {
	    		mIdMap.put(objects.get(i), i);
	    	}
	    }
	
	    @Override
	    public long getItemId(int position) {
	    	String item = getItem(position);
	    	return mIdMap.get(item);
	    }
	
	    @Override
	    public boolean hasStableIds() {
	    	return true;
	    }
	
	}

}

