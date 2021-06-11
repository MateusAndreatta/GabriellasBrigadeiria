package com.mateusandreatta.gabriellasbrigadeiria.ui.newOrder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.mateusandreatta.gabriellasbrigadeiria.NewOrderActivity;
import com.mateusandreatta.gabriellasbrigadeiria.R;
import com.mateusandreatta.gabriellasbrigadeiria.Utils.Global;
import com.mateusandreatta.gabriellasbrigadeiria.Utils.Status;
import com.mateusandreatta.gabriellasbrigadeiria.model.Client;
import com.mateusandreatta.gabriellasbrigadeiria.model.Order;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;

public class NewOrderClientFragment extends Fragment {

    private View root;
    private NewOrderViewModel newOrderViewModel;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_new_order_tab_client, container, false);
        newOrderViewModel = new ViewModelProvider(requireActivity()).get(NewOrderViewModel.class);

        EditText address = root.findViewById(R.id.editTextClientAddress);
        EditText addressDetails = root.findViewById(R.id.editTextClientAddressDetails);
        EditText deliveryFee = root.findViewById(R.id.editTextDeliveryFee);

        CheckBox checkbox = root.findViewById(R.id.checkBox);
        checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
//                address.setEnabled(!isChecked);
//                addressDetails.setEnabled(!isChecked);
//                deliveryFee.setEnabled(!isChecked);

            if(isChecked){
                address.setVisibility(View.GONE);
                addressDetails.setVisibility(View.GONE);
                deliveryFee.setVisibility(View.GONE);
            }else{
                address.setVisibility(View.VISIBLE);
                addressDetails.setVisibility(View.VISIBLE);
                deliveryFee.setVisibility(View.VISIBLE);
            }

        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        newOrderViewModel.getEditOrder().observe(getViewLifecycleOwner(), order -> {
            try {
                getOldOrderData(order);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Ocorreu um erro, tente novamente mais tarde.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getOldOrderData(Order orderEdit) throws Exception {

        EditText phone = root.findViewById(R.id.editTextClientPhone);
        EditText name = root.findViewById(R.id.editTextClientName);
        EditText address = root.findViewById(R.id.editTextClientAddress);
        EditText addressDetails = root.findViewById(R.id.editTextClientAddressDetails);

        phone.setText(orderEdit.getClient().getPhone());
        name.setText(orderEdit.getClient().getName());
        address.setText(orderEdit.getClient().getAddress());
        addressDetails.setText(orderEdit.getClient().getAddressDetails());

        EditText deliveryFee = root.findViewById(R.id.editTextDeliveryFee);
        CheckBox checkboxDelivery = root.findViewById(R.id.checkBox);

        deliveryFee.setText(Global.formatCurrencyDoubleValue(orderEdit.getDeliveryFee()));
        checkboxDelivery.setChecked(!orderEdit.isDelivery());

    }


}
