//  CLASSE PER LA GESTIONE DELLA SESSIONE: QUI SI PUO' COMINCIARE UNA NUOVA SESSIONE, METTERLA IN PAUSA, STOPPARLA,
//  SONO VISUALIZZATI I DATI PROVENIENTI DALL'ACCELEROMETRO E LE CADUTE IN TEMPO REALE

package it.unipd.dei.rilevatoredicadute;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.os.IBinder;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ListView;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.ComponentName;
import android.util.Log;
import android.graphics.Color;
import android.database.Cursor;

import java.util.GregorianCalendar;
import java.util.Random;
import java.util.LinkedList;
import java.util.List;

import it.unipd.dei.rilevatoredicadute.ServiceCronometro.MyBinder;
import it.unipd.dei.rilevatoredicadute.FindFall.MyBinderText;


public class NewThird extends ActionBarActivity{
	
	GregorianCalendar cal;
	MyDBManager db;
	public long stopTime=0;		
	private  ListView listView;
	private List<DatiCadute> fallList;//lista delle cadute temporanea 
	long it = 0;	
	TextView timestampText;
	TextView xAccViewS = null;
	TextView yAccViewS = null;
	TextView zAccViewS = null;
	TextView tx;
	public static String PACKAGE_NAME;
	String date,data;
	String NS;//nomeSessione
	String StampaDurataService;	
	Intent mService = null;
	CountDownTimer cdCrono = null; //conto alla rovescia perla visualizzazione dati del cronometro
	CountDownTimer cdText = null; //conto alla rovescia per la visualizzazione dati accelerometro
	/*memorizza stato della sessione
	  0 -> activity stoppata
	  1 -> activity avviata
	  2 -> activity in pausa
	  3 -> cambio rotazione schermo
	 */
	int s;
	int SNT; //stato sessione activity newthird per gestione parametri	
	int cStart = 0; // contatore metodo start()
	ImageButton playBtn;
	ImageButton pauseBtn;
	ImageButton stopBtn;		
	boolean mServiceBound = false;
	boolean mServiceBoundText = false;
	boolean rec, vis = true;
	ServiceCronometro sc;
	FindFall ff;
	Intent T;	
	Intent intent;
	Intent TextIntent;
	MyReceiver myReceiver;   //Receiver per aggiornare la durata della sessione
	CustomAdapterFalls adapter;	
	Receiver receiver; //Receiver per l'aggiornamento delle informazioni su una caduta durante una sessione
	TextReceiver textReceiver; //Receiver per l'aggiornamento dei dati dell'accelerometro
	float [] arrayRicevuto = new float[3];
	boolean VarSavedInstance = false; //variabile per sapere se è stata salvata l'istanza dell'activity
	boolean permesso;
	
