//CLASSE GESTIONE LISTA CADUTE

package it.unipd.dei.rilevatoredicadute;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.database.Cursor;

public class Fourth extends ActionBarActivity {
	
	
    MyDBManager db;
    Intent intent;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fourth);
		db=new MyDBManager(this);
		intent = getIntent();
		TextView nomeSessione = (TextView) findViewById(R.id.nomeSessione);
		TextView DataOra = (TextView) findViewById(R.id.dataEora);
		TextView latitudine = (TextView) findViewById(R.id.latitude);
		TextView longitudine = (TextView) findViewById(R.id.longitude);
		Cursor crs=db.selectCaduta(intent.getStringExtra(MainActivity.PACKAGE_NAME+".nameSession"));
		nomeSessione.setText(intent.getStringExtra(MainActivity.PACKAGE_NAME+".nameSession"));
		DataOra.setText(crs.getString(0)+"  "+crs.getString(1));
		latitudine.setText(crs.getString(2));
		longitudine.setText(crs.getString(3));
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

	/*@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}*/
}