package it.unipd.dei.rilevatoredicadute;

public class DatiCadute {
	
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	private int second;
	private int latitudine;
	private int longitudine;
	
	
	public DatiCadute(int d, int m, int y, int h, int min, int sec, int lat, int lon){		
		day = d;
		month = m;
		year = y;
		hour = h;
		minute = min;
		second = sec;
		latitudine=lat;
		longitudine=lon;
	}
	
	public DatiCadute(){
		this( 0, 0, 0, 0, 0, 0, 0, 0);		 
	}	
	
	public void setData(int d, int m, int y){
		day = d;
		month = m;
		year = y;
	}
	
	public void setHour(int h, int min, int sec){
		hour = h;
		minute = min;
		second = sec;
	}	
	

	public String getData(){
		return(Integer.toString(day)+ "/" + Integer.toString(month) + "/" + Integer.toString(year));
	}
	
	public String getHour(){
		return(Integer.toString(hour)+ ":" + Integer.toString(minute) + ":" + Integer.toString(second));
	}
	
	public void setLatitudine(int lat){
		latitudine=lat;
	}
	
	public void setLongitudine(int lon){
		longitudine=lon;
	}
	
	public int getLongitudine(){
		return longitudine;
	}

	public int getLatitudine(){
		return latitudine;
	}

	
	
}