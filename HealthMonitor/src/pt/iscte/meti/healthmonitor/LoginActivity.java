package pt.iscte.meti.healthmonitor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import pt.iscte.meti.healthmonitor.tasks.GetHealthTask;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {
	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	public static String mUser = null;
	public static String mPassword = null;

	// UI references.
	private EditText mUserView;
	private EditText mPasswordView;
	@SuppressWarnings("unused")
	private View mLoginFormView;
	@SuppressWarnings("unused")
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		// Set up the login form.
		mUserView = (EditText) findViewById(R.id.user);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id,
					KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					attemptLogin();
					return true;
				}
				return false;
			}
		});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					attemptLogin();
				}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		menu.getItem(0).setIcon(R.drawable.menu_icon);
		menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
	        public boolean onMenuItemClick(MenuItem item) {
	            Intent settingsIntent = new Intent(LoginActivity.this, SettingsActivity.class);
	            LoginActivity.this.startActivity(settingsIntent);
	            return false;
	        }
	    });
		
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}
		// Reset errors.
		mUserView.setError(null);
		mPasswordView.setError(null);
		// Store values at the time of the login attempt.
		mUser = mUserView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		boolean cancel = false;
		View focusView = null;
		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 3) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}
		// Check for a valid username address.
		if (TextUtils.isEmpty(mUser)) {
			mUserView.setError(getString(R.string.error_field_required));
			focusView = mUserView;
			cancel = true;
		}
		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			mAuthTask = new UserLoginTask(this);
			mAuthTask.execute((Void) null);
		}
	}

	public EditText getPasswordEditView() {
		return mPasswordView;
	}
	
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		private final String loginUrl = "%s/HealthMonitor/check-credentials.php?user=%s&pass=%s";
		
		private Activity activity = null;
		
		private ProgressDialog progressDiag = null;
		
		private String sendGet(String url) throws ClientProtocolException, IOException {
			String responseString = null;
			
			HttpClient client = new DefaultHttpClient();
		    HttpResponse response = client.execute(new HttpGet(url));
	    	StatusLine statusLine = response.getStatusLine();
	    	if(statusLine.getStatusCode() == HttpStatus.SC_OK){
	            ByteArrayOutputStream out = new ByteArrayOutputStream();
	            response.getEntity().writeTo(out);
	            out.close();
	            responseString = out.toString();
	        } else {
	        	Log.e(GetHealthTask.class.getName(), "Failed to send get!");
	            // Closes the connection.
	            response.getEntity().getContent().close();
	            throw new IOException(statusLine.getReasonPhrase());
	        }
	    	return responseString;
		}
		
		public UserLoginTask(Activity activity) {
			this.activity = activity;
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			String url = String.format(loginUrl,MainActivity.serverAddress,LoginActivity.mUser,LoginActivity.mPassword);
			try {
				sendGet(url);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}

		@Override
		protected void onPreExecute(){ 
		   super.onPreExecute();
		   progressDiag = new ProgressDialog(activity);
		   progressDiag.setMessage("Sign In...");
		   progressDiag.show();  
		}
		
		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			if(progressDiag.isShowing())
	        	progressDiag.dismiss();
			if(success) {
				// save credentials
				SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
			    SharedPreferences.Editor editor = settings.edit();
			    editor.putString("username", LoginActivity.mUser);
			    editor.putString("password", LoginActivity.mPassword);
			    editor.commit();
			    // start main activity
				Intent intent=new Intent(activity,MainActivity.class);
				startActivity(intent);
				finish();
			} else {
				if(activity instanceof LoginActivity) {
					((LoginActivity) activity).getPasswordEditView().setError(getString(R.string.error_incorrect_password));
					((LoginActivity) activity).getPasswordEditView().requestFocus();
				}
			}
			activity = null;
		}

		@Override
		protected void onCancelled() {
			if(progressDiag.isShowing())
	        	progressDiag.dismiss();
			
			mAuthTask = null;
			activity = null;
		}
	}
}
