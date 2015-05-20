package it.unipd.dei.rilevatoredicadute;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.database.sqlite.SQLiteException;

public class MyDBManager{
	
	private MyDBHelper dbhelper;
	
	public MyDBManager(Context ctx){
		
		dbhelper=new MyDBHelper(ctx);
		//SQLiteDatabase db = dbhelper.getWritableDatabase();
	}
	
public void close(){
	
	dbhelper.close();	
	
	}
	

	public void addSessione(String nome, String data, String ora, String durata ,int ncadute){
		
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put(MyDBHelper.COL_NOME, nome);
		cv.put(MyDBHelper.COL_DATA, data);
		cv.put(MyDBHelper.COL_ORA, ora);
		cv.put(MyDBHelper.COL_DURATA, durata);
		cv.put(MyDBHelper.COL_NCADUTE, ncadute);						
		
		db.insert(MyDBHelper.TABLE_SESSIONE, null, cv);
	}
	
public void addCaduta(String data, String ora, String lat, String lon, String sessione){
		
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put(MyDBHelper.COL_DATAC, data);
		cv.put(MyDBHelper.COL_ORAC, ora);
		cv.put(MyDBHelper.COL_LAT, lat);
		cv.put(MyDBHelper.COL_LON, lon);
		cv.put(MyDBHelper.COL_SESS, sessione);
		
		db.insert(MyDBHelper.TABLE_CADUTA, null, cv);
	}

public void deleteSessione(int id){
	SQLiteDatabase db = dbhelper.getWritableDatabase();
	String string =String.valueOf(id);
	String delete = "DELETE FROM "+MyDBHelper.TABLE_SESSIONE+ " WHERE " +MyDBHelper.COL_IDS+ " =" +id;
	db.execSQL(delete);
	
}

public boolean renameSessione(String newName, int id)
{
   SQLiteDatabase db=dbhelper.getWritableDatabase();
   String RENAME_SESSIONE = "UPDATE" + MyDBHelper.TABLE_SESSIONE + "SET" + MyDBHelper.COL_NOME + "=" 
                            + newName + "WHERE"+ MyDBHelper.COL_IDS + "= ?" + id + "";
   db.execSQL(RENAME_SESSIONE);
   return true;
}



public Cursor selectSessione(){
    Cursor crs=null;
     try
     {
        SQLiteDatabase db=dbhelper.getReadableDatabase();
        crs=db.query(MyDBHelper.TABLE_SESSIONE, null, null, null, null, null, null, null);        
     }
     catch(SQLiteException sqle)
     {
       return null;
       }     
     return crs;
 }




 public Cursor selectCaduta()
 {
     Cursor crs=null;
     try
     {
         SQLiteDatabase db=dbhelper.getReadableDatabase();
         crs=db.query(MyDBHelper.TABLE_CADUTA, null, null, null, null, null, null, null);
         
     }
     catch(SQLiteException sqle)
     {
         return null;
     }     
     return crs;     
 }

	
	
}