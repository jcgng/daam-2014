package pt.iscte.meti.healthmonitor;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import pt.iscte.meti.healthmonitor.listeners.SwipeListListener;
import pt.iscte.meti.healthmonitor.models.PatientData;
import pt.iscte.meti.healthmonitor.models.PatientListAdapter;
import pt.iscte.meti.healthmonitor.tasks.GetInfoTask;

import pt.iscte.meti.healthmonitor.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

/**
 * TODO:
 * 
 * Important:
 *		Local medicine pill icon: Create AsyncTask to update icon and hour
 * 		Remote health values notification
 * 
 * Clean code:
 * 		Remove all AsycTasks from activity classes	
 * 
 * Allow/resolve rotation problem
 * 
 * @author João Guiomar
 */
public class MainActivity extends Activity {
	private ListView patientListView;
	private EditText inputSearch;
	
	private ArrayList<PatientData> patientsList = new ArrayList<PatientData>();
	private SwipeListListener swipeListListener;
	
	private PatientListAdapter patientListAdapter = null;
	
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
//		if(savedInstanceState==null) {
			// get patients
			if(LoginActivity.mUser!=null && LoginActivity.mPassword!=null)
				new GetInfoTask(this,true).execute(GetInfoTask.REQUESTS.GET_PATIENT.toString());
//		}
			
			// TODO: To update the ListView every minute
//			final Handler handler = new Handler()
//			handler.postDelayed( new Runnable() {
//			    @Override
//			    public void run() {
//			        adapter.notifyDataSetChanged();
//			        handler.postDelayed( this, 60 * 1000 );
//			    }
//			}, 60 * 1000 );
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
//	@Override
//	protected void onSaveInstanceState(Bundle savedInstanceState) {
//		 super.onSaveInstanceState(savedInstanceState);
//		 savedInstanceState.putSerializable("patientsList", patientListAdapter.getAllPatientsList());
//	}
//	@Override
//	protected void onRestoreInstanceState(Bundle savedInstanceState) {
//		patientsList.addAll((ArrayList<PatientData>)savedInstanceState.getSerializable("patientsList"));
//		setPatientListAdapter();
//        getPatientListView().setAdapter(getPatientListAdapter());
//	}
	
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
