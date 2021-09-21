package com.ardnn.myquote

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ardnn.myquote.databinding.ItemRvQuotesBinding

class QuoteAdapter(
    private val listQuotes: ArrayList<QuoteModel>
) : RecyclerView.Adapter<QuoteAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rv_quotes, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(listQuotes[position])
    }

    override fun getItemCount(): Int {
        return listQuotes.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemRvQuotesBinding.bind(itemView)

        internal fun onBind(quoteModel: QuoteModel) {
            with (binding) {
                tvQuote.text = quoteModel.quote
                tvAuthor.text = quoteModel.author
            }
        }
    }
}