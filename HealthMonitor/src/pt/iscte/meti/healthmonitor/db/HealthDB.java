package pt.iscte.meti.healthmonitor.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HealthDB extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "HealthMonitor";
    
    public static final String DB_TABLE_PATIENTS = "patients";
    public static final String DB_TABLE_HEALTH = "health";
    public static final String DB_TABLE_ID = "id";
    public static final String DB_TABLE_ID_PATIENTS = "idPatients";
    public static final String DB_TABLE_PATIENT_NAME = "name";
    public static final String DB_TABLE_BED_NUMBER = "bedNumber";
    public static final String DB_TABLE_BPM = "bpm";
    public static final String DB_TABLE_TEMP = "temp";
    public static final String DB_TABLE_DATE_TIME = "dateTime";
    
    
    private final String DB_TABLE_CREATE_PATIENTS = "CREATE TABLE " + DB_TABLE_PATIENTS + 
    		" (" + DB_TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DB_TABLE_ID_PATIENTS +" INT);";
    
    private final String DB_TABLE_CREATE_HEALTH = "CREATE TABLE " + DB_TABLE_HEALTH + 
    		" (" + DB_TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DB_TABLE_BPM + " INT, " + DB_TABLE_TEMP + " FLOAT, " + DB_TABLE_DATE_TIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " + DB_TABLE_ID_PATIENTS +" INT, " + DB_TABLE_BED_NUMBER + " INT, " + DB_TABLE_PATIENT_NAME + " VARCHAR(45));";
    HealthDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_TABLE_CREATE_PATIENTS);
        db.execSQL(DB_TABLE_CREATE_HEALTH);
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { 
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_PATIENTS);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_HEALTH);
	    onCreate(db);
	}
	
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_PATIENTS);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_HEALTH);
	    onCreate(db);
	}
}
