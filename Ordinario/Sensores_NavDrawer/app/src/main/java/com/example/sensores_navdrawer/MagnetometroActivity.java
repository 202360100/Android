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

public class MagnetometroActivity extends AppCompatActivity
        implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor magnetometro;

    private TextView txtX;
    private TextView txtY;
    private TextView txtZ;
    private TextView txtIntensidad;
    private TextView txtEstado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magnetometro);

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
                findViewById(R.id.header_magnetometro),
                (view, windowInsets) -> {

                    Insets insets = windowInsets.getInsets(
                            WindowInsetsCompat.Type.statusBars()
                    );

                    view.setPadding(0, insets.top, 0, 0);

                    return windowInsets;
                }
        );

        // Inicializar componentes
        txtX = findViewById(R.id.txt_magnetico_x);
        txtY = findViewById(R.id.txt_magnetico_y);
        txtZ = findViewById(R.id.txt_magnetico_z);

        txtIntensidad = findViewById(R.id.txt_intensidad);

        txtEstado = findViewById(
                R.id.txt_estado_magnetometro
        );

        ImageButton btnVolver =
                findViewById(R.id.btn_volver_magnetometro);

        btnVolver.setOnClickListener(v -> finish());

        // Obtener administrador de sensores
        sensorManager = (SensorManager)
                getSystemService(SENSOR_SERVICE);

        // Obtener magnetómetro
        magnetometro = sensorManager.getDefaultSensor(
                Sensor.TYPE_MAGNETIC_FIELD
        );

        if (magnetometro != null) {

            txtEstado.setText(
                    "Magnetómetro disponible. Esperando lecturas..."
            );

        } else {

            txtEstado.setText(
                    "Este dispositivo no cuenta con magnetómetro."
            );

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (magnetometro != null) {

            sensorManager.registerListener(
                    this,
                    magnetometro,
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

        if (event.sensor.getType() ==
                Sensor.TYPE_MAGNETIC_FIELD) {

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Mostrar valores de los tres ejes
            txtX.setText(String.format(
                    Locale.getDefault(),
                    "%.2f µT",
                    x
            ));

            txtY.setText(String.format(
                    Locale.getDefault(),
                    "%.2f µT",
                    y
            ));

            txtZ.setText(String.format(
                    Locale.getDefault(),
                    "%.2f µT",
                    z
            ));

            // Calcular intensidad magnética total
            double intensidad = Math.sqrt(
                    x * x + y * y + z * z
            );

            txtIntensidad.setText(String.format(
                    Locale.getDefault(),
                    "%.2f µT",
                    intensidad
            ));

            txtEstado.setText(
                    "Magnetómetro funcionando correctamente."
            );
        }
    }

    @Override
    public void onAccuracyChanged(
            Sensor sensor,
            int accuracy
    ) {

        if (accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {

            txtEstado.setText(
                    "Advertencia: lecturas magnéticas poco confiables."
            );

        }
    }
}