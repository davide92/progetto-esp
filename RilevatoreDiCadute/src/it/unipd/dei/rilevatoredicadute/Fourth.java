//CLASSE GESTIONE LISTA CADUTE: DEFINISCE LE INFORMAZIONI RELATIVE ALLA CADUTA

package it.unipd.dei.rilevatoredicadute;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;

public class Fourth extends ActionBarActivity {	
	
    MyDBManager db;
    Intent intent;
    String nomeSess;
    int cl; //colore
    Bitmap bm; //immagine
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fourth);
		db = new MyDBManager(this);
		intent = getIntent();
		Bundle extra = intent.getExtras();
		bm = (Bitmap)extra.getParcelable("image");
		ImageView image = (ImageView)findViewById(R.id.picture4);
		image.setImageBitmap(bm);
		cl = intent.getIntExtra("color", 0);
		ColorFilter filter = new LightingColorFilter(Color.WHITE, cl);
		image.setColorFilter(filter);
		TextView nomeSessione = (TextView) findViewById(R.id.nomeSessione);
		TextView Data = (TextView) findViewById(R.id.data);
		TextView Ora = (TextView) findViewById(R.id.ora);
		TextView latitudine = (TextView) findViewById(R.id.latitude);
		TextView longitudine = (TextView) findViewById(R.id.longitude);
		nomeSess = intent.getStringExtra(MainActivity.PACKAGE_NAME+".nameSession");		
		nomeSessione.setText(nomeSess);
		Data.setText("    "+intent.getStringExtra(MainActivity.PACKAGE_NAME+".dataCaduta"));
		Ora.setText("    "+intent.getStringExtra(MainActivity.PACKAGE_NAME+".oraCaduta"));		
		latitudine.setText("  "+intent.getStringExtra(MainActivity.PACKAGE_NAME+".latitudine"));
		longitudine.setText("  "+intent.getStringExtra(MainActivity.PACKAGE_NAME+".longitudine"));		
	}	

 	@Override
 	protected void onStop() { 		
	    super.onStop();
	    if (db != null) 
	    {
	        db.close();
	    }
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
	
	@Override
	public void onBackPressed() {//ritorno alla activity 2 alla pressione del tasto di ritorno
		Intent UI2 = new Intent(this, Second.class);
		Bundle extra = new Bundle();
		extra.putParcelable("image", bm);
		UI2.putExtra("nameSession", nomeSess);
		UI2.putExtras(extra);
		UI2.putExtra("color", cl);
		startActivity(UI2);
		super.onBackPressed();
	}
}