package it.unipd.dei.rilevatoredicadute;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.content.Context;
import android.content.Intent;
import android.content.ContentValues;

public class Third extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_third);
		
		EditText et = (EditText)findViewById(R.id.insTesto);		
		
		final ImageButton playBtn = (ImageButton)findViewById(R.id.start);
		final ImageButton pauseBtn = (ImageButton)findViewById(R.id.pause);
		final ImageButton stopBtn = (ImageButton)findViewById(R.id.stop);
		pauseBtn.setVisibility(View.INVISIBLE);
		playBtn.setOnClickListener(new View.OnClickListener() {
					
			@Override
			public void onClick(View v) {
				playBtn.setVisibility(View.INVISIBLE);
				pauseBtn.setVisibility(View.VISIBLE);			

				 /*   MyDatabase mmdb=new MyDatabase(getBaseContext());            
				    mmdb.open();
				    mmdb.insertSessione("sessione prova", "25-04-2015", "17.02", "12.56", "0");				    
				            mmdb.close();*/
			}
			
			
		});
	}

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.third, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}*/
}