	@Override
	protected void onCreate(Bundle savedInstance) {		
		super.onCreate(savedInstance);		
		setContentView(R.layout.activity_sess_curr);
				
		listView = (ListView) findViewById(R.id.listViewCadute);
		fallList = new LinkedList<DatiCadute>();
		adapter = new CustomAdapterFalls(this, R.id.listViewCadute, fallList);       
		listView.setAdapter(adapter);
		cal= new GregorianCalendar();		
				
		timestampText = (TextView) findViewById(R.id.timestamp_text);
		playBtn = (ImageButton)findViewById(R.id.start);
		pauseBtn = (ImageButton)findViewById(R.id.pause);
		stopBtn = (ImageButton)findViewById(R.id.stop);		
		//inizializzazione dello stato della sessione
		if((savedInstance !=null)){	
			VarSavedInstance = true;
			Log.v("<<SAVED INSTANCE ON>>", "---SAVED INSTANCE ON---");			
			StampaDurataService = savedInstance.getString("durataCrono");			
			SNT = savedInstance.getInt("statesession");			
			data = savedInstance.getString("data");
			stopTime = savedInstance.getLong("TempoPausa");
			NS = savedInstance.getString("nomeSessione");
			cStart = savedInstance.getInt("cMs");
			Log.v("null","" +NS);
		}				
	}
	//salvataggio dello stato della sessione in corso
	@Override
	protected void onSaveInstanceState(Bundle savedInstance) {			
	    savedInstance.putInt("statesession", SNT);	    
	    savedInstance.putString("data", date);
	    savedInstance.putString("durataCrono", StampaDurataService);	    
	    savedInstance.putLong("TempoPausa", stopTime);
	    savedInstance.putString("nomeSessione", NS);
	    savedInstance.putInt("cMs", cStart);
	    Log.v("<<ATTENZIONE>>","CAMBIO ORIENTAZIONE TELEFONO");
	    super.onSaveInstanceState(savedInstance);	    
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		Log.v("TAG", "----INIZIO-THIRD----");
		//COUNT DOWN TIMER PER LA GESTIONE DELLA TEXTVIEW CHE VISUALIZZA LA DURATA DELLA SESSIONE
		cdCrono = new CountDownTimer(1000L, 100L) {					
					@Override
					public void onTick(long millisUntilFinished) {
					// TODO Auto-generated method stub										
					}					
					@Override
					public void onFinish() {						
					if(StampaDurataService != null)	
						timestampText.setText(""+StampaDurataService+"");
					else
						timestampText.setText("0:0:0");			
					cdCrono.start();								
					}
		};
		//COUNT DOWN TIMER PER LA GESTIONE DELLA TEXTVIEW CHE VISUALIZZA I VALORI DELL'ACCELEROMETRO	
		cdText = new CountDownTimer(5000L, 500L) {					
					@Override
					public void onTick(long millisUntilFinished) {
					// TODO Auto-generated method stub										
					}					
					@Override
					public void onFinish() {
						if(arrayRicevuto[0] != 0.0f && arrayRicevuto[1] != 0.0f && arrayRicevuto[2] != 0.0f){
							xAccViewS.setText(""+arrayRicevuto[0]);
							yAccViewS.setText(""+arrayRicevuto[1]);
							zAccViewS.setText(""+arrayRicevuto[2]);
						}
						else{
							xAccViewS.setText("0.0");
							yAccViewS.setText("0.0");
							zAccViewS.setText("0.0");
						}
					cdText.start();								
					}
		};
		
		myReceiver = new MyReceiver();
		receiver = new Receiver();
		textReceiver = new TextReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ServiceCronometro.MY_ACTION);
		IntentFilter filter = new IntentFilter();
		filter.addAction(FindFall.BROADCAST);
		IntentFilter TextFilt = new IntentFilter();
		TextFilt.addAction(FindFall.TEXTVIEW);
		registerReceiver(myReceiver,intentFilter);
		registerReceiver(receiver, filter);
		registerReceiver(textReceiver, TextFilt);
		
		db = new MyDBManager(this);	
		PACKAGE_NAME = getApplicationContext().getPackageName();
		T = getIntent(); 
		if(!(VarSavedInstance)){
			NS = T.getStringExtra(MainActivity.PACKAGE_NAME+".nomeSessione");
			Log.v("null","" +NS);
			        
		}
		s = T.getIntExtra(MainActivity.PACKAGE_NAME+".statoSessione", 3);
		
		xAccViewS = (TextView) findViewById(R.id.xDataS);
		yAccViewS = (TextView) findViewById(R.id.yDataS);
		zAccViewS = (TextView) findViewById(R.id.zDataS);
		tx = (TextView)findViewById(R.id.TestoSessCurr);        
        tx.setText(NS);    
        
        if(s == 1 && SNT ==2 ){
        	playBtn.setVisibility(View.VISIBLE);
			pauseBtn.setVisibility(View.INVISIBLE);
			//stopTime = T.getLongExtra(MainActivity.PACKAGE_NAME+".PausaTempo", 0);
        }else{
        	if(s == 2 && SNT == 1){
        		playBtn.setVisibility(View.INVISIBLE);
				pauseBtn.setVisibility(View.VISIBLE);
				cStart++;
				cdText.start();
        	}else{
        		if(s == 3){//GESTIONE ORIENTAZIONE TELEFONO
        			if(SNT == 1){				
        				playBtn.setVisibility(View.INVISIBLE);
        				pauseBtn.setVisibility(View.VISIBLE);
        				cStart++;
        				cdText.start();						
        			}
        			else{				
        				playBtn.setVisibility(View.VISIBLE);
        				pauseBtn.setVisibility(View.INVISIBLE);				
        			}
        		}else{
        			SNT = s;
        			if(s == 1){
        				playBtn.setVisibility(View.INVISIBLE);
        				pauseBtn.setVisibility(View.VISIBLE);
        				cStart++;
        				cdText.start();
        			}
        			else{			
        				playBtn.setVisibility(View.VISIBLE);
        				pauseBtn.setVisibility(View.INVISIBLE);
        				stopTime = T.getLongExtra(MainActivity.PACKAGE_NAME+".PausaTempo", 0);
        			}
        		}	
        	}
        }
        
		intent = new Intent(getApplicationContext(), ServiceCronometro.class);		
		TextIntent = new Intent(getApplicationContext(), FindFall.class);
		TextIntent.putExtra("nome sessione", NS);
		
