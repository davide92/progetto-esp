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
import android.app.Activity;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Chronometer;
import it.unipd.dei.rilevatoredicadute.ServiceCronometro;
import android.graphics.Color;
import java.util.Random;
import android.widget.ListView;
import java.util.LinkedList;
import java.util.List;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.IBinder;
import android.content.ComponentName;

import it.unipd.dei.rilevatoredicadute.ServiceCronometro.MyBinder;


public class NewThird extends ActionBarActivity implements SensorEventListener,LocationListener{
	
	public static String PACKAGE_NAME;
	//private Chronometer chronometer;
	TextView timestampText;
	GregorianCalendar cal;
	MyDBManager db;
	private long stopTime=0;	
	private SensorManager mysm = null;
	private LocationManager locMg = null;
	private Sensor accel = null;
	private ArrayList<AccelData> acData = new ArrayList<AccelData>(15000);
	private int i, j, k = 0;
	private double latitude, longitude;
	private String nS;
	private  ListView listView;
	private List<DatiCadute> fallList;
    Intent T;	
	int s;	
	long it = 0;
	long pauseTime=0;		
	private boolean rec, vis = true;	
	TextView xAccViewS = null;
	TextView yAccViewS = null;
	TextView zAccViewS = null;
	DataOutputStream f = null;
	FileOutputStream fo = null;	
	File file;
	String lastFileName = "null";
	String date,data;
	Intent mService = null;
	CountDownTimer cdSave = null;
	CountDownTimer cdView = null;
	CountDownTimer cdCrono = null;
	int year;
	int month;
	int day;	
	ImageButton playBtn;
	ImageButton pauseBtn;
	ImageButton stopBtn;
	long remainingtimeS;
	long remainingtimeW;
	String NS;//nomeSessione
	TextView tx;
	ServiceCronometro sc = new ServiceCronometro();
	boolean inPausa;
	//long tempoCronometro;
	//long pausa=0;
	boolean mServiceBound = false;
	Intent intent;
	
	
	@Override
	protected void onCreate(Bundle savedInstance) {		
		super.onCreate(savedInstance);		
		setContentView(R.layout.activity_sess_curr);
		timestampText = (TextView) findViewById(R.id.timestamp_text);
		listView = (ListView) findViewById(R.id.listViewCadute);
		fallList = new LinkedList<DatiCadute>();
		cal= new GregorianCalendar();
		//chronometer = (Chronometer) findViewById(R.id.chronometer);
		playBtn = (ImageButton)findViewById(R.id.start);
		pauseBtn = (ImageButton)findViewById(R.id.pause);
		stopBtn = (ImageButton)findViewById(R.id.stop);		
		if((savedInstance !=null)){			
			//chronometer.setBase(savedInstance.getLong("time_seconds"));
			timestampText.setText(sc.StampaDurata());
			s=savedInstance.getInt("statesession");			
			data=savedInstance.getString("data");
			
			if(s==1){
				playBtn.setVisibility(View.INVISIBLE);
				pauseBtn.setVisibility(View.VISIBLE);
				//sc.Start(chronometer);
				//chronometer.start();
				start();
				cdSave.start();
				cdView.start();
			}else{
				playBtn.setVisibility(View.VISIBLE);
				pauseBtn.setVisibility(View.INVISIBLE);
			}
		}
		//if(!locMg.isProviderEnabled(LocationManager.GPS_PROVIDER)){	
			//Toast.makeText(getApplicationContext(), "Attivi il gps", Toast.LENGTH_LONG).show();
		//}
		//Log.v("CREATHIRD",""+sc.GetBase(chronometer)+"");	
	}
	
