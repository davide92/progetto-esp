package it.unipd.dei.rilevatoredicadute;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageButton;
import android.content.Intent;
import android.util.Log;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import java.util.GregorianCalendar;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.widget.Toast;
import android.widget.Chronometer;

public class SessioneCorrente extends ActionBarActivity implements SensorEventListener, LocationListener{	
	
	private Chronometer chrono;	
	GregorianCalendar cal;
	MyDBManager db;		
	private Long[] tempoStop={0L};
	Intent intent;
	Intent back;
	private TextView tx;
	private String nomeSessione;
	int statoSessione;
	long tempoPausa;
	long p=0;
	private SensorManager mysm;
	private LocationManager locMg = null;
	private Sensor accel;
	private ArrayList<AccelData> acData = new ArrayList<AccelData>(1000);
	private long it;
	private int i = 0;
	private int j = 0;
	private int k = 1;
	int year;
	int month;
	int day;
	TextView xAccViewS = null;
	TextView yAccViewS = null;
	TextView zAccViewS = null;
	DataOutputStream f = null;
	FileOutputStream fo = null;
	File file;
	String lastFileName = "null";
	String date,data;	
	CountDownTimer cdSaveSC = null;
	CountDownTimer cdViewSC = null;		
	private boolean rec, vis = true;
	float[] DaAcc = new float[3];
	long TemAppStop=0;
	boolean stoppata=false;
	ImageButton playBtn;
	ImageButton pauseBtn;
	ImageButton stopBtn;
	long sss;
	long temp;
	Intent mService = null;
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {	    
	    savedInstanceState.putLong("ti_sec", chrono.getBase());
	    savedInstanceState.putInt("stasess", statoSessione);
	    savedInstanceState.putString("data", date);
	    Log.w("SC__PA__CGB",""+chrono.getBase()+"");
	    super.onSaveInstanceState(savedInstanceState);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_sess_curr);
		
