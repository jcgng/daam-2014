package pt.iscte.meti.healthmonitor;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import pt.iscte.meti.healthmonitor.listeners.SwipeListListener;
import pt.iscte.meti.healthmonitor.models.PatientData;
import pt.iscte.meti.healthmonitor.models.PatientListAdapter;
import pt.iscte.meti.healthmonitor.service.AlertsService;
import pt.iscte.meti.healthmonitor.tasks.GetPatientsTask;

import pt.iscte.meti.healthmonitor.R;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

/**
 * TODO:
 * 
 * Important:
 *		Settings: Change login credentials, activate/deactivate notifications
 *
 *		Vibrate in alarm and vibrate in MonitorActivity new values		
 *
 *		Add clock with dateTime to MonitorActivity
 *		Add chart with last 10 values to MonitorActivity
 * 		Add retry to error alert
 * 
 * Clean code & Design:
 * 		Change pills image position
 * 		Remove all AsycTasks from activity classes	
 * 
 * Allow/resolve rotation problem
 * 
 * @author João Guiomar
 */
public class MainActivity extends Activity {
	public static String serverAddress = null;

	private ListView patientListView;
	private EditText inputSearch;
	
	private ArrayList<PatientData> patientsList = new ArrayList<PatientData>();
	private SwipeListListener swipeListListener;
	
	private PatientListAdapter patientListAdapter = null;
	
	private Thread thread = null;
	
	private TextWatcher filterTextWatcher = new TextWatcher() {
		@Override
	    public void afterTextChanged(Editable s) {  }
	    @Override
	    public void beforeTextChanged(CharSequence s, int start, int count, int after) {  }
		@Override
	    public void onTextChanged(CharSequence s, int start, int before, int count) { patientListAdapter.getFilter().filter(s); }
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// bind ListView
		patientListView = (ListView) findViewById(R.id.patientListView);
		patientListView.setTextFilterEnabled(true);
		// bind EditText
		inputSearch = (EditText) findViewById(R.id.inputSearch);
		inputSearch.addTextChangedListener(filterTextWatcher);
		
		// set listeners
		this.patientListView.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	Intent intent = new Intent(view.getContext(), PatientActivity.class);
		    	PatientData selected = patientsList.get(position);
		    	intent.putExtra("idPatients", selected.getId());
		    	intent.putExtra("name", selected.getName());
		    	intent.putExtra("age", selected.getAge());
		    	intent.putExtra("gender", selected.getGender());
		    	intent.putExtra("bedNumber", selected.getBed());
		    	intent.putExtra("registerTimestamp", selected.getRegisterTimestamp());
		    	intent.putExtra("birthday", selected.getBirthday());
		    	intent.putExtra("diagnosis", selected.getDiagnosis());
		    	intent.putExtra("background", selected.getBackground());
		    	
		    	intent.putParcelableArrayListExtra("medications",selected.getMedications());
		    	
		    	if(selected.getPhoto()!=null) {
			    	ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			    	selected.getPhoto().compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
			    	intent.putExtra("photo",byteArrayOutputStream.toByteArray());
		    	}
		    	
		    	startActivity(intent);
	        }
	    });
		this.swipeListListener = new SwipeListListener(this);
		this.patientListView.setOnTouchListener(this.swipeListListener);
		
		if(LoginActivity.mUser!=null && LoginActivity.mPassword!=null) {
			// start thread
			thread = new Thread() {
				@Override
				public void run() {
					try {
						while (!isInterrupted()) {
							Thread.sleep(1000);
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									if(LoginActivity.mUser!=null && LoginActivity.mPassword!=null)
										new GetPatientsTask(MainActivity.this,true).execute(GetPatientsTask.REQUESTS.GET_PATIENT.toString());
								}
							});
							Thread.sleep(1800000);
						}
				    } catch (InterruptedException e) {
				    	
				    }
				}
			};
			thread.start();
		}
		// TODO: If notifications are active in settings
		// start every 30 seconds
		Calendar calendar = Calendar.getInstance();
		Intent intent = new Intent(this, AlertsService.class);
		PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
		AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 30*1000, pendingIntent); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
	        public boolean onMenuItemClick(MenuItem item) {
	            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
	            MainActivity.this.startActivity(settingsIntent);
	            return false;
	        }
	    });
		
		return true;
	}
		
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(thread!=null && !thread.isInterrupted())
			thread.interrupt();
	}
	
	public ListView getPatientListView() {
		return patientListView;
	}
	
	public ArrayList<PatientData> getPatientListData() {
		return patientsList;
	}
	
	public SwipeListListener getSwipeListListener() {
		return swipeListListener;
	}
	
	public void setPatientListAdapter() {
		patientListAdapter = new PatientListAdapter(this,patientsList);
	}
	public PatientListAdapter getPatientListAdapter() {
		return patientListAdapter;
	}
}
