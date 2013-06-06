package com.example.runspyrun;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

//Activity that allows players to choose between attacking and defending
//to be replaced by map based activity as explained in proposal
//links to DefendActivity and CourseListActivity
public class AttackDefend extends Activity {
    
	private EditText addressEdit;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set layout
        setContentView(R.layout.activity_main);
        //Add text edit for address
        addressEdit = (EditText)findViewById(R.id.address);
    }
    
    @SuppressLint("NewApi")
    //Opens DefendActivity
	public void makeCourse(View view) {
    	Intent intent = new Intent(this, DefendActivity.class);
    	//If the address isn't empty, fetches the first location returned from the string
    	//(if any) and add the longitude and latitude as extras (passes the values to the
    	//new activity)
    	if(!addressEdit.getText().toString().isEmpty()){
    	Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());     
    	 try {
    	        List<Address> address = geoCoder.getFromLocationName(addressEdit.getText().toString(), 1);    
    	        intent.putExtra("latitude",address.get(0).getLatitude());
    	        intent.putExtra("longitude", address.get(0).getLongitude());            
    	    } catch (IOException e) {
    	        e.printStackTrace();
    	    }
    	}
    	startActivity(intent);
    }
    
    //Opens CourseListActivity
    public void playCourse(View view) {
    	Intent intent = new Intent(this, CourseListActivity.class);
    	startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
