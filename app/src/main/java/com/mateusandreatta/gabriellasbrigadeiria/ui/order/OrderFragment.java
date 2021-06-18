package com.mateusandreatta.gabriellasbrigadeiria.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mateusandreatta.gabriellasbrigadeiria.DatePickerFragment;
import com.mateusandreatta.gabriellasbrigadeiria.NewOrderActivity;
import com.mateusandreatta.gabriellasbrigadeiria.OrderDataModel;
import com.mateusandreatta.gabriellasbrigadeiria.OrdersArrayAdapter;
import com.mateusandreatta.gabriellasbrigadeiria.R;
import com.mateusandreatta.gabriellasbrigadeiria.Utils.Global;
import com.mateusandreatta.gabriellasbrigadeiria.Utils.Status;
import com.mateusandreatta.gabriellasbrigadeiria.databinding.FragmentOrderBinding;
import com.mateusandreatta.gabriellasbrigadeiria.model.Order;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class OrderFragment extends Fragment {

    private static final String TAG = "TAG-OrderFragment";

    private OrderViewModel orderViewModel;
    private FragmentOrderBinding binding;
    private RecyclerView recyclerView;
    private OrdersArrayAdapter adapter;
    private FirebaseFirestore db;
    private OrderDataModel dataModel;
    private ProgressBar loading;
    private LottieAnimationView animationView;
    private TextView textViewNoOrdersFound, textViewTotalProductsValue, textViewTotalDeliveryFeeValue;
    private CardView cardViewTotalValue;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        binding = FragmentOrderBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.floatingActionButtonAddOrder.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), NewOrderActivity.class));
        });
        dataModel = OrderDataModel.getInstance();
        db = FirebaseFirestore.getInstance();

        orderViewModel.getSelectedDate().observe(getViewLifecycleOwner(), date -> {
            Log.i(TAG,"Update date");
            dataModel.orderArrayList.clear();
            adapter.notifyDataSetChanged();
            loading.setVisibility(View.VISIBLE);
            loadOrders(date);
        });

        loading = binding.progressBarOrders;
        animationView = binding.animationView;
        recyclerView = binding.RecyclerViewOrders;
        textViewNoOrdersFound = binding.textViewNoOrdersFound;
        cardViewTotalValue = binding.cardViewTotalValue;
        textViewTotalProductsValue = binding.textViewTotalProductsValue;
        textViewTotalDeliveryFeeValue = binding.textViewTotalDeliveryFeeValue;

        adapter = new OrdersArrayAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setClickListener(new OrdersArrayAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                View viewDetails = view.findViewById(R.id.DetailsLayout);
                if(viewDetails.getVisibility() == View.VISIBLE)
                    viewDetails.setVisibility(View.GONE);
                else
                    viewDetails.setVisibility(View.VISIBLE);
            }

            @Override
            public boolean onItemLongClick(int position, View view) {

                Order order = dataModel.orderArrayList.get(position);

                AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
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

        DialogFragment newFragment = new DatePickerFragment(orderViewModel);
        binding.imageButtonCalendarSetDate.setOnClickListener(v -> {
            newFragment.show(getParentFragmentManager(), "datePicker");
        });

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper
                .SimpleCallback(0, ItemTouchHelper.LEFT |
                ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder,
                                 int direction) {

                int adapterPosition = viewHolder.getAdapterPosition();
                Order order = dataModel.orderArrayList.get(adapterPosition);

                if(order.getStatus().equals(Status.EM_ANDAMENTO))
                    order.setStatus(Status.CONCLUIDO);
                else
                    order.setStatus(Status.EM_ANDAMENTO);
                db.collection("orders").document(order.getFirestoreId()).set(order);
            }
        });

        helper.attachToRecyclerView(recyclerView);
    }

    private void loadOrders(Date date){

        loading.setVisibility(View.VISIBLE);

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startDate = cal.getTime();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Date endDate = cal.getTime();
        
        Log.i(TAG, startDate.toString());
        Log.i(TAG, endDate.toString());
        binding.textViewDateFilter.setText(new SimpleDateFormat("dd/MM/yyyy").format(date));
        db.collection("orders")
                .whereGreaterThanOrEqualTo("date", startDate)
                .whereLessThanOrEqualTo("date", endDate)
                .orderBy("date")
                .orderBy("status", Query.Direction.DESCENDING)
                .orderBy("deliveryTime")
                .addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.w("loadOrders", "Listen failed.", error);
                return;
            }
            dataModel.orderArrayList.clear();
            for (QueryDocumentSnapshot doc : value) {
                Order order = doc.toObject(Order.class);
                if(order.isEnable())
                    dataModel.orderArrayList.add(order);
            }
            adapter.notifyDataSetChanged();

            updateUI();
        });
    }

    private void updateUI(){
        loading.setVisibility(View.GONE);
        if(dataModel.orderArrayList.isEmpty()){
            animationView.setVisibility(View.VISIBLE);
            textViewNoOrdersFound.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            cardViewTotalValue.setVisibility(View.GONE);
        }else{
            animationView.setVisibility(View.GONE);
            textViewNoOrdersFound.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            cardViewTotalValue.setVisibility(View.VISIBLE);

            double totalProductsValue = 0d;
            double totalDeliveryFee = 0d;

            for (Order order : dataModel.orderArrayList) {
                totalDeliveryFee += order.getDeliveryFee();
                totalProductsValue += order.getTotal() - order.getDeliveryFee();
            }
            textViewTotalProductsValue.setText(Global.formatCurrencyDoubleValue(totalProductsValue));
            textViewTotalDeliveryFeeValue.setText(Global.formatCurrencyDoubleValue(totalDeliveryFee));

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}