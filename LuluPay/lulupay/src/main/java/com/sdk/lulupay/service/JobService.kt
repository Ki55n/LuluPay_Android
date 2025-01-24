package com.sdk.lulupay.service

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import com.sdk.lulupay.preference.PreferencesHelper

class JobService : JobService() {

    override fun onStartJob(params: JobParameters?): Boolean {
        // Retrieve data from the JobScheduler
        val receivedData = params?.extras?.getString("job_data")
        Log.d("MyJobService", "Job started with data: $receivedData")
        
        jobFinished(params, false)
        return true // Work is still being done in a background thread
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d("MyJobService", "Job stopped before completion")
        return true // Reschedule the job if stopped prematurely
    }
}