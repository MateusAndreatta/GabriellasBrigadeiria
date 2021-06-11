package com.mateusandreatta.gabriellasbrigadeiria;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mateusandreatta.gabriellasbrigadeiria.Utils.Global;
import com.mateusandreatta.gabriellasbrigadeiria.model.Order;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OrdersArrayAdapter extends RecyclerView.Adapter<OrdersArrayAdapter.ViewHolder> {


    private static ClickListener clickListener;

    public void setClickListener(ClickListener clickListener){
        OrdersArrayAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View view);
        boolean onItemLongClick(int position,View view);
    }

    int counter = 0;
    @NonNull
    @Override
    public OrdersArrayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                              int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View itemView = layoutInflater.inflate(
                R.layout.order_recycler_item, parent,false
        );

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersArrayAdapter.ViewHolder holder,
                                 int position) {
        Order order = OrderDataModel.getInstance().orderArrayList.get(position);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        Context c = holder.itemView.getContext();

        holder.textViewStatus.setText(order.getStatus());
        holder.textViewClientName.setText(order.getClient().getName());
        holder.textViewDate.setText(simpleDateFormat.format(order.getDate()));
        holder.textViewTime.setText(c.getString(R.string.item_view_time) + " " +  order.getDeliveryTime());
        holder.textViewProducts.setText("Possui " + order.getProducts().size() + " Produtos");
        holder.textViewOrderPrice.setText(c.getString(R.string.item_view_order) + " " + Global.formatCurrencyDoubleValue(order.getTotal() - order.getDeliveryFee()));
        holder.textViewOrderDeliveryFee.setText(c.getString(R.string.item_view_delivery) + " " + Global.formatCurrencyDoubleValue(order.getDeliveryFee()));
        holder.textViewOrderTotal.setText(c.getString(R.string.item_view_total) + " " + Global.formatCurrencyDoubleValue(order.getTotal()));

        if(order.isDelivery()){
            holder.imageViewOrderIcon.setImageResource(R.drawable.ic_item_card_order_delivery);
        }else{
            holder.imageViewOrderIcon.setImageResource(R.drawable.ic_item_card_order_local);
        }
    }

    @Override
    public int getItemCount() {
        return OrderDataModel.getInstance().orderArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewStatus;
        TextView textViewClientName;
        TextView textViewDate;
        TextView textViewTime;
        TextView textViewProducts;
        TextView textViewOrderPrice;
        TextView textViewOrderDeliveryFee;
        TextView textViewOrderTotal;
        ImageView imageViewOrderIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            textViewClientName = itemView.findViewById(R.id.textViewClientName);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewProducts = itemView.findViewById(R.id.textViewProducts);
            textViewOrderPrice = itemView.findViewById(R.id.textViewOrderPrice);
            textViewOrderDeliveryFee = itemView.findViewById(R.id.textViewOrderDeliveryFee);
            textViewOrderTotal = itemView.findViewById(R.id.textViewOrderTotal);
            imageViewOrderIcon = itemView.findViewById(R.id.imageViewOrderIcon);

            itemView.setOnClickListener(view -> {
                if(clickListener == null)
                    return;
                clickListener.onItemClick(getAdapterPosition(),view);
            });

            itemView.setOnLongClickListener(view -> {
                if(clickListener == null)
                    return false;

                return clickListener.onItemLongClick(getAdapterPosition(),view);
            });
        }
    }
}
