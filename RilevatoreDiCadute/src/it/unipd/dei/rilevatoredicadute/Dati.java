package it.unipd.dei.rilevatoredicadute;

public class Dati {
	
	private int anno;
	private int mese;
	private int giorno;
	private int ora;
	private int minuti;
	private int secondi;
	private int cadute;
	private int colore;
	private int stato;
	private String nomeSessione;
	private String durataSessione;
	
	
	public Dati(String nS, int d, int m, int y, int h, int min, int sec, String dS, int fal, int col, int sta){
		nomeSessione = nS;
		giorno = d;
		mese = m;
		anno = y;
		ora = h;
		minuti = min;
		secondi = sec;
		durataSessione = dS;
		cadute=fal;
		colore = col;
		stato=sta;
	}
	
	public Dati(){		
		this("nessuna sessione", 0, 0, 0, 0, 0, 0, 0 + ":" + 0 + ":" + 0, 0, 0, 0);		 
	}

	public void setNomeSessione( String nS){
		nomeSessione = nS;
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
	
	public void setSessione(String ds){
		durataSessione=ds;
	}
	
	public void setStato(int s){
		stato=s;
	}
		
	public String getNomeSessione(){
		return nomeSessione;
	}

	public String getData(){
		return(Integer.toString(giorno)+ "/" + Integer.toString(mese) + "/" + Integer.toString(anno));
	}
	
	public String getOra(){
		return(Integer.toString(ora)+ ":" + Integer.toString(minuti) + ":" + Integer.toString(secondi));
	}
	
	public String getDurataSessione(){
		return durataSessione;
	}
	
	public int getCadute(){
		return cadute;
	}
	public int getColore(){
		return colore;
	}
	
	public int getStato(){
		return stato;
	}
}