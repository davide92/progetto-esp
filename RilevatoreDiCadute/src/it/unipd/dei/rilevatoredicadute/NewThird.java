package it.unipd.dei.rilevatoredicadute;

import android.support.v7.app.ActionBarActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.os.Vibrator;
import android.os.IBinder;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ListView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.ComponentName;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.util.Log;

import android.graphics.Color;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.database.Cursor;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.LinkedList;
import java.util.List;

//import it.unipd.dei.rilevatoredicadute.ServiceCronometro;

import it.unipd.dei.rilevatoredicadute.ServiceCronometro.MyBinder;


public class NewThird extends ActionBarActivity implements SensorEventListener,LocationListener{	
		
	GregorianCalendar cal;
	MyDBManager db;
	public long stopTime=0;	
	private SensorManager mysm = null;
	private LocationManager locMg = null;
	private Sensor accel = null;
	private ArrayList<AccelData> acData = new ArrayList<AccelData>(15000);	
	private double latitude, longitude;	
	private  ListView listView;
	private List<DatiCadute> fallList;	
	long it = 0;
	//long pauseTime=0;	
	TextView timestampText;
	TextView xAccViewS = null;
	TextView yAccViewS = null;
	TextView zAccViewS = null;
	TextView tx;
	DataOutputStream f = null;
	FileOutputStream fo = null;		
	File file;
	public static String PACKAGE_NAME;
	String lastFileName = "null";
	String date,data;
	String NS;//nomeSessione
	String StampaDurataService;
	private String nS;
	Intent mService = null;
	CountDownTimer cdSaveSC = null;
	CountDownTimer cdViewSC = null;
	CountDownTimer cdCrono = null;
	int s;
	int SNT; //stato sessione activity newthird per gestione parametri
	int year;
	int month;
	int day;
	private int i, j, k = 0;
	ImageButton playBtn;
	ImageButton pauseBtn;
	ImageButton stopBtn;		
	boolean mServiceBound = false;
	private boolean rec, vis = true;
	ServiceCronometro sc;// = new ServiceCronometro();
	Intent T;	
	Intent intent;	
	CustomAdapterFalls adapter;
	MyReceiver myReceiver;
	BroadcastReceiver receiver;
	IntentFilter filter;
	
	@Override
	protected void onCreate(Bundle savedInstance) {		
		super.onCreate(savedInstance);		
		setContentView(R.layout.activity_sess_curr);
		
		listView = (ListView) findViewById(R.id.listViewCadute);
		fallList = new LinkedList<DatiCadute>();
		adapter = new CustomAdapterFalls(this, R.id.listViewCadute, fallList);       
	    listView.setAdapter(adapter);
		cal= new GregorianCalendar();
		receiver = new BroadcastReceiver(){

			@Override
			public void onReceive(Context context, Intent intent) {
				if(intent.getBooleanExtra("fall", false)){
					Log.v("Receiver newThird", "Entrato");
					Vibrator vibr = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
					vibr.vibrate(500L);
					//updateListView();
					updateUI();
				}
				
			}
			
		};
	    filter = new IntentFilter(FindFall.BROADCAST);
		//COUNT DOWN TIMER PER LA GESTIONE DELLA TEXTVIEW CHE VISUALIZZA LA DURATA DELLA SESSIONE
		cdCrono = new CountDownTimer(1000L, 100L) {					
			@Override
			public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub
			//remainingtimeS= millisUntilFinished /1000;							
			}					
			@Override
			public void onFinish() {
			//if(mServiceBound){
			//StampaDurataService=sc.StampaDurata();
			if(StampaDurataService != null)	
				timestampText.setText(""+StampaDurataService+"");
			else
				timestampText.setText("0:0:0");
			//Log.v("STAMPAdurataNEWTHIRD",""+timestampText.getText()+"");
			//Log.v("countdownCrono","finito");
			cdCrono.start();
								//}
			}
		};
				
