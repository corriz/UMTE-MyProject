package cz.fim.project;


import java.io.File;
import java.util.List;

import cz.fim.project.data.Clients;
import cz.fim.projekt.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomClientAdapter extends BaseAdapter {
	
	List<Clients> data;
	LayoutInflater inflater;
	
	

	public CustomClientAdapter(List<Clients> clients,
			Context context) {
		this.inflater = LayoutInflater.from(context);
		this.data = clients;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if(v == null){
			v = inflater.inflate(R.layout.klient_row_lv, null);
		}
		TextView name = (TextView) v.findViewById(R.id.txtClintName);
		TextView email = (TextView) v.findViewById(R.id.txClientEmail);
				
		Clients cl = data.get(position);
		name.setText(""+ cl.getFirstname() + " " + cl.getLastname());
		email.setText(cl.getCity());
		if(cl.getObrSign() != null){
			ImageView icon = (ImageView) v.findViewById(R.id.klient_icon);
			File imgFile = new  File(cl.getObrSign().toString());
			if(imgFile.exists()){
			    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
			    icon.setImageBitmap(myBitmap);
			    }
		}
		
		return v;
	}
}
