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

public class DefendActivity extends FragmentActivity {

	private GoogleMap mMap;
	private LocationManager locationManager;
	private LinearLayout mLinearLayout;
	private String dragging = "";
	private Marker inMarker = null;
	private Marker outMarker = null;
	private Boolean isReadable = false;
	private Boolean isWritable = false;
	private Vector<Marker> markers = new Vector<Marker>();
	private HashMap<String, LatLng> markerLocations = new HashMap<String, LatLng>(20);
	private LatLngBounds bounds;
	private double minDist = 10;
	private float width = 800;
	private float height = 800;
	private Polygon polygon;
    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.defend);
        HorizontalScrollView hView = (HorizontalScrollView) findViewById(R.id.iconlist);
        final Context context = this;
        isReadable = isExternalStorageReadable();
        isWritable = isExternalStorageWritable();
        LatLng positionCenter = new LatLng(-27.497307,153.013102);
        Bundle extras = getIntent().getExtras();
        if(extras != null){
        	positionCenter = new LatLng(extras.getDouble("latitude"), extras.getDouble("longitude"));
        }
        final LatLng fCenter = positionCenter;
        setUpMapIfNeeded(fCenter);
        Double hypot = Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2));
        bounds = new LatLngBounds(findLatLng(fCenter, hypot,225.0), findLatLng(fCenter, hypot, 45.0));
        final PolygonOptions pOptions = new PolygonOptions()
		.add(findLatLng(fCenter, hypot, 45.0),
   			 findLatLng(fCenter, hypot, 135.0),
   			 findLatLng(fCenter, hypot, 225.0),
   			 findLatLng(fCenter, hypot, 315.0));
        polygon = mMap.addPolygon(pOptions);
        Button cButton = (Button)findViewById(R.id.Centre);
        cButton.setOnClickListener(new OnClickListener(){
        
			@Override
			public void onClick(View arg0) {
				mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(fCenter, 15));
			}
        	
        });
        
        Button clearButton = (Button)findViewById(R.id.Clear);
        clearButton.setOnClickListener(new OnClickListener(){

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
        OnClickListener cListener = new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				dragging = (String) v.getTag();
				v.setBackgroundColor(Color.WHITE);
			}
        	
        };
        final ImageView imageView = (ImageView) findViewById(R.id.mine);
        imageView.setTag("mine");
        imageView.setOnClickListener(cListener);
        
        final ImageView inView = (ImageView) findViewById(R.id.hackin);
        inView.setTag("hackin");
        inView.setOnClickListener(cListener);
    
        final ImageView outView = (ImageView) findViewById(R.id.hackout);
        outView.setTag("hackout");
        outView.setOnClickListener(cListener);
        
        final EditText courseName = (EditText) findViewById(R.id.name);
        //a motion event listener would work better
        mMap.setOnMapClickListener(new OnMapClickListener(){

			@Override
			public void onMapClick(LatLng point) {
				if(dragging != "")
				{
					float[] tempV = new float[1];
					Boolean lengthCheck = true;
					for(Marker m : markers){
						Location.distanceBetween(m.getPosition().latitude, m.getPosition().longitude, point.latitude, point.longitude, tempV);
						if(tempV[0] < minDist){lengthCheck = false;}
					}
					
					if(lengthCheck && bounds.contains(point))
					{
						if(dragging == "mine")
						{
							Marker tempMarker = mMap.addMarker(new MarkerOptions()
									.position(point)
									.title("mine")
									.draggable(true)
									.icon(BitmapDescriptorFactory.fromResource(R.drawable.mine_small)));
							dragging = "";
							imageView.setBackgroundColor(Color.BLACK);
							markers.add(tempMarker);
							markerLocations.put(tempMarker.getId(), tempMarker.getPosition());
							
						}
						if(dragging == "hackin")
						{
							if(inMarker != null)
							{
								inMarker.remove();
							}
							inMarker = mMap.addMarker(new MarkerOptions()
									.position(point)
									.title("hackin")
									.draggable(true)
									.icon(BitmapDescriptorFactory.fromResource(R.drawable.hackin_small)));
							dragging = "";
							inView.setBackgroundColor(Color.BLACK);
							markers.add(inMarker);
							markerLocations.put(inMarker.getId(), inMarker.getPosition());
						}
						if(dragging == "hackout")
						{
							if(outMarker != null)
							{
								outMarker.remove();
							}
							outMarker = mMap.addMarker(new MarkerOptions()
									.position(point)
									.title("hackout")
									.draggable(true)
									.icon(BitmapDescriptorFactory.fromResource(R.drawable.hackout_small)));
							dragging = "";
							outView.setBackgroundColor(Color.BLACK);
							markers.add(outMarker);
							markerLocations.put(outMarker.getId(), outMarker.getPosition());
						}
					}
				}
				
			}
        	
        });
        
        mMap.setOnMarkerDragListener(new OnMarkerDragListener(){

        	private LatLng originalPosition;
			@Override
			public void onMarkerDrag(Marker arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onMarkerDragEnd(Marker arg0) {
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
				if(!lengthCheck || !bounds.contains(arg0.getPosition())){
					arg0.setPosition(originalPosition);
				}
				else
				{
					markerLocations.remove(arg0.getId());
					markerLocations.put(arg0.getId(), arg0.getPosition());
				}
				
			}

			@Override
			public void onMarkerDragStart(Marker arg0) {
				originalPosition = markerLocations.get(arg0.getId());
				
			}
        	
        });
        
        Button button = (Button) findViewById(R.id.save);
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
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
          return provider2 == null;
        }
        return provider1.equals(provider2);
    }
    
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


