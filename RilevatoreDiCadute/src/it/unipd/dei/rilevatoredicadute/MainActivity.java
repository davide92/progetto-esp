package it.unipd.dei.rilevatoredicadute;

import java.util.LinkedList;
import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.database.Cursor;
import android.widget.Chronometer;

public class MainActivity extends ActionBarActivity {	
	
	//IL VALORE VAIRAIBILE i:
	//i=0, una sessione si puo' iniziare
	//i=1 una sessione e' in esecuzione
	//i=2 una sessione e' in pausa	
	//per la main activity, state prende il posto di i
	
	public static String PACKAGE_NAME; 	
	MyDBManager db;	          
    Intent MA;
    String name;
    long tempo;       
    int state; 
    long pt=0;
    long ddd;
    
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
        
        	name=MA.getStringExtra(MainActivity.PACKAGE_NAME+".nameSession");
        	Log.v("TAG__MA__NAME",""+name+"");
        	tempo=MA.getLongExtra(MainActivity.PACKAGE_NAME+".StopTime", 0);        	
        	Log.v("TAG__MA__TEMPO1","sec>"+(tempo/1000 % 60)+"<minuti>"+((tempo / (1000*60)) % 60)+"<ore"+((tempo / (1000*60*60)) % 24)+"");
        	state=MA.getIntExtra(MainActivity.PACKAGE_NAME+".state", 0);
        	Log.v("stato MA", ""+state+"");
              	
        ListView listView = (ListView) findViewById(R.id.listView1);
        List<Dati> list = new LinkedList<Dati>();
        
        if(name!=null){
        	if(state!=0){        		
        		if(state==1){ 
        			Log.v("aaaaaaa","bbbbbbb");        			
        			pt=SystemClock.elapsedRealtime();
        		}        		
        	}
        }        
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
            int falls=Integer.parseInt(crs.getString(crs.getColumnIndex("NCadute")));
            durataSessione = crs.getString(crs.getColumnIndex("Durata"));              
            list.add(new Dati(crs.getString(1),day, month, year,hour, minutes, seconds, durataSessione, falls));
        	}while(crs.moveToNext());//fine while
        }
        else{
        	list.add(new Dati());
        }//fine IF
        crs.close();
        
        CustomAdapter adapter = new CustomAdapter(this, R.layout.list_items, list);
        listView.setAdapter(adapter);        
        listView.setOnItemClickListener(new OnItemClickListener(){
    	public void onItemClick(AdapterView<?> adapter, View v, int position, long id){      		
    		
    		Intent UI2;    		
    		UI2 = new Intent(getApplicationContext(), Second.class);    		
    		UI2.putExtra(PACKAGE_NAME+".nameSession", ((Dati)adapter.getItemAtPosition(position)).getNomeSessione());     		   	
    		Log.v("Nome Sessione MA-->",((Dati)adapter.getItemAtPosition(position)).getNomeSessione());
        	startActivity(UI2);        	
        	}
        });            
	}
	
	/*@Override
	protected void onRestart(){
		super.onRestart();
		Log.v("MAINACTIVITY","RITORNO ACTIVITY");
		Log.v("NUMERO SESSIONI",""+i+"");
		Long cr = MA.getLongExtra(MainActivity.PACKAGE_NAME+".stopTime",0);
		 int seconds = (int)(cr/1000 % 60);
	     int minutes = (int) ((cr / (1000*60)) % 60);
		 int hours   = (int) ((cr / (1000*60*60)) % 24);
		 Log.v("DURATA PASSATA",""+hours+""+minutes+""+seconds);
	}*/

	
	/*@Override
	protected void onStop() {
	    Log.w("TAG", "App stopped");
	    super.onStop();
	}
	
	@Override
	protected void onPause() {
	    Log.w("TAG", "App paused");
	    super.onPause();
	}*/
		
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
			    
    	/*MenuItem meIt1 = */menu.add(0, R.id.nuovaSessione, 1, "Nuova Sessione");
    	/*MenuItem meIt2 = */menu.add(0, R.id.delete, 2, "Elimina");
    	/*MenuItem meIt3 = */menu.add(0, R.id.sessioneInCorso, 3, "Sessione In Corso" );
    	/*MenuItem meIt4 = */menu.add(0, R.id.preferenze, 4, "Preferenze");
    	/*if(i==0){
    		Intent intent=getIntent();
    		boolean play=intent.getBooleanExtra(PACKAGE_NAME+".playable",true);
    		if(play==true)
    		meIt1.setIntent(new Intent(this, Third.class));
    	}
    	else{
    		Log.v("controllo sessione","Sessione in corso, non puoi crearne una");
    	}
    	meIt2.setIntent(new Intent(this, Delete.class));
    	meIt3.setIntent(new Intent(this, Third.class));
    	meIt4.setIntent(new Intent(this, Fifth.class));    	
    	*/		
		return true;
		
    } 
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		//int id = item.getItemId();
		//if (id == R.id.action_settings) {
		//	return true;
		//}
		switch (item.getItemId()) {
		case R.id.nuovaSessione:{
			if(state==0)
				startActivity(new Intent(this, Third.class));
			else{				
				String text="IMPOSSIBILE INIZIARE NUOVA SESSIONE. SESSIONE "+name+" IN CORSO";				
				Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
				toast.show();
			}
			break;
		}
		case R.id.delete:
			startActivity(new Intent(this, Delete.class));
			break;
 
		case R.id.sessioneInCorso:{	
			if(state!=0){
				Intent SC;			   		
				SC = new Intent(getApplicationContext(), SessioneCorrente.class);
				if(state==1)
					ddd=SystemClock.elapsedRealtime()-pt+tempo;
				else
					ddd=tempo;
				Log.v("TAG__MA__ddd","sec>"+(ddd/1000 % 60)+"<minuti>"+((ddd / (1000*60)) % 60)+"<ore>"+((ddd / (1000*60*60)) % 24)+"");
				Log.v("TAG__MA__TEMPO2","sec>"+(tempo/1000 % 60)+"<minuti>"+((tempo / (1000*60)) % 60)+"<ore>"+((tempo / (1000*60*60)) % 24)+"");
				
				SC.putExtra(PACKAGE_NAME+".TempoPausa",ddd);
				SC.putExtra(PACKAGE_NAME+".nomeSessione",name);
				SC.putExtra(PACKAGE_NAME+".statoSessione", state);				
				startActivity(SC);
			}else{				
				String text="NESSUNA SESSIONE IN CORSO";				
				Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
				toast.show();
			 }
    		break;
		}
			
		
		case R.id.preferenze:			
			break;
		}	
		return true;
		
		//return super.onOptionsItemSelected(item);
	}
	
	/*@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	        return false;
	    }
	    return super.onKeyDown(keyCode, event);
	}*/
	
	@Override
	public void onBackPressed() {
	}
	    
}