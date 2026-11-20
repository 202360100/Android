package com.example.riskmex;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.riskmex.model.GameManager;
import com.example.riskmex.model.Player;

public class MainActivity extends AppCompatActivity {

    private GameManager gameManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnNewGame = findViewById(R.id.btnNewGame);
        Button btnRules = findViewById(R.id.btnRules);
        Button btnAbout = findViewById(R.id.btnAbout);

        btnNewGame.setOnClickListener(v -> showPlayerSelection());

        btnRules.setOnClickListener(v -> showRules());

        btnAbout.setOnClickListener(v -> showAbout());
    }



    private void showPlayerSelection() {
        String[] options = {
                "2 JUGADORES",
                "3 JUGADORES",
                "4 JUGADORES"
        };

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        layout.setPadding(padding, padding, padding, padding);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Nueva partida")
                .setMessage("Selecciona la cantidad de jugadores")
                .setView(layout)
                .setNegativeButton("Cancelar", null)
                .create();

        for (int i = 0; i < options.length; i++) {
            final int playerCount = i + 2;

            TextView button = new TextView(this);
            button.setText(options[i]);
            button.setTextColor(0xFFFFFFFF);
            button.setTextSize(16);
            button.setGravity(Gravity.CENTER);
            button.setTypeface(null, android.graphics.Typeface.BOLD);

            android.graphics.drawable.GradientDrawable background =
                    new android.graphics.drawable.GradientDrawable();

            background.setColor(0xFF304454);

            float radius = 12 * getResources().getDisplayMetrics().density;
            background.setCornerRadius(radius);

            button.setBackground(background);

            int height = (int) (48 * getResources()
                    .getDisplayMetrics().density);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            height
                    );

            params.setMargins(0, 0, 0, (int) (10 * getResources()
                    .getDisplayMetrics().density));

            layout.addView(button, params);

            button.setOnClickListener(v -> {
                dialog.dismiss();
                createGame(playerCount);
            });
        }

        dialog.show();
    }

    private void createGame(int playerCount) {
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        intent.putExtra("PLAYER_COUNT", playerCount);
        startActivity(intent);
    }


    private void showRules() {

        String rules =
                "OBJETIVO\n" +
                        "Conquistar los 32 estados de México.\n\n" +

                        "UNIDADES\n" +
                        "• Infantería: ataque 0, defensa 0.\n" +
                        "• Caballería: ataque +1, defensa -1.\n" +
                        "• Artillería: ataque +1, defensa +1.\n\n" +

                        "COMBATE\n" +
                        "• Dados de seis caras.\n" +
                        "• Hasta 3 dados de ataque.\n" +
                        "• Hasta 2 dados de defensa.\n" +
                        "• Los empates favorecen al defensor.\n\n" +

                        "TURNOS\n" +
                        "1. Reclutamiento.\n" +
                        "2. Ataque.\n" +
                        "3. Movimiento.\n\n" +

                        "GANA EL JUGADOR QUE CONQUISTE " +
                        "LOS 32 ESTADOS.";

        new AlertDialog.Builder(this)
                .setTitle("Reglas de Risk México")
                .setMessage(rules)
                .setPositiveButton("Cerrar", null)
                .show();
    }

    private void showAbout() {

        new AlertDialog.Builder(this)
                .setTitle("Acerca de Risk México")
                .setMessage(
                        "Risk México es un juego de estrategia " +
                                "por turnos inspirado en los juegos de " +
                                "conquista territorial.\n\n" +
                                "Desarrollado como proyecto académico " +
                                "en Android Studio utilizando Java."
                )
                .setPositiveButton("Cerrar", null)
                .show();
    }
}
