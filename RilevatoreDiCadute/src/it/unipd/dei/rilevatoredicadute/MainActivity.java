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
import android.database.Cursor;




public class MainActivity extends ActionBarActivity {			
	
	MyDBManager db;
	//Intent intent=getIntent();
    String pkg="it.unipd.dei.rilevatoredicadute";    
   // boolean p=intent.getBooleanExtra(pkg+".myPlay",true);	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new MyDBManager(this);         
        String durataSessione = 0 + ":" + 0 + ":" + 0;
        setContentView(R.layout.activity_main);           
        ListView listView = (ListView) findViewById(R.id.listView1);
        List<Dati> list = new LinkedList<Dati>();
        
        Cursor crs=db.selectSessione();         
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
        }
        crs.close();
        
        CustomAdapter adapter = new CustomAdapter(this, R.layout.list_items, list);
        listView.setAdapter(adapter);        
        listView.setOnItemClickListener(new OnItemClickListener(){
    	public void onItemClick(AdapterView<?> adapter, View v, int position, long id){      		
    		
    		Intent UI2;    		
    		UI2 = new Intent(getApplicationContext(), Second.class);    		
    		UI2.putExtra(pkg+".nameSession", ((Dati)adapter.getItemAtPosition(position)).getNomeSessione());     		   	
    		Log.v("grazie",((Dati)adapter.getItemAtPosition(position)).getNomeSessione());
        	startActivity(UI2);        	
        	}
        });          
            
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
			    
    	MenuItem meIt1 = menu.add(0, R.id.nuovaSessione, 1, "Nuova Sessione");
    	MenuItem meIt2 = menu.add(0, R.id.delete, 2, "Elimina");    	
    	MenuItem meIt4 = menu.add(0, R.id.preferenze, 4, "Preferenze");
    	meIt1.setIntent(new Intent(this, Third.class));    	
    	meIt2.setIntent(new Intent(this, Delete.class));
    	meIt4.setIntent(new Intent(this, Fifth.class));
    	
    	return true;
    } 
	    
}