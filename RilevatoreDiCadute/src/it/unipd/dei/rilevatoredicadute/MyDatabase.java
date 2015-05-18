package it.unipd.dei.rilevatoredicadute;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDatabase {
	static class SessioneMetaData{
        static final String SESSIONE_TABLE="Sessione";
        static final String IDS="_id";
        static final String KEY_NOME="NomeSessione";
        static final String KEY_DATA = "DataInizio";
        static final String KEY_ORA = "OraInizio";
        static final String KEY_DURATA = "Durata";
        static final String KEY_NFALLS = "nCadute"; 
 }


    static class CadutaMetaData{
        static final String CADUTA_TABLE="Caduta";
        static final String IDC="_id";
        static final String KEY_DATAC = "DataCaduta";
        static final String KEY_ORAC = "OraCaduta";
        static final String KEY_LAT = "Latitudine";
        static final String KEY_LON = "Longitudine";
        static final String KEY_SESS = "NSessione"; 
        
        
    }

        private static final String TAG = "MyDatabase";       
        private static final String DATABASE_NAME = "CadDatabase.db";    
        private static final int DATABASE_VERSION = 1;

        private static final String SESSIONE_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "  //codice sql di creazione della tabella sessione
                + SessioneMetaData.SESSIONE_TABLE + " (" 
                + SessioneMetaData.IDS+ " integer primary key autoincrement, "
                + SessioneMetaData.KEY_NOME + " text not null, "
                + SessioneMetaData.KEY_DATA + " text not null,"
                + SessioneMetaData.KEY_ORA + " text not null,"
                + SessioneMetaData.KEY_DURATA + " text not null,"
                + SessioneMetaData.KEY_NFALLS + " text not null);";


        private static final String CADUTA_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "  //codice sql di creazione della tabella caduta
                + CadutaMetaData.CADUTA_TABLE + " (" 
                + CadutaMetaData.IDC+ " integer primary key autoincrement, "
                + CadutaMetaData.KEY_DATAC + " text not null, "
                + CadutaMetaData.KEY_ORAC + " text not null,"
                + CadutaMetaData.KEY_LAT + " text not null,"
                + CadutaMetaData.KEY_LON + " text not null,"
                + CadutaMetaData.KEY_SESS + " text not null);";/*, FOREIGN KEY("+CadutaMetaData.KEY_SESS+"REFERENCES "+SessioneMetaData.SESSIONE_TABLE +"("+SessioneMetaData.IDS+") ));";*/


        private Context context;
        private DatabaseHelper DBHelper;
        private SQLiteDatabase db;
        

        public MyDatabase(Context ctx) 
        {
        	
            this.context = ctx;
            DBHelper = new DatabaseHelper(context);
        }

        private static class DatabaseHelper extends SQLiteOpenHelper 
        {
            DatabaseHelper(Context context) 
            {
                super(context, DATABASE_NAME, null, DATABASE_VERSION);
            }

            @Override
            public void onCreate(SQLiteDatabase db) 
            {
            	db.execSQL(SESSIONE_TABLE_CREATE);
                db.execSQL(CADUTA_TABLE_CREATE);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, 
                                  int newVersion) 
            {
                Log.w(TAG, "Upgrading database from version " + oldVersion 
                      + " to "
                      + newVersion + ", which will destroy all old data");
                db.execSQL("DROP TABLE IF EXISTS Employee");
                onCreate(db);
            }
        }


        public MyDatabase open() throws SQLException 
        {
            db = DBHelper.getWritableDatabase();
            return this;
        }

        //---closes the database---    
        public void close() 
        {
            DBHelper.close();
        }


        public void insertSessione(String name, String date, String hour, String duration, String nfalls){
            ContentValues cv=new ContentValues();
            cv.put(SessioneMetaData.KEY_NOME, name);
            cv.put(SessioneMetaData.KEY_DATA, date);
            cv.put(SessioneMetaData.KEY_ORA, hour);
            cv.put(SessioneMetaData.KEY_DURATA, duration);
            cv.put(SessioneMetaData.KEY_NFALLS, nfalls);
            db.insert(SessioneMetaData.SESSIONE_TABLE, null, cv);
      }

      
      public void insertCaduta(String date, String hour, String lat, String lon, String sess){
            ContentValues cvc=new ContentValues();
            
            cvc.put(CadutaMetaData.KEY_DATAC, date);
            cvc.put(CadutaMetaData.KEY_ORAC, hour);
            cvc.put(CadutaMetaData.KEY_LAT, lat);
            cvc.put(CadutaMetaData.KEY_LON, lon);
            cvc.put(CadutaMetaData.KEY_SESS, sess);
            db.insert(CadutaMetaData.CADUTA_TABLE, null, cvc);
      }
      
      public Cursor fetchSessioni(){
          return db.query(SessioneMetaData.SESSIONE_TABLE,new String[] { SessioneMetaData.IDS, SessioneMetaData.KEY_NOME, SessioneMetaData.KEY_DATA, SessioneMetaData.KEY_ORA, SessioneMetaData.KEY_DURATA, SessioneMetaData.KEY_NFALLS},null, null, null, null, null);
   }

}