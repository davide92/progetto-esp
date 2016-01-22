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
		TextView valoreLatitudine = (TextView)convertView.findViewById(R.id.latitude);
		TextView data = (TextView)convertView.findViewById(R.id.data);
		TextView ora = (TextView)convertView.findViewById(R.id.ora);		
		TextView valoreLongitudine = (TextView)convertView.findViewById(R.id.longitude);
		DatiCadute dc = getItem(position);		
		valoreLatitudine.setText((dc.getLatitudine()));
		valoreLongitudine.setText((dc.getLongitudine()));
		data.setText(dc.getData()+"  ");
		ora.setText(" "+dc.getOra());
		return convertView;
	}	
}