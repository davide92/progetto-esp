package it.unipd.dei.rilevatoredicadute;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
		if(d.getStato() == 0){//caduta stoppata ha scritte nere
			convertView = inflater.inflate(R.layout.list_items, null);
			ImageView thumbnail = (ImageView)convertView.findViewById(R.id.picture);
			int cl = list.get(position).getColore();
			ColorFilter filter = new LightingColorFilter(Color.WHITE, cl);
			thumbnail.setColorFilter(filter);
			TextView nomeSessione = (TextView)convertView.findViewById(R.id.nomeSessione);
			TextView dataEora = (TextView)convertView.findViewById(R.id.dataEora);
			TextView durataSessione = (TextView)convertView.findViewById(R.id.durataSessione);
			TextView numeroCadute = (TextView)convertView.findViewById(R.id.numeroCadute);
			nomeSessione.setText(d.getNomeSessione());
			dataEora.setText(d.getData() + " " + d.getOra());
			durataSessione.setText(d.getDurataSessione());
			numeroCadute.setText(Integer.toString(d.getCadute()));
		}else{//caduta in esecuzione o in pausa, colore delle scritte blu 
			convertView = inflater.inflate(R.layout.list_items_2, null);
			ImageView thumbnail = (ImageView)convertView.findViewById(R.id.picture);
			int cl = list.get(position).getColore();
			ColorFilter filter = new LightingColorFilter(Color.WHITE, cl);
			thumbnail.setColorFilter(filter);
			TextView nomeSessione = (TextView)convertView.findViewById(R.id.nomeSessione);
			TextView dataEora = (TextView)convertView.findViewById(R.id.dataEora);
			TextView durataSessione = (TextView)convertView.findViewById(R.id.durataSessione);
			TextView numeroCadute = (TextView)convertView.findViewById(R.id.numeroCadute);
			nomeSessione.setText(d.getNomeSessione());
			dataEora.setText(d.getData() + " " + d.getOra());
			durataSessione.setText(d.getDurataSessione());
			numeroCadute.setText(Integer.toString(d.getCadute()));
			nomeSessione.setTextColor(Color.BLUE);
			dataEora.setTextColor(Color.BLUE);
			durataSessione.setTextColor(Color.BLUE);
			numeroCadute.setTextColor(Color.BLUE);
		}
		return convertView;
	}	
}