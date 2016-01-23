/*service per la rilevazione delle cadute, il salvataggio dei datiaccelerometro su file, 
  la lettura dei dati da accelerometroerometro e dati della posizione via GPS
 */
package it.unipd.dei.rilevatoredicadute;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;
import android.os.IBinder;
import android.os.Binder;
import android.os.Bundle;
import android.os.CountDownTimer;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;


public class FindFall extends Service implements SensorEventListener, LocationListener{
	long it = 0;
	//valori immediati
	private float xVal;
	private float yVal;
	private float zVal;
	//valori precedenti
	private float xValLast;
	private float yValLast;
	private float zValLast;
	Intent mReceiver = null;
	Intent thActivity = null;
	private float alpha = (float)10; //differenza tra valore immediato e precedente per cui si ha una caduta
	String sessione;
	public static final String BROADCAST = "it.unipd.dei.rilevatoredicadute.android.action.broadcast";
	public static final String TEXTVIEW = "Gestione Textview";
	MyDBManager dbF;
	GregorianCalendar calendario;
	IBinder mBinder = new MyBinderText();
	private SensorManager mioGestoreSensore = null;
	private LocationManager gestoreLocazione = null;
	private Sensor accelerometro = null;
	private ArrayList<AccelerometroData> acData = new ArrayList<AccelerometroData>(15000);	//lista temporanea dati accelerometroerometro
	DataOutputStream f = null;
	FileOutputStream fo = null;		
	File file; //file per il salvataggio di tutti i dati dell'accelerometroerometro
	String lastFileName = "null";
	CountDownTimer cdSaveSC = null; //conto alla rovescia lettura/salvataggio nella lista temporanea dati accelerometroerometro
	CountDownTimer cdViewSC = null; //conto alla rovescia visualizzazione dati accelerometroerometro
	boolean rec, vis = true; //variabili per la visualizzazione e salvataggio dei dati da accelerometroerometro
	private double latitudine, longitudine;
	float[] Datiaccelerometroerometro = new float[3];
	String date;
	int k, i, j;
	int permesso;
	PackageManager packMan;
	Location l;	
	final int UN_SECONDO = 1000; 
	
	public FindFall() {
		super();
	}
	
	@Override
	public void onCreate(){
		super.onCreate();
		//conto alla rovescia per salvataggio dei dati dell'accelerometroerometro nella lista
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
		//conto alla rovescia per visualizzazione dei dati dell'accelerometroerometro
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
		
		dbF = new MyDBManager(this);
		start();//metodo per quando comincia il service
	}
	
	@Override
	public int onStartCommand (Intent intent, int flags, int startId) {		
		Log.v("findFall", "inizio onStartCommand");	
		sessione = intent.getStringExtra("nome sessione");
		Log.v("nomesessione service", ""+sessione);
		mReceiver = new Intent(this, MyReceiver.class);	
		/*
		//avvio delle impostazioni per attivare il GPS
		if (!gestoreLocazione.isProviderEnabled( LocationManager.NETWORK_PROVIDER) ) {			
			Intent ISetting = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);			
			ISetting.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);			
			ISetting.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);			
			ISetting.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
			startActivity(ISetting);
			while(!gestoreLocazione.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
				settaGPS();
			}
		}*/
		
		//avvio conto alla rovescia
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
 						xVal = x;
 						yVal = y;
 						zVal = z;
 						xValLast = acData.get(k).getX();
 						yValLast = acData.get(k).getY();
 						zValLast = acData.get(k).getZ();
 						caduta();//metodo per la verifica dell'avvenuta di una caduta	
 						k++;
					} 
 					
