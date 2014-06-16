package pt.iscte.meti.healthmonitor;

import pt.iscte.meti.healthmonitor.db.HealthDS;
import pt.iscte.meti.healthmonitor.draw.Drawing;
import pt.iscte.meti.healthmonitor.models.HealthData;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;

public class MonitorActivity extends Activity {
	private TextView bpmTextView = null;
	private ImageView heartImageView = null;
	private TextView tempTextView = null;
	private ImageView thermometerImageView = null;
	private Thread thread = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitor);
		// Show the Up button in the action bar.
		setupActionBar();
		// get patient information
		Bundle bundle = getIntent().getExtras();
		TextView nameTextView = (TextView)findViewById(R.id.name);
		nameTextView.setText(bundle.getString("name"));
		TextView bedNumberTextView = (TextView)findViewById(R.id.bedNumber);
		final String bedNumber = "" + bundle.getInt("bedNumber");
		bedNumberTextView.setText("Bed: " + bedNumber);
		// bind bpm and temp TextView
		this.bpmTextView = (TextView) findViewById(R.id.bpm);
		this.tempTextView = (TextView) findViewById(R.id.temp);
		// bind heart ImageView
		heartImageView = (ImageView) findViewById(R.id.heartImageView);
		heartImageView.setImageResource(R.drawable.heart);
		heartImageView.setVisibility(View.INVISIBLE);
		// bind thermometer DrawView
		thermometerImageView = (ImageView) findViewById(R.id.thermometerImageView);
		thermometerImageView.setVisibility(View.INVISIBLE);
		// start thermometer
		final Drawing thermometer = new Drawing(thermometerImageView);
		// start pulse
		final Animation pulse = AnimationUtils.loadAnimation(MonitorActivity.this, R.anim.pulse);
		pulse.setRepeatCount(Animation.INFINITE);
		
		final Integer idPatients = bundle.getInt("idPatients");
		
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
								// database open
						        HealthDS datasource =  new HealthDS(MonitorActivity.this);
								datasource.open();
						        // save to database
								HealthData healthData = datasource.getLastHealth(idPatients);
						        // database close
					            datasource.close();
					            datasource = null;
								
					            if(healthData!=null) {
									bpmTextView.setText(healthData.getBpm() + " bpm");
									heartImageView.setVisibility(View.VISIBLE);
									heartImageView.startAnimation(pulse);
									
									tempTextView.setText(healthData.getTemp() + " ºC");
									thermometer.drawThermometer(healthData.getTemp());
									thermometerImageView.setVisibility(View.VISIBLE);
					            }
							}
						});
					}
			    } catch (InterruptedException ie) {
			    	ie.printStackTrace();
			    }
			}
		};
		thread.start();
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
		getMenuInflater().inflate(R.menu.monitor, menu);
		return true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(thread!=null && !thread.isInterrupted())
			thread.interrupt();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public TextView getBpmTextView() {
		return bpmTextView;
	}
	public ImageView getHeartImageView() {
		return heartImageView;
	}
	
	public TextView getTempTextView() {
		return tempTextView;
	}
	public ImageView getThermometherImageView() {
		return thermometerImageView;
	}
}
