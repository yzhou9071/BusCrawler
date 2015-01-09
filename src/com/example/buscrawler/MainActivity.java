package com.example.buscrawler;

import java.util.ArrayList;

import com.example.spider.Spider;
import com.example.sqlite.Sqlite;

import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	//private static final String[] citylist_ch = {"长沙"};
	//private static final String[] citylist_en = {"changsha"};
	private Spinner spin_city;
	public Sqlite sqlite;
	private static ArrayList<String> citylist = new ArrayList<String>();
	private static ArrayList<String> citych = new ArrayList<String>();
	private static ArrayList<String> cityen = new ArrayList<String>();
	private static ArrayList<String> historylist = new ArrayList<String>();
	//private ProgressDialog prodia;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		sqlite = new Sqlite(this,"businfo");
		
		/*Button btn_online = (Button) findViewById(R.id.btn_online);
		btn_online.setOnClickListener(onlineClickListener);*/
		
		Button btn_offline = (Button) findViewById(R.id.btn_offline);
		btn_offline.setOnClickListener(offlineClickListener);
		
		Button btn_add = (Button) findViewById(R.id.btn_add);
		btn_add.setOnClickListener(addClickListener);
		
		initValues();
	}
	
	public void initValues() {
		citylist.clear();
		citylist = sqlite.getCity();
		
		citych.clear();
		cityen.clear();

		spin_city = (Spinner) findViewById(R.id.spin_city);
		ArrayAdapter<String> adapter;
		/*if (citylist.isEmpty()) {
			Toast.makeText(getApplicationContext(), "可以添加城市哦！", 0).show();
			adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, citylist_ch);
		} else {*/
			for (int i = 0; i < citylist.size(); i++) {
				String tmp = citylist.get(i);
				citych.add(tmp.substring(0, tmp.indexOf("+")));
				cityen.add(tmp.substring(tmp.indexOf("+") + 1));
			}

			adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, citych);
		//}
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin_city.setAdapter(adapter);
		spin_city.setVisibility(View.VISIBLE);

		historylist.clear();
		historylist = sqlite.getHistory();
		// System.out.println("&&&&&&&&&&&&&"+historylist);
		ArrayAdapter<String> adapter_begin = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, historylist);
		AutoCompleteTextView text_begin = (AutoCompleteTextView) findViewById(R.id.text_begin);
		text_begin.setAdapter(adapter_begin);

		ArrayAdapter<String> adapter_end = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, historylist);
		AutoCompleteTextView text_end = (AutoCompleteTextView) findViewById(R.id.text_end);
		text_end.setAdapter(adapter_end);
	}
/*	
	private OnClickListener onlineClickListener = new OnClickListener(){
		public void onClick(View v) {
			EditText text_begin = (EditText) findViewById(R.id.text_begin);
			text_begin.getText();

			EditText text_end = (EditText) findViewById(R.id.text_end);
			text_end.getText();
			
			String city_ch = (String) spin_city.getSelectedItem();
			String city = null;
			
			if (citylist.isEmpty()) {
				for (int i = 0; i < citylist_ch.length; i++) {
					if (city_ch.equals(citylist_ch[i])) {
						city = citylist_en[i];
						break;
					}
				}
			} else {
				for (int i = 0; i < citylist.size(); i++) {
					if (city_ch.equals(citych.get(i))) {
						city = cityen.get(i);
						break;
					}
				}
			}
			Spider crawler = new Spider("http://bus.mapbar.com/" + city
					+ "/xianlu", 20, "公交线路", city);
			//System.out.println("Search Start...");
			crawler.buscrawler(city);
			//prodia= ProgressDialog.show(MainActivity.this, "Loading...", "Please wait...", true, false);
			//prodia.show();

			//System.out.println("Search Finish...");
			ArrayList<String> routes = crawler.getRoutes();
			ArrayList<String> pos = crawler.getPos();
			ArrayList<Integer> index = crawler.getIndex();
			//System.out.println("All Gets...");
			sqlite.upgrade(city);
			// sqlite.upgradeLine(city+"_line");
			//System.out.println("DB Create Success...");
			for (int i = 0; i < routes.size(); i++) {
				System.out.println("**POS:" + pos.get(i) + "**ROUTES:"
						+ routes.get(i) + "**INDEX:" + index.get(i));
				sqlite.insert(city, pos.get(i), routes.get(i), index.get(i));
				
				 * if(index.get(i) == 1){ sqlite.insertLine(city+"_line",
				 * routes.get(i)); }
				 
			}
			//System.out.println("DB Insert Success...");
			sqlite.close();
			//System.out.println("Save Success...");
			//prodia.dismiss();
			// text_begin.setText("Success !");
		}
	};
	*/
	private OnClickListener offlineClickListener = new OnClickListener(){
		public void onClick(View v){
			EditText text_begin = (EditText) findViewById(R.id.text_begin);
			EditText text_end = (EditText) findViewById(R.id.text_end);
			RadioButton rbt_z = (RadioButton) findViewById(R.id.radio0);
			RadioButton rbt_d = (RadioButton) findViewById(R.id.radio1);
			RadioButton rbt_h = (RadioButton) findViewById(R.id.radio2);
			
			String city_ch = (String) spin_city.getSelectedItem();
			String city = null;
			for(int i=0;i<citych.size();i++){
				if(city_ch.equals(citych.get(i))){
					city = cityen.get(i);
					break;
				}
			}
			
			sqlite.insert_history(text_begin.getText().toString().trim(), text_end.getText().toString().trim());
			Toast.makeText(getApplicationContext(), "开始查询", 0).show();
			
			String line = null;
			//search by diff ways
			if(rbt_z.isChecked()){
				//System.out.println("XUAN ZHONG ZONGHE");
				line = sqlite.search_z(city, text_begin.getText().toString().trim(), text_end.getText().toString().trim());
			}
			if(rbt_d.isChecked()){
				//System.out.println("XUAN ZHONG ZHIDA");
				line = sqlite.search_d(city, text_begin.getText().toString().trim(), text_end.getText().toString().trim());
				//text_end.setText(line);
			}
			if(rbt_h.isChecked()){
				//System.out.println("XUAN ZHONG HUANCHENG");
				line = sqlite.search_h(city, text_begin.getText().toString().trim(), text_end.getText().toString().trim());
			}
			
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, Detail.class);
			intent.putExtra("line", line);
			startActivity(intent);//无返回值的调用,启动一个明确的activity
		}
	};
	
	private OnClickListener addClickListener = new OnClickListener(){
		public void onClick(View v){
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, City.class);
			//intent.putExtra("str", "come from main activity");
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
