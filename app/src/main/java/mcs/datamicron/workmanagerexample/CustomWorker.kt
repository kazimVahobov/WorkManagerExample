package mcs.datamicron.workmanagerexample

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.text.format.DateFormat
import android.util.Log
import androidx.work.*
import java.util.*
import java.util.concurrent.TimeUnit

class CustomWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        val sharedPreferences = applicationContext.getSharedPreferences("My App", MODE_PRIVATE)
        var count = sharedPreferences.getInt("count", 0)
        var date = sharedPreferences.getString("date", "")
        count++
        showNotification(count, date!!)

        return Result.success()
    }

    companion object {
        fun periodRequest() {
            val periodicWorkRequest =
                PeriodicWorkRequest.Builder(CustomWorker::class.java, 15, TimeUnit.MINUTES)
                    .addTag("double_b_worker")
                    .build()

            WorkManager.getInstance().enqueueUniquePeriodicWork(
                "double_b_worker",
                ExistingPeriodicWorkPolicy.KEEP,
                periodicWorkRequest
            )
        }
    }

    private fun showNotification(count: Int, date: String) {
        val cal: Calendar = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = System.currentTimeMillis()
        val currentDate: String = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString()

        val resultDate = "$date ! $currentDate"

        val sharedPreferences = applicationContext.getSharedPreferences("My App", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("count", count)
        editor.putString("date", resultDate)
        editor.apply()

        Log.e("##CustomWorker", "I work in background: $count")
        Log.e("##CustomWorker", "I work in background: $date")
    }

}