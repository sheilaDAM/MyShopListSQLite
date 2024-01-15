package com.sheilajnieto.myshoplistsqlite.ui;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.sheilajnieto.myshoplistsqlite.MainActivity;
import com.sheilajnieto.myshoplistsqlite.R;
import com.sheilajnieto.myshoplistsqlite.db.sqlite.ShoppingListSQLiteHelper;
import com.sheilajnieto.myshoplistsqlite.db.sqlite.dao.ListDAO;
import com.sheilajnieto.myshoplistsqlite.interfaces.IOnClickListener;
import com.sheilajnieto.myshoplistsqlite.models.ListClass;
import com.sheilajnieto.myshoplistsqlite.models.adapters.ShoppingListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {

    public interface IOnAttachListener {
        SQLiteDatabase getDatabase();
    }

    private RecyclerView listRecView;
    private IOnClickListener clickListener;
    private List<ListClass> lists;
    private SQLiteDatabase db;
    private List<Integer> productQuantities;
    private ListDAO listDAO;

    public ListFragment() {
        super(R.layout.list_fragment);
        db = ShoppingListSQLiteHelper.getInstance(getContext()).getWritableDatabase();
        listDAO = new ListDAO(db);
    }


    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadListFromDB();
        ShoppingListAdapter adapter = new ShoppingListAdapter(lists, clickListener, productQuantities);
        listRecView = view.findViewById(R.id.rvList);
        listRecView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        listRecView.setHasFixedSize(true);
        listRecView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        listRecView.setAdapter(adapter);

    }

    private void loadListFromDB() {
        Context context = getContext();
        db = ShoppingListSQLiteHelper.getInstance(context).getWritableDatabase();
        // Creamos una instancia de ListDAO para insertar la nueva lista en la base de datos
        lists = listDAO.findAll();
        //obtenemos la cantidad de productos de cada lista
        productQuantities = getProductQuantitiesFromLists(lists);
    }

    private List<Integer> getProductQuantitiesFromLists(List<ListClass> lists) {
        List<Integer> productQuantities = new ArrayList<>();
        for (ListClass list : lists) {
            int productCount = listDAO.countProductsFromShoppingList(list.getId());
            productQuantities.add(productCount);
        }
        return productQuantities;
    }

    public void updateList() {

    }

    public void onAttach(Context contex) {
        super.onAttach(contex);
        IOnAttachListener attachListener = (IOnAttachListener) contex;
      //  db = attachListener.getDatabase();
    }

}

