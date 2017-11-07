package tecsup.integrador.loginandroid.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import layout.ExplorarFragment;
import layout.MapFragment;
import layout.PerfilFragment;
import layout.ProductoFragment;
import layout.StoreFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tecsup.integrador.loginandroid.R;
import tecsup.integrador.loginandroid.models.Usuario;
import tecsup.integrador.loginandroid.service.ApiService;
import tecsup.integrador.loginandroid.service.ApiServiceGenerator;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView txtName;
    private TextView txtEmail;

    private String id;
    private String nombre;
    private String email;
    private String dni;

    // SharedPreferences
    private SharedPreferences sharedPreferences;

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //con esto agregamos datos en el header del menu-------------------------------
        View hView = navigationView.getHeaderView(0);
        txtName = (TextView) hView.findViewById(R.id.name);
        txtEmail = (TextView) hView.findViewById(R.id.email);

        // init SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // get id from SharedPreferences
        id = sharedPreferences.getString("id", null);
        Log.d(TAG, "comerciante_id: " + id);

        final ApiService service = ApiServiceGenerator.createService(ApiService.class);

        Call<List<Usuario>> call = service.getUsuarios();

        call.enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {

                        List<Usuario> usuarios = response.body();
                        Log.d(TAG, "usuarios: " + usuarios);

                        for (Usuario usuario : usuarios) {

                            if (String.valueOf(usuario.getId()).equalsIgnoreCase(id)) {

                                nombre = usuario.getNombre();
                                email = usuario.getEmail();
                                dni = usuario.getDni();

                                txtName.setText(nombre);
                                txtEmail.setText(email);

                            } else {

                                Toast.makeText(getApplicationContext(), "Usuario no encontrado.", Toast.LENGTH_SHORT);
                            }
                        }

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (Throwable x){}
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });


    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {

        // remove from SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean success = editor.putBoolean("islogged", false).commit();
        boolean success2 = editor
                .putString("tienda_id", "")
                .commit();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (id == R.id.navigation_store) {
            StoreFragment fragment = new StoreFragment();
            transaction.replace(R.id.content, fragment);
            transaction.commit();

        } else if (id == R.id.navigation_productos) {
            ProductoFragment fragment = new ProductoFragment();
            transaction.replace(R.id.content, fragment);
            transaction.commit();

        } else if (id == R.id.navigation_map) {
            MapFragment fragment = new MapFragment();
            transaction.replace(R.id.content, fragment);
            transaction.commit();

        } else if (id == R.id.navigation_explorar) {
            ExplorarFragment fragment = new ExplorarFragment();
            transaction.replace(R.id.content, fragment);
            transaction.commit();

        } else if (id == R.id.nav_perfil) {
            PerfilFragment fragment = new PerfilFragment();
            transaction.replace(R.id.content, fragment);

            Bundle args = new Bundle();
            args.putString("name", nombre);
            args.putString("email", email);
            args.putString("dni", dni);
            fragment.setArguments(args);
            transaction.commit();

        } else if (id == R.id.nav_logut) {
            logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {

                case R.id.navigation_store:
                    StoreFragment fragment1 = new StoreFragment();
                    FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
                    transaction1.replace(R.id.content, fragment1);
                    transaction1.commit();

                    return true;
                case R.id.navigation_productos:
                    ProductoFragment fragment2 = new ProductoFragment();
                    FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                    transaction2.replace(R.id.content, fragment2);
                    transaction2.commit();

                    return true;
                case R.id.navigation_map:
                    MapFragment fragment3 = new MapFragment();
                    FragmentTransaction transaction3 = getSupportFragmentManager().beginTransaction();
                    transaction3.replace(R.id.content, fragment3);
                    transaction3.commit();

                    return true;
                case R.id.navigation_explorar:
                    ExplorarFragment fragment4 = new ExplorarFragment();
                    FragmentTransaction transaction4 = getSupportFragmentManager().beginTransaction();
                    transaction4.replace(R.id.content, fragment4);
                    transaction4.commit();

                    return true;
            }
            return false;
        }

    };
}
