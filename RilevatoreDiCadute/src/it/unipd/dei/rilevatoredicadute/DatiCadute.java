package it.unipd.dei.rilevatoredicadute;

public class DatiCadute {
	
	private int anno;
	private int mese;
	private int giorno;
	private int ora;
	private int minuti;
	private int secondi;
	private String latitudine;
	private String longitudine;
	private String sessione;
	
	public DatiCadute(int d, int m, int y, int h, int min, int sec, String lat, String lon, String sess){		
		giorno = d;
		mese = m;
		anno = y;
		ora = h;
		minuti = min;
		secondi = sec;
		latitudine=lat;
		longitudine=lon;
		sessione=sess;
	}
	
	public DatiCadute(){
		this( 0, 0, 0, 0, 0, 0, "0.0", "0.0", "nessuna sessione");		 
	}	
	
	public void setData(int d, int m, int y){
		giorno = d;
		mese = m;
		anno = y;
	}
	
	public void setOra(int h, int min, int sec){
		ora = h;
		minuti = min;
		secondi = sec;
	}	
	

	public String getData(){
		return(Integer.toString(giorno)+ "/" + Integer.toString(mese) + "/" + Integer.toString(anno));
	}
	
	public String getOra(){
		return(Integer.toString(ora)+ ":" + Integer.toString(minuti) + ":" + Integer.toString(secondi));
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
		return sessione;
		}	
	
}