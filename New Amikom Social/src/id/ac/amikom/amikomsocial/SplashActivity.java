package id.ac.amikom.amikomsocial;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.app.Activity;
import android.content.Intent;

public class SplashActivity extends Activity {

	protected int _splashTime = 2800;
	private Thread splashTread;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
    	requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		final SplashActivity sPlashScreen = this;

		// thread for displaying the SplashScreen
		splashTread = new Thread() {
			@Override
			public void run() {
				try {
					synchronized (this) {
						wait(_splashTime);
					}

				} catch (InterruptedException e) {
				} finally {
					finish();

					Intent i = new Intent();
					i.setClass(sPlashScreen, MainActivity.class);
					i.putExtra("url", "");
					startActivity(i);
					
				}
			}
		};

		splashTread.start();
    	
    }

    
}
