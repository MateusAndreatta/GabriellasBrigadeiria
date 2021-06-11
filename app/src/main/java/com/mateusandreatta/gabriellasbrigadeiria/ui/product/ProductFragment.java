package com.mateusandreatta.gabriellasbrigadeiria.ui.product;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mateusandreatta.gabriellasbrigadeiria.R;
import com.mateusandreatta.gabriellasbrigadeiria.databinding.FragmentProductBinding;
import com.mateusandreatta.gabriellasbrigadeiria.model.Product;

import java.util.ArrayList;

//TODO: Remover produto

public class ProductFragment extends Fragment {

    private final String TAG = "TAG-SlideshowFragment";

    private ProductViewModel productViewModel;
    private FragmentProductBinding binding;
    private FirebaseFirestore db;
    private ListView listView;
    private ArrayList<String> itens = new ArrayList<>();
    private ArrayList<Product> products = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        productViewModel =
                new ViewModelProvider(this).get(ProductViewModel.class);

        binding = FragmentProductBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        listView = binding.listViewProducts;

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Product product = products.get(i);
            showDialog(product);
        });

        loadProducts();

        binding.floatingActionButtonAddProduct.setOnClickListener(v -> {
            showDialog(null);
        });

        return root;
    }

    private void loadProducts(){
        db.collection("products").addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.w(TAG, "Listen failed.", error);
                return;
            }
            itens.clear();
            products.clear();
            for (QueryDocumentSnapshot doc : value) {
                if (doc.get("name") != null && doc.get("price") != null) {
                    Product product = new Product(doc.getId(), doc.getString("name"), doc.getDouble("price"));
                    itens.add(product.toString());
                    products.add(product);
                }
            }
            updateListView();
        });
    }

    private void updateListView(){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                itens
        );
        listView.setAdapter(adapter);
    }

    private void showDialog(Product product){
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        final View view = getLayoutInflater().inflate(R.layout.add_product_layout, null);
        EditText editTextProductName = (EditText) view.findViewById(R.id.editTextProductName);
        EditText editTextProductPrice = (EditText) view.findViewById(R.id.editTextDeliveryFee);
        alert.setTitle("Cadastrar produto");
        alert.setMessage("Insira o nome e o preço do produto");

        if(product != null){
            alert.setTitle("Editar produto");
            editTextProductName.setText(product.getName());
            editTextProductPrice.setText(product.getPrice().toString());
        }

        alert.setView(view);

        alert.setPositiveButton("Salvar", (dialog, whichButton) -> {

            String productName = editTextProductName.getText().toString();
            Double productPrice = Double.valueOf(editTextProductPrice.getText().toString());
            if( product == null){
                //Add new product
                Product product1 = new Product(productName,productPrice);
                db.collection("products")
                        .add(product1)
                        .addOnSuccessListener(documentReference -> {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            Toast.makeText(getActivity(), "Produto Adicionado com sucesso", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Log.w(TAG, "Error adding document", e);
                            Toast.makeText(getActivity(), "Erro ao adicionar o produto", Toast.LENGTH_SHORT).show();
                        });
            }else{
                // edit product
                product.setName(productName);
                product.setPrice(productPrice);

                db.collection("products").document(product.getFirestoreId())
                        .set(product).addOnCompleteListener(task ->
                        Toast.makeText(getActivity(), "Produto editado com sucesso", Toast.LENGTH_SHORT).show());
            }



        });

        alert.setNegativeButton("Cancelar", (dialog, whichButton) -> {
            // what ever you want to do with No option.
        });

        alert.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}