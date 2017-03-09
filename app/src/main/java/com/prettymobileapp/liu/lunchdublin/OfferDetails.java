package com.prettymobileapp.liu.lunchdublin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class OfferDetails extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.offer_details);

		TextView tv_4 = (TextView) findViewById(R.id.textView4);
		Intent intent_received = getIntent();
		Offer offer_selected = (Offer) intent_received.getSerializableExtra("offer_selected");
		tv_4.setText( offer_selected.get_offer_short());
	}
}