		cdCrono.start();
		updateUI();//aggiornamento interfaccia utente
		
		
		playBtn.setOnClickListener(new View.OnClickListener() {					
			@Override
			public void onClick(View v) {				
				if(SNT == 2)
					cdCrono.start();
				SNT = 1;
				playBtn.setVisibility(View.INVISIBLE);
				pauseBtn.setVisibility(View.VISIBLE);				
				
				cStart++;
				String data = ""+cal.get(GregorianCalendar.YEAR)+ "/" + (cal.get(GregorianCalendar.MONTH)+1)+ "/" +cal.get(GregorianCalendar.DATE);
																
				long millisecondi = System.currentTimeMillis();
				int secondo = (int) (millisecondi / 1000) % 60 ;
				int minuto = (int) ((millisecondi / (1000*60)) % 60);
				int ora   = (int) ((millisecondi / (1000*60*60)) % 24);
				String tempo = ""+ora+ ":" + minuto+ ":" +secondo+"";	
				Random rm = new Random();
				int cl = Color.argb(255, rm.nextInt(254), rm.nextInt(254), rm.nextInt(254));
				if(db.noSessStessoNome(NS)){
						db.aggSessione(NS, data, tempo, "XX:XX:XX", 0, cl, 1, 0);
				}
				if ( stopTime != 0 ){
					long intervalloPausa = (SystemClock.elapsedRealtime() - stopTime);
					intent.putExtra("pausa",intervalloPausa);
				}				
				//avvio dei service
				startService(intent);
				bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);				
				startService(TextIntent);
				bindService(TextIntent, mServiceConnectionText, Context.BIND_AUTO_CREATE);
				cdText.start();
				Log.v("List-1","--HO PREMUTO IL TASTO PLAY---");				
			}						
		});
		
		pauseBtn.setOnClickListener(new View.OnClickListener(){			
			@Override
			public void onClick(View v){				
				
				SNT = 2;
				cStart--;
				cdCrono.cancel();				
				timestampText.setText(""+StampaDurataService+"");
				playBtn.setVisibility(View.VISIBLE);
				pauseBtn.setVisibility(View.INVISIBLE);
				Log.v("List-2","--HO PREMUTO IL TASTO PAUSE--");				
			    stopTime = SystemClock.elapsedRealtime();	
			    //chiusura della "connessione" al service		    
			    if (mServiceBound) {
					 unbindService(mServiceConnection);
					 mServiceBound = false;
					 }
			    //chiusura della "connessione" al service
				if(mServiceBoundText){
					unbindService(mServiceConnectionText);
					mServiceBoundText = false;
				}
			    stopService(TextIntent);
			    cdText.cancel();	//cancella conto alla rovescia		  		    
			}				
		});
		
		stopBtn.setOnClickListener(new View.OnClickListener(){			
			@Override
			public void onClick(View v){
				//aggiornamento della durata della sessione					
				if(SNT == 0){
					return;
				}
				SNT = 0;
				Log.v("List-3","--HO PREMUTO IL TASTO STOP--");								
				String ora=StampaDurataService;				
				db.aggiornaDurataSessione(ora,NS);
				db.aggiornaStatoSessione(SNT, NS);
								
				Log.v("stopSessione------->",""+NS+"");
				//chiusura della "connessione" al service
				if (mServiceBound) {
					 unbindService(mServiceConnection);
					 mServiceBound = false;
					 }
				//chiusura della "connessione" al service
				if(mServiceBoundText){
					unbindService(mServiceConnectionText);
					mServiceBoundText = false;
				}
				
				stopService(intent);
				if(cStart >0)
					stopService(TextIntent);
				
				cdCrono.cancel();
				cdText.cancel();
								
				Intent UIMA;
			    UIMA = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(UIMA);
				
			}			
			
		});		
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		SNT = 2;
		Log.v("ACT-THIRD","PAUSED");
		String ora=StampaDurataService;
		db.aggiornaDurataSessione(ora,NS);
		db.aggiornaStatoSessione(SNT, NS);
						
		Log.v("stopSessione------->",""+NS+"");
		//chiusura della "connessione" al service
		if (mServiceBound) {
			 unbindService(mServiceConnection);
			 mServiceBound = false;
			 }
		//chiusura della "connessione" al service
		if(mServiceBoundText){
			unbindService(mServiceConnectionText);
			mServiceBoundText = false;
		}
		
		stopService(intent);
		if(cStart >0)
			stopService(TextIntent);
		
		cdCrono.cancel();
		cdText.cancel();
	}	

	@Override
	protected void onStop(){
		super.onStop();
		SNT = 0;
		db.aggiornaStatoSessione(SNT, NS);
		Log.v("ACTIVITY NEWTHIRD","NEWTHIRD STOPPED");
		Log.v("sessione stop",""+SNT+"");
		try{
			unregisterReceiver(receiver);
			}catch(IllegalArgumentException e){						
			}
		try{
			unregisterReceiver(myReceiver);
		}catch(Exception exc){			
		}
		try{
			unregisterReceiver(textReceiver);
		}catch(Exception exc){
			
		}
		//chiusura della "connessione" al service
		if (mServiceBound) {
			unbindService(mServiceConnection);
			mServiceBound = false;
	    }
		//chiusura della "connessione" al service
		if(mServiceBoundText){
			unbindService(mServiceConnectionText);
			mServiceBoundText = false;
		}
		cdCrono.cancel();
		cdText.cancel();					
	}
	
	@Override
	protected void onRestart(){
		super.onRestart();	
		Log.v("NEW THIRD", "ACTIVITY NEWTHIRD RESTART  "+NS);
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();	
		if (db != null) 
	    {
	        db.close();
	    }
	}
	
	//RECEIVER GESTIONE TEXTVIEW DURATA SESSIONE
	private class MyReceiver extends BroadcastReceiver{		 
		 @Override
		 public void onReceive(Context arg0, Intent arg1) {
		  // TODO Auto-generated method stub		  	  
		  if(SNT != 2)
			 StampaDurataService = arg1.getStringExtra("TimeStamp");
		 }		 
	}
	
	//receiver che fa vibrare il dispositivo e aggiorna l'interfaccia utente avvenuta una caduta
	private class Receiver extends BroadcastReceiver{			
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			boolean ricevuto = arg1.getBooleanExtra("fall", false);			
			if(ricevuto){					
				Vibrator vibr = (Vibrator) arg0.getSystemService(Context.VIBRATOR_SERVICE);
				vibr.vibrate(500L);			
				updateUI();
			}						
		}						
	}    
	//receiver per la lettura dei valori dell'accelerometro
	private class TextReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context arg0, Intent arg1){
			if(SNT != 2)
				arrayRicevuto = arg1.getFloatArrayExtra("textview");//array dei valori degli assi x, y e z dell'accelerometro	
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
	 
	 private ServiceConnection mServiceConnectionText= new ServiceConnection(){
		 @Override
		 public void onServiceDisconnected(ComponentName name) {
		 mServiceBoundText = false;
		 }		 
		 
		 @Override
		 public void onServiceConnected(ComponentName name, IBinder service) {
		 MyBinderText myBinderT = (MyBinderText) service;
		 ff = myBinderT.getService();
		 mServiceBoundText = true;
		 } 
	 };
	 
	// aggiunta degli elementi del menù	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
			
		super .onCreateOptionsMenu(menu);		
		menu.add(0, R.id.mostra, 1, "Mostra Sessioni");		
		return true;
	}
	//azioni da fare nella selezione di un elemento
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()){
			case(R.id.mostra):{				  
				Intent UIMA;
				UIMA = new Intent(getApplicationContext(), MainActivity.class);
				Log.v("---","---");										
				db.aggiornaStatoSessione(SNT, NS);
				if(SNT == 2){
					db.inserireTempoPausaSessione(NS, stopTime);					
				}
				if(mServiceBoundText){
					unbindService(mServiceConnectionText);
					mServiceBoundText = false;
				}
				startActivity(UIMA);
			break;
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
	}	
	
	//metodo per aggiornare la UI nel caso di una caduta rilevata
	private void updateUI() {
		//cancellazione dei dati salvati e visualizzati precedentemente
		if(fallList.size() > 0){
			fallList.clear();
		}
		adapter.clear();
		//lettura di tutte le cadute avvenute nella sessione
		Cursor crs = db.selezCaduta(NS);
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
		        String lat = crs.getString(crs.getColumnIndex("Latitudine"));
		        String longi = crs.getString(crs.getColumnIndex("Longitudine"));
		        fallList.add(new DatiCadute(day, month, year, hour, minutes, seconds, lat, longi, NS));
			    //adapter.add(new DatiCadute(day, month, year, hour, minutes, seconds, lat, longi, NS));
			}while(crs.moveToNext());
			//avviso della presenza di una nuova caduta
			adapter.notifyDataSetChanged();								       				
		}
		crs.close();
	}	
}