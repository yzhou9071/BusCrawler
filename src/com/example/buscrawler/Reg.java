package com.example.buscrawler;

import java.util.Timer;
import java.util.TimerTask;

import com.example.sqlite.Sqlite;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
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

public class Reg extends ActionBarActivity {
	private Sqlite sqlite;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reg);
		sqlite = new Sqlite(this,"businfo");
		
		Button btn_reg = (Button) findViewById(R.id.btn_reg);
		btn_reg.setOnClickListener(regClickListener);
		/*Button btn_return = (Button) findViewById(R.id.btn_return);
		btn_return.setOnClickListener(returnClickListener);*/
    }  

	private OnClickListener regClickListener = new OnClickListener(){
		public void onClick(View v){
			EditText username = (EditText) findViewById(R.id.username);
			EditText userpwd = (EditText) findViewById(R.id.userpwd);
			EditText userpwdverify = (EditText) findViewById(R.id.userpwdverify);
			
			String name = username.getText().toString();
			String pwd = userpwd.getText().toString();
			String pwdverify = userpwdverify.getText().toString();
			if(name.equals("") || pwd.equals("") || pwdverify.equals("")){
				//System.out.println("弹框提示不得为空");
				Toast.makeText(getApplicationContext(),"注册信息不得为空！", 0).show();
			}
			else if(pwd.equals(pwdverify)){
				sqlite.create_user();
				sqlite.insert_user(name, pwd);
				sqlite.close();
				Toast.makeText(getApplicationContext(),"注册成功！", 0).show();
				
				final Intent intent = new Intent();
				intent.setClass(Reg.this, MainActivity.class);
				intent.putExtra("str", "come from reg activity");
				startActivity(intent);//无返回值的调用,启动一个明确的activity
			}
			else{
				//System.out.println("弹框提示密码输入错误");
				Toast.makeText(getApplicationContext(),"两次密码不一致！", 0).show();
			}
		}
	};
	
	/*private OnClickListener returnClickListener = new OnClickListener(){
		public void onClick(View v){
			Intent intent = new Intent();
			intent.setClass(Reg.this, Login.class);
			intent.putExtra("str", "come from reg activity return");
			startActivity(intent);//无返回值的调用,启动一个明确的activity
		}
	};*/

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
