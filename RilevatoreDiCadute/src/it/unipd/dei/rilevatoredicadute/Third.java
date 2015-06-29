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
	private SensorManager mysm = null;
	private Sensor accel = null;;
	private ArrayList<AccelData> acData = new ArrayList<AccelData>(1000);
	private long it;
	private int i, j = 0;
        Intent MA;
	private EditText et;
	int s;
	long pauseTime=0;		
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
		Log.v("TAG", "----INIZIO-THIRD----");
		db = new MyDBManager(this);
		et = (EditText)findViewById(R.id.insTesto);			
		final ImageButton playBtn = (ImageButton)findViewById(R.id.start);
		final ImageButton pauseBtn = (ImageButton)findViewById(R.id.pause);
		final ImageButton stopBtn = (ImageButton)findViewById(R.id.stop);
		xAccViewS= (TextView) findViewById(R.id.xDataS);
		yAccViewS= (TextView) findViewById(R.id.yDataS);
		zAccViewS= (TextView) findViewById(R.id.zDataS);
		chronometer = (Chronometer) findViewById(R.id.chronometer);
		pauseBtn.setVisibility(View.INVISIBLE);				
		
		playBtn.setOnClickListener(new View.OnClickListener() {					
			@Override
			public void onClick(View v) {
				
				s=1;
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
				
					Log.v("List-1","--HO PREMUTO IL TASTO PLAY---");				
				Log.v("nomeSessioneThird--->",""+et.getText().toString()+"");
				Intent UIMA;    		
				UIMA = new Intent(getApplicationContext(), MainActivity.class);				
				UIMA.putExtra(MainActivity.PACKAGE_NAME+".state", s);
				Log.v("stato sessione third", ""+s+"");	
				//pauseTime=SystemClock.elapsedRealtime()-chronometer.getBase();					
				//Log.v("valore pauseTime","sec"+pauseTime/1000 % 60+"minuti"+((pauseTime / (1000*60)) % 60)+"ore"+((pauseTime / (1000*60*60)) % 24)+"");
				
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
				s=2;
				playBtn.setVisibility(View.VISIBLE);
				pauseBtn.setVisibility(View.INVISIBLE);
				Log.v("List-2","--HO PREMUTO IL TASTO PAUSE--");				
			    stopTime = SystemClock.elapsedRealtime();			    
			    Intent UIMA;
			    UIMA = new Intent(getApplicationContext(), MainActivity.class);
			    UIMA.putExtra(MainActivity.PACKAGE_NAME+".state", i);	
			    pauseTime=SystemClock.elapsedRealtime()-chronometer.getBase();
			    Log.v("valore pauseTime","sec>"+(pauseTime/1000 % 60)+"<minuti>"+((pauseTime / (1000*60)) % 60)+"<ore"+((pauseTime / (1000*60*60)) % 24)+"");
				UIMA.putExtra(MainActivity.PACKAGE_NAME+".StopTime", (chronometer.getBase()-(chronometer.getBase()-pauseTime)));
				chronometer.stop();
				//Log.v("tempo in pausa",""+chronometer.getBase()+"");
				//Log.v("valore pauseTime",""+pauseTime+"");
				Log.v("stato sessione third", ""+i+"");
				//long ddd=SystemClock.elapsedRealtime() - chronometer.getBase();
				//Log.v("valore durata di prova", "secondi--"+ddd/1000 % 60+"minuti--"+((ddd / (1000*60)) % 60)+"ore--"+((ddd / (1000*60*60)) % 24)+"");
				Log.v("CRONOMETRO-GETSTRING",""+chronometer.getText().toString()+"");
			    
			    cdView.cancel();
			    pause();
			}			
			
		});
		
		stopBtn.setOnClickListener(new View.OnClickListener(){			
			@Override
			public void onClick(View v){
				//update della durata della sessione	
				if(s==1)
					pauseTime=SystemClock.elapsedRealtime()-chronometer.getBase();
				s=0;
				Log.v("List-3","--HO PREMUTO IL TASTO STOP--");
				Long saveTime = pauseTime;//SystemClock.elapsedRealtime() - (chronometer.getBase());//-(chronometer.getBase()-pauseTime));
		        int seconds = (int)(saveTime/1000 % 60);
		        int minutes = (int) ((saveTime / (1000*60)) % 60);
				int hours   = (int) ((saveTime / (1000*60*60)) % 24);
		        
				String ora=""+hours+":"+minutes+":"+seconds+"";
				Log.v("durataSessione",ora);
				Log.v("valore pauseTime","sec>"+(pauseTime/1000 % 60)+"<minuti>"+((pauseTime / (1000*60)) % 60)+"<ore"+((pauseTime / (1000*60*60)) % 24)+"");
				db.updateDurataSessione(ora,et.getText().toString());
				Log.v("stopSessione------->",et.getText().toString());				
				chronometer.stop();
				Intent UIMA;
			    UIMA = new Intent(getApplicationContext(), MainActivity.class);					
			    UIMA.putExtra(MainActivity.PACKAGE_NAME+".state", s);
			    UIMA.putExtra(MainActivity.PACKAGE_NAME+".StopTime", pauseTime);
			    Log.v("stato sessione third", ""+s+"");
			    //long ddd=pauseTime;
				//Log.v("valore durata di prova", "secondi--"+ddd/1000 % 60+"minuti--"+((ddd / (1000*60)) % 60)+"ore--"+((ddd / (1000*60*60)) % 24)+"");
				Log.v("CRONOMETRO-GETSTRING",""+chronometer.getText().toString()+"");			
				
				cdSave.cancel();
				cdView.cancel();
				stop();
                                startActivity(UIMA);
			}			
			
		});		
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		Log.v("ACT-THIRD","PAUSED");
	}	

	@Override
	protected void onStop(){
		super.onStop();
		db.close();		
		Log.v("ACT-THIRD","STOPPED");		
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.		
		super .onCreateOptionsMenu(menu);
		//MenuItem meIt1 = 
		menu.add(0, R.id.mostra, 1, "Mostra Sessioni");		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch(item.getItemId()){
			case(R.id.mostra):{				  
				if(i==1)
					pauseTime=SystemClock.elapsedRealtime()-chronometer.getBase();
				Intent UIMA;
				UIMA = new Intent(getApplicationContext(), MainActivity.class);
				UIMA.putExtra(MainActivity.PACKAGE_NAME+".StopTime", pauseTime);//(chronometer.getBase()-(chronometer.getBase()-pauseTime)));
				Log.v("---","---");
				Log.v("valore pauseTime","sec>"+(pauseTime/1000 % 60)+"<minuti>"+((pauseTime / (1000*60)) % 60)+"<ore"+((pauseTime / (1000*60*60)) % 24)+"");
				UIMA.putExtra(MainActivity.PACKAGE_NAME+".nameSession", et.getText().toString());
				UIMA.putExtra(MainActivity.PACKAGE_NAME+".state", i);					
				chronometer.stop();
				startActivity(UIMA);
			break;
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
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