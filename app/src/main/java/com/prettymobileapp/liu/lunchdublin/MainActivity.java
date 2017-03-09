package com.prettymobileapp.liu.lunchdublin;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.text.DateFormat;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import android.location.Location;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity  implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{
	private final String TAG = "checkError";
	private ArrayList<Offer> list_offers = new ArrayList<>();
	private ListView theListView1;
	private Button button_add_offer,button_start_noti,button_stop_noti,button_alarm;
	private NotificationManager notificationManager;
	private boolean isNotificationActive=false;
	private int notifID=33;
	private GoogleApiClient mGoogleApiClient;
	private Location mLocation;
	private	LocationRequest mLocationRequest;
	protected Location mCurrentLocation, mLastLocation;
	protected String mLastUpdateTime;
	protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key"; protected final static String LOCATION_KEY = "location-key"; protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
	private LatLng addressPos, finalAddressPos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

//------------------------------------------------------------------ code starts here
		button_add_offer = (Button) findViewById(R.id.button_add_offer);
		button_start_noti = (Button) findViewById(R.id.button1);
		button_stop_noti = (Button) findViewById(R.id.button2);
		button_alarm = (Button) findViewById(R.id.button3);
		theListView1 = (ListView) findViewById(R.id.theListView);

		Log.v(TAG, "1 starts....");
//------------------------------------------------------------------ get lastknown/current location
		if (mGoogleApiClient == null) {	mGoogleApiClient = new GoogleApiClient.Builder(this)	.addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).addApi(AppIndex.API).build();}
//------------------------------------------------------------------ Read all offers from Firebase
		Log.v(TAG, "2 read from firebase ...");
		final DatabaseReference offerRef = FirebaseDatabase.getInstance().getReference().child("lunchoffer");
		offerRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				ArrayList<String> list_addresses = new ArrayList<>();
				for (DataSnapshot snapshot : dataSnapshot.getChildren()) {	Offer offer = snapshot.getValue(Offer.class);	list_offers.add(offer); list_addresses.add(offer.get_restaurant_location()); }
				Log.v(TAG, "3 listView start here...");
				TheAdapter theAdapter1 = new TheAdapter(getApplicationContext(), list_offers);
				theListView1.setAdapter(theAdapter1);
				//-------------------------------------------------------calculate distance
				if (mLastLocation != null) {
					Log.v(TAG, "---1 AsyncTask to calculate distance");
					new CalculateDistance().execute( list_addresses );
				} else {Log.v(TAG, "---2 not start AsyncTast, Location is null");	}
				//-------------------------------------------------------
			}
			@Override
			public void onCancelled(DatabaseError databaseError) {
				Log.v(TAG, "failed to read SINGLE value");
			}
		});

		theListView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				Offer value = (Offer) adapterView.getItemAtPosition(i);
				Log.v(TAG, i + " was clicked.");
				Intent intent = new Intent(MainActivity.this, OfferDetails.class);
				intent.putExtra("offer_selected", value);
				startActivity(intent);
			}
		});
//-----------------------------------------------------------------------------click to add new offers
		button_add_offer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.v(TAG, " add_offer clicked");
				Intent intent = new Intent(MainActivity.this, AddOffer.class);
				//intent.putExtra(...,..);
				startActivity(intent);
			}
		});
//-----------------------------------------------------------------------------start notification
		button_start_noti.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.v(TAG, " start notification");
				NotificationCompat.Builder notificBuilder = new NotificationCompat.Builder(getApplicationContext())
						.setContentTitle("Lunch Offer").setContentText("Lunch").setTicker("Alert Lunch Offer").setSmallIcon(R.drawable.lunch);
				Intent moreInfoIntent = new Intent(getApplicationContext(), MoreInfoNotification.class);
				TaskStackBuilder tStackBuilder = TaskStackBuilder.create(getApplicationContext());
				tStackBuilder.addParentStack(MoreInfoNotification.class);
				tStackBuilder.addNextIntent(moreInfoIntent);
				PendingIntent pendingIntent = tStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
				notificBuilder.setContentIntent(pendingIntent);
				notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				notificationManager.notify(notifID, notificBuilder.build());
				isNotificationActive = true;
			}
		});

//-----------------------------------------------------------------------------stop notification
		button_stop_noti.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.v(TAG, " stop notification");
				if (isNotificationActive) {
					notificationManager.cancel(notifID);
				}
			}
		});
