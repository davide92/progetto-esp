package it.unipd.dei.rilevatoredicadute;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MyDBHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 14;
	private static final String DATABASE_NAME = "progettoDB.db";
	public static final String TABLE_SESSIONE = "Sessione";
	public static final String TABLE_CADUTA="Caduta";	

	public static final String COL_IDS = "_id";
	public static final String COL_NOME = "Nome";
	public static final String COL_DATA = "DataInizio";
	public static final String COL_ORA="OraInizio";
    public static final String COL_DURATA = "Durata";
    public static final String COL_NCADUTE = "NCadute";
    public static final String COL_COLOR = "Colore";
    public static final String COL_STATO = "Stato";
    public static final String COL_TEMPOPAUSA = "TempoPausa";

	
    public static final String COL_IDC="_id";
    public static final String COL_DATAC = "DataCaduta";
    public static final String COL_ORAC = "OraCaduta";
    public static final String COL_LAT = "Latitudine";
    public static final String COL_LON = "Longitudine";
    public static final String COL_SESS = "NSessione"; 
	
        		
	public MyDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_SESSIONE_TABLE = "CREATE TABLE "
	                + TABLE_SESSIONE + "("	                
             		+ COL_IDS        +   " INTEGER PRIMARY KEY AUTOINCREMENT, " 
             		+ COL_NOME 	     +   " TEXT UNIQUE NOT NULL, "
             		+ COL_DATA       +   " TEXT NOT NULL, "
	                + COL_ORA        +   " TEXT NOT NULL, " 
             		+ COL_DURATA     +   " TEXT NOT NULL, "  
		            + COL_NCADUTE    +   " INTEGER NOT NULL, "
		            + COL_COLOR		 +	 " INTEGER DEFAULT 0, "
		            + COL_STATO      +   " INTEGER NOT NULL, "
		            + COL_TEMPOPAUSA +   " BIGINT NOT NULL, "
		            + " CHECK (" +COL_IDS+ ">0)" + ");";
		
      		db.execSQL(CREATE_SESSIONE_TABLE);

		
		String CREATE_CADUTA_TABLE = "CREATE TABLE " +
             		TABLE_CADUTA + "("
             		+ COL_IDC 			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
             		+ COL_DATAC 		+ " TEXT NOT NULL, "
             		+ COL_ORAC 			+ " TEXT UNIQUE NOT NULL, "
	                + COL_LAT 			+ " TEXT NOT NULL, " 
             		+ COL_LON 			+ " TEXT NOT NULL, " 
             		+ COL_SESS 			+ " TEXT NOT NULL " + ");";
      		db.execSQL(CREATE_CADUTA_TABLE);
	}



	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSIONE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CADUTA);
      		onCreate(db);
	}
	
	

}