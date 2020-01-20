package com.mgkbadola.guessthemovie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.home.*

class Home : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)
        val int = Intent(this,Game::class.java)
        frnd.setOnClickListener {
            int.putExtra("game_type",1)
            startActivity(int)
        }
        comp.setOnClickListener {
            int.putExtra("game_type",2)
            startActivity(int)
        }
    }
}
