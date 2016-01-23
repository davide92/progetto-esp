package it.unipd.dei.rilevatoredicadute;

public class AccelerometroData{
	private float x;
	private float y;
	private float z;
	private long t;
	
	public AccelerometroData( long t, float x, float y, float z){
		this.t = t;
		this.x = x;
		this.y = y;
		this.z = z;
		
	}
	
	public float getX(){
		return x;
	}
	
	public float getY(){
		return y;
	}
	
	public float getZ(){
		return z;
	}
	
	public long getT(){
		return t;
	}


}
