package com.example.sensores_navdrawer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.os.Bundle;
import android.graphics.Color;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class AcelerometroActivity extends AppCompatActivity
        implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor acelerometro;

    private TextView txtX, txtY, txtZ, txtEstado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acelerometro);

        // Configuración de pantalla Edge-to-Edge
        WindowCompat.setDecorFitsSystemWindows(
                getWindow(), false
        );

        getWindow().setStatusBarColor(Color.TRANSPARENT);

        WindowCompat.getInsetsController(
                getWindow(),
                getWindow().getDecorView()
        ).setAppearanceLightStatusBars(false);

        // Ajustar barra superior
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.header_acelerometro),
                (view, windowInsets) -> {

                    Insets insets = windowInsets.getInsets(
                            WindowInsetsCompat.Type.statusBars()
                    );

                    view.setPadding(0, insets.top, 0, 0);

                    return windowInsets;
                }
        );

        // Inicializar componentes
        txtX = findViewById(R.id.txt_x);
        txtY = findViewById(R.id.txt_y);
        txtZ = findViewById(R.id.txt_z);
        txtEstado = findViewById(R.id.txt_estado);

        ImageButton btnVolver = findViewById(R.id.btn_volver);

        btnVolver.setOnClickListener(v -> finish());

        // Obtener el administrador de sensores
        sensorManager = (SensorManager)
                getSystemService(SENSOR_SERVICE);

        // Obtener el acelerómetro
        acelerometro = sensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER
        );

        // Comprobar disponibilidad
        if (acelerometro != null) {

            txtEstado.setText(
                    "Acelerómetro disponible. Esperando lecturas..."
            );

        } else {

            txtEstado.setText(
                    "Este dispositivo no cuenta con acelerómetro."
            );

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (acelerometro != null) {

            sensorManager.registerListener(
                    this,
                    acelerometro,
                    SensorManager.SENSOR_DELAY_UI
            );

        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Liberar el sensor cuando la pantalla no está activa
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            txtX.setText(String.format(
                    Locale.getDefault(), "%.2f m/s²", x
            ));

            txtY.setText(String.format(
                    Locale.getDefault(), "%.2f m/s²", y
            ));

            txtZ.setText(String.format(
                    Locale.getDefault(), "%.2f m/s²", z
            ));

            txtEstado.setText("Sensor funcionando correctamente");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No necesitamos modificar nada aquí.
    }
}