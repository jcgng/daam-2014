package pt.iscte.meti.healthmonitor.models;

import java.util.ArrayList;
import java.util.Calendar;

import pt.iscte.meti.healthmonitor.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

public class PatientListAdapter extends ArrayAdapter<PatientData> {
	Context context;
	private final int[] colors = new int[] { 0x30A3A3CC, 0x30CCCCFF };
	private ArrayList<PatientData> patientsList;
	private ArrayList<PatientData> allPatientsList = new ArrayList<PatientData>();
	private Filter filter;
	
	public PatientListAdapter(Context context, ArrayList<PatientData> patientsList) {
		super(context, R.layout.patient_list_layout, patientsList);
		this.patientsList = patientsList;
		this.allPatientsList.addAll(patientsList);
		this.context = context;
	}
	
	/**
	 * Based on:
	 * Android ListView with Custom Layout and Filter 
	 * http://www.mysamplecode.com/2012/07/android-listview-custom-layout-filter.html
	 */
	@SuppressLint("DefaultLocale")
	@Override public Filter getFilter() {
	   if(filter == null){
		   filter  = new Filter() {
				@Override 
				protected FilterResults performFiltering(CharSequence constraint) {
					String search = constraint.toString();
				    FilterResults result = new FilterResults();
				    if(search != null && search.length() > 0) {
				    	ArrayList<PatientData> filteredList = new ArrayList<PatientData>();
					    for(int i = 0, l = allPatientsList.size(); i < l; i++) {
				    		PatientData patient = allPatientsList.get(i);
				    		if(patient.getName().toLowerCase().indexOf(search.toLowerCase())==0) {
				    			filteredList.add(patient);
					    	}
				    		synchronized(this) {
				    			result.count = filteredList.size();
				    			result.values = filteredList;
				    		}
					    }
				    } else {
				    	synchronized(this) {
				    		result.values = allPatientsList;
				    		result.count = allPatientsList.size();
				    	}
				    }
				    return result;
				}

				@SuppressWarnings("unchecked")
				@Override protected void publishResults(CharSequence constraint, FilterResults results) {
					patientsList = (ArrayList<PatientData>)results.values;
				    notifyDataSetChanged();
				    clear();
				    for(int i = 0, l = patientsList.size(); i < l; i++) 
				    	add(patientsList.get(i));
				    
				    notifyDataSetInvalidated();
				}
		   };
	   }
	   return filter;
	}
	/*** ***/
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.patient_list_layout, parent, false);
		
	    if(position%2 == 0)
			view.setBackgroundColor(this.colors[0]);
		else 
			view.setBackgroundColor(this.colors[1]);
		
		ImageView imageView = (ImageView) view.findViewById(R.id.patientPic);
		if(patientsList.get(position).getPhoto()!=null) {
			imageView.setImageBitmap(patientsList.get(position).getPhoto());
		} else {
			imageView.setImageResource(patientsList.get(position).getPhotoResource());
		}
	    
	    TextView nameTextView = (TextView) view.findViewById(R.id.patientName);
	    nameTextView.setText(patientsList.get(position).getName() + "\nBed: " + patientsList.get(position).getBed());
	
	    ImageView pillImageView = (ImageView) view.findViewById(R.id.pillIcon);
	    ArrayList<MedicationData> medications =  patientsList.get(position).getMedications();    
	    Calendar calendar = Calendar.getInstance();
	    int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
	    int currentMinute = calendar.get(Calendar.MINUTE);
	    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
	    Integer startHour = Integer.parseInt(settings.getString("meds_start_hour",context.getResources().getString(R.string.pref_default_meds_starthour)));
	    for(MedicationData medication : medications) { 
	    	String schedule = medication.getSchedule(); 
	    	int nextHour = currentHour;
	    	if(schedule.equals(MedicationData.SCHEDULES.H24.toString())) {
	    		nextHour = medication.nextHour(startHour.intValue(),currentHour,24);
		    } else if(schedule.equals(MedicationData.SCHEDULES.H12.toString())) {
		    	nextHour = medication.nextHour(startHour.intValue(),currentHour,12);
		    } else if(schedule.equals(MedicationData.SCHEDULES.H8.toString())) {
		    	nextHour = medication.nextHour(startHour.intValue(),currentHour,8);
		    } else if(schedule.equals(MedicationData.SCHEDULES.H6.toString())) {
		    	nextHour = medication.nextHour(startHour.intValue(),currentHour,6);
		    } else if(schedule.equals(MedicationData.SCHEDULES.H4.toString())) {
		    	nextHour = medication.nextHour(startHour.intValue(),currentHour,4);
		    }
	    	// set icon
    		if((nextHour-currentHour)==1 && currentMinute>=30) {
    			pillImageView.setImageResource(R.drawable.pill);
    			break;
    		}
	    }	    
		return view;
    }
	
	public ArrayList<PatientData> getAllPatientsList() {
		return allPatientsList;
	}
}
