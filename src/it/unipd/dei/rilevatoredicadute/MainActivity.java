package it.unipd.dei.rilevatoredicadute;

import java.util.LinkedList;
import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.database.Cursor;




public class MainActivity extends ActionBarActivity {		
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);           
        ListView listView = (ListView) findViewById(R.id.listView1);
        List<Dati> list = new LinkedList<Dati>();
       // MyDatabase db= new MyDatabase(this);
        //Cursor crs=db.fetchSessioni();       
        list.add(new Dati());
        CustomAdapter adapter = new CustomAdapter(this, R.layout.list_items, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener(){
    	public void onItemClick(AdapterView<?> adapter, View v, int position, long id){
        	//Dati value = (Dati)adapter.getItemAtPosition(position);
        	Intent UI2 = new Intent(getApplicationContext(), Second.class);
        	startActivity(UI2);
        	}
        });
            
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);
    	MenuItem meIt1 = menu.add(0, R.id.nuovaSessione, 1, "Nuova Sessione");
    	MenuItem meIt2 = menu.add(0, R.id.delete, 2, "Elimina");
    	MenuItem meIt3 = menu.add(0, R.id.rinomina, 3, "Rinomina");
    	MenuItem meIt4 = menu.add(0, R.id.preferenze, 4, "Preferenze");
    	meIt1.setIntent(new Intent(this, Third.class));
    	meIt4.setIntent(new Intent(this, Fifth.class));
    	meIt3.setIntent(new Intent(this, Sixth.class));
    	return true;
    } 
    
}