package ir.esfandune.doaahd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class Splash extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		
		
		AlphaAnimation  blinkanimation= new AlphaAnimation(0, 1); // Change alpha from fully visible to invisible
		blinkanimation.setDuration(1000); // duration - half a second
		blinkanimation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
		
		
		ImageView i = (ImageView) findViewById(R.id.splash_img);
		
		i.startAnimation(blinkanimation);
		
		
		//
		
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {

				finish();
				startActivity(new Intent(getBaseContext(), Act_Main.class));
			}
        }, 1500);// 2000

	
		
		
	}//
}
