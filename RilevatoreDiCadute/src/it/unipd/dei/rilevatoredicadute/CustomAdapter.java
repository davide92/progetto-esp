package it.unipd.dei.rilevatoredicadute;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<Dati>{
	
	private List<Dati> list;
	public CustomAdapter(Context context, int textViewResourceId, List <Dati> objects){
		super(context, textViewResourceId, objects);
		list=objects;
	}

	public View getView( int position, View convertView, ViewGroup parent){
		LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Dati d = getItem(position);
		if(d.getState() == 0){
			convertView = inflater.inflate(R.layout.list_items, null);
			ImageView thumbnail = (ImageView)convertView.findViewById(R.id.picture);
			int cl = list.get(position).getColor();
			ColorFilter filter = new LightingColorFilter(Color.WHITE, cl);
			thumbnail.setColorFilter(filter);
			TextView nomeSessione = (TextView)convertView.findViewById(R.id.nomeSessione);
			TextView dataEora = (TextView)convertView.findViewById(R.id.dataEora);
			TextView durataSessione = (TextView)convertView.findViewById(R.id.durataSessione);
			TextView numeroCadute = (TextView)convertView.findViewById(R.id.numeroCadute);
			nomeSessione.setText(d.getNomeSessione());
			dataEora.setText(d.getData() + " " + d.getHour());
			durataSessione.setText(d.getDurataSessione());
			numeroCadute.setText(Integer.toString(d.getFalls()));
		}else{
			convertView = inflater.inflate(R.layout.list_items_2, null);
			ImageView thumbnail = (ImageView)convertView.findViewById(R.id.picture);
			int cl = list.get(position).getColor();
			ColorFilter filter = new LightingColorFilter(Color.WHITE, cl);
			thumbnail.setColorFilter(filter);
			TextView nomeSessione = (TextView)convertView.findViewById(R.id.nomeSessione);
			TextView dataEora = (TextView)convertView.findViewById(R.id.dataEora);
			TextView durataSessione = (TextView)convertView.findViewById(R.id.durataSessione);
			TextView numeroCadute = (TextView)convertView.findViewById(R.id.numeroCadute);
			nomeSessione.setText(d.getNomeSessione());
			dataEora.setText(d.getData() + " " + d.getHour());
			durataSessione.setText(d.getDurataSessione());
			numeroCadute.setText(Integer.toString(d.getFalls()));
			nomeSessione.setTextColor(Color.BLUE);
			dataEora.setTextColor(Color.BLUE);
			durataSessione.setTextColor(Color.BLUE);
			numeroCadute.setTextColor(Color.BLUE);
		}
		/*if(d.getState() == 1 || d.getState() == 2){
			convertView.setBackgroundColor(0xbbdefb);
		}*/
		return convertView;
	}	
}