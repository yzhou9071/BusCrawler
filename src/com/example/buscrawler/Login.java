package com.example.buscrawler;

import java.util.Timer;
import java.util.TimerTask;

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

public class Login extends ActionBarActivity {	
	private Sqlite sqlite;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		sqlite = new Sqlite(this,"businfo");
		
		Button btn_login = (Button) findViewById(R.id.btn_login);
		btn_login.setOnClickListener(loginClickListener);
		
		Button btn_reg = (Button) findViewById(R.id.btn_reg);
		btn_reg.setOnClickListener(regClickListener);
    }  
  
	private OnClickListener loginClickListener = new OnClickListener(){
		public void onClick(View v){
			//判断用户名及密码
			EditText username = (EditText) findViewById(R.id.username);
			EditText userpwd = (EditText) findViewById(R.id.userpwd);
			
			String name = username.getText().toString();
			String pwd = userpwd.getText().toString();
			if(name.equals("") || pwd.equals("")){
				//System.out.println("弹框提示不得为空");
				Toast.makeText(getApplicationContext(),"登录信息不得为空！", 0).show();
			}
			else{
				if (sqlite.search_user(name,pwd)){
					//System.out.println("弹框提示登录成功");
					//System.out.println("跳转到主页面");
					Toast.makeText(getApplicationContext(),"登录成功！", 0).show();
					
					final Intent intent = new Intent();
					intent.setClass(Login.this, MainActivity.class);
					//intent.putExtra("str", "come from login activity");
					startActivity(intent);//无返回值的调用,启动一个明确的activity
				}
				else{
					//System.out.println("弹框提示登录失败");
					Toast.makeText(getApplicationContext(),"登录失败！", 0).show();
				}
				
			}
		}
	};
	
	private OnClickListener regClickListener = new OnClickListener(){
		public void onClick(View v){
			Intent intent = new Intent();
			intent.setClass(Login.this, Reg.class);
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
