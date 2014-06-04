package pt.iscte.meti.healthmonitor;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class SplashscreenActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splashscreen);
		new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            	if(LoginActivity.mUser==null || LoginActivity.mPassword==null) {
        			Intent login=new Intent(SplashscreenActivity.this,LoginActivity.class);
        			startActivity(login);
        			finish();
        		} else {
	                Intent main = new Intent(SplashscreenActivity.this, MainActivity.class);
	                startActivity(main);
	                finish();
            	}
            }
        }, 3000);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

}
