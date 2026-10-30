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

public class GiroscopioActivity extends AppCompatActivity
        implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor giroscopio;

    private TextView txtX, txtY, txtZ, txtEstado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giroscopio);

        // Configuración Edge-to-Edge
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
                findViewById(R.id.header_giroscopio),
                (view, windowInsets) -> {

                    Insets insets = windowInsets.getInsets(
                            WindowInsetsCompat.Type.statusBars()
                    );

                    view.setPadding(0, insets.top, 0, 0);

                    return windowInsets;
                }
        );

        // Inicializar componentes
        txtX = findViewById(R.id.txt_giro_x);
        txtY = findViewById(R.id.txt_giro_y);
        txtZ = findViewById(R.id.txt_giro_z);
        txtEstado = findViewById(R.id.txt_estado_giroscopio);

        ImageButton btnVolver =
                findViewById(R.id.btn_volver_giroscopio);

        btnVolver.setOnClickListener(v -> finish());

        // Obtener administrador de sensores
        sensorManager = (SensorManager)
                getSystemService(SENSOR_SERVICE);

        // Obtener giroscopio
        giroscopio = sensorManager.getDefaultSensor(
                Sensor.TYPE_GYROSCOPE
        );

        if (giroscopio != null) {

            txtEstado.setText(
                    "Giroscopio disponible. Esperando movimientos..."
            );

        } else {

            txtEstado.setText(
                    "Este dispositivo no cuenta con giroscopio."
            );

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (giroscopio != null) {

            sensorManager.registerListener(
                    this,
                    giroscopio,
                    SensorManager.SENSOR_DELAY_UI
            );

        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            txtX.setText(String.format(
                    Locale.getDefault(), "%.3f rad/s", x
            ));

            txtY.setText(String.format(
                    Locale.getDefault(), "%.3f rad/s", y
            ));

            txtZ.setText(String.format(
                    Locale.getDefault(), "%.3f rad/s", z
            ));

            txtEstado.setText(
                    "Giroscopio funcionando correctamente"
            );
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}