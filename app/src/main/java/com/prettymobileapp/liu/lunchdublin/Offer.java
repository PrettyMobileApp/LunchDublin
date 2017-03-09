package com.prettymobileapp.liu.lunchdublin;

import android.util.Log;

import java.io.Serializable;

/**
 * Created by m on 05/03/2017.
 */
public class Offer implements Serializable {
	private final String TAG = "checkError";
	private int offer_id;
	private String restaurant_name,offer_short,offer_details,restaurant_location;
	private float distance;

	public Offer(String restaurant_name,String offer_short,String offer_details,String restaurant_location,float distance) {
		this.restaurant_name = restaurant_name;
		this.offer_short = offer_short;
		this.offer_details = offer_details;
		this.restaurant_location = restaurant_location;
		this.distance=distance;
		Log.v(TAG,"Create Offer object: offer_id is "+offer_id );
	}
	public Offer(){this("", "", "","",1000000);}
	
	public void set_restaurant_name(String restaurant_name) {  this.restaurant_name = restaurant_name;    }
    public String get_restaurant_name() {   return restaurant_name;    }
	
	public void set_offer_short(String offer_short) {  this.offer_short = offer_short;    }
    public String get_offer_short() {   return offer_short;    }
	
	public void set_offer_details(String offer_details) {  this.offer_details = offer_details;    }
    public String get_offer_details() {   return offer_details;    }
	
	public void set_restaurant_location(String restaurant_location) {  this.restaurant_location = restaurant_location;    }
	public String get_restaurant_location() {   return restaurant_location;    }
	public void set_distance(float distance) {  this.distance= distance;    }
	public float get_distance() {   return distance;    }
	
}
