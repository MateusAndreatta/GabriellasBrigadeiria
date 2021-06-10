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

import com.mateusandreatta.gabriellasbrigadeiria.NewOrderActivity;
import com.mateusandreatta.gabriellasbrigadeiria.R;
import com.mateusandreatta.gabriellasbrigadeiria.Utils.Status;
import com.mateusandreatta.gabriellasbrigadeiria.model.Client;
import com.mateusandreatta.gabriellasbrigadeiria.model.Order;

public class NewOrderClientFragment extends Fragment {

    private View root;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_new_order_tab_client, container, false);

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



}
