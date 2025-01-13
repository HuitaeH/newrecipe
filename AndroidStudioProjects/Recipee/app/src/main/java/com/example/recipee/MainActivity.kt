package com.example.recipee

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // HomeFragment로 전환
                    true
                }
                R.id.navigation_recipe -> {
                    // RecipeFragment로 전환
                    true
                }
                R.id.navigation_community -> {
                    // CommunityFragment로 전환
                    true
                }
                R.id.navigation_mypage -> {
                    // MyPageFragment로 전환
                    true
                }
                else -> false
            }
        }
    }
}