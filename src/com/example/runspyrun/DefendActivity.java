package com.example.runspyrun;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.io.FileOutputStream;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.location.LocationManager;
import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.content.ClipData;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.location.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import android.support.v4.app.*;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.*;

//Defend Activity, allows users to select obstacles and place them onto a map, they can
//then save this course and play it later.
public class DefendActivity extends FragmentActivity {

	//Google map object
	private GoogleMap mMap;
	
	//<Old, possibly unnecessary code>
	//Android class for handling location information
	private LocationManager locationManager;
	//Android layout
	private LinearLayout mLinearLayout;
	//</Old, possibly unnecessary code>
	
	//String that determines what type of obstacle will be placed on map
	private String dragging = "";
	//Reference to Hack In marker
	private Marker inMarker = null;
	//Reference to Hack out marker
	private Marker outMarker = null;
	//Boolean that holds whether the course file is readable
	private Boolean isReadable = false;
	//Boolean that holds whether the course file is writable
	private Boolean isWritable = false;
	//A vector of all markers currently on the map
	private Vector<Marker> markers = new Vector<Marker>();
	//A hashmap that contains the markers on the map, as well as their location, used
	//to return markers to old positions after being dragged to illegal positions
	private HashMap<String, LatLng> markerLocations = new HashMap<String, LatLng>(20);
	//Maps bound object that defines the bounds markers can be placed within
	private LatLngBounds bounds;
	//The minimum distance allowed between obstacles (in metres)
	private double minDist = 10;
	//The width of the course (in metres)
	private float width = 800;
	//The height of the course (in metres)
	private float height = 800;
	//Map polgyon class
	private Polygon polygon;
    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set layout
        setContentView(R.layout.defend);
        //Create scrolling list of obstacles
        HorizontalScrollView hView = (HorizontalScrollView) findViewById(R.id.iconlist);
        //Android context, to be used as reference by listeners
        final Context context = this;
        
        //Check if read/write access is allowed
        isReadable = isExternalStorageReadable();
        isWritable = isExternalStorageWritable();
        
        //The default centre position of map (St Lucia's Great Court)
        LatLng positionCenter = new LatLng(-27.497307,153.013102);
        
        //Checks if latitude and longitude were given by AttackDefend, if so, makes them
        //the centre position
        Bundle extras = getIntent().getExtras();
        if(extras != null){
        	positionCenter = new LatLng(extras.getDouble("latitude"), extras.getDouble("longitude"));
        }
        final LatLng fCenter = positionCenter;
        
        //Function that creates map object with initial values
        setUpMapIfNeeded(fCenter);
        
