package it.unipd.dei.rilevatoredicadute;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.os.SystemClock;

import java.util.GregorianCalendar;

import android.widget.Chronometer;

import java.lang.Object;

public class Third extends ActionBarActivity {
	
	private Chronometer chronometer;
	GregorianCalendar cal;
	MyDBManager db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_third);
		//Intent intent=getIntent();
		//String pkg="it.unipd.dei.rilevatoredicadute";    
		//final String nS=intent.getStringExtra(pkg+".nameSession");
		db = new MyDBManager(/*getApplicationContext()*/this);
			
		final EditText et = (EditText)findViewById(R.id.insTesto);			
		final ImageButton playBtn = (ImageButton)findViewById(R.id.start);
		final ImageButton pauseBtn = (ImageButton)findViewById(R.id.pause);
		final ImageButton stopBtn = (ImageButton)findViewById(R.id.stop);
		chronometer = (Chronometer) findViewById(R.id.chronometer);
		pauseBtn.setVisibility(View.INVISIBLE);
		//final String nomeSessione=et.getText().toString();
		
		playBtn.setOnClickListener(new View.OnClickListener() {					
			@Override
			public void onClick(View v) {
				
				cal= new GregorianCalendar();
								
				playBtn.setVisibility(View.INVISIBLE);
				pauseBtn.setVisibility(View.VISIBLE);	
				
				//da.setData(cal.get(GregorianCalendar.YEAR), cal.get(GregorianCalendar.MONTH)+1, cal.get(GregorianCalendar.DATE));
				String data = ""+cal.get(GregorianCalendar.YEAR)+ "/" + (cal.get(GregorianCalendar.MONTH)+1)+ "/" +cal.get(GregorianCalendar.DATE);
				
				//da.setHour(cal.get(GregorianCalendar.HOUR_OF_DAY), cal.get(GregorianCalendar.MINUTE), cal.get(GregorianCalendar.SECOND));
				//String ora = ""+cal.get(GregorianCalendar.HOUR_OF_DAY)+ ":" + cal.get(GregorianCalendar.MINUTE)+ ":" +cal.get(GregorianCalendar.SECOND);
				
				
				long milliseconds=System.currentTimeMillis();
				int seconds = (int) (milliseconds / 1000) % 60 ;
				int minutes = (int) ((milliseconds / (1000*60)) % 60);
				int hours   = (int) ((milliseconds / (1000*60*60)) % 24);
				String ora = ""+hours+ ":" + minutes+ ":" +seconds+"";				
				db.addSessione(et.getText().toString(), data, ora, "0:0:0", 0);
				Log.v("ora1",""+cal.get(GregorianCalendar.HOUR_OF_DAY)+ ":" + cal.get(GregorianCalendar.MINUTE)+ ":" +cal.get(GregorianCalendar.SECOND));
				Log.v("ora2",""+hours+""+minutes+""+seconds+"");
				db.close();	
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
				
				//String pkg=getPackageName();				
				//UI2 = new Intent(getApplicationContext(), MainActivity.class);
				//UI2.putExtra(pkg+".myPlay", play); 	
				//startActivity(UI2);		
				
				Log.v("List","ho premuti il tasto play");	
				Log.v("tastoPlaySessione------->",et.getText().toString());
			}
			
			
		});
		
		pauseBtn.setOnClickListener(new View.OnClickListener(){			
			@Override
			public void onClick(View v){
				playBtn.setVisibility(View.VISIBLE);
				pauseBtn.setVisibility(View.INVISIBLE);
				Log.v("List","ho premuti il tasto pause");
				chronometer.stop();
			}			
			
		});
		
		stopBtn.setOnClickListener(new View.OnClickListener(){			
			@Override
			public void onClick(View v){
				//update della durata della sessione
				
				Intent back = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(back);
				Long saveTime = SystemClock.elapsedRealtime() - chronometer.getBase();
		        int seconds = (int)(saveTime/1000 % 60);
		        int minutes = (int) ((saveTime / (1000*60)) % 60);
				int hours   = (int) ((saveTime / (1000*60*60)) % 24);
		        
				String ora=""+hours+":"+minutes+":"+seconds+"";
				Log.v("durataSessione",ora);
				db.updateDurataSessione(ora,et.getText().toString());
				Log.v("stopSessione------->",et.getText().toString());
			}			
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.third, menu);
		//return true;
		super .onCreateOptionsMenu(menu);
		MenuItem meIt1 = menu.add(0, R.id.mostra, 1, "Mostra Sessioni");
		meIt1.setIntent(new Intent(this, MainActivity.class));
		return true;
	}

	/*@Override
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