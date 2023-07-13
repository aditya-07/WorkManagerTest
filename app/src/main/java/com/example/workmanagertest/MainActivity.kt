package com.example.workmanagertest

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import java.time.Duration
import java.util.UUID
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    runAndObserverOneTime()
  }

  fun runAndObserverOneTime() {
    val work = OneTimeWorkRequestBuilder<ProgressWorker>().build()
    WorkManager.getInstance(this)
      .getWorkInfoByIdLiveData(work.id).observe(this) {
        Log.d("WorkerTest-OneTime", it.toString())
      }
    WorkManager.getInstance(this).enqueueUniqueWork("one-time-progress", ExistingWorkPolicy.KEEP, work )
  }

  fun runAndObservePeriodicWork() {
    val work = PeriodicWorkRequestBuilder<ProgressWorker>(Duration.ofMinutes(15)).build()
    WorkManager.getInstance(this)
      .getWorkInfoByIdLiveData(work.id).observe(this) {
        Log.d("WorkerTest-Periodic", it.toString())
      }
    WorkManager.getInstance(this).enqueueUniquePeriodicWork("periodic-time-progress", ExistingPeriodicWorkPolicy.KEEP, work )
  }
}



class ProgressWorker(context: Context, parameters: WorkerParameters) :
  CoroutineWorker(context, parameters) {

  companion object {
    const val Progress = "Progress"
    private const val delayDuration = 10L
  }

  override suspend fun doWork(): Result {
    for (i in 0..100 step 20) {
      delay(delayDuration)
      Log.d("WorkerTest","ProgressWorker posting progress : $i")
      setProgress(workDataOf(Progress to i))
    }

    return Result.success()
  }
}