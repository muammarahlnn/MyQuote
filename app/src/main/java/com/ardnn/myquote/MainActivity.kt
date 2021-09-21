package com.ardnn.myquote

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.ardnn.myquote.databinding.ActivityMainBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getRandomQuote()

        // if button clicked
        binding.btnAllQuotes.setOnClickListener {
            val toListQuotes = Intent(this@MainActivity, ListQuotesActivity::class.java)
            startActivity(toListQuotes)
        }
    }

    private fun getRandomQuote() {
        // show progress bar
        binding.progressBar.visibility = View.VISIBLE

        // create client and fetch data from given url API
        val client = AsyncHttpClient()
        val url = "https://quote-api.dicoding.dev/random"

        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>,
                responseBody: ByteArray
            ) {
                // hide progress bar
                binding.progressBar.visibility = View.INVISIBLE

                // get response
                val result = String(responseBody)
                Log.d(TAG, result)

                // parsing response json
                try {
                    val responseObject = JSONObject(result)
                    val quote = responseObject.getString("en")
                    val author = responseObject.getString("author")

                    // set it to widgets
                    with (binding) {
                        tvQuote.text = quote
                        tvAuthor.text = author
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }

            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>,
                responseBody: ByteArray,
                error: Throwable
            ) {
                // hide progress bar
                binding.progressBar.visibility = View.INVISIBLE

                // show error message
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }

        })
    }
}