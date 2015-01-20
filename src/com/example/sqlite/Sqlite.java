package com.example.sqlite;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Sqlite extends SQLiteOpenHelper {
	private static final int database_version = 1;
	private SQLiteDatabase db;
	
	public Sqlite(Context context, String databasename) {
		super(context, databasename, null, database_version);
	}

	//Create DB
	public void create_user(){
		db = this.getWritableDatabase();
		//暂时直接消除原有用户
		//待优化，检测表格是否存在，存在不执行该步操作
		//String sql = "DROP TABLE IF EXISTS user";
		//db.execSQL(sql);
		String sql = "CREATE TABLE if not exists user (NAME varchar, PWD varchar);";
		db.execSQL(sql);
	}
	//Upgrade DB
	public void upgrade(String table_name) {
		db = this.getWritableDatabase();
		String sql = "DROP TABLE IF EXISTS " + table_name;
		db.execSQL(sql);
		sql = "DROP VIEW IF EXISTS RouteT0_" + table_name;
		db.execSQL(sql);
		String esql = "CREATE TABLE " + table_name
				+ " (POS varchar, ROUTES varchar, POINT integer);";
		db.execSQL(esql);
		//db.close();
	}

	//DB Insert
	public long insert(String table_name,String pos, String routes,int index) {
		//System.out.println("**Table Name:"+table_name+"**POS:"+pos+"**Routes:"+routes+"**Point:"+index);
		//SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("POS", pos);
		cv.put("ROUTES", routes);
		cv.put("POINT", index);
		//System.out.println("***********POS:"+pos+"**ROUTES:"+routes+"**INdex:"+index);
		long row = db.insert(table_name, null, cv);
		//db.close();
		return row;
	}
	
	// DB Insert User
	public long insert_user(String name, String pwd) {
		ContentValues cv = new ContentValues();
		cv.put("NAME", name);
		cv.put("PWD", pwd);
		long row = db.insert("user", null, cv);
		// db.close();
		return row;
	}
		
	// Search user
	public Boolean search_user(String name, String pwd){
		db = this.getReadableDatabase();
		String sql = "SELECT * FROM user where NAME=\""+ name +"\" and PWD=\""+ pwd +"\"";
		Cursor C = db.rawQuery(sql,null);
		if (C.getCount()==0){
			db.close();
			return false;
		}
		else{
			db.close();
			return true;
		}
	}
	
	public String search_z(String table_name, String beginPoint, String endPoint) {
		// System.out.println("***"+beginPoint+"***"+endPoint);
		db = this.getReadableDatabase();

		String sql1 = "select sr1.POS as start, sr2.POS as end, sr1.ROUTES as route, sr2.POINT - sr1.POINT as num ";
		String sql2 = "from " + table_name + " sr1," + table_name + " sr2 ";
		String sql3 = "where sr1.ROUTES=sr2.ROUTES and sr1.POINT<sr2.POINT and sr1.POS=\""
				+ beginPoint + "\" and sr2.POS=\"" + endPoint + "\"";

		String result = new String();
		Cursor C = db.rawQuery(sql1 + sql2 + sql3, null);
		while (C.moveToNext()) {
			String route = C.getString(C.getColumnIndex("route"));
			Integer num = C.getInt(C.getColumnIndex("num"));
			result = result + "\r\n" + "乘坐" + route + "，共计" + num + "站";
		}

		String sql4 = "create view if not exists RouteT0_"
				+ table_name
				+ " as select r1.POS as start,r2.POS as end,r1.ROUTES as route, r2.POINT - r1.POINT as num ";
		String sql5 = "from " + table_name + " r1," + table_name
				+ " r2 where r1.ROUTES = r2.ROUTES and r1.POINT < r2.POINT";
		db.execSQL(sql4 + sql5);

		sql1 = "select r1.start as start, r1.route as route1, r1.end as middle, r2.route as route2, r2.end as end, r1.num + r2.num as num ";
		sql2 = "from RouteT0_" + table_name + " r1,RouteT0_" + table_name
				+ " r2 where r1.start = \"" + beginPoint
				+ "\" and  r1.end = r2.start and r2.end = \"" + endPoint + "\"";

		String route1 = null;
		String route2 = null;
		while (C.moveToNext()) {
			// String start = C2.getString(C2.getColumnIndex("start"));
			// 避免重复公交显示
			if (route1.equals(C.getString(C.getColumnIndex("route1")))
					&& route2.equals(C.getString(C.getColumnIndex("route2")))) {
				continue;
			} else {
				route1 = C.getString(C.getColumnIndex("route1"));
				route2 = C.getString(C.getColumnIndex("route2"));
			}
			String middle = C.getString(C.getColumnIndex("middle"));
			Integer num = C.getInt(C.getColumnIndex("num"));
			result = result + "\r\n" + "乘坐" + route1 + "在" + middle + "换乘"
					+ route2 + "，经过" + num + "站";
		}
		C.close();
		db.close();

		return result;
	}
	
	public String search_d(String table_name,String beginPoint,String endPoint){
		//System.out.println("直达开始查询**BeginPoint:"+beginPoint+"**EndPoint:"+endPoint);
		db = this.getReadableDatabase();
		
		String sql1 = "select sr1.POS as start, sr2.POS as end, sr1.ROUTES as route, sr2.POINT - sr1.POINT as num ";
        String sql2 = "from " + table_name + " sr1,"+ table_name +" sr2 ";
        String sql3 = "where sr1.ROUTES=sr2.ROUTES and sr1.POINT<sr2.POINT and sr1.POS=\"" + beginPoint + "\" and sr2.POS=\"" + endPoint +"\"";
        
        String result = new String();
        Cursor C = db.rawQuery(sql1 + sql2 +sql3,null);
        while(C.moveToNext()){
            //String start = C.getString(C.getColumnIndex("start"));
            String route = C.getString(C.getColumnIndex("route"));
            //String end = C.getString(C.getColumnIndex("end"));
            Integer num = C.getInt(C.getColumnIndex("num"));
            //result = result + "\r\n" + "起始站点:" + start + "   目的站点:" + end + "   乘坐线路:" + route + "   经过的站点数:" + num;
            result = result + "\r\n" + "乘坐" + route + "，共计" + num +"站";
        }
        C.close();
        db.close();
        //System.out.println("直达结束查询"+result);
        System.out.println("直达结束查询");
        
        return result;
	}
	
	//添加城市
	public void insert_city(String city_ch,String city_en){
		db = this.getWritableDatabase();
		String sql = "CREATE TABLE if not exists citylist (CITYCH varchar, CITYEN varchar);";
		db.execSQL(sql);
		
		sql = "select * from citylist where CITYCH=\""+city_ch+"\" and CITYEN=\""+city_en+"\"";
		Cursor C = db.rawQuery(sql,null);
		if (C.getCount()==0){
			ContentValues cv = new ContentValues();
			cv.put("CITYCH", city_ch);
			cv.put("CITYEN", city_en);
			db.insert("citylist", null, cv);
		}
		db.close();
	}
	
	public Boolean check_city(String cityname){
		Boolean isin = false;
		
		db = this.getWritableDatabase();
		String sql = "CREATE TABLE if not exists citylist (CITYCH varchar, CITYEN varchar);";
		db.execSQL(sql);
		db.close();
		
		db = this.getReadableDatabase();
		sql = "select * from citylist where CITYEN=\""+cityname+"\"";
		Cursor C = db.rawQuery(sql,null);
		while(C.moveToNext()){
			isin = true;
        }
		db.close();
		
		return isin;
	}
	
	//获取城市
	public 	ArrayList<String> getCity(){
		ArrayList<String> citylist = new ArrayList<String>();
		
		db = this.getWritableDatabase();
		String sql = "CREATE TABLE if not exists citylist (CITYCH varchar, CITYEN varchar);";
		db.execSQL(sql);
		db.close();
		
		db = this.getReadableDatabase();
		sql = "select * from citylist";
		Cursor C = db.rawQuery(sql,null);
		while(C.moveToNext()){
            String citych = C.getString(C.getColumnIndex("CITYCH"));
            String cityen = C.getString(C.getColumnIndex("CITYEN"));
            citylist.add(citych+"+"+cityen);
        }
		db.close();
		
		//System.out.println("***************"+citylist);
		return citylist;
	}
	
	//添加搜索历史
	public void insert_history(String startpoint,String endpoint){
		db = this.getWritableDatabase();
		String sql = "CREATE TABLE if not exists history (CITYCH varchar);";
		db.execSQL(sql);
		
		sql = "select * from history where CITYCH=\""+startpoint+"\"";
		Cursor C = db.rawQuery(sql,null);
		if (C.getCount()==0){
			ContentValues cv = new ContentValues();
			cv.put("CITYCH", startpoint);
			db.insert("history", null, cv);
		}
		sql = "select * from history where CITYCH=\""+endpoint+"\"";
		C = db.rawQuery(sql,null);
		if (C.getCount()==0){
			ContentValues cv = new ContentValues();
			cv.put("CITYCH",endpoint);
			db.insert("history", null, cv);
		}
		db.close();
		//System.out.println("添加历史成功");
	}
	
	//获取搜索历史
	public 	ArrayList<String> getHistory(){
		ArrayList<String> citylist = new ArrayList<String>();
		
		db = this.getWritableDatabase();
		String sql = "CREATE TABLE if not exists history (CITYCH varchar);";
		db.execSQL(sql);
		db.close();
		
		db = this.getReadableDatabase();
		sql = "select * from history";
		Cursor C = db.rawQuery(sql,null);
		while(C.moveToNext()){
            String citych = C.getString(C.getColumnIndex("CITYCH"));
            citylist.add(citych);
        }
		db.close();
		
		//System.out.println("***************"+citylist);
		return citylist;
	}
	
	public String search_h(String table_name,String beginPoint,String endPoint){
		System.out.println("换乘查询开始***"+beginPoint+"***"+endPoint);
		db = this.getReadableDatabase();
		
		//db.execSQL("DROP VIEW IF EXISTS RouteT0");
		String sql4 = "create view if not exists RouteT0_"+table_name+" as select r1.POS as start,r2.POS as end,r1.ROUTES as route, r2.POINT - r1.POINT as num ";
        String sql5 = "from "+table_name+" r1,"+table_name+" r2 where r1.ROUTES = r2.ROUTES and r1.POINT < r2.POINT";
        db.execSQL(sql4 + sql5);
        
        String result = new String();
        String sql1 = "select r1.start as start, r1.route as route1, r1.end as middle, r2.route as route2, r2.end as end, r1.num + r2.num as num ";
        String sql2 = "from RouteT0_"+table_name+" r1,RouteT0_"+table_name+" r2 where r1.start = \""+beginPoint+"\" and  r1.end = r2.start and r2.end = \""+endPoint+"\"";
        Cursor C2 = db.rawQuery(sql1 + sql2,null);
        
        String route1 = null;
        String route2 = null;
        while(C2.moveToNext()){
            //String start = C2.getString(C2.getColumnIndex("start"));
            //避免重复公交显示
            if(route1.equals(C2.getString(C2.getColumnIndex("route1"))) && route2.equals(C2.getString(C2.getColumnIndex("route2")))){
            	continue;
            }
            else{
                route1 = C2.getString(C2.getColumnIndex("route1"));
                route2 = C2.getString(C2.getColumnIndex("route2"));
            }
            String middle = C2.getString(C2.getColumnIndex("middle"));
            //String end = C2.getString(C2.getColumnIndex("end"));
            Integer num = C2.getInt(C2.getColumnIndex("num"));
            //result = result + "\r\n" + "起始站点:" + start
            //        + "   乘坐路线1:" + route1 + "   中转站点:" + middle + "   乘坐路线2:" + route2 + "   目的站点:" + end + "   经过的站点数:" + num;
            result = result + "\r\n"+"乘坐" + route1 + "在" + middle + "换乘" + route2 +"，经过" + num + "站";
        }

        //exchange two times
        /*sql1 = "select r1.start as start, r1.route as route1, r1.end as middle1, r2.route as route2, r2.end as middle2, r3.route as route3, r3.end as end, r1.num + r2.num + r3.num as num ";
        sql2 = "from RouteT0 r1,RouteT0 r2, RouteT0 r3 where r1.start = \"玉兰路\" and  r1.end = r2.start and r2.end = r3.start and r3.end = \"山月路口\"";
        C2 = db.rawQuery(sql1 + sql2,null);
        while(C2.moveToNext()){
            String start = C2.getString(C2.getColumnIndex("start"));
            String route1 = C2.getString(C2.getColumnIndex("route1"));
            String middle1 = C2.getString(C2.getColumnIndex("middle1"));
            String route2 = C2.getString(C2.getColumnIndex("route2"));
            String middle2 = C2.getString(C2.getColumnIndex("middle2"));
            String route3 = C2.getString(C2.getColumnIndex("route3"));
            String end = C2.getString(C2.getColumnIndex("end"));
            Integer num = C2.getInt(C2.getColumnIndex("num"));
            result = result + "\r\n" + "起始站点:" + start
                    + "   乘坐路线1:" + route1 + "   中转站点1:" + middle1 + "   乘坐路线2:" + route2 +
                    "   中转站点2:" + middle2 + "   乘坐路线3:" + route3 + "   目的站点:" + end + "   经过的站点数:" + num;
        }*/
        C2.close();
        db.close();
        
        //System.out.println("换乘结束查询"+result);
        System.out.println("换乘结束查询");
        
		return result;
	}
	
	public void close(){
		//SQLiteDatabase db = this.getWritableDatabase();
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