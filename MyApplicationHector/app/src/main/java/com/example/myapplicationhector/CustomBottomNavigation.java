package com.example.myapplicationhector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.myapplicationhector.network.ApiService;
import com.example.myapplicationhector.network.RetrofitClient;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomBottomNavigation extends LinearLayout {

    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String PREF_KEY_USER_ID = "USER_ID";
    private static final String ACTION_PROFILE_IMAGE_UPDATED = "com.example.myapplicationhector.PROFILE_IMAGE_UPDATED";

    private ImageView profileIcon;
    private BroadcastReceiver profileImageReceiver;

    public CustomBottomNavigation(Context context) {
        super(context);
        init(context);
    }

    public CustomBottomNavigation(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomBottomNavigation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.bottom_navigation, this, true);

        profileIcon = findViewById(R.id.profile);
        registerProfileImageReceiver(context);

        ImageView homeIcon = findViewById(R.id.homeIcon);
        ImageView searchIcon = findViewById(R.id.searchIcon);
        ImageView addIcon = findViewById(R.id.addIcon);
        ImageView notificationIcon = findViewById(R.id.notificationIcon);

        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int userId = preferences.getInt(PREF_KEY_USER_ID, -1);

        if (userId != -1) {
            fetchProfileImage(context, userId);
        }

        homeIcon.setOnClickListener(v -> {
            showToast(context, "Home");
            Intent intent = new Intent(context, HomeActivity.class);
            context.startActivity(intent);
        });

        searchIcon.setOnClickListener(v -> showToast(context, "Search"));
        addIcon.setOnClickListener(v -> showToast(context, "Add"));
        notificationIcon.setOnClickListener(v -> showToast(context, "Notifications"));

        profileIcon.setOnClickListener(v -> {
            showToast(context, "Profile");
            Intent intent = new Intent(context, ProfileActivity.class);
            context.startActivity(intent);
        });
    }

    private void registerProfileImageReceiver(Context context) {
        profileImageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String imageUrl = intent.getStringExtra("imageUrl");
                loadProfileImage(context, imageUrl);
            }
        };
        IntentFilter filter = new IntentFilter(ACTION_PROFILE_IMAGE_UPDATED);
        context.registerReceiver(profileImageReceiver, filter);
    }

    private void fetchProfileImage(Context context, int userId) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<JsonObject> call = apiService.obtenerImagenPerfil(userId);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String profileImageUrl = response.body().has("profileImageUrl") && !response.body().get("profileImageUrl").isJsonNull()
                            ? response.body().get("profileImageUrl").getAsString()
                            : null;
                    loadProfileImage(context, profileImageUrl);
                } else {
                    loadProfileImage(context, null);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                loadProfileImage(context, null);
            }
        });
    }

    private void loadProfileImage(Context context, String profileImageUrl) {
        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            Glide.with(context)
                    .load(profileImageUrl)
                    .override(90, 90)
                    .placeholder(R.drawable.iconamoon__profile_circle_fill__1_)
                    .error(R.drawable.iconamoon__profile_circle_fill__1_)
                    .circleCrop()
                    .into(profileIcon);
        } else {
            Glide.with(context)
                    .load(R.drawable.iconamoon__profile_circle_fill__1_)
                    .override(90, 90)
                    .circleCrop()
                    .into(profileIcon);
        }
    }

    private void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().unregisterReceiver(profileImageReceiver);
    }
}
