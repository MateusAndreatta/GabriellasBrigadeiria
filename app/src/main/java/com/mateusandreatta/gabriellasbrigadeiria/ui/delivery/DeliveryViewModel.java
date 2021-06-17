package com.mateusandreatta.gabriellasbrigadeiria.ui.delivery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Date;

public class DeliveryViewModel extends ViewModel {

    private MutableLiveData<Date> mDate;

    public DeliveryViewModel() {
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