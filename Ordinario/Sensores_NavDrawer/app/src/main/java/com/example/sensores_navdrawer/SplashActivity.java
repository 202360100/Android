package com.example.sensores_navdrawer;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.Animator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

public class SplashActivity extends AppCompatActivity {

    private Handler handler = new Handler(Looper.getMainLooper());

    private ImageView imgIcono;
    private TextView txtTitulo;
    private TextView txtSubtitulo;

    private final Runnable abrirInicio = () -> {

        if (!isFinishing() && !isDestroyed()) {

            android.content.Intent intent =
                    new android.content.Intent(
                            SplashActivity.this,
                            MainActivity.class
                    );

            startActivity(intent);

            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Instalar SplashScreen antes de super.onCreate()
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        // Colores de las barras del sistema
        getWindow().setStatusBarColor(Color.rgb(13, 71, 161));
        getWindow().setNavigationBarColor(Color.rgb(13, 71, 161));

        // Inicializar componentes
        imgIcono = findViewById(R.id.img_splash_icon);

        txtTitulo = findViewById(R.id.txt_splash_titulo);

        txtSubtitulo = findViewById(R.id.txt_splash_subtitulo);

        // Preparar animaciones
        prepararAnimaciones();

        // Abrir MainActivity después de 3 segundos
        handler.postDelayed(abrirInicio, 3000);
    }

    private void prepararAnimaciones() {

        // Estado inicial del icono
        imgIcono.setAlpha(0f);
        imgIcono.setScaleX(0.3f);
        imgIcono.setScaleY(0.3f);

        // Estado inicial de los textos
        txtTitulo.setAlpha(0f);
        txtSubtitulo.setAlpha(0f);

        txtTitulo.setTranslationY(30f);
        txtSubtitulo.setTranslationY(30f);

        // Animación del icono
        ObjectAnimator iconoAlpha =
                ObjectAnimator.ofFloat(
                        imgIcono,
                        View.ALPHA,
                        0f,
                        1f
                );

        ObjectAnimator iconoEscalaX =
                ObjectAnimator.ofFloat(
                        imgIcono,
                        View.SCALE_X,
                        0.3f,
                        1f
                );

        ObjectAnimator iconoEscalaY =
                ObjectAnimator.ofFloat(
                        imgIcono,
                        View.SCALE_Y,
                        0.3f,
                        1f
                );

        // Animación del título
        ObjectAnimator tituloAlpha =
                ObjectAnimator.ofFloat(
                        txtTitulo,
                        View.ALPHA,
                        0f,
                        1f
                );

        ObjectAnimator tituloMovimiento =
                ObjectAnimator.ofFloat(
                        txtTitulo,
                        View.TRANSLATION_Y,
                        30f,
                        0f
                );

        // Animación del subtítulo
        ObjectAnimator subtituloAlpha =
                ObjectAnimator.ofFloat(
                        txtSubtitulo,
                        View.ALPHA,
                        0f,
                        1f
                );

        ObjectAnimator subtituloMovimiento =
                ObjectAnimator.ofFloat(
                        txtSubtitulo,
                        View.TRANSLATION_Y,
                        30f,
                        0f
                );

        // Agrupar animaciones
        AnimatorSet animacionIcono = new AnimatorSet();

        animacionIcono.playTogether(
                iconoAlpha,
                iconoEscalaX,
                iconoEscalaY
        );

        animacionIcono.setDuration(1000);

        animacionIcono.setInterpolator(
                new OvershootInterpolator()
        );

        AnimatorSet animacionTitulo = new AnimatorSet();

        animacionTitulo.playTogether(
                tituloAlpha,
                tituloMovimiento
        );

        animacionTitulo.setDuration(800);

        animacionTitulo.setStartDelay(300);

        animacionTitulo.setInterpolator(
                new AccelerateDecelerateInterpolator()
        );

        AnimatorSet animacionSubtitulo = new AnimatorSet();

        animacionSubtitulo.playTogether(
                subtituloAlpha,
                subtituloMovimiento
        );

        animacionSubtitulo.setDuration(800);

        animacionSubtitulo.setStartDelay(600);

        animacionSubtitulo.setInterpolator(
                new AccelerateDecelerateInterpolator()
        );

        // Ejecutar todas las animaciones
        AnimatorSet animacionGeneral = new AnimatorSet();

        animacionGeneral.playTogether(
                animacionIcono,
                animacionTitulo,
                animacionSubtitulo
        );

        animacionGeneral.start();
    }

    @Override
    protected void onDestroy() {

        handler.removeCallbacks(abrirInicio);

        super.onDestroy();
    }
}