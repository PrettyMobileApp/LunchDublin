package com.prettymobileapp.liu.lunchdublin;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Created by m on 06/03/2017.
 */
public class AlertReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		createNotification(context, "Times Up","5 Seconds has passed","Alert");
	}
	//------------------------------create noti method
	public void createNotification(Context context, String msg, String msgText, String msgAlert){
		Intent i = new Intent(context,MainActivity.class);
		PendingIntent notificIntent = PendingIntent.getActivity(context,0,i,0);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.lunch)
				.setContentTitle(msg)
				.setTicker(msgAlert)
				.setContentText(msgText);
		mBuilder.setContentIntent(notificIntent);
		mBuilder.setDefaults(Notification.DEFAULT_SOUND);
		mBuilder.setAutoCancel(true);
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(1,mBuilder.build());


	}

}
