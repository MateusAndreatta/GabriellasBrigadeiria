package com.mateusandreatta.gabriellasbrigadeiria;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mateusandreatta.gabriellasbrigadeiria.Utils.Global;
import com.mateusandreatta.gabriellasbrigadeiria.Utils.Status;
import com.mateusandreatta.gabriellasbrigadeiria.databinding.ActivityNewOrderBinding;
import com.mateusandreatta.gabriellasbrigadeiria.model.Client;
import com.mateusandreatta.gabriellasbrigadeiria.model.FCMResponse;
import com.mateusandreatta.gabriellasbrigadeiria.model.NotificationRequest;
import com.mateusandreatta.gabriellasbrigadeiria.model.Order;
import com.mateusandreatta.gabriellasbrigadeiria.model.Product;
import com.mateusandreatta.gabriellasbrigadeiria.service.GoogleApiFcmService;
import com.mateusandreatta.gabriellasbrigadeiria.ui.newOrder.NewOrderViewModel;
import com.mateusandreatta.gabriellasbrigadeiria.ui.newOrder.SectionsPagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewOrderActivity extends AppCompatActivity {

    private final String TAG = "TAG-NewOrderActivity";
    private NewOrderViewModel newOrderViewModel;

    private ActivityNewOrderBinding binding;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;

    private ArrayList<Product> products = new ArrayList<>();
    private boolean edit;
    private Order orderEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNewOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.menu_new_order));

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);

        newOrderViewModel = new ViewModelProvider(this).get(NewOrderViewModel.class);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            orderEdit = (Order) getIntent().getSerializableExtra("order");
            edit = true;
            getSupportActionBar().setTitle(getResources().getString(R.string.menu_edit_order));
            newOrderViewModel.setmEditOrder(orderEdit);
        }

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

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
        Button btn = binding.getRoot().findViewById(R.id.buttonSaveOrder);
        progressBar.setVisibility(View.VISIBLE);
        btn.setEnabled(false);
        try{
            Order order = getOrder();

            if(!edit){
                db.collection("orders")
                        .add(order)
                        .addOnSuccessListener(documentReference -> {
                            progressBar.setVisibility(View.GONE);
                            btn.setEnabled(true);
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            sendNewProductNotification(order.getDate());
                            Toast.makeText(this, "Pedido adicionado com sucesso", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            progressBar.setVisibility(View.GONE);
                            btn.setEnabled(true);
                            Log.w(TAG, "Error adding document", e);
                            Toast.makeText(this, "Erro ao adicionar o pedido", Toast.LENGTH_SHORT).show();
                        });
            }else{
                db.collection("orders").document(order.getFirestoreId())
                        .set(order).addOnCompleteListener(task -> {
                           if(task.isSuccessful()){
                               progressBar.setVisibility(View.GONE);
                               btn.setEnabled(true);
                               Toast.makeText(this, "Pedido editado com sucesso", Toast.LENGTH_SHORT).show();
                               finish();
                           }else{
                               progressBar.setVisibility(View.GONE);
                               btn.setEnabled(true);
                               Toast.makeText(this, "Erro ao editar o pedido", Toast.LENGTH_SHORT).show();
                           }
                        });

            }

        }catch (Exception ex){
            progressBar.setVisibility(View.GONE);
            btn.setEnabled(true);
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private Order getOrder() throws Exception {

        Order order = new Order();
        Client client = new Client();

        // Get client data
        EditText phone = binding.getRoot().findViewById(R.id.editTextClientPhone);
        EditText name = binding.getRoot().findViewById(R.id.editTextClientName);
        AutoCompleteTextView neighborhood = binding.getRoot().findViewById(R.id.autoCompleteTextViewClientNeighborhood);
        EditText address = binding.getRoot().findViewById(R.id.editTextClientAddress);
        EditText addressDetails = binding.getRoot().findViewById(R.id.editTextClientAddressDetails);

        client.setPhone(phone.getText().toString());
        client.setName(name.getText().toString());
        client.setNeighborhood(neighborhood.getText().toString());
        client.setAddress(address.getText().toString());
        client.setAddressDetails(addressDetails.getText().toString());

        order.setClient(client);

        EditText orderDetails = binding.getRoot().findViewById(R.id.editTextDetails);
        EditText orderDate = binding.getRoot().findViewById(R.id.editTextOrderDate);
        EditText orderTime = binding.getRoot().findViewById(R.id.editTextOrderTime);
        EditText deliveryFee = binding.getRoot().findViewById(R.id.editTextDeliveryFee);
        Spinner paymentMethod = binding.getRoot().findViewById(R.id.spinnerPaymentMethod);
        CheckBox checkboxDelivery = binding.getRoot().findViewById(R.id.checkBox);
        ListView listViewProducts = binding.getRoot().findViewById(R.id.listViewOrderProducts);

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

        double deliveryFeeValue = 0d;
        if(!deliveryFee.getText().toString().isEmpty()){
            deliveryFeeValue = Global.getDoubleValueFromMaskedEditText(deliveryFee.getText().toString());
        }

        if(orderDate.getText().toString().isEmpty()){
            throw new Exception("Não é possivel criar um pedido sem uma data!");
        }

        Date date = new SimpleDateFormat("dd/MM/yyyy").parse(orderDate.getText().toString());

        order.setDate(date);
        order.setDeliveryTime(orderTime.getText().toString());
        order.setDetails(orderDetails.getText().toString());
        order.setProducts(productsInOrder);
        order.setPaymentMethod(paymentMethod.getSelectedItem().toString());

        if(!delivery){
            deliveryFeeValue = 0d;
        }
        order.setDelivery(delivery);
        order.setDeliveryFee(deliveryFeeValue);
        order.setTotal(total + deliveryFeeValue);


        order.setEnable(true);
        if(!edit || orderEdit.getStatus() == null)
            order.setStatus(Status.EM_ANDAMENTO);
        else
            order.setStatus(orderEdit.getStatus());

        if(edit)
            order.setFirestoreId(orderEdit.getFirestoreId());

        return order;
    }

    private void sendNewProductNotification(Date orderDate){

        db.collection("tokens").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String orderDateString = simpleDateFormat.format(orderDate);

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("https://fcm.googleapis.com/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    GoogleApiFcmService googleApiFcmService = retrofit.create(GoogleApiFcmService.class);

                    for (QueryDocumentSnapshot document : task.getResult()){
                        if(!document.getId().equals(firebaseUser.getUid())){
                            sendPostRequestNotification(googleApiFcmService,document.getData().get("token").toString(), orderDateString);
                        }
                    }
                }
            }
        });

    }

    /*
    O ideal seria ter um servidor backend para realizar os envios ou com o uso do firebase functions
    e nao o proprio cliente realizar o envio.
    Mas para manter o projeto sem custos de backend, o priprio cliente está realizando o envio das notificações via POST
    */
    private void sendPostRequestNotification(GoogleApiFcmService googleApiFcmService, String token, String orderDateString){
        Log.d(TAG, "sendPostRequestNotification: " + token);

        Call<FCMResponse> requestBodyCall = googleApiFcmService.sendNotification(new NotificationRequest(getString(R.string.notification_title), getString(R.string.notification_body) + " " + orderDateString, token));

        requestBodyCall.enqueue(new Callback<FCMResponse>() {
            @Override
            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                Log.d(TAG, "Notification resquestCode: " + response.code());
            }

            @Override
            public void onFailure(Call<FCMResponse> call, Throwable t) {
                Log.d(TAG, "Notification onFailure: " + t.getMessage());
            }
        });
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
    }
}