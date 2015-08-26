package it.unipd.dei.rilevatoredicadute;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Binder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Chronometer;
import android.app.Service;

public class ServiceCronometro extends Service {	
	
	IBinder mBinder = new MyBinder();
	Chronometer crono;
	long pausaService;
	CountDownTimer ServiceCrono = null;
	final static String MY_ACTION = "MY_ACTION";
	
	public ServiceCronometro(){
		super();
	}	
	
	@Override
	public void onCreate(){
		super.onCreate();		
		crono = new Chronometer(this);
		Log.v("CREATE-SERVICE-CRONOMETRO","INIZIO_CREATE");
		
		//COUNT DOWN TIMER CHE INVIA ALLA ACTIVITY NEWTHIRD LA STRINGA INDICANTE LA DURATA DELLA SESSIONE		
		ServiceCrono  = new CountDownTimer(500L, 50L) {					
			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub
				//remainingtimeS= millisUntilFinished /1000;							
				}					
			@Override
			public void onFinish() {					 
					//Log.v("STAMPADURATASERVICE",""+StampaDurata()+"");					
					Intent NW = new Intent();
					NW.setAction(MY_ACTION);
					NW.putExtra("TimeStamp", StampaDurata());
					sendBroadcast(NW);
					ServiceCrono.start();						
				}
		};		
	}
	
	public void startCrono(){
		crono.start();
	}
	
	public void stopCrono(){
		crono.stop();		
	}
	
	@Override
	public int onStartCommand (Intent intent, int flags, int startId) {	
		if(intent != null)	
			pausaService = intent.getLongExtra("pausa",0L);
		
		else
			pausaService = 0L;
		
			Log.v("valore pausaS",""+pausaService+"");
			
			if(pausaService==0L)
				crono.setBase(SystemClock.elapsedRealtime());
			else
				crono.setBase(crono.getBase() + pausaService);
			
			startCrono();
			
			/*ServiceCrono  = new CountDownTimer(500L, 50L) {					
				@Override
				public void onTick(long millisUntilFinished) {
					// TODO Auto-generated method stub
					//remainingtimeS= millisUntilFinished /1000;							
					}					
				@Override
				public void onFinish() {					 
						Log.v("STAMPADURATASERVICE",""+StampaDurata()+"");
						//Intent NW = new Intent(getApplicationContext(), NewThird.class);
						Intent NW = new Intent();
						NW.setAction(MY_ACTION);
						NW.putExtra("TimeStamp", StampaDurata());
						sendBroadcast(NW);
						ServiceCrono.start();						
					}
			}.start();*/
			ServiceCrono.start();
			
		return START_STICKY;	
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
	
	public String StampaDurata() {		
			long elapsedMillis = SystemClock.elapsedRealtime()- crono.getBase();
			//Log.v(">>>>>>>",""+crono.getBase()+"");
			int hours = (int) (elapsedMillis / 3600000);
			int minutes = (int) (elapsedMillis - hours * 3600000) / 60000;
			int seconds = (int) (elapsedMillis - hours * 3600000 - minutes * 60000) / 1000;
			//int millis = (int) (elapsedMillis - hours * 3600000 - minutes * 60000 - seconds * 1000);
			String ora = ""+hours+":"+minutes+":"+seconds+""; 
			return ora;		
	 }
	
 public class MyBinder extends Binder {
	 ServiceCronometro getService() {
		 return ServiceCronometro.this;
	 }
 }
 
 @Override
 public void onDestroy(){
	 super.onDestroy();	 	
	 stopCrono();
	 ServiceCrono.cancel();
	 Log.v("----","FINE SERVICE");	 
 } 
	}//FINE CLASSE SERVICE CRONOMETRO












