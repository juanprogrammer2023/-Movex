package com.example.myapplicationhector;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplicationhector.network.ApiService;
import com.example.myapplicationhector.network.CommentRequest;
import com.example.myapplicationhector.network.Etiqueta;
import com.example.myapplicationhector.network.EtiquetasRequest;
import com.example.myapplicationhector.network.Post;
import com.example.myapplicationhector.network.RetrofitClient;
import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.constraintlayout.helper.widget.Flow;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private List<Post> postList;
    private ApiService apiService;

    public PostAdapter(List<Post> postList) {
        this.postList = postList;
        this.apiService = RetrofitClient.getApiService(); // Inicializa el servicio de Retrofit
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = postList.get(position);

        // Asignar valores a los TextView
        holder.tituloTextView.setText(post.getTitulo());
        holder.contenidoTextView.setText(post.getContenido());
        holder.nombreUsuarioTextView.setText(post.getName() + " " + post.getLastName());
        holder.fechaPublicacionTextView.setText(formatFecha(post.getFechaPublicacion()));

        // Verificar si el usuario tiene una imagen de perfil asignada
        if (post.getProfileImage() != null && !post.getProfileImage().isEmpty()) {
            // Si hay una imagen, cargarla con Glide aplicando la transformación de círculo
            Glide.with(holder.itemView.getContext())
                    .load(post.getProfileImage())
                    .circleCrop()  // Aplica la transformación de círculo
                    .placeholder(R.drawable.iconamoon__profile_circle_fill__1_)
                    .error(R.drawable.close) // Imagen de error si hay problemas al cargar la imagen
                    .into(holder.profileImageView);
        } else {
            // Si no hay imagen de perfil, mostrar directamente una imagen predeterminada en forma de círculo
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.iconamoon__profile_circle_fill__1_)  // Usa la imagen predeterminada
                    .circleCrop()  // Aplica la transformación de círculo
                    .into(holder.profileImageView);
        }

        // Cargar etiquetas, labels, u otros elementos si los tienes
        cargarEtiquetas(holder.spinnerEtiquetas);
        cargarEtiquetasEnLabels(holder.labelsContainer, post.getId());
        loadCommentsForPost(post.getId(), holder.commentsRecyclerView); // Cargar

        holder.btnSendComment.setOnClickListener(v -> {
            String comentario = holder.etComment.getText().toString().trim();
            if (!comentario.isEmpty()) {
                Context context = holder.itemView.getContext();
                SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                int userId = sharedPreferences.getInt("USER_ID", -1);

                if (userId != -1) {
                    enviarComentario(post.getId(), userId, comentario, context, holder.commentsRecyclerView); // Pasa el RecyclerView
                    holder.etComment.setText(""); // Limpiar el campo después de enviar
                } else {
                    Toast.makeText(context, "Error: ID de usuario no encontrado", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(holder.itemView.getContext(), "Escribe un comentario", Toast.LENGTH_SHORT).show();
            }
        });


        // Acción del botón para añadir la etiqueta seleccionada
        holder.btnAddEtiqueta.setOnClickListener(v -> {
            Etiqueta etiquetaSeleccionada = (Etiqueta) holder.spinnerEtiquetas.getSelectedItem();
            if (etiquetaSeleccionada != null) {
                asignarEtiquetaAlPost(post.getId(), etiquetaSeleccionada.getId(), holder.itemView.getContext(), holder.labelsContainer); // Pasa labelsContainer
            } else {
                Toast.makeText(holder.itemView.getContext(), "Seleccione una etiqueta", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    // Método para cargar las etiquetas desde la API y configurar el Spinner
    private void cargarEtiquetas(Spinner spinner) {
        apiService.obtenerEtiquetas().enqueue(new Callback<List<Etiqueta>>() {
            @Override
            public void onResponse(Call<List<Etiqueta>> call, Response<List<Etiqueta>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Etiqueta> etiquetas = response.body();
                    if (etiquetas.isEmpty()) {
                        Toast.makeText(spinner.getContext(), "No se encontraron etiquetas", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ArrayAdapter<Etiqueta> adapter = new ArrayAdapter<>(spinner.getContext(),
                            android.R.layout.simple_spinner_item, etiquetas);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                } else {
                    Toast.makeText(spinner.getContext(), "Error al cargar etiquetas: Respuesta vacía", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Etiqueta>> call, Throwable t) {
                Toast.makeText(spinner.getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enviarComentario(int postId, int userId, String comentario, Context context, RecyclerView commentsRecyclerView) {
        CommentRequest request = new CommentRequest(userId, comentario);

        apiService.enviarComentario(postId, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Comentario enviado exitosamente", Toast.LENGTH_SHORT).show();
                    loadCommentsForPost(postId, commentsRecyclerView); // Recargar los comentarios después de enviar el nuevo
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Toast.makeText(context, "Error al enviar comentario: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Error al procesar el mensaje de error", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    // Método para cargar las etiquetas y generar labels dinámicos en el contenedor
    private void cargarEtiquetasEnLabels(ConstraintLayout labelsContainer, int postId) {
        Flow flow = labelsContainer.findViewById(R.id.flowLabels);
        labelsContainer.removeAllViews();
        labelsContainer.addView(flow);

        apiService.obtenerEtiquetas().enqueue(new Callback<List<Etiqueta>>() {
            @Override
            public void onResponse(Call<List<Etiqueta>> call, Response<List<Etiqueta>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Etiqueta> etiquetas = response.body();
                    if (etiquetas.isEmpty()) {
                        Toast.makeText(labelsContainer.getContext(), "No se encontraron etiquetas para los labels", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    apiService.obtenerEtiquetasDelPost(postId).enqueue(new Callback<List<Etiqueta>>() {
                        @Override
                        public void onResponse(Call<List<Etiqueta>> call, Response<List<Etiqueta>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                List<Etiqueta> etiquetasAsignadas = response.body();
                                List<Integer> ids = new ArrayList<>();

                                for (Etiqueta etiqueta : etiquetas) {
                                    TextView label = new TextView(labelsContainer.getContext());
                                    label.setId(View.generateViewId());
                                    label.setText(etiqueta.getNombre());

                                    if (etiquetasAsignadas.stream().anyMatch(e -> e.getId() == etiqueta.getId())) {
                                        label.setBackgroundResource(R.drawable.label_asignada_background);
                                        label.setTextColor(ContextCompat.getColor(labelsContainer.getContext(), R.color.colorAssignedLabelText));
                                    } else {
                                        label.setBackgroundResource(R.drawable.label_background);
                                        label.setTextColor(ContextCompat.getColor(labelsContainer.getContext(), R.color.colorUnassignedLabelText));
                                    }

                                    label.setPadding(16, 16, 16, 16);
                                    ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                                            ConstraintLayout.LayoutParams.WRAP_CONTENT,
                                            ConstraintLayout.LayoutParams.WRAP_CONTENT
                                    );
                                    params.setMargins(8, 8, 8, 8);
                                    label.setLayoutParams(params);

                                    labelsContainer.addView(label);
                                    ids.add(label.getId());
                                    Log.d("Etiquetas", "Label ID: " + label.getId() + ", Nombre: " + etiqueta.getNombre());
                                }

                                int[] idsArray = ids.stream().mapToInt(i -> i).toArray();
                                flow.setReferencedIds(idsArray);
                            } else {
                                Toast.makeText(labelsContainer.getContext(), "Error al cargar etiquetas asignadas al post", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Etiqueta>> call, Throwable t) {
                            Toast.makeText(labelsContainer.getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(labelsContainer.getContext(), "Error al cargar etiquetas para labels: Respuesta vacía", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Etiqueta>> call, Throwable t) {
                Toast.makeText(labelsContainer.getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para asignar una etiqueta al post
    private void asignarEtiquetaAlPost(int postId, int etiquetaId, Context context, ConstraintLayout labelsContainer) {
        EtiquetasRequest request = new EtiquetasRequest(postId, Collections.singletonList(etiquetaId));
        apiService.asignarEtiquetas(postId, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Etiqueta asignada exitosamente", Toast.LENGTH_SHORT).show();
                    cargarEtiquetasEnLabels(labelsContainer, postId); // Recargar etiquetas en labelsContainer después de asignar
                } else {
                    Toast.makeText(context, "Error al asignar etiqueta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }



    // Clase interna ViewHolder que contiene las referencias de los elementos de vista
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView profileImageView;
        TextView tituloTextView, contenidoTextView, nombreUsuarioTextView, fechaPublicacionTextView;
        Spinner spinnerEtiquetas;
        Button btnAddEtiqueta;
        ConstraintLayout labelsContainer;
        RecyclerView commentsRecyclerView;
        EditText etComment;  // Campo de comentario
        ImageButton btnSendComment;  // Botón de enviar comentario

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            tituloTextView = itemView.findViewById(R.id.tituloTextView);
            contenidoTextView = itemView.findViewById(R.id.contenidoTextView);
            nombreUsuarioTextView = itemView.findViewById(R.id.nombreUsuarioTextView);
            fechaPublicacionTextView = itemView.findViewById(R.id.fechaPublicacionTextView);
            spinnerEtiquetas = itemView.findViewById(R.id.spinnerEtiquetas);
            btnAddEtiqueta = itemView.findViewById(R.id.btnAddEtiqueta);
            labelsContainer = itemView.findViewById(R.id.labelsContainer);
            commentsRecyclerView = itemView.findViewById(R.id.commentsRecyclerView);
            etComment = itemView.findViewById(R.id.etComment);  // Campo de texto para el comentario
            btnSendComment = itemView.findViewById(R.id.btnSendComment);  // Botón para enviar el comentario
        }
    }


    private void loadCommentsForPost(int postId, RecyclerView commentsRecyclerView) {
        apiService.getCommentsForPost(postId).enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Comment> comments = response.body();
                    CommentsAdapter commentAdapter = new CommentsAdapter(commentsRecyclerView.getContext(), comments);
                    commentsRecyclerView.setAdapter(commentAdapter);
                    commentsRecyclerView.setLayoutManager(new LinearLayoutManager(commentsRecyclerView.getContext()));
                } else {
                    Log.e("PostAdapter", "Error al obtener los comentarios");
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Log.e("PostAdapter", "Error de conexión: " + t.getMessage());
            }
        });
    }


    // Método para formatear la fecha de publicación
    private String formatFecha(String fechaOriginal) {
        try {
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date fecha = formatoEntrada.parse(fechaOriginal);

            SimpleDateFormat formatoSalida = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return formatoSalida.format(fecha);
        } catch (Exception e) {
            e.printStackTrace();
            return "Fecha no válida";
        }
    }
}
