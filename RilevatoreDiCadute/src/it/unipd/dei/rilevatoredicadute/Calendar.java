package it.unipd.dei.rilevatoredicadute;


import java.util.GregorianCalendar;



public abstract class Calendar extends Object implements Comparable<Calendar> {	
	
	GregorianCalendar dataAttuale=new GregorianCalendar();	
	
	public int anno(){
		int year = dataAttuale.get(GregorianCalendar.YEAR);
		return year;
	}
	
	public int mese(){
		int month = dataAttuale.get(GregorianCalendar.MONTH)+1;
		return month;
	}
	
	public int giorno(){
		int day = dataAttuale.get(GregorianCalendar.DATE);
		return day;
	}
	
	public int ora(){
		int hour = dataAttuale.get(GregorianCalendar.HOUR);
		return hour;
	}
	
	public int minuti(){
		int minutes = dataAttuale.get(GregorianCalendar.MINUTE);
		return minutes;
	}
	
	public int secondi(){
		int seconds = dataAttuale.get(GregorianCalendar.SECOND);
		return seconds;
	}
	
	
}