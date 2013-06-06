package com.example.runspyrun;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

//Main Menu, contains links to AttackDefend, Gameplay, Settings, Ranking, Setting, Profile and Credit
public class Menu extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Set layout
		setContentView(R.layout.menu);
		
		//Add Profile button and make it link to Profile
		ImageButton a = (ImageButton) findViewById(R.id.iB_profile);
		a.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(com.example.runspyrun.Menu.this, com.example.runspyrun.Profile.class));	
			}
		});
		
		//Add Rankings button and make it link to Ranking
		ImageButton b = (ImageButton) findViewById(R.id.iB_rank);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(com.example.runspyrun.Menu.this, com.example.runspyrun.Ranking.class));	
			}
		});
		
		//Add Profile Settings and make it link to Setting
		ImageButton c = (ImageButton) findViewById(R.id.iB_settings);
		c.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(com.example.runspyrun.Menu.this, com.example.runspyrun.Setting.class));	
			}
		});
		
		//Add Credits button and make it link to Credit
		ImageButton d = (ImageButton) findViewById(R.id.iB_credit);
		d.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(com.example.runspyrun.Menu.this, com.example.runspyrun.Credit.class));	
			}
		});
		
		//Add FindGame button and make it link to AttackDefend
		ImageButton e = (ImageButton) findViewById(R.id.iB_findgame);
		e.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(com.example.runspyrun.Menu.this, com.example.runspyrun.AttackDefend.class));	
			}
		});
		
		//Add Gameplay (help) button and make it link to Gameplay
		ImageButton f = (ImageButton) findViewById(R.id.iB_gameplay);
		f.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Dialog c = new Dialog(Menu.this);
				c.setContentView(R.layout.hound);
				c.show();
			}
		});
		
	}
	
}
