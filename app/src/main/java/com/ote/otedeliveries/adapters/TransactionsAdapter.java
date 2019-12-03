package com.ote.otedeliveries.adapters;

import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.ote.otedeliveries.R;
import com.ote.otedeliveries.models.Transaction;
import com.ote.otedeliveries.utils.Utils;

import java.util.ArrayList;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.DataViewHolder>{
    private ArrayList<Transaction> dataList;

    public TransactionsAdapter(Context context, ArrayList<Transaction> dataList){
        this.dataList = dataList;
    }

    @Override
    @NonNull
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item, parent, false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder viewHolder, final int position) {
        viewHolder.setData(dataList.get(position));
    }

    @Override
    public int getItemCount(){
        return dataList.size();
    }

    static class DataViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView transactionID;
        AppCompatTextView transactionType;
        AppCompatTextView transactionDate;

        DataViewHolder(View itemView) {
            super(itemView);

            transactionID = itemView.findViewById(R.id.transactionID);
            transactionType = itemView.findViewById(R.id.transactionType);
            transactionDate = itemView.findViewById(R.id.transactionDate);
        }

        private void setData(Transaction transaction){
            transactionID.setText(transaction.getTransactionID());
            transactionType.setText(transaction.getTransactionType());
            transactionDate.setText(Utils.getFormattedDateSimple(transaction.getTransactionDateTime()));
        }
    }

    public static class ItemTouchListener implements RecyclerView.OnItemTouchListener{
        private GestureDetector gestureDetector;
        private OrdersAdapter.ClickListener clickListener;

        public ItemTouchListener(Context context, final RecyclerView recyclerView, final OrdersAdapter.ClickListener clickListener){
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e){
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e){
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());

                    if(child != null && clickListener != null)
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e){

            View child = rv.findChildViewUnder(e.getX(), e.getY());

            if(child != null && clickListener != null && gestureDetector.onTouchEvent(e))
                clickListener.onClick(child, rv.getChildPosition(child));

            return false;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e){

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept){

        }
    }

    public interface ClickListener{
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }
}