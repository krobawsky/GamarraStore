package tecsup.integrador.loginandroid.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import layout.StoreFragment;
import retrofit2.Call;
import retrofit2.Callback;
import tecsup.integrador.loginandroid.R;
import tecsup.integrador.loginandroid.models.Tienda;
import tecsup.integrador.loginandroid.service.ApiService;
import tecsup.integrador.loginandroid.service.ApiServiceGenerator;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;

    private static final String TAG = StoreFragment.class.getSimpleName();

    private Button bmapa;
    private Button bterreno;
    private Button bhibrido;
    private Button binterior;

    // SharedPreferences
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        bmapa = (Button)findViewById(R.id.bmapa);
        bterreno = (Button)findViewById(R.id.bterreno);
        bhibrido = (Button)findViewById(R.id.bhibrido);
        binterior = (Button)findViewById(R.id.binterior);

        bmapa.setOnClickListener(this);
        bterreno.setOnClickListener(this);
        bhibrido.setOnClickListener(this);
        binterior.setOnClickListener(this);

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if(status == ConnectionResult.SUCCESS){

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

        } else {

            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, (Activity)getApplicationContext(), 10);
            dialog.show();

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bmapa:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                // Polylines are useful for marking paths and routes on the map.
                mMap.addPolyline(new PolylineOptions().geodesic(true).width(3).color(Color.RED)
                        .add(new LatLng(-12.061628, -77.018032))  // 1
                        .add(new LatLng(-12.068700, -77.017075))  // 2
                        .add(new LatLng(-12.068641, -77.016098))  // 3
                        .add(new LatLng(-12.069630, -77.015940))  // 4
                        .add(new LatLng(-12.069368, -77.013823))  // 5
                        .add(new LatLng(-12.071609, -77.013477))  // 6
                        .add(new LatLng(-12.071552, -77.013095))  // 7
                        .add(new LatLng(-12.072325, -77.012881))  // 8
                        .add(new LatLng(-12.071772, -77.011634))  // 9
                        .add(new LatLng(-12.061044, -77.013112))  // 10
                        .add(new LatLng(-12.061628, -77.018032))  // 11
                );

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

                                    double latitudDouble = Double.parseDouble(tienda.getLatitud());
                                    double longitudDouble = Double.parseDouble(tienda.getLongitud());
                                    Log.d(TAG, "LatLng: " + latitudDouble + " ; " + longitudDouble);

                                    LatLng gamarraTienda = new LatLng(latitudDouble, longitudDouble);
                                    mMap.addMarker(new MarkerOptions().position(gamarraTienda).title(tienda.getNombre()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                                }

                            } else {
                                Log.e(TAG, "onError: " + response.errorBody().string());
                                throw new Exception("Error en el servicio");
                            }

                        } catch (Throwable t) {
                            try {
                                Log.e(TAG, "onThrowable: " + t.toString(), t);
                                Toast.makeText(MapsActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                            }catch (Throwable x){}
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Tienda>> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.toString());
                        Toast.makeText(MapsActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

                break;
            case R.id.bhibrido:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                // Polylines are useful for marking paths and routes on the map.
                mMap.addPolyline(new PolylineOptions().geodesic(true).width(3).color(Color.RED)
                        .add(new LatLng(-12.061628, -77.018032))  // 1
                        .add(new LatLng(-12.068700, -77.017075))  // 2
                        .add(new LatLng(-12.068641, -77.016098))  // 3
                        .add(new LatLng(-12.069630, -77.015940))  // 4
                        .add(new LatLng(-12.069368, -77.013823))  // 5
                        .add(new LatLng(-12.071609, -77.013477))  // 6
                        .add(new LatLng(-12.071552, -77.013095))  // 7
                        .add(new LatLng(-12.072325, -77.012881))  // 8
                        .add(new LatLng(-12.071772, -77.011634))  // 9
                        .add(new LatLng(-12.061044, -77.013112))  // 10
                        .add(new LatLng(-12.061628, -77.018032))  // 11
                );
                break;
            case R.id.bterreno:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                // Polylines are useful for marking paths and routes on the map.
                mMap.addPolyline(new PolylineOptions().geodesic(true).width(3).color(Color.RED)
                        .add(new LatLng(-12.061628, -77.018032))  // 1
                        .add(new LatLng(-12.068700, -77.017075))  // 2
                        .add(new LatLng(-12.068641, -77.016098))  // 3
                        .add(new LatLng(-12.069630, -77.015940))  // 4
                        .add(new LatLng(-12.069368, -77.013823))  // 5
                        .add(new LatLng(-12.071609, -77.013477))  // 6
                        .add(new LatLng(-12.071552, -77.013095))  // 7
                        .add(new LatLng(-12.072325, -77.012881))  // 8
                        .add(new LatLng(-12.071772, -77.011634))  // 9
                        .add(new LatLng(-12.061044, -77.013112))  // 10
                        .add(new LatLng(-12.061628, -77.018032))  // 11
                );
                break;
            case R.id.binterior:
                // Algunos edificios tienen mapa de interior. Hay que ponerse sobre ellos y directamente veremos las plantas
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(-12.065770361373355, -77.01431976127282), 16));

                // Polylines are useful for marking paths and routes on the map.
                mMap.addPolyline(new PolylineOptions().geodesic(true).width(3).color(Color.RED)
                        .add(new LatLng(-12.061628, -77.018032))  // 1
                        .add(new LatLng(-12.068700, -77.017075))  // 2
                        .add(new LatLng(-12.068641, -77.016098))  // 3
                        .add(new LatLng(-12.069630, -77.015940))  // 4
                        .add(new LatLng(-12.069368, -77.013823))  // 5
                        .add(new LatLng(-12.071609, -77.013477))  // 6
                        .add(new LatLng(-12.071552, -77.013095))  // 7
                        .add(new LatLng(-12.072325, -77.012881))  // 8
                        .add(new LatLng(-12.071772, -77.011634))  // 9
                        .add(new LatLng(-12.061044, -77.013112))  // 10
                        .add(new LatLng(-12.061628, -77.018032))  // 11
                );

                break;
            default:
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        String tiendaNombre = getIntent().getExtras().getString("nombre");
        String latitud = getIntent().getExtras().getString("latitud", null);
        String longitud = getIntent().getExtras().getString("longitud", null);

        double latitudDouble = Double.parseDouble(latitud);
        double longitudDouble = Double.parseDouble(longitud);
        Log.d(TAG, "LatLng: " + latitud + " ; " + latitudDouble);

        if (latitud == null && longitud == null){
            // Add a marker in Gamarra and move the camera
            Toast.makeText(getApplicationContext(),"Error", Toast.LENGTH_LONG).show();

        } else {
            // Add a marker in Mapa and move the camera
            LatLng gamarra = new LatLng(latitudDouble, longitudDouble);
            float zoomlevel = 16;
            mMap.addMarker(new MarkerOptions().position(gamarra).title(tiendaNombre).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gamarra, zoomlevel));
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(getApplicationContext(),"Has pulsado una marca", Toast.LENGTH_LONG).show();
                return false;
            }
        });

        // Polylines are useful for marking paths and routes on the map.
        mMap.addPolyline(new PolylineOptions().geodesic(true).width(3).color(Color.RED)
                .add(new LatLng(-12.061628, -77.018032))  // 1
                .add(new LatLng(-12.068700, -77.017075))  // 2
                .add(new LatLng(-12.068641, -77.016098))  // 3
                .add(new LatLng(-12.069630, -77.015940))  // 4
                .add(new LatLng(-12.069368, -77.013823))  // 5
                .add(new LatLng(-12.071609, -77.013477))  // 6
                .add(new LatLng(-12.071552, -77.013095))  // 7
                .add(new LatLng(-12.072325, -77.012881))  // 8
                .add(new LatLng(-12.071772, -77.011634))  // 9
                .add(new LatLng(-12.061044, -77.013112))  // 10
                .add(new LatLng(-12.061628, -77.018032))  // 11
        );
    }
}