	@Override
	protected void onSaveInstanceState(Bundle savedInstance) {
	    //savedInstance.putLong("time_seconds", sc.GetBase(chronometer));
	    savedInstance.putInt("statesession", s);	    
	    savedInstance.putString("data", date);
	    super.onSaveInstanceState(savedInstance);
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		Log.v("TAG", "----INIZIO-THIRD----");
		//Intent intent = new Intent(this, ServiceCronometro.class);
		db = new MyDBManager(this);	
		PACKAGE_NAME = getApplicationContext().getPackageName();
		T=getIntent(); 		        
        NS=T.getStringExtra(MainActivity.PACKAGE_NAME+".nomeSessione");
        tx=(TextView)findViewById(R.id.TestoSessCurr);
        //if(NS==null)
        	//NS="Sessione "+(db.MaxIDSessione()+1)+"";
        tx.setText(NS);
        //timestampText.setText(sc.StampaDurata());
        //Log.v("tst",""+timestampText.getText()+"");
		xAccViewS= (TextView) findViewById(R.id.xDataS);
		yAccViewS= (TextView) findViewById(R.id.yDataS);
		zAccViewS= (TextView) findViewById(R.id.zDataS);						
		if(s==1){
			playBtn.setVisibility(View.INVISIBLE);
			pauseBtn.setVisibility(View.VISIBLE);
		}
		else{
			playBtn.setVisibility(View.VISIBLE);
			pauseBtn.setVisibility(View.INVISIBLE);
		}
		intent = new Intent(getApplicationContext(), ServiceCronometro.class);
		
		playBtn.setOnClickListener(new View.OnClickListener() {					
			@Override
			public void onClick(View v) {
				
				//inPausa=false;
				s=1;
				playBtn.setVisibility(View.INVISIBLE);
				pauseBtn.setVisibility(View.VISIBLE);
				
				//if(T.getStringExtra(MainActivity.PACKAGE_NAME+".nomeSessione")==null){			
					String data = ""+cal.get(GregorianCalendar.YEAR)+ "/" + (cal.get(GregorianCalendar.MONTH)+1)+ "/" +cal.get(GregorianCalendar.DATE);
																
					long milliseconds=System.currentTimeMillis();
					int seconds = (int) (milliseconds / 1000) % 60 ;
					int minutes = (int) ((milliseconds / (1000*60)) % 60);
					int hours   = (int) ((milliseconds / (1000*60*60)) % 24);
					String ora = ""+hours+ ":" + minutes+ ":" +seconds+"";	
					Random rm = new Random();
					int cl = Color.argb(255, rm.nextInt(254), rm.nextInt(254), rm.nextInt(254));
					db.addSessione(/*et.getText().toString()*/NS, data, ora, "XX:XX:XX", 0, cl, 1);
					db.close(); 
					//Intent intent = new Intent(getApplicationContext(), ServiceCronometro.class);
					//startService(intent);
					//bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
				//}	
					cdCrono = new CountDownTimer(1000L, 200L) {					
						@Override
						public void onTick(long millisUntilFinished) {
							// TODO Auto-generated method stub
							//remainingtimeS= millisUntilFinished /1000;
							}					
						@Override
						public void onFinish() {							
							timestampText.setText(sc.StampaDurata());
							Log.v("tst",""+timestampText.getText()+"");
						}
					}.start();
				
					//tempoCronometro=SystemClock.elapsedRealtime()-pausa;
				//GESTIONE PLAY/RESUME CRONOMETRO
				if ( stopTime != 0 ){
					long intervalloPausa = (SystemClock.elapsedRealtime() - stopTime);
					intent.putExtra("pausa",intervalloPausa);
				}
				//else
					//intent.putExtra("pausa", 0L);
			        //sc.SetBase(chronometer, SystemClock.elapsedRealtime());
					//sc.SetBase(chronometer, tempoCronometro);
			    // on resume after pause
			   // else
			    //{
			        //long intervalloPausa = (SystemClock.elapsedRealtime() - stopTime);
			    	//long intervalloPausa = (tempoCronometro - stopTime);
			    	//sc.SetBase(chronometer, sc.GetBase(chronometer)/*chronometer.getBase()*/ + intervalloPausa);			       
			    //}
			    //chronometer.start();
				//sc.Start(chronometer);
			    //FINE GESTIONE					
				
				startService(intent);
				bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
				
				Log.v("List-1","--HO PREMUTO IL TASTO PLAY---");				
				Log.v("nomeSessioneThird--->",""+NS+"");
				Intent UIMA;    		
				UIMA = new Intent(getApplicationContext(), MainActivity.class);				
				UIMA.putExtra(MainActivity.PACKAGE_NAME+".state", s);
				Log.v("stato sessione third", ""+s+"");			
				
				cdSave = new CountDownTimer(5000L, 1000L) {					
					@Override
					public void onTick(long millisUntilFinished) {
						// TODO Auto-generated method stub
						//remainingtimeS= millisUntilFinished /1000;
						}					
					@Override
					public void onFinish() {
						rec = true;	
						//Log.v("countdown","finito");
						
					}
				}.start();	
				
				cdView = new CountDownTimer(21000L, 1000L) {					
					@Override
					public void onTick(long millisUntilFinished) {
						// TODO Auto-generated method stub
						//remainingtimeW= millisUntilFinished /1000;
					}					
					@Override
					public void onFinish() {
						vis = true;	
						//Log.v("countdownView","finito");
					}
				}.start();
				start();				
			}						
		});
		
		pauseBtn.setOnClickListener(new View.OnClickListener(){			
			@Override
			public void onClick(View v){
				//inPausa=true;
				s=2;
				timestampText.setText(sc.StampaDurata());
				playBtn.setVisibility(View.VISIBLE);
				pauseBtn.setVisibility(View.INVISIBLE);
				Log.v("List-2","--HO PREMUTO IL TASTO PAUSE--");				
			    stopTime = SystemClock.elapsedRealtime();			    
			    //Intent UIMA;
			    //UIMA = new Intent(getApplicationContext(), MainActivity.class);
			    //UIMA.putExtra(MainActivity.PACKAGE_NAME+".state", s);	
			   // pauseTime=SystemClock.elapsedRealtime()-sc.GetBase(chronometer);//chronometer.getBase();
			   // Log.v("valore pauseTime","sec>"+(pauseTime/1000 % 60)+"<minuti>"+((pauseTime / (1000*60)) % 60)+"<ore"+((pauseTime / (1000*60*60)) % 24)+"");
				//UIMA.putExtra(MainActivity.PACKAGE_NAME+".StopTime", (chronometer.getBase()-(chronometer.getBase()-pauseTime)));
				//UIMA.putExtra(MainActivity.PACKAGE_NAME+".StopTime",(sc.GetBase(chronometer)-(sc.GetBase(chronometer)-pauseTime)));
			    //chronometer.stop();				
				//sc.Stop(chronometer);
				Log.v("stato sessione third", ""+s+"");	
				 //stopService(intent);
				 Log.v("NEWtHIRD","SERVICE CRONOMETRO PAUSA");
			    cdView.cancel();
			    pause();
			    
			}				
		});
		
		stopBtn.setOnClickListener(new View.OnClickListener(){			
			@Override
			public void onClick(View v){
				//update della durata della sessione
				//inPausa=false;
				//if(s==1)
				//	pauseTime=SystemClock.elapsedRealtime()-sc.GetBase(chronometer);//chronometer.getBase();
				s=0;
				Log.v("List-3","--HO PREMUTO IL TASTO STOP--");
				//Long saveTime = pauseTime;//SystemClock.elapsedRealtime() - (chronometer.getBase());//-(chronometer.getBase()-pauseTime));
		        //int seconds = (int)(saveTime/1000 % 60);
		        //int minutes = (int) ((saveTime / (1000*60)) % 60);
				//int hours   = (int) ((saveTime / (1000*60*60)) % 24);		        
				//String ora=""+hours+":"+minutes+":"+seconds+"";				
				String ora=sc.StampaDurata();
				Log.v("durataSessione",ora);
				Log.v("valore pauseTime","sec>"+(pauseTime/1000 % 60)+"<minuti>"+((pauseTime / (1000*60)) % 60)+"<ore"+((pauseTime / (1000*60*60)) % 24)+"");
				db.updateDurataSessione(ora,NS);//et.getText().toString());
				//Log.v("stopSessione------->",et.getText().toString());				
				Log.v("stopSessione------->",""+NS+"");
				//chronometer.stop();
				if (mServiceBound) {
					 unbindService(mServiceConnection);
					 mServiceBound = false;
					 }
				 Intent intent = new Intent(getApplicationContext(), ServiceCronometro.class);
				 stopService(intent);
				 Log.v("NEWtHIRD","SERVICE CRONOMETRO FINITO");
					 
				//sc.Stop(chronometer);
				Intent UIMA;
			    UIMA = new Intent(getApplicationContext(), MainActivity.class);					
			   // UIMA.putExtra(MainActivity.PACKAGE_NAME+".state", s);
			   // UIMA.putExtra(MainActivity.PACKAGE_NAME+".StopTime", pauseTime);
			    Log.v("stato sessione third", ""+s+"");									
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
		Log.v("sessione stop",""+s+"");
		//if(inPausa==false){
			//pausa=SystemClock.elapsedRealtime()-tempoCronometro;
		//}
		 
		if (mServiceBound) {
	    unbindService(mServiceConnection);
	    mServiceBound = false;
	    }
	}
	
	 private ServiceConnection mServiceConnection = new ServiceConnection() {
		 
		 @Override
		 public void onServiceDisconnected(ComponentName name) {
		 mServiceBound = false;
		 }
		 
		 @Override
		 public void onServiceConnected(ComponentName name, IBinder service) {
		 MyBinder myBinder = (MyBinder) service;
		 sc = myBinder.getService();
		 mServiceBound = true;
		 }
		 };
	
	//DA BACKGROUND A FOREGROUND
	/*@Override
	protected void onRestart(){
		super.onRestart();				
		Log.v("ACT-THIRD","RESTART");
		if(inPausa==true){
			playBtn.setVisibility(View.INVISIBLE);
			pauseBtn.setVisibility(View.VISIBLE);			
		}
		else{
			playBtn.setVisibility(View.VISIBLE);
			pauseBtn.setVisibility(View.INVISIBLE);
		}			
	}*/
	
	@Override
	protected void onResume(){
		super.onResume();				
		Log.v("ACT-THIRD","RESUME");
		Log.v("STATO SESSIONE RESUME",""+s+"");
		if(s==1){
			playBtn.setVisibility(View.INVISIBLE);
			pauseBtn.setVisibility(View.VISIBLE);
			//sc.SetBase(chronometer, pausa);
		}
		else{
			playBtn.setVisibility(View.VISIBLE);
			pauseBtn.setVisibility(View.INVISIBLE);
		}			
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
				//if(s==1)
					//pauseTime=SystemClock.elapsedRealtime()-sc.GetBase(chronometer);//chronometer.getBase();
				Intent UIMA;
				UIMA = new Intent(getApplicationContext(), MainActivity.class);
				UIMA.putExtra(MainActivity.PACKAGE_NAME+".StopTimeT", pauseTime);//(chronometer.getBase()-(chronometer.getBase()-pauseTime)));
				Log.v("---","---");
				Log.v("valore pauseTime","sec>"+(pauseTime/1000 % 60)+"<minuti>"+((pauseTime / (1000*60)) % 60)+"<ore"+((pauseTime / (1000*60*60)) % 24)+"");
				//UIMA.putExtra(MainActivity.PACKAGE_NAME+".nameSession", et.getText().toString());
				UIMA.putExtra(MainActivity.PACKAGE_NAME+".nameSessionT", NS);
				UIMA.putExtra(MainActivity.PACKAGE_NAME+".stateT", s);					
				//chronometer.stop();
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
		
		it = System.currentTimeMillis();
		locMg = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		locMg.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
		mysm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		if(mysm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
			accel = mysm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			mysm.registerListener((SensorEventListener) this, accel, SensorManager.SENSOR_DELAY_NORMAL);			
			year = cal.get(GregorianCalendar.YEAR);
			month = cal.get(GregorianCalendar.MONTH)+1;
			day = cal.get(GregorianCalendar.DAY_OF_MONTH);
			//int hour = c.get(GregorianCalendar.HOUR_OF_DAY);
			//int min = c.get(GregorianCalendar.MINUTE);
			//int sec= c.get(GregorianCalendar.SECOND);
			date = "" + year + month + day /*+ hour + min + sec */;			
		}
		nS = tx.getText().toString();
		int fallCount = db.CountCaduta(nS);
		db.close();
		if(fallCount > 0){
		Cursor crs = db.selectCaduta(nS);
		if(crs.moveToFirst()){
			do{
				String strData = crs.getString(crs.getColumnIndex("DataCaduta"));
		        String[] dataf=strData.split("/");
		        int day=Integer.parseInt(dataf[0]);  
		        int month=Integer.parseInt(dataf[1]);  
		        int year=Integer.parseInt(dataf[2]);
		        String strTime = crs.getString(crs.getColumnIndex("OraCaduta"));
		        String[] oraf=strTime.split(":");
		        int hour=Integer.parseInt(oraf[0]);  
		        int minutes=Integer.parseInt(oraf[1]);  
		        int seconds=Integer.parseInt(oraf[2]);
		        double lat = crs.getDouble(crs.getColumnIndex("Latitudine"));
		        double longi = crs.getDouble(crs.getColumnIndex("Longitudine"));
		        fallList.add(new DatiCadute(day, month, year, hour, minutes, seconds, lat, longi, nS));
				}while(crs.moveToNext());	
			}
		}
			
				CustomAdapterFalls adapter = new CustomAdapterFalls(this, R.id.listViewCadute, fallList);       
			    listView.setAdapter(adapter);
	}
	
	private void pause(){
		if(mysm != null){
			mysm.unregisterListener(this);			
		}
		if(locMg != null){
			locMg.removeUpdates(this);
		}
		xAccViewS.setText("0");
		yAccViewS.setText("0");
		zAccViewS.setText("0");		
		
		//if(data==null){
		if(!(lastFileName.equals(NS))){//date al posto di NS
			lastFileName = NS;
		//}else{
			//if(!(lastFileName.equals(data))){
				//lastFileName = data;
		//}	
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
			
		}
		//}
		
		if(i < acData.size()){
			try {
				fo = openFileOutput(NS, Context.MODE_APPEND);//date al posto di NS
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
		if(locMg != null){
			locMg.removeUpdates(this);
		}
		
		//if(!(lastFileName.equals(date))){
			//lastFileName = date;
		//if(data==null){
			if(!(lastFileName.equals(date))){
				lastFileName = date;
			//}else{
				//if(!(lastFileName.equals(data))){
					//lastFileName = data;
				}
			 //}
		//}	
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
				fo = openFileOutput(NS, Context.MODE_APPEND);//date al posto di NS
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
			mService = new Intent(getApplicationContext(), FindFall.class);
			if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
				
				if(rec){
					float x = event.values[0];
 					float y = event.values[1];
 					float z = event.values[2]; 					
 					//rec = false;
 					long time = System.currentTimeMillis()-it;					
 					if(acData.size() >= 1){
						//mService = new Intent(getApplicationContext(), FindFall.class);
						if(locMg.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null){
							mService.putExtra("long", longitude);
							mService.putExtra("lat", latitude);
						}
						mService.putExtra("xVal", x);
						mService.putExtra("yVal", y);
						mService.putExtra("zVal", z);
						mService.putExtra("xValLast", acData.get(k).getX());
						mService.putExtra("yValLast", acData.get(k).getY());
						mService.putExtra("zValLast", acData.get(k).getZ());
						//mService.putExtra("nomSess", et.getText().toString());
						mService.putExtra("nomSess", NS);
						//Log.v("nomeSessPassataFF",""+et.getText().toString()+"");
						startService(mService);
						//stopService(mService);
						k++;								
					}
 					acData.add(new AccelData(time, x, y, z));
					cdSave.start();
				}
				
				if(vis && !acData.isEmpty() && j<acData.size()){
					xAccViewS.setText("" + acData.get(j).getX());
					yAccViewS.setText("" + acData.get(j).getY());
					zAccViewS.setText("" + acData.get(j).getZ());
					j+=4;
					vis = false;
				    cdView.start();
				}
			}
			else
				stopService(mService);
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
}