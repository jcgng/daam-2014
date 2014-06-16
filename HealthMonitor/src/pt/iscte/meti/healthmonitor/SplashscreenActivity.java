package pt.iscte.meti.healthmonitor;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;

public class SplashscreenActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splashscreen);
		new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            	// Restore preferences
                SharedPreferences settings = getSharedPreferences(MainActivity.MY_PREFS, 0);
                LoginActivity.mUser = settings.getString("username", null);
                LoginActivity.mPassword = settings.getString("password", null);
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
