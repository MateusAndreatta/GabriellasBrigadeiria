package com.mateusandreatta.gabriellasbrigadeiria;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mateusandreatta.gabriellasbrigadeiria.Utils.Status;
import com.mateusandreatta.gabriellasbrigadeiria.databinding.ActivityNewOrderBinding;
import com.mateusandreatta.gabriellasbrigadeiria.model.Client;
import com.mateusandreatta.gabriellasbrigadeiria.model.Order;
import com.mateusandreatta.gabriellasbrigadeiria.model.Product;
import com.mateusandreatta.gabriellasbrigadeiria.ui.newOrder.NewOrderClientFragment;
import com.mateusandreatta.gabriellasbrigadeiria.ui.newOrder.NewOrderInfoFragment;
import com.mateusandreatta.gabriellasbrigadeiria.ui.newOrder.NewOrderViewModel;
import com.mateusandreatta.gabriellasbrigadeiria.ui.newOrder.SectionsPagerAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewOrderActivity extends AppCompatActivity {

    private final String TAG = "TAG-NewOrderActivity";
    private NewOrderViewModel newOrderViewModel;

    private ActivityNewOrderBinding binding;
    private FirebaseFirestore db;
    private ArrayList<Product> products = new ArrayList<>();
    private NewOrderInfoFragment newOrderInfoFragment;
    private NewOrderClientFragment newOrderClientFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNewOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle(getResources().getString(R.string.menu_new_order));

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);

        newOrderViewModel = new ViewModelProvider(this).get(NewOrderViewModel.class);

        db = FirebaseFirestore.getInstance();
        loadProducts();
    }

    private void loadProducts(){
        db.collection("products").addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.w(TAG, "Listen failed.", error);
                return;
            }
            products.clear();
            for (QueryDocumentSnapshot doc : value) {
                if (doc.get("name") != null && doc.get("price") != null) {
                    Product product = new Product(doc.getId(), doc.getString("name"), doc.getDouble("price"));
                    products.add(product);
                }
            }
            updateViewModel();
        });
    }

    private void updateViewModel(){
        Log.i(TAG, "updateViewModel with " + products.size() + " products");
        newOrderViewModel.setProducts(products);
    }


    public void saveOrderClick(View view){
        ProgressBar progressBar = binding.getRoot().findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        try{
            Order order = getOrder();

            db.collection("orders")
                    .add(order)
                    .addOnSuccessListener(documentReference -> {
                        progressBar.setVisibility(View.GONE);
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Toast.makeText(this, "Pedido adicionado com sucesso", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        Log.w(TAG, "Error adding document", e);
                        Toast.makeText(this, "Erro ao adicionar o pedido", Toast.LENGTH_SHORT).show();
                    });

        }catch (Exception ex){
            progressBar.setVisibility(View.GONE);

            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private Order getOrder() throws Exception {

        Order order = new Order();
        Client client = new Client();

        // Get client data
        EditText phone = binding.getRoot().findViewById(R.id.editTextClientPhone);
        EditText name = binding.getRoot().findViewById(R.id.editTextClientName);
        EditText address = binding.getRoot().findViewById(R.id.editTextClientAddress);
        EditText addressDetails = binding.getRoot().findViewById(R.id.editTextClientAddressDetails);

        client.setPhone(phone.getText().toString());
        client.setName(name.getText().toString());
        client.setAddress(address.getText().toString());
        client.setAddressDetails(addressDetails.getText().toString());

        order.setClient(client);

        EditText orderDetails = binding.getRoot().findViewById(R.id.editTextDetails);
        EditText orderDate = binding.getRoot().findViewById(R.id.editTextOrderDate);
        EditText orderTime = binding.getRoot().findViewById(R.id.editTextOrderTime);
        EditText deliveryFee = binding.getRoot().findViewById(R.id.editTextDeliveryFee);
        CheckBox checkboxDelivery = binding.getRoot().findViewById(R.id.checkBox);
        ListView listViewProducts = binding.getRoot().findViewById(R.id.listViewProducts);

        boolean delivery = !checkboxDelivery.isChecked();

        ListAdapter adapter = listViewProducts.getAdapter();

        ArrayList<Product> productsInOrder = new ArrayList<>();
        Double total = 0d;

        for (int i=0;i<adapter.getCount();i++){
            Product p = (Product) adapter.getItem(i);
            total += p.getPrice();
            productsInOrder.add(p);
        }

        if(productsInOrder.size() <= 0 ){
            throw new Exception("Não é possivel criar um pedido, sem nenhum produto!");
        }

        Double deliveryFeeValue = 0d;
        if(!deliveryFee.getText().toString().isEmpty()){
            deliveryFeeValue = Double.valueOf(deliveryFee.getText().toString());
        }

        if(orderDate.getText().toString().isEmpty()){
            throw new Exception("Não é possivel criar um pedido sem uma data!");
        }

        Date date = new SimpleDateFormat("dd/MM/yyyy").parse(orderDate.getText().toString());

        order.setDetails(orderDetails.getText().toString());
        order.setDate(date);
        order.setDeliveryTime(orderTime.getText().toString());
        order.setDetails(orderDetails.getText().toString());
        order.setProducts(products);

        if(!delivery){
            deliveryFeeValue = 0d;
        }
        order.setDelivery(delivery);
        order.setDeliveryFee(deliveryFeeValue);
        order.setTotal(total + deliveryFeeValue);


        order.setEnable(true);
        order.setStatus(Status.EM_ANDAMENTO);
        return order;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:break;
        }
        return true;
    }

    @Override
    public void onBackPressed(){
        finish();
        return;
    }
}