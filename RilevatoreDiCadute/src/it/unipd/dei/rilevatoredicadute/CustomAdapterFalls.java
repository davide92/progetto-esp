package it.unipd.dei.rilevatoredicadute;

import java.util.List;
import android.content.Context;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class CustomAdapterFalls extends ArrayAdapter<DatiCadute>{
	
	private List<DatiCadute> listC;
	public CustomAdapterFalls(Context context, int textViewResourceId, List <DatiCadute> objects){
		super(context, textViewResourceId, objects);
		listC = objects;
	}

	public View getView( int position, View convertView, ViewGroup parent){
		LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.fall_item, null);
		//TextView latitudine = (TextView)convertView.findViewById(R.id.latitudeTx);
		TextView latVal = (TextView)convertView.findViewById(R.id.latitude);
		TextView data = (TextView)convertView.findViewById(R.id.data);
		TextView ora = (TextView)convertView.findViewById(R.id.ora);
		//TextView longitudine = (TextView)convertView.findViewById(R.id.longitudeTx);
		TextView longVal = (TextView)convertView.findViewById(R.id.longitude);
		DatiCadute dc = getItem(position);		
		latVal.setText((dc.getLatitudine()));
		longVal.setText((dc.getLongitudine()));
		data.setText(dc.getData()+"  ");
		ora.setText(" "+dc.getHour());
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