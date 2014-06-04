package pt.iscte.meti.healthmonitor;

import java.util.ArrayList;

import pt.iscte.meti.healthmonitor.R;
import pt.iscte.meti.healthmonitor.models.MedicationData;
import pt.iscte.meti.healthmonitor.models.MedicationListAdapter;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.graphics.BitmapFactory;
import android.os.Build;

public class PatientActivity extends Activity {
//	Bundle bundle = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patient);
		// Show the Up button in the action bar.
		setupActionBar();
//		if(savedInstanceState==null) {
//			// get patient information
//			bundle = getIntent().getExtras();
//		} else {
//			bundle = savedInstanceState;
//		}
		Bundle bundle = getIntent().getExtras();
		TextView nameTextView = (TextView)findViewById(R.id.name);
		nameTextView.setText(bundle.getString("name"));
		TextView ageTextView = (TextView)findViewById(R.id.age);
		ageTextView.setText("Age: " + bundle.getInt("age"));
		TextView genderTextView = (TextView)findViewById(R.id.gender);
		genderTextView.setText("Gender: " + bundle.getChar("gender"));
		TextView bedNumberTextView = (TextView)findViewById(R.id.bedNumber);
		bedNumberTextView.setText("Bed: " + bundle.getInt("bedNumber"));
		TextView registerTextView = (TextView)findViewById(R.id.register);
		registerTextView.setText("Register: " + bundle.getString("registerTimestamp"));
		TextView birthdayTextView = (TextView)findViewById(R.id.birthday);
		birthdayTextView.setText("Birthday: " + bundle.getString("birthday"));
		TextView diagnosisTextView = (TextView)findViewById(R.id.diagnosis);
		diagnosisTextView.setText("Diagnosis: " + bundle.getString("diagnosis"));
		TextView backgroundTextView = (TextView)findViewById(R.id.background);
		backgroundTextView.setText("Background: " + bundle.getString("background"));
		
		ArrayList<MedicationData> medications = getIntent().getParcelableArrayListExtra("medications");
		MedicationListAdapter adapter = new MedicationListAdapter(this,medications);
		ListView medicationListView = (ListView)findViewById(R.id.medicationListView);
		medicationListView.setAdapter(adapter);
		
		ImageView imageViewPic = (ImageView) findViewById(R.id.imageViewPic);
		byte[] byteArray = bundle.getByteArray("photo");
		if(byteArray!=null) {
			imageViewPic.setImageBitmap(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length));
		} else {
			if(bundle.getChar("gender")=='M')
				imageViewPic.setImageResource(R.drawable.man);
			else 
				imageViewPic.setImageResource(R.drawable.woman);
		}
		
		// TODO: To update the ListView every minute
//		final Handler handler = new Handler()
//		handler.postDelayed( new Runnable() {
//		    @Override
//		    public void run() {
//		        adapter.notifyDataSetChanged();
//		        handler.postDelayed( this, 60 * 1000 );
//		    }
//		}, 60 * 1000 );
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.patient, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
//	@Override
//	protected void onSaveInstanceState(Bundle savedInstanceState) {
//		 super.onSaveInstanceState(savedInstanceState);
//		 savedInstanceState.putAll(bundle);
//	}
//	@Override
//	protected void onRestoreInstanceState(Bundle savedInstanceState) {
//		bundle = savedInstanceState;
//	}

}