 					acData.add(new AccelerometroData(time, x, y, z));
					cdSaveSC.start();
				}
				//invio dati per la visualizzazione dei valori dell'accelerometroerometro
				if(vis && !acData.isEmpty() && j < acData.size()){
					Datiaccelerometroerometro[0] = acData.get(j).getX();
					Datiaccelerometroerometro[1] = acData.get(j).getY();
					Datiaccelerometroerometro[2] = acData.get(j).getZ();
					thActivity = new Intent();
					thActivity.setAction(TEXTVIEW);
					thActivity.putExtra("textview", Datiaccelerometroerometro);
					sendBroadcast(thActivity);
					j += 4;
					vis = false;
				    cdViewSC.start();
				}
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
	}	
	
	@Override
	public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
		latitudine = location.getLatitude();
		longitudine = location.getLongitude();
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub			
	}
	
	@Override
	public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub	
		Log.v("FindFall->onProviderEnabled","NETWORK_PROVIDER ABILITATO");
	}
	
	@Override
	public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub	
		Log.v("FindFall->onProviderDisabled","NETWORK_PROVIDER NON ABILITATO");
	}
	//metodo per rilevare una caduta
	private void caduta(){
		float x = java.lang.Math.abs(xVal)- java.lang.Math.abs(xValLast);
		float y = java.lang.Math.abs(yVal)- java.lang.Math.abs(yValLast);
		float z = java.lang.Math.abs(zVal)- java.lang.Math.abs(zValLast);
				
		if(((java.lang.Math.abs(x)) >= alpha) || ((java.lang.Math.abs(y)) >= alpha) || ((java.lang.Math.abs(z)) >= alpha)){		
			mReceiver.putExtra("fall", true);			
			mReceiver.putExtra("lat", latitudine);
			mReceiver.putExtra("long", longitudine);		
			calendario=new GregorianCalendar();
			
			thActivity.setAction(BROADCAST);
			thActivity.putExtra("fall", true);
			String data = ""+calendario.get(GregorianCalendar.YEAR)+ "/" + (calendario.get(GregorianCalendar.MONTH)+1)+ "/" +calendario.get(GregorianCalendar.DATE);
			String ora = ""+calendario.get(GregorianCalendar.HOUR_OF_DAY)+ ":" + calendario.get(GregorianCalendar.MINUTE)+ ":" +calendario.get(GregorianCalendar.SECOND);
			if(dbF.noCaduteStessaOra(sessione, ora)){
				Log.v("FIND FALL", "NESSUNA CADUTA CON LA STESSA DATA");
				dbF.aggCaduta(data,ora,Double.toString(latitudine),Double.toString(longitudine),sessione);				
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
			gestoreLocazione = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
			Log.i("valore locationManager", ""+gestoreLocazione);			
			//gestoreLocazione.requestLocationUpdatas(LocationManager.NETWORK_PROVIDER, 5000, 5, this);			
			//l = gestoreLocazione.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			
			//avvio delle impostazioni per attivare il GPS
			if ( !gestoreLocazione.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ){			
				Intent ISetting = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);			
				ISetting.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);			
				ISetting.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);			
				ISetting.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
				startActivity(ISetting);		
				while(!gestoreLocazione.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
					settaGPS();
				}
			}
			else
				settaGPS();
			mioGestoreSensore = (SensorManager) getSystemService(Context.SENSOR_SERVICE);					
			accelerometro = mioGestoreSensore.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			if(mioGestoreSensore.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
				mioGestoreSensore.registerListener((SensorEventListener) this, accelerometro,  SensorManager.SENSOR_DELAY_NORMAL);			
				Log.v("SENSORE accelerometroEROMETRO----->","accelerometroEROMETRO REGISTRATO");			
			}
	}
	
	private void settaGPS(){
		gestoreLocazione.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 5, this);		
		l = gestoreLocazione.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);		
	}
	
	private void stop(){
		Log.v("GESTIONE FILE","METODO STOP");
		if(mioGestoreSensore != null){
			mioGestoreSensore.unregisterListener(this);
			Log.v("SENSORE accelerometroEROMETRO----->","ACCELEROMETRO STOPPATO");
		}
		if(gestoreLocazione != null){
			gestoreLocazione.removeUpdates(this);
		}
		//scrittura nel file dei dati dell'accelerometroerometro salvati nella lista 
		if(!(lastFileName.equals(sessione))){
			lastFileName = sessione;				
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
				fo = openFileOutput(sessione, Context.MODE_APPEND);
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
		
		accelerometro = null;	
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