		cdSaveSC = new CountDownTimer(5000L, 1000L) {					
			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub
				//remainingtimeS= millisUntilFinished /1000;
				}					
			@Override
			public void onFinish() {
				rec = true;	
				Log.v("countdown","finito");
			}
		};
		
		cdViewSC = new CountDownTimer(21000L, 1000L) {					
			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub
				//remainingtimeW= millisUntilFinished /1000;
			}					
			@Override
			public void onFinish() {
				vis = true;	
				Log.v("countdownView","finito");
			}
		};
		
		cal=new GregorianCalendar();
		chrono = (Chronometer) findViewById(R.id.chronometer);
		playBtn = (ImageButton)findViewById(R.id.start);
		pauseBtn = (ImageButton)findViewById(R.id.pause);
		intent=getIntent();
		if((savedInstanceState !=null)){
			//chronometer.stop();
			data=savedInstanceState.getString("data");
			chrono.setBase(savedInstanceState.getLong("ti_sec"));
			statoSessione=savedInstanceState.getInt("stasess");
			Log.w("SC__ONCNN__CGB",""+savedInstanceState.getLong("ti_sec")+"");
			if(statoSessione==1){
				playBtn.setVisibility(View.INVISIBLE);
				pauseBtn.setVisibility(View.VISIBLE);
				chrono.start();
				cdSaveSC.start();
				cdViewSC.start();
			}else{
				playBtn.setVisibility(View.VISIBLE);
				pauseBtn.setVisibility(View.INVISIBLE);
			}
		}else{
			tempoStop[0] =intent.getLongExtra(MainActivity.PACKAGE_NAME+".TempoPausa",0);
			statoSessione = intent.getIntExtra(MainActivity.PACKAGE_NAME+".statoSessione", 0);
			Log.w("SC__ONCN__CGB",""+tempoStop[0]+"");
		}
		
	}
	
	
	
	@Override
	protected void onStart(){
		super.onStart();
		Log.v("TAG", "----INIZIO-SESSIONE-CORRENTE----");
		db = new MyDBManager(this);						
		playBtn = (ImageButton)findViewById(R.id.start);
		pauseBtn = (ImageButton)findViewById(R.id.pause);
		stopBtn = (ImageButton)findViewById(R.id.stop);
		xAccViewS= (TextView) findViewById(R.id.xDataS);
		yAccViewS= (TextView) findViewById(R.id.yDataS);
		zAccViewS= (TextView) findViewById(R.id.zDataS);
		tx=(TextView)findViewById(R.id.TestoSessCurr);
		//chronometer = (Chronometer) findViewById(R.id.chronometer);		
		//intent=getIntent();		
		//tempoStop[0] =intent.getLongExtra(MainActivity.PACKAGE_NAME+".TempoPausa",0);
		nomeSessione = intent.getStringExtra(MainActivity.PACKAGE_NAME+".nomeSessione");
		//statoSessione = intent.getIntExtra(MainActivity.PACKAGE_NAME+".statoSessione", 0);				
		tx.setText(nomeSessione);		
		
		Log.v("TAG__SC__SessioneCorrenteNome", ""+nomeSessione+"");
		Log.v("TAG__SC__tempoStop","sec>"+(tempoStop[0]/1000 % 60)+"<minuti>"+((tempoStop[0] / (1000*60)) % 60)+"<ore>"+((tempoStop[0] / (1000*60*60)) % 24)+"");
		Log.v("TAG__SC__statoSessione", ""+statoSessione+"");
		if(nomeSessione!=null){
			if(stoppata==false){				
				if(statoSessione==1){
					playBtn.setVisibility(View.INVISIBLE);
					pauseBtn.setVisibility(View.VISIBLE);					
					chrono.setBase(SystemClock.elapsedRealtime()-tempoStop[0]);					
					chrono.start();					
					cdSaveSC.start();
					cdViewSC.start();
					start();
				}
				else{
					if(statoSessione==2){
						playBtn.setVisibility(View.VISIBLE);
						pauseBtn.setVisibility(View.INVISIBLE);
						chrono.setBase( tempoStop[0]);
						tempoPausa=chrono.getBase();
					}
				}
			}else{				
				sss=SystemClock.elapsedRealtime()-TemAppStop;
				chrono.setBase(sss);				
				chrono.start();
				 int seconds = (int)(sss/1000 % 60);
			      int minutes = (int) ((sss / (1000*60)) % 60);
			      int hours   = (int) ((sss / (1000*60*60)) % 24);
			      Log.v("SC__ORA__RITORNO__BACK",""+hours+"/"+minutes+"/"+seconds+"");
			    long kkk=SystemClock.elapsedRealtime() - temp;
			    int sec = (int)(kkk/1000 % 60);
			      int min = (int) ((kkk / (1000*60)) % 60);
			      int hou   = (int) ((kkk / (1000*60*60)) % 24);
			      Log.v("SC__ORA__CONTROLLO__BACK",""+hou+"/"+min+"/"+sec+"");  
			 }
		}
		
		
		playBtn.setOnClickListener(new View.OnClickListener() {					
			@Override
			public void onClick(View v) {
				
				statoSessione=1;
				cal= new GregorianCalendar();								
				playBtn.setVisibility(View.INVISIBLE);
				pauseBtn.setVisibility(View.VISIBLE);
				//stoppata=true;
								
				//GESTIONE PLAY/RESUME CRONOMETRO
				if ( p == 0 )
			        chrono.setBase( SystemClock.elapsedRealtime()-tempoStop[0]);
			    // on resume after pause
			    else
			    {
			        long intervalloPausa = (SystemClock.elapsedRealtime() -p);// tempoStop[0]);
			        chrono.setBase( chrono.getBase() + intervalloPausa );
			    }
			    chrono.start();
			    //FINE GESTIONE	
			    cdSaveSC.start();/* = new CountDownTimer(5000L, 1000L) {					
					@Override
					public void onTick(long millisUntilFinished) {
						// TODO Auto-generated method stub						
					}					
					@Override
					public void onFinish() {
						rec = true;	
						Log.v("countdown","finito");
					}
				}.start();*/
				
				cdViewSC.start();/* = new CountDownTimer(21000L, 1000L) {					
					@Override
					public void onTick(long millisUntilFinished) {
						// TODO Auto-generated method stub						
					}					
					@Override
					public void onFinish() {
						vis = true;	
						Log.v("countdownView","finito");
					}
				}.start();*/
			    start();
			}			
		});
		
		pauseBtn.setOnClickListener(new View.OnClickListener(){			
			@Override
			public void onClick(View v){
				playBtn.setVisibility(View.VISIBLE);
				pauseBtn.setVisibility(View.INVISIBLE);
				Log.v("List","ho premuti il tasto pause");
				tempoPausa=SystemClock.elapsedRealtime()- chrono.getBase();
				chrono.stop();
			    p = SystemClock.elapsedRealtime();
			    statoSessione=2;
			    pause();
			}			
			
		});
		
		stopBtn.setOnClickListener(new View.OnClickListener(){			
			@Override
			public void onClick(View v){
				//update della durata della sessione
				if(statoSessione==1)					
					tempoPausa=SystemClock.elapsedRealtime() - chrono.getBase();			
					
				Long saveTime = tempoPausa;// +tempoStop[0];
		        int seconds = (int)(saveTime/1000 % 60);
		        int minutes = (int) ((saveTime / (1000*60)) % 60);
				int hours   = (int) ((saveTime / (1000*60*60)) % 24);		        
				String ora=""+hours+":"+minutes+":"+seconds+"";
				Log.v("durataSessione",ora);
				db.updateDurataSessione(ora,tx.getText().toString());
				chrono.stop();					
				Log.v("stopSessione------->",tx.getText().toString());		
				statoSessione=0;
				stop();
				stoppata=false;
				Intent back = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(back);				
			}						
		});		
	}	
	
	@Override
	protected void onStop() 
	{
	    super.onStop();
	    TemAppStop = chrono.getBase();	
	    temp = SystemClock.elapsedRealtime();
        int seconds = (int)(TemAppStop/1000 % 60);
        int minutes = (int) ((TemAppStop / (1000*60)) % 60);
		int hours   = (int) ((TemAppStop / (1000*60*60)) % 24);
	    Log.v("VAL_TEMPappSTOP",""+hours+"/"+minutes+"/"+seconds+"");
	    int sec = (int)(temp/1000 % 60);
        int min = (int) ((temp / (1000*60)) % 60);
		int hou  = (int) ((temp / (1000*60*60)) % 24);
	    Log.v("VAL_temp",""+hou+"/"+min+"/"+sec+"");
	    stoppata=true;
	    Log.v("SESSIONE-CORRENTE","STOPPATA");
	    
	}
	
	@Override
	protected void onDestroy() 
	{
	    super.onDestroy();
	    if (db != null) 
	    {
	        db.close();
	    }
	}

	/*@Override
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
		switch (item.getItemId()) {
		case R.id.mostra:			
			startActivity(new Intent(this, MainActivity.class));
			break;
		}	
		return super.onOptionsItemSelected(item);
	}*/
	
	
