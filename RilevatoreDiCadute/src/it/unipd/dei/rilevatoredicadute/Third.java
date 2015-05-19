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
import android.util.Log;

import java.util.GregorianCalendar;

public class Third extends ActionBarActivity {
	
	//Dati da = new Dati();
	GregorianCalendar cal;
	boolean play=true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_third);
		
				
		final EditText et = (EditText)findViewById(R.id.insTesto);		
		
		final ImageButton playBtn = (ImageButton)findViewById(R.id.start);
		final ImageButton pauseBtn = (ImageButton)findViewById(R.id.pause);
		final ImageButton stopBtn = (ImageButton)findViewById(R.id.stop);
		pauseBtn.setVisibility(View.INVISIBLE);
		
		playBtn.setOnClickListener(new View.OnClickListener() {					
			@Override
			public void onClick(View v) {
				cal= new GregorianCalendar();			
				if(play){
				playBtn.setVisibility(View.INVISIBLE);
				pauseBtn.setVisibility(View.VISIBLE);	
				
				//da.setData(cal.get(GregorianCalendar.YEAR), cal.get(GregorianCalendar.MONTH)+1, cal.get(GregorianCalendar.DATE));
				String data = ""+cal.get(GregorianCalendar.YEAR)+ "/" + (cal.get(GregorianCalendar.MONTH)+1)+ "/" +cal.get(GregorianCalendar.DATE);
				
				//da.setHour(cal.get(GregorianCalendar.HOUR_OF_DAY), cal.get(GregorianCalendar.MINUTE), cal.get(GregorianCalendar.SECOND));
				String ora = ""+cal.get(GregorianCalendar.HOUR_OF_DAY)+ ":" + cal.get(GregorianCalendar.MINUTE)+ ":" +cal.get(GregorianCalendar.SECOND);
				
				MyDBManager db = new MyDBManager(getApplicationContext());
				db.addSessione(et.getText().toString(), data, ora, "0:0:0", 0);
				db.close();
				 Intent UI2 = new Intent(getApplicationContext(), MainActivity.class);
		         	startActivity(UI2);
				play=false;
				Log.v("List","ho premuti il tasto play");
				}
				else{
					System.out.println("okokokokokokokokokokk");
				}			 
			}
			
			
		});
		
		pauseBtn.setOnClickListener(new View.OnClickListener(){
			
			@Override
			public void onClick(View v){
				playBtn.setVisibility(View.VISIBLE);
				pauseBtn.setVisibility(View.INVISIBLE);
				play=true;
				Log.v("List","ho premuti il tasto pause");
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