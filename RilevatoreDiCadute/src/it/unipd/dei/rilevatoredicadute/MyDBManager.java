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

public void deleteSessione(String nome_sessione){
	SQLiteDatabase db = dbhelper.getWritableDatabase();
	String delete = "DELETE FROM "+MyDBHelper.TABLE_SESSIONE+ " WHERE " +MyDBHelper.COL_NOME+ " = '" +nome_sessione+"';";
	db.execSQL(delete);	
}

public void updateDurataSessione(String newDurata, String nameSessione){
	SQLiteDatabase db = dbhelper.getWritableDatabase();
	String update = "UPDATE "+MyDBHelper.TABLE_SESSIONE+" SET "+MyDBHelper.COL_DURATA+" = '"+newDurata+"' WHERE "+MyDBHelper.COL_NOME+" = '"+nameSessione+"';";
	db.execSQL(update);
}


//ESEGUE IL SELECT SU TUTTE LE SESSIONI
public Cursor selectAllSessions(){
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

//ESEGUE IL SELECT SULLA SESSIONE PASSATA PER PARAMETRO
public Cursor selectSession(String nameSession){
  Cursor crs=null;
  String selectColumns[] = new String[]{""+MyDBHelper.COL_NOME+"",""+MyDBHelper.COL_DATA+"",""+MyDBHelper.COL_ORA+"",""+MyDBHelper.COL_DURATA+"", ""+MyDBHelper.COL_NCADUTE+""};
  String whereClause = ""+MyDBHelper.COL_NOME+"= ?" ;
  String whereArgs[] = new String[]{""+nameSession+""};
  
  try
   {
      SQLiteDatabase db=dbhelper.getReadableDatabase();
      crs=db.query(MyDBHelper.TABLE_SESSIONE, selectColumns, whereClause, whereArgs, null, null, null, null);        
   }
   catch(SQLiteException sqle)
   {
     return null;
     }     
   return crs;
}


 public Cursor selectCaduta(String nameSession)
 {
     Cursor crs=null;
     String selectColumns[] = new String[]{""+MyDBHelper.COL_DATAC+"",""+MyDBHelper.COL_ORAC+"",""+MyDBHelper.COL_LAT+"",""+MyDBHelper.COL_LON+""};
     String whereClause = ""+MyDBHelper.COL_SESS+"= ?" ;
     String whereArgs[] = new String[]{""+nameSession+""};
     
     try
     {
         SQLiteDatabase db=dbhelper.getReadableDatabase();
         crs=db.query(MyDBHelper.TABLE_CADUTA, selectColumns, whereClause, whereArgs, null, null, null, null);
         
     }
     catch(SQLiteException sqle)
     {
         return null;
     }     
     return crs;     
 }
 
 public int CountCaduta(String nameSession){	 
	
	 SQLiteDatabase db = dbhelper.getReadableDatabase();
	 Cursor mCount= db.rawQuery("select count(*) from "+MyDBHelper.TABLE_CADUTA+ " where " +MyDBHelper.COL_SESS+ " = '" +nameSession+"';", null);
	 mCount.moveToFirst();
	 int count= mCount.getInt(0);
	 mCount.close();
	 return count;
 }
 
 /*public void numberCaduta(){
	 SQLiteDatabase db=dbhelper.getReadableDatabase();
	 String numeroCaduta="SELECT COUNT( " +MyDBHelper.COL_IDC+ " FROM " +MyDBHelper.TABLE_CADUTA+ " );";
	 db.execSQL(numeroCaduta);
	 //return Integer.parseInt(numeroCaduta);
 }*/

	
	
}