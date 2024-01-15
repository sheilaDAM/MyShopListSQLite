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
import com.sheilajnieto.myshoplistsqlite.db.sqlite.dao.ListDAO;
import com.sheilajnieto.myshoplistsqlite.interfaces.IOnClickListener;
import com.sheilajnieto.myshoplistsqlite.models.ListClass;
import com.sheilajnieto.myshoplistsqlite.ui.AddListBoxDialogFragment;
import com.sheilajnieto.myshoplistsqlite.ui.DetailListFragment;
import com.sheilajnieto.myshoplistsqlite.ui.FragmentNoLists;
import com.sheilajnieto.myshoplistsqlite.ui.ListFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AddListBoxDialogFragment.OnListAddedListener, IOnClickListener, ListFragment.IOnAttachListener {

    private DrawerLayout drawerLayout;
    private ListFragment listFragment;
    private FragmentNoLists fragmentNoLists;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Toolbar toolbar;
    private View headerLayout;
    private DetailListFragment fragmentDetalle;
    private boolean hayDetalle;
    private int listClicked;
    private SQLiteDatabase db;
    private ListDAO listDAO;


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
        if(listDAO.findAll().size() > 0) {
            listFragment = new ListFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment_content_main, listFragment)
                    .commit();
        } else {
            fragmentNoLists = new FragmentNoLists();
            fragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment_content_main, fragmentNoLists)
                    .commit();
        }


        FloatingActionButton btAdd = findViewById(R.id.fbtAdd);
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddListDialog();
            }
        });

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Lista de la compra");
        setSupportActionBar(toolbar);


        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        headerLayout = navigationView.getHeaderView(0);
        actualizarDatosNavigationDrawer();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Fragment currentFragment = fragmentManager.findFragmentById(R.id.nav_host_fragment_content_main);
                if (hayDetalle) {
                    if (currentFragment instanceof DetailListFragment && fragmentManager.getBackStackEntryCount() > 0) {
                        fragmentManager.popBackStack();
                        hayDetalle = false;
                    }
                } else if (!hayDetalle) {
                    if (drawerLayout.isOpen()) {
                        drawerLayout.close();
                    } else {
                        // Si no hay detalle y no está abierto el cajón de navegación, cierra la aplicación
                        finishAffinity();
                    }
                }
            }
        });

        /* FIN ESTRUCTURA NAVIGATION DRAWER*/

} //FIN onCreate

    private void showAddListDialog() {
        AddListBoxDialogFragment dialog = new AddListBoxDialogFragment();
        dialog.setOnListAddedListener(this);
        dialog.show(getSupportFragmentManager(), "AddListDialogFragment");
    }

    private void actualizarDatosNavigationDrawer() {

        TextView tvUserName = headerLayout.findViewById(R.id.tvUserName);
        TextView tvUserEmail = headerLayout.findViewById(R.id.tvUserEmail);

        tvUserName.setText("Sheila Jiménez Nieto");
        tvUserEmail.setText("sheilajnieto@gmail.com");
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void cargarDatos() {
       // Toast.makeText(this, "Listas cargadas: ", Toast.LENGTH_SHORT).show();
    }

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

    @Override
    public void onClick(int position) {
        listClicked = position;
        hayDetalle = true;
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .replace(R.id.nav_host_fragment_content_main, DetailListFragment.class, null)
                .commit();

    }

    @Override
    public void onListAdded(String listName) {

        // Creamos una instancia de ListDAO para insertar la nueva lista en la base de datos
        //listDAO = new ListDAO(db);

        // Creamos una nueva lista con el nombre insertado en el cuadro de diálogo
        ListClass newList = new ListClass(listName);

        // Insertamos la nueva lista en la base de datos sqlite
        boolean isInserted = listDAO.insert(newList);

        if (isInserted) {
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

    @Override
    public SQLiteDatabase getDatabase() {
        if(db == null) {
            db = ShoppingListSQLiteHelper.getInstance(this).getWritableDatabase();
        }
        return db;
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