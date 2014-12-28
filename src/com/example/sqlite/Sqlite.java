package com.example.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Sqlite extends SQLiteOpenHelper {
	private static final int database_version = 1;

	public Sqlite(Context context, String databasename) {
		super(context, databasename, null, database_version);
	}

	//Create DB
	public void create( String table_name) {
		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "CREATE TABLE " + table_name
				+ " (POS text, ROUTES text, POINT integer);";
		db.execSQL(sql);
	}

	//Upgrade DB
	public void upgrade(String table_name) {
		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "DROP TABLE IF EXISTS " + table_name;
		db.execSQL(sql);
		create(table_name);
	}
	public void upgradeLine(String table_name) {
		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "DROP TABLE IF EXISTS " + table_name;
		db.execSQL(sql);
		String esql = "CREATE TABLE " + table_name
				+ " (LINE text);";
		db.execSQL(esql);
	}

	//DB Insert
	public long insert(String table_name,String pos, String routes,int index) {
		//System.out.println("**Table Name:"+table_name+"**POS:"+pos+"**Routes:"+routes+"**Point:"+index);
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("POS", pos);
		cv.put("ROUTES", routes);
		cv.put("POINT", index);
		long row = db.insert(table_name, null, cv);
		return row;
	}
	public long insertLine(String table_name,String routes) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("LINE", routes);
		long row = db.insert(table_name, null, cv);
		return row;
	}
	
	public String search_z(String table_name,String beginPoint,String endPoint){
		System.out.println("***"+beginPoint+"***"+endPoint);
		//SQLiteDatabase db = this.getWritableDatabase();
		//String sql = "DROP TABLE IF EXISTS " + table_name;
		//db.execSQL(sql);
		String line = "tmp";
		return line;
	}
	
	public String search_d(String table_name,String beginPoint,String endPoint){
		System.out.println("***"+beginPoint+"***"+endPoint);
		//SQLiteDatabase db = this.getWritableDatabase();
		//String sql = "DROP TABLE IF EXISTS " + table_name;
		//db.execSQL(sql);
		String line = "tmp";
		return line;
	}
	
	public String search_h(String table_name,String beginPoint,String endPoint){
		System.out.println("***"+beginPoint+"***"+endPoint);
		//SQLiteDatabase db = this.getWritableDatabase();
		//String sql = "DROP TABLE IF EXISTS " + table_name;
		//db.execSQL(sql);
		String line = "tmp";
		return line;
	}
	
	public void close(){
		SQLiteDatabase db = this.getWritableDatabase();
		db.close();
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
}