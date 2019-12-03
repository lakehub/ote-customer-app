package com.ote.otedeliveries.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ote.otedeliveries.R;
import com.ote.otedeliveries.activities.order.OrderDetailActivity;
import com.ote.otedeliveries.adapters.OrdersAdapter;
import com.ote.otedeliveries.models.Order;
import com.ote.otedeliveries.utils.OteRecyclerView;
import com.ote.otedeliveries.utils.Utils;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class HistoryFragment extends Fragment {
    @BindView(R.id.empty_view) View empty_view;
    @BindView(R.id.history_recyclerview) OteRecyclerView historyRecyclerView;

    private OrdersAdapter ordersAdapter;

    private Realm realm;

    private RealmResults<Order> resultList;
    private ArrayList<Order> ordersDataList = new ArrayList<>();

    private RealmChangeListener<RealmResults<Order>> ordersListChangeListener = new RealmChangeListener<RealmResults<Order>>(){
        @Override
        public void onChange(@NonNull RealmResults<Order> element){
            if(ordersAdapter != null && ordersDataList != null){
                ordersDataList.clear();
                ordersDataList.addAll(realm.copyFromRealm(element));
                ordersAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(resultList != null)
            resultList.removeChangeListener(ordersListChangeListener);
    }

    public static HistoryFragment newInstance(){
        return new HistoryFragment();
    }

    public HistoryFragment(){
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        ButterKnife.bind(this, view);

        realm = Utils.getRealmInstance();

        historyRecyclerView.setEmptyView(empty_view);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        RealmQuery<Order> query = realm.where(Order.class).equalTo("isBeingEdited", false);
        resultList = query.sort("dateCreated", Sort.ASCENDING).findAll();

        ordersDataList.clear();
        ordersDataList.addAll(realm.copyFromRealm(resultList));

        ordersAdapter = new OrdersAdapter(this.getActivity(), ordersDataList);
        historyRecyclerView.setAdapter(ordersAdapter);

        resultList.addChangeListener(ordersListChangeListener);

        historyRecyclerView.addOnItemTouchListener(new OrdersAdapter.ItemTouchListener(getActivity(), historyRecyclerView, new OrdersAdapter.ClickListener(){
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(new Intent(getActivity(), OrderDetailActivity.class));
                intent.putExtra("orderID", ((AppCompatTextView) view.findViewById(R.id.orderID)).getText().toString());
                startActivity(intent);

                Objects.requireNonNull(getActivity()).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return view;
    }
}