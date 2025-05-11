package com.example.vizora;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.os.Build;
import android.Manifest;
import android.widget.Toast;
import android.os.Environment;
import android.net.Uri;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class TakePhotoActivity extends AppCompatActivity {
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    int tag = 1;
    ImageView imageView;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 1001;

    private static final int EXTERNAL_STORAGE_PERMISSION_CODE = 1;
    private static final int MANAGE_EXTERNAL_STORAGE_PERMISSION_CODE = 11;

    private Bitmap currentPhoto; // A fénykép tárolása

    boolean isRunning = false;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);

        imageView = findViewById(R.id.imageView3); // Hozzáadod az ImageView referencia létrehozását

        // Kamera permission ellenőrzés
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE);
        }

        // Ha van már elmentett fénykép, megjelenítjük
        if (currentPhoto != null) {
            imageView.setImageBitmap(currentPhoto);
        }
    }

    public void openCam(View view) {
        // Ha van engedély a kamerához
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            currentPhoto = (Bitmap) extras.get("data");  // Elmentjük a fényképet
            imageView.setImageBitmap(currentPhoto); // A képet beállítjuk az ImageView-ba
            saveImageToExternalStorage(currentPhoto);
        }
    }

    /*
    *    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
        android:maxSdkVersion="32" />
    <uses-permission android:name="android.permission.CAMERA"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="28"/>
    *   <uses-feature
        android:name="android.hardware.camera"
        android:required="false" />
        ezek kellenek a kamera és a kép tároláshoz
    *
    * */


    @Override
    protected void onResume() {
        super.onResume();

        // Ha van mentett kép, újratöltjük
        if (currentPhoto != null) {
            imageView.setImageBitmap(currentPhoto);
        }
    }
    private void saveImageToExternalStorage(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 (API 29) és újabb verziók esetén
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, "saved_image.jpg");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Vizora");

            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            if (uri != null) {
                try (OutputStream out = getContentResolver().openOutputStream(uri)) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);  // Kép kompresszálása és írása
                    Toast.makeText(this, "Kép sikeresen mentve a galériába!", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Hiba történt a kép mentésekor.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Hiba történt a kép mentésekor.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Android 9 (API 28) és alatti verziók esetén
            saveImageToExternalStorageLogic(bitmap);
        }
    }


    // A tényleges fájl mentés logikája
    private void saveImageToExternalStorageLogic(Bitmap bitmap) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            FileOutputStream fos = null;
            try {
                File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Vizora");
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File file = new File(dir, "saved_image.jpg");
                fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();

                // Értesítés a médiatároló rendszernek, hogy új fájl jött létre
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

                Toast.makeText(this, "Kép sikeresen mentve a galériába!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Hiba történt a kép mentésekor.", Toast.LENGTH_SHORT).show();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            Toast.makeText(this, "Nincs elérhető külső tároló!", Toast.LENGTH_SHORT).show();
        }
    }

    // Engedély kérés eredményének kezelése
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Kamera engedély
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCam(null); // Kamera elindítása
            } else {
                Toast.makeText(this, "Kameraengedély szükséges a fényképezéshez!", Toast.LENGTH_LONG).show();
            }
        }

        // Külső tároló engedély
        if (requestCode == EXTERNAL_STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveImageToExternalStorageLogic(currentPhoto); // Kép mentése
            } else {
                Toast.makeText(this, "Külső tároló engedély szükséges a kép mentéséhez!", Toast.LENGTH_SHORT).show();
            }
        }

        // Fájlkezelői engedély (Android 11 és újabb)
        if (requestCode == MANAGE_EXTERNAL_STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveImageToExternalStorageLogic(currentPhoto); // Kép mentése
            } else {
                Toast.makeText(this, "A fájlkezelő engedély nem lett megadva!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}