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
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

/**
 * TODO:
 * 
 * Important:
 * 	Add retry to error alert
 * 
 * Clean code & Design:
 * 	Remove all AsycTasks from activity classes	
 * 
 * Allow/resolve rotation problem
 * 
 * @author Jo�o Guiomar
 */
public class MainActivity extends Activity {
//	public static String serverAddress = null;

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
		// hide keyboard
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
    	boolean alerts = settings.getBoolean("notification_alerts", true);
        if(alerts) {
			// start every 20 seconds
			Calendar calendar = Calendar.getInstance();
			Intent intent = new Intent(this, AlertsService.class);
			PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
			AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
			alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 20*1000, pendingIntent); 
        }
	}

	@Override
	public void onStart() {
		super.onStart();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		String mUser = settings.getString("username", null);
	    String mPassword = settings.getString("password", null);
		if((thread==null || !thread.isAlive()) && mUser!=null && mPassword!=null) {
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
									new GetPatientsTask(MainActivity.this,true).execute(GetPatientsTask.REQUESTS.GET_PATIENT.toString());
								}
							});
							// 20 minutes
							Thread.sleep(1200000);
						}
				    } catch (InterruptedException e) {
				    	e.printStackTrace();
				    }
				}
			};
			thread.start();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		menu.getItem(0).setIcon(R.drawable.menu_icon);
		menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
	        public boolean onMenuItemClick(MenuItem item) {
	            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
	            MainActivity.this.startActivity(settingsIntent);
	            return false;
	        }
	    });
		
		return true;
	}
	
//	@Override
//	public void onStop() {
//		super.onStop();
//		if(thread!=null && !thread.isInterrupted()) {
//			thread.interrupt();
//			thread = null;
//		}
//	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(thread!=null && !thread.isInterrupted()) {
			thread.interrupt();
			thread = null;
		}
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
