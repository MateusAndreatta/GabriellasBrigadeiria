package com.mateusandreatta.gabriellasbrigadeiria.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mateusandreatta.gabriellasbrigadeiria.NewOrderActivity;
import com.mateusandreatta.gabriellasbrigadeiria.OrderDataModel;
import com.mateusandreatta.gabriellasbrigadeiria.OrdersArrayAdapter;
import com.mateusandreatta.gabriellasbrigadeiria.R;
import com.mateusandreatta.gabriellasbrigadeiria.databinding.FragmentOrderBinding;
import com.mateusandreatta.gabriellasbrigadeiria.model.Order;
import com.mateusandreatta.gabriellasbrigadeiria.model.Product;

public class OrderFragment extends Fragment {

    private OrderViewModel orderViewModel;
    private FragmentOrderBinding binding;
    private RecyclerView recyclerView;
    private OrdersArrayAdapter adapter;
    private FirebaseFirestore db;
    private OrderDataModel dataModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        orderViewModel =
                new ViewModelProvider(this).get(OrderViewModel.class);

        binding = FragmentOrderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.floatingActionButtonAddOrder.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), NewOrderActivity.class));
        });
        dataModel = OrderDataModel.getInstance();
        db = FirebaseFirestore.getInstance();

        loadOrders();

        recyclerView = binding.RecyclerViewOrders;
        adapter = new OrdersArrayAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setClickListener(new OrdersArrayAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View view) {
            }

            @Override
            public boolean onItemLongClick(int position, View view) {
                return true;
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return root;
    }


    private void loadOrders(){
        db.collection("orders").addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.w("loadOrders", "Listen failed.", error);
                return;
            }
            dataModel.orderArrayList.clear();
            for (QueryDocumentSnapshot doc : value) {
                Order order = doc.toObject(Order.class);
                dataModel.orderArrayList.add(order);
            }
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}