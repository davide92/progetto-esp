package it.unipd.dei.rilevatoredicadute;

import java.util.List;
import android.content.Context;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class CustomAdapter extends ArrayAdapter<Dati>{
	
	public CustomAdapter(Context context, int textViewResourceId, List <Dati> objects){
		super(context, textViewResourceId, objects);
	}

	public View getView( int position, View convertView, ViewGroup parent){
		LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.list_items, null);
		TextView nomeSessione = (TextView)convertView.findViewById(R.id.nomeSessione);
		TextView dataEora = (TextView)convertView.findViewById(R.id.dataEora);
		TextView durataSessione = (TextView)convertView.findViewById(R.id.durataSessione);		
		Dati d = getItem(position);
		//picture.setImage();
		nomeSessione.setText(d.getNomeSessione());
		dataEora.setText(d.getData() + " " + d.getHour());
		durataSessione.setText(d.getDurataSessione());
		return convertView;
	}
}