/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 */
package tecsup.integrador.loginandroid.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import tecsup.integrador.loginandroid.R;
import tecsup.integrador.loginandroid.models.Usuario;
import tecsup.integrador.loginandroid.service.ApiService;
import tecsup.integrador.loginandroid.service.ApiServiceGenerator;
import tecsup.integrador.loginandroid.service.ResponseMessage;

public class UserRegisterActivity extends Activity {

    private static final String TAG = UserRegisterActivity.class.getSimpleName();

    private Button btnRegister;
    private Button btnLinkToLogin;

    private EditText inputFullName;
    private EditText inputDNI;
    private EditText inputEmail;
    private EditText inputPassword, inputPassword2;

    private ProgressDialog pDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputFullName = (EditText) findViewById(R.id.name);
        inputDNI = (EditText) findViewById(R.id.dni);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputPassword2 = (EditText) findViewById(R.id.password2);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                final String nombre = inputFullName.getText().toString().trim();
                final String dni = inputDNI.getText().toString().trim();
                final String email = inputEmail.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();
                String password2 = inputPassword2.getText().toString().trim();

                if (!nombre.isEmpty() && !dni.isEmpty() && !email.isEmpty() && !password.isEmpty() && !password2.isEmpty()) {

                    if( dni.length() == 8 ){

                        if ( password.equalsIgnoreCase(password2) ){

                            ApiService service = ApiServiceGenerator.createService(ApiService.class);
                            Call<List<Usuario>> usuarios = service.getUsuarios();
                            usuarios.enqueue(new Callback<List<Usuario>>() {
                                @Override
                                public void onResponse(Call<List<Usuario>> call, retrofit2.Response<List<Usuario>> response) {
                                    try {

                                        int statusCode = response.code();
                                        Log.d(TAG, "HTTP status code: " + statusCode);

                                        if (response.isSuccessful()) {

                                            List<Usuario> usuarios = response.body();
                                            Log.d(TAG, "usuarios: " + usuarios);
                                            hideDialog();


                                            for (Usuario usuario : usuarios) {

                                                if (usuario.getEmail().equalsIgnoreCase(email)) {
                                                    Toast.makeText(getApplication(), "Este Email ya está registrado, intente con otro.", Toast.LENGTH_SHORT).show();


                                                } else if (usuario.getDni().equalsIgnoreCase(dni)) {
                                                    Toast.makeText(getApplication(), "Este DNI ya está registrado, intente con otro.", Toast.LENGTH_SHORT).show();


                                                } else if (!usuario.getEmail().equalsIgnoreCase(email) && !usuario.getDni().equalsIgnoreCase(dni)) {
                                                    registerUser(nombre, dni, email, password);

                                                }

                                            }

                                        } else {
                                            Log.e(TAG, "onError: " + response.errorBody().string());
                                            throw new Exception("Error en el servicio");
                                        }

                                    } catch (Throwable t) {
                                        try {
                                            Log.e(TAG, "onThrowable: " + t.toString(), t);
                                            hideDialog();
                                            Toast.makeText(UserRegisterActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                                        }catch (Throwable x){}
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<Usuario>> call, Throwable t) {
                                    Log.e(TAG, "onFailure: " + t.toString());
                                    hideDialog();
                                    Toast.makeText(UserRegisterActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                                }

                            });

                        }else{
                            Toast.makeText(getApplicationContext(),
                                    "Contraseñas diferentes!", Toast.LENGTH_LONG)
                                    .show();
                        }

                    }else {
                        Toast.makeText(getApplicationContext(),
                                "DNI erróneo!", Toast.LENGTH_LONG)
                                .show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Por favor complete todos los campos!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    protected void registerUser(final String nombre, final String dni, final String email, final String password) {

        ApiService service = ApiServiceGenerator.createService(ApiService.class);
        Call<ResponseMessage> call = null;

        pDialog.setMessage("Registrando ...");
        showDialog();
        call = service.createUsuario(nombre, dni, email, password);

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

                        Toast.makeText(getApplicationContext(), "Usuario registrado. Intente ingresar!", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(
                                UserRegisterActivity.this,
                                LoginActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        hideDialog();
                        Toast.makeText(UserRegisterActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (Throwable x) {
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(UserRegisterActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
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
