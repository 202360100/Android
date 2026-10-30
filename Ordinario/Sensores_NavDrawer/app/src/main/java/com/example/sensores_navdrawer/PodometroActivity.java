package com.example.sensores_navdrawer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.os.Bundle;
import android.graphics.Color;
import android.widget.ImageButton;
import android.widget.TextView;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class PodometroActivity extends AppCompatActivity
        implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensorPasos;
    private ActivityResultLauncher<String> permisoActividad;

    private TextView txtPasos, txtEstado;

    private int pasosIniciales = -1;
    private int pasosActuales = 0;
    private int pasosBase = 0;

    private void registrarSensor() {

        if (sensorPasos == null) {
            txtEstado.setText("Sensor de pasos no disponible.");
            return;
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED) {

            sensorManager.registerListener(
                    this,
                    sensorPasos,
                    SensorManager.SENSOR_DELAY_NORMAL
            );

        } else {

            permisoActividad.launch(
                    Manifest.permission.ACTIVITY_RECOGNITION
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        permisoActividad = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                concedido -> {

                    if (concedido) {

                        txtEstado.setText(
                                "Permiso concedido. Preparando podómetro..."
                        );

                        registrarSensor();

                    } else {

                        txtEstado.setText(
                                "Se necesita el permiso de actividad física para contar pasos."
                        );

                    }
                }
        );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podometro);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        WindowCompat.getInsetsController(
                getWindow(),
                getWindow().getDecorView()
        ).setAppearanceLightStatusBars(false);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.header_podometro),
                (view, windowInsets) -> {

                    Insets insets = windowInsets.getInsets(
                            WindowInsetsCompat.Type.statusBars()
                    );

                    view.setPadding(0, insets.top, 0, 0);

                    return windowInsets;
                }
        );

        txtPasos = findViewById(R.id.txt_pasos);
        txtEstado = findViewById(R.id.txt_estado_podometro);

        ImageButton btnVolver =
                findViewById(R.id.btn_volver_podometro);

        MaterialButton btnReiniciar =
                findViewById(R.id.btn_reiniciar_pasos);

        btnVolver.setOnClickListener(v -> finish());

        btnReiniciar.setOnClickListener(v -> {
            pasosBase = pasosActuales;
            txtPasos.setText("0");
        });

        sensorManager = (SensorManager)
                getSystemService(SENSOR_SERVICE);

        sensorPasos = sensorManager.getDefaultSensor(
                Sensor.TYPE_STEP_COUNTER
        );

        if (sensorPasos != null) {

            txtEstado.setText(
                    "Sensor de pasos disponible. Esperando movimiento..."
            );

        } else {

            txtEstado.setText(
                    "El dispositivo no dispone de sensor TYPE_STEP_COUNTER."
            );

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sensorPasos != null) {
            registrarSensor();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {

            pasosActuales = (int) event.values[0];

            if (pasosIniciales == -1) {
                pasosIniciales = pasosActuales;
                pasosBase = pasosActuales;
            }

            int pasos = Math.max(
                    0,
                    pasosActuales - pasosBase
            );

            txtPasos.setText(String.valueOf(pasos));

            txtEstado.setText(
                    "Sensor funcionando correctamente"
            );
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}