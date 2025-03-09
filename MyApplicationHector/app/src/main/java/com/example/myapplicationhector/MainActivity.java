package com.example.myapplicationhector;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplicationhector.network.ApiService;
import com.example.myapplicationhector.network.LoginRequest;
import com.example.myapplicationhector.network.LoginResponse;
import com.example.myapplicationhector.network.RetrofitClient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private AutoCompleteTextView emailEditText;
    private EditText passwordEditText;
    private Switch viewSwitch;
    private ArrayAdapter<String> adapter;
    private String[] emailDomains = {"gmail.com", "ecci.edu.co", "hotmail.com","example.com","yahoo.com"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button loginButton = findViewById(R.id.btnLogin);
        Button registerButton = findViewById(R.id.btnRegistro);
        viewSwitch = findViewById(R.id.switch1);
        emailEditText = findViewById(R.id.textEmail);
        passwordEditText = findViewById(R.id.textPsw);

        // Inicializar el adaptador de autocompletado
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        emailEditText.setAdapter(adapter);

        // Agregar un TextWatcher para controlar el autocompletado
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                int atIndex = text.indexOf("@");

                // Verifica si el "@" está presente y hay algo después
                if (atIndex > 0) {
                    String domainPart = text.substring(atIndex + 1).toLowerCase();
                    List<String> suggestions = new ArrayList<>();

                    // Filtrar las sugerencias de dominio basadas en la parte del dominio escrita por el usuario
                    for (String domain : emailDomains) {
                        if (domain.startsWith(domainPart)) {
                            suggestions.add(text.substring(0, atIndex + 1) + domain);
                        }
                    }

                    // Actualizar el adaptador con las sugerencias filtradas
                    adapter.clear();
                    adapter.addAll(suggestions);
                    adapter.notifyDataSetChanged();

                    // Mostrar las sugerencias si hay alguna disponible
                    if (!suggestions.isEmpty()) {
                        emailEditText.showDropDown();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Configura el manejador de clic para el botón REGISTRARSE
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Configura el switch para mostrar/ocultar la contraseña
        viewSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            passwordEditText.setSelection(passwordEditText.getText().length());
        });

        // Manejar el clic en el botón LOGIN
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (validateInputs(email, password)) {
                performLogin(email, password);
            } else {
                Toast.makeText(MainActivity.this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInputs(String email, String password) {
        // Validar que el email sea un formato válido
        return !email.isEmpty() && !password.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void performLogin(String email, String password) {
        ApiService apiService = RetrofitClient.getApiService();
        LoginRequest loginRequest = new LoginRequest(email, password);
        Call<LoginResponse> call = apiService.loginUser(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse != null && loginResponse.getToken() != null) {
                        String token = loginResponse.getToken();
                        int userId = loginResponse.getUserId();

                        SharedPreferences preferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("TOKEN", token);
                        editor.putInt("USER_ID", userId);
                        editor.apply();

                        Toast.makeText(MainActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        intent.putExtra("TOKEN", token);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Error: El servidor no devolvió un token.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                if (t instanceof IOException) {
                    Toast.makeText(MainActivity.this, "Error de red. Verifica tu conexión a Internet.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Ocurrió un error inesperado: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void handleErrorResponse(Response<LoginResponse> response) {
        switch (response.code()) {
            case 400:
                Toast.makeText(MainActivity.this, "Error 400: Solicitud incorrecta. Verifica tus datos.", Toast.LENGTH_LONG).show();
                break;
            case 401:
                Toast.makeText(MainActivity.this, "Error 401: No autorizado. Credenciales incorrectas.", Toast.LENGTH_LONG).show();
                break;
            case 500:
                Toast.makeText(MainActivity.this, "Error 500: Error interno del servidor. Intenta más tarde.", Toast.LENGTH_LONG).show();
                break;
            default:
                try {
                    String errorBody = response.errorBody().string();
                    Toast.makeText(MainActivity.this, "Error " + response.code() + ": " + errorBody, Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error inesperado al procesar la respuesta del servidor.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
