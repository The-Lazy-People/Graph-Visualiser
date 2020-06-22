package com.thelazypeople.graphvisualiser

import android.animation.Animator
import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.Explode
import android.transition.Fade
import android.view.Window
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(window){
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            exitTransition = Fade()
        }

        //requestFeature to be called before assigning layout.
        setContentView(R.layout.activity_splash)
        window.statusBarColor = resources.getColor(R.color.white)

        splash_lottie.setMinAndMaxFrame(0, 90)
        splash_lottie.addAnimatorListener(object : Animator.AnimatorListener{
            override fun onAnimationRepeat(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                startActivity(Intent(baseContext, MainActivity::class.java),
                    ActivityOptions.makeSceneTransitionAnimation(this@SplashActivity).toBundle())
                finish()
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {
            }

        })
    }
}