package com.example.clonewhatsapp;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.content.SharedPreferences;

public class CloneWhatsAppApp extends Application {

    private int actividadesActivas = 0;

    private final Handler handler =
            new Handler(Looper.getMainLooper());

    private final Runnable heartbeat = new Runnable() {
        @Override
        public void run() {
            actualizarConexion();
            handler.postDelayed(this, 20000);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        registerActivityLifecycleCallbacks(
                new ActivityLifecycleCallbacks() {

                    @Override
                    public void onActivityStarted(Activity activity) {
                        actividadesActivas++;

                        if (actividadesActivas == 1) {
                            iniciarPresencia();
                        }
                    }

                    @Override
                    public void onActivityStopped(Activity activity) {
                        actividadesActivas--;

                        if (actividadesActivas == 0) {
                            detenerPresencia();
                        }
                    }

                    @Override
                    public void onActivityCreated(
                            Activity activity, Bundle state) {}

                    @Override
                    public void onActivityResumed(Activity activity) {}

                    @Override
                    public void onActivityPaused(Activity activity) {}

                    @Override
                    public void onActivitySaveInstanceState(
                            Activity activity, Bundle state) {}

                    @Override
                    public void onActivityDestroyed(Activity activity) {}
                });
    }

    private void iniciarPresencia() {
        handler.removeCallbacks(heartbeat);
        actualizarConexion();
        handler.postDelayed(heartbeat, 20000);
    }

    private void detenerPresencia() {
        handler.removeCallbacks(heartbeat);
        actualizarConexion();
    }

    private void actualizarConexion() {

        SharedPreferences preferences =
                getSharedPreferences(
                        "supabase_session",
                        MODE_PRIVATE
                );

        String userId = preferences.getString("user_id", null);
        String token = preferences.getString("access_token", null);

        if (userId == null || token == null) return;

        PerfilAPI api = new PerfilAPI();

        api.actualizarUltimaConexion(
                userId,
                token,
                new PerfilAPI.ActualizarCallback() {

                    @Override
                    public void onSuccess() {}

                    @Override
                    public void onError(String mensaje) {
                        android.util.Log.e(
                                "Presencia",
                                mensaje
                        );
                    }
                }
        );
    }
}
