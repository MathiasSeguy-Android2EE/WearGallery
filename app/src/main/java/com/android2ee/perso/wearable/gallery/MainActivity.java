package com.android2ee.perso.wearable.gallery;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

public class MainActivity extends Activity {
	/**
	 * 
	 */
	private static final String SWITCH = "switch";
	/**
	 * 
	 */
	private static final String NAME = "name";
	/**
	 * 
	 */
	private static final String INTENT_SWITCH = "IntentSwitch";
	/**
	 * 
	 */
	private static final String INTENT_LOVE = "intentLove";
	boolean celesteVisible = false;
	/**
	 * The pendingIntent fired
	 */
	PendingIntent pdIntentLove;
	/**
	 * The pendingIntent fired
	 */
	PendingIntent pdIntentSwitch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//no inflation
		// setContentView(R.layout.activity_main);
		//Define the Intents
		Intent intentLove = new Intent(this, MainActivity.class);
		intentLove.putExtra(NAME, INTENT_LOVE);
		pdIntentLove = PendingIntent.getActivity(this, 0, intentLove,0);

		Intent intentSwitch = new Intent(this, MainActivity.class);
		intentSwitch.putExtra(SWITCH, true);
		intentSwitch.putExtra(NAME, INTENT_SWITCH);
		pdIntentSwitch = PendingIntent.getActivity(this, 1, intentSwitch, 0);//PendingIntent.FLAG_CANCEL_CURRENT

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		celesteVisible = prefs.getBoolean("celesteVisible", false);
		if (getIntent().hasExtra(NAME)) {
			//Log.e("MainActivity", "Intent has a name " + getIntent().getStringExtra(NAME));
			if (getIntent().getStringExtra(NAME).equals(INTENT_LOVE)) {
				//do nothing
			} else if (getIntent().getStringExtra(NAME).equals(INTENT_SWITCH)) {
				if (getIntent().hasExtra(SWITCH)) {
					celesteVisible = !celesteVisible;
					//Log.e("MainActivity", "Switch handled");
				} 
				//then show notification
				showStackingNotification();
			}
		} else {
			//Log.e("MainActivity", "Intent has no name");
			//at launch celeste is never visible
			celesteVisible = false;
			showStackingNotification();
		}

		prefs.edit().putBoolean("celesteVisible", celesteVisible).commit();

		finish();
	}

	/**
	 * Build the core of the notification
	 * 
	 * @return the builder filled
	 */
	private NotificationCompat.Builder buildCoreNotification() {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		builder.setAutoCancel(true)
				.setContentIntent(pdIntentLove)
				.setContentTitle("This is the ContentTitle")
				.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.paysage))
				.setLights(0x99FF0000, 0, 1000)// don't work
				.setNumber(41108)
				.setOngoing(false)
				.setPriority(Integer.MIN_VALUE)
				.setProgress(100, 0, true) // don't work
				.setSmallIcon(R.drawable.ic_notif_small_icon)
				.setSubText("This is SubText")
				.setTicker("This is Ticker")
				.setVibrate(new long[] { 100, 200, 100, 200, 100 }) // don't work
				.setWhen(System.currentTimeMillis())
				// .setColor();//not yet implemented waiting for L
				
				.setContentInfo("This is a ContentInfo");
		// .setUsesChronometer(true);
		return builder;
	}

	private void showNotification(int id, Notification notif) {
		NotificationManagerCompat notifManager = NotificationManagerCompat.from(this);
		notifManager.notify(id, notif);
	}

	private void hideNotification(int id) {
		NotificationManagerCompat notifManager = NotificationManagerCompat.from(this);
		notifManager.cancel(id);
	}

	/**
	 * Build a Stacking notification
	 */
	private void showStackingNotification() {
		showCeline();
		showBasile();
		showCeleste();
		// Group summary
		// Not displayed on the wearable
		NotificationCompat.Builder builderGroup = buildCoreNotification();
		builderGroup.setContentTitle("Love You")
				.setContentText("Displayed only on the phone not on the wearable")
				.setGroup("MyLove")
				.setGroupSummary(true)
				// add an action
				.addAction(new NotificationCompat.Action(R.drawable.ic_notif_small_icon, "Action", pdIntentLove))
				.addAction(new NotificationCompat.Action(R.drawable.ic_notif_switch, "Switch", pdIntentSwitch));
		builderGroup.extend(new NotificationCompat.WearableExtender()
				.setBackground(BitmapFactory.decodeResource(getResources(), R.drawable.paysage)));
		// then insure to have a different id for each notification
		showNotification(R.string.hello_world + 5, builderGroup.build());
	}

	/**
	 * 
	 */
	private void showCeline() {
		// first notification page (because of the A)
		// Not displayed on the phone
		NotificationCompat.Builder builder1 = buildCoreNotification();
		builder1.setContentTitle("Celine")
				.setGroup("MyLove")
				// add an action
				.addAction(new NotificationCompat.Action(R.drawable.ic_notif_small_icon, "Action", pdIntentLove))
				.setSortKey("A");
		NotificationCompat.BigPictureStyle builderBigPicture1 = new NotificationCompat
				.BigPictureStyle(builder1)
						.bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.celine));
		showNotification(R.string.hello_world + 1, builderBigPicture1.build());
	}

	/**
	 * 
	 */
	private void showBasile() {
		// second notification page (because of the B)
		// Not displayed on the phone
		NotificationCompat.Builder builderP2 = buildCoreNotification();
		builderP2
				.setContentTitle("Basile")
				// add an action
				.addAction(new NotificationCompat.Action(R.drawable.ic_notif_small_icon, "Action", pdIntentLove))
				.setGroup("MyLove")
				.setSortKey("B");
		NotificationCompat.BigPictureStyle builderBigPicture2 = new NotificationCompat
				.BigPictureStyle(builderP2)
						.bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.basile));
		showNotification(R.string.hello_world + 2, builderBigPicture2.build());
	}

	/**
	 * 
	 */
	private void showCeleste() {
		if (celesteVisible) {
			// third notification page (because of the C)
			// Not displayed on the phone
			NotificationCompat.Builder builderP3 = buildCoreNotification();
			builderP3
					.setContentTitle("Carine")
					.setGroup("MyLove")
					.setSortKey("C")
					.addAction(new NotificationCompat.Action(R.drawable.ic_notif_switch, "Switch", pdIntentSwitch));
			NotificationCompat.BigPictureStyle builderBigPicture3 = new NotificationCompat
					.BigPictureStyle(builderP3)
							.bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.carine));
			// third notification page (because of the C)
			NotificationCompat.Builder builderP4 = buildCoreNotification();
			builderP4
					.setContentTitle("Love")
					.setGroup("MyLove")
					.setSortKey("D")
					.addAction(new NotificationCompat.Action(R.drawable.ic_notif_switch, "Switch", pdIntentSwitch));
			NotificationCompat.BigTextStyle builderBigText = new NotificationCompat
					.BigTextStyle(builderP4)
							.bigText("Je t'aime comme le vent aime à faire chanter les arbres, en te soufflant des mots doux.");

			showNotification(R.string.hello_world + 3, builderBigPicture3.build());
			showNotification(R.string.hello_world + 4, builderBigText.build());
		} else {
			//Log.e("MainActivity", "Switch handled canceling notification");
			hideNotification(R.string.hello_world + 3);
			hideNotification(R.string.hello_world + 4);
		}
	}
}
