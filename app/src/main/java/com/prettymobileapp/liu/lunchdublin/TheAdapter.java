package com.prettymobileapp.liu.lunchdublin;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by m on 05/03/2017.
 */
public class TheAdapter extends ArrayAdapter<Offer>
{private final String TAG = "checkError";
	public TheAdapter(Context context, ArrayList<Offer> objects){ super(context, R.layout.row_layout,objects);}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		Log.v(TAG,"--listView position"+String.valueOf(position));
		LayoutInflater theIInflater=LayoutInflater.from(getContext());
		View theView=theIInflater.inflate(R.layout.row_layout,parent,false);
		Offer offers=getItem(position);

		TextView theTextView =(TextView) theView.findViewById(R.id.textView1);
		theTextView.setText(offers.get_offer_short());
		TextView theTextView2 =(TextView) theView.findViewById(R.id.textView2);
		theTextView2.setText(offers.get_restaurant_name());
		TextView theTextView3 =(TextView) theView.findViewById(R.id.textView3);
		theTextView3.setText("test line3");
		ImageView imageView = (ImageView) theView.findViewById(R.id.imageView);
		Log.v(TAG,"--end of listView position"+String.valueOf(position));
		return theView;

	}

}
