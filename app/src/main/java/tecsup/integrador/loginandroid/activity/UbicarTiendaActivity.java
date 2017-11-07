package tecsup.integrador.loginandroid.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import tecsup.integrador.loginandroid.R;

public class UbicarTiendaActivity extends FragmentActivity implements GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, OnMapReadyCallback {

    private static final String TAG = UbicarTiendaActivity.class.getSimpleName();

    private double latitud = 0;
    private double longitud = 0;

    private GoogleMap mMap;
    private TextView mTapTextView;

    private Button btnBack, btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicar_tienda);

        setUpMapIfNeeded();
        mTapTextView = (TextView) findViewById(R.id.tap_text);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnSave = (Button) findViewById(R.id.btnSave);

        // Link to Login Screen
        btnBack.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), TiendaRegisterActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void setUpMap()
    {
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
    }

    @Override
    public void onMapClick(LatLng point) {
        mTapTextView.setText("Punto marcado=" + point);
        mMap.clear();
        // Polylines are useful for marking paths and routes on the map.
        mMap.addPolyline(new PolylineOptions().geodesic(true).width(2).color(Color.RED)
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

        mMap.addMarker(new MarkerOptions()
                .position(point)
                .title("Tu tienda"));

        latitud = point.latitude;
        longitud = point.longitude;

        Log.d(TAG, "LatLng: " + latitud + ", "+ longitud);
    }

    @Override
    public void onMapLongClick(LatLng point) {
        mTapTextView.setText("Punto, presionado=" + point);
        mMap.clear();
        // Polylines are useful for marking paths and routes on the map.
        mMap.addPolyline(new PolylineOptions().geodesic(true).width(2).color(Color.RED)
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

        mMap.addMarker(new MarkerOptions()
                .position(point)
                .title("Tu tienda"));

        latitud = point.latitude;
        longitud = point.longitude;

        Log.d(TAG, "LatLng: " + latitud + ", "+ longitud);
    }

    private void setUpMapIfNeeded() {
        // Hacer una comprobación nula para confirmar que ya no hemos instanciado el mapa.
        if (mMap == null) {
            // Intenta obtener el mapa desde el SupportMapFragment.
            SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFrag.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        if (mMap != null) {
            setUpMap();
        }

        LatLng gamarraTienda = new LatLng(-12.065770361373355, -77.01431976127282);

        float zoomlevel = 16;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gamarraTienda, zoomlevel));

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        // Polylines are useful for marking paths and routes on the map.
        mMap.addPolyline(new PolylineOptions().geodesic(true).width(2).color(Color.RED)
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

    public void Save(View view){

        if(latitud == 0 && longitud == 0){
            Toast.makeText(getApplicationContext(),"Primero marca tu tienda.", Toast.LENGTH_LONG).show();

        } else {
            Intent i = new Intent(this, TiendaRegisterActivity.class);

            String lat = String.valueOf(latitud);
            String lng = String.valueOf(longitud);

            i.putExtra("latitud", lat);
            i.putExtra("longitud", lng);

            startActivity(i);
            finish();

        }
    }

}
