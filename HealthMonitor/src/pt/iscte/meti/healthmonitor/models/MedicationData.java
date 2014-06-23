package pt.iscte.meti.healthmonitor.models;

import android.os.Parcel;
import android.os.Parcelable;

public class MedicationData implements Parcelable {
	public static enum SCHEDULES {
	    H24("24h/24h"),
	  	H12("12h/12h"),
	    H8("8h/8h"),
	    H6("6h/6h"),
	    H4("4h/4h");
	    
	    private final String schedule;       
	    private SCHEDULES(String str) { schedule = str; }
	    public String toString() { return schedule; }
	};
	
	private int idMedications;
	private String drug;
	private int dosage;
	private String units;
	private int patientDosage;
	private String schedule;
	private String route;
	
	public MedicationData(
			int idMedications,
			String drug,
			int dosage,
			String units,
			int patientDosage,
			String schedule,
			String route) {
		this.idMedications = idMedications;
		this.drug = drug;
		this.dosage = dosage;
		this.units = units;
		this.patientDosage = patientDosage;
		this.schedule = schedule;
		this.route= route; 
	}
	
	public MedicationData(Parcel parcel) {
		this.idMedications = parcel.readInt();
		this.drug = parcel.readString();
		this.dosage = parcel.readInt();
		this.units = parcel.readString();
		this.patientDosage = parcel.readInt(); 
		this.schedule = parcel.readString();
		this.route = parcel.readString();
	}
	
	public int getId() {
		return idMedications;
	}
	
	public String getDrug() {
		return drug;
	}
	
	public int getDosage() {
		return dosage;
	}
	
	public String getUnits() {
		return units;
	}
	
	public int getPatientDosage() { 
		return patientDosage; 
	}
	
	public String getSchedule() {
		return schedule;
	}
	
	public String getRoute() { 
		return route; 
	}

	public static final Creator<MedicationData> CREATOR = new Creator<MedicationData>() {
         public MedicationData createFromParcel(Parcel in) {
             return new MedicationData(in);
         }
         public MedicationData[] newArray (int size) {
             return new MedicationData[size];
         }
    };
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(idMedications);
	    parcel.writeString(drug);
	    parcel.writeInt(dosage);
	    parcel.writeString(units);
	    parcel.writeInt(patientDosage);
	    parcel.writeString(schedule);
	    parcel.writeString(route);
	}
}
