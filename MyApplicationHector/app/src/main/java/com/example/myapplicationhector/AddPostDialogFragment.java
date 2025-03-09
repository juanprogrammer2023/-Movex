package com.example.myapplicationhector;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.myapplicationhector.network.ApiService;
import com.example.myapplicationhector.network.RetrofitClient;
import com.example.myapplicationhector.network.Post;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPostDialogFragment extends DialogFragment {

    private EditText tituloPublicacion;
    private EditText contenidoPublicacion;
    private Button buttonPublicar;
    private Button buttonCancelar;

    // Clave para pasar argumentos
    private static final String ARG_USER_ID = "userId";

    public static AddPostDialogFragment newInstance(int userId) {
        AddPostDialogFragment fragment = new AddPostDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflar el layout que creaste para el post
        View view = inflater.inflate(R.layout.activity_add_post, container, false);

        // Obtener referencias a los elementos del layout
        tituloPublicacion = view.findViewById(R.id.tituloPublicacion);
        contenidoPublicacion = view.findViewById(R.id.contenidoPublicacion);
        buttonPublicar = view.findViewById(R.id.buttonPublicar);
        buttonCancelar = view.findViewById(R.id.buttonCancel);

        // Obtener el userId de los argumentos
        int userId = getArguments() != null ? getArguments().getInt(ARG_USER_ID) : -1;

        // Configurar el botón "Publicar"
        buttonPublicar.setOnClickListener(v -> {
            String title = tituloPublicacion.getText().toString();
            String content = contenidoPublicacion.getText().toString();

            // Validar si el título y contenido no están vacíos
            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(getActivity(), "El título y el contenido no pueden estar vacíos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Llamar a la API para crear el post
            ApiService apiService = RetrofitClient.getApiService();
            Post newPost = new Post(title, content, userId);

            Call<Void> call = apiService.crearPost(newPost);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        // Mostrar mensaje de éxito
                        Toast.makeText(getActivity(), "Post creado exitosamente", Toast.LENGTH_SHORT).show();
                        dismiss(); // Cierra el diálogo

                        // Recargar los posts en la actividad principal si es necesario
                        if (getActivity() instanceof HomeActivity) {
                            ((HomeActivity) getActivity()).cargarPosts();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error al crear el post", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getActivity(), "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Configurar el botón "Cancelar"
        buttonCancelar.setOnClickListener(v -> dismiss()); // Cierra el diálogo

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            Window window = getDialog().getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.6);
            params.gravity = Gravity.BOTTOM;
            window.setAttributes(params);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}