		//COUNT DOWN TIMER PER LA GESTIONE DEL FILE CHE SALVA SU ESSO I DATI DEI TRE ASSI RIGUARDANTI L'ACCELEROMETRO		
		cdSaveSC = new CountDownTimer(5000L, 1000L) {					
			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub				
			}					
			@Override
			public void onFinish() {
				rec = true;	
				//Log.v("countdownSave","finito");				
			}
		};
				
		//COUNT DOWN TIMER PER LA GESTIONE DELLE TEXTVIEW CHE VISUALIZZANO I DATI DEI TRE ASSI RIGUARDANTI L'ACCELEROMETRO
		cdViewSC = new CountDownTimer(21000L, 1000L) {					
			@Override
			public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub			
			}					
			@Override
			public void onFinish() {
				vis = true;	
				//Log.v("countdownView-->","finito");
			}
		};
		
		timestampText = (TextView) findViewById(R.id.timestamp_text);
		playBtn = (ImageButton)findViewById(R.id.start);
		pauseBtn = (ImageButton)findViewById(R.id.pause);
		stopBtn = (ImageButton)findViewById(R.id.stop);		
		
		if((savedInstance !=null)){				
			StampaDurataService = savedInstance.getString("durataCrono");			
			SNT = savedInstance.getInt("statesession");
			Log.v("NEWTHIRDvaloreSTATO", "-----VALORE VARIABILE SNT  "+SNT+"");
			data = savedInstance.getString("data");//VERIFICARE SE DA TOGLIERE
			stopTime = savedInstance.getLong("TempoPausa");
			
			if(SNT==1){
				playBtn.setVisibility(View.INVISIBLE);
				pauseBtn.setVisibility(View.VISIBLE);				
				start();
				cdSaveSC.start();
				cdViewSC.start();
			}else{
				playBtn.setVisibility(View.VISIBLE);
				pauseBtn.setVisibility(View.INVISIBLE);
			}
		}		
		//if(!locMg.isProviderEnabled(LocationManager.GPS_PROVIDER)){	
			//Toast.makeText(getApplicationContext(), "Attivi il gps", Toast.LENGTH_LONG).show();
		//}		
	}
	
	@Override
	protected void onSaveInstanceState(Bundle savedInstance) {	    
	    savedInstance.putInt("statesession", SNT);	    
	    savedInstance.putString("data", date);
	    savedInstance.putString("durataCrono", StampaDurataService);	    
	    savedInstance.putLong("TempoPausa", stopTime);
	    Log.v("<<ATTENZIONE>>","CAMBIO ORIENTAZIONE TELEFONO");
	    super.onSaveInstanceState(savedInstance);
	    //BISOGNA SALVARE ANCHE IL LONG CHE MEMORIZZA IL TEMPO IN PAUSA, GUARDARE PLYBTN E PAUSEBTN
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		this.registerReceiver(receiver, filter);
		Log.v("TAG", "----INIZIO-THIRD----");		
		
		myReceiver = new MyReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ServiceCronometro.MY_ACTION);
		registerReceiver(myReceiver,intentFilter);
		
		db = new MyDBManager(this);	
		PACKAGE_NAME = getApplicationContext().getPackageName();
		T = getIntent(); 		        
        NS = T.getStringExtra(MainActivity.PACKAGE_NAME+".nomeSessione");
        s = T.getIntExtra(MainActivity.PACKAGE_NAME+".statoSessione", 3);
        Log.v("NEWTHIRD", "VALORE VARIABILE S --> " +s);
                
        tx=(TextView)findViewById(R.id.TestoSessCurr);        
        tx.setText(NS);        
        
		xAccViewS= (TextView) findViewById(R.id.xDataS);
		yAccViewS= (TextView) findViewById(R.id.yDataS);
		zAccViewS= (TextView) findViewById(R.id.zDataS);						
		if(s==3){//GESTIONE ORIENTAMENTO TELEFONO: s==3 SIGNIFICA CHE L'ORIENTAMENTO DELLO SCHERMO E' CAMBIATA
			if(SNT==1){
				playBtn.setVisibility(View.INVISIBLE);
				pauseBtn.setVisibility(View.VISIBLE);
				start();
				//cdCrono.start();
			}
			else{
				playBtn.setVisibility(View.VISIBLE);
				pauseBtn.setVisibility(View.INVISIBLE);
			}
		}else{
			SNT = s;
			if(s==1){
				playBtn.setVisibility(View.INVISIBLE);
				pauseBtn.setVisibility(View.VISIBLE);
				start();
				//cdCrono.start();
				cdSaveSC.start();
				cdViewSC.start();
			}
			else{
				playBtn.setVisibility(View.VISIBLE);
				pauseBtn.setVisibility(View.INVISIBLE);
			}
		}		
		intent = new Intent(getApplicationContext(), ServiceCronometro.class);	
				
		cdCrono.start();
		
		playBtn.setOnClickListener(new View.OnClickListener() {					
			@Override
			public void onClick(View v) {				
				
				//s=1;
				
				SNT = 1;
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
				Intent intent = new Intent(getApplicationContext(), ServiceCronometro.class);					
				//}	
					/*cdCrono = new CountDownTimer(2000L, 100L) {					
						@Override
						public void onTick(long millisUntilFinished) {
							// TODO Auto-generated method stub
							//remainingtimeS= millisUntilFinished /1000;							
							}					
						@Override
						public void onFinish() {
							if(mServiceBound){
								StampaDurataService=sc.StampaDurata();
								timestampText.setText(""+StampaDurataService+"");
								//Log.v("STAMPAdurataNEWTHIRD",""+timestampText.getText()+"");
								cdCrono.start();
							}
						}
					}.start();*/					
				
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
				Log.v("stato sessione third", ""+SNT+"");			
				
				/*cdSave = new CountDownTimer(5000L, 1000L) {					
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
				}.start();*/
				cdSaveSC.start();				
				/*cdView = new CountDownTimer(21000L, 1000L) {					
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
				}.start();*/
				cdViewSC.start();
				start();				
			}						
		});
		
		pauseBtn.setOnClickListener(new View.OnClickListener(){			
			@Override
			public void onClick(View v){
				
				//s=2;
				SNT = 2;
				cdCrono.cancel();				
				timestampText.setText(""+StampaDurataService+"");
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
			   	Log.v("stato sessione third", ""+SNT+"");					
				Log.v("NEWtHIRD","SERVICE CRONOMETRO PAUSA");
			    cdViewSC.cancel();
			    pause();
			    
			}				
		});
		
		stopBtn.setOnClickListener(new View.OnClickListener(){			
			@Override
			public void onClick(View v){
				//update della durata della sessione
				
				//if(s==1)
				//	pauseTime=SystemClock.elapsedRealtime()-sc.GetBase(chronometer);//chronometer.getBase();
				//s=0;
				SNT = 0;
				Log.v("List-3","--HO PREMUTO IL TASTO STOP--");
				//Long saveTime = pauseTime;//SystemClock.elapsedRealtime() - (chronometer.getBase());//-(chronometer.getBase()-pauseTime));
		        //int seconds = (int)(saveTime/1000 % 60);
		        //int minutes = (int) ((saveTime / (1000*60)) % 60);
				//int hours   = (int) ((saveTime / (1000*60*60)) % 24);		        
				//String ora=""+hours+":"+minutes+":"+seconds+"";					
				String ora=StampaDurataService;
				Log.v("durataSessione",""+ora+"");
				//Log.v("valore pauseTime","sec>"+(pauseTime/1000 % 60)+"<minuti>"+((pauseTime / (1000*60)) % 60)+"<ore"+((pauseTime / (1000*60*60)) % 24)+"");
				db.updateDurataSessione(ora,NS);//et.getText().toString());
								
				Log.v("stopSessione------->",""+NS+"");
				//chronometer.stop();
				if (mServiceBound) {
					 unbindService(mServiceConnection);
					 mServiceBound = false;
					 }
				Intent intent = new Intent(getApplicationContext(), ServiceCronometro.class);
				 //intent.putExtra("stato",SNT);
				 stopService(intent);
				 Log.v("NEWtHIRD","SERVICE CRONOMETRO FINITO");					 
				
				Intent UIMA;
			    UIMA = new Intent(getApplicationContext(), MainActivity.class);					
			   // UIMA.putExtra(MainActivity.PACKAGE_NAME+".state", s);
			   // UIMA.putExtra(MainActivity.PACKAGE_NAME+".StopTime", pauseTime);
			    Log.v("stato sessione third", ""+SNT+"");									
				cdSaveSC.cancel();
				cdViewSC.cancel();
				cdCrono.cancel();
				stop();				
                startActivity(UIMA);
			}			
			
		});		
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		try{
			this.unregisterReceiver(receiver);
		}catch(IllegalArgumentException e){
			
		}
		Log.v("ACT-THIRD","PAUSED");
	}	

	@Override
	protected void onStop(){
		super.onStop();
		db.close();		
		Log.v("ACT-THIRD","STOPPED");
		//Log.v("sessione stop",""+SNT+"");
		try{
			unregisterReceiver(myReceiver);
		}catch(Exception exc){
			Log.v("ERRORE-RECEIVER","NON DISREGISTRATO");
		}		 
		if (mServiceBound) {
			unbindService(mServiceConnection);
			mServiceBound = false;
	    }
		cdCrono.cancel();
		//cdSaveSC.cancel();
		//cdViewSC.cancel();
		if(s==3){			
			cdSaveSC.cancel();//DA RIVEDERE
			cdViewSC.cancel();//DA RIVEDERE
			stop();//DA RIVEDERE
		}
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		Log.v("ACT-THIRD","DESTROYED");
		//if (mServiceBound) {
			//unbindService(mServiceConnection);
			//mServiceBound = false;
	    //}
	}
	
	private class MyReceiver extends BroadcastReceiver{		 
		 @Override
		 public void onReceive(Context arg0, Intent arg1) {
		  // TODO Auto-generated method stub		  	  
		  if(SNT !=2)
			 StampaDurataService = arg1.getStringExtra("TimeStamp");
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
	@Override
	protected void onRestart(){
		super.onRestart();				
		Log.v("ACT-THIRD","RESTART");
		/*if(inPausa==true){
			playBtn.setVisibility(View.INVISIBLE);
			pauseBtn.setVisibility(View.VISIBLE);			
		}
		else{
			playBtn.setVisibility(View.VISIBLE);
			pauseBtn.setVisibility(View.INVISIBLE);
		}*/		
	}
	
	/*@Override
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
		//bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
	}*/
	
	
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
				
				Intent UIMA;
				UIMA = new Intent(getApplicationContext(), MainActivity.class);
				//UIMA.putExtra(MainActivity.PACKAGE_NAME+".StopTimeT", pauseTime);//(chronometer.getBase()-(chronometer.getBase()-pauseTime)));
				Log.v("---","---");
				//Log.v("valore pauseTime","sec>"+(pauseTime/1000 % 60)+"<minuti>"+((pauseTime / (1000*60)) % 60)+"<ore"+((pauseTime / (1000*60*60)) % 24)+"");
				//UIMA.putExtra(MainActivity.PACKAGE_NAME+".nameSession", et.getText().toString());
				//UIMA.putExtra(MainActivity.PACKAGE_NAME+".nameSessionT", NS);
				//UIMA.putExtra(MainActivity.PACKAGE_NAME+".stateT", s);					
				MyDBManager db = new MyDBManager(this);
				db.updateStatoSessione(SNT, NS);
				db.close();
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
		
		Log.v("GESTIONE FILE","METODO START");
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
		//nS = tx.getText().toString();
		/*MyDBManager db = new MyDBManager(this);
		int fallCount = db.CountCaduta(NS);
		db.close();
		if(fallCount > 0){
		Cursor crs = db.selectCaduta(NS);
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
		}*/			
	}
	
	private void pause(){
		Log.v("GESTIONE FILE","METODO PAUSE");
		if(mysm != null){
			mysm.unregisterListener(this);			
		}
		if(locMg != null){
			locMg.removeUpdates(this);
		}
		xAccViewS.setText("0");
		yAccViewS.setText("0");
		zAccViewS.setText("0");		
		
		if(!(lastFileName.equals(date))){
			lastFileName = date;
		
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
		Log.v("GESTIONE FILE","METODO STOP");
		if(mysm != null){
			mysm.unregisterListener(this);
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
	
	private void updateUI() {
		Log.v("updateUI", "inzio");
		if(fallList.size() > 0){
			fallList.clear();
		}
		adapter.clear();
		Cursor crs = db.selectCaduta(NS);
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
		        fallList.add(new DatiCadute(day, month, year, hour, minutes, seconds, lat, longi, NS));
		        //adapter.add(new DatiCadute(day, month, year, hour, minutes, seconds, lat, longi, NS));
				}while(crs.moveToNext());
			crs.close();
			adapter.notifyDataSetChanged();
			
			/*runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					adapter.clear();
					for(int i = 0; i<fallList.size(); i++){
						adapter.add(fallList.get(i));
					}
					adapter.notifyDataSetChanged();
				}
			});*/
				
			}

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
						mService.putExtra("nomSess", NS);
						
						startService(mService);
						
						k++;								
					}
 					acData.add(new AccelData(time, x, y, z));
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