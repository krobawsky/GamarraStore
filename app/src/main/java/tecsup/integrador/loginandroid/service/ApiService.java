package tecsup.integrador.loginandroid.service;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import tecsup.integrador.loginandroid.models.Categoria;
import tecsup.integrador.loginandroid.models.Producto;
import tecsup.integrador.loginandroid.models.Tienda;
import tecsup.integrador.loginandroid.models.Usuario;
import tecsup.integrador.loginandroid.models.tiendaCategoria;

public interface ApiService {

    String API_BASE_URL = "https://gamarra-rest-krobawsky.c9users.io";

    //Usuarios
    @GET("api/v1/usuarioall")
    Call<List<Usuario>> getUsuarios();

    @FormUrlEncoded
    @POST("/api/v1/usuario")
    Call<ResponseMessage> createUsuario(@Field("nombre") String nombre,
                                        @Field("dni") String dni,
                                        @Field("email") String email,
                                        @Field("password") String password);

    @GET("api/v1/login/user/{email}/pwd/{password}")
    Call<ResponseMessage> loginUsuario(@Path("email") String email,
                                       @Path("password") String password);


    //Tiendas
    @GET("api/v1/tiendas")
    Call<List<Tienda>> getTiendas();

    @FormUrlEncoded
    @POST("/api/v1/tiendas")
    Call<ResponseMessage> createTienda (@Field("nombre") String nombre,
                                        @Field("puesto") String puesto,
                                        @Field("telefono") String telefono,
                                        @Field("latitud") String latitud,
                                        @Field("longitud") String longitud,
                                        @Field("comerciante_id") String comerciante_id);


    //Productos
    @GET("api/v1/productos")
    Call<List<Producto>> getProductos();

    @GET("api/v1/productos/{id}")
    Call<Producto> showProducto(@Path("id") Integer id);

    @Multipart
    @POST("/api/v1/productos")
    Call<ResponseMessage> createProducto(
            @Part("nombre") RequestBody nombre,
            @Part("precio") RequestBody precio,
            @Part("descripcion") RequestBody descripcion,
            @Part("tienda_id") RequestBody tienda_id,
            @Part("categoria_producto_id") RequestBody categoria_producto_id,
            @Part MultipartBody.Part imagen
    );

    @DELETE("/api/v1/productos/{id}")
    Call<ResponseMessage> destroyProducto(@Path("id") Integer id);


    //Categorias
    @GET("api/v1/categorias_producto")
    Call<List<Categoria>> getCategoriaProducto();

    @GET("api/v1/categorias_tienda")
    Call<List<Categoria>> getCategoriaTienda();


    //Tienda has categoria
    @GET("api/v1/tienda_categorias")
    Call<List<tiendaCategoria>> getTiendaHasCategoria();

    @FormUrlEncoded
    @POST("/api/v1/tienda_categorias")
    Call<ResponseMessage> createTiendaHasCategoria(@Field("tienda_id") String tienda_id,
                                                   @Field("categoria_tienda_id") String categoria_tienda_id);

    @DELETE("/api/v1/tienda_categorias/delete/{id}")
    Call<ResponseMessage> destroyTiendaHasCategoria(@Path("id") Integer id);

}
