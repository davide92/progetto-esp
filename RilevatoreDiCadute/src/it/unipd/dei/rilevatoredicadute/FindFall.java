package it.unipd.dei.rilevatoredicadute;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;
//import it.unipd.dei.rilevatoredicadute.ServiceCronometro.MyBinder;



import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import android.os.IBinder;
import android.os.Binder;
import android.os.Bundle;
import android.os.CountDownTimer;

public class FindFall extends Service implements SensorEventListener, LocationListener{
	long it = 0;
	private float xVal;
	private float yVal;
	private float zVal;
	private float xValLast;
	private float yValLast;
	private float zValLast;
	Intent mReceiver = null;
	Intent thActivity = null;
	private float alpha=(float)15;
	String sessione;
	public static final String BROADCAST = "it.unipd.dei.rilevatoredicadute.android.action.broadcast";
	public static final String TEXTVIEW = "Gestione Textview";
	MyDBManager dbF;
	GregorianCalendar calendar;
	double lat,lon;
	IBinder mBinder = new MyBinderText();
	private SensorManager mysm = null;
	private LocationManager locMg = null;
	private Sensor accel = null;
	private ArrayList<AccelData> acData = new ArrayList<AccelData>(15000);	
	DataOutputStream f = null;
	FileOutputStream fo = null;		
	File file;
	String lastFileName = "null";
	CountDownTimer cdSaveSC = null;
	CountDownTimer cdViewSC = null;
	boolean rec, vis = true;
	private double latitude, longitude;
	float[] DatiAccelerometro = new float[3];
	int year;
	int month;
	int day;
	String date;
	int k, i, j;
	
	public FindFall() {
		super();
	}
	
	@Override
	public void onCreate(){
		super.onCreate();
		
		cdSaveSC = new CountDownTimer(2000L, 500L) {					
			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub				
			}					
			@Override
			public void onFinish() {
				rec = true;									
			}
		};			
		cdViewSC = new CountDownTimer(5000L, 500L) {					
			@Override
			public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub			
			}					
			@Override
			public void onFinish() {
				vis = true;					
			}
		};
		
		dbF=new MyDBManager(this);
		start();
	}
	
	@Override
	public int onStartCommand (Intent intent, int flags, int startId) {		
		Log.v("findFall", "inizio onStartCommand");	
		sessione = intent.getStringExtra("nome sessione");
		Log.v("nomesessione service", ""+sessione);
		mReceiver = new Intent(this, MyReceiver.class);	
		cdSaveSC.start();
		cdViewSC.start();
		return START_STICKY;		
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		synchronized (this) {				
			if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){				
				if(rec){					
					float x = event.values[0];
 					float y = event.values[1];
 					float z = event.values[2]; 	 					
 					long time = System.currentTimeMillis()-it;					
 					if(acData.size() >= 1){
 						if(locMg.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null){
							mReceiver.putExtra("long", longitude);
							mReceiver.putExtra("lat", latitude);
						}						
 						xVal = x;
 						yVal = y;
 						zVal = z;
 						xValLast = acData.get(k).getX();
 						yValLast = acData.get(k).getY();
 						zValLast = acData.get(k).getZ();
 						caduta();
						k++;						
					} 					
 					acData.add(new AccelData(time, x, y, z));
					cdSaveSC.start();
				}
				
				if(vis && !acData.isEmpty() && j<acData.size()){
					//xAccViewS.setText("" + acData.get(j).getX());
					//yAccViewS.setText("" + acData.get(j).getY());
					//zAccViewS.setText("" + acData.get(j).getZ());
					//float[] DatiAccelerometro = new float[3];
					DatiAccelerometro[0] = acData.get(j).getX();
					DatiAccelerometro[1] = acData.get(j).getY();
					DatiAccelerometro[2] = acData.get(j).getZ();
					thActivity = new Intent();
					thActivity.setAction(TEXTVIEW);
					thActivity.putExtra("textview", DatiAccelerometro);
					sendBroadcast(thActivity);
					j+=4;
					vis = false;
				    cdViewSC.start();
				}
			}						
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		//Log.d("Accelerometro", "onAccurancyChanged: " + sensor + ", accuracy: " + accuracy);		
	}	
	
	@Override
	public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
		longitude = location.getLongitude();
		latitude = location.getLatitude(); 			
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
	
	private void caduta(){
		float x=java.lang.Math.abs(xVal)- java.lang.Math.abs(xValLast);
		float y=java.lang.Math.abs(yVal)- java.lang.Math.abs(yValLast);
		float z=java.lang.Math.abs(zVal)- java.lang.Math.abs(zValLast);				
		
		if(((java.lang.Math.abs(x))>= alpha) || ((java.lang.Math.abs(y))>= alpha) || ((java.lang.Math.abs(z))>= alpha)){
			mReceiver.putExtra("fall", true);			
			mReceiver.putExtra("lat", lat);
			mReceiver.putExtra("long", lon);
			calendar=new GregorianCalendar();
			
			thActivity.setAction(BROADCAST);
			//thActivity.setAction(TEXTVIEW);
			thActivity.putExtra("fall", true);
			//thActivity.putExtra("textview", DatiAccelerometro);
			String data = ""+calendar.get(GregorianCalendar.YEAR)+ "/" + (calendar.get(GregorianCalendar.MONTH)+1)+ "/" +calendar.get(GregorianCalendar.DATE);
			String ora = ""+calendar.get(GregorianCalendar.HOUR_OF_DAY)+ ":" + calendar.get(GregorianCalendar.MINUTE)+ ":" +calendar.get(GregorianCalendar.SECOND);
			if(dbF.notFallSameHour(sessione, ora)){
				Log.v("FIND FALL", "NESSUNA CADUTA CON LA STESSA DATA");
				dbF.addCaduta(data,ora,Double.toString(lat),Double.toString(lon),sessione);				
				Log.v("sessione in cui e' avvenuta la caduta",""+sessione);	
				sendBroadcast(mReceiver);
				sendBroadcast(thActivity);
			}
			else
				Log.v("FIND FALL", "GIA' PRESENTE CADUTA");
		}
	}
	
