package com.sheilajnieto.myshoplistsqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.sheilajnieto.myshoplistsqlite.db.sqlite.ShoppingListSQLiteHelper;
import com.sheilajnieto.myshoplistsqlite.db.sqlite.dao.CategoryDAO;
import com.sheilajnieto.myshoplistsqlite.db.sqlite.dao.ListDAO;
import com.sheilajnieto.myshoplistsqlite.db.sqlite.dao.ProductDAO;
import com.sheilajnieto.myshoplistsqlite.db.sqlite.dao.ProductListDAO;
import com.sheilajnieto.myshoplistsqlite.interfaces.IOnClickListener;
import com.sheilajnieto.myshoplistsqlite.interfaces.UpdateListFragmentAfterDelete;
import com.sheilajnieto.myshoplistsqlite.models.Category;
import com.sheilajnieto.myshoplistsqlite.models.ListClass;
import com.sheilajnieto.myshoplistsqlite.models.Product;
import com.sheilajnieto.myshoplistsqlite.models.ProductList;
import com.sheilajnieto.myshoplistsqlite.ui.AddListBoxDialogFragment;
import com.sheilajnieto.myshoplistsqlite.ui.FragmentAppInformation;
import com.sheilajnieto.myshoplistsqlite.ui.FragmentNoLists;
import com.sheilajnieto.myshoplistsqlite.ui.FragmentNoProducts;
import com.sheilajnieto.myshoplistsqlite.ui.ListFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AddListBoxDialogFragment.OnListAddedListener, IOnClickListener, ListFragment.IOnAttachListener, UpdateListFragmentAfterDelete {

    private DrawerLayout drawerLayout;
    private ListFragment listFragment; //fragmento que se muestra cuando hay listas
    private FragmentNoLists fragmentNoLists; //fragmento que se muestra cuando no hay listas
    private FragmentNoProducts fragmentNoProducts; //fragmento que se muestra cuando no hay productos
    private FragmentManager fragmentManager = getSupportFragmentManager(); //para poder tratar los fragments por código
    private FragmentAppInformation fragmentInfo;
    private Toolbar toolbar;
    private View headerLayout;
    private ListFragment.ListType listType; //para determinar qué tipo de listado se muestra, si listas, categorías o productos
    private boolean shoppingListClicked; //para saber si se ha pulsado sobre una lista de la compra
    private boolean categoryListClicked;
    private boolean productClicked;
    private SQLiteDatabase db;
    private ListDAO listDAO; //para acceder a los métodos de listDAO y poder hacer las consultas a la base de datos
    private CategoryDAO categoryDAO;
    private ProductListDAO productListDAO;
    private ProductDAO productDAO;
    private int listSelected; //guarda el id de la lista seleccionada
    private int categorySelected; //guarda el id de la categoría seleccionada
    private int productSelected; //guarda el id del producto seleccionado
    private ListClass shoppingListSelected; //guarda la lista seleccionada
    private Category categoryObjectSelected;
    private Product productObjectSelected;
    private Fragment currentFragment; //para saber en qué fragment estamos
    private boolean areCategoriesShown;
    private Context contextMain;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //obtenemos la instancia de la base de datos
        db = ShoppingListSQLiteHelper.getInstance(this).getWritableDatabase();
        if (db == null) {
            Toast.makeText(this, "Error al conectar con la BD", Toast.LENGTH_SHORT).show();
            finish();
        }

        shoppingListClicked = false;
        listDAO = new ListDAO(db, this);
        categoryDAO = new CategoryDAO(db, this);
        productListDAO = new ProductListDAO(db, this);
        productDAO = new ProductDAO(db, this);

        setContentView(R.layout.activity_main);

        //Si hay listas en la base de datos, cargamos el fragmento de listas, si no, cargamos el fragmento de no hay listas
        if(listDAO.findAll().size() > 0) {
            listFragment = new ListFragment();
            listType = ListFragment.ListType.SHOPPING_LIST;
            fragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.nav_host_fragment_content_main, listFragment)
                    .commit();
        } else {
            fragmentNoLists = new FragmentNoLists();
            fragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.nav_host_fragment_content_main, fragmentNoLists)
                    .commit();
        }

      //------- MANEJO BOTÓN FLOTANTE + QUE APARECE EN LA PARTE INFERIOR DERECHA PARA AÑADIR LISTAS/PRODUCTOS
        FloatingActionButton btAdd = findViewById(R.id.fbtAdd);
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentFragment = fragmentManager.findFragmentById(R.id.nav_host_fragment_content_main);
                if (!shoppingListClicked && !areCategoriesShown && (currentFragment instanceof FragmentNoLists || currentFragment instanceof ListFragment)) {
                    showAddListBoxDialog(); //este método mostrará el cuadro de diálogo para añadir una lista al pulsar el botón
                } else if (shoppingListClicked && currentFragment instanceof FragmentNoProducts) {
                    //si estamos dentro de una lista concreta mostrará el listado de categorías para elegir una
                    areCategoriesShown = true;
                    contextMain = MainActivity.this;
                    listType = ListFragment.ListType.CATEGORY_LIST;
                    listFragment.uptadateList(listType);
                    fragmentManager.beginTransaction().addToBackStack(null)
                            .replace(R.id.nav_host_fragment_content_main, ListFragment.class, null)
                            .commit();

                }/* else if (shoppingListClicked && currentFragment instanceof FragmentList) {
                    listType = ListFragment.ListType.CATEGORY_LIST;
                    listFragment.uptadateList(listType);
                    fragmentManager.beginTransaction().addToBackStack(null)
                            .replace(R.id.nav_host_fragment_content_main, ListFragment.class, null)
                            .commit();
                }*/
            }
        });

        //------- MANEJO BOTÓN RETROCESO -------
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                currentFragment = fragmentManager.findFragmentById(R.id.nav_host_fragment_content_main);

                //si estamos dentro de una lista concreta y hemos pulsado mostrar listado de categorías y pulsamos el botón de retroceso..
                if (shoppingListClicked && currentFragment instanceof ListFragment && fragmentManager.getBackStackEntryCount() > 0) {
                    //si estamos dentro de una categoría concreta volveremos al listado de categorías
                    if (categoryListClicked) {
                        categoryListClicked = false;
                        listType = ListFragment.ListType.CATEGORY_LIST;
                        listFragment.uptadateList(listType);
                        fragmentManager.popBackStack();
                        fragmentManager.beginTransaction().addToBackStack(null)
                                .replace(R.id.nav_host_fragment_content_main, ListFragment.class, null)
                                .commit();
                        String shoppingListName = shoppingListSelected.getName();
                        toolbar.setTitle(shoppingListName);
                    //si se está mostrando el listado de categorías volveremos al listado de productos, si no hay se mostrará el fragmentNoProducts
                    } else {
                        shoppingListClicked = true;
                        areCategoriesShown = false;
                        fragmentManager.beginTransaction().addToBackStack(null)
                                .replace(R.id.nav_host_fragment_content_main, fragmentNoProducts)
                                .commit();
                        String shoppingListName = shoppingListSelected.getName();
                        toolbar.setTitle(shoppingListName);
                    }
                //si estamos dentro de una lista concreta y no hay productos, al pulsar el botón de retroceso volveremos al listado de listas
                } else if (shoppingListClicked && currentFragment instanceof FragmentNoProducts && fragmentManager.getBackStackEntryCount() > 0) {
                    shoppingListClicked = false;
                    updateFragmentVisibility(); //este método actualiza el fragmento principal, si hay listas se mostrará el listado de listas, si no el fragmento de no hay listas
                    toolbar.setTitle("Mis listas");
                //si estamos en el fragmento donde se muestra info de la app, volveremos al listado de listas
                } else if (currentFragment instanceof FragmentAppInformation && fragmentManager.getBackStackEntryCount() > 0) {
                    updateFragmentVisibility();
                    toolbar.setTitle("Mis listas");
                } else {
                    finish();
                }
            // Si el cajón de navegación está abierto, lo cerramos
                if(drawerLayout.isOpen())

            {
                drawerLayout.close();
            }
        }
        });

        //--------ESTRUCTURA NAVIGATION DRAWER--------

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Mis listas");
        setSupportActionBar(toolbar);


        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        headerLayout = navigationView.getHeaderView(0);
        updateDataNavigationDrawer();

        //-------- FIN ESTRUCTURA NAVIGATION DRAWER--------


} //------- FIN onCreate -------

    //ESTE MÉTODO SIRVE PARA ACTUALIZAR DATOS DEL NAVIGATION DRAWER
    private void updateDataNavigationDrawer() {

        TextView tvUserName = headerLayout.findViewById(R.id.tvUserName);
        TextView tvUserEmail = headerLayout.findViewById(R.id.tvUserEmail);

        tvUserName.setText("Mi app de la compra");
        tvUserEmail.setText("Salud y alimentación");
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    //MÉTODO PARA MOSTRAR EL MENÚ DE OPCIONES EN LA TOOLBAR (EL DE LOS 3 PUNTOS)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //Recibimos un item donde se ha hecho click en el menú, cuando se hace click se ejecuta este método
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId(); //tenemos el id del item
        FragmentManager fragmentManager = getSupportFragmentManager(); //si queremo tratar los fragments por código
        if (itemId == R.id.action_info) {
            toolbar.setTitle("Instrucciones de la app");
            fragmentInfo = new FragmentAppInformation();

            fragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.nav_host_fragment_content_main,  fragmentInfo, null)
                    .commit();
            return true; //esto indica que hemos tomado mando de la acción indicada en la opción del menú
        }
        return false;
    }

    //MÉTODO PARA MOSTRAR EL MENÚ DE OPCIONES EN EL NAVIGATION DRAWER (EL DE LAS 3 RAYITAS)
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_create_shopping_list) {
            fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_content_main, ListFragment.class, null).commit();
        } else if (itemId == R.id.nav_show_lists) {
            fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_content_main, ListFragment.class, null).commit();
        } else
            throw new RuntimeException("unknown fragment");
        drawerLayout.close();
        return true;
    }

    /*------ FIN PARTE ESTRUCTURA NAVIGATION DRAWER fuera del onCreate ----------*/

    //ESTE MÉTODO SE EJECUTA CUANDO SE HACE CLICK EN EL BOTÓN DE AÑADIR LISTA
    private void showAddListBoxDialog() {
        AddListBoxDialogFragment dialog = new AddListBoxDialogFragment();
        dialog.setOnListAddedListener(this);
        dialog.show(getSupportFragmentManager(), "AddListDialogFragment");
    }

    //CUANDO INSERTEMOS UNA LISTA NUEVA, SE EJECUTA ESTE MÉTODO
    @Override
    public void onListAdded(String listName) {

        contextMain = getApplicationContext();
        // Creamos una nueva lista con el nombre insertado en el cuadro de diálogo
        ListClass newList = new ListClass(listName);

        // Insertamos la nueva lista en la base de datos sqlite
        boolean isInserted = listDAO.insert(newList);

        if (isInserted) {
            if (currentFragment instanceof FragmentNoLists) {
                listFragment = new ListFragment();
                // Si se inserta la nueva lista en la bd se reemplaza el fragmento actual con ListFragment
                fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_content_main, ListFragment.class, null).commit();
                listType = ListFragment.ListType.SHOPPING_LIST;
            } else {
                listType = ListFragment.ListType.SHOPPING_LIST;
                listFragment.uptadateList(listType);
                fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_content_main, ListFragment.class, null).commit();
            }

        } else {
            // Maneja el caso en que la inserción en la base de datos haya fallado
            Toast.makeText(this, "Error al insertar lista en la BD", Toast.LENGTH_SHORT).show();
            Log.e("MainActivity", "Error al insertar lista en la BD");
        }
    }

    //CUANDO PULSEMOS SOBRE UN ITEM LISTA (ES DECIR, SOBRE UNA LISTA DE LA COMPRA) SE EJECUTARÁ ESTE MÉTODO
    @Override
    public void onShoppingListClicked(int position, int realListId) {
        listSelected = realListId;
        Log.d("MainActivity", "ShoppingListClicked: " + listSelected);
        shoppingListSelected = listDAO.findById(listSelected);
        contextMain= getApplicationContext();
        shoppingListClicked = true;
        toolbar.setTitle(shoppingListSelected.getName());

        if (listDAO.hasProducts(shoppingListSelected.getId())) {
           //TODO mostrar el listado de productos añadidos a la lista concreta pulsada
            listType = ListFragment.ListType.CATEGORY_LIST;
            listFragment.uptadateList(listType);
            fragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .replace(R.id.nav_host_fragment_content_main, ListFragment.class, null)
                    .commit();
        }else {
            contextMain = getApplicationContext();
            fragmentNoProducts= new FragmentNoProducts();
            fragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.nav_host_fragment_content_main, fragmentNoProducts)
                    .commit();
        }
    }

    @Override
    public void onCategoryClicked(int position) {
        categoryListClicked = true;
        categorySelected = position+1; //la posición empieza en 0, pero el id de la lista empieza en 1, para que coincidan sumamos 1
        categoryObjectSelected = categoryDAO.findById(categorySelected);
        shoppingListClicked = true;
        toolbar.setTitle(categoryObjectSelected.getName());

            listType = ListFragment.ListType.PRODUCT_LIST;
            listFragment.uptadateList(listType);
            fragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .replace(R.id.nav_host_fragment_content_main, ListFragment.class, null)
                    .commit();

    }

    @Override
    public void onProductClicked(int position, int realProducttId) {
        productSelected = realProducttId;
        Product product = productDAO.findById(productSelected);
        int fkProductId = product.getId();
        int fkListId = shoppingListSelected.getId();
        boolean isPurchased = false;
        ProductList pl = new ProductList(fkProductId, fkListId, isPurchased);
        boolean isInserted = productListDAO.insert(pl);

        if (isInserted) {
            if (currentFragment instanceof FragmentNoProducts) {
                listFragment = new ListFragment();
                listType = ListFragment.ListType.PRODUCTS_IN_LIST;
                // Si se inserta la nueva lista en la bd se reemplaza el fragmento actual con ListFragment
                fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_content_main, ListFragment.class, null).commit();
            } else {
                listType = ListFragment.ListType.PRODUCTS_IN_LIST;
                listFragment.uptadateList(listType);
                fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_content_main, ListFragment.class, null).commit();
            }
        } else {
            // Maneja el caso en que la inserción en la base de datos haya fallado
            Toast.makeText(this, "Error al insertar producto en lista de compra", Toast.LENGTH_SHORT).show();
            Log.e("MainActivity", "Error al insertar producto en lista de compra");
        }
    }

    @Override
    public SQLiteDatabase getDatabase() {
        if(db == null) {
            db = ShoppingListSQLiteHelper.getInstance(this).getWritableDatabase();
        }
        return db;
    }

    @Override
    public ListFragment.ListType getListType() {
        return listType;
    }

    @Override
    public ListClass getList() {
        return shoppingListSelected;
    }

    @Override
    public Category getCategorySelected() {
        return categoryObjectSelected;
    }

    @Override
    public Context getContext() {
        return MainActivity.this;
    }


    @Override
    public void updateFragmentVisibility() {
        if(listDAO.findAll().size() > 0) {
            listFragment = new ListFragment();
            listType = ListFragment.ListType.SHOPPING_LIST;
            fragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.nav_host_fragment_content_main, listFragment)
                    .commit();
        } else {
            fragmentNoLists = new FragmentNoLists();
            fragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.nav_host_fragment_content_main, fragmentNoLists)
                    .commit();
        }
    }

}