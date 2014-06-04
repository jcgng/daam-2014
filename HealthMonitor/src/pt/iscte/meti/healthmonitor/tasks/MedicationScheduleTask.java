package pt.iscte.meti.healthmonitor.tasks;

import pt.iscte.meti.healthmonitor.MainActivity;
import pt.iscte.meti.healthmonitor.PatientActivity;
import android.app.Activity;
import android.os.AsyncTask;

public class MedicationScheduleTask extends AsyncTask<Void, Void, Boolean>{	
	private Activity activity = null;
	
	public MedicationScheduleTask(Activity activity) {
		this.activity = activity; 
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		return true;
	}

	@Override
    protected void onPostExecute(Boolean result) {

	}
}
