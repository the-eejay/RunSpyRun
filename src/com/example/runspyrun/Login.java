package com.example.runspyrun;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;


//Activity that allows users to login.  Currently just contains textboxes and links to
//Registration and Menu.  Proper login code to added later
public class Login extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Gets rid of title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //sets layout
		setContentView(R.layout.activity_login);
		
		//Creates login button
		ImageButton b = (ImageButton) findViewById(R.id.iB_login);
		b.setOnClickListener(new OnClickListener() {
			//Listener that opens Menu
			@Override
			public void onClick(View v) {
				startActivity(new Intent(com.example.runspyrun.Login.this,com.example.runspyrun.Menu.class));	
			}
		});
		
		//Creates Text that links back to Registration
		TextView a = (TextView) findViewById(R.id.tVregister);
		a.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(com.example.runspyrun.Login.this,com.example.runspyrun.Registration.class));	
			}
		});
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
