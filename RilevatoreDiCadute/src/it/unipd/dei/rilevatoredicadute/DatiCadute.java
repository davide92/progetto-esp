package it.unipd.dei.rilevatoredicadute;

public class DatiCadute {
	
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	private int second;
	private String latitudine;
	private String longitudine;
	private String session;
	
	public DatiCadute(int d, int m, int y, int h, int min, int sec, String lat, String lon, String sess){		
		day = d;
		month = m;
		year = y;
		hour = h;
		minute = min;
		second = sec;
		latitudine=lat;
		longitudine=lon;
		session=sess;
	}
	
	public DatiCadute(){
		this( 0, 0, 0, 0, 0, 0, "0.0", "0.0", "nessuna sessione");		 
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
	
	public void setLatitudine(String lat){
		latitudine=lat;
	}
	
	public void setLongitudine(String lon){
		longitudine=lon;
	}
	
	public String getLongitudine(){
		return longitudine;
	}

	public String getLatitudine(){
		return latitudine;
	}
	
	public String getSessione(){
		return session;
		}	
	
}