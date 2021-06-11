package com.mateusandreatta.gabriellasbrigadeiria.ui.newOrder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mateusandreatta.gabriellasbrigadeiria.model.Order;
import com.mateusandreatta.gabriellasbrigadeiria.model.Product;

import java.util.ArrayList;

public class NewOrderViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Product>> mProducts;
    private MutableLiveData<Order> mEditOrder;

    public NewOrderViewModel() {
        mProducts = new MutableLiveData<>();
        mEditOrder = new MutableLiveData<>();
        mProducts.setValue(new ArrayList<>());
    }

    public LiveData<ArrayList<Product>> getProducts() {
        return mProducts;
    }

    public void setProducts(ArrayList<Product> products) {
        this.mProducts.setValue(products);
    }

    public MutableLiveData<Order> getEditOrder() {
        return mEditOrder;
    }

    public void setmEditOrder(Order mEditOrder) {
        this.mEditOrder.setValue(mEditOrder);
    }
}