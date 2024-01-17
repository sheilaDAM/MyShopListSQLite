package com.sheilajnieto.myshoplistsqlite;

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
import com.sheilajnieto.myshoplistsqlite.interfaces.IOnClickListener;
import com.sheilajnieto.myshoplistsqlite.models.ListClass;
import com.sheilajnieto.myshoplistsqlite.ui.AddListBoxDialogFragment;
import com.sheilajnieto.myshoplistsqlite.ui.FragmentNoLists;
import com.sheilajnieto.myshoplistsqlite.ui.FragmentNoProducts;
import com.sheilajnieto.myshoplistsqlite.ui.ListFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AddListBoxDialogFragment.OnListAddedListener, IOnClickListener, ListFragment.IOnAttachListener {

    private DrawerLayout drawerLayout;
    private ListFragment listFragment; //fragmento que se muestra cuando hay listas
    private FragmentNoLists fragmentNoLists; //fragmento que se muestra cuando no hay listas
    private FragmentNoProducts fragmentNoProducts; //fragmento que se muestra cuando no hay productos
    private FragmentManager fragmentManager = getSupportFragmentManager(); //para poder tratar los fragments por código
    private Toolbar toolbar;
    private View headerLayout;
    private ListFragment.ListType listType; //para determinar qué tipo de listado se muestra, si listas, categorías o productos
    private boolean shoppingListClicked; //para saber si se ha pulsado sobre una lista de la compra
    private SQLiteDatabase db;
    private ListDAO listDAO; //para acceder a los métodos de listDAO y poder hacer las consultas a la base de datos
    private int listSelected; //guarda el id de la lista seleccionada
    private ListClass shoppingListSelected; //guarda la lista seleccionada
    private Fragment currentFragment; //para saber en qué fragment estamos


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //obtenemos la instancia de la base de datos
        db = ShoppingListSQLiteHelper.getInstance(this).getWritableDatabase();
        if (db == null) {
            Toast.makeText(this, "Error al conectar con la BD", Toast.LENGTH_SHORT).show();
            finish();
        }

        listDAO = new ListDAO(db);

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
                if (shoppingListClicked && currentFragment instanceof ListFragment) {
                    showAddListBoxDialog(); //este método mostrará el cuadro de diálogo para añadir una lista al pulsar el botón
                } else if (shoppingListClicked && currentFragment instanceof FragmentNoProducts) {
                    //mostrará el listado de categorías para elegir una
                    listType = ListFragment.ListType.CATEGORY_LIST;
                    listFragment.uptadateList(listType);
                    fragmentManager.beginTransaction().addToBackStack(null)
                            .replace(R.id.nav_host_fragment_content_main, ListFragment.class, null)
                            .commit();
                }
            }
        });

        //--------ESTRUCTURA NAVIGATION DRAWER--------

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Mis listas de la compra");
        setSupportActionBar(toolbar);


        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        headerLayout = navigationView.getHeaderView(0);
        updateDataNavigationDrawer();

        //------- MANEJO BOTÓN RETROCESO -------
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                currentFragment = fragmentManager.findFragmentById(R.id.nav_host_fragment_content_main);

                // Si estamos en la lista de categorías, regresamos a la lista de la compra
                if (shoppingListClicked && currentFragment instanceof ListFragment && fragmentManager.getBackStackEntryCount() > 0) {
                    listType = ListFragment.ListType.SHOPPING_LIST;
                    listFragment.uptadateList(listType);
                    fragmentManager.popBackStack();
                    fragmentManager.beginTransaction().addToBackStack(null).addToBackStack(null)
                            .replace(R.id.nav_host_fragment_content_main, ListFragment.class, null)
                            .commit();
                    toolbar.setTitle("Mis listas de la compra");
                    shoppingListClicked = false;
                } else if (shoppingListClicked && currentFragment instanceof FragmentNoProducts && fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack();
                    listFragment.uptadateList(ListFragment.ListType.SHOPPING_LIST);
                    toolbar.setTitle("Mis listas de la compra");
                    shoppingListClicked = false;
                }else {
                        finish();
                }
                // Si el cajón de navegación está abierto, lo cerramos
                if (drawerLayout.isOpen()) {
                    drawerLayout.close();
                }
            }
        });


} //FIN onCreate

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
        if (itemId == R.id.nav_create_shopping_list) {
            toolbar.setTitle("Crear lista de la compra");
            return true; //esto indica que hemos tomado mando de la acción indicada en la opción del menú
        } else if (itemId == R.id.nav_show_lists) {
            toolbar.setTitle("Mis listas de la compra");
            //para reemplazar un fragment por otro en el container view, cambia lo que hay en el fcvListado por el Fragment.class que indicamos
            //fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_content_main, FragmentListado.class, null).commit();
            return true;
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

    /*------ FIN ESTRUCTURA NAVIGATION DRAWER ----------*/

    //ESTE MÉTODO SE EJECUTA CUANDO SE HACE CLICK EN EL BOTÓN DE AÑADIR LISTA
    private void showAddListBoxDialog() {
        AddListBoxDialogFragment dialog = new AddListBoxDialogFragment();
        dialog.setOnListAddedListener(this);
        dialog.show(getSupportFragmentManager(), "AddListDialogFragment");
    }

    //CUANDO INSERTEMOS UNA LISTA NUEVA, SE EJECUTA ESTE MÉTODO
    @Override
    public void onListAdded(String listName) {

        // Creamos una nueva lista con el nombre insertado en el cuadro de diálogo
        ListClass newList = new ListClass(listName);

        // Insertamos la nueva lista en la base de datos sqlite
        boolean isInserted = listDAO.insert(newList);

        if (isInserted) {
            listType = ListFragment.ListType.SHOPPING_LIST;
            listFragment.uptadateList(listType);
            // Si se inserta la nueva lista en la bd se reemplaza el fragmento actual con ListFragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment_content_main, ListFragment.class, null)
                    .addToBackStack(null)
                    .commit();
            if (listFragment != null) {
               // listFragment.updateRecyclerView(listName);
            }
        } else {
            // Maneja el caso en que la inserción en la base de datos haya fallado
            Toast.makeText(this, "Error al insertar lista en la BD", Toast.LENGTH_SHORT).show();
            Log.e("MainActivity", "Error al insertar lista en la BD");
        }
    }

    //CUANDO PULSEMOS SOBRE UN ITEM LISTA (ES DECIR, SOBRE UNA LISTA DE LA COMPRA) SE EJECUTARÁ ESTE MÉTODO
    @Override
    public void onShoppingListClicked(int position) {
        listSelected = position+1; //la posición empieza en 0, pero el id de la lista empieza en 1, para que coincidan sumamos 1
        shoppingListSelected = listDAO.findById(listSelected);
        shoppingListClicked = true;
        toolbar.setTitle(shoppingListSelected.getName());

        if (listDAO.hasProducts(shoppingListSelected.getId())) {

            listType = ListFragment.ListType.CATEGORY_LIST;
            listFragment.uptadateList(listType);
            fragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .replace(R.id.nav_host_fragment_content_main, ListFragment.class, null)
                    .commit();
        }else {
            fragmentNoProducts= new FragmentNoProducts();
            fragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.nav_host_fragment_content_main, fragmentNoProducts)
                    .commit();
        }
    }

    @Override
    public void onCategoryClicked(int position) {

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
}

/*
Para insertar la fecha en la base de datos, puedes convertir el Date a String utilizando SimpleDateFormat:

java
Copy code
Date now = new Date();
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
String formattedDate = sdf.format(now);
// Ahora, puedes guardar formattedDate en tu base de datos
Y cuando recuperas el valor de la base de datos, puedes convertir el String a Date de la siguiente manera:

java
Copy code
String dateFromDatabase = // Obtén la fecha de la base de datos
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
Date parsedDate = null;
try {
    parsedDate = sdf.parse(dateFromDatabase);
} catch (ParseException e) {
    e.printStackTrace();
}
// Ahora, parsedDate contiene la fecha y hora recuperadas
 */