package it.unipd.dei.rilevatoredicadute;

import android.content.Intent;
import android.os.IBinder;
import android.os.Binder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Chronometer;
import android.app.Service;

public class ServiceCronometro extends Service {	
	
	private IBinder mBinder = new MyBinder();
	private Chronometer crono;
	private long pausa;
	
	public ServiceCronometro(){
		super();
	}
	
	
	@Override
	public void onCreate(){
		super.onCreate();
		//questo = getIntent();
		
		//crono = new Chronometer(this);
		//crono.setBase(SystemClock.elapsedRealtime());
		//crono.start();		
	}
	
	@Override
	public int onStartCommand (Intent intent, int flags, int startId) {
	    //data=(String) intent.getExtras().get("data");
		pausa = intent.getLongExtra("pausa",0L);
		Log.v("valore pausaS",""+pausa+"");
		crono = new Chronometer(this);
		if(pausa==0L)
			crono.setBase(SystemClock.elapsedRealtime());
		else
			crono.setBase(crono.getBase() + pausa);
		crono.start();			
		return START_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent intent){
		return mBinder;
	}

	/*public void SetBase(Chronometer crono,long TIME){
		//crono.setBase(SystemClock.elapsedRealtime());
		crono.setBase(TIME);
	}

	public long GetBase(Chronometer crono){
		return crono.getBase();	
	}

	public void Start(Chronometer crono){
		crono.start();	
	}

	public void Stop(Chronometer crono){
		crono.stop();
	}*/
	
	
	public String StampaDurata() {		
			long elapsedMillis = SystemClock.elapsedRealtime()- crono.getBase();
			int hours = (int) (elapsedMillis / 3600000);
			int minutes = (int) (elapsedMillis - hours * 3600000) / 60000;
			int seconds = (int) (elapsedMillis - hours * 3600000 - minutes * 60000) / 1000;
			//int millis = (int) (elapsedMillis - hours * 3600000 - minutes * 60000 - seconds * 1000);
			return hours + ":" + minutes + ":" + seconds;
		
	 }
	
 public class MyBinder extends Binder {
	 ServiceCronometro getService() {
		 return ServiceCronometro.this;
	 }
 }


}




