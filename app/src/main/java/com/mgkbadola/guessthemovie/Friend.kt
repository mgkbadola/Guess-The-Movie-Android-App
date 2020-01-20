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
                mvnm_et.visibility = View.GONE
                mvsubmit.visibility = View.GONE

                movie = mvnm_et.text.toString()
                mvnm_et.text.clear()
                var len = 0
                for(char in movie){
                    if(char == ' ')
                        mvGuess.append("/ ")
                    else{
                        mvGuess.append("_ ")
                        len++
                    }
                }
                lives = len/2 + 1
                if(lives > 9)
                    lives=9
                cntlyf.append(" $lives")

                mvGuess.visibility = View.VISIBLE
                charac.visibility = View.VISIBLE
                charsub.visibility = View.VISIBLE
                letters.visibility = View.VISIBLE
                cntlyf.visibility = View.VISIBLE
            }
        }
        charsub.setOnClickListener {
            if(!charac.text.trim().isEmpty()) {
                val char = charac.text.toString()
                charac.text.clear()
                if (!movie.contains(char.toUpperCase()) && !movie.contains(char.toLowerCase())) {
                    lives--
                    letters.append(char)
                    if (lives == 0) {
                        Toast.makeText(this, "Game Over!", Toast.LENGTH_SHORT).show()
                        mvGuess.text = movie

                        charac.visibility = View.GONE
                        cntlyf.visibility = View.GONE
                        charsub.visibility = View.GONE
                        letters.visibility = View.GONE

                        status.text = "SORRY! The movie was NOT guessed!"
                    }
                    else{
                        cntlyf.text="Lives remaining: $lives"
                    }

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
                        status.text = "CONGRATS! The movie was guessed!"
                    }

                }
            }
        }
    }
}
