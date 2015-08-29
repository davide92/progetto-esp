//CLASSE GESTIONE LISTA CADUTE

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
		nomeSessione.setText(nameSession);
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
	
}