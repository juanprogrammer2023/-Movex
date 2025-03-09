package com.example.myapplicationhector.network;

import com.example.myapplicationhector.Comment;
import com.example.myapplicationhector.UploadResponse;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    // Ya existente para registrar un usuario
    @POST("/register")
    Call<Void> registerUser(@Body User user);

    // Ya existente para iniciar sesión
    @POST("/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    // Obtener todos los posts
    @GET("/posts/listar") // Cambia la ruta según tu API
    Call<List<Post>> obtenerPosts();

    // Crear un nuevo post
    @POST("/posts/crear") // Cambia la ruta según tu API
    Call<Void> crearPost(@Body Post post);

    // Obtener todas las etiquetas disponibles
    @GET("/api/etiquetas")  // Cambia la ruta según tu API
    Call<List<Etiqueta>> obtenerEtiquetas();

    // NUEVO: Asignar etiquetas a un post
    @POST("/posts/{postId}/etiquetas")
    Call<Void> asignarEtiquetas(@Path("postId") int postId, @Body EtiquetasRequest etiquetasRequest);

    @GET("api/posts/{postId}/etiquetas")
    Call<List<Etiqueta>> obtenerEtiquetasDelPost(@Path("postId") int postId);

    @GET("/comments/{postId}")
    Call<List<Comment>> getCommentsForPost(@Path("postId") int postId);

    @GET("user/{id}")
    Call<User> getUserProfile(@Path("id") int userId);

    @PUT("/user/{id}")
    Call<Void> updateUserProfile(@Path("id") int userId, @Body User user);

    @POST("posts/{postId}/comments")
    Call<Void> enviarComentario(@Path("postId") int postId, @Body CommentRequest request);



    @Multipart
    @POST("/upload") // Cambia la ruta según tu endpoint
    Call<UploadResponse> uploadProfileImage(@Part MultipartBody.Part file, @Part("userId") RequestBody userId);
        @GET("usuario/{id}/imagen_perfil")
        Call<JsonObject> obtenerImagenPerfil(@Path("id") int userId);

}
