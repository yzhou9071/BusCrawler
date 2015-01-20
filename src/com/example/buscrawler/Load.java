package com.example.buscrawler;

import java.util.ArrayList;

import com.example.spider.Spider;
import com.example.sqlite.Sqlite;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Load extends ActionBarActivity {
	private Sqlite sqlite;
	private String cityname;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.load);
		cityname = getIntent().getStringExtra("cityname");
		
		sqlite = new Sqlite(this,"businfo");
		
		Button btn_add = (Button) findViewById(R.id.btn_invi);
		btn_add.setOnClickListener(addClickListener);
	}  

	private OnClickListener addClickListener = new OnClickListener(){
		public void onClick(View v) {
			initValues(cityname);
			Toast.makeText(getApplicationContext(), "数据配置成功，马上跳转！", 0).show();
			Intent intent = new Intent();
			intent.setClass(Load.this, MainActivity.class);
			startActivity(intent);
			finish();
		}
	};
  
	private void initValues(String cityname) {
		// TODO Auto-generated method stub
		Spider crawler = new Spider("http://mbus.mapbar.com/"+cityname+"/xianlu", 20, "公交线路", "changsha");
		crawler.buscrawler(cityname);
		ArrayList<String> routes = crawler.getRoutes();
		ArrayList<String> pos = crawler.getPos();
		ArrayList<Integer> index = crawler.getIndex();
		sqlite.upgrade(cityname);
		for (int i = 0; i < routes.size(); i++) {
			sqlite.insert(cityname, pos.get(i), routes.get(i),index.get(i));
		}
		sqlite.close();
	}

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
