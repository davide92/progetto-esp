//CLASSE GESTIONE LISTA CADUTE

package it.unipd.dei.rilevatoredicadute;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.database.Cursor;
import android.util.Log;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;


public class Fourth extends ActionBarActivity {
	
	
    MyDBManager db;
    Intent intent;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fourth);
		db=new MyDBManager(this);
		intent = getIntent();
		Bundle extra = intent.getExtras();
		final Bitmap bm = (Bitmap)extra.getParcelable("image");
		ImageView image = (ImageView)findViewById(R.id.picture4);
		image.setImageBitmap(bm);
		int cl = intent.getIntExtra("color", 0);
		ColorFilter filter = new LightingColorFilter(Color.WHITE, cl);
		image.setColorFilter(filter);
		TextView nomeSessione = (TextView) findViewById(R.id.nomeSessione);
		TextView Data = (TextView) findViewById(R.id.data);
		TextView Ora = (TextView) findViewById(R.id.ora);
		TextView latitudine = (TextView) findViewById(R.id.latitude);
		TextView longitudine = (TextView) findViewById(R.id.longitude);
		String nameSession = intent.getStringExtra(MainActivity.PACKAGE_NAME+".nameSession");
		//String fallHour = intent.getStringExtra("hour");
		//Cursor crs=db.selectCaduta(intent.getStringExtra(MainActivity.PACKAGE_NAME+".nameSession"));
		//nomeSessione.setText(intent.getStringExtra(MainActivity.PACKAGE_NAME+".nameSession"));
		nomeSessione.setText(nameSession);
		Data.setText("    "+intent.getStringExtra(MainActivity.PACKAGE_NAME+".dataCaduta"));
		Ora.setText("    "+intent.getStringExtra(MainActivity.PACKAGE_NAME+".oraCaduta"));		
		latitudine.setText("  "+intent.getStringExtra(MainActivity.PACKAGE_NAME+".latitudine"));
		longitudine.setText("  "+intent.getStringExtra(MainActivity.PACKAGE_NAME+".longitudine"));
		//Cursor crs=db.selectCadutaWithHour(nameSession, fallHour);
		//DataOra.setText(crs.getString(0)+"  "+crs.getString(1));
		//latitudine.setText(crs.getString(2));
		//longitudine.setText(crs.getString(3));
		/*if(crs.moveToLast()){
			Data.setText(crs.getString(crs.getColumnIndex("DataCaduta"))+" "+fallHour);
			Ora.setText(crs.getString(crs.getColumnIndex("DataCaduta"))+" "+fallHour);
			latitudine.setText(crs.getString(crs.getColumnIndex("Latitudine")));
			longitudine.setText(crs.getString(crs.getColumnIndex("Longitudine")));
		}
		crs.close();*/
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
	protected void onPause() {
	    //Log.w("TAG", "App paused");
	    super.onPause();
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