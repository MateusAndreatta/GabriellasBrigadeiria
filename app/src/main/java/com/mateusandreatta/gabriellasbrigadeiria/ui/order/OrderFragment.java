package com.mateusandreatta.gabriellasbrigadeiria.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.mateusandreatta.gabriellasbrigadeiria.NewOrderActivity;
import com.mateusandreatta.gabriellasbrigadeiria.databinding.FragmentOrderBinding;

public class OrderFragment extends Fragment {

    private OrderViewModel orderViewModel;
    private FragmentOrderBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        orderViewModel =
                new ViewModelProvider(this).get(OrderViewModel.class);

        binding = FragmentOrderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.floatingActionButtonAddOrder.setOnClickListener(v -> {
/*            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_content_main,
                            new AddOrderFragment()).
                    addToBackStack(null).commit();*/

//            Fragment f = getParentFragmentManager().findFragmentById(R.id.nav_add_order);

//            FragmentManager fragmentManager = getParentFragmentManager();
//            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//            fragmentManager.popBackStackImmediate();
//
//            Navigation.findNavController(root).navigate(R.id.action_nav_orders_to_nav_add_order2);

            startActivity(new Intent(getContext(), NewOrderActivity.class));
        });

        final TextView textView = binding.textHome;
        orderViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}