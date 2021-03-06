package com.mgkbadola.guessthemovie

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.game.*
import kotlin.random.Random


class Game : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var db: FirebaseFirestore
    private lateinit var imm: InputMethodManager
        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game)
        db = FirebaseFirestore.getInstance()
        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        var movie = ""
        var category = ""
        var lives = 0
        val extras = intent.extras
        if (extras!!["game_type"] == 1) {
            cat_et.setOnItemSelectedListener(this)
            ArrayAdapter.createFromResource(
                this,
                R.array.category,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                cat_et.adapter = adapter
            }

            mvnm_et.requestFocus()
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            //done button = submit
            mvnm_et.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
                if (event != null && event.keyCode === KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                    mvsubmit.performClick()
                }
                false
            })

            mvsubmit.setOnClickListener {
                if (mvnm_et.text.toString().trim().isEmpty()) {
                    Toast.makeText(this, "Name field is blank!", Toast.LENGTH_SHORT).show()
                }
                else {
                    mvnm_et.visibility = View.INVISIBLE; mvnm_et.clearFocus()
                    cat_et.visibility = View.INVISIBLE;
                    mvsubmit.visibility = View.INVISIBLE;

                    val t = StringBuilder(mvnm_et.text.toString())
                    t[0] = t[0].toUpperCase()
                    for (i in t.indices) {
                        if (t[i] == ' ')
                            t[i + 1] = t[i + 1].toUpperCase()
                    }
                    movie = t.toString()
                    category = cat_et.selectedItem.toString()
                    mvnm_et.text.clear()
                    cat.text = category
                    saveMovietoDatabase(movie, category)

                    var len = 0
                    for (char in movie) {
                        if (char == ' ')
                            mvGuess.append("/ ")
                        else {
                            mvGuess.append("_ ")
                            len++
                        }
                    }
                    lives = len / 3 + 1
                    if (lives > 7)
                        lives = 7
                    cntlyf.append(" $lives")

                    cat.visibility = View.VISIBLE
                    mvGuess.visibility = View.VISIBLE
                    charac.visibility = View.VISIBLE
                    charsub.visibility = View.VISIBLE
                    letters.visibility = View.VISIBLE
                    cntlyf.visibility = View.VISIBLE
                    charac.requestFocus()
                }
            }
        }
        else if (extras["game_type"] == 2) {
            var count:Int
            mvnm_et.visibility = View.INVISIBLE; mvnm_et.clearFocus()
            cat_et.visibility = View.INVISIBLE;
            mvsubmit.visibility = View.INVISIBLE;
            //slows down
            db.collection("count").document("counter").get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val i: MutableMap<String, Any>? = document.data
                    count = (i!!["counter"] as Long).toInt()
                    count = Random.nextInt(0, count)
                    Log.i("value of count","$count")
                    db.collection("movies").get().addOnSuccessListener { documents ->
                        movie = documents.documents[count]["name"] as String
                        category = documents.documents[count]["category"] as String
                        cat.text = category
                        var len = 0
                        for (char in movie) {
                            if (char == ' ')
                                mvGuess.append("/ ")
                            else {
                                mvGuess.append("_ ")
                                len++
                            }
                        }
                        lives = len / 3 + 1
                        if (lives > 7)
                            lives = 7
                        cntlyf.append(" $lives")
                        mvGuess.visibility = View.VISIBLE
                        charac.visibility = View.VISIBLE
                        charsub.visibility = View.VISIBLE
                        letters.visibility = View.VISIBLE
                        cntlyf.visibility = View.VISIBLE
                        cat.visibility = View.VISIBLE
                        charac.requestFocus()
                    }

                }
            }
        }
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        //done button=submit
        charac.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (event != null && event.keyCode === KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                charsub.performClick()
            }
            false
        })

        charsub.setOnClickListener {
            if (!charac.text.trim().isEmpty()) {
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
                        cat.visibility = View.GONE

                        status.text = "SORRY! The movie was NOT guessed!"
                        imm.toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0)
                    } else {
                        cntlyf.text = "Lives remaining: $lives"
                    }

                } else {
                    val partmovie = StringBuilder(mvGuess.text.toString())
                    for (i in movie.indices) {
                        if (movie[i] == char[0].toLowerCase() || movie[i] == char[0].toUpperCase())
                            partmovie.setCharAt(2 * i, movie[i])
                    }
                    mvGuess.text = partmovie
                    if (!partmovie.contains('_')) {
                        mvGuess.text = movie
                        charac.visibility = View.INVISIBLE
                        charsub.visibility = View.INVISIBLE
                        cat.visibility = View.INVISIBLE
                        status.text = "CONGRATS! The movie was guessed!"
                    }

                }
            }
            charac.requestFocus()
        }
    }

    private fun saveMovietoDatabase(mvname: String, category: String) {
        val movie = Movie(mvname,category)
        val docRef = db.collection("movies").document(mvname)
        docRef.get().addOnSuccessListener{
                document ->
                if (!document.exists()) {
                    db.collection("movies").document(mvname).set(movie)
                    db.collection("count").document("counter").
                    update("counter",FieldValue.increment(1))
                }
            }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        parent!!.getItemAtPosition(position)
    }
}
