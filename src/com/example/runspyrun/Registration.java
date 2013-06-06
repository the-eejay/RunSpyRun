package com.example.runspyrun;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

//Activity that allows users to register, currently just creates textboxes and links to
//Login
public class Registration extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//Sets layout
		setContentView(R.layout.registration);
		
		//Creates button that links to Login
		ImageButton b = (ImageButton) findViewById(R.id.iB_send);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(com.example.runspyrun.Registration.this,com.example.runspyrun.Login.class));	
			}
		});

	}
	
}
