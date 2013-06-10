package com.example.runspyrun;

import java.io.IOException;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONException;

import com.wikitude.architect.ArchitectUrlListener;
import com.wikitude.architect.ArchitectView;

import com.example.runspyrun.AttackDefend;
import com.example.runspyrun.PoiDetailActivity;
import com.example.runspyrun.PoiBean;
import com.example.runspyrun.R;
import com.example.runspyrun.R.id;
import com.example.runspyrun.R.layout;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class AttackActivity extends Activity implements LocationListener, ArchitectUrlListener {

	// Needed for Wikitude
	private ArchitectView architectView;
	
	// Location stuff
	private LocationManager locManager;
	private Location loc; // stores players current location
	private Location hackInLoc; // Hack In Point
	private Location hackOutLoc; // Hack Out Point
	private List<Location> landMineLocs = new ArrayList<Location>();
	
	private String courseName;
	private Course course;
	private CourseReader cr;
	private boolean gameStarted = false;
	
	final double accuracy = 0.0005;
	final int PROXIMITY = 10;
	
	private String apiKey = "";
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		if (!ArchitectView.isDeviceSupported(this)) {
			// Phone must have a camera
			Toast.makeText(this, "minimum requirements not fulfilled",
					Toast.LENGTH_LONG).show();
			this.finish();
			return;
		}
		
		Bundle extras = getIntent().getExtras();
		
		courseName = extras.getString("COURSE"); // Course name gets taken from previous activity
		
		try {
			cr = new CourseReader(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Search for course based on its name
		ArrayList<Course> courseList = cr.getCourses();
		Iterator<Course> it = courseList.iterator();
		while (it.hasNext()) {
			course = it.next();
			if (course.getName().equals(courseName)) {
				break;
			}
		}
		
		setContentView(R.layout.activity_course);

		this.architectView = (ArchitectView) this.findViewById(R.id.architectView);
		
		architectView.onCreate(apiKey);
		
		int UPDATE_INTERVAL = 5 * 1000; // 5 seconds
		
		// Request a new location every 5 seconds and store it in loc
		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_INTERVAL, 0, this);
		loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		while (loc == null); // We should get a location before we move on
		
		return;
	}
	
	protected void onStart() {
		super.onStart();
		
		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
	}
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
    	super.onPostCreate(savedInstanceState);
    	
    	//IMPORTANT: creates ARchitect core modules
    	if(this.architectView != null)
    		this.architectView.onPostCreate();
    	
    	//register this activity as handler of "architectsdk://" urls
    	this.architectView.registerUrlListener(this);
    	
    	try {
			loadWorld();
		} catch (IOException e) {
			e.printStackTrace();
		}

    }

	@Override
	protected void onResume() {
		super.onResume();

		this.architectView.onResume();
		if (loc != null) {
			this.architectView.setLocation(loc.getLatitude(), loc.getLongitude(), loc.getAltitude(), 1f);
		}

	}
    @Override
    protected void onPause() {
    	super.onPause();
    	if(this.architectView != null)
    		this.architectView.onPause();
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	
    	if(this.architectView != null)
    		this.architectView.onDestroy();
    }
    
    @Override
    public void onLowMemory() {
    	super.onLowMemory();
    	
    	if(this.architectView != null)
    		this.architectView.onLowMemory();
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	locManager.removeUpdates(this);
    }
    
    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch(which) {
			case DialogInterface.BUTTON_POSITIVE:
				dialog.dismiss();
				break;
			}
		}
    };
	
	
	private void loadWorld() throws IOException {
		/** Loads the world into user's camera view */
		this.architectView.load("tutorial1.html");
		
		JSONArray array = new JSONArray();
		
		PoiBean hackIn = course.getHackInPoint();
		PoiBean hackOut = course.getHackOutPoint();
		ArrayList<PoiBean> mines = course.getLandMines();
		
		try {
			array.put(hackIn.toJSONObject());
			array.put(hackOut.toJSONObject());
			
			for (int i = 0; i < mines.size(); ++i) {
				array.put(mines.get(i).toJSONObject());
				landMineLocs.add(mines.get(i).makeLoc());
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		hackInLoc = new Location(hackIn.makeLoc());
		
		//hackInLoc.set(hackIn.makeLoc());
		hackOutLoc = new Location(hackOut.makeLoc());
		
		this.architectView.callJavascript("newData(" + array.toString() + ");");

	}

	@SuppressLint("NewApi")
	@Override
	public void onLocationChanged(Location location) {
		loc.set(location);
		if (this.architectView != null) {
			this.architectView.setLocation((float) (loc.getLatitude()), (float)(loc.getLongitude()), loc.getAccuracy());
		}
		
		DecimalFormat df = new DecimalFormat("#.0");
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setPositiveButton("OK", dialogClickListener);
		
		String text = "";
		if (!gameStarted) {
			if (loc.distanceTo(hackInLoc) <= PROXIMITY && !gameStarted) {
				// Player starts the game by being near hack in point
				text = "Game Started!\n";
				builder.setMessage("Game started! Get to the end! Avoid the landmines!").show();
				gameStarted = true;
			} else {
				// Player has not yet reached hack in point
				text = "Distance to Hack In Point is " + df.format(loc.distanceTo(hackInLoc)) + " metres\n";
			}
		} else {
			// Game is started
			if (loc.distanceTo(hackOutLoc) <= PROXIMITY) {
				// Player reached the hack out point
				text = "You win!\n";
				builder.setMessage("You got to the Hack Out Point! You win!").show();
				gameStarted = false;
			} else {
				// Player has not yet reached the hack out point
				text = "Distance to Hack Out Point is " + df.format(loc.distanceTo(hackOutLoc)) + " metres\n";
			}
			
			if (distanceToClosestMine() <= PROXIMITY) {
				// Player hit a land mine
				text = "You just walked over the land mine.  Game Over.";
				builder.setMessage("You just got blown up.\nWhy would you get blown up?\n" +
						"You'll never make it as a spy if you get blown up.").show();
				gameStarted = false;
			} else {
				text += "Distance to nearest landmine is " + df.format(distanceToClosestMine()) + " metres\n";
			}
		}

		try {
			JSONObject json = new JSONObject(text);
			this.architectView.callJavascript(json.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// Would like this message to display permanently.  Above is my incorrect attempt.
		// Displaying it with a Toast message for now.
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
		
	}
	
	private float distanceToClosestMine() {
		// Returns the current distance to the nearest landmine
		float closestDistance = 0;
		Location temp;
		Iterator<Location> iter = landMineLocs.iterator();
		
		while (iter.hasNext()) {
			temp = iter.next();
			if (closestDistance == 0 || loc.distanceTo(temp) < closestDistance) {
				closestDistance = loc.distanceTo(temp);
			}
		}
		
		return closestDistance;
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean urlWasInvoked(String url) {
		//parsing the retrieved url string
				List<NameValuePair> queryParams = URLEncodedUtils.parse(URI.create(url), "UTF-8");
				
				String id = "";
				String description = "";
				String name = "";
				// getting the values of the contained GET-parameters
				for(NameValuePair pair : queryParams)
				{
					if(pair.getName().equals("id"))
					{
						id = pair.getValue();
					}
					if(pair.getName().equals("description"))
					{
						description = pair.getValue();
					}
					if(pair.getName().equals("name"))
					{
						name = pair.getValue();
					}
				}
				
				//get the corresponding poi bean for the given id
//				PoiBean bean = poiBeanList.get(Integer.parseInt(id));
				//start a new intent for displaying the content of the bean
				Intent intent = new Intent(this, PoiDetailActivity.class);
				intent.putExtra("POI_NAME", name);
				intent.putExtra("POI_DESC", description);
				this.startActivity(intent);
				return true;
	}
}
