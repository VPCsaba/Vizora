package com.example.vizora;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;



public class AddActivity extends AppCompatActivity {

    private static final String TAG = "AddActivity";

    // UI elemek változói
    private EditText emailEditText;
    private EditText contractNumberEditText; // activity_add.xml-ben editTextText
    private EditText waterClockIdEditText;   // activity_add.xml-ben editTextText2
    private EditText waterCubicEditText;     // activity_add.xml-ben editTextNumber
    private Button addLogButton;

    // Firestore példány
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // EZ LEGYEN LEGFELÜL
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add); // UI-t először töltjük be
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // UI elemek inicializálása CSAK setContentView után
        emailEditText = findViewById(R.id.editTextTextEmailAddress3);
        contractNumberEditText = findViewById(R.id.editTextText);
        waterClockIdEditText = findViewById(R.id.editTextText2);
        waterCubicEditText = findViewById(R.id.editTextNumber);
        addLogButton = findViewById(R.id.addWaterClockLog);

        // Firestore példány
        db = FirebaseFirestore.getInstance();

        // Intent feldolgozása – szerkesztés vagy új hozzáadás
        Intent intent = getIntent();
        String documentId = intent.getStringExtra("DOCUMENT_ID");

        if (documentId != null) {
            // Szerkesztés esetén betöltjük az adatokat
            emailEditText.setText(intent.getStringExtra("EMAIL"));
            contractNumberEditText.setText(intent.getStringExtra("CONTRACT_NUMBER"));
            waterClockIdEditText.setText(intent.getStringExtra("WATERCLOCK_ID"));
            waterCubicEditText.setText(String.valueOf(intent.getIntExtra("WATER_CUBIC", 0)));

            addLogButton.setText("Módosítás mentése");
        }

        // Gomb kattintás esemény
        addLogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWaterClockLog(documentId);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        101);
            }
        }
    }


    private void saveWaterClockLog(String doc) {
        String email = emailEditText.getText().toString().trim();
        String contractNumber = contractNumberEditText.getText().toString().trim();
        String waterclockID = waterClockIdEditText.getText().toString().trim();
        String waterCubicStr = waterCubicEditText.getText().toString().trim();

        if (email.isEmpty() || contractNumber.isEmpty() || waterclockID.isEmpty() || waterCubicStr.isEmpty()) {
            Toast.makeText(this, "Kérlek tölts ki minden mezőt!", Toast.LENGTH_SHORT).show();
            return;
        }

        int waterCubic;
        try {
            waterCubic = Integer.parseInt(waterCubicStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Érvénytelen köbméter érték!", Toast.LENGTH_SHORT).show();
            return;
        }

        WaterClockLogItem logItem = new WaterClockLogItem(doc, email, contractNumber, waterclockID, waterCubic);
        CollectionReference colRef = db.collection("waterClockLogs");

        if (doc != null) {
            // 🔁 LÉTEZŐ DOKUMENTUM FRISSÍTÉSE
            colRef.document(doc)
                    .set(logItem)
                    .addOnSuccessListener(aVoid -> {
                        new NotificationHelper(this).send("Sikeresen frissítette: "+logItem.getEmail());
                        Toast.makeText(this, "Bejegyzés frissítve!", Toast.LENGTH_SHORT).show();
                        finish(); // visszalépés
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Hiba történt a frissítés során!", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Update error", e);
                    });
        } else {
            // ➕ ÚJ DOKUMENTUM LÉTREHOZÁSA
            colRef.add(logItem)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Bejelentés sikeres!", Toast.LENGTH_SHORT).show();
                        finish(); // visszalépés
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Hiba történt a mentés során!", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Add error", e);
                    });
        }
    }
}
