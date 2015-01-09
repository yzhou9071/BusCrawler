package com.example.buscrawler;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

public class Welcome extends ActionBarActivity {	
	private ImageView welcomeImg;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		
		welcomeImg = (ImageView) this.findViewById(R.id.welcome_img);  
        AlphaAnimation anima = new AlphaAnimation(0.3f, 1.0f);  
        anima.setDuration(3000);// 设置动画显示时间  
        welcomeImg.startAnimation(anima);  
        anima.setAnimationListener(new AnimationImpl());

    }  
	private class AnimationImpl implements AnimationListener {  
		  
        @Override  
        public void onAnimationStart(Animation animation) {  
            welcomeImg.setBackgroundResource(R.drawable.welcome);  
        }  
  
        @Override  
        public void onAnimationEnd(Animation animation) {  
            skip(); // 动画结束后跳转到别的页面  
        }  
  
        @Override  
        public void onAnimationRepeat(Animation animation) {  
        }  
  
    }  
  
    private void skip() {  
        startActivity(new Intent(this, Login.class));  
        finish();  
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