//INIZIO GESTIONE SENSORE ACCELEROMETRO	
private void start(){
		
		locMg = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		mysm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		if(mysm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
			accel = mysm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			mysm.registerListener((SensorEventListener) this, accel, SensorManager.SENSOR_DELAY_NORMAL);			
		//if(mysm.registerListener((SensorEventListener) this, accel, SensorManager.SENSOR_DELAY_NORMAL)){
			year = cal.get(GregorianCalendar.YEAR);
			month = cal.get(GregorianCalendar.MONTH)+1;
			day = cal.get(GregorianCalendar.DAY_OF_MONTH);
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
					if(acData.size() > 2){
						mService = new Intent(getApplicationContext(), FindFall.class);
						if(locMg.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null){
							double longitude = locMg.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
							double latitude = locMg.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
							mService.putExtra("long", longitude);
							mService.putExtra("lat", latitude);
						}
						mService.putExtra("xVal", acData.get(k).getX());
						mService.putExtra("yVal", acData.get(k).getY());
						mService.putExtra("zVal", acData.get(k).getZ());
						mService.putExtra("xValLast", acData.get(k-1).getX());
						mService.putExtra("yValLast", acData.get(k-1).getY());
						mService.putExtra("zValLast", acData.get(k-1).getZ());
						startService(mService);
						k++;								
					}
					cdSaveSC.start();
				}
				
				if(vis && !acData.isEmpty() && j<acData.size()){
					xAccViewS.setText("" + acData.get(j).getX());
					yAccViewS.setText("" + acData.get(j).getY());
					zAccViewS.setText("" + acData.get(j).getZ());
					j+=4;
					vis = false;
					cdViewSC.start();
				}
				
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	//	Log.d("Accelerometro", "onAccurancyChanged: " + sensor + ", accuracy: " + accuracy);
		
	}
	
	@Override
	public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub			
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub			
	}
	
	@Override
	public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub			
	}
	
	@Override
	public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
	}
}