package com.example.myapplicationhector;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000; // Duración del splash screen (3 segundos)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        // Encuentra el logo y aplica la animación
        ImageView logo = findViewById(R.id.logo);
        animateLogo(logo);

        // Usar un Handler para mostrar la pantalla de inicio durante unos segundos
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Al terminar el tiempo, redirige a MainActivity
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();  // Finaliza la SplashActivity para que no se vuelva al splash al presionar "atrás"
            }
        }, SPLASH_DURATION);
    }

    private void animateLogo(ImageView logo) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(logo, View.SCALE_X, 0.5f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(logo, View.SCALE_Y, 0.5f, 1f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(logo, View.ALPHA, 0f, 1f);
        ObjectAnimator rotation = ObjectAnimator.ofFloat(logo, View.ROTATION, 0f, 360f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, alpha, rotation);
        animatorSet.setDuration(1500);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.start();
    }
}
