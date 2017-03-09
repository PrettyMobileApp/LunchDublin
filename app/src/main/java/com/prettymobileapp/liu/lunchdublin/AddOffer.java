package com.prettymobileapp.liu.lunchdublin;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class AddOffer extends AppCompatActivity {
	private final String TAG = "checkError";
	private EditText et_restaurant,et_offer,et_details, et_location;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_offer);

		et_restaurant = (EditText) findViewById(R.id.editText_restaurant);
		et_offer = (EditText) findViewById(R.id.editText_offer);
		et_details = (EditText) findViewById(R.id.editText_details);
		et_location = (EditText) findViewById(R.id.editText_location);
		Button button_submit = (Button) findViewById(R.id.button_submit);
		
		
		
		button_submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				
				if(et_restaurant!=null && et_offer!=null && et_details!=null && et_location!=null){
					String name = et_restaurant.getText().toString();
					String offer = et_offer.getText().toString();
					String details = et_details.getText().toString();
					String location = et_location.getText().toString();
				
					Offer new_offer = new Offer(name,offer,details,location,1000000);
					//new_offer.set_restaurant_name(name);
					//new_offer.set_offer_short(offer);
					//new_offer.set_offer_details(details);
					//new_offer.set_restaurant_location(location);
				
					FirebaseDatabase database = FirebaseDatabase.getInstance();
					DatabaseReference offerRef = database.getReference();
					offerRef.child("lunchoffer").child(offer).setValue(new_offer);
				}else{  Toast.makeText(getApplicationContext(), "Offer not completed", Toast.LENGTH_SHORT).show();}
				
				
			}
		});


	}
}