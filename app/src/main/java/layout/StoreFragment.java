package layout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tecsup.integrador.loginandroid.R;
import tecsup.integrador.loginandroid.activity.MapsActivity;
import tecsup.integrador.loginandroid.activity.TiendaRegisterActivity;
import tecsup.integrador.loginandroid.models.Categoria;
import tecsup.integrador.loginandroid.models.Tienda;
import tecsup.integrador.loginandroid.models.tiendaCategoria;
import tecsup.integrador.loginandroid.service.ApiService;
import tecsup.integrador.loginandroid.service.ApiServiceGenerator;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StoreFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StoreFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public StoreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StoreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StoreFragment newInstance(String param1, String param2) {
        StoreFragment fragment = new StoreFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private static final String TAG = StoreFragment.class.getSimpleName();

    private SharedPreferences sharedPreferences;

    private Button btnLinkToRegister;

    private Button btnMapa;
    private TextView inputNombre;
    private TextView inputPuesto;
    private TextView inputTelefono;
    private TextView inputCategorias;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_store, container, false);

        btnLinkToRegister = (Button) view.findViewById(R.id.btnRegister);

        btnMapa = (Button) view.findViewById(R.id.btnMapa);
        inputNombre = (TextView) view.findViewById(R.id.txtNombre);
        inputPuesto = (TextView) view.findViewById(R.id.txtPuesto);
        inputTelefono = (TextView) view.findViewById(R.id.txtPhone);
        inputCategorias = (TextView) view.findViewById(R.id.txtCategorias);

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

                        // init SharedPreferences
                        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

                        // get id from SharedPreferences
                        String comerciante_id = sharedPreferences.getString("id", null);
                        Log.d(TAG, "comerciante_id: " + comerciante_id);

                        for (Tienda tienda : tiendas) {

                            if (tienda.getComerciante_id().equalsIgnoreCase(comerciante_id)) {

                                Log.d(TAG, "id: " + tienda.getComerciante_id());
                                //Obtenemos las categorias
                                getCategorias();

                                int tienda_id = tienda.getId();
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                boolean success2 = editor
                                        .putString("tienda_id", String.valueOf(tienda_id))
                                        .commit();
                                Log.d(TAG, "tienda_id: " + tienda_id);

                                inputNombre.setText(tienda.getNombre());
                                inputPuesto.setText(tienda.getPuesto());
                                inputTelefono.setText(tienda.getTelefono());

                                final String nombre = tienda.getNombre();
                                final String latitud = tienda.getLatitud();
                                final String longitud = tienda.getLongitud();

                                btnMapa.setText("ver mapa");

                                btnMapa.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View view) {
                                        Intent i = new Intent(getActivity(), MapsActivity.class);
                                        i.putExtra("nombre", nombre);
                                        i.putExtra("latitud", latitud);
                                        i.putExtra("longitud", longitud);
                                        startActivity(i);
                                    }
                                });

                                btnLinkToRegister.setText("Editar");

                            } else {
                                inputNombre.setText("Crear tu tienda!");
                                inputPuesto.setText("");
                                inputTelefono.setText("");
                                inputCategorias.setText("");

                                btnLinkToRegister.setText("Registrar");
                            }
                        }

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (Throwable x){}
                }
            }

            @Override
            public void onFailure(Call<List<Tienda>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), TiendaRegisterActivity.class);
                startActivity(i);
            }
        });

        return view;
    }

    private List<Categoria> categoriaTiendas;

    private void getCategorias() {

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

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (Throwable x){}
                }
            }

            @Override
            public void onFailure(Call<List<Categoria>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


        Call<List<tiendaCategoria>> call2 = service.getTiendaHasCategoria();
        call2.enqueue(new Callback<List<tiendaCategoria>>() {
            @Override
            public void onResponse(Call<List<tiendaCategoria>> call, Response<List<tiendaCategoria>> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {

                        List<tiendaCategoria> tiendaCategorias = response.body();
                        Log.d(TAG, "tiendaCategorias: " + tiendaCategorias);

                        String tienda_id = sharedPreferences.getString("tienda_id", null);
                        Log.d(TAG, "tienda_id: " + tienda_id);

                        List<String> values = new ArrayList<String>();

                        for (tiendaCategoria tiendaCategoria : tiendaCategorias) {
                            if (tiendaCategoria.getTienda_id().equalsIgnoreCase(tienda_id)) {
                                for (Categoria categoria : categoriaTiendas) {
                                    if(tiendaCategoria.getCategoria_tienda_id().equalsIgnoreCase(String.valueOf(categoria.getId()))){
                                        values.add(categoria.getNombre());
                                    }
                                }
                            }
                        }
                        Log.d(TAG, "values: " + values);

                        inputCategorias.setText(values.toString());

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (Throwable x){}
                }
            }

            @Override
            public void onFailure(Call<List<tiendaCategoria>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            Toast.makeText(context, "Store Fragment", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
