package com.example.buscrawler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import android.widget.AbsListView.LayoutParams;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sqlite.Sqlite;

public class Detail extends ActionBarActivity {	
	private Sqlite sqlite;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);
		/*
		Button btn_return = (Button) findViewById(R.id.btn_return);
		btn_return.setOnClickListener(returnClickListener);
		*/
		
		initValues(getIntent().getStringExtra("line"));
		
    } 
	
	public void initValues(String line){
		System.out.println("*** From Detail ***"+line);
		String tmpLine[] = line.split("\r\n");
		
		List<String> data = new ArrayList<String>();
		for(int i=0;i<tmpLine.length;i++){
			if(i==0) continue;
	        data.add(tmpLine[i]);
	    }
		
		ListView buslist = (ListView) this.findViewById(R.id.buslist);
        buslist.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,data));
        //setContentView(buslist);
	}
	
  /*      
	private OnClickListener returnClickListener = new OnClickListener(){
		public void onClick(View v){
			Intent intent = new Intent();
			intent.setClass(Detail.this, MainActivity.class);
			intent.putExtra("str", "come from detail activity");
			startActivity(intent);//无返回值的调用,启动一个明确的activity
		}
	};
	*/
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
