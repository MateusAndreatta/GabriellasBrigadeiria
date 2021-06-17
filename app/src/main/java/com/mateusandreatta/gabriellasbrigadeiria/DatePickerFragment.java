package com.mateusandreatta.gabriellasbrigadeiria;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.mateusandreatta.gabriellasbrigadeiria.ui.delivery.DeliveryViewModel;
import com.mateusandreatta.gabriellasbrigadeiria.ui.order.OrderViewModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private ViewModel viewModel;
    private EditText editText;

    public DatePickerFragment(ViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public DatePickerFragment(EditText editText) {
        this.editText = editText;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0, 0);
        if(viewModel != null){
            if(viewModel instanceof OrderViewModel){
                ((OrderViewModel) viewModel).setDate(calendar.getTime());
            }
            if(viewModel instanceof DeliveryViewModel){
                ((DeliveryViewModel) viewModel).setDate(calendar.getTime());
            }
        }
        if(editText != null){
            editText.setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
        }
    }
}
