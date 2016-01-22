//classe gestione LISTA SESSIONI

package it.unipd.dei.rilevatoredicadute;

import java.util.LinkedList;
import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.util.Log;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;

public class Second extends ActionBarActivity {
	
	private int cl; //colore immagine
	MyDBManager db;
	String nS; //nome sessione
	int contaCadute = 0;
	Bitmap bm; //immagine
	TextView nomeSessione;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();		    
		nS=intent.getStringExtra("nameSession");	
		Bundle extra = intent.getExtras();
		bm = (Bitmap)extra.getParcelable("image");
	    setContentView(R.layout.activity_second);
	    ImageView image = (ImageView)findViewById(R.id.picture2);
	    cl = intent.getIntExtra("color", 0);
	    image.setImageBitmap(bm);
	    ColorFilter filter = new LightingColorFilter(Color.WHITE, cl);
	    image.setColorFilter(filter);
	    ListView FallLV = (ListView) findViewById(R.id.listViewCadute);
	    List<DatiCadute> FallList = new LinkedList<DatiCadute>();
	   
        db = new MyDBManager(this);
        //recupero dati della sessione selezionata nella main activity
        Cursor crs = db.selezSessione(nS);
        nomeSessione = (TextView)findViewById(R.id.nomeSessione);
        TextView data = (TextView)findViewById(R.id.data);
        TextView ora = (TextView)findViewById(R.id.ora);
        TextView durataSessione = (TextView)findViewById(R.id.durataSessione);		        
        if (crs.moveToFirst()){
        	nomeSessione.setText(crs.getString(0));
        	data.setText(crs.getString(1));
        	ora.setText(crs.getString(2));
        	durataSessione.setText(crs.getString(3));		   
        }        
        Log.v("nome sessione---->",""+nS+"");
        crs.close();		
        
        //recupero dati di tutte le cadute della sessione selezionata nella main activity
        Cursor c = db.selezCaduta(nS);                      
        if(c.moveToFirst()){
        	do{
		        String strData = c.getString(c.getColumnIndex("DataCaduta"));
		        String[] dataCaduta=strData.split("/");
		        int giorno=Integer.parseInt(dataCaduta[0]);  
		        int mese=Integer.parseInt(dataCaduta[1]);  
		        int anno=Integer.parseInt(dataCaduta[2]);
		
		        String strTime = c.getString(c.getColumnIndex("OraCaduta"));
		        String[] oraCaduta=strTime.split(":");
		        int ore = Integer.parseInt(oraCaduta[0]);  
		        int minuti = Integer.parseInt(oraCaduta[1]);  
		        int secondi = Integer.parseInt(oraCaduta[2]);           	
		        
		        FallList.add(new DatiCadute(giorno, mese, anno,ore, minuti, secondi, (c.getString(2)), (c.getString(3)),nS));           
        	}while(c.moveToNext());//fine while
        }              
        c.close();        
       
        CustomAdapterFalls FALLadapter = new CustomAdapterFalls(this, R.layout.fall_item, FallList);
	    FallLV.setAdapter(FALLadapter);        
        FallLV.setOnItemClickListener(new OnItemClickListener(){
	    public void onItemClick(AdapterView<?> FALLadapter, View v, int position, long id){	    	 	
	    	 	
	    	   	Intent UI4 = new Intent(getApplicationContext(), Fourth.class);
	    	   	UI4.putExtra(MainActivity.PACKAGE_NAME+".nameSession", nS);
	    	   	UI4.putExtra(MainActivity.PACKAGE_NAME+".dataCaduta", ((DatiCadute) FALLadapter.getItemAtPosition(position)).getData());
	    	   	UI4.putExtra(MainActivity.PACKAGE_NAME+".oraCaduta", ((DatiCadute) FALLadapter.getItemAtPosition(position)).getOra());
	    	   	UI4.putExtra(MainActivity.PACKAGE_NAME+".longitudine", ((DatiCadute) FALLadapter.getItemAtPosition(position)).getLongitudine());
	    	   	UI4.putExtra(MainActivity.PACKAGE_NAME+".latitudine", ((DatiCadute) FALLadapter.getItemAtPosition(position)).getLatitudine());
	    	   	Bundle extra = new Bundle();
	    	   	extra.putParcelable("image", bm);
	    	    UI4.putExtras(extra);
	    	    UI4.putExtra("color", cl);
	        	startActivity(UI4);
	        	
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
	protected void onStop() {	    
	    super.onStop();
	    if (db != null) 
	    {
	        db.close();
	    }
	}
	//creazione degli elementi del menù
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);
		menu.add(0, R.id.delete, 2, "Elimina caduta");
    	return true;
    } 
	//azioni di ogni elemento del menù
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id == R.id.delete){
			Intent del = new Intent(this, DeleteFall.class);
	    	del.putExtra("session", nomeSessione.getText());
	     	del.putExtra("color", cl);
	    	Bundle extra = new Bundle();
	    	extra.putParcelable("image", bm);
	    	del.putExtras(extra);
	    	startActivity(del);
		}
		return true;
	}


}