package com.example.myapplicationhector;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplicationhector.network.ApiService;
import com.example.myapplicationhector.network.RetrofitClient;
import com.example.myapplicationhector.network.Post;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String PREF_KEY_TOKEN = "TOKEN";
    private static final String PREF_KEY_USER_ID = "USER_ID";
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homeactivity);


        ImageView addPostButton = findViewById(R.id.SubirPost);
        recyclerView = findViewById(R.id.recyclerView);

        // Configurar el RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Recuperar el token y el ID del usuario de SharedPreferences
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String token = preferences.getString(PREF_KEY_TOKEN, "No Token");
        int userId = preferences.getInt(PREF_KEY_USER_ID, -1);

        // Muestra el token o cualquier otra información relevante

        // Cargar los posts desde la API
        cargarPosts();

        // Configura el botón de cerrar sesión

        // Configurar el botón para agregar un post
//        addPostButton.setOnClickListener(v -> showAddPostDialog(userId));
        addPostButton.setOnClickListener(v -> {
            AddPostDialogFragment dialogFragment = AddPostDialogFragment.newInstance(userId);
            dialogFragment.show(getSupportFragmentManager(), "AddPostDialog");
        });
    }

    public void cargarPosts() {
        ApiService apiService = RetrofitClient.getApiService();

        Call<List<Post>> call = apiService.obtenerPosts();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Post> postList = response.body();
                    postAdapter = new PostAdapter(postList);
                    recyclerView.setAdapter(postAdapter);
                } else {
                    Toast.makeText(HomeActivity.this, "Error al obtener los posts", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Log.e("HomeActivity", "Error al obtener los posts: " + t.getMessage());
                Toast.makeText(HomeActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddPostDialog(int userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Agregar Nuevo Post");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText inputTitle = new EditText(this);
        inputTitle.setHint("Título");
        layout.addView(inputTitle);

        final EditText inputContent = new EditText(this);
        inputContent.setHint("Contenido");
        inputContent.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        layout.addView(inputContent);

        builder.setView(layout);

        builder.setPositiveButton("Publicar", (dialog, which) -> {
            String title = inputTitle.getText().toString();
            String content = inputContent.getText().toString();

            // Validar si el título y contenido no están vacíos
            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(HomeActivity.this, "El título y el contenido no pueden estar vacíos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Llamar a la API para crear el post
            ApiService apiService = RetrofitClient.getApiService();
            Post newPost = new Post(title, content, userId);  // Ajusta el constructor de Post según tu modelo

            Call<Void> call = apiService.crearPost(newPost);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        // Mostrar mensaje de éxito
                        Toast.makeText(HomeActivity.this, "Post creado exitosamente", Toast.LENGTH_SHORT).show();

                        // Recargar los posts para mostrar el nuevo
                        cargarPosts();
                    } else {
                        Toast.makeText(HomeActivity.this, "Error al crear el post", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("HomeActivity", "Error al crear el post: " + t.getMessage());
                    Toast.makeText(HomeActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }



}
