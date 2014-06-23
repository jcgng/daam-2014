package pt.iscte.meti.healthmonitor.service;

import java.util.List;

import pt.iscte.meti.healthmonitor.R;
import pt.iscte.meti.healthmonitor.db.HealthDS;
import pt.iscte.meti.healthmonitor.tasks.GetHealthTask;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

public class AlertsService extends Service {

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// get credentials
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String mUser = settings.getString("username", null);
        String mPassword = settings.getString("password", null);
		if(mUser!=null && mPassword!=null) {
	        // get patients
	        HealthDS datasource = new HealthDS(this);
			datasource.open();
			List<Integer> patients = datasource.getAllPatients();
			// database close
            datasource.close();
            datasource = null;
            // get health values
	        for(Integer idPatients : patients) {
	        	new GetHealthTask(this,false).execute(GetHealthTask.REQUESTS.GET_HEALTH.toString(),idPatients.toString());
	        }
		}
		return Service.START_NOT_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	public void sendNotification(String title, String msg, Intent intent) {
		NotificationCompat.Builder mBuilder =
			    new NotificationCompat.Builder(this)
			    .setSmallIcon(R.drawable.ic_launcher)
			    .setContentTitle(title)
			    .setContentText(msg);
		
		PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
		
		mBuilder.setContentIntent(pendingIntent);
		
		// Sets an ID for the notification
		int mNotificationId = 001;
		// Gets an instance of the NotificationManager service
		NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// Builds the notification and issues it.
		mNotifyMgr.notify(mNotificationId, mBuilder.build());
	}
}
