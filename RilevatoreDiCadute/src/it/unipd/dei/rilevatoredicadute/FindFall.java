package it.unipd.dei.rilevatoredicadute;

import android.app.IntentService;
import android.content.Intent;

public class FindFall extends IntentService{
	private float xVal;
	private float yVal;
	private float zVal;
	private float xValLast;
	private float yValLast;
	private float zValLast;
	Intent mActivity = null;

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
					
		mActivity = new Intent(this, MyReceiver.class);
				
		if(((java.lang.Math.abs(xVal)- java.lang.Math.abs(xValLast))> 7) || ((java.lang.Math.abs(yVal)- java.lang.Math.abs(yValLast))> 7) || ((java.lang.Math.abs(zVal)- java.lang.Math.abs(zValLast))> 7)){
			mActivity.putExtra("fall", true);
			mActivity.putExtra("lat", intent.getDoubleExtra("lat", 0));
			mActivity.putExtra("long", intent.getDoubleExtra("long", 0));
			sendBroadcast(mActivity);
		}
		stopSelf();
	}
}
