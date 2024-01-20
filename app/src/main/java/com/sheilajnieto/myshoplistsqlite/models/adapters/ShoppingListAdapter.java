package com.sheilajnieto.myshoplistsqlite.models.adapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.sheilajnieto.myshoplistsqlite.MainActivity;
import com.sheilajnieto.myshoplistsqlite.db.sqlite.ShoppingListSQLiteHelper;
import com.sheilajnieto.myshoplistsqlite.db.sqlite.dao.ListDAO;
import com.sheilajnieto.myshoplistsqlite.interfaces.IOnClickListener;
import com.sheilajnieto.myshoplistsqlite.R;
import com.sheilajnieto.myshoplistsqlite.models.ListClass;
import com.sheilajnieto.myshoplistsqlite.ui.FragmentNoLists;
import com.sheilajnieto.myshoplistsqlite.ui.ListFragment;

import java.text.SimpleDateFormat;
import java.util.List;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ListadoViewHolder> {

    private final List<ListClass> lists;
    List<Integer> productQuantities;
    private final IOnClickListener listener;
    private ListDAO listDAO;
    private SQLiteDatabase db;
    private Context contextMain;
    private int adapterPosition;

    public ShoppingListAdapter(List<ListClass> lists, IOnClickListener listener, List<Integer> productQuantities, SQLiteDatabase db, Context context) {
        this.lists = lists;
        this.listener = listener;
        this.productQuantities = productQuantities;
        this.contextMain = context;
        this.db = db;
        db = ShoppingListSQLiteHelper.getInstance(contextMain).getWritableDatabase();
        listDAO = new ListDAO(db);

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
                adapterPosition = getAdapterPosition();
            }
        }
    }

    // ------- MANEJO SWIPE PARA ELIMINAR UN LISTADO -------

    public void deleteItem(int position) {
        position = adapterPosition;
        // Obtenemos la lista que queremos eliminar
        ListClass listToDelete = lists.get(position);

        // Eliminamos la lista de la base de datos
        listDAO.delete(listToDelete);

        // Eliminamos la lista de las listas de datos del adaptador
        lists.remove(position);
        productQuantities.remove(position);

        // Notificamos al adaptador que se eliminó un elemento
        notifyItemRemoved(position);
        notifyDataSetChanged();

    }

}


