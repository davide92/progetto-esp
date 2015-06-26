package it.unipd.dei.rilevatoredicadute;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import android.widget.Chronometer;


public class Third extends ActionBarActivity implements SensorEventListener{
	
	private Chronometer chronometer;
	GregorianCalendar cal;
	MyDBManager db;
	private long stopTime=0;
	private boolean playable;
	//String pkg=getPackageName();
	private SensorManager mysm = null;
	private Sensor accel = null;;
	private ArrayList<AccelData> acData = new ArrayList<AccelData>(1000);
	private long it;
	private int i, j = 0;
	private boolean rec, vis = true;
	TextView xAccViewS = null;
	TextView yAccViewS = null;
	TextView zAccViewS = null;
	DataOutputStream f = null;
	FileOutputStream fo = null;
	File file;
	String lastFileName = "null";
	String date;
	CountDownTimer cdSave = null;
	CountDownTimer cdView = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_third);
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		//Intent intent=getIntent();
		//String pkg="it.unipd.dei.rilevatoredicadute";    
		//final String nS=intent.getStringExtra(pkg+".nameSession");
		db = new MyDBManager(this);
			
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
				
				playable=false;
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
				db.addSessione(et.getText().toString(), data, ora, "XX:XX:XX", 0);
				Log.v("ora1",""+cal.get(GregorianCalendar.HOUR_OF_DAY)+ ":" + cal.get(GregorianCalendar.MINUTE)+ ":" +cal.get(GregorianCalendar.SECOND));
				Log.v("ora2",""+hours+""+minutes+""+seconds+"");
				db.close();  
				
				//GESTIONE PLAY/RESUME CRONOMETRO
				if ( stopTime == 0 )
			        chronometer.setBase( SystemClock.elapsedRealtime() );
			    // on resume after pause
			    else
			    {
			        long intervalloPausa = (SystemClock.elapsedRealtime() - stopTime);
			        chronometer.setBase( chronometer.getBase() + intervalloPausa );
			    }
			    chronometer.start();
				
			    //FINE GESTIONE			    
				
				//String pkg=getPackageName();				
				//UI2 = new Intent(getApplicationContext(), MainActivity.class);
				//UI2.putExtra(pkg+".myPlay", play); 	
				//startActivity(UI2);					
				Log.v("List","ho premuti il tasto play");	
				Log.v("tastoPlaySessione------->",et.getText().toString());
				onBackPressed();
				
				cdSave = new CountDownTimer(5000L, 1000L) {
					
					@Override
					public void onTick(long millisUntilFinished) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onFinish() {
						rec = true;
						
					}
				}.start();
				
				cdView = new CountDownTimer(21000L, 1000L) {
					
					@Override
					public void onTick(long millisUntilFinished) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onFinish() {
						vis = true;
						
					}
				}.start();
				start();
			}						
		});
		
		pauseBtn.setOnClickListener(new View.OnClickListener(){			
			@Override
			public void onClick(View v){
				playBtn.setVisibility(View.VISIBLE);
				pauseBtn.setVisibility(View.INVISIBLE);
				Log.v("List","ho premuti il tasto pause");
				chronometer.stop();
			    stopTime = SystemClock.elapsedRealtime();
			    onBackPressed();
			    
			    cdView.cancel();
			    pause();
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
				playable=true;
				onBackPressed();
				
				cdSave.cancel();
				cdView.cancel();
				stop();
			}			
			
		});		
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		Log.v("pausaNuovaSessione","!!!!111!!1!!!!!!!!!!1111");
		Intent UISEC;    		
		UISEC = new Intent(getApplicationContext(), SessioneCorrente.class);    		
		UISEC.putExtra(MainActivity.PACKAGE_NAME+".stopTime", chronometer.getBase());
		Log.v("TEMPO CRONOMETRATO", ""+chronometer.getBase()+"");
	}	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.third, menu);
		//return true;
		super .onCreateOptionsMenu(menu);
		MenuItem meIt1 = menu.add(0, R.id.mostra, 1, "Mostra Sessioni");
		//Intent UIM;    		
		//UIM = new Intent(getApplicationContext(), MainActivity.class);    		
		//UIM.putExtra(MainActivity.PACKAGE_NAME+".playable", playable);
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
	
	@Override
	public void onBackPressed(){//non testato
		if(playable){
			super.onBackPressed();//back button funziona 
		}else{
			//back button bloccato
		}
	}
	
	private void start(){
		
		mysm = (SensorManager) getSystemService(SENSOR_SERVICE);
		accel = mysm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mysm.registerListener((SensorEventListener) this, accel, SensorManager.SENSOR_DELAY_NORMAL);
		if(mysm.registerListener((SensorEventListener) this, accel, SensorManager.SENSOR_DELAY_NORMAL)){
			int year = cal.get(GregorianCalendar.YEAR);
			int month = cal.get(GregorianCalendar.MONTH)+1;
			int day = cal.get(GregorianCalendar.DAY_OF_MONTH);
			//int hour = c.get(GregorianCalendar.HOUR_OF_DAY);
			//int min = c.get(GregorianCalendar.MINUTE);
			//int sec= c.get(GregorianCalendar.SECOND);
			date = "" + year + month + day /*+ hour + min + sec */;
									
		}
				
	}
	
	private void pause(){
		if(mysm != null){
			mysm.unregisterListener(this);			
		}
		xAccViewS.setText("0");
		yAccViewS.setText("0");
		zAccViewS.setText("0");
		
		if(!(lastFileName.equals(date))){
			lastFileName = date;
			try {
				fo = openFileOutput(date, Context.MODE_PRIVATE);
				fo.close();
			} catch (FileNotFoundException e) {
				Log.e("Impossibile trovare il file ", date, e);
				Toast.makeText(	this, "Errore openFileOutput", Toast.LENGTH_LONG).show();
			}catch (IOException e) {
				Log.e("Impossibile chiudere il file", date, e); 
				Toast.makeText(	this, "Errore chiusura", Toast.LENGTH_LONG).show();
			}
			
		}
		
		if(i < acData.size()){
			try {
				fo = openFileOutput(date, Context.MODE_APPEND);
				while(i < acData.size()){
					fo.write(("" + acData.get(i).getT() + " " + acData.get(i).getX() + " " + acData.get(i).getY() + " " + acData.get(i).getZ() + '\n').getBytes());
					i++;
				}
			} catch (FileNotFoundException e) {
				Log.e("Impossibile trovare il file ", date, e);
				Toast.makeText(	this, "Errore openFileOutput", Toast.LENGTH_LONG).show();
			}catch (IOException e) {
				Log.e("Impossibile scrivere sul file", date, e); 
				Toast.makeText(	this, "Errore scrittura", Toast.LENGTH_LONG).show();
			}
			try {
				fo.close();
			} catch (IOException e) {
				Log.e("Impossibile chiudere il file", date, e); 
				Toast.makeText(	this, "Errore chiusura", Toast.LENGTH_LONG).show(); 
			}
		}
	}
	
	private void stop(){
		if(mysm != null){
			mysm.unregisterListener(this);
		}
		
		if(!(lastFileName.equals(date))){
			lastFileName = date;			
			try {
				fo = openFileOutput(date, Context.MODE_PRIVATE);
				fo.close();
			} catch (FileNotFoundException e) {
				Log.e("Impossibile trovare il file ", date, e);
				Toast.makeText(	this, "Errore openFileOutput", Toast.LENGTH_LONG).show();
			}catch (IOException e) {
				Log.e("Impossibile chiudere il file", date, e); 
				Toast.makeText(	this, "Errore chiusura", Toast.LENGTH_LONG).show();
			}
			
		}
		
		if(i < acData.size()){
			try {
				fo = openFileOutput(date, Context.MODE_APPEND);
				while(i < acData.size()){
					fo.write(("" + acData.get(i).getT() + " " + acData.get(i).getX() + " " + acData.get(i).getY() + " " + acData.get(i).getZ() + '\n').getBytes());
					i++;
				}
			} catch (FileNotFoundException e) {
				Log.e("Impossibile trovare il file ", date, e);
				Toast.makeText(	this, "Errore openFileOutput", Toast.LENGTH_LONG).show();
			}catch (IOException e) {
				Log.e("Impossibile scrivere sul file", date, e); 
				Toast.makeText(	this, "Errore scrittura", Toast.LENGTH_LONG).show();
			}
			try {
				fo.close();
			} catch (IOException e) {
				Log.e("Impossibile chiudere il file", date, e); 
				Toast.makeText(	this, "Errore chiusura", Toast.LENGTH_LONG).show(); 
			}
		}
		
	

		acData.clear();
		i = j = 0;
		
		xAccViewS.setText("0");
		yAccViewS.setText("0");
		zAccViewS.setText("0");
		accel = null;
		
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		synchronized (this) {
			if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
				
				if(rec){
					float x = event.values[0];
					float y = event.values[1];
					float z = event.values[2];
					AccelData d = new AccelData(System.currentTimeMillis()-it, x, y, z);
					acData.add(d);
					rec = false;
					cdSave.start();
				}
				
				if(vis && !acData.isEmpty()){
					xAccViewS.setText("" + acData.get(j).getX());
					yAccViewS.setText("" + acData.get(j).getY());
					zAccViewS.setText("" + acData.get(j).getZ());
					j+=4;
					vis = false;
					cdView.start();
				}
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		Log.d("Accelerometro", "onAccurancyChanged: " + sensor + ", accuracy: " + accuracy);
		
	}
}