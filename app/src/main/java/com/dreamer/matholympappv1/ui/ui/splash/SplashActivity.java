package com.dreamer.matholympappv1.ui.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dreamer.matholympappv1.MainActivity;
import com.dreamer.matholympappv1.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 1500; // 1.5 секунды

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Получаем элементы интерфейса
        ImageView splashLogo = findViewById(R.id.splash_logo);
        TextView splashText = findViewById(R.id.splash_text);

        // Создаём анимацию появления (fade-in)
        Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        fadeIn.setDuration(800);

        // Запускаем анимацию на логотипе
        splashLogo.startAnimation(fadeIn);

        // Небольшая задержка для текста
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            splashText.startAnimation(fadeIn);
        }, 300);

        // Переход в MainActivity после задержки
        // MainActivity сама проверит авторизацию и покажет нужный экран
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Закрываем SplashActivity, чтобы пользователь не мог вернуться
        }, SPLASH_DURATION);
    }
}
