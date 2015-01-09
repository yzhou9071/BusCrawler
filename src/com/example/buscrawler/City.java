package com.example.buscrawler;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.example.spider.Spider;
import com.example.sqlite.Sqlite;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class City extends ActionBarActivity {	
	private Sqlite sqlite;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.city);
		
		sqlite = new Sqlite(this,"businfo");
		
		Button btn_add = (Button) findViewById(R.id.btn_add);
		btn_add.setOnClickListener(addClickListener);
    }  
  
	private OnClickListener addClickListener = new OnClickListener(){
		public void onClick(View v) {
			// 判断用户名及密码
			EditText citytext = (EditText) findViewById(R.id.citytext);
			EditText pinyintext = (EditText) findViewById(R.id.pinyintext);

			String city_ch = citytext.getText().toString();
			String city_en = pinyintext.getText().toString();
			if (city_ch.equals("") || city_en.equals("")) {
				// System.out.println("弹框提示不得为空");
				Toast.makeText(getApplicationContext(), "信息不得为空！", 0).show();
			} else {
				sqlite.insert_city(city_ch, city_en);
				
				Toast.makeText(getApplicationContext(), "正在努力更新数据，请稍候再稍候！", 0).show();
				
				//爬取公交数据
				Spider crawler = new Spider("http://bus.mapbar.com/" + city_en
						+ "/xianlu", 20, "公交线路", city_en);
				crawler.buscrawler(city_en);
				ArrayList<String> routes = crawler.getRoutes();
				ArrayList<String> pos = crawler.getPos();
				ArrayList<Integer> index = crawler.getIndex();
				sqlite.upgrade(city_en);
				for (int i = 0; i < routes.size(); i++) {
					//System.out.println("**POS:" + pos.get(i) + "**ROUTES:"
					//		+ routes.get(i) + "**INDEX:" + index.get(i));
					sqlite.insert(city_en, pos.get(i), routes.get(i), index.get(i));
				}
				sqlite.close();
				
				// System.out.println("弹框提示登录成功");
				// System.out.println("跳转到主页面");
				Toast.makeText(getApplicationContext(), "添加成功！", 0).show();

				final Intent intent = new Intent();
				intent.setClass(City.this, MainActivity.class);
				intent.putExtra("str", "come from city activity");
				startActivity(intent);// 无返回值的调用,启动一个明确的activity
			}
		}
	};
	
	private OnClickListener regClickListener = new OnClickListener(){
		public void onClick(View v){
			Intent intent = new Intent();
			intent.setClass(City.this, Reg.class);
			intent.putExtra("str", "come from login activity");
			startActivity(intent);//无返回值的调用,启动一个明确的activity
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
