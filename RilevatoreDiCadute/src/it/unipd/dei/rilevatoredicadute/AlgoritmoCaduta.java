package it.unipd.dei.rilevatoredicadute;

import android.widget.Toast;
import android.content.Context;
import android.util.Log;

public class AlgoritmoCaduta{
	
	private float x;
	private float y;
	private float z;
	private float g= (float)9.8;
	float proSca;
	String str= "AVVENUTA CADUTA";
	Context c;
	
	public AlgoritmoCaduta(float cx, float cy, float cz,Context ctx){
		x=cx;
		y=cy;
		z=cz;
		c=ctx;
	}
	
	public void Caduta(float cx, float cy, float cz,Context ctx){
		
		x=cx;
		y=cy;
		z=cz;
		c=ctx;
		proSca=(float)Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2)+Math.pow(z, 2));		
		if(proSca>(g)){
			Log.v("valore proSca",""+proSca+"");
			Toast toast = Toast.makeText(c, str, Toast.LENGTH_SHORT);
			toast.show(); 			
		}		
	}	
}