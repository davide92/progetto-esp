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
		
	}
	
	public void close(){	
		dbhelper.close();	
	}	
	//aggiunta di una nuova sessione nel database
	public void aggSessione(String nome, String data, String ora, String durata ,int ncadute, int col, int stato, long tempoPausa){
			
			SQLiteDatabase db = dbhelper.getWritableDatabase();
			
			ContentValues cv = new ContentValues();
			cv.put(MyDBHelper.COL_NOME, nome);
			cv.put(MyDBHelper.COL_DATA, data);
			cv.put(MyDBHelper.COL_ORA, ora);
			cv.put(MyDBHelper.COL_DURATA, durata);
			cv.put(MyDBHelper.COL_NCADUTE, ncadute);
			cv.put(MyDBHelper.COL_COLOR, col);
			cv.put(MyDBHelper.COL_STATO, stato);
			cv.put(MyDBHelper.COL_TEMPOPAUSA, tempoPausa );
			
			db.insert(MyDBHelper.TABLE_SESSIONE, null, cv);
		}
	//aggiunta di una caduta relativa a una sessione assegnata	
	public void aggCaduta(String data, String ora, String lat, String lon, String sessione){
			
			SQLiteDatabase db = dbhelper.getWritableDatabase();
			
			ContentValues cv = new ContentValues();
			cv.put(MyDBHelper.COL_DATAC, data);
			cv.put(MyDBHelper.COL_ORAC, ora);
			cv.put(MyDBHelper.COL_LAT, lat);
			cv.put(MyDBHelper.COL_LON, lon);
			cv.put(MyDBHelper.COL_SESS, sessione);
			
			db.insert(MyDBHelper.TABLE_CADUTA, null, cv);
		}
	//cancellazione di una sessione
	public void cancSessione(String nome_sessione){
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		String delete = "DELETE FROM "+MyDBHelper.TABLE_SESSIONE+ " WHERE " +MyDBHelper.COL_NOME+ " = '" +nome_sessione+"';";
		db.execSQL(delete);	
	}
	//aggiornamento della durata di una sessione
	public void aggiornaDurataSessione(String newDurata, String nameSessione){
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		String update = "UPDATE "+MyDBHelper.TABLE_SESSIONE+" SET "+MyDBHelper.COL_DURATA+" = '"+newDurata+"' WHERE "+MyDBHelper.COL_NOME+" = '"+nameSessione+"';";
		db.execSQL(update);
	}
	//aggiornamento della durata della sessione pausata
	public void inserireTempoPausaSessione(String nomeSessione, long tempoP){
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		String update = "UPDATE "+MyDBHelper.TABLE_SESSIONE+" SET "+MyDBHelper.COL_TEMPOPAUSA+" = '"+tempoP+"' WHERE "+MyDBHelper.COL_NOME+" = '"+nomeSessione+"';";
		db.execSQL(update);
	}
	//aggiornamento dello stato della sessione
	public void aggiornaStatoSessione(int statoP, String nomeSessioneP){
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		String update = "UPDATE "+MyDBHelper.TABLE_SESSIONE+" SET "+MyDBHelper.COL_STATO+" = '"+statoP+"' WHERE "+MyDBHelper.COL_NOME+" = '"+nomeSessioneP+"';";
		db.execSQL(update);
	}
	//ESEGUE LA SELEZIONE DI TUTTE LE SEZIONICON RELATIVE INFORMAZIONI
	public Cursor selezTutteSessioni(){
	    Cursor crs=null;
	     try
	     {
	        SQLiteDatabase db = dbhelper.getReadableDatabase();
	        crs = db.query(MyDBHelper.TABLE_SESSIONE, null, null, null, null, null, null, null);        
	     }
	     catch(SQLiteException sqle)
	     {
	       return null;
	       }     
	     return crs;
	 }
	
	//ESEGUE LA SELEZIONE SULLA SESSIONE PASSATA PER PARAMETRO
	public Cursor selezSessione(String nameSession){
	  Cursor crs = null;
	  String selectColumns[] = new String[]{""+MyDBHelper.COL_NOME+"",""+MyDBHelper.COL_DATA+"",""+MyDBHelper.COL_ORA+"",""+MyDBHelper.COL_DURATA+"", ""+MyDBHelper.COL_NCADUTE+"",""+MyDBHelper.COL_COLOR+""};
	  String whereClause = ""+MyDBHelper.COL_NOME+"= ?" ;
	  String whereArgs[] = new String[]{""+nameSession+""};
	  
	  try
	   {
	      SQLiteDatabase db = dbhelper.getReadableDatabase();
	      crs = db.query(MyDBHelper.TABLE_SESSIONE, selectColumns, whereClause, whereArgs, null, null, null, null);        
	   }
	   catch(SQLiteException sqle)
	   {
	     return null;
	    }     
	   return crs;
	}
	
	//esegue la selezione delle cadute sulla sezione passata per parametro
	 public Cursor selezCaduta(String nameSession)
	 {
	     Cursor crs=null;
	     String selectColumns[] = new String[]{""+MyDBHelper.COL_DATAC+"",""+MyDBHelper.COL_ORAC+"",""+MyDBHelper.COL_LAT+"",""+MyDBHelper.COL_LON+""};
	     String whereClause = ""+MyDBHelper.COL_SESS+"= ?" ;
	     String whereArgs[] = new String[]{""+nameSession+""};
	     
	     try
	     {
	         SQLiteDatabase db = dbhelper.getReadableDatabase();
	         crs = db.query(MyDBHelper.TABLE_CADUTA, selectColumns, whereClause, whereArgs, null, null, null, null);
	         
	     }
	     catch(SQLiteException sqle)
	     {
	         return null;
	     }     
	     return crs;     
	 }
	 //selezione di tutte le cadute
	 public Cursor selezTutteCadute(){
		   Cursor crs = null;
		    try
		     {
		        SQLiteDatabase db = dbhelper.getReadableDatabase();
		        crs = db.query(MyDBHelper.TABLE_CADUTA, null, null, null, null, null, null, null);        
		     }
		     catch(SQLiteException sqle)
			 {
		 	       return null;
		     }     
		    return crs;
		  }
	 //conta il numero di sessioni salvate nel database	 
	 public int contaSessioni(){	 
			
		 SQLiteDatabase db = dbhelper.getReadableDatabase();
		 Cursor mCount = db.rawQuery("select count(*) from "+MyDBHelper.TABLE_SESSIONE+";", null);
		 mCount.moveToFirst();
		 int count = mCount.getInt(0);
		 mCount.close();
		 return count;
	 }
	 //conta numero di cadute per ogni sessione
	 public int contaCadute(String nameSession){	 
		
		 SQLiteDatabase db = dbhelper.getReadableDatabase();
		 Cursor mCount = db.rawQuery("select count(*) from "+MyDBHelper.TABLE_CADUTA+ " where " +MyDBHelper.COL_SESS+ " = '" +nameSession+"';", null);
		 mCount.moveToFirst();
		 int count = mCount.getInt(0);
		 mCount.close();
		 return count;
	 }
	//metodo per trovare il primo numero disponibile per il nome della sessione 
	 public boolean noSessStessoNome(String session){
		 SQLiteDatabase database = dbhelper.getReadableDatabase();
		 String sql = "select count(*) from "+MyDBHelper.TABLE_SESSIONE+ " where "+MyDBHelper.COL_NOME+ " = '"+ session +"';" ; 
		 Cursor cur = database.rawQuery(sql, null);
		 cur.moveToFirst();
		 int count = cur.getInt(0);
		 cur.close();
		 if(count == 0)
			 return true;
		 return false;
	 }
	 //metodo per non avere più di una sessione nello stesso momento
	 public boolean noCaduteStessaOra(String session, String hour){
		 SQLiteDatabase database = dbhelper.getReadableDatabase();
		 String sql = "select count(*) from "+MyDBHelper.TABLE_CADUTA+ " where "+MyDBHelper.COL_SESS+ " = '"+ session +"' and " + MyDBHelper.COL_ORAC + " = '"+hour+"';" ; 
		 Cursor cur = database.rawQuery(sql, null);
		 cur.moveToFirst();
		 int count = cur.getInt(0);
		 cur.close();
		 if(count == 0)
			 return true;
		 return false;
	 }
	 //selezione della caduta di una sessione e avvenuta in una determinata ora
	 public Cursor selezCadutaConOra(String nameSession, String hour)
	 	 {
	 	     Cursor crs=null;
	 	     String selectColumns[] = new String[]{""+MyDBHelper.COL_DATAC+"",""+MyDBHelper.COL_LAT+"",""+MyDBHelper.COL_LON+""};
	 	     String whereClause = ""+MyDBHelper.COL_SESS+"= ?"+" AND "+MyDBHelper.COL_ORAC + "= ?";
	 	     String whereArgs[] = new String[]{""+nameSession+"", ""+ hour+""};
	  	
	 	     try
	 	     {
	 	         SQLiteDatabase db = dbhelper.getReadableDatabase();
	 	         crs = db.query(MyDBHelper.TABLE_CADUTA, selectColumns, whereClause, whereArgs, null, null, null, null);
	 	         
	 	     }
	 	     catch(SQLiteException sqle)
	 	     {
	 	         return null;
	 	     }     
	 	     return crs;     
	 	 }
	 	//cancellare una caduta di una determinata sessione e ora
	 	public void cancCaduta(String nameSession, String hour){
	 		SQLiteDatabase db = dbhelper.getWritableDatabase();
	 		String delete = "DELETE FROM "+MyDBHelper.TABLE_CADUTA+ " WHERE " +MyDBHelper.COL_SESS+ " = '" +nameSession+ "' AND " + MyDBHelper.COL_ORAC + " = '"+ hour + "';";
	 		db.execSQL(delete);	
	 	}
	 	//cazncella tute le cadute di una sessione
	 	public void cancCadute(String nameSession){
	 		SQLiteDatabase db = dbhelper.getWritableDatabase();
	 		String delete = "DELETE FROM "+MyDBHelper.TABLE_CADUTA+ " WHERE " +MyDBHelper.COL_SESS+ " = '" +nameSession+ "';";
	 		db.execSQL(delete);	
	 	}	
}