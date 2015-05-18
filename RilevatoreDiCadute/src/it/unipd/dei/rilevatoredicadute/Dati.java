package it.unipd.dei.rilevatoredicadute;

public class Dati {
	
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	private int second;
	private String nomeSessione;
	private String durataSessione;
	
	public Dati(String nS, int d, int m, int y, int h, int min, int sec){
		nomeSessione = nS;
		day = d;
		month = m;
		year = y;
		hour = h;
		minute = min;
		second = sec;
	}
	
	public Dati(){
		this("nessuna sessione", 0, 0, 0, 0, 0, 0);
		durataSessione = 0 + ":" + 0 + ":" + 0; 
	}

	public void setNomeSessione( String nS){
		nomeSessione = nS;
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
	
	public String getNomeSessione(){
		return nomeSessione;
	}
	
	public String getData(){
		return(Integer.toString(day)+ "/" + Integer.toString(month) + "/" + Integer.toString(year));
	}
	
	public String getHour(){
		return(Integer.toString(hour)+ ":" + Integer.toString(minute) + ":" + Integer.toString(second));
	}
	
	public String getDurataSessione(){
		return durataSessione;
	}
}