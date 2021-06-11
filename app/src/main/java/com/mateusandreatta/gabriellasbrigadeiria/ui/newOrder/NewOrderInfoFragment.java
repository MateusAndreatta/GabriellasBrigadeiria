package com.mateusandreatta.gabriellasbrigadeiria.ui.newOrder;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.mateusandreatta.gabriellasbrigadeiria.R;
import com.mateusandreatta.gabriellasbrigadeiria.Utils.Global;
import com.mateusandreatta.gabriellasbrigadeiria.model.Order;
import com.mateusandreatta.gabriellasbrigadeiria.model.Product;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewOrderInfoFragment extends Fragment {

    private static final String TAG = "TAG-NewOrderActivity";
    private NewOrderViewModel newOrderViewModel;
    private Spinner spinner;
    private ArrayAdapter<Product> spinnerAdapter;
    private ArrayAdapter<Product> listViewAdapter;
    private ListView listView;
    private View root;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_new_order_tab_info, container, false);
        Log.i(TAG,"onCreateView" );
        newOrderViewModel = new ViewModelProvider(requireActivity()).get(NewOrderViewModel.class);
        spinner = root.findViewById(R.id.spinnerProducts);
        listView = root.findViewById(R.id.listViewOrderProducts);
        spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, new ArrayList<>());
        listViewAdapter =  new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, new ArrayList<>());
        spinner.setAdapter(spinnerAdapter);
        listView.setAdapter(listViewAdapter);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Product product = listViewAdapter.getItem(i);
            showDialog(product, i);
        });

        root.findViewById(R.id.buttonAddProductToOrder).setOnClickListener(v -> {
            addProduct();
        });

        EditText dateInput = (EditText) root.findViewById(R.id.editTextOrderDate);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        dateInput.setText(simpleDateFormat.format(new Date()));

        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        newOrderViewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            spinnerAdapter.addAll(products);
            spinnerAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        });

        newOrderViewModel.getEditOrder().observe(getViewLifecycleOwner(), order -> {
            try {
                getOldOrderData(order);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Ocorreu um erro, tente novamente mais tarde.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void addProduct(){
        Product p = (Product) spinner.getSelectedItem();
        listViewAdapter.add(p);
    }

    private void showDialog(Product product, int i){
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        final View view = getLayoutInflater().inflate(R.layout.add_product_layout, null);
        EditText editTextProductName = (EditText) view.findViewById(R.id.editTextProductName);
        EditText editTextProductPrice = (EditText) view.findViewById(R.id.editTextDeliveryFee);

        editTextProductName.setText(product.getName());
        editTextProductPrice.setText(String.valueOf(product.getPrice()));

        alert.setTitle("Alterar produto");
        alert.setMessage("Insira o nome e o preço do produto");

        alert.setView(view);

        alert.setPositiveButton("Salvar", (dialog, whichButton) -> {

            String productName = editTextProductName.getText().toString();
            Double productPrice = Double.valueOf(editTextProductPrice.getText().toString());

            listViewAdapter.remove(product);
            listViewAdapter.add(new Product(productName, productPrice));
        });

        alert.setNegativeButton("Remover", (dialog, whichButton) -> {
            listViewAdapter.remove(product);
        });

        alert.show();
    }

    private void getOldOrderData(Order orderEdit) throws Exception {

        EditText orderDetails = root.findViewById(R.id.editTextDetails);
        EditText orderDate = root.findViewById(R.id.editTextOrderDate);
        EditText orderTime = root.findViewById(R.id.editTextOrderTime);

        orderDetails.setText(orderEdit.getDetails());
        orderDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(orderEdit.getDate()));
        orderTime.setText(orderEdit.getDeliveryTime());
        listViewAdapter.addAll(orderEdit.getProducts());

    }

}
