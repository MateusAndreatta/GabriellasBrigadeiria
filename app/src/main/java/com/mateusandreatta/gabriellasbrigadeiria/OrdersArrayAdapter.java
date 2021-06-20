package com.mateusandreatta.gabriellasbrigadeiria;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mateusandreatta.gabriellasbrigadeiria.Utils.Global;
import com.mateusandreatta.gabriellasbrigadeiria.Utils.Status;
import com.mateusandreatta.gabriellasbrigadeiria.model.Order;
import com.mateusandreatta.gabriellasbrigadeiria.model.Product;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;

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
        holder.order = order;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        Context c = holder.itemView.getContext();

        holder.textViewStatus.setText(order.getStatus());
        holder.textViewClientName.setText(order.getClient().getName());
        holder.textViewDate.setText(simpleDateFormat.format(order.getDate()));
        holder.textViewOrderPrice.setText(c.getString(R.string.item_view_order) + " " + Global.formatCurrencyDoubleValue(order.getTotal() - order.getDeliveryFee()));
        holder.textViewOrderDeliveryFee.setText(c.getString(R.string.item_view_delivery) + " " + Global.formatCurrencyDoubleValue(order.getDeliveryFee()));
        holder.textViewOrderTotal.setText(c.getString(R.string.item_view_total) + " " + Global.formatCurrencyDoubleValue(order.getTotal()));
        holder.textViewOrderDetails.setText(StringUtils.capitalize(order.getDetails()));

        String payment = c.getString(R.string.item_view_payment_method);
        if(order.getPaymentMethod() != null){
            payment += " " + order.getPaymentMethod();
        }
        holder.textViewPaymentMethod.setText(payment);

        StringBuilder stringBuilder = new StringBuilder();
        for (Product product : order.getProducts()) {
            stringBuilder.append("\n").append(product.toString());
        }
        holder.textViewProductsList.setText(stringBuilder.toString());

        if(order.getClient().getPhone().isEmpty()){
            holder.textViewClientPhone.setVisibility(View.GONE);
            holder.imageButtonWhatsapp.setVisibility(View.GONE);
        }else{
            holder.textViewClientPhone.setText(order.getClient().getPhone());
            holder.textViewClientPhone.setVisibility(View.VISIBLE);
            holder.imageButtonWhatsapp.setVisibility(View.VISIBLE);
        }

        if(order.isDelivery()){
            holder.textViewTime.setText(c.getString(R.string.item_view_time_delivery) + " " +  order.getDeliveryTime());
            holder.imageViewOrderIcon.setImageResource(R.drawable.ic_item_card_order_delivery);
            String address = "";
            if(order.getClient().getNeighborhood() != null && !order.getClient().getNeighborhood().isEmpty())
                address += order.getClient().getNeighborhood() + " - ";
            address += order.getClient().getAddress();
            holder.textViewClientAddress.setText(address);
            holder.textViewClientAddressDetails.setText(order.getClient().getAddressDetails());

            holder.textViewClientAddress.setVisibility(View.VISIBLE);
            holder.textViewClientAddressDetails.setVisibility(View.VISIBLE);
            holder.imageButtonMaps.setVisibility(View.VISIBLE);

        }else{
            holder.textViewTime.setText(c.getString(R.string.item_view_time) + " " +  order.getDeliveryTime());
            holder.imageViewOrderIcon.setImageResource(R.drawable.ic_item_card_order_local);
            holder.textViewClientAddress.setVisibility(View.GONE);
            holder.textViewClientAddressDetails.setVisibility(View.GONE);
            holder.imageButtonMaps.setVisibility(View.GONE);
        }

        holder.imageButtonWhatsapp.setOnClickListener(v->{
            openWhatsAppConversationUsingUri(c,order.getClient().getPhone());
        });

        holder.imageButtonCopyClipboard.setOnClickListener(v -> {
            copyToClipboard(c,order);
            Toast.makeText(c, "Pedido copiado para a área de transferência", Toast.LENGTH_SHORT).show();
        });

        holder.imageButtonMaps.setOnClickListener(v -> {
            openMaps(c, order.getClient().getAddress());
        });

        if(order.getStatus().equals(Status.CONCLUIDO))
            holder.textViewStatus.setTextColor(c.getColor(R.color.status_green));
        else
            holder.textViewStatus.setTextColor(c.getColor(R.color.status_orange));
    }

    @Override
    public int getItemCount() {
        return OrderDataModel.getInstance().orderArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        Order order;
        TextView textViewStatus;
        TextView textViewClientName;
        TextView textViewDate;
        TextView textViewTime;
        TextView textViewPaymentMethod;
        TextView textViewOrderPrice;
        TextView textViewOrderDeliveryFee;
        TextView textViewOrderTotal;
        TextView textViewProductsList;
        TextView textViewClientPhone;
        TextView textViewClientAddress;
        TextView textViewClientAddressDetails;
        TextView textViewOrderDetails;
        ImageView imageViewOrderIcon;
        ImageButton imageButtonMaps;
        ImageButton imageButtonWhatsapp;
        ImageButton imageButtonCopyClipboard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            textViewClientName = itemView.findViewById(R.id.textViewClientName);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewPaymentMethod = itemView.findViewById(R.id.textViewPaymentMethod);
            textViewOrderPrice = itemView.findViewById(R.id.textViewOrderPrice);
            textViewOrderDeliveryFee = itemView.findViewById(R.id.textViewOrderDeliveryFee);
            textViewOrderTotal = itemView.findViewById(R.id.textViewOrderTotal);
            textViewProductsList = itemView.findViewById(R.id.textViewProductsList);
            textViewClientPhone = itemView.findViewById(R.id.textViewClientPhone);
            textViewClientAddress = itemView.findViewById(R.id.textViewClientAddress);
            textViewClientAddressDetails = itemView.findViewById(R.id.textViewClientAddressDetails);
            textViewOrderDetails = itemView.findViewById(R.id.textViewOrderDetails);
            imageViewOrderIcon = itemView.findViewById(R.id.imageViewOrderIcon);
            imageButtonMaps = itemView.findViewById(R.id.imageButtonMaps);
            imageButtonWhatsapp = itemView.findViewById(R.id.imageButtonWhatsapp);
            imageButtonCopyClipboard = itemView.findViewById(R.id.imageButtonCopyClipboard);

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

    private static String getWhatsAppLink(String number){
        number = number.replace("(", "");
        number = number.replace(")", "");
        number = number.replace("-", "");
        number = number.replace(" ", "");
        return "https://api.whatsapp.com/send?phone=55" + number;
    }

    private static String getMapsLink(String address){
        return "https://www.google.com/maps?q=" + address.replace(" ", "+");
    }

    private static void openWhatsAppConversationUsingUri(Context context, String numberWithCountryCode) {
        Uri uri = Uri.parse(getWhatsAppLink(numberWithCountryCode));
        Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(sendIntent);
    }

    private static void openMaps(Context context, String address){
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + address);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        context.startActivity(mapIntent);
    }

    private static void copyToClipboard(Context c,Order o){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        StringBuilder sb = new StringBuilder();
        String type = o.isDelivery() ? "ENTREGA" : "RETIRADA";
        sb.append("[").append(type).append("] ");
        sb.append(simpleDateFormat.format(o.getDate())).append(" - ").append(o.getDeliveryTime());
        sb.append("\n\n");
        sb.append(o.getClient().getName()).append("\n");

        for (Product product : o.getProducts()) {
            sb.append("\n").append(product.toString());
        }
        sb.append("\n").append("------------------").append("\n");
        sb.append(c.getString(R.string.item_view_order)).append(" ").append(Global.formatCurrencyDoubleValue(o.getTotal() - o.getDeliveryFee())).append("\n");
        sb.append(c.getString(R.string.item_view_delivery)).append(" ").append(Global.formatCurrencyDoubleValue(o.getDeliveryFee())).append("\n");
        sb.append(c.getString(R.string.item_view_total)).append(" ").append(Global.formatCurrencyDoubleValue(o.getTotal())).append("\n");
        sb.append(c.getString(R.string.item_view_payment_method)).append(" ").append(o.getPaymentMethod()).append("\n");

        sb.append("\n");
        if(!o.getClient().getPhone().isEmpty()){
            sb.append("WhatsApp: ").append(o.getClient().getPhone()).append("\n");
            sb.append(getWhatsAppLink(o.getClient().getPhone()));
        }

        if(o.isDelivery()){
            sb.append("\n\n").append("Endereço: ");
            if(o.getClient().getNeighborhood() != null && !o.getClient().getNeighborhood().isEmpty()){
                sb.append(o.getClient().getNeighborhood()).append(" - ");
            }
            sb.append(o.getClient().getAddress()).append("\n");
            sb.append(o.getClient().getAddressDetails()).append("\n");
            sb.append(getMapsLink(o.getClient().getAddress()));
        }

        if(!o.getDetails().isEmpty()){
            sb.append("\n\n").append(c.getString(R.string.item_view_details)).append(" ").append(o.getDetails());
        }

        ClipboardManager clipboard = (ClipboardManager) c.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("pedido", sb.toString());
        clipboard.setPrimaryClip(clip);
    }
}
