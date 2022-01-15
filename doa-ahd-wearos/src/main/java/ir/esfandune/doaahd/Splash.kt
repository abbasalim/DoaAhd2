package ir.esfandune.doaahd

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import ir.esfandune.doaahd.Act_Main

class Splash : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val blinkanimation = AlphaAnimation(0f, 1f) // Change alpha from fully visible to invisible
        blinkanimation.duration = 700 // duration - half a second
        blinkanimation.interpolator = LinearInterpolator() // do not alter animation rate
        val i = findViewById<View>(R.id.splash_img) as ImageView
        i.startAnimation(blinkanimation)
        val handler = Handler()
        handler.postDelayed({
            finish()
            startActivity(Intent(baseContext, Act_Main::class.java))
        }, 1100) // 2000
    } //
}