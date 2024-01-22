package com.sheilajnieto.myshoplistsqlite.ui;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sheilajnieto.myshoplistsqlite.R;
import com.sheilajnieto.myshoplistsqlite.SwipeToDelete;
import com.sheilajnieto.myshoplistsqlite.interfaces.UpdateListFragmentAfterDelete;
import com.sheilajnieto.myshoplistsqlite.db.sqlite.ShoppingListSQLiteHelper;
import com.sheilajnieto.myshoplistsqlite.db.sqlite.dao.CategoryDAO;
import com.sheilajnieto.myshoplistsqlite.db.sqlite.dao.ListDAO;
import com.sheilajnieto.myshoplistsqlite.db.sqlite.dao.ProductDAO;
import com.sheilajnieto.myshoplistsqlite.interfaces.IOnClickListener;
import com.sheilajnieto.myshoplistsqlite.models.Category;
import com.sheilajnieto.myshoplistsqlite.models.ListClass;
import com.sheilajnieto.myshoplistsqlite.models.Product;
import com.sheilajnieto.myshoplistsqlite.models.adapters.CategoryListAdapter;
import com.sheilajnieto.myshoplistsqlite.models.adapters.ProductListAdapter;
import com.sheilajnieto.myshoplistsqlite.models.adapters.ShoppingListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListFragment extends Fragment {

    public enum ListType{SHOPPING_LIST, CATEGORY_LIST, PRODUCT_LIST}
    private ListType listType;


    public interface IOnAttachListener {
        SQLiteDatabase getDatabase();
        ListType getListType();
        ListClass getList();
        Category getCategorySelected();
        Context getContext();
    }

    private RecyclerView listRecView;
    private IOnClickListener clickListener; //para manejar si se hace click en un item de la lista
    private List<ListClass> lists;
    private List<Category> categories;
    private List<Product> products;
    private SQLiteDatabase db;
    private List<Integer> productQuantities;
    private ListDAO listDAO;
    private CategoryDAO categoryDAO;
    private ProductDAO productDAO;
    private ShoppingListAdapter shoppingListAdapter;
    private CategoryListAdapter categoryListAdapter;
    private ProductListAdapter productListAdapter;
    private ListClass list;
    private Category category;
    private IOnAttachListener attachListener;
    private Context contextMain;
    private ItemTouchHelper itemTouchHelper;


    public ListFragment() {
        super(R.layout.list_fragment);
        db = ShoppingListSQLiteHelper.getInstance(getContext()).getWritableDatabase();
        listDAO = new ListDAO(db);
        categoryDAO = new CategoryDAO(db, contextMain);
        productDAO = new ProductDAO(db, contextMain);
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
        contextMain = attachListener.getContext();
        db = ShoppingListSQLiteHelper.getInstance(contextMain).getWritableDatabase();
        categories = categoryDAO.findAll();
    }

    private void loadProductListByCategoryFromDB() {
        db = ShoppingListSQLiteHelper.getInstance(contextMain).getWritableDatabase();
        int categoryID = category.getId();
        // Crear un Map con la condición de búsqueda
        Map<String, String> condition = new HashMap<>();
        condition.put("fk_category_id", String.valueOf(categoryID));

        ProductDAO productDAO = new ProductDAO(db, contextMain);
        products = productDAO.findBy(condition);

    }

    public void uptadateList(ListType tipoListado) {
        this.listType = tipoListado;
        switch(tipoListado) {
            case SHOPPING_LIST:
                if (shoppingListAdapter == null)
                    shoppingListAdapter = new ShoppingListAdapter(lists, clickListener, productQuantities, db, contextMain);
                itemTouchHelper = new ItemTouchHelper(new SwipeToDelete(shoppingListAdapter, contextMain, (UpdateListFragmentAfterDelete) requireActivity()));
                itemTouchHelper.attachToRecyclerView(listRecView);
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
                category = attachListener.getCategorySelected();
                loadProductListByCategoryFromDB();
                if (productListAdapter == null)
                    productListAdapter = new ProductListAdapter(products, clickListener);
                listRecView.setLayoutManager(new GridLayoutManager(getContext(), 3));
                listRecView.setAdapter(productListAdapter);
                productListAdapter.notifyDataSetChanged();
                break;
        }

    }


    public void setCategory(Category category) {
        this.category = category;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        attachListener = (IOnAttachListener) context;
        clickListener = (IOnClickListener) context;
        contextMain = context;
        categoryDAO = new CategoryDAO(db, context);
        productDAO = new ProductDAO(db, context);
        /** Obtenemos los datos*/
        listType = attachListener.getListType();
        list = attachListener.getList();
        category = attachListener.getCategorySelected();

      //  db = attachListener.getDatabase();
    }

}

