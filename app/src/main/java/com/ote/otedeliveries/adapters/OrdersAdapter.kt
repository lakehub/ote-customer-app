package com.ote.otedeliveries.adapters

import android.content.Context
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView

import com.ote.otedeliveries.R
import com.ote.otedeliveries.models.Order
import com.ote.otedeliveries.utils.Utils

import java.util.ArrayList

class OrdersAdapter(context: Context, private val dataList: ArrayList<Order>) : RecyclerView.Adapter<OrdersAdapter.DataViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_item, parent, false)
        return DataViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: DataViewHolder, position: Int) {
        viewHolder.setData(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var orderID: AppCompatTextView
        var orderDestination: AppCompatTextView
        var orderDate: AppCompatTextView
        var orderStatus: AppCompatTextView

        init {

            orderID = itemView.findViewById(R.id.orderID)
            orderDestination = itemView.findViewById(R.id.orderDestination)
            orderDate = itemView.findViewById(R.id.orderDate)
            orderStatus = itemView.findViewById(R.id.orderStatus)
        }

        fun setData(order: Order) {
            orderID.text = order.orderID
            orderDestination.text = order.dropOffAddress
            orderDate.text = Utils.getFormattedDateSimple(order.dateCreated * 1000)
            orderStatus.text = order.orderStatus

            if (order.orderStatus == orderStatus.context.getString(R.string.finished))
                orderStatus.setTextColor(orderStatus.context.resources.getColor(R.color.greenShade))
            else
                orderStatus.setTextColor(orderStatus.context.resources.getColor(R.color.colorRed))
        }
    }

    class ItemTouchListener(context: Context, recyclerView: RecyclerView, private val clickListener: ClickListener?) : RecyclerView.OnItemTouchListener {
        private val gestureDetector: GestureDetector

        init {
            gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    return true
                }

                override fun onLongPress(e: MotionEvent) {
                    val child = recyclerView.findChildViewUnder(e.x, e.y)

                    if (child != null && clickListener != null)
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child))
                }
            })
        }

        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {

            val child = rv.findChildViewUnder(e.x, e.y)

            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e))
                clickListener.onClick(child, rv.getChildPosition(child))

            return false
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

        }
    }

    interface ClickListener {
        fun onClick(view: View, position: Int)
        fun onLongClick(view: View?, position: Int)
    }
}