private void start(){			
		Log.v("GESTIONE FILE","METODO START");
		it = System.currentTimeMillis();		
		locMg = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		locMg.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
		mysm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		accel = mysm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		if(mysm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
			mysm.registerListener((SensorEventListener) this, accel,  SensorManager.SENSOR_DELAY_NORMAL);
			GregorianCalendar cal = new GregorianCalendar(); 
			year = cal.get(GregorianCalendar.YEAR);
			month = cal.get(GregorianCalendar.MONTH)+1;
			day = cal.get(GregorianCalendar.DAY_OF_MONTH);			
			date = "" + year + month + day ;				
			Log.v("SENSORE ACCELEROMETRO----->","ACCELEROMETRO REGISTRATO");			
		}
}

private void stop(){
	Log.v("GESTIONE FILE","METODO STOP");
	if(mysm != null){
		mysm.unregisterListener(this);
		//mysm.unregisterListener(this, accel);
		Log.v("SENSORE ACCELEROMETRO----->","ACCELEROMETRO STOPPATO");
	}
	if(locMg != null){
		locMg.removeUpdates(this);
	}
	
	if(!(lastFileName.equals(date))){
		lastFileName = date;				
		}
		
	try {
		fo = openFileOutput(lastFileName, Context.MODE_PRIVATE);
		fo.close();
	} catch (FileNotFoundException e) {
		Log.e("Impossibile trovare il file ", date, e);
		Toast.makeText(	this, "Errore openFileOutput", Toast.LENGTH_LONG).show();
	}catch (IOException e) {
		Log.e("Impossibile chiudere il file", date, e); 
		Toast.makeText(	this, "Errore chiusura", Toast.LENGTH_LONG).show();
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
	
	accel = null;	
}
	@Override
	public IBinder onBind(Intent intent){
		return mBinder;
	}
	
	 @Override
	 public void onRebind(Intent intent) {	 
	 super.onRebind(intent);
	 }
	 
	 @Override
	 public boolean onUnbind(Intent intent) {
	 return true;
	 }		
	
 public class MyBinderText extends Binder {
	 FindFall getService() {
		 return FindFall.this;
	 }
 }
	public void onDestroy(){
		super.onDestroy();	
		stop();
		dbF.close();
		cdSaveSC.cancel();
		cdViewSC.cancel();
	}
}