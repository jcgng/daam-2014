package pt.iscte.meti.healthmonitor.models;

import java.util.ArrayList;
import java.util.Calendar;

import pt.iscte.meti.healthmonitor.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MedicationListAdapter extends ArrayAdapter<MedicationData> {
	private Context context;
	private ArrayList<MedicationData> medicationList;
	private final int[] colors = new int[] { 0x30A3A3CC, 0x30CCCCFF };
		
	public MedicationListAdapter(Context context, ArrayList<MedicationData> medicationList) {
		super(context, R.layout.medication_list_layout, medicationList);
		this.context = context;
		this.medicationList = medicationList;
	}

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.medication_list_layout, parent, false);
		
		 if(position%2 == 0)
			view.setBackgroundColor(this.colors[0]);
		else 
			view.setBackgroundColor(this.colors[1]);
		
		TextView drugTextView = (TextView) view.findViewById(R.id.drug);
	    drugTextView.setText(medicationList.get(position).getDrug());
		
	    TextView patientDosageTextView = (TextView) view.findViewById(R.id.patientDosage);
	    patientDosageTextView.setText("Dosage: " + medicationList.get(position).getPatientDosage());
	    
	    TextView scheduleTextView = (TextView) view.findViewById(R.id.schedule);
	    ImageView pillImageView = (ImageView) view.findViewById(R.id.pillIcon);
	    String schedule = medicationList.get(position).getSchedule();
	    Calendar calendar = Calendar.getInstance();
	    int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
	    int currentMinute = calendar.get(Calendar.MINUTE);
	    int nextHour = currentHour;
	    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
	    Integer startHour = Integer.parseInt(settings.getString("meds_start_hour",context.getResources().getString(R.string.pref_default_meds_starthour)));
	    if(schedule.equals(MedicationData.SCHEDULES.H24.toString())) {
	    	nextHour = medicationList.get(position).nextHour(startHour.intValue(),currentHour,24);
	    } else if(schedule.equals(MedicationData.SCHEDULES.H12.toString())) {
	    	nextHour = medicationList.get(position).nextHour(startHour.intValue(),currentHour,12);
	    } else if(schedule.equals(MedicationData.SCHEDULES.H8.toString())) {
	    	nextHour = medicationList.get(position).nextHour(startHour.intValue(),currentHour,8);
	    } else if(schedule.equals(MedicationData.SCHEDULES.H6.toString())) {
	    	nextHour = medicationList.get(position).nextHour(startHour.intValue(),currentHour,6);
	    } else if(schedule.equals(MedicationData.SCHEDULES.H4.toString())) {
	    	nextHour = medicationList.get(position).nextHour(startHour.intValue(),currentHour,4);
	    }
	    // set icon
		if((nextHour-currentHour)==1 && currentMinute>=30) {
			pillImageView.setImageResource(R.drawable.pill);
		}
		if(nextHour==24) nextHour = 0;
		String hourStr = (nextHour>10?nextHour+":00":"0"+nextHour+":00");
		scheduleTextView.setText(hourStr);
		
	    TextView routeTextView = (TextView) view.findViewById(R.id.route);
	    routeTextView.setText(medicationList.get(position).getRoute());
	    		
		return view;
	}
}
