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

import com.example.runspyrun.MainActivity;
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

	private ArchitectView architectView;
	private LocationManager locManager;
	private Location loc;
	private PoiBean hackInBean;
	private PoiBean hackOutBean;
	private List<PoiBean> landMineBeans = new ArrayList<PoiBean>();
	private Location hackInLoc;
	private Location hackOutLoc;
	private List<Location> landMineLocs = new ArrayList<Location>();
	private String button;
	private TextView myText;
	private final double[] courseOne = {
			-27.562396,
			153.04050, // Hack In Point
			-27.562384,
			153.039701, // Hack Out Point
			-27.561757,
			153.042104, // Landmines
			-27.562803,
			153.042705,
			-27.563221,
			153.041782,
			};
	private final double[] courseTwo = {
			-27.499983,
			153.014655, // Hack In Point
			-27.500972,
			153.013372, // Hack Out Point
			-27.50073,
			153.014579, // Landmines]
			-27.500801,
			153.015411,
	};
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
		
		button = "Button2";
		
		if (!ArchitectView.isDeviceSupported(this)) {
			Toast.makeText(this, "minimum requirements not fulfilled",
					Toast.LENGTH_LONG).show();
			this.finish();
			return;
		}
		
		setContentView(R.layout.activity_course);

		this.architectView = (ArchitectView) this.findViewById(R.id.architectView);
		
		architectView.onCreate(apiKey);
		
		int UPDATE_INTERVAL = 5 * 1000; // 5 seconds
		
		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_INTERVAL, 0, this);
		
		loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		while (loc == null);
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
			loadSampleWorld(button);
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
	
	
	private void loadSampleWorld(String button) throws IOException {
		this.architectView.load("tutorial1.html");
		
		double[] course;
		
		JSONArray array = new JSONArray();
		List<PoiBean> poiBeanList = new ArrayList<PoiBean>();
		
		if (button.equals("Button1")) {
			course = courseOne;
		} else if (button.equals("Button2")) {
			course = courseTwo;
		} else { // No more courses yet.
			course = courseTwo;
		}
		
		try {
			hackInBean = new PoiBean(
					"1", 
					"Hack In Point", 
					"This is a hack in point",
					1,
					course[0],
					course[1],
					loc.getAltitude());
			
			array.put(hackInBean.toJSONObject());
			poiBeanList.add(hackInBean);
			
			hackOutBean = new PoiBean(
					"2",
					"Hack Out Point",
					"This is a hack out point",
					2,
					course[2],
					course[3],
					loc.getAltitude());
			
			array.put(hackOutBean.toJSONObject());
			poiBeanList.add(hackOutBean);
			
			for (int i=4; i < course.length; i = i+2) {
				PoiBean landMineBean = new PoiBean(
						"3",
						"Landmine! Watch out!",
						"This is a landmine.  If you step on it you will die!",
						3,
						course[i],
						course[i+1],
						loc.getAltitude());
				
				array.put(landMineBean.toJSONObject());
				poiBeanList.add(landMineBean);
				landMineBeans.add(landMineBean);
			}
			
			this.architectView.callJavascript("newData(" + array.toString() + ");");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		hackInLoc = new Location("");
		hackInLoc.setLatitude(hackInBean.getLatitude());
		hackInLoc.setLongitude(hackInBean.getLongitude());
		hackInLoc.setAltitude(hackInBean.getAltitude());
		
		hackOutLoc = new Location("");
		hackOutLoc.setLatitude(hackOutBean.getLatitude());
		hackOutLoc.setLongitude(hackOutBean.getLongitude());
		hackOutLoc.setAltitude(hackOutBean.getAltitude());
		
		Iterator<PoiBean> it = landMineBeans.iterator();
		
		while (it.hasNext()) {
			PoiBean landMineBean = it.next();
			Location landMineLoc = new Location("");
			landMineLoc.setLatitude(landMineBean.getLatitude());
			landMineLoc.setLongitude(landMineBean.getLongitude());
			landMineLoc.setAltitude(landMineBean.getAltitude());
			landMineLocs.add(landMineLoc);
		}
		
		Context context = getApplicationContext();
		CharSequence text = "Lat: " + loc.getLatitude() + "\n Long: " + loc.getLongitude();
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();

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
				text = "Game Started!\n";
				builder.setMessage("Game started! Get to the end! Avoid the landmines!").show();
				gameStarted = true;
			} else {
				text = "Distance to Hack In Point is " + df.format(loc.distanceTo(hackInLoc)) + " metres\n";
			}
		} else {
			if (loc.distanceTo(hackOutLoc) <= PROXIMITY) {
				text = "You win!\n";
				builder.setMessage("You got to the Hack Out Point! You win!").show();
				gameStarted = false;
			} else {
				text = "Distance to Hack Out Point is " + df.format(loc.distanceTo(hackOutLoc)) + " metres\n";
			}
			
			if (distanceToClosestMine() <= PROXIMITY) {
				text = "You just walked over the land mine.  Game Over.";
				builder.setMessage("You just got blown up.\n  Why would you get blown up?\n" +
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
		
		
		
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
		
	}
	
	private float distanceToClosestMine() {
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
