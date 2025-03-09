package com.example.myapplicationhector;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.myapplicationhector.network.ApiService;
import com.example.myapplicationhector.network.RetrofitClient;
import com.example.myapplicationhector.network.User;

import org.apache.commons.io.IOUtils;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.InputStream;

public class ProfileActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String PREF_KEY_USER_ID = "USER_ID";
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String ACTION_PROFILE_IMAGE_UPDATED = "com.example.myapplicationhector.PROFILE_IMAGE_UPDATED";

    private TextView userName, userLastName, userEmail, userPhone;
    private ImageView userProfileImage;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userName = findViewById(R.id.userName);
        userLastName = findViewById(R.id.userLastName);
        userEmail = findViewById(R.id.userEmail);
        userPhone = findViewById(R.id.userPhone);
        userProfileImage = findViewById(R.id.userProfileImage);
        Button logoutButton = findViewById(R.id.btnLogout);
        ImageButton editButton = findViewById(R.id.editButton);

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        userId = preferences.getInt(PREF_KEY_USER_ID, -1);

        if (userId != -1) {
            cargarDatosUsuario(userId);
        }

        logoutButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear().apply();

            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            finish();
        });

        editButton.setOnClickListener(v -> showEditDialog());
        userProfileImage.setOnClickListener(v -> selectProfileImage());
    }

    private void selectProfileImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            Glide.with(this).load(imageUri).circleCrop().into(userProfileImage);
            subirImagen(imageUri);
        }
    }

    private void cargarDatosUsuario(int userId) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<User> call = apiService.getUserProfile(userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    userName.setText(user.getName());
                    userLastName.setText(user.getLastName());
                    userEmail.setText(user.getEmail());
                    userPhone.setText(user.getPhone());

                    String profileImageUrl = user.getProfileImage();
                    Glide.with(ProfileActivity.this)
                            .load(profileImageUrl != null ? profileImageUrl : R.drawable.iconamoon__profile_circle_fill__1_)
                            .circleCrop()
                            .into(userProfileImage);
                } else {
                    Toast.makeText(ProfileActivity.this, "Error al obtener los datos del usuario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void subirImagen(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            byte[] imageBytes = IOUtils.toByteArray(inputStream);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageBytes);
            MultipartBody.Part body = MultipartBody.Part.createFormData("profileImage", "profile_image.jpg", requestFile);
            RequestBody userIdPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(userId));

            ApiService apiService = RetrofitClient.getApiService();
            Call<UploadResponse> call = apiService.uploadProfileImage(body, userIdPart);
            call.enqueue(new Callback<UploadResponse>() {
                @Override
                public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String imageUrl = response.body().getImageUrl();
                        Glide.with(ProfileActivity.this).load(imageUrl).circleCrop().into(userProfileImage);
                        Toast.makeText(ProfileActivity.this, "Imagen subida exitosamente", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(ACTION_PROFILE_IMAGE_UPDATED);
                        intent.putExtra("imageUrl", imageUrl);
                        sendBroadcast(intent);
                    } else {
                        Toast.makeText(ProfileActivity.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UploadResponse> call, Throwable t) {
                    Toast.makeText(ProfileActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error al seleccionar la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private void showEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Actualizar Información");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText nameInput = new EditText(this);
        nameInput.setHint("Nombre");
        nameInput.setText(userName.getText().toString());
        layout.addView(nameInput);

        final EditText lastNameInput = new EditText(this);
        lastNameInput.setHint("Apellido");
        lastNameInput.setText(userLastName.getText().toString());
        layout.addView(lastNameInput);

        final EditText emailInput = new EditText(this);
        emailInput.setHint("Correo");
        emailInput.setText(userEmail.getText().toString());
        layout.addView(emailInput);

        final EditText phoneInput = new EditText(this);
        phoneInput.setHint("Teléfono");
        phoneInput.setText(userPhone.getText().toString());
        layout.addView(phoneInput);

        builder.setView(layout);

        builder.setPositiveButton("Actualizar", (dialog, which) -> {
            String newName = nameInput.getText().toString();
            String newLastName = lastNameInput.getText().toString();
            String newEmail = emailInput.getText().toString();
            String newPhone = phoneInput.getText().toString();

            actualizarInformacionUsuario(newName, newLastName, newEmail, newPhone);
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void actualizarInformacionUsuario(String name, String lastName, String email, String phone) {
        ApiService apiService = RetrofitClient.getApiService();
        User user = new User(name, lastName, email, phone, "");

        Call<Void> call = apiService.updateUserProfile(userId, user);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Información actualizada correctamente", Toast.LENGTH_SHORT).show();
                    userName.setText(name);
                    userLastName.setText(lastName);
                    userEmail.setText(email);
                    userPhone.setText(phone);
                } else {
                    Toast.makeText(ProfileActivity.this, "Error al actualizar la información", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
