package com.example.recipee

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // 앱 시작 시 HomeFragment를 기본으로 표시
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }

        // 네비게이션 아이콘 색상 설정
        bottomNavigation.itemIconTintList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(-android.R.attr.state_checked)
            ),
            intArrayOf(
                ContextCompat.getColor(this, R.color.selected_color),
                ContextCompat.getColor(this, R.color.unselected_color)
            )
        )

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