//-----------------------------------------------------------------------------start notification
		button_alarm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.v(TAG, " add_offer clicked");
				Long alertTime = new GregorianCalendar().getTimeInMillis() + 5 * 1000;
				Intent alertIntent = new Intent(getApplicationContext(), AlertReceiver.class);
				AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
				alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime, PendingIntent.getBroadcast(getApplicationContext(), 1, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));
			}
		});
	}
//-----------------------------------------------------------------------------------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void onStart() {
		Log.i(TAG, "onStart");
		mGoogleApiClient.connect();
		super.onStart();
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Main Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app URL is correct.
				Uri.parse("android-app://com.prettymobileapp.liu.lunchdublin/http/host/path")
		);
		AppIndex.AppIndexApi.start(mGoogleApiClient, viewAction);
	}
	@Override
	protected void onStop() {
		Log.i(TAG, "onStop");
		mGoogleApiClient.disconnect();
		super.onStop();
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Main Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app URL is correct.
				Uri.parse("android-app://com.prettymobileapp.liu.lunchdublin/http/host/path")
		);
		AppIndex.AppIndexApi.end(mGoogleApiClient, viewAction);
	}
	@Override
	public void onConnected(Bundle bundle) {  Log.i(TAG, "onConnected");   try{ mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);}catch (SecurityException e){e.printStackTrace();}
		if (mLastLocation != null) {
			Log.i(TAG, "location is not null");
			//mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
			//mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
		}else{Log.i(TAG, "location is null");Toast.makeText(this, "location not found, check GPS ",Toast.LENGTH_SHORT).show();}	}
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {Log.i(TAG, "onConnectionFailed");   	}
	@Override
	public void onConnectionSuspended(int i) {Log.i(TAG, "onConnectionSuspended");   	}

//---------------------------------------------------------------------------------------------distance
	class CalculateDistance extends AsyncTask<ArrayList<String>, Void, Void> {
		List<Float> the_distances = new ArrayList<>();

		@Override
		protected Void doInBackground(ArrayList<String>... strings) {
			ArrayList<String> the_addresses = strings[0];
			for( String the_address : the_addresses){
			the_address = the_address.replaceAll(" ","%20");
			Log.v(TAG,"---calculate distance "+the_address);
			String uri = "http://maps.google.com/maps/api/geocode/json?address=" +	the_address +"%20Dublin%20Ireland"+ "&sensor=false";
			HttpGet httpGet = new HttpGet(uri);
			HttpClient client = new DefaultHttpClient();
			HttpResponse response;
			StringBuilder stringBuilder = new StringBuilder();
			try {
				response = client.execute(httpGet);
				HttpEntity entity = response.getEntity();
				InputStream stream = entity.getContent();
				int byteData;
				while ((byteData = stream.read()) != -1) {	stringBuilder.append((char) byteData);	}
			} catch (ClientProtocolException e) {	Log.v(TAG,"---error1 calculate distance");e.printStackTrace();	} catch (IOException e) {	Log.v(TAG,"---error2 calculate distance");	e.printStackTrace();	}
			double lat = 0.0, lng = 0.0;
			JSONObject jsonObject;
			Location location_end = new Location("");
			try {
				jsonObject = new JSONObject(stringBuilder.toString());
				lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
						.getJSONObject("geometry").getJSONObject("location")
						.getDouble("lng");
				lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
						.getJSONObject("geometry").getJSONObject("location")
						.getDouble("lat");
				location_end.setLongitude(lng); location_end.setLatitude(lat);
				the_distances . add(mLastLocation.distanceTo(location_end));
			} catch (JSONException e) {		e.printStackTrace();		}}
			return null;
		}
		@Override
		protected void onPostExecute(Void avoid) {
			Log.v(TAG,"---calculate distance onPostExecute");
			for(int i=0;i<the_distances.size();i++ ) {

				TextView the_TextView3 = (TextView) theListView1.getChildAt(i).findViewById(R.id.textView3);
				String st = "unknown";
				if(the_distances.get(i)<5000) {st=String.valueOf(Math.round(the_distances.get(i)) + "m");}
				the_TextView3.setText(st);
			}
		}
}


//------------------------------------------------------------------------------------------------------------------ end
}
