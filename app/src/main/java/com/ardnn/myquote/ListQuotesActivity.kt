package com.ardnn.myquote

import android.util.Log
import android.widget.Toast
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import java.lang.Exception
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ardnn.myquote.databinding.ActivityListQuotesBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler

class ListQuotesActivity : AppCompatActivity() {

    companion object {
        private val TAG = ListQuotesActivity::class.java.simpleName
    }

    private lateinit var binding: ActivityListQuotesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListQuotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // set action bar title
        supportActionBar?.title = "List of Quotes"

        // setup recyclerview
        val layoutManager = LinearLayoutManager(this)
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        with (binding) {
            rvQuotes.layoutManager = layoutManager
            rvQuotes.addItemDecoration(itemDecoration)
        }

        getListQuotes()
    }

    private fun getListQuotes() {
        // show progress bar
        binding.progressBar.visibility = View.VISIBLE

        // create client object and fetch data from the given API url
        val client = AsyncHttpClient()
        val url = "https://quote-api.dicoding.dev/list"

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

                // parsing json and put into a list
                val listQuotes = ArrayList<QuoteModel>()
                try {
                    val jsonArray = JSONArray(result)
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val quote = jsonObject.getString("en")
                        val author = jsonObject.getString("author")

                        val quoteModel = QuoteModel(quote, author)
                        listQuotes.add(quoteModel)
                    }

                    // set recyclerview adapter
                    val adapter = QuoteAdapter(listQuotes)
                    binding.rvQuotes.adapter = adapter

                } catch (e: Exception) {
                    Toast.makeText(this@ListQuotesActivity, e.message, Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this@ListQuotesActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

}