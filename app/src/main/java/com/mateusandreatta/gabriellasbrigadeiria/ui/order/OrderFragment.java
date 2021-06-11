package com.mateusandreatta.gabriellasbrigadeiria.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

                Order order = dataModel.orderArrayList.get(position);

                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle(order.getClient().getName() != null ? "Pedido de: " + order.getClient().getName() : "Alterar pedido");
                alert.setMessage("O que deseja fazer?");


                alert.setPositiveButton("Editar", (dialog, whichButton) -> {
                    Intent intent = new Intent(getContext(), NewOrderActivity.class);
                    intent.putExtra("order", order);
                    startActivity(intent);
                });

                alert.setNegativeButton("Remover", (dialog, whichButton) -> {
                    order.setEnable(false);
                    db.collection("orders").document(order.getFirestoreId())
                            .set(order).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Pedido removido.", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getActivity(), "Erro ao remover o pedido.", Toast.LENGTH_SHORT).show();
                        }
                    });
                });

                alert.setNeutralButton("Cancelar", (dialog, whichButton) -> {

                });

                alert.show();

                return true;
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return root;
    }


    //TODO: Filtro por data
    private void loadOrders(){
        db.collection("orders")
                .whereEqualTo("enable",true)
                .addSnapshotListener((value, error) -> {
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