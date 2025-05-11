package com.example.vizora;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

public class NotificationJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {

        new NotificationHelper(this).send("Ideje bejelenteni a vízóra állását! 💧");


        jobFinished(params, false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {

        return false;
    }
}