//classe gestione LISTA SESSIONI

package it.unipd.dei.rilevatoredicadute;

import java.util.LinkedList;
import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
//import java.util.Date;
import android.util.Log;

public class Second extends ActionBarActivity {
	
	private int cl;
	MyDBManager db;
	String nS;
	int countFalls=0;
	Bitmap bm;
	TextView nomeSessione;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent=getIntent();		    
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
	    CustomAdapterFalls FALLadapter = new CustomAdapterFalls(this, R.layout.fall_item, FallList);
	    FallLV.setAdapter(FALLadapter);
        db=new MyDBManager(this);
        Cursor crs=db.selectSession(nS);
        nomeSessione = (TextView)findViewById(R.id.nomeSessione);
		TextView data = (TextView)findViewById(R.id.data);
		TextView ora = (TextView)findViewById(R.id.ora);
		TextView durataSessione = (TextView)findViewById(R.id.durataSessione);		
		//Dati d = getItem(position);
		//picture.setImage();
		if (crs.moveToFirst()){
			nomeSessione.setText(crs.getString(0));
			data.setText(crs.getString(1));
			ora.setText(crs.getString(2));
			durataSessione.setText(crs.getString(3));		   
		}        
        Log.v("nome sessione---->",""+nS+"");
        crs.close();		
		 
        Cursor c=db.selectCaduta(nS);                      
        if(c.moveToFirst()){
        	do{
            String strData = c.getString(c.getColumnIndex("DataCaduta"));
            String[] dataf=strData.split("/");
            int day=Integer.parseInt(dataf[0]);  
            int month=Integer.parseInt(dataf[1]);  
            int year=Integer.parseInt(dataf[2]);

            String strTime = c.getString(c.getColumnIndex("OraCaduta"));
            String[] oraf=strTime.split(":");
            int hour=Integer.parseInt(oraf[0]);  
            int minutes=Integer.parseInt(oraf[1]);  
            int seconds=Integer.parseInt(oraf[2]);
            
            FallList.add(new DatiCadute(day, month, year,hour, minutes, seconds, Double.parseDouble(c.getString(2)), Double.parseDouble(c.getString(3)), nS));           
        	}while(c.moveToNext());//fine while
        }
        else{
        	//list.add(new Dati());
        }//fine IF
        //crs.close();        
        c.close();
        
	     FallLV.setOnItemClickListener(new OnItemClickListener(){
	     public void onItemClick(AdapterView<?> FALLadapter, View v, int position, long id){

	    	 Intent UI4 = new Intent(getApplicationContext(), Fourth.class);
	    	 UI4.putExtra("nameSession", ((DatiCadute)FALLadapter.getItemAtPosition(position)).getSessione());
	    	 UI4.putExtra("hour", ((DatiCadute)FALLadapter.getItemAtPosition(position)).getHour());
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
	    //Log.w("TAG", "App stopped");
	    super.onStop();
	    if (db != null) 
	    {
	        db.close();
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);
    	MenuItem meIt1 = menu.add(0, R.id.nuovaSessione, 1, "Nuova Sessione"); 
    	MenuItem meIt2 = menu.add(0, R.id.delete, 2, "Elimina caduta");
    	MenuItem meIt4 = menu.add(0, R.id.preferenze, 4, "Preferenze");
    	meIt1.setIntent(new Intent(this, Third.class)); 
    	Intent del = new Intent(this, DeleteFall.class);
    	del.putExtra("session", nomeSessione.getText());
    	del.putExtra("color", cl);
		Bundle extra = new Bundle();
		extra.putParcelable("image", bm);
    	del.putExtras(extra);
    	meIt2.setIntent(del);
    	meIt4.setIntent(new Intent(this, Fifth.class));
    	return true;
    } 

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


}