        //Creates bounds from width and height
        Double hypot = Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2));
        bounds = new LatLngBounds(findLatLng(fCenter, hypot,225.0), findLatLng(fCenter, hypot, 45.0));
        final PolygonOptions pOptions = new PolygonOptions()
		.add(findLatLng(fCenter, hypot, 45.0),
   			 findLatLng(fCenter, hypot, 135.0),
   			 findLatLng(fCenter, hypot, 225.0),
   			 findLatLng(fCenter, hypot, 315.0));
        //Creates a rectangular polygon object that shows the map's bounds
        polygon = mMap.addPolygon(pOptions);
        
        //Creates centre button
        Button cButton = (Button)findViewById(R.id.Centre);
        cButton.setOnClickListener(new OnClickListener(){
        	//Listener that resets that cameraPosition on click
			@Override
			public void onClick(View arg0) {
				CameraPosition cameraPosition = new CameraPosition.Builder()
													.target(fCenter)
													.zoom(15)
													.bearing(0)
													.tilt(0)
													.build();
				mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
			}
        	
        });
        
        //Creates clear button
        Button clearButton = (Button)findViewById(R.id.Clear);
        clearButton.setOnClickListener(new OnClickListener(){
        	//Listener that deletes all markers on map on click
			@Override
			public void onClick(View v) {
				mMap.clear();
				markers.clear();
				markerLocations.clear();
				inMarker = null;
				outMarker = null;
				dragging = "";
				polygon = mMap.addPolygon(pOptions);
				
			}
        	
        });
        
        //Adds three images of obstacles to scrollbar
        final ImageView imageView = (ImageView) findViewById(R.id.mine);
        imageView.setTag("mine");
        
        final ImageView inView = (ImageView) findViewById(R.id.hackin);
        inView.setTag("hackin");
    
        final ImageView outView = (ImageView) findViewById(R.id.hackout);
        outView.setTag("hackout");
        
        OnClickListener cListener = new View.OnClickListener(){
        	//Listener that changes the selects an obstacle when its image is clicked
			@Override
			public void onClick(View v) {
				dragging = (String) v.getTag();
				imageView.setBackgroundColor(Color.BLACK);
				inView.setBackgroundColor(Color.BLACK);
				outView.setBackgroundColor(Color.BLACK);
				v.setBackgroundColor(Color.WHITE);
			}
        	
        };
        outView.setOnClickListener(cListener);
        inView.setOnClickListener(cListener);
        imageView.setOnClickListener(cListener);
        
        //Creates textbox for entering course name
        final EditText courseName = (EditText) findViewById(R.id.name);

        mMap.setOnMapClickListener(new OnMapClickListener(){
        	//Listener that adds obstacles to map
			@Override
			public void onMapClick(LatLng point) {
				//Checks if any obstacle is selected
				if(dragging != "")
				{
					//Checks if the point clicked isn't too close to another, existing 
					//marker
					float[] tempV = new float[1];
					Boolean lengthCheck = true;
					for(Marker m : markers){
						Location.distanceBetween(m.getPosition().latitude, m.getPosition().longitude, point.latitude, point.longitude, tempV);
						if(tempV[0] < minDist){lengthCheck = false;}
					}
					
					if(lengthCheck && bounds.contains(point))
					{
						//For mine obstacles
						if(dragging == "mine")
						{
							Marker tempMarker = mMap.addMarker(new MarkerOptions()
									.position(point)
									.title("mine")
									.draggable(true)
									.icon(BitmapDescriptorFactory.fromResource(R.drawable.mine_small)));
							markers.add(tempMarker);
							markerLocations.put(tempMarker.getId(), tempMarker.getPosition());
							
						}
						//For hackin obstacles
						if(dragging == "hackin")
						{
							//There can be only one hackin marker, so old one is replaced
							if(inMarker != null)
							{
								inMarker.remove();
							}
							inMarker = mMap.addMarker(new MarkerOptions()
									.position(point)
									.title("hackin")
									.draggable(true)
									.icon(BitmapDescriptorFactory.fromResource(R.drawable.hackin_small)));
							markers.add(inMarker);
							markerLocations.put(inMarker.getId(), inMarker.getPosition());
						}
						//For hackout obstacles
						if(dragging == "hackout")
						{
							//There can be only one hackin marker, so old one is replaced
							if(outMarker != null)
							{
								outMarker.remove();
							}
							outMarker = mMap.addMarker(new MarkerOptions()
									.position(point)
									.title("hackout")
									.draggable(true)
									.icon(BitmapDescriptorFactory.fromResource(R.drawable.hackout_small)));
							markers.add(outMarker);
							markerLocations.put(outMarker.getId(), outMarker.getPosition());
						}
					}
				}
				
			}
        	
        });
        
        mMap.setOnMarkerDragListener(new OnMarkerDragListener(){
        	//Listener to prevent markers from being dragged into illegal positions
        	private LatLng originalPosition;
			@Override
			public void onMarkerDrag(Marker arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onMarkerDragEnd(Marker arg0) {
				//Checks is new position is too close to existing markers
				float[] tempV = new float[1];
				Boolean lengthCheck = true;
				for(Marker m : markers){
					if(!m.getId().equals(arg0.getId()))
					{
					Location.distanceBetween(m.getPosition().latitude, m.getPosition().longitude, arg0.getPosition().latitude, arg0.getPosition().longitude, tempV);
					if(tempV[0] < minDist){
						lengthCheck = false;}
					}
				}
				//Add marker if it isn't too close to other markers and is within bounds
				if(!lengthCheck || !bounds.contains(arg0.getPosition())){
					arg0.setPosition(originalPosition);
				}
				//Move marker back to original position
				else
				{
					markerLocations.remove(arg0.getId());
					markerLocations.put(arg0.getId(), arg0.getPosition());
				}
				
			}
			//Save original position of marker 
			@Override
			public void onMarkerDragStart(Marker arg0) {
				originalPosition = markerLocations.get(arg0.getId());
				
			}
        	
        });
        
        //Create save button
        Button button = (Button) findViewById(R.id.save);
        //Listener that saves the course to file when clicked
        button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
			public void onClick(View v) {
                if(isReadable && isWritable) {
                	File dir = new File(context.getExternalFilesDir(null), "courses.txt");
                	courseToFile(dir, courseName.getText().toString());
                } else {
                	Toast.makeText(getApplicationContext(), "Could not write to file", Toast.LENGTH_LONG).show();
                }
            }
        });

        

    }

    
    protected void onStart(){
    	super.onStart();
    	
    	locationManager =
                (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        
        
    }
    
    protected void onStop()
    {
    	super.onStop();
    }
    
    private void setUpMapIfNeeded(LatLng center) {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
        	mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        	mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        	mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 15));

        }
        else
        {
        	
        }
    }
    

    /** Checks whether two providers are the same */
    //Probably unnecessary now
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
          return provider2 == null;
        }
        return provider1.equals(provider2);
    }
    
    //Saves course to file given, returns true if successful, false otherwise
    private Boolean courseToFile(File fname, String courseName){
    	String lineSep = System.getProperty("line.separator");
    	try {
    		OutputStream outputStream = new FileOutputStream(fname, true);
    		String header = "Name: " + courseName + lineSep;
    		outputStream.write(header.getBytes());
			for(Marker m : markers){
				String output = (String) m.getTitle() + "," + String.valueOf(m.getPosition().latitude) + "," + String.valueOf(m.getPosition().longitude) + lineSep;
				outputStream.write(output.getBytes());
				Log.e("file", output);
			}
			String output = "END" + lineSep;
			outputStream.write(output.getBytes());
			Toast.makeText(this, "Course Saved", Toast.LENGTH_LONG).show();
			outputStream.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Toast.makeText(this, "Course Not Saved", Toast.LENGTH_LONG).show();
			e.printStackTrace();
			return false;
		}
    	
    	return true;
    	
    }
    
    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
            Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
    
    //Function that returns the LatLng of a point, given the angle (in degrees) and
    //distance from (in metres) of a LatLng point.
    private LatLng findLatLng(LatLng point, Double dist, Double theta)
    {
    	if(dist == 0 || point == null)
    	{
    		return null;
    	}
    	else
    	{
    		Double dx = dist * Math.sin(theta / 180.0 * Math.PI);
    		Double dy = dist * Math.cos(theta / 180.0 * Math.PI);
    		
    		Double dLongitude = dx/(111320*Math.cos(point.latitude / 180.0 * Math.PI));
    		Double dLatitude = dy/110540;
    		
    		return new LatLng(point.latitude + dLatitude, point.longitude + dLongitude);
    		
    	}
    }

}


