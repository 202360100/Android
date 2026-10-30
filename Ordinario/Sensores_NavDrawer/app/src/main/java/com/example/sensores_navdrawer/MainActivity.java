package com.example.sensores_navdrawer;

import android.content.Intent;
import android.os.Bundle;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    TextView txtTitulo;
    NavigationView navigationView;
    MaterialToolbar toolbar;
    android.widget.ImageButton btnMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Activar Edge-to-Edge
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // Barra de estado transparente
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        // Iconos blancos en la barra de estado
        WindowCompat.getInsetsController(
                getWindow(),
                getWindow().getDecorView()
        ).setAppearanceLightStatusBars(false);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        btnMenu = findViewById(R.id.btn_menu);

        TextView txtTitulo = findViewById(R.id.txt_titulo);
        View header = findViewById(R.id.header_container);

        ViewCompat.setOnApplyWindowInsetsListener(header, (view, windowInsets) -> {

            Insets insets = windowInsets.getInsets(
                    WindowInsetsCompat.Type.statusBars()
            );

            view.setPadding(
                    0,
                    insets.top,
                    0,
                    0
            );

            return windowInsets;
        });

        btnMenu.setOnClickListener(v -> {

            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {

                drawerLayout.closeDrawer(GravityCompat.START);

            } else {

                drawerLayout.openDrawer(GravityCompat.START);

            }

        });


        // Eventos de las opciones
        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_inicio) {

                txtTitulo.setText("Inicio");

            } else if (id == R.id.nav_acelerometro) {

                abrirActivity(AcelerometroActivity.class);

            } else if (id == R.id.nav_podometro) {

                abrirActivity(PodometroActivity.class);

            } else if (id == R.id.nav_giroscopio) {

                abrirActivity(GiroscopioActivity.class);

            } else if (id == R.id.nav_camara) {

                abrirActivity(CamaraActivity.class);

            } else if (id == R.id.nav_gps) {

                abrirActivity(GPSActivity.class);

            } else if (id == R.id.nav_magnetometro) {

                abrirActivity(MagnetometroActivity.class);

            }

            drawerLayout.closeDrawer(GravityCompat.START);

            return true;
        });


    }

    private void abrirActivity(Class<?> actividad) {

        Intent intent = new Intent(MainActivity.this, actividad);
        startActivity(intent);

    }
}