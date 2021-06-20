package com.mateusandreatta.gabriellasbrigadeiria.ui.delivery;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mateusandreatta.gabriellasbrigadeiria.DatePickerFragment;
import com.mateusandreatta.gabriellasbrigadeiria.DeliveryArrayAdapter;
import com.mateusandreatta.gabriellasbrigadeiria.NewOrderActivity;
import com.mateusandreatta.gabriellasbrigadeiria.OrderDataModel;
import com.mateusandreatta.gabriellasbrigadeiria.OrdersArrayAdapter;
import com.mateusandreatta.gabriellasbrigadeiria.R;
import com.mateusandreatta.gabriellasbrigadeiria.Utils.Global;
import com.mateusandreatta.gabriellasbrigadeiria.Utils.Status;
import com.mateusandreatta.gabriellasbrigadeiria.databinding.FragmentDeliveryBinding;
import com.mateusandreatta.gabriellasbrigadeiria.databinding.FragmentOrderBinding;
import com.mateusandreatta.gabriellasbrigadeiria.model.Order;
import com.mateusandreatta.gabriellasbrigadeiria.ui.order.OrderViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DeliveryFragment extends Fragment {

    private DeliveryViewModel deliveryViewModel;

    private static final String TAG = "TAG-DeliveryFragment";

    private FragmentDeliveryBinding binding;
    private RecyclerView recyclerView;
    private DeliveryArrayAdapter adapter;
    private FirebaseFirestore db;
    private OrderDataModel dataModel;
    private ProgressBar loading;
    private LottieAnimationView animationView;
    private TextView textViewNoOrdersFound;
    private ArrayList<Order> ordersSelected;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDeliveryBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        deliveryViewModel = new ViewModelProvider(this).get(DeliveryViewModel.class);

        binding.floatingActionButtonNavegate.setOnClickListener(v -> {
            if(!ordersSelected.isEmpty()){
                StringBuilder stringBuilder = new StringBuilder();
                for (Order order : ordersSelected) {
                    stringBuilder.append(order.getClient().getAddress().replace(" ","+")).append("|");
                }
                openGPS(stringBuilder.toString());
            }else{
                Toast.makeText(getContext(), "Nenhum pedido foi selecionado!", Toast.LENGTH_SHORT).show();
            }
        });
        dataModel = OrderDataModel.getInstance();
        db = FirebaseFirestore.getInstance();

        deliveryViewModel.getSelectedDate().observe(getViewLifecycleOwner(), date -> {
            Log.i(TAG,"Update date");
            dataModel.orderArrayList.clear();
            adapter.notifyDataSetChanged();
            loading.setVisibility(View.VISIBLE);
            loadOrders(date);
        });

        loading = binding.progressBarOrders;
        animationView = binding.animationView;
        recyclerView = binding.recyclerViewDelivery;
        textViewNoOrdersFound = binding.textViewNoOrdersFound;
        ordersSelected = new ArrayList<>();
        adapter = new DeliveryArrayAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setClickListener(new DeliveryArrayAdapter.ClickListener() {
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

                CardView viewCard = (CardView) view.findViewById(R.id.cardViewDelivery);
                Order order = dataModel.orderArrayList.get(position);
                ColorStateList cardBackgroundColor = viewCard.getCardBackgroundColor();
                if(cardBackgroundColor.getDefaultColor() == getContext().getColor(R.color.card_background_selected)){
                    viewCard.setCardBackgroundColor(Color.WHITE);
                    ordersSelected.remove(order);
                }else{
                    viewCard.setCardBackgroundColor(getContext().getColor(R.color.card_background_selected));
                    ordersSelected.add(order);
                }

                return true;
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        DialogFragment newFragment = new DatePickerFragment(deliveryViewModel);
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
                        if(order.isEnable() && order.isDelivery())
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
        }else{
            animationView.setVisibility(View.GONE);
            textViewNoOrdersFound.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void openGPS(String address){

        Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&dir_action=navigate&destination= "+ Global.gabriellasBrigadeiriaAddress + "&waypoints=" + address +"&travelmode=driving&dir_action=navigate");
        Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        intent.setPackage("com.google.android.apps.maps");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            try {
                Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                startActivity(unrestrictedIntent);
            } catch (ActivityNotFoundException innerEx) {
                Toast.makeText(getActivity(), R.string.toast_erro_maps, Toast.LENGTH_LONG).show();
            }
        }
    }


}