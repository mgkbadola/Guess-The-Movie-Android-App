package com.mgkbadola.guessthemovie

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.game.*
import kotlin.random.Random


class Game : AppCompatActivity() {
    lateinit var ref:DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game)

        ref = FirebaseDatabase.getInstance().reference

        var movie = ""
        var lives = 0
        //val mv = Movie(arrayListOf("Andaz Apna Apna", "Housefull", "Agent Vinod", "Qayamat Se Qayamat Tak"))
        val extras = intent.extras
        if(extras!!["game_type"]==1) {
            mvsubmit.setOnClickListener {
                if (mvnm_et.text.toString().trim().isEmpty()||cat_et.text.toString().trim().isEmpty()) {
                    Toast.makeText(this, "Name field is blank!", Toast.LENGTH_SHORT).show()
                }
                else {
                    mvnm_et.visibility = View.GONE
                    cat_et.visibility = View.GONE
                    mvsubmit.visibility = View.GONE
                    val category = cat_et.text.toString()
                    val t = StringBuilder(mvnm_et.text.toString())
                    t[0]=t[0].toUpperCase()
                    for(i in t.indices){
                        if(t[i]==' ')
                            t[i+1]=t[i+1].toUpperCase()
                    }
                    movie = t.toString()
                    mvnm_et.text.clear()
                    cat_et.text.clear()

                    saveMovietoDatabase(movie,category)

                    var len = 0
                    for (char in movie) {
                        if (char == ' ')
                            mvGuess.append("/ ")
                        else {
                            mvGuess.append("_ ")
                            len++
                        }
                    }
                    lives = len / 2 + 1
                    if (lives > 7)
                        lives = 7
                    cntlyf.append(" $lives")

                    mvGuess.visibility = View.VISIBLE
                    charac.visibility = View.VISIBLE
                    charsub.visibility = View.VISIBLE
                    letters.visibility = View.VISIBLE
                    cntlyf.visibility = View.VISIBLE
                }
            }
        }
        /*else if(extras["game_type"]==2){
            var str = ""
            var count=0
            mvnm_et.visibility = View.INVISIBLE
            cat_et.visibility = View.INVISIBLE
            mvsubmit.visibility = View.INVISIBLE
            //need to fix this
            ref.addValueEventListener(object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        for (h in p0.children)
                            ++count
                    }
                }
            })
            movie = str

            var len = 0
            for (char in movie) {
                if (char == ' ')
                    mvGuess.append("/ ")
                else {
                    mvGuess.append("_ ")
                    len++
                }
            }
            lives = len / 2 + 1
            if (lives > 7)
                lives = 7
            cntlyf.append(" $lives")

            mvGuess.visibility = View.VISIBLE
            charac.visibility = View.VISIBLE
            charsub.visibility = View.VISIBLE
            letters.visibility = View.VISIBLE
            cntlyf.visibility = View.VISIBLE
        }*/
        charsub.setOnClickListener {
            if(!charac.text.trim().isEmpty()) {
                val char = charac.text.toString()
                charac.text.clear()
                if (!movie.contains(char.toUpperCase()) && !movie.contains(char.toLowerCase())) {
                    lives--
                    letters.append(char.toLowerCase())
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
    private fun saveMovietoDatabase(mvname: String, category: String){
        var flag=1
        ref.orderByChild("name").equalTo(mvname).addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot){
                if(p0.exists()) {
                        flag=0
                    }
            }
        })
        if(flag!=0){
            val movie=Movie(mvname,category)
            ref.child(mvname).setValue(movie)
        }
    }
}
