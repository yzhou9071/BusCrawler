package com.example.buscrawler;

import java.util.ArrayList;

import com.example.spider.Spider;
import com.example.sqlite.Sqlite;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
	private static final String[] citylist_ch = {"长沙","广州","杭州","上海"};
	private static final String[] citylist_en = {"changsha","guangzhou","hangzhou","shanghai"};
	private Spinner spin_city;
	public Sqlite sqlite;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		sqlite = new Sqlite(this,"businfo");
		
		spin_city = (Spinner) findViewById(R.id.spin_city);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,citylist_ch);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_city.setAdapter(adapter);
        spin_city.setVisibility(View.VISIBLE);
		
		Button btn_online = (Button) findViewById(R.id.btn_online);
		btn_online.setOnClickListener(onlineClickListener);
		
		Button btn_offline = (Button) findViewById(R.id.btn_offline);
		btn_offline.setOnClickListener(offlineClickListener);
	}
	
	private OnClickListener onlineClickListener = new OnClickListener(){
		public void onClick(View v){
			TextView text_begin = (TextView) findViewById(R.id.text_begin);
			text_begin.getText();
			
			TextView text_end = (TextView) findViewById(R.id.text_end);
			text_end.getText();
			
			String city_ch = (String) spin_city.getSelectedItem();
			String city = null;
			for(int i=0;i<citylist_ch.length;i++){
				if(city_ch.equals(citylist_ch[i])){
					city = citylist_en[i];
					break;
				}
			}
			
			Spider crawler = new Spider("http://bus.mapbar.com/"+city+"/xianlu",20,"公交线路",city);
			System.out.println("Search Start...");
			crawler.buscrawler(city);
			System.out.println("Search Finish...");
			ArrayList<String> routes = crawler.getRoutes();
			ArrayList<String> pos = crawler.getPos();
			ArrayList<Integer> index = crawler.getIndex();
			System.out.println("All Gets...");
			sqlite.upgrade(city);
			sqlite.upgradeLine(city+"_line");
			System.out.println("DB Create Success...");
			for(int i=0;i<routes.size();i++){
				System.out.println("**POS:"+pos.get(i)+"**ROUTES:"+routes.get(i)+"**INDEX:"+index.get(i));
				sqlite.insert(city, pos.get(i), routes.get(i), index.get(i));
				if(index.get(i) == 1){
					sqlite.insertLine(city+"_line", routes.get(i));
				}
			}
			System.out.println("DB Insert Success...");
			sqlite.close();
			System.out.println("Save Success...");
			
			text_begin.setText("Success !");
		}
	};
	
	private OnClickListener offlineClickListener = new OnClickListener(){
		public void onClick(View v){
			TextView text_begin = (TextView) findViewById(R.id.text_begin);
			TextView text_end = (TextView) findViewById(R.id.text_end);
			RadioButton rbt_z = (RadioButton) findViewById(R.id.radio0);
			RadioButton rbt_d = (RadioButton) findViewById(R.id.radio2);
			RadioButton rbt_h = (RadioButton) findViewById(R.id.radio1);
			
			String city_ch = (String) spin_city.getSelectedItem();
			String city = null;
			for(int i=0;i<citylist_ch.length;i++){
				if(city_ch.equals(citylist_ch[i])){
					city = citylist_en[i];
					break;
				}
			}
			
			//search by diff ways
			if(rbt_z.isChecked()){
				System.out.println("XUAN ZHONG ZONGHE");
				String line = sqlite.search_z(city, text_begin.getText().toString().trim(), text_end.getText().toString().trim());
			}
			if(rbt_d.isChecked()){
				System.out.println("XUAN ZHONG ZHIDA");
				String line = sqlite.search_d(city, text_begin.getText().toString().trim(), text_end.getText().toString().trim());
			}
			if(rbt_h.isChecked()){
				System.out.println("XUAN ZHONG HUANCHENG");
				String line = sqlite.search_h(city, text_begin.getText().toString().trim(), text_end.getText().toString().trim());
			}			
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
