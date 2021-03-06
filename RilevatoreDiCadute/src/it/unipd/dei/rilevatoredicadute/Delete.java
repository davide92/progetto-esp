/*CLASSE CHE SI OCCUPA DI ELIMINARE UNA O PIU' SESSIONI.
 *  APRE UN ALERTDIALOG PER CONFERMARE L'ELIMINAZIONE
 *  DELLA SESSIONE SCELTA */

package it.unipd.dei.rilevatoredicadute;

import java.util.LinkedList;
import java.util.List;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;

public class Delete extends ActionBarActivity {	
	
	MyDBManager db = new MyDBManager(this);	
	Cursor crs = null;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); 
        String durataSessione = 0 + ":" + 0 + ":" + 0;
        final ListView listView = (ListView) findViewById(R.id.listView1);
        List<Dati> list = new LinkedList<Dati>(); //lista delle sessioni 
        //selezione delle sessioni e visualizzazione nella lista
        crs = db.selezTutteSessioni();         
        if(crs.moveToFirst()){
        	do{
        		String strData = crs.getString(crs.getColumnIndex("DataInizio"));
        		String[] dataCaduta = strData.split("/");
        		int giorno = Integer.parseInt(dataCaduta[0]);  
        		int mese = Integer.parseInt(dataCaduta[1]);  
        		int anno = Integer.parseInt(dataCaduta[2]);

        		String strTempo = crs.getString(crs.getColumnIndex("OraInizio"));
        		String[] oraCaduta = strTempo.split(":");
        		int ora = Integer.parseInt(oraCaduta[0]);  
        		int minuti = Integer.parseInt(oraCaduta[1]);  
        		int secondi = Integer.parseInt(oraCaduta[2]); 
        		int numeroCadute = db.contaCadute(crs.getString(1));
        		int colore = crs.getInt(crs.getColumnIndex("Colore"));
        		int stato = crs.getInt(crs.getColumnIndex("Stato"));
        		durataSessione = crs.getString(crs.getColumnIndex("Durata")); 
        		if(stato == 0){
        			list.add(new Dati(crs.getString(1),giorno, mese, anno,ora, minuti, secondi, durataSessione, numeroCadute, colore, stato));
        		}
            }while(crs.moveToNext());//fine do while
        }
   
        CustomAdapter adapter = new CustomAdapter(this, R.layout.list_items, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener(){
	    	public void onItemClick(AdapterView<?> adapter, View v, int position, long id){    		       	
	    		final AdapterView<?> a = adapter;
		    	 final int pos = position;
		    	 AlertDialog.Builder alert = new AlertDialog.Builder(Delete.this);//dialogo di allerta per l'eliminazione della sessione
		    	 alert.setTitle("Elimina");
		    	 alert.setMessage("Eliminare l'elemento?");
		    	 alert.setPositiveButton("Si", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String sessione = ((Dati)a.getItemAtPosition(pos)).getNomeSessione();
						db.cancSessione(sessione);
						db.cancCadute(sessione);
						db.close();
						getApplicationContext().deleteFile(sessione);
						Intent UI2 = new Intent(getApplicationContext(), MainActivity.class);
			         	startActivity(UI2);
					}
				});
		    	 alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						return;						
					}
				});
		    	AlertDialog dialog = alert.create();
		    	dialog.show();           	
	    	}    	
        });        
        crs.close();
            
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
}