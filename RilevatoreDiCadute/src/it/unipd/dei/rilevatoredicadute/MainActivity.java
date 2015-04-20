package it.unipd.dei.rilevatoredicadute;

import java.util.LinkedList;
import java.util.List;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;



public class MainActivity extends ActionBarActivity {
		
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ListView listView = (ListView) findViewById(R.id.listView1);
        List<Dati> list = new LinkedList<Dati>();
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
	
    public boolean onCreateOptionsMenu(Menu menu){
    	super.onCreateOptionsMenu(menu);
    	MenuItem meIt1 = menu.add(0, R.id.start, 1, "Start");
    	MenuItem meIt2 = menu.add(0, R.id.delete, 2, "Cancella");
    	MenuItem meIt3 = menu.add(0, R.id.rinomina, 3, "Rinomina");
    	MenuItem meIt4 = menu.add(0, R.id.preferenze, 4, "Preferenze");
    	return true;
    } 
    
}