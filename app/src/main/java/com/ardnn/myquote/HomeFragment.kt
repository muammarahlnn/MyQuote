package com.ardnn.myquote

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.ardnn.myquote.databinding.FragmentHomeBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class HomeFragment : Fragment() {

    companion object {
        private val TAG = HomeFragment::class.java.simpleName
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // get random quote
        getRandomQuote()

        // if button clicked
        binding.btnListQuotes.setOnClickListener { view ->
            view.findNavController().navigate(
                R.id.action_homeFragment_to_listQuotesFragment
            )
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
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
                Log.d(HomeFragment.TAG, result)

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