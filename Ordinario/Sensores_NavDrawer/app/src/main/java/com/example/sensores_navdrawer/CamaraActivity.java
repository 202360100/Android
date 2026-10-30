package com.example.sensores_navdrawer;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.common.util.concurrent.ListenableFuture;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CamaraActivity extends AppCompatActivity {

    private PreviewView previewView;
    private MaterialButton btnCapturar;
    private TextView txtEstado;
    private ImageView imgCaptura;

    private ImageCapture imageCapture;
    private ExecutorService cameraExecutor;

    private ActivityResultLauncher<String> permisoCamara;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camara);

        WindowCompat.setDecorFitsSystemWindows(
                getWindow(), false
        );

        getWindow().setStatusBarColor(Color.TRANSPARENT);

        WindowCompat.getInsetsController(
                getWindow(),
                getWindow().getDecorView()
        ).setAppearanceLightStatusBars(false);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.header_camara),
                (view, windowInsets) -> {

                    Insets insets = windowInsets.getInsets(
                            WindowInsetsCompat.Type.statusBars()
                    );

                    view.setPadding(0, insets.top, 0, 0);

                    return windowInsets;
                }
        );

        previewView = findViewById(R.id.preview_camara);
        btnCapturar = findViewById(R.id.btn_capturar);
        txtEstado = findViewById(R.id.txt_estado_camara);
        imgCaptura = findViewById(R.id.img_captura);

        ImageButton btnVolver =
                findViewById(R.id.btn_volver_camara);

        btnVolver.setOnClickListener(v -> finish());

        cameraExecutor = Executors.newSingleThreadExecutor();

        permisoCamara = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                concedido -> {

                    if (concedido) {
                        iniciarCamara();
                    } else {
                        txtEstado.setText(
                                "Se necesita permiso para utilizar la cámara."
                        );
                    }
                }
        );

        btnCapturar.setOnClickListener(v -> tomarFotografia());

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED) {

            iniciarCamara();

        } else {

            permisoCamara.launch(Manifest.permission.CAMERA);

        }
    }

    private void iniciarCamara() {

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {

            try {

                ProcessCameraProvider cameraProvider =
                        cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();

                preview.setSurfaceProvider(
                        previewView.getSurfaceProvider()
                );

                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(
                                ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
                        )
                        .build();

                CameraSelector cameraSelector =
                        CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();

                cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        imageCapture
                );

                btnCapturar.setEnabled(true);

                txtEstado.setText(
                        "Cámara funcionando correctamente."
                );

            } catch (Exception e) {

                txtEstado.setText(
                        "Error al iniciar cámara: " + e.getMessage()
                );

            }

        }, ContextCompat.getMainExecutor(this));
    }

    private void tomarFotografia() {

        if (imageCapture == null) {
            return;
        }

        String nombreArchivo = "SENSOR_" +
                new SimpleDateFormat(
                        "yyyyMMdd_HHmmss",
                        Locale.getDefault()
                ).format(new Date()) + ".jpg";

        ContentValues values = new ContentValues();

        values.put(
                MediaStore.MediaColumns.DISPLAY_NAME,
                nombreArchivo
        );

        values.put(
                MediaStore.MediaColumns.MIME_TYPE,
                "image/jpeg"
        );

        values.put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + "/SensoresAndroid"
        );

        ImageCapture.OutputFileOptions outputOptions =
                new ImageCapture.OutputFileOptions.Builder(
                        getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values
                ).build();

        btnCapturar.setEnabled(false);

        imageCapture.takePicture(
                outputOptions,
                cameraExecutor,
                new ImageCapture.OnImageSavedCallback() {

                    @Override
                    public void onImageSaved(
                            ImageCapture.OutputFileResults result) {

                        Uri uri = result.getSavedUri();

                        runOnUiThread(() -> {

                            if (uri != null) {
                                imgCaptura.setImageURI(uri);
                            }

                            txtEstado.setText(
                                    "Fotografía guardada correctamente."
                            );

                            btnCapturar.setEnabled(true);

                            Toast.makeText(
                                    CamaraActivity.this,
                                    "Fotografía capturada",
                                    Toast.LENGTH_SHORT
                            ).show();
                        });
                    }

                    @Override
                    public void onError(ImageCaptureException exception) {

                        runOnUiThread(() -> {

                            txtEstado.setText(
                                    "Error al tomar fotografía: "
                                            + exception.getMessage()
                            );

                            btnCapturar.setEnabled(true);
                        });
                    }
                }
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }
}