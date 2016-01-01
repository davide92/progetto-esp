package it.unipd.dei.rilevatoredicadute;

import java.util.LinkedList;
import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class DeleteFall extends ActionBarActivity {

	private int cl; //colore
	MyDBManager db; 
	String nS; //nome sessione
	int contaCadute = 0;
	TextView nomeSessione;
	Bitmap bm; //immagine
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent=getIntent();		    
		nS = intent.getStringExtra("session");
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
	    //visualizzazione delle cadute relative alla sessione selezionata
        db = new MyDBManager(this);
        Cursor crs = db.selezSessione(nS);
        Cursor c = db.selezCaduta(nS);                      
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
            
            FallList.add(new DatiCadute(day, month, year,hour, minutes, seconds, c.getString(2), c.getString(3), nS));           
        	}while(c.moveToNext());//fine while
        }
        else{
        	//list.add(new Dati());
        }//fine IF
        //crs.close();        
        c.close();
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
        //c.close();
        crs.close();		
		 
	     FallLV.setOnItemClickListener(new OnItemClickListener(){
	     public void onItemClick(AdapterView<?> FALLadapter, View v, int position, long id){ 
	    	 final AdapterView<?> fa = FALLadapter;
	    	 final int pos = position;
	    	 AlertDialog.Builder alert = new AlertDialog.Builder(DeleteFall.this);//mesaggio di allerta
	    	 alert.setTitle("Elimina");
	    	 alert.setMessage("Eliminare l'elemento?");
	    	 alert.setPositiveButton("Si", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {//eliminazione della caduta
					db.cancCaduta(((DatiCadute)fa.getItemAtPosition(pos)).getSessione(), ((DatiCadute)fa.getItemAtPosition(pos)).getOra());
					db.close();
					Intent UI2 = new Intent(getApplicationContext(), Second.class);
					UI2.putExtra("nameSession", nomeSessione.getText());
			    	UI2.putExtra("color", cl);
		    		Bundle extra = new Bundle();
		    		extra.putParcelable("image", bm);
		    		UI2.putExtras(extra);
		         	startActivity(UI2);
				}
			});
	    	 alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					return;
					
				}
			});
	    	AlertDialog dialog = alert.create(); //creazione del dialogo di allerta
	    	dialog.show();            	
    	}
    	
        });
	    //crs.close();
            
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
	public void onBackPressed() {//ritorno alla activity precedente(seconda)
		Intent UI2 = new Intent(this, Second.class);
		Bundle extra = new Bundle();
		extra.putParcelable("image", bm);
		UI2.putExtra("nameSession", nS);
		UI2.putExtras(extra);
		UI2.putExtra("color", cl);
		startActivity(UI2);
		super.onBackPressed();
	}
}