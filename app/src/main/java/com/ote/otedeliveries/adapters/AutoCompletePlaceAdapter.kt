package com.ote.otedeliveries.adapters

import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.ote.otedeliveries.R
import com.ote.otedeliveries.callbacks.AutoCompletePlaceCallback
import java.util.*
import kotlin.collections.ArrayList

class AutoCompletePlaceAdapter(private val callback: AutoCompletePlaceCallback) :
    RecyclerView.Adapter<AutoCompletePlaceAdapter.MyViewHolder>() {

    private var data: ArrayList<AutocompletePrediction> = ArrayList()
    private var query: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.place_item, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) = holder.bind(data[position])

    fun swapData(data: ArrayList<AutocompletePrediction>, query: String) {
        this.query = query
        this.data = data
        notifyDataSetChanged()
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvPrimary: TextView = itemView.findViewById(R.id.tvPrimary)
        private val tvSecondary: TextView = itemView.findViewById(R.id.tvSecondary)
        fun bind(item: AutocompletePrediction) = with(itemView) {
            tvSecondary.text = item.getSecondaryText(null)
            tvPrimary.text = item.getPrimaryText(null)

            val spannableStr = SpannableString(item.getPrimaryText(null))

            val clickableSpan = object : ClickableSpan() {
                override fun onClick(view: View) {

                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                    ds.color = ContextCompat.getColor(tvPrimary.context, R.color.colorPrimary)
                }
            }

            if (item.getPrimaryText(null).toString().toLowerCase(Locale.getDefault()).contains(query.toLowerCase(Locale.getDefault()))) {
                val start = item.getPrimaryText(null).toString().toLowerCase(Locale.getDefault())
                    .indexOf(query.toLowerCase(Locale.getDefault()))
                val end = start.plus(query.length)

                spannableStr.setSpan(
                    clickableSpan,
                    start,
                    end,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                tvPrimary.text = spannableStr
            } else {
                spannableStr.setSpan(
                    clickableSpan,
                    0,
                    item.getPrimaryText(null).toString().length,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                tvPrimary.text = spannableStr
            }

            setOnClickListener {
                this@AutoCompletePlaceAdapter.callback.onClickCallback(item)
            }
        }
    }
}