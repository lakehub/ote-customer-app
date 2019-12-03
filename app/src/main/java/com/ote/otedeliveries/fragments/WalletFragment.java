package com.ote.otedeliveries.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ote.otedeliveries.R;
import com.ote.otedeliveries.adapters.TransactionsAdapter;
import com.ote.otedeliveries.models.Transaction;
import com.ote.otedeliveries.utils.OteRecyclerView;
import com.ote.otedeliveries.utils.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class WalletFragment extends Fragment {
    @BindView(R.id.empty_view) View empty_view;
    @BindView(R.id.transactions_recyclerview) OteRecyclerView transactions_recyclerview;

    public static WalletFragment newInstance(){
        return new WalletFragment();
    }

    public WalletFragment(){
    }

    private TransactionsAdapter transactionsAdapter;

    private Realm realm;

    private RealmResults<Transaction> resultList;
    private ArrayList<Transaction> ordersDataList = new ArrayList<>();

    private RealmChangeListener<RealmResults<Transaction>> transactionsListChangeListener = new RealmChangeListener<RealmResults<Transaction>>(){
        @Override
        public void onChange(@NonNull RealmResults<Transaction> element){
            if(transactionsAdapter != null && ordersDataList != null){
                ordersDataList.clear();
                ordersDataList.addAll(realm.copyFromRealm(element));
                transactionsAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(resultList != null)
            resultList.removeChangeListener(transactionsListChangeListener);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);

        ButterKnife.bind(this, view);

        realm = Utils.getRealmInstance();

        transactions_recyclerview.setEmptyView(empty_view);
        transactions_recyclerview.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        RealmQuery<Transaction> query = realm.where(Transaction.class);
        resultList = query.sort("transactionDateTime", Sort.ASCENDING).findAll();

        ordersDataList.clear();
        ordersDataList.addAll(realm.copyFromRealm(resultList));

        transactionsAdapter = new TransactionsAdapter(this.getActivity(), ordersDataList);
        transactions_recyclerview.setAdapter(transactionsAdapter);

        resultList.addChangeListener(transactionsListChangeListener);

        return view;
    }
}
