package it.unipd.dei.rilevatoredicadute;

import java.util.List;
import android.content.Context;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class CustomAdapterFalls extends ArrayAdapter<DatiCadute>{
	
	public CustomAdapterFalls(Context context, int textViewResourceId, List <DatiCadute> objects){
		super(context, textViewResourceId, objects);
	}

	public View getView( int position, View convertView, ViewGroup parent){
		LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.fall_item, null);
		TextView latitudine = (TextView)convertView.findViewById(R.id.latitudeTx);
		TextView dataEora = (TextView)convertView.findViewById(R.id.dataEora);
		TextView longitudine = (TextView)convertView.findViewById(R.id.longitudeTx);
		DatiCadute dc = getItem(position);
		//picture.setImage();
		latitudine.setText(dc.getLatitudine());
		longitudine.setText(dc.getLongitudine());
		dataEora.setText(dc.getData() + " " + dc.getHour());		
		return convertView;
	}
	
	
	
	/*public class CursorAdapter extends ArrayAdapter<Dati>{
	 * 		public CursorAdapter(Context context, int textViewResourceId, List <Dati> Objects){
	 * 			super(context)
	 * 		}
	 * }
	 * 
	 */
}