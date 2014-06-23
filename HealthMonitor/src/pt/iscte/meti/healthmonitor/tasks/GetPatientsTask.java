package pt.iscte.meti.healthmonitor.tasks;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.iscte.meti.healthmonitor.MainActivity;
import pt.iscte.meti.healthmonitor.R;
import pt.iscte.meti.healthmonitor.db.HealthDS;
import pt.iscte.meti.healthmonitor.models.MedicationData;
import pt.iscte.meti.healthmonitor.models.PatientData;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

public class GetPatientsTask extends AsyncTask<String,String,JSONArray> {
	public static enum REQUESTS {
	  	GET_PATIENT("GetPatient");
	    
	    private final String request;       
	    private REQUESTS(String str) { request = str; }
	    public String toString() { return request; }
	};
	
	private Activity activity = null;
	
	private final String patientsUrl = "%s/HealthMonitor/get-patient.php?user=%s&pass=%s";
	private final String photoUrl = "%s/HealthMonitor/get-photo.php?photo=%s";
		
	private String request = null;
	
	private boolean showDiags = true;
	private ProgressDialog progressDiag = null;

	private boolean errorOccurred = false;
	private Builder alertDiag = null;
		
	public GetPatientsTask(Activity activity,boolean showDiags) {
		this.activity = activity; 
		this.showDiags = showDiags; 
	}
	
	private String sendGet(String url) throws ClientProtocolException, IOException {
		String responseString = null;
		HttpClient client = new DefaultHttpClient();
	    HttpResponse response = client.execute(new HttpGet(url));
    	StatusLine statusLine = response.getStatusLine();
    	if(statusLine.getStatusCode() == HttpStatus.SC_OK){
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            response.getEntity().writeTo(out);
            out.close();
            responseString = out.toString();
        } else {
        	Log.e(GetHealthTask.class.getName(), "Failed to send get!");
            // Closes the connection.
            response.getEntity().getContent().close();
            throw new IOException(statusLine.getReasonPhrase());
        }
    	return responseString;
	}
		
	private Bitmap downloadPhoto(String photo) throws IOException {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(activity);
	    String serverAddress = settings.getString("server_address", activity.getResources().getString(R.string.pref_default_server));
		String urlStr = String.format(photoUrl,serverAddress,photo);
		URL url = new URL(urlStr);
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setDoInput(true);
	    conn.connect();
	    return BitmapFactory.decodeStream(conn.getInputStream());
	}
	
	private JSONArray GetPatients() throws ClientProtocolException, IOException, JSONException {
		JSONArray jsonArray = null;
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(activity);
		String mUser = settings.getString("username", null);
	    String mPassword = settings.getString("password", null);
	    String serverAddress = settings.getString("server_address", activity.getString(R.string.pref_default_server));
		String url = String.format(patientsUrl,serverAddress,mUser,mPassword);
		String patientsInfo = sendGet(url);
		if(patientsInfo!=null && !patientsInfo.equals("")) {
			jsonArray = new JSONArray(patientsInfo);
			Log.i(GetHealthTask.class.getName(), "Number of entries " + jsonArray.length());
		}
		if((activity instanceof MainActivity) && (jsonArray!=null)){
			MainActivity mainActivity = (MainActivity) activity; 
			int length = jsonArray.length();
			mainActivity.getPatientListData().clear();
			// database open
			HealthDS datasource =  new HealthDS(activity);
			datasource.open();
			datasource.cleanPatients();
	        for(int i=0;i<length;i++) {
				JSONObject jsonData = jsonArray.getJSONObject(i);
				// get medication
				ArrayList<MedicationData> medications = new ArrayList<MedicationData>();
				JSONArray medicationsJsonArray = jsonData.getJSONArray("medications");
				for(int j=0;j<medicationsJsonArray.length();j++) {
					JSONObject medicationJsonData = medicationsJsonArray.getJSONObject(j);
					medications.add(new MedicationData(
						medicationJsonData.getInt("idMedications"),
						medicationJsonData.getString("drug"),
						medicationJsonData.getInt("dosage"),
						medicationJsonData.getString("units"),
						medicationJsonData.getInt("patientDosage"),
						medicationJsonData.getString("schedule"),
						medicationJsonData.getString("route")));
				}
				PatientData patient = new PatientData(
					jsonData.getInt("idPatients"),
					jsonData.getString("registerTimestamp"),
					jsonData.getString("name"),
					jsonData.getInt("age"),
					jsonData.getString("gender").charAt(0),
					jsonData.getInt("bedNumber"),
					jsonData.getString("Boards_deviceId"),
					jsonData.getString("birthday"),
					jsonData.getString("diagnosis"),
					jsonData.getString("background"),
					medications);
				
				if(jsonData.getString("photo")!=null && !jsonData.getString("photo").equals("null")) {
					// download photo
					patient.setPhoto(downloadPhoto(jsonData.getString("photo")));
				}
					
				if(jsonData.getString("gender").charAt(0)=='M') {
					patient.setPhotoResource(R.drawable.man);
				} else { 
					patient.setPhotoResource(R.drawable.woman);
				}				
	            mainActivity.getPatientListData().add(patient);
	            // save to database
	            datasource.addPatient(patient.getId());
	        }
	        // database close
            datasource.close();
            datasource = null;
		}
		
		return jsonArray;
	}
	
	private void fillPatientList(JSONArray jsonArray) throws JSONException {
		if((activity instanceof MainActivity) && (jsonArray!=null)){
			MainActivity mainActivity = (MainActivity) activity; 
	        mainActivity.setPatientListAdapter();
	        mainActivity.getPatientListView().setAdapter(mainActivity.getPatientListAdapter());
		}
	}
		
	@Override
	protected JSONArray doInBackground(String... params) {
		if(params!=null && params.length>0) {
			if(params[0].equals(REQUESTS.GET_PATIENT.toString())) {
				request = REQUESTS.GET_PATIENT.toString();
				try {
					return this.GetPatients();
				} catch (ClientProtocolException e) {
					errorOccurred = true;
					e.printStackTrace();
				} catch (IOException e) {
					errorOccurred = true;
					e.printStackTrace();
				} catch (JSONException e) {
					errorOccurred = true;
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	@Override
    protected void onPostExecute(JSONArray result) {
        super.onPostExecute(result);
        if(request.equals(REQUESTS.GET_PATIENT.toString())) {
			try {
				fillPatientList(result);
			} catch (JSONException e) {
				errorOccurred = true;
				e.printStackTrace();
			}
		}
        if(progressDiag.isShowing())
        	progressDiag.dismiss();
        if(errorOccurred && showDiags)
        	alertDiag.show();
       
       	activity = null;
        
    }
	
	@Override
	protected void onPreExecute(){ 
	   super.onPreExecute();
	   progressDiag = new ProgressDialog(activity);
	   progressDiag.setMessage("Loading...");
	   if(showDiags)
			progressDiag.show();
	
	   alertDiag = new AlertDialog.Builder(activity); 
	   alertDiag.setTitle("Error");  
       alertDiag.setMessage("Error getting information from server!");  
	}
	
	@Override
	protected void onCancelled() {		
		if(progressDiag.isShowing())
        	progressDiag.dismiss();
		
		activity = null;
	}

}
