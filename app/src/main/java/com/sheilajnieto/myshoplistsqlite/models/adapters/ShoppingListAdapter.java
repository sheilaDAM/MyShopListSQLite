package com.sheilajnieto.myshoplistsqlite.models.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sheilajnieto.myshoplistsqlite.interfaces.IOnClickListener;
import com.sheilajnieto.myshoplistsqlite.R;
import com.sheilajnieto.myshoplistsqlite.models.ListClass;

import java.text.SimpleDateFormat;
import java.util.List;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ListadoViewHolder> {

    private final List<ListClass> lists;
    List<Integer> productQuantities;
    private final IOnClickListener listener;


    public ShoppingListAdapter(List<ListClass> lists, IOnClickListener listener, List<Integer> productQuantities) {
        this.lists = lists;
        this.listener = listener;
        this.productQuantities = productQuantities;
    }


    @NonNull
    @Override
    public ListadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shopping_list, parent, false);
        return new ListadoViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull ListadoViewHolder holder, int position) {
       ListClass list = lists.get(position);
       holder.bindShoppingList(list, position);
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public class ListadoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvListName;
        private TextView tvCreationDate;
        private TextView tvProductsQuantity;
        private Context context;

        public ListadoViewHolder(View itemview) {
            super(itemview);
            this.context = itemview.getContext();
            this.tvListName = itemview.findViewById(R.id.tvListName);
            this.tvCreationDate = itemview.findViewById(R.id.tvCreationDate);
            this.tvProductsQuantity = itemview.findViewById(R.id.tvProductsQuantity);

            itemview.setOnClickListener(this);

        }

        public void bindShoppingList(ListClass list, int position) {
            tvListName.setText(list.getName());
            tvCreationDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(list.getDate()));
            tvProductsQuantity.setText(String.valueOf(productQuantities.get(position)));

        }


        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onShoppingListClicked(getAdapterPosition());
            }
        }
    }
}


