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
       // Cuenta cuenta = cuentas[position];
       ListClass list = lists.get(position);
       holder.bindCorreo(list, position);
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

        public void bindCorreo(ListClass list, int position) {
            tvListName.setText(list.getName());
                tvCreationDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(list.getDate()));
            tvProductsQuantity.setText(String.valueOf(productQuantities.get(position)));

        }
/*
            try {
                Resources res = context.getResources();
                String imagenCiclista = "cyclist_" + cyclist.getCyclistId();
                int resID = res.getIdentifier(imagenCiclista, "drawable", context.getPackageName());
                if (resID != 0) {
                    ivCyclist.setImageResource(resID);
                    Bitmap bitmap = BitmapFactory.decodeResource(res, resID);
                    cyclist.setImagenCiclista(bitmap);
                } else {
                    imagenCiclista = "cyclist_1";
                    resID = res.getIdentifier(imagenCiclista, "drawable", context.getPackageName());
                    ivCyclist.setImageResource(resID);
                    Bitmap bitmap = BitmapFactory.decodeResource(res, resID);
                    cyclist.setImagenCiclista(bitmap);
                }
            } catch (Exception e) {
            }

        }
 */

        public void updateData(List<ListClass> newLists) {
            //lists.clear();  // Limpia la lista actual
            lists.addAll(newLists);  // Agrega la nueva lista
            notifyDataSetChanged();  // Notifica al adaptador que los datos han cambiado
        }


        public void onClick(View v) {
            if (listener != null) {
                listener.onClick(getAdapterPosition());
            }
        }
    }
}


