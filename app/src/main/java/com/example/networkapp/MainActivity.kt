package com.example.networkapp

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException

// TODO (1: Fix any bugs)
// TODO (2: Add function saveComic(...) to save and load comic info automatically when app starts)

private const val AUTO_SAVE_KEY = "auto_save"

class MainActivity : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    lateinit var titleTextView: TextView
    lateinit var descriptionTextView: TextView
    lateinit var numberEditText: EditText
    lateinit var showButton: Button
    lateinit var comicImageView: ImageView
    lateinit var jsonObject : VolleyError

    private lateinit var preferences: SharedPreferences

    private val internalFilename = "my_file"
    private lateinit var comicFile: File

    private var autoSave = false




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestQueue = Volley.newRequestQueue(this)

        titleTextView = findViewById<TextView>(R.id.comicTitleTextView)
        descriptionTextView = findViewById<TextView>(R.id.comicDescriptionTextView)
        numberEditText = findViewById<EditText>(R.id.comicNumberEditText)
        showButton = findViewById<Button>(R.id.showComicButton)
        comicImageView = findViewById<ImageView>(R.id.comicImageView)

        preferences = getPreferences(MODE_PRIVATE)
        autoSave = preferences.getBoolean(AUTO_SAVE_KEY, false)



        showButton.setOnClickListener {
            downloadComic(numberEditText.text.toString())
        }

        comicFile = File(filesDir, internalFilename)

        if(comicFile.exists()){
            showComic(loadComic())
        }

    }

    private fun downloadComic (comicId: String) {
        val url = "https://xkcd.com/$comicId/info.0.json"
        val comicName = "$comicId"
        comicFile = File(filesDir, comicName)
        requestQueue.add (
            JsonObjectRequest(url, {
                saveComic(it)
                showComic(it)}, {
            })
        )
    }

    private fun showComic (comicObject: JSONObject) {
        titleTextView.text = comicObject.getString("title")
        descriptionTextView.text = comicObject.getString("alt")
        Picasso.get().load(comicObject.getString("img")).into(comicImageView)
    }

    private fun saveComic(comicObject: JSONObject) {
        val comicId = comicObject.getString("num")
        val comicName = "$comicId"
        comicFile = File(filesDir, internalFilename)
        val outputStream = FileOutputStream(comicFile)
        outputStream.write(comicObject.toString().toByteArray())
        Log.d("saving", outputStream.toString())
        outputStream.close()
    }

    private fun loadComic() : JSONObject {
            val br = BufferedReader(FileReader(comicFile))
            val text = StringBuilder()
            var line: String?
        while (br.readLine().also { line = it } != null) {
            text.append(line)
        }
            br.close()
        return JSONObject(text.toString())
    }



}