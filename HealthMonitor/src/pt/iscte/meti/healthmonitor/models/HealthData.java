package pt.iscte.meti.healthmonitor.models;

public class HealthData {
	private long id;
	private int idPatients, bedNumber, bpm;
	private float temp;
	private String name,dateTime;
		
	  public long getId() {
	    return id;
	  }
	  public void setId(long id) {
	    this.id = id;
	  }

	  public int getIdPatients() {
	    return idPatients;
	  }
	  public void setIdPatients(int idPatients) {
		  this.idPatients = idPatients;
	  }
	  
	  public int getBedNumber() {
		  return bedNumber;
	  }
	  public void setBedNumber(int bedNumber) {
		  this.bedNumber = bedNumber;
	  }
	  
	  public String getName() {
		  return name;
	  }
	  public void setName(String name) {
		  this.name = name;
	  }
	  
	  public int getBpm() {
		  return bpm;
	  }
	  public void setBpm(int bpm) {
		  this.bpm = bpm;
	  }
	  
	  public float getTemp() {
		  return temp;
	  }
	  public void setTemp(float temp) {
		  this.temp = temp;
	  }
	  
	  public String getDateTime() {
		  return dateTime;
	  }
	  public void setDateTime(String dateTime) {
		  this.dateTime = dateTime;
	  }
}
