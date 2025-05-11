package com.example.vizora;

import android.app.ActivityOptions;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class menu extends AppCompatActivity {

    private JobScheduler mJobScheduler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mJobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        setJobScheduler();
        Button buttom1  = findViewById(R.id.uploadbutton);
        Button buttom2  = findViewById(R.id.CamButton);
        Button buttom3  = findViewById(R.id.previousuploadbutton2);

        Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        AnimationSet set = new AnimationSet(true);
        set.addAnimation(slideIn);
        set.addAnimation(fadeIn);

// Animáció indítása
        buttom1.startAnimation(set);
        buttom2.startAnimation(set);
        buttom3.startAnimation(set);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        101);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            // Ellenőrizzük, hogy a felhasználó megadta-e az engedélyt
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Az engedély megadva, értesítéseket küldhetünk
                Toast.makeText(this, "Permission granted for notifications", Toast.LENGTH_SHORT).show();
            } else {
                // Az engedély megtagadva
                Toast.makeText(this, "Permission denied for notifications", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onRestart(){
        super.onRestart();
        Toast.makeText(this, "Ne felejtsd el beküldeni a vízóraállást!", Toast.LENGTH_SHORT).show();
    }
    public void addWaterClock(View view){
        Intent intent = new Intent(this, AddActivity.class);
        startActivity(intent,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }
    public void showWaterClock(View view){
        Intent intent = new Intent(this, ShowLogsActivity.class);
        startActivity(intent,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }
    public void takePhotoClick(View view){
        Intent intent = new Intent(this, TakePhotoActivity.class);
        startActivity(intent,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }
    /*
    *
    * <service
            android:name=".NotificationJobService"
            android:permission="android.permission.BIND_JOB_SERVICE" />
    * <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    *
    * notikhoz kellenek és mehetnek az AndroidManifest.xml-be
    *
    * bejelentkezés után, elfogadod és a menube vagy tehát küldi percenként, a másik notit meg módosításkor, az is elég ha csak a kártyára nyomsz, de van gomb is
    *
    * */
    private void setJobScheduler() {
        // SeekBar, Switch, RadioButton
        int networkType = JobInfo.NETWORK_TYPE_UNMETERED;
        Boolean isDeviceCharging = true;
        int hardDeadline = 60000; // 5 * 1000 ms = 5 sec.

        ComponentName serviceName = new ComponentName(getPackageName(), NotificationJobService.class.getName());
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceName)
                .setRequiredNetworkType(networkType)
                .setRequiresCharging(isDeviceCharging)
                .setOverrideDeadline(hardDeadline);

        JobInfo jobInfo = builder.build();
        mJobScheduler.schedule(jobInfo);

    }
}