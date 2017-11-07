package tecsup.integrador.loginandroid.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tecsup.integrador.loginandroid.R;
import tecsup.integrador.loginandroid.models.Categoria;
import tecsup.integrador.loginandroid.models.Tienda;
import tecsup.integrador.loginandroid.models.tiendaCategoria;
import tecsup.integrador.loginandroid.service.ApiService;
import tecsup.integrador.loginandroid.service.ApiServiceGenerator;
import tecsup.integrador.loginandroid.service.ResponseMessage;

import static android.R.attr.id;

public class TiendaRegisterActivity extends AppCompatActivity {

    private static final String TAG = UserRegisterActivity.class.getSimpleName();

    private Button btnRegister;
    private Button btnBack;

    private EditText inputNombre;
    private EditText inputPuesto;
    private EditText inputTelefono;
    private Button btnMapa;

    MultiAutoCompleteTextView multiCategorias;
    Button Get, Delete;
    List<String> values;
    List<Categoria> categoriaTiendas;
    List<tiendaCategoria> tiendaHasCategorias;

    private String comerciante_id;
    private String tienda_id;
    private SharedPreferences sharedPreferences;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tienda);

        inputNombre = (EditText) findViewById(R.id.txtNombre);
        inputPuesto = (EditText) findViewById(R.id.txtPuesto);
        inputTelefono = (EditText) findViewById(R.id.txtPhone);
        btnMapa = (Button) findViewById(R.id.btnMapa);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnBack = (Button) findViewById(R.id.btnBack);

        multiCategorias = (MultiAutoCompleteTextView) findViewById(R.id.multiAutoCompleteTextView1);
        Get = (Button) findViewById(R.id.btnAgregarCategorias);
        Delete = (Button) findViewById(R.id.btnQuitarCategorias);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // init SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // get id from SharedPreferences
        comerciante_id = sharedPreferences.getString("id", null);
        Log.d(TAG, "comerciante_id: " + comerciante_id);

        tienda_id = sharedPreferences.getString("tienda_id", null);
        Log.d(TAG, "tienda_id: " + tienda_id);

        //Verificar si ya esta registrado
        initialize();

        //Extremos las categorias de productos del api
        ApiService service = ApiServiceGenerator.createService(ApiService.class);
        Call<List<Categoria>> call = service.getCategoriaTienda();

        call.enqueue(new Callback<List<Categoria>>() {
            @Override
            public void onResponse(Call<List<Categoria>> call, Response<List<Categoria>> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {

                        categoriaTiendas = response.body();
                        Log.d(TAG, "categoriaTienda: " + categoriaTiendas);

                        values = new ArrayList<String>();

                        for (Categoria categoria : categoriaTiendas) {
                            values.add(categoria.getNombre());
                        }

                        ArrayAdapter<String> adp = new ArrayAdapter<String>(TiendaRegisterActivity.this, android.R.layout.simple_dropdown_item_1line, values);
                        multiCategorias.setAdapter(adp);
                        multiCategorias.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
                        multiCategorias.setThreshold(1);

                        Get.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                String[] str = multiCategorias.getText().toString().split(", ");

                                for (Categoria categoria : categoriaTiendas) {

                                    for(int i=0; i<str.length; i++) {
                                        if(categoria.getNombre().equalsIgnoreCase(str[i])){
                                            agregarCategorias(tienda_id, String.valueOf(categoria.getId()));

                                        }else {
                                            Toast.makeText(getBaseContext(), "La categoria "+str[i]+" no se encuentra en el producto.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                        });

                        Delete.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                String[] str = multiCategorias.getText().toString().split(", ");

                                for (final Categoria categoria : categoriaTiendas) {

                                    for(int i=0; i<str.length; i++) {
                                        if(categoria.getNombre().equalsIgnoreCase(str[i])){

                                            Log.d(TAG, "tiendaCategorias: " + tiendaHasCategorias);

                                            for (tiendaCategoria tiendaCategoria : tiendaHasCategorias) {

                                                if (tiendaCategoria.getTienda_id().equalsIgnoreCase(tienda_id)
                                                        && tiendaCategoria.getCategoria_tienda_id().equalsIgnoreCase(String.valueOf(categoria.getId()))) {

                                                    quitarCategorias(tiendaCategoria.getId());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        });

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        Toast.makeText(TiendaRegisterActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (Throwable x){}
                }
            }

            @Override
            public void onFailure(Call<List<Categoria>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(TiendaRegisterActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                final String nombre = inputNombre.getText().toString().trim();
                final String puesto = inputPuesto.getText().toString().trim();
                final String telefono = inputTelefono.getText().toString().trim();

                if (!nombre.isEmpty() && !puesto.isEmpty() && !telefono.isEmpty()) {

                    Bundle extras = getIntent().getExtras();
                    if(extras != null){
                        ApiService service = ApiServiceGenerator.createService(ApiService.class);
                        Call<List<Tienda>> tiendas = service.getTiendas();
                        tiendas.enqueue(new Callback<List<Tienda>>() {
                            @Override
                            public void onResponse(Call<List<Tienda>> call, retrofit2.Response<List<Tienda>> response) {
                                try {

                                    int statusCode = response.code();
                                    Log.d(TAG, "HTTP status code: " + statusCode);

                                    if (response.isSuccessful()) {

                                        List<Tienda> tiendas = response.body();
                                        Log.d(TAG, "tiendas: " + tiendas);

                                        for (Tienda tienda : tiendas) {

                                            if (tienda.getPuesto().equalsIgnoreCase(puesto)) {
                                                Toast.makeText(getApplication(), "Error, este puesto ya está registrado.", Toast.LENGTH_SHORT).show();

                                            }  else  {

                                                String latitud = getIntent().getExtras().getString("latitud", null);
                                                String longitud = getIntent().getExtras().getString("longitud", null);

                                                registerTienda(nombre, puesto, telefono, latitud, longitud);
                                            }

                                        }

                                    } else {
                                        Log.e(TAG, "onError: " + response.errorBody().string());
                                        throw new Exception("Error en el servicio");
                                    }

                                } catch (Throwable t) {
                                    try {
                                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                                        Toast.makeText(TiendaRegisterActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                                    }catch (Throwable x){}
                                }
                            }

                            @Override
                            public void onFailure(Call<List<Tienda>> call, Throwable t) {
                                Log.e(TAG, "onFailure: " + t.toString());
                                Toast.makeText(TiendaRegisterActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                            }

                        });

                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Intente ubicar la tienda en el mapa.", Toast.LENGTH_LONG)
                                .show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Por favor complete todos los campos!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        btnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(TiendaRegisterActivity.this, UbicarTiendaActivity.class);
                startActivity(i);
                finish();

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                finish();
            }
        });

    }

    protected void registerTienda(final String nombre, final String puesto, final String telefono, final String latitud, final String longitud ) {

        ApiService service = ApiServiceGenerator.createService(ApiService.class);
        Call<ResponseMessage> call = null;

        pDialog.setMessage("Registrando ...");
        showDialog();

        call = service.createTienda(nombre, puesto, telefono, latitud, longitud, comerciante_id);

        call.enqueue(new Callback<ResponseMessage>() {
            @Override
            public void onResponse(Call<ResponseMessage> call, retrofit2.Response<ResponseMessage> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);
                    hideDialog();

                    if (response.isSuccessful()) {

                        ResponseMessage responseMessage = response.body();
                        Log.d(TAG, "responseMessage: " + responseMessage);

                        Toast.makeText(getApplicationContext(), "Tienda registrada!", Toast.LENGTH_LONG).show();
                        finish();

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        hideDialog();
                        Toast.makeText(TiendaRegisterActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (Throwable x) {
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(TiendaRegisterActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    protected void agregarCategorias(final String tienda_id, final String categoria_tienda_id) {

        ApiService service = ApiServiceGenerator.createService(ApiService.class);
        Call<ResponseMessage> call = null;

        call = service.createTiendaHasCategoria(tienda_id, categoria_tienda_id);

        call.enqueue(new Callback<ResponseMessage>() {
            @Override
            public void onResponse(Call<ResponseMessage> call, retrofit2.Response<ResponseMessage> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);
                    hideDialog();

                    if (response.isSuccessful()) {

                        ResponseMessage responseMessage = response.body();
                        Log.d(TAG, "responseMessage: " + responseMessage);

                        Toast.makeText(getApplicationContext(), "Categorias Agregadas!", Toast.LENGTH_LONG).show();

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        hideDialog();
                        Toast.makeText(TiendaRegisterActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (Throwable x) {
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(TiendaRegisterActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    protected void quitarCategorias(final int id) {

        final ApiService service = ApiServiceGenerator.createService(ApiService.class);

        final Call<ResponseMessage> call = service.destroyTiendaHasCategoria(id);
        call.enqueue(new Callback<ResponseMessage>() {
            @Override
            public void onResponse(Call<ResponseMessage> call, retrofit2.Response<ResponseMessage> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);
                    hideDialog();

                    if (response.isSuccessful()) {

                        ResponseMessage responseMessage = response.body();
                        Log.d(TAG, "responseMessage: " + responseMessage);

                        Toast.makeText(getApplicationContext(), "Categorias Quitadas!", Toast.LENGTH_LONG).show();

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        hideDialog();
                        Toast.makeText(TiendaRegisterActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (Throwable x) {
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(TiendaRegisterActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });

    }

    private void initialize() {

        ApiService service = ApiServiceGenerator.createService(ApiService.class);

        Call<List<tiendaCategoria>> call = service.getTiendaHasCategoria();
        call.enqueue(new Callback<List<tiendaCategoria>>() {
            @Override
            public void onResponse(Call<List<tiendaCategoria>> call, Response<List<tiendaCategoria>> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {

                        tiendaHasCategorias = response.body();
                        Log.d(TAG, "tiendaHasCategorias: " + tiendaHasCategorias);

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        Toast.makeText(TiendaRegisterActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (Throwable x){}
                }
            }

            @Override
            public void onFailure(Call<List<tiendaCategoria>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(TiendaRegisterActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });

        Call<List<Tienda>> callTiendas = service.getTiendas();
        callTiendas.enqueue(new Callback<List<Tienda>>() {
            @Override
            public void onResponse(Call<List<Tienda>> call, Response<List<Tienda>> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {

                        List<Tienda> tiendas = response.body();
                        Log.d(TAG, "tiendas: " + tiendas);

                        for (Tienda tienda : tiendas) {

                            if (String.valueOf(tienda.getComerciante_id()).equalsIgnoreCase(comerciante_id)) {

                                String nombre = tienda.getNombre();
                                String puesto = tienda.getPuesto();
                                String telefono = tienda.getTelefono();

                                inputNombre.setText(nombre);
                                inputPuesto.setText(puesto);
                                inputTelefono.setText(telefono);

                                List<String> valuesSet = new ArrayList<String>();
                                for (tiendaCategoria tiendaCategoria : tiendaHasCategorias) {
                                    if (tiendaCategoria.getTienda_id().equalsIgnoreCase(tienda_id)) {
                                        for (Categoria categoria : categoriaTiendas) {
                                            if(tiendaCategoria.getCategoria_tienda_id().equalsIgnoreCase(String.valueOf(categoria.getId()))){
                                                valuesSet.add(categoria.getNombre());
                                            }
                                        }
                                    }
                                }
                                Log.d(TAG, "values: " + valuesSet);

                                String cadena = valuesSet.toString();
                                int cadena1 = cadena.length();//ubico el tamaño de la cadena
                                String extraerp = cadena.substring(0,1); // Extraigo laprimera letra
                                String extraeru = cadena.substring(cadena1-1); //Extraigo la ultima letra letra

                                String remplazado = cadena.replace(extraerp,""); // quitamos el primer caracter
                                String remplazadofinal = remplazado.replace(extraeru, "");// se quita el ultimo caracter
                                Log.d(TAG, "valuesSet: " + remplazadofinal);
                                multiCategorias.setText(remplazadofinal);
                            }
                        }

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        Toast.makeText(TiendaRegisterActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (Throwable x){}
                }
            }

            @Override
            public void onFailure(Call<List<Tienda>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(TiendaRegisterActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
