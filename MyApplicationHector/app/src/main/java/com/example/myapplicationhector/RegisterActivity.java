package com.example.myapplicationhector;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplicationhector.network.User;
import com.example.myapplicationhector.network.ApiService;
import com.example.myapplicationhector.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText inputName;
    private EditText inputLastName;
    private EditText inputEmail;
    private EditText inputPhone;
    private EditText inputPsw;
    private EditText inputConfirmPsw;
    private Button btnRegister;
    private ImageView btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputName = findViewById(R.id.inputName);
        inputLastName = findViewById(R.id.inputLastName);
        inputEmail = findViewById(R.id.inputEmail);
        inputPhone = findViewById(R.id.inputPhone);
        inputPsw = findViewById(R.id.inputPsw);
        inputConfirmPsw = findViewById(R.id.inputconfirmpsw);
        btnRegister = findViewById(R.id.btnRegistrar);
        btnVolver = findViewById(R.id.btnVolver); // Asegúrate de que este ID es correcto

        btnRegister.setOnClickListener(v -> {
            String name = inputName.getText().toString();
            String lastName = inputLastName.getText().toString();
            String email = inputEmail.getText().toString();
            String phone = inputPhone.getText().toString();
            String password = inputPsw.getText().toString();
            String confirmPassword = inputConfirmPsw.getText().toString();

            if (validateInputs(name, lastName, email, phone, password, confirmPassword)) {
                User user = new User(name, lastName, email, phone, password);
                sendUserData(user);
            }
        });

        btnVolver.setOnClickListener(v -> finish()); // Finaliza la actividad y regresa a la anterior
    }

    private boolean validateInputs(String name, String lastName, String email, String phone, String password, String confirmPassword) {
        return !name.isEmpty() && !lastName.isEmpty() && !email.isEmpty() && !phone.isEmpty() && !password.isEmpty() && password.equals(confirmPassword);
    }

    private void sendUserData(User user) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<Void> call = apiService.registerUser(user);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    showToast("User registered successfully");
                    finish(); // Finaliza la actividad y regresa a la anterior
                } else {
                    showToast("Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showToast("Failure: " + t.getMessage());
                System.out.println(t.getMessage());
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}

