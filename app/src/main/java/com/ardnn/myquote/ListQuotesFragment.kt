package com.ardnn.myquote

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ardnn.myquote.databinding.FragmentListQuotesBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import java.lang.Exception


class ListQuotesFragment : Fragment() {

    companion object {
        private val TAG = ListQuotesFragment::class.java.simpleName
    }

    private var _binding: FragmentListQuotesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentListQuotesBinding.inflate(inflater, container, false)

        // setup recyclerview
        val layoutManager = LinearLayoutManager(activity)
        binding.rvQuotes.layoutManager = layoutManager

        // load list of quotes
        getListQuotes()

        // if button clicked
        binding.btnRefresh.setOnClickListener {
            getListQuotes()
        }
        binding.btnBack.setOnClickListener { view ->
            view.findNavController().navigate(
                R.id.action_listQuotesFragment_to_homeFragment
            )
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun getListQuotes() {
        // show progress bar, hide recyclerview and disabel btn refresh
        with (binding) {
            progressBar.visibility = View.VISIBLE
            rvQuotes.visibility = View.INVISIBLE
            btnRefresh.isEnabled = false
        }

        // create client object and fetch data from the given API url
        val client = AsyncHttpClient()
        val url = "https://quote-api.dicoding.dev/list"

        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>,
                responseBody: ByteArray
            ) {
                // hide progress bar, show recyclerview, and enable btn refresh
                with (binding) {
                    progressBar.visibility = View.INVISIBLE
                    rvQuotes.visibility = View.VISIBLE
                    btnRefresh.isEnabled = true
                }

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
                    Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
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
                Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }
}

