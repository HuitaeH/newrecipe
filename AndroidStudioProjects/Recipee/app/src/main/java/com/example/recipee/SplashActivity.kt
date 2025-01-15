package com.example.recipee

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.view.animation.BounceInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val utensilIcon: ImageView = findViewById(R.id.ic_utensil)
        val recipeeText: TextView = findViewById(R.id.recipee_text)

        // Step 1: Create the bounce animation
        val bounceAnimator = ObjectAnimator.ofFloat(utensilIcon, "translationY", 0f, -50f, 0f)
        bounceAnimator.duration = 800
        bounceAnimator.interpolator = BounceInterpolator()

        // Step 2: Create the shift animation
        val shiftAnimator = ObjectAnimator.ofFloat(utensilIcon, "translationX", 0f, -200f)
        shiftAnimator.duration = 500

        // Step 3: Chain animations
        bounceAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // Start the shift animation after the bounce animation ends
                shiftAnimator.start()
            }
        })

        shiftAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // Make the text visible after the shift animation ends
                recipeeText.visibility = android.view.View.VISIBLE
            }
        })

        // Start the first animation
        bounceAnimator.start()

        // Transition to the next activity after the animations and splash
        Handler().postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, 3000) // Adjusted delay to match animation duration
    }
}
