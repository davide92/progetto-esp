package it.unipd.dei.rilevatoredicadute;

import android.app.IntentService;
import android.content.Intent;

import java.util.GregorianCalendar;

public class FindFall extends IntentService{
	private float xVal;
	private float yVal;
	private float zVal;
	private float xValLast;
	private float yValLast;
	private float zValLast;
	Intent mReceiver = null;
	private float alpha=(float)10;
	String sessione;	
	MyDBManager dbF;
	GregorianCalendar calendar;
	double lat,lon;

	public FindFall() {
		super("Find Fall");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
			
		xVal = intent.getFloatExtra("xVal", 0);
		yVal = intent.getFloatExtra("yVal", 0);
		zVal = intent.getFloatExtra("zVal", 0);
		xValLast = intent.getFloatExtra("xValLast", 0);
		yValLast = intent.getFloatExtra("yValLast", 0);
		zValLast = intent.getFloatExtra("zValLast", 0);
		sessione = intent.getStringExtra("nomSess");
		lat = intent.getDoubleExtra("lat", 0);
		lon = intent.getDoubleExtra("long", 0);
					
		mReceiver = new Intent(this, MyReceiver.class);
		Intent thActivity = new Intent(this, NewThird.class);
		
		float x=java.lang.Math.abs(xVal)- java.lang.Math.abs(xValLast);
		float y=java.lang.Math.abs(yVal)- java.lang.Math.abs(yValLast);
		float z=java.lang.Math.abs(zVal)- java.lang.Math.abs(zValLast);
				
		if(((java.lang.Math.abs(x))>= alpha) || ((java.lang.Math.abs(y))>= alpha) || ((java.lang.Math.abs(z))>= alpha)){
			mReceiver.putExtra("fall", true);
			thActivity.putExtra("fall", true);
			mReceiver.putExtra("lat", lat);//intent.getDoubleExtra("lat", 0));
			mReceiver.putExtra("long", lon);//intent.getDoubleExtra("long", 0));
			calendar=new GregorianCalendar();
			String data = ""+calendar.get(GregorianCalendar.YEAR)+ "/" + (calendar.get(GregorianCalendar.MONTH)+1)+ "/" +calendar.get(GregorianCalendar.DATE);
			String ora = ""+calendar.get(GregorianCalendar.HOUR_OF_DAY)+ ":" + calendar.get(GregorianCalendar.MINUTE)+ ":" +calendar.get(GregorianCalendar.SECOND);
			dbF=new MyDBManager(this);	
			dbF.addCaduta(data,ora,Double.toString(lat),Double.toString(lon),sessione);
			dbF.close();
			sendBroadcast(mReceiver);
			sendBroadcast(thActivity);
		}
		
		stopSelf();
	}	
}