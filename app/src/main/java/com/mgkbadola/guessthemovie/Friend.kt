package com.mgkbadola.guessthemovie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_friend.*
import java.lang.StringBuilder

class Friend : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend)
        var movie = ""
        var lives = 0
        mvsubmit.setOnClickListener{
            if(mvnm_et.text.toString().trim().isEmpty()){
                Toast.makeText(this,"Name field is blank!",Toast.LENGTH_SHORT).show()
            }
            else{
                mvnm_et.visibility = View.INVISIBLE
                mvsubmit.visibility = View.INVISIBLE
                movie = mvnm_et.text.toString()
                for(char in movie){
                    if(char == ' ')
                        mvGuess.append("/ ")
                    else
                        mvGuess.append("_ ")
                }
                lives = movie.length/2 + 1
                mvGuess.visibility = View.VISIBLE
                charac.visibility = View.VISIBLE
                charsub.visibility = View.VISIBLE
            }
        }
        charsub.setOnClickListener {
            if(!charac.text.trim().isEmpty()) {
                val char = charac.text.toString()
                charac.text.clear()
                if (!movie.contains(char.toUpperCase()) && !movie.contains(char.toLowerCase())) {
                    lives--
                    if (lives == 0) {
                        Toast.makeText(this, "Game Over!", Toast.LENGTH_SHORT).show()
                        mvGuess.text = movie
                        charac.visibility=View.INVISIBLE
                        charsub.visibility=View.INVISIBLE
                        status.text = "Movie was NOT guessed!"
                    } else
                        Toast.makeText(this, "Lives remaining: $lives", Toast.LENGTH_SHORT).show()

                }
                else{
                    val partmovie = StringBuilder(mvGuess.text.toString())
                    for(i in movie.indices){
                        if(movie[i]==char[0].toLowerCase() || movie[i]==char[0].toUpperCase())
                            partmovie.setCharAt(2*i,movie[i])
                    }
                    mvGuess.text=partmovie
                    if(!partmovie.contains('_')){
                        mvGuess.text=movie
                        charac.visibility=View.INVISIBLE
                        charsub.visibility=View.INVISIBLE
                        Toast.makeText(this, "CONGRATS! YOU WON!", Toast.LENGTH_SHORT).show()
                        status.text = "Movie was guessed!"
                    }

                }
            }
        }
    }
}
