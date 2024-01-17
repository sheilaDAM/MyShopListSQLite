package com.sheilajnieto.myshoplistsqlite.ui;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sheilajnieto.myshoplistsqlite.R;
import com.sheilajnieto.myshoplistsqlite.db.sqlite.ShoppingListSQLiteHelper;
import com.sheilajnieto.myshoplistsqlite.db.sqlite.dao.CategoryDAO;
import com.sheilajnieto.myshoplistsqlite.db.sqlite.dao.ListDAO;
import com.sheilajnieto.myshoplistsqlite.interfaces.IOnClickListener;
import com.sheilajnieto.myshoplistsqlite.models.Category;
import com.sheilajnieto.myshoplistsqlite.models.ListClass;
import com.sheilajnieto.myshoplistsqlite.models.adapters.CategoryListAdapter;
import com.sheilajnieto.myshoplistsqlite.models.adapters.ShoppingListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListFragment extends Fragment {

    public enum ListType{SHOPPING_LIST, CATEGORY_LIST, PRODUCT_LIST}
    private ListType listType;


    public interface IOnAttachListener {
        SQLiteDatabase getDatabase();
        ListType getListType();
        ListClass getList();
    }

    private RecyclerView listRecView;
    private IOnClickListener clickListener;
    private List<ListClass> lists;
    private List<Category> categories;
    private SQLiteDatabase db;
    private List<Integer> productQuantities;
    private ListDAO listDAO;
    private CategoryDAO categoryDAO;
    private ShoppingListAdapter shoppingListAdapter;
    private CategoryListAdapter categoryListAdapter;
    private ListClass list;
    private Context contextMain;

    public ListFragment() {
        super(R.layout.list_fragment);
        db = ShoppingListSQLiteHelper.getInstance(getContext()).getWritableDatabase();
        listDAO = new ListDAO(db);
        categoryDAO = new CategoryDAO(db, getContext());
    }


    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadListFromDB();
        listRecView = view.findViewById(R.id.rvList);
        uptadateList(listType);

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

    private void loadCategoryListFromDB() {
        db = ShoppingListSQLiteHelper.getInstance(contextMain).getWritableDatabase();
        // Creamos una instancia de ListDAO para insertar la nueva lista en la base de datos
        categories = categoryDAO.findAll();
    }

    public void uptadateList(ListType tipoListado) {
        this.listType = tipoListado;
        switch(tipoListado) {
            case SHOPPING_LIST:
                // Context activityContext = getActivity();
                if (shoppingListAdapter == null)
                    shoppingListAdapter = new ShoppingListAdapter(lists, clickListener, productQuantities);
                listRecView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
                listRecView.setHasFixedSize(true);
                listRecView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                listRecView.setAdapter(shoppingListAdapter);
                shoppingListAdapter.notifyDataSetChanged();
                break;
            case CATEGORY_LIST:
                loadCategoryListFromDB();
                if (categoryListAdapter == null)
                    categoryListAdapter = new CategoryListAdapter(categories, clickListener);
                listRecView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
                listRecView.setHasFixedSize(true);
                listRecView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                listRecView.setAdapter(categoryListAdapter);
                categoryListAdapter.notifyDataSetChanged();
                break;
            case PRODUCT_LIST:
                if (shoppingListAdapter == null)
                    shoppingListAdapter = new ShoppingListAdapter(lists, clickListener, productQuantities);
                break;
        }

    }

    public void onAttach(Context context) {
        super.onAttach(context);
        IOnAttachListener attachListener = (IOnAttachListener) context;
        /** Obtenemos los datos*/
        listType = attachListener.getListType();
        list = attachListener.getList();
        clickListener = (IOnClickListener) context;
        contextMain =context;
        categoryDAO = new CategoryDAO(db, context);
      //  db = attachListener.getDatabase();
    }

}

