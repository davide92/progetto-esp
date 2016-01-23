package it.unipd.dei.rilevatoredicadute;

import java.util.LinkedList;
import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import android.widget.Toast;
import android.database.Cursor;

import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

public class MainActivity extends ActionBarActivity {	
	
	//IL VALORE VARIAIBILE s:
	//s=0, una sessione puo' iniziare
	//s=1, una sessione e' in esecuzione
	//s=2, una sessione e' in pausa		
	
	public static String PACKAGE_NAME; 	
	MyDBManager db;	          
    Intent MA; //intent dell'activity
    String nome;       
    int stato; //stato di ogni sessione salvata
    int statoNSC; //stato nuova sessione in corso    
    long tempoPausaMA;
    String NSC; //nome sessione in corso
    boolean noSessioneInCorso = false;   
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        db = new MyDBManager(this);               
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		Log.v("TAG", "----INIZIO-MAIN-ACTIVITY----");		
        String durataSessione = 0 + ":" + 0 + ":" + 0; 
        MA = getIntent();        
        PACKAGE_NAME = getApplicationContext().getPackageName();
        if(db.contaSessioni() != 0){//layout in presenza di sessioni salvate
        	setContentView(R.layout.activity_main);
        	ListView listView = (ListView) findViewById(R.id.listView1);
            List<Dati> list = new LinkedList<Dati>();         
            //lettura dal database di tutte le sessioni salvate e visualizzazione
            Cursor crs = db.selezTutteSessioni();        
            if(crs.moveToFirst()){
            	do{
                String strData = crs.getString(crs.getColumnIndex("DataInizio"));
                String[] dataCaduta = strData.split("/");
                int giorno = Integer.parseInt(dataCaduta[0]);  
                int mese = Integer.parseInt(dataCaduta[1]);  
                int anno = Integer.parseInt(dataCaduta[2]);           

                String strTime = crs.getString(crs.getColumnIndex("OraInizio"));
                String[] oraCaduta = strTime.split(":");
                int ora = Integer.parseInt(oraCaduta[0]);  
                int minuto = Integer.parseInt(oraCaduta[1]);  
                int secondo = Integer.parseInt(oraCaduta[2]);
                int cadute = db.contaCadute(crs.getString(1));
                durataSessione = crs.getString(crs.getColumnIndex("Durata"));
                if(durataSessione.equals("XX:XX:XX")){
                	NSC = crs.getString(1);
                	statoNSC = Integer.parseInt(crs.getString(crs.getColumnIndex("Stato")));
                	if(statoNSC == 2){
                		tempoPausaMA = Long.parseLong(crs.getString(crs.getColumnIndex("TempoPausa")));            		
                	}
                	noSessioneInCorso = true;            	
                }            
                stato = Integer.parseInt(crs.getString(crs.getColumnIndex("Stato")));
                int colore = crs.getInt(crs.getColumnIndex("Colore"));
                list.add(new Dati(crs.getString(1),giorno, mese, anno, ora, minuto, secondo, durataSessione, cadute, colore, stato));           
            	}while(crs.moveToNext());//fine while
            }
            crs.close();        
            Log.v("NOME SESSIONE IN CORSO MA",""+NSC+"");
            
            CustomAdapter adapter = new CustomAdapter(this, R.layout.list_items, list);       
            listView.setAdapter(adapter);        
            listView.setOnItemClickListener(new OnItemClickListener(){
        	public void onItemClick(AdapterView<?> adapter, View v, int position, long id){      		
        		
        		Log.v("MA-SELEZIONE-SESSIONE",""+(((Dati)adapter.getItemAtPosition(position)).getNomeSessione())+"");
        		Intent UI2;    //intent activity nella second.class 		
        		Intent ST;   //intent activity nella newThird
        		UI2 = new Intent(getApplicationContext(), Second.class);    		
        		ST = new Intent(getApplicationContext(),NewThird.class);
        		
        		if((((Dati)adapter.getItemAtPosition(position)).getNomeSessione()).equals(NSC)){    				   					
        					   			
        					ST.putExtra(PACKAGE_NAME+".nomeSessione",NSC);
        					ST.putExtra(PACKAGE_NAME+".statoSessione", statoNSC);
        					if(statoNSC == 2)
        						ST.putExtra(PACKAGE_NAME+".PausaTempo", tempoPausaMA);
        					startActivity(ST);    				    				
        			}else{
        				final ImageView im = (ImageView) v.findViewById(R.id.picture);
        				final BitmapDrawable bd = (BitmapDrawable) im.getDrawable();
        				final Bitmap b = bd.getBitmap();
        				Bundle extra = new Bundle();
        				extra.putParcelable("image", b);    				   		
        				UI2 = new Intent(getApplicationContext(), Second.class);    		
        				UI2.putExtra("nameSession", ((Dati)adapter.getItemAtPosition(position)).getNomeSessione()); 
        				UI2.putExtras(extra);
        				UI2.putExtra("color", ((Dati)adapter.getItemAtPosition(position)).getColore());
        				startActivity(UI2);    				
        			}
            	}
            });
        }else{
        	setContentView(R.layout.empty_main); //layout senza sessioni
        }             	
                    
	}	
	
	@Override
	protected void onStop() {	    
	    super.onStop();
	    if (db != null) 
	    {
	        db.close();
	    }
	}
	
	@Override
	protected void onPause() {	    
	    super.onPause();
	}
		
	@Override
	protected void onDestroy() 
	{
	    super.onDestroy();
	    if (db != null) 
	    {
	        db.close();
	    }
	}	
	//creazione degli elementi del menù
	@Override
    public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);		
			    
    	menu.add(0, R.id.nuovaSessione, 1, "Nuova Sessione");
    	menu.add(0, R.id.delete, 2, "Elimina Sessione");    	
    		
		return true;
		
    } 
	// azioni per ogni elemento del menù
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
				
		switch (item.getItemId()) {
		case R.id.nuovaSessione:{
			if(!(noSessioneInCorso)){
				Intent NS = new Intent(getApplicationContext(),NewThird.class);
				String par = "Sessione " + trovaNumSess();
				NS.putExtra(PACKAGE_NAME+".nomeSessione", par);
				startActivity(NS);
			}
			else{				
				String text = "IMPOSSIBILE INIZIARE NUOVA SESSIONE. SESSIONE "+NSC+" IN CORSO";				
				Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
				toast.show();
			}
			break;
		}
		case R.id.delete:
			startActivity(new Intent(this, Delete.class));
			break;
 
		}
		return true;		
	}
	
	//metodo per trovare il primo numero di sessione disponibile
	private int trovaNumSess(){
		int i;
		int count = db.contaSessioni();
		for(i = 1; i < (count+1); i++){
			if(db.noSessStessoNome("Sessione "+i)){
				break;
			}
		}
		return i;
	}
	
	@Override
	public void onBackPressed() {		
	}	    
}