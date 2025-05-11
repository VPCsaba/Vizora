package com.example.vizora;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.EventListener;

import java.util.ArrayList;
import java.util.List;


public class ShowLogsActivity extends AppCompatActivity implements WaterClockLogAdapter.OnItemClickListener {

    private static final String TAG = "ShowLogsActivity";

    private RecyclerView recyclerView;
    private WaterClockLogAdapter adapter;

    private FirebaseFirestore db; // Firestore példány
    private Query waterClockLogsQuery;
    private ListenerRegistration snapshotListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_logs); // A fő layout RecyclerView-val
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Firestore példány lekérése
        db = FirebaseFirestore.getInstance();


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        adapter = new WaterClockLogAdapter();


        adapter.setOnItemClickListener(this);

        // Adapter hozzárendelése a RecyclerView-hoz
        recyclerView.setAdapter(adapter);


        waterClockLogsQuery = db.collection("waterClockLogs")
                .orderBy("email", Query.Direction.ASCENDING); // Példa rendezésre email szerint


        snapshotListener = waterClockLogsQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot value, FirebaseFirestoreException e) {
                // Ez a callback minden alkalommal lefut, amikor az adat változik a Firestore-ban
                // (beleértve az első betöltést is)

                if (e != null) {
                    // Hiba történt a figyelés során
                    Log.w(TAG, "Listen failed.", e);
                    Toast.makeText(ShowLogsActivity.this, "Hiba a Firestore adatok betöltésekor!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Nincs hiba, feldolgozzuk az adatokat a QuerySnapshot-ból
                List<WaterClockLogItem> logsList = new ArrayList<>();
                if (value != null) {

                    for (DocumentSnapshot doc : value) {

                        WaterClockLogItem item = doc.toObject(WaterClockLogItem.class);
                        if (item != null) {

                            item.setId(doc.getId());

                            logsList.add(item);
                        }
                    }
                }

                // Frissítjük az Adapter adatlistáját az új adatokkal
                adapter.setData(logsList);

                Log.d(TAG, "Firestore data updated. Item count: " + logsList.size());
                findViewById(R.id.button2).setOnClickListener(v -> {
                    // Összes adat, ahol az email nem üres
                    updateQuery(db.collection("waterClockLogs")
                            .whereGreaterThan("email", "")
                            .orderBy("email", Query.Direction.ASCENDING));
                });

                findViewById(R.id.button3).setOnClickListener(v -> {
                    // Top 5 legnagyobb víz
                    updateQuery(db.collection("waterClockLogs")
                            .orderBy("waterCubic", Query.Direction.DESCENDING)
                            .limit(5));
                });

                findViewById(R.id.button4).setOnClickListener(v -> {
                    // Top 5 legkisebb víz
                    updateQuery(db.collection("waterClockLogs")
                            .orderBy("waterCubic", Query.Direction.ASCENDING)
                            .limit(5));
                });
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (snapshotListener != null) {
            snapshotListener.remove(); // Eltávolítjuk a figyelőt
            Log.d(TAG, "Firestore snapshot listener removed.");
        }
    }

    private void updateQuery(Query newQuery) {
        if (snapshotListener != null) {
            snapshotListener.remove(); // előző figyelő leállítása
        }

        snapshotListener = newQuery.addSnapshotListener((value, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                Toast.makeText(this, "Lekérdezés hiba!", Toast.LENGTH_SHORT).show();
                return;
            }

            List<WaterClockLogItem> logsList = new ArrayList<>();
            if (value != null) {
                for (DocumentSnapshot doc : value) {
                    WaterClockLogItem item = doc.toObject(WaterClockLogItem.class);
                    if (item != null) {
                        item.setId(doc.getId());
                        logsList.add(item);
                    }
                }
            }

            adapter.setData(logsList);
            Log.d(TAG, "Lekérdezés frissítve. Elemek: " + logsList.size());
        });
    }


    @Override
    public void onDeleteClick(WaterClockLogItem item) {
        if (item == null || item.getId() == null) {
            Log.w(TAG, "Attempted to delete null item or item with null ID.");
            Toast.makeText(this, "Hiba: Nem törölhető elem!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Itt történik a TÖRLÉSI LOGIKA a CORE SDK-val!
        String documentId = item.getId(); // Lekérjük a dokumentum ID-t az objektumból
        Log.d(TAG, "Attempting to delete document with ID: " + documentId);

        db.collection("waterClockLogs").document(documentId)
                .delete() // A törlés metódus
                .addOnSuccessListener(aVoid -> {
                    // Sikeres törlés
                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    Toast.makeText(this, "Bejegyzés törölve!", Toast.LENGTH_SHORT).show();
                    // Mivel figyelőt használunk, a törlés után a figyelő kap egy frissítést,
                    // ami automatikusan frissíti az Adaptert és a listát!
                })
                .addOnFailureListener(e -> {
                    // Hiba a törlés során
                    Log.w(TAG, "Error deleting document", e);
                    Toast.makeText(this, "Hiba történt a törlés során!", Toast.LENGTH_SHORT).show();
                });
    }

    // Ezt hívja meg az Adapter, amikor az EGÉSZ listaelemre kattintanak
    @Override
    public void onItemClick(WaterClockLogItem item) {
        if (item == null || item.getId() == null) {
            Log.w(TAG, "Attempted to click on null item or item with null ID.");
            return;
        }
        // Ide írhatod a MODOSÍTÁSI vagy RÉSZLETEK MEGJELENÍTÉSE LOGIKÁT
        Log.d(TAG, "Item clicked with ID: " + item.getId());


        Intent modifyIntent = new Intent(this, AddActivity.class);
        modifyIntent.putExtra("DOCUMENT_ID", item.getId());
        modifyIntent.putExtra("EMAIL", item.getEmail());
        modifyIntent.putExtra("CONTRACT_NUMBER", item.getContractNumber());
        modifyIntent.putExtra("WATERCLOCK_ID", item.getWaterclockID());
        modifyIntent.putExtra("WATER_CUBIC", item.getWaterCubic());
        startActivity(modifyIntent);
    }


    @Override
    public void onUpdateClick(WaterClockLogItem item) {
        onItemClick(item);
    }
}
