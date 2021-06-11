package com.mateusandreatta.gabriellasbrigadeiria;

import com.mateusandreatta.gabriellasbrigadeiria.model.Order;

import java.util.ArrayList;

public class OrderDataModel {

    private static OrderDataModel instance = new OrderDataModel();
    public ArrayList<Order> orderArrayList = new ArrayList<>();

    private OrderDataModel(){;
    }

    public static OrderDataModel getInstance(){
        return instance;
    }

}
