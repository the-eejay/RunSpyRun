package com.example.runspyrun;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Vector;
import java.io.FileOutputStream;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.location.LocationManager;
import android.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.content.ClipData;
import android.content.Context;
import android.content.res.Resources;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.support.v4.app.*;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
	private double minDist = 500;
    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.defend);
        setUpMapIfNeeded();
        HorizontalScrollView hView = (HorizontalScrollView) findViewById(R.id.iconlist);
        final Context context = this;
        isReadable = isExternalStorageReadable();
        isWritable = isExternalStorageWritable();
        //Resources res = getResources();
        //Drawable myImage = res.getDrawable(R.drawable.defend_asset);
        mMap.setOnCameraChangeListener(new OnCameraChangeListener(){

			@Override
			public void onCameraChange(CameraPosition position) {
				mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-27.46368,152.99762), 15));
			}

			
        });
        OnClickListener cListener = new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				dragging = (String) v.getTag();
			}
        	
        };
        ImageView imageView = (ImageView) findViewById(R.id.mine);
        imageView.setTag("mine");
        imageView.setOnClickListener(cListener);
        
        ImageView inView = (ImageView) findViewById(R.id.hackin);
        inView.setTag("hackin");
        inView.setOnClickListener(cListener);
        
        ImageView outView = (ImageView) findViewById(R.id.hackout);
        outView.setTag("hackout");
        outView.setOnClickListener(cListener);
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
					if(lengthCheck)
					{
						if(dragging == "mine")
						{
							Marker tempMarker = mMap.addMarker(new MarkerOptions()
									.position(point)
									.title("mine")
									.draggable(true)
									.icon(BitmapDescriptorFactory.fromResource(R.drawable.mine_small)));
							dragging = "";
							
							markers.add(tempMarker);
							
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
							markers.add(inMarker);
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
							markers.add(outMarker);
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
					if(m != arg0)
					{
					Location.distanceBetween(m.getPosition().latitude, m.getPosition().longitude, arg0.getPosition().latitude, arg0.getPosition().longitude, tempV);
					if(tempV[0] < minDist){lengthCheck = false;}
					}
				}
				if(!lengthCheck){
					arg0.setPosition(originalPosition);
				}
				
			}

			@Override
			public void onMarkerDragStart(Marker arg0) {
				originalPosition = arg0.getPosition();
				
			}
        	
        });
        
        Button button = (Button) findViewById(R.id.save);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(isReadable && isWritable)
                {
                	File dir = new File(context.getExternalFilesDir(null), "courses.txt");
                	courseToFile(dir, "course1");
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
    
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
        	mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        	mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        	mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-27.46368,152.99762), 15));

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
    	try {
    		OutputStream outputStream = new FileOutputStream(fname, true);
    		String header = "[" + courseName + ":";
    		outputStream.write(header.getBytes());
			for(Marker m : markers){
				String output = (String) m.getTitle() + "," + String.valueOf(m.getPosition().latitude) + ',' + String.valueOf(m.getPosition().longitude) + '\n';
				outputStream.write(output.getBytes());
				Log.e("file", output);
			}
			outputStream.write("]".getBytes());
			outputStream.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
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

}

