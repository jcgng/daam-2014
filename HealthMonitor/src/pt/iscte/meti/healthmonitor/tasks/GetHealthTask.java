package pt.iscte.meti.healthmonitor.tasks;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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

import pt.iscte.meti.healthmonitor.LoginActivity;
import pt.iscte.meti.healthmonitor.MainActivity;
import pt.iscte.meti.healthmonitor.MonitorActivity;
import pt.iscte.meti.healthmonitor.R;
import pt.iscte.meti.healthmonitor.db.HealthDS;
import pt.iscte.meti.healthmonitor.models.HealthData;
import pt.iscte.meti.healthmonitor.service.AlertsService;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class GetHealthTask extends AsyncTask<String,String,JSONArray> {
	public static enum REQUESTS {
	    GET_HEALTH("GetHealth");
	    
	    private final String request;       
	    private REQUESTS(String str) { request = str; }
	    public String toString() { return request; }
	};
	
	private Context context = null;
	
	private final String healthUrl = "%s/HealthMonitor/get-health.php?user=%s&pass=%s&patient=%d";
		
	private String request = null;
	
	private boolean showDiags = true;
	private boolean errorOccurred = false;
	private Builder alertDiag = null;

	private HealthData healthData = null;
	
	public GetHealthTask(Context context,boolean showDiags) {
		this.context = context; 
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
	
	private JSONArray GetHealth(int idPatients) throws ClientProtocolException, IOException, JSONException {
		JSONArray jsonArray = null;
		String url = String.format(healthUrl,MainActivity.SERVER,LoginActivity.mUser,LoginActivity.mPassword,idPatients);
		String healthInfo = sendGet(url);
		jsonArray = new JSONArray(healthInfo);
		Log.i(GetHealthTask.class.getName(), "Number of entries " + jsonArray.length());
		return jsonArray;
	}

	private void fillHealthValues(JSONArray jsonArray) throws JSONException {
		if((context instanceof AlertsService) && (jsonArray!=null)) {
			AlertsService alertsService = (AlertsService) context;
			int length = jsonArray.length();
	        for(int i=0;i<length;i++){
				JSONObject jsonData = jsonArray.getJSONObject(i);
				String param = jsonData.getString("param1");	
				if(param.equals("bpm")) {
					int bpm = jsonData.getInt("val1");
					healthData.setBpm(bpm);

				} else if(param.equals("temp")) {
					float temp = (float) jsonData.getDouble("val1");
					healthData.setTemp(temp);
				}
				String dateTime = jsonData.getString("dateTime");
				healthData.setDateTime(dateTime);
				int bedNumber = jsonData.getInt("bedNumber");
				healthData.setBedNumber(bedNumber);
				String name = jsonData.getString("name");
				healthData.setName(name);
	        }
	        // database open
	        HealthDS datasource =  new HealthDS(alertsService);
			datasource.open();
	        // save to database
            int res = datasource.addHealth(healthData);
	        // database close
            datasource.close();
            datasource = null;
            // test values and send notification
            if ((res==0) && (healthData.getBpm()<55 || healthData.getBpm()>100 || healthData.getTemp()<35 || healthData.getTemp()>37)) {
	            Intent intent = new Intent(alertsService, MonitorActivity.class);
	            intent.putExtra("name",healthData.getName());
	            intent.putExtra("bedNumber",healthData.getBedNumber());
	            intent.putExtra("idPatients",healthData.getIdPatients());
	            alertsService.sendNotification("Health Monitor","Alert: " + healthData.getName(),intent);
            }
		}
	}
	
	@Override
	protected JSONArray doInBackground(String... params) {
		if(params!=null && params.length>0) {
			if(params[0].equals(REQUESTS.GET_HEALTH.toString())) {
				request = REQUESTS.GET_HEALTH.toString();
				Integer idPatients = null;
				if(params.length>1 && params[1]!=null) 
					idPatients = Integer.parseInt(params[1]);
				if(idPatients!=null)
					try {
						healthData = new HealthData();
						healthData.setIdPatients(idPatients);
						return this.GetHealth(idPatients.intValue());
					} catch (ClientProtocolException e) {
//						errorOccurred = true;
						e.printStackTrace();
					} catch (IOException e) {
//						errorOccurred = true;
						e.printStackTrace();
					} catch (JSONException e) {
//						errorOccurred = true;
						e.printStackTrace();
					}
			} 
		}
		return null;
	}
	
	@Override
    protected void onPostExecute(JSONArray result) {
        super.onPostExecute(result);
        if(request.equals(REQUESTS.GET_HEALTH.toString())) {
			try {
				fillHealthValues(result);
			} catch (JSONException e) {
//				errorOccurred = true;
				e.printStackTrace();
			}
        }

        if(errorOccurred && showDiags)
        	alertDiag.show();
       
       	context = null;
        
    }
	
	@Override
	protected void onPreExecute(){ 
	   super.onPreExecute();
	   
	   alertDiag = new AlertDialog.Builder(context); 
	   alertDiag.setTitle("Error");  
       alertDiag.setMessage("Error getting information from server!");  
	}
	
	@Override
	protected void onCancelled() {		
		context = null;
	}
}
