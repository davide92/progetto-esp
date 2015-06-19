//classe gestione LISTA SESSIONI

package it.unipd.dei.rilevatoredicadute;

import java.util.LinkedList;
import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
//import java.util.Date;
import android.util.Log;

public class Second extends ActionBarActivity {

	MyDBManager db;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent=getIntent();		    
		String nS=intent.getStringExtra(MainActivity.PACKAGE_NAME+".nameSession");	
		 setContentView(R.layout.activity_second);
		//ListView listView = (ListView) findViewById(R.id.listViewCadute);		
        //List<DatiCadute> listFalls = new LinkedList<DatiCadute>();	
        db=new MyDBManager(this);
        Cursor crs=db.selectSession(nS);
        Cursor c=db.selectCaduta();
        int result = c.getCount();
        TextView nomeSessione = (TextView)findViewById(R.id.nomeSessione);
		TextView data = (TextView)findViewById(R.id.data);
		TextView ora = (TextView)findViewById(R.id.ora);
		TextView durataSessione = (TextView)findViewById(R.id.durataSessione);
		TextView NCadute = (TextView)findViewById(R.id.numeroCadute);
		//Dati d = getItem(position);
		//picture.setImage();
		if (crs.moveToFirst()){
		nomeSessione.setText(crs.getString(0));
		data.setText(crs.getString(1));
		ora.setText(crs.getString(2));
		durataSessione.setText(crs.getString(3));
		//NCadute.setText((crs.getString(3)));
		}
        Log.v("numero di cadute","" +result+ "");
        Log.v("nome sessione---->",nS);
        c.close();
        crs.close();
		
		/* CustomAdapterFalls Fallsadapter = new CustomAdapterFalls(this, R.layout.fall_item, listFalls);
	        listView.setAdapter(Fallsadapter);
	        listView.setOnItemClickListener(new OnItemClickListener(){
	    	public void onItemClick(AdapterView<?> adapter, View v, int position, long id){

	    		//final String titoloriga = (String) adapter.getItemAtPosition(position);  
	            //Log.d("List", "Ho cliccato sull'elemento con titolo" + titoloriga);  
	        	//Dati value = (Dati)adapter.getItemAtPosition(position);   		
	        	Intent UI2 = new Intent(getApplicationContext(), Second.class);
	        	startActivity(UI2);
	        	//Log.v("List","ho premuto l'elemento" +id);
	        	}
	        }); */     
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