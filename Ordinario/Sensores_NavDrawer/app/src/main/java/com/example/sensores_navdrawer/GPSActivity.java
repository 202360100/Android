package com.example.sensores_navdrawer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class GPSActivity extends AppCompatActivity
        implements LocationListener {

    private LocationManager locationManager;

    private TextView txtLatitud;
    private TextView txtLongitud;
    private TextView txtPrecision;
    private TextView txtProveedor;
    private TextView txtEstado;

    private ActivityResultLauncher<String[]> permisoUbicacion;

    private boolean permisoConcedido = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

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
                findViewById(R.id.header_gps),
                (view, windowInsets) -> {

                    Insets insets = windowInsets.getInsets(
                            WindowInsetsCompat.Type.statusBars()
                    );

                    view.setPadding(0, insets.top, 0, 0);

                    return windowInsets;
                }
        );

        // Inicializar componentes
        txtLatitud = findViewById(R.id.txt_latitud);
        txtLongitud = findViewById(R.id.txt_longitud);
        txtPrecision = findViewById(R.id.txt_precision);
        txtProveedor = findViewById(R.id.txt_proveedor);
        txtEstado = findViewById(R.id.txt_estado_gps);

        ImageButton btnVolver =
                findViewById(R.id.btn_volver_gps);

        MaterialButton btnActualizar =
                findViewById(R.id.btn_actualizar_gps);

        btnVolver.setOnClickListener(v -> finish());

        locationManager = (LocationManager)
                getSystemService(LOCATION_SERVICE);

        // Solicitar permisos
        permisoUbicacion = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                resultados -> {

                    boolean fine = Boolean.TRUE.equals(
                            resultados.get(
                                    Manifest.permission.ACCESS_FINE_LOCATION
                            )
                    );

                    boolean coarse = Boolean.TRUE.equals(
                            resultados.get(
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                    );

                    permisoConcedido = fine || coarse;

                    if (permisoConcedido) {

                        obtenerUbicacion();

                    } else {

                        txtEstado.setText(
                                "Se necesita permiso de ubicación."
                        );

                    }
                }
        );

        btnActualizar.setOnClickListener(v -> solicitarPermiso());

        solicitarPermiso();
    }

    private void solicitarPermiso() {

        boolean fine = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;

        boolean coarse = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;

        permisoConcedido = fine || coarse;

        if (permisoConcedido) {

            obtenerUbicacion();

        } else {

            permisoUbicacion.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });

        }
    }

    private void obtenerUbicacion() {

        if (!permisoConcedido) {
            return;
        }

        try {

            boolean gpsActivo = locationManager.isProviderEnabled(
                    LocationManager.GPS_PROVIDER
            );

            boolean redActiva = locationManager.isProviderEnabled(
                    LocationManager.NETWORK_PROVIDER
            );

            if (!gpsActivo && !redActiva) {

                txtEstado.setText(
                        "Activa los servicios de ubicación del dispositivo."
                );

                return;
            }

            txtEstado.setText(
                    "Buscando ubicación..."
            );

            // Registrar actualizaciones GPS
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && gpsActivo) {

                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        2000,
                        1,
                        this,
                        Looper.getMainLooper()
                );
            }

            // Registrar actualizaciones por red
            if (redActiva) {

                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        2000,
                        1,
                        this,
                        Looper.getMainLooper()
                );
            }

            // Intentar recuperar una ubicación anterior
            Location ultima = null;

            if (gpsActivo &&
                    ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED) {

                ultima = locationManager.getLastKnownLocation(
                        LocationManager.GPS_PROVIDER
                );
            }

            if (ultima == null && redActiva) {

                ultima = locationManager.getLastKnownLocation(
                        LocationManager.NETWORK_PROVIDER
                );
            }

            if (ultima != null) {

                mostrarUbicacion(ultima);

            }

        } catch (SecurityException e) {

            txtEstado.setText(
                    "No se cuenta con permiso de ubicación."
            );

        } catch (Exception e) {

            txtEstado.setText(
                    "Error: " + e.getMessage()
            );

        }
    }

    private void mostrarUbicacion(Location location) {

        txtLatitud.setText(String.format(
                Locale.getDefault(),
                "%.6f°",
                location.getLatitude()
        ));

        txtLongitud.setText(String.format(
                Locale.getDefault(),
                "%.6f°",
                location.getLongitude()
        ));

        txtPrecision.setText(String.format(
                Locale.getDefault(),
                "%.2f metros",
                location.getAccuracy()
        ));

        txtProveedor.setText(
                "Proveedor: " + location.getProvider()
        );

        txtEstado.setText(
                "Ubicación obtenida correctamente."
        );
    }

    @Override
    public void onLocationChanged(Location location) {

        mostrarUbicacion(location);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (permisoConcedido) {
            obtenerUbicacion();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }
}