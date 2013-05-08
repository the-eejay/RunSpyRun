package com.example.runspyrun;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {
    
	private EditText longEdit;
	private EditText latEdit;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        longEdit = (EditText)findViewById(R.id.longedit);
        latEdit = (EditText)findViewById(R.id.latedit);
    }
    
    public void makeCourse(View view) {
    	Intent intent = new Intent(this, DefendActivity.class);
    	intent.putExtra("longitude", Double.parseDouble(longEdit.getText().toString()));
    	intent.putExtra("latitude", Double.parseDouble(latEdit.getText().toString()));
    	startActivity(intent);
    }
    
    public void playCourse(View view) {
    	Intent intent = new Intent(this, AttackActivity.class);
    	startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
