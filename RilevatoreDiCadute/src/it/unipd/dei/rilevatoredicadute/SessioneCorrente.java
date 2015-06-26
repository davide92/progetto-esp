package it.unipd.dei.rilevatoredicadute;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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

import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.widget.Toast;
import android.widget.Chronometer;


public class SessioneCorrente extends ActionBarActivity implements SensorEventListener{
	
	
	private Chronometer chronometer;
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
	private Sensor accel;
	private ArrayList<AccelData> acData = new ArrayList<AccelData>(1000);
	private long it;
	private int i = 0;
	private int j = 0;
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
	String date;
	Lock lock = new ReentrantLock();
	//float deltaX=0;
	//float deltaY=0;
	//float deltaZ=0;
	float lastX, lastY, lastZ;
	AlgoritmoCaduta AC;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sess_curr);
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		Log.v("TAG", "----INIZIO-SESSIONE-CORRENTE----");
		db = new MyDBManager(this);						
		final ImageButton playBtn = (ImageButton)findViewById(R.id.start);
		final ImageButton pauseBtn = (ImageButton)findViewById(R.id.pause);
		final ImageButton stopBtn = (ImageButton)findViewById(R.id.stop);
		xAccViewS= (TextView) findViewById(R.id.xDataS);
		yAccViewS= (TextView) findViewById(R.id.yDataS);
		zAccViewS= (TextView) findViewById(R.id.zDataS);
		tx=(TextView)findViewById(R.id.TestoSessCurr);
		chronometer = (Chronometer) findViewById(R.id.chronometer);
		
		intent=getIntent();		
		tempoStop[0] =intent.getLongExtra(MainActivity.PACKAGE_NAME+".TempoPausa",0);
		nomeSessione = intent.getStringExtra(MainActivity.PACKAGE_NAME+".nomeSessione");
		statoSessione = intent.getIntExtra(MainActivity.PACKAGE_NAME+".statoSessione", 0);				
		tx.setText(nomeSessione);		
		
		Log.v("SessioneCorrenteNome", ""+nomeSessione+"");
		Log.v("TAG__SC__tempoStop","sec>"+(tempoStop[0]/1000 % 60)+"<minuti>"+((tempoStop[0] / (1000*60)) % 60)+"<ore>"+((tempoStop[0] / (1000*60*60)) % 24)+"");
		Log.v("stato sessione corrente", ""+statoSessione+"");
		if(nomeSessione!=null){
			if(statoSessione==1){
				playBtn.setVisibility(View.INVISIBLE);
				pauseBtn.setVisibility(View.VISIBLE);
				chronometer.setBase(SystemClock.elapsedRealtime()-tempoStop[0] );
				chronometer.start();
				cal=new GregorianCalendar();
				start();
			}
			else{
				if(statoSessione==2){
				playBtn.setVisibility(View.VISIBLE);
				pauseBtn.setVisibility(View.INVISIBLE);
				chronometer.setBase( tempoStop[0]);//SystemClock.elapsedRealtime()-tempoStop[0] );
				tempoPausa=chronometer.getBase();
				}
			}
		}
		playBtn.setOnClickListener(new View.OnClickListener() {					
			@Override
			public void onClick(View v) {
				
				statoSessione=1;
				cal= new GregorianCalendar();								
				playBtn.setVisibility(View.INVISIBLE);
				pauseBtn.setVisibility(View.VISIBLE);				
								
				//GESTIONE PLAY/RESUME CRONOMETRO
				if ( p == 0 )
			        chronometer.setBase( SystemClock.elapsedRealtime()-tempoStop[0]);
			    // on resume after pause
			    else
			    {
			        long intervalloPausa = (SystemClock.elapsedRealtime() -p);// tempoStop[0]);
			        chronometer.setBase( chronometer.getBase() + intervalloPausa );
			    }
			    chronometer.start();
			    //FINE GESTIONE	
			    start();
			}			
		});
		
		pauseBtn.setOnClickListener(new View.OnClickListener(){			
			@Override
			public void onClick(View v){
				playBtn.setVisibility(View.VISIBLE);
				pauseBtn.setVisibility(View.INVISIBLE);
				Log.v("List","ho premuti il tasto pause");
				tempoPausa=SystemClock.elapsedRealtime()- chronometer.getBase();
				chronometer.stop();
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
					tempoPausa=SystemClock.elapsedRealtime()-chronometer.getBase();
				Long saveTime = tempoPausa;// +tempoStop[0];
		        int seconds = (int)(saveTime/1000 % 60);
		        int minutes = (int) ((saveTime / (1000*60)) % 60);
				int hours   = (int) ((saveTime / (1000*60*60)) % 24);		        
				String ora=""+hours+":"+minutes+":"+seconds+"";
				Log.v("durataSessione",ora);
				db.updateDurataSessione(ora,tx.getText().toString());
				chronometer.stop();
				Log.v("stopSessione------->",tx.getText().toString());		
				statoSessione=0;
				stop();
				Intent back = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(back);				
			}						
		});		
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
		
		mysm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		if(mysm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
			accel = mysm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			mysm.registerListener((SensorEventListener) this, accel, SensorManager.SENSOR_DELAY_NORMAL);
			Log.v("valore mysm",""+mysm+"");
		//if(mysm.registerListener((SensorEventListener) this, accel, SensorManager.SENSOR_DELAY_NORMAL)){
			year = cal.get(GregorianCalendar.YEAR);
			month = cal.get(GregorianCalendar.MONTH)+1;
			day = cal.get(GregorianCalendar.DAY_OF_MONTH);
			//int hour = c.get(GregorianCalendar.HOUR_OF_DAY);
			//int min = c.get(GregorianCalendar.MINUTE);
			//int sec= c.get(GregorianCalendar.SECOND);
			date = "" + year + month + day /*+ hour + min + sec */;
			//date="01022015";
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
				
				/*xAccView.setText("" + event.values[0]);
				yAccView.setText("" + event.values[1]);
				zAccView.setText("" + event.values[2]);*/
				try {
					if(lock.tryLock(10000, TimeUnit.MILLISECONDS)){
						float x = event.values[0];
						float y = event.values[1];
						float z = event.values[2];
						
						//x = Math.abs(lastX - event.values[0]);
						//y = Math.abs(lastY - event.values[1]);
						//z = Math.abs(lastZ - event.values[2]);

						// if the change is below 2, it is just plain noise
						//if (x < 2)
							//x = 0;
						//if (y < 2)
							//y = 0;
						//if (z < 2)
							//z = 0;

						// set the last know values of x,y,z
						//lastX = event.values[0];
						//lastY = event.values[1];
						//lastZ = event.values[2];
						//leastTime += System.currentTimeMillis();
						
						AccelData d = new AccelData(System.currentTimeMillis()-it, x, y, z);
						acData.add(d);
						AC=new AlgoritmoCaduta(x,y,z,this);
						AC.Caduta(x,y,z,this);
					}
					if(lock.tryLock(31000, TimeUnit.MILLISECONDS) &&(!acData.isEmpty()) && j < acData.size()){
						xAccViewS.setText("" + acData.get(j).getX());
						yAccViewS.setText("" + acData.get(j).getY());
						zAccViewS.setText("" + acData.get(j).getZ());
						j+=3;						
					}
					lock.unlock();
				} catch (InterruptedException e) {
					Log.e("Problemi accelerometro", date, e); 
					Toast.makeText(	this, "Errore lock", Toast.LENGTH_LONG).show(); 
				}
				
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	//	Log.d("Accelerometro", "onAccurancyChanged: " + sensor + ", accuracy: " + accuracy);
		
	}
}