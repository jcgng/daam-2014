package pt.iscte.meti.healthmonitor.models;

import java.util.ArrayList;

import android.graphics.Bitmap;

// TODO: Make this class Parcelable

public class PatientData {
	private int idPatients;
	private String registerTimestamp;	
	private String name;
	private int age;	
	private char gender;	
	private int bedNumber;	
	private String deviceId;
	private Integer photoResource = null;
	private Bitmap photo = null;
	private String birthday; 
	private String diagnosis; 
	private String background;
	private ArrayList<MedicationData> medications;
	
	public PatientData(
			int idPatients,
			String registerTimestamp,	
			String name,
			int age,	
			char gender,	
			int bedNumber,	
			String deviceId,
			String birthday,
			String diagnosis,
			String background,
			ArrayList<MedicationData> medications) {
		
		this.idPatients=idPatients;
		this.registerTimestamp=registerTimestamp;	
		this.name=name;
		this.age=age;	
		this.gender=gender;	
		this.bedNumber=bedNumber;	
		this.deviceId=deviceId;
		this.birthday = birthday;
		this.diagnosis = diagnosis;
		this.background = background;
		this.medications = medications;
	}
	
	public int getId() {
		return idPatients;
	}
	public void setId(int id) {
		idPatients = id;
	}
	
	public String getRegisterTimestamp() {
		return this.registerTimestamp;
	}
	public void setRegisterTimestamp(String timestamp) {
		this.registerTimestamp = timestamp;
	}
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public int getAge() {
		return this.age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	
	public char getGender() {
		return this.gender;
	}
	public void setGender(char gender) {
		this.gender = gender;
	}
	
	public int getBed() {
		return this.bedNumber;
	}
	public void setBed(int bed) {
		this.bedNumber = bed;
	}
	
	public String getDeviceId() {
		return this.deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	public void setPhoto(Bitmap photo) {
		this.photo = photo;
	}
	public Bitmap getPhoto() {
		return photo;
	}
	
	public void setPhotoResource(int resource) {
		photoResource = resource;
	}
	public Integer getPhotoResource() {
		return photoResource;	
	}
	
	public String getBirthday() {
		return this.birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	
	public String getDiagnosis() {
		return this.diagnosis;
	}
	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}
	
	public String getBackground() {
		return this.background;
	}
	public void setBackground(String background) {
		this.background = background;
	}
	
	public ArrayList<MedicationData> getMedications() {
		return this.medications;
	}
	public void setMedications(ArrayList<MedicationData> medications) {
		this.medications = medications;
	}
}