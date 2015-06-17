package it.unipd.dei.rilevatoredicadute;

import java.util.LinkedList;
import java.util.List;

import android.support.v7.app.ActionBarActivity;
//import android.content.Intent;
import android.os.Bundle;
//import android.view.Menu;
//import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.content.Intent;
import android.database.Cursor;

//import android.util.Log;





public class Delete extends ActionBarActivity {	
	
	MyDBManager db= new MyDBManager(this);	
	Cursor crs=null;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);           
        final ListView listView = (ListView) findViewById(R.id.listView1);
        List<Dati> list = new LinkedList<Dati>();
        
        crs=db.selectSessione();         
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
            String durataSessione = 0 + ":" + 0 + ":" + 0;            
            list.add(new Dati(crs.getString(1),day, month, year,hour, minutes, seconds, durataSessione, falls));
            }while(crs.moveToNext());//fine while
        }
        else{
        	list.add(new Dati());
        }
        //crs.close();
        
        CustomAdapter adapter = new CustomAdapter(this, R.layout.list_items, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener(){
    	public void onItemClick(AdapterView<?> adapter, View v, int position, long id){    		       	
    		//final String titoloriga = (String) adapter.getItemAtPosition(position);  
            //Log.d("List", "Ho cliccato sull'elemento con titolo" + titoloriga);     		
    		//adapter.remove(adapter.getItem(position)); 
    		//db.deleteSessione();
    		//TODO sistemare
    		db.deleteSessione(((Dati)adapter.getItemAtPosition(position)).getNomeSessione());
    		Intent UI2 = new Intent(getApplicationContext(), MainActivity.class);
         	startActivity(UI2);       
         	
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