package tecsup.integrador.loginandroid.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tecsup.integrador.loginandroid.R;
import tecsup.integrador.loginandroid.models.Categoria;
import tecsup.integrador.loginandroid.service.ApiService;
import tecsup.integrador.loginandroid.service.ApiServiceGenerator;
import tecsup.integrador.loginandroid.service.ResponseMessage;

public class ProductoRegisterActivity extends AppCompatActivity {

    private static final String TAG = UserRegisterActivity.class.getSimpleName();

    private ImageView imagePreview;
    private Spinner spinnerCategoria;

    private EditText nombreInput;
    private EditText precioInput;
    private EditText detallesInput;

    // SharedPreferences
    private SharedPreferences sharedPreferences;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto_register);

        imagePreview = (ImageView) findViewById(R.id.imagen_preview);
        nombreInput = (EditText) findViewById(R.id.nombre_input);
        precioInput = (EditText) findViewById(R.id.precio_input);
        detallesInput = (EditText) findViewById(R.id.detalles_input);
        spinnerCategoria = (Spinner) findViewById(R.id.spinner);

        // init SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        //Extremos las categorias de productos del api
        ApiService service = ApiServiceGenerator.createService(ApiService.class);
        Call<List<Categoria>> call = service.getCategoriaProducto();

        call.enqueue(new Callback<List<Categoria>>() {
            @Override
            public void onResponse(Call<List<Categoria>> call, Response<List<Categoria>> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {

                        final List<Categoria> categoriasProducto = response.body();
                        Log.d(TAG, "categorias: " + categoriasProducto);

                        List<String> values = new ArrayList<String>();

                        for (Categoria categoria : categoriasProducto) {
                            values.add(categoria.getNombre());
                        }

                        ArrayAdapter dataAdapter = new ArrayAdapter(getApplication(), android.R.layout.simple_spinner_item, values);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerCategoria.setAdapter(dataAdapter);

                        spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                                String item = parent.getItemAtPosition(pos).toString();
                                Toast.makeText(parent.getContext(), "Haz seleccionado: " + item, Toast.LENGTH_LONG).show();

                                for (Categoria categoria : categoriasProducto) {

                                    if( categoria.getNombre().equalsIgnoreCase(item)){
                                        int categoria_producto_id = categoria.getId();
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        boolean success3 = editor
                                                .putString("categoria_producto_id", String.valueOf(categoria_producto_id))
                                                .commit();
                                    }
                                }

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        Toast.makeText(ProductoRegisterActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (Throwable x){}
                }
            }

            @Override
            public void onFailure(Call<List<Categoria>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(ProductoRegisterActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });

    }

    /**
     * Camera handler
     */

    private static final int CAPTURE_IMAGE_REQUEST = 300;

    private Uri mediaFileUri;

    public void takePicture(View view) {
        try {

            if (!permissionsGranted()) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_LIST, PERMISSIONS_REQUEST);
                return;
            }

            // Creando el directorio de imágenes (si no existe)
            File mediaStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    throw new Exception("Failed to create directory");
                }
            }

            // Definiendo la ruta destino de la captura (Uri)
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
            mediaFileUri = Uri.fromFile(mediaFile);

            // Iniciando la captura
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mediaFileUri);
            startActivityForResult(intent, CAPTURE_IMAGE_REQUEST);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
            Toast.makeText(this, "Error en captura: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_REQUEST) {
            // Resultado en la captura de la foto
            if (resultCode == RESULT_OK) {
                try {
                    Log.d(TAG, "ResultCode: RESULT_OK");
                    // Toast.makeText(this, "Image saved to: " + mediaFileUri.getPath(), Toast.LENGTH_LONG).show();

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mediaFileUri);

                    // Reducir la imagen a 800px solo si lo supera
                    bitmap = scaleBitmapDown(bitmap, 800);

                    imagePreview.setImageBitmap(bitmap);
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                    Toast.makeText(this, "Error al procesar imagen: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "ResultCode: RESULT_CANCELED");
            } else {
                Log.d(TAG, "ResultCode: " + resultCode);
            }
        }
    }

    public void callRegister(View view) {

        String nombre = nombreInput.getText().toString();
        String precio = precioInput.getText().toString();
        String detalles = detallesInput.getText().toString();

        if (nombre.isEmpty() || precio.isEmpty()) {
            Toast.makeText(this, "Nombre y Precio son campos requeridos", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService service = ApiServiceGenerator.createService(ApiService.class);

        Call<ResponseMessage> call = null;

        if (mediaFileUri == null) {
            Toast.makeText(this, "Suba una fotografía.", Toast.LENGTH_SHORT).show();
            return;

        } else {

            pDialog.setMessage("Subiendo producto ...");
            showDialog();

            // Si se incluye hacemos envió en multiparts
            File file = new File(mediaFileUri.getPath());
            Log.d(TAG, "File: " + file.getPath() + " - exists: " + file.exists());

            // Podemos enviar la imagen con el tamaño original, pero lo mejor será comprimila antes de subir (byteArray)
            // RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);

            Bitmap bitmap = BitmapFactory.decodeFile(mediaFileUri.getPath());

            // Reducir la imagen a 800px solo si lo supera
            bitmap = scaleBitmapDown(bitmap, 800);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), byteArray);
            MultipartBody.Part imagenPart = MultipartBody.Part.createFormData("imagen", file.getName(), requestFile);

            // get id from SharedPreferences
            String tienda_id = sharedPreferences.getString("tienda_id", null);
            Log.d(TAG, "tienda_id: " + tienda_id);

            String categoria_producto_id = sharedPreferences.getString("categoria_producto_id", null);
            Log.d(TAG, "categoria_producto_id: " + categoria_producto_id);

            RequestBody nombrePart = RequestBody.create(MultipartBody.FORM, nombre);
            RequestBody precioPart = RequestBody.create(MultipartBody.FORM, precio);
            RequestBody detallesPart = RequestBody.create(MultipartBody.FORM, detalles);
            RequestBody tiendaIdPart = RequestBody.create(MultipartBody.FORM, tienda_id);
            RequestBody categoriaIdPart = RequestBody.create(MultipartBody.FORM, categoria_producto_id);

            call = service.createProducto(nombrePart, precioPart, detallesPart, tiendaIdPart, categoriaIdPart, imagenPart);
        }

        call.enqueue(new Callback<ResponseMessage>() {
            @Override
            public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {

                        hideDialog();
                        ResponseMessage responseMessage = response.body();
                        Log.d(TAG, "responseMessage: " + responseMessage);

                        Toast.makeText(ProductoRegisterActivity.this, responseMessage.getMessage(), Toast.LENGTH_LONG).show();
                        finish();

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        hideDialog();
                        Toast.makeText(ProductoRegisterActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (Throwable x) {
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(ProductoRegisterActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    /**
     * Permissions handler
     */

    private static final int PERMISSIONS_REQUEST = 200;

    private static String[] PERMISSIONS_LIST = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private boolean permissionsGranted() {
        for (String permission : PERMISSIONS_LIST) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {
                for (int i = 0; i < grantResults.length; i++) {
                    Log.d(TAG, "" + grantResults[i]);
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, PERMISSIONS_LIST[i] + " permiso rechazado!", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                Toast.makeText(this, "Permisos concedidos, intente nuevamente.", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Redimensionar una imagen bitmap
    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
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
