package pt.iscte.meti.healthmonitor.db;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.meti.healthmonitor.models.HealthData;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class HealthDS {
	  private SQLiteDatabase database;
	  private HealthDB healthDb;
	  
	  private String[] columnsPatients = { 
			  HealthDB.DB_TABLE_ID,
		      HealthDB.DB_TABLE_ID_PATIENTS};
	  
	  private String[] columnsHealth = { 
		  HealthDB.DB_TABLE_ID,
	      HealthDB.DB_TABLE_BPM,
	      HealthDB.DB_TABLE_TEMP,
	      HealthDB.DB_TABLE_DATE_TIME,
	      HealthDB.DB_TABLE_ID_PATIENTS,
	      HealthDB.DB_TABLE_BED_NUMBER,
	      HealthDB.DB_TABLE_PATIENT_NAME };

	  public HealthDS(Context context) {
		  healthDb = new HealthDB(context);
	  }

	  public void open() throws SQLException {
		  database = healthDb.getWritableDatabase();
	  }

	  public void close() {
		  healthDb.close();
	  }

	  public boolean addPatient(int idPatients) {
		  ContentValues values = new ContentValues();
		  values.put(columnsPatients[1], idPatients);
		  
		  long id = database.replace(HealthDB.DB_TABLE_PATIENTS, null, values);
		  return (id<0?false:true);
	  }
	  
	  public List<Integer> getAllPatients() {
		List<Integer> patients = new ArrayList<Integer>();
			
		Cursor cursor = database.query(HealthDB.DB_TABLE_PATIENTS, columnsPatients, null, null, null, null, null);
			
		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
		  patients.add(cursor.getInt(1));
		  cursor.moveToNext();
		}
		cursor.close();
		return patients;
	  }
	  
	  public void cleanPatients() {
		  database.delete(HealthDB.DB_TABLE_PATIENTS, "1", null);
	  }
	  
	  /**
	   * Add health value to database table
	   * 
	   * @param healthData
	   * @return 0 success, 1 already exists, -1 error
	   */
	  public int addHealth(HealthData healthData) {
		  // check last dateTime
		  HealthData lastHealthData = getLastHealth(healthData.getIdPatients());
		  if(lastHealthData!=null && healthData.getDateTime().equals(lastHealthData.getDateTime()))
			  return 1;
		  
		  // insert
		  ContentValues values = new ContentValues();
		  values.put(columnsHealth[1], healthData.getBpm());
		  values.put(columnsHealth[2], healthData.getTemp());
		  values.put(columnsHealth[3], healthData.getDateTime());
		  values.put(columnsHealth[4], healthData.getIdPatients());
		  values.put(columnsHealth[5], healthData.getBedNumber());
		  values.put(columnsHealth[6], healthData.getName());
		  long id = database.insert(HealthDB.DB_TABLE_HEALTH, null, values);
		  
		  if(id<0) {
			  return -1;
		  } else {
			  // check size
			  List<HealthData> healthList = getHealth(healthData.getIdPatients());
			  // keep last 10 values
			  if(healthList.size()>10)
				  deleteHealth(healthList.get(0)); // remove first

			  return 0;
		  }
	  }

	  public void deleteHealth(HealthData healthData) {
		  String id = healthData.getId() + "";
		  String whereClause = HealthDB.DB_TABLE_ID + " = ?" + id;
		  String[] whereArgs = new String[] { id };
		  database.delete(HealthDB.DB_TABLE_HEALTH, whereClause, whereArgs);
	  }

	  public void deletePatientHealth(HealthData healthData) {
		  String id = healthData.getIdPatients() + "";
		  String whereClause = HealthDB.DB_TABLE_ID_PATIENTS + " = ?" + id;
		  String[] whereArgs = new String[] { id };
		  database.delete(HealthDB.DB_TABLE_HEALTH, whereClause, whereArgs);
	  }
	 	  
	  public List<HealthData> getHealth(Integer idPatients) {
	    List<HealthData> healthData = new ArrayList<HealthData>();

    	String whereClause = HealthDB.DB_TABLE_ID_PATIENTS + " = ? ";
    	String[] whereArgs = new String[] {
			idPatients.toString() 
		};
    	String orderBy = columnsHealth[3];
	    	
	    Cursor cursor = database.query(HealthDB.DB_TABLE_HEALTH, columnsHealth, whereClause, whereArgs, null, null, orderBy);

	    cursor.moveToFirst();
	    while(!cursor.isAfterLast()) {
	      HealthData data = cursorToHealthData(cursor);
	      healthData.add(data);
	      cursor.moveToNext();
	    }
	    cursor.close();
	    return healthData;
	  }

	  public HealthData getLastHealth(Integer idPatients) {
		  Cursor cursor = database.rawQuery("SELECT * FROM " + HealthDB.DB_TABLE_HEALTH + " WHERE idPatients = " + idPatients.toString() + " ORDER BY " + columnsHealth[3] + " DESC LIMIT 1", null);
		  HealthData healthData = null;
		  cursor.moveToFirst();
		  while(!cursor.isAfterLast()) {
			  healthData = cursorToHealthData(cursor);
			  cursor.moveToNext();
		  }
		  cursor.close();
		  return healthData;
	  }
	  
	  private HealthData cursorToHealthData(Cursor cursor) {
	    HealthData healthData = new HealthData();
	    healthData.setId(cursor.getLong(0));
	    healthData.setBpm(cursor.getInt(1));
	    healthData.setTemp(cursor.getFloat(2));
	    healthData.setDateTime(cursor.getString(3));
	    healthData.setIdPatients(cursor.getInt(4));
	    return healthData;
	  }
}
