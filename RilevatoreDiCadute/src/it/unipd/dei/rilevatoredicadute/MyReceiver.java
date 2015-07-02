package it.unipd.dei.rilevatoredicadute;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getBooleanExtra("fall", false)){
			double longitude = intent.getDoubleExtra("long", 0);
			double latitude = intent.getDoubleExtra("lat", 0);
			Toast.makeText(context, "caduta" + " " + longitude + " " + latitude, Toast.LENGTH_LONG).show();
			Vibrator vibr = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
			vibr.vibrate(500L);
		}	
	}
}
