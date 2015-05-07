package it.unipd.dei.rilevatoredicadute;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

public class Second extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second);
		ListView listView = (ListView) findViewById(R.id.listViewCadute);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);
    	MenuItem meIt1 = menu.add(0, R.id.nuovaSessione, 1, "Nuova Sessione");
    	MenuItem meIt2 = menu.add(0, R.id.delete, 2, "Elimina");
    	MenuItem meIt3 = menu.add(0, R.id.rinomina, 3, "Rinomina");
    	MenuItem meIt4 = menu.add(0, R.id.preferenze, 4, "Preferenze");
    	meIt1.setIntent(new Intent(this, Third.class));
    	meIt3.setIntent(new Intent(this, Sixth.class));
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