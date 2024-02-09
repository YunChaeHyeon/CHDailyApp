package com.example.mydailyapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ismaeldivita.chipnavigation.ChipNavigationBar


class MainActivity : AppCompatActivity() {

    val fragment = weather_Fragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        openMainFragment()
        supportActionBar?.hide()

        var menu_bottom = findViewById<ChipNavigationBar>(R.id.bottom_nav_bar)
        menu_bottom.setItemSelected(R.id.nav_weather)

        menu_bottom.setOnItemSelectedListener {
            when(it){
                R.id.nav_weather -> {
                    openMainFragment()
                }
                R.id.nav_accountBook -> {
                    val accountBookFragment = accountBook_Fragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frag_container_nav, accountBookFragment).commit()
                }
                R.id.nav_todoList -> {
                    val todoListFragment = TodoList_Fragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frag_container_nav, todoListFragment).commit()
                }
            }
        }
    }

    private fun openMainFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frag_container_nav, fragment)
        transaction.commit()
    }


}