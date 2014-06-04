package pt.iscte.meti.healthmonitor.models;

import java.util.ArrayList;
import java.util.Calendar;

import pt.iscte.meti.healthmonitor.R;

import android.content.Context;
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
	    
	    // TODO: Add this to MedicationScheduleTask
	    TextView scheduleTextView = (TextView) view.findViewById(R.id.schedule);
	    ImageView pillImageView = (ImageView) view.findViewById(R.id.pillIcon);
	    String schedule = medicationList.get(position).getSchedule();
	    Calendar calendar = Calendar.getInstance();
	    int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
	    int currentMinute = calendar.get(Calendar.MINUTE);
	    if(schedule.equals(MedicationData.SCHEDULES.H24.toString())) {
	    	scheduleTextView.setText("08:00");
	    	// set icon
    		if(currentHour<8 && currentHour>=7 && currentMinute>=30)
    			pillImageView.setImageResource(R.drawable.pill);
	    } else if(schedule.equals(MedicationData.SCHEDULES.H12.toString())) {
	    	if(currentHour<8) {
	    		scheduleTextView.setText("08:00");
	    		// set icon
	    		if(currentHour>=7 && currentMinute>=30)
	    			pillImageView.setImageResource(R.drawable.pill);
	    	} else {
	    		scheduleTextView.setText("16:00");
	    		// set icon
	    		if(currentHour>=15 && currentMinute>=30)
	    			pillImageView.setImageResource(R.drawable.pill);
	    	}
	    } else if(schedule.equals(MedicationData.SCHEDULES.H8.toString())) {
	    	if(currentHour<8) {
	    		scheduleTextView.setText("08:00");
		    	// set icon
	    		if(currentHour>=7 && currentMinute>=30)
	    			pillImageView.setImageResource(R.drawable.pill);
	    	} else if(currentHour<16) {
	    		scheduleTextView.setText("16:00");
	    		// set icon
	    		if(currentHour>=15 && currentMinute>=30)
	    			pillImageView.setImageResource(R.drawable.pill);
	    	} else if(currentHour<=23) {
	    		scheduleTextView.setText("00:00");
	    		// set icon
	    		if(currentHour>=23 && currentMinute>=30)
	    			pillImageView.setImageResource(R.drawable.pill);
	    	}
	    } else if(schedule.equals(MedicationData.SCHEDULES.H6.toString())) {
	    	if(currentHour<2 || currentHour>=20) {
	    		scheduleTextView.setText("02:00");
		    	// set icon
	    		if(currentHour>=1 && currentMinute>=30)
	    			pillImageView.setImageResource(R.drawable.pill);
	    	} else if(currentHour<8) {
	    		scheduleTextView.setText("08:00");
	    		// set icon
	    		if(currentHour>=7 && currentMinute>=30)
	    			pillImageView.setImageResource(R.drawable.pill);
	    	} else if(currentHour<14) {
	    		scheduleTextView.setText("14:00");
	    		// set icon
	    		if(currentHour>=13 && currentMinute>=30)
	    			pillImageView.setImageResource(R.drawable.pill);
	    	} else if(currentHour<20) {
	    		scheduleTextView.setText("20:00");
	    		// set icon
	    		if(currentHour>=19 && currentMinute>=30)
	    			pillImageView.setImageResource(R.drawable.pill);
	    	}
	    } else if(schedule.equals(MedicationData.SCHEDULES.H4.toString())) {
	    	if(currentHour<4) {
	    		scheduleTextView.setText("04:00");
		    	// set icon
	    		if(currentHour>=3 && currentMinute>=30)
	    			pillImageView.setImageResource(R.drawable.pill);
	    	} else if(currentHour<8) {
	    		scheduleTextView.setText("08:00");
		    	// set icon
				if(currentHour>=7 && currentMinute>=30)
					pillImageView.setImageResource(R.drawable.pill);
	    	} else if(currentHour<12) {
	    		scheduleTextView.setText("12:00");
		    	// set icon
	    		if(currentHour>=11 && currentMinute>=30)
	    			pillImageView.setImageResource(R.drawable.pill);
	    	} else if(currentHour<16) { 
	    		scheduleTextView.setText("16:00");
		    	// set icon
	    		if(currentHour>=15 && currentMinute>=30)
	    			pillImageView.setImageResource(R.drawable.pill);
	    	} else if(currentHour<20) { 
	    		scheduleTextView.setText("20:00");
		    	// set icon
	    		if(currentHour>=19 && currentMinute>=30)
	    			pillImageView.setImageResource(R.drawable.pill);
	    	} else if(currentHour<=23) { 
	    		scheduleTextView.setText("00:00");
		    	// set icon
	    		if(currentHour>=23 && currentMinute>=30)
	    			pillImageView.setImageResource(R.drawable.pill);
	    	}
	    }
	    
	    TextView routeTextView = (TextView) view.findViewById(R.id.route);
	    routeTextView.setText(medicationList.get(position).getRoute());
	    		
		return view;
	}
}
