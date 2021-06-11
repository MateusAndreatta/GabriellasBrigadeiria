package com.mateusandreatta.gabriellasbrigadeiria.ui.order;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Date;

public class OrderViewModel extends ViewModel {

    private MutableLiveData<Date> mDate;

    public OrderViewModel() {
        mDate = new MutableLiveData<>();
        mDate.setValue(new Date());
    }

    public LiveData<Date> getSelectedDate() {
        return mDate;
    }

    public void setDate(Date mDate) {
        this.mDate.setValue(mDate);
    }
}