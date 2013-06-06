package com.example.runspyrun;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class Menu extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
		
		ImageButton a = (ImageButton) findViewById(R.id.iB_profile);
		a.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(com.example.runspyrun.Menu.this, com.example.runspyrun.Profile.class));	
			}
		});
		
		ImageButton b = (ImageButton) findViewById(R.id.iB_rank);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(com.example.runspyrun.Menu.this, com.example.runspyrun.Ranking.class));	
			}
		});
		
		ImageButton c = (ImageButton) findViewById(R.id.iB_settings);
		c.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(com.example.runspyrun.Menu.this, com.example.runspyrun.Setting.class));	
			}
		});
		
		ImageButton d = (ImageButton) findViewById(R.id.iB_credit);
		d.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(com.example.runspyrun.Menu.this, com.example.runspyrun.Credit.class));	
			}
		});
		
		ImageButton e = (ImageButton) findViewById(R.id.iB_findgame);
		e.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(com.example.runspyrun.Menu.this, com.example.runspyrun.AttackDefend.class));	
			}
		});
		
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
