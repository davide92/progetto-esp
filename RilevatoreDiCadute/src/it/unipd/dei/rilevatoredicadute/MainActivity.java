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
	
	//IL VALORE VAIRAIBILE s:
	//s=0, una sessione si puo' iniziare
	//s=1 una sessione e' in esecuzione
	//s=2 una sessione e' in pausa		
	
	public static String PACKAGE_NAME; 	
	MyDBManager db;	          
    Intent MA;
    String name;       
    int state;
    int stateNSC; //stato nuova sessione in corso    
    long tempoPausaMA;
    String NSC; //nome sessione in corso
    boolean noSessioneInCorso = false;   
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        db = new MyDBManager(this);
        setContentView(R.layout.activity_main);       
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		Log.v("TAG", "----INIZIO-MAIN-ACTIVITY----");		
        String durataSessione = 0 + ":" + 0 + ":" + 0; 
        MA=getIntent();        
        PACKAGE_NAME = getApplicationContext().getPackageName();       
              	
        ListView listView = (ListView) findViewById(R.id.listView1);
        List<Dati> list = new LinkedList<Dati>();         
        
        Cursor crs=db.selectAllSessions();        
        if(crs.moveToFirst()){
        	do{
            String strData = crs.getString(crs.getColumnIndex("DataInizio"));
            String[] dataf=strData.split("/");
            int day=Integer.parseInt(dataf[0]);  
            int month=Integer.parseInt(dataf[1]);  
            int year=Integer.parseInt(dataf[2]);           

            String strTime = crs.getString(crs.getColumnIndex("OraInizio"));
            String[] oraf=strTime.split(":");
            int hour=Integer.parseInt(oraf[0]);  
            int minutes=Integer.parseInt(oraf[1]);  
            int seconds=Integer.parseInt(oraf[2]);
            int falls=db.CountCaduta(crs.getString(1));
            durataSessione = crs.getString(crs.getColumnIndex("Durata"));
            if(durataSessione.equals("XX:XX:XX")){
            	NSC = crs.getString(1);
            	stateNSC = Integer.parseInt(crs.getString(crs.getColumnIndex("Stato")));
            	if(stateNSC == 2){
            		tempoPausaMA = Long.parseLong(crs.getString(crs.getColumnIndex("TempoPausa")));            		
            	}
            	noSessioneInCorso = true;            	
            }            
            state = Integer.parseInt(crs.getString(crs.getColumnIndex("Stato")));
            int cl = crs.getInt(crs.getColumnIndex("Colore"));
            list.add(new Dati(crs.getString(1),day, month, year,hour, minutes, seconds, durataSessione, falls, cl, state));           
        	}while(crs.moveToNext());//fine while
        }
        else{
        	list.add(new Dati());
        }//fine IF
        crs.close();        
        Log.v("NOME SESSIONE IN CORSO MA",""+NSC+"");
        
        CustomAdapter adapter = new CustomAdapter(this, R.layout.list_items, list);       
        listView.setAdapter(adapter);        
        listView.setOnItemClickListener(new OnItemClickListener(){
    	public void onItemClick(AdapterView<?> adapter, View v, int position, long id){      		
    		
    		Log.v("MA-SELEZIONE-SESSIONE",""+(((Dati)adapter.getItemAtPosition(position)).getNomeSessione())+"");
    		Intent UI2;    		
    		Intent ST;
    		UI2 = new Intent(getApplicationContext(), Second.class);    		
    		ST = new Intent(getApplicationContext(),NewThird.class);
    		
    		if((((Dati)adapter.getItemAtPosition(position)).getNomeSessione()).equals(NSC)){    				   					
    					   			
    					ST.putExtra(PACKAGE_NAME+".nomeSessione",NSC);
    					ST.putExtra(PACKAGE_NAME+".statoSessione", stateNSC);
    					if(stateNSC == 2)
    						ST.putExtra(PACKAGE_NAME+".PausaTempo", tempoPausaMA);
    					startActivity(ST);    				    				
    			}else{
    				final ImageView im = (ImageView) v.findViewById(R.id.picture);
    				final BitmapDrawable bd = (BitmapDrawable) im.getDrawable();
    				final Bitmap b = bd.getBitmap();
    				Bundle extra = new Bundle();
    				extra.putParcelable("image", b);    				   		
    				UI2 = new Intent(getApplicationContext(), Second.class);    		
    				UI2.putExtra(PACKAGE_NAME+".nameSession", ((Dati)adapter.getItemAtPosition(position)).getNomeSessione()); 
    				UI2.putExtras(extra);
    				UI2.putExtra("color", ((Dati)adapter.getItemAtPosition(position)).getColor());    				
    				startActivity(UI2);    				
    			}
        	}
        });            
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

	@Override
    public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);		
			    
    	menu.add(0, R.id.nuovaSessione, 1, "Nuova Sessione");
    	menu.add(0, R.id.delete, 2, "Elimina Sessione");    	
    		
		return true;
		
    } 
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		switch (item.getItemId()) {
		case R.id.nuovaSessione:{
			if(!(noSessioneInCorso)){
				Intent NS= new Intent(getApplicationContext(),NewThird.class);
				String par="Sessione "/*+(db.MaxIDSessione()+1)+""*/ + findNumSess();
				NS.putExtra(PACKAGE_NAME+".nomeSessione", par);
				startActivity(NS);
			}
			else{				
				String text="IMPOSSIBILE INIZIARE NUOVA SESSIONE. SESSIONE "+NSC+" IN CORSO";				
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
	
	private int findNumSess(){
		int i;
		int count = db.CountSession();
		for(i=1; i<(count+1); i++){
			if(db.notSessSameName("Sessione "+i)){
				break;
			}
		}
		return i;
	}
	
	@Override
	public void onBackPressed() {		
	}	    
}