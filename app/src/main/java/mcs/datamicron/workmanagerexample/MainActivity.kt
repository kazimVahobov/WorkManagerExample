package mcs.datamicron.workmanagerexample

import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.ExecutionException


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val cal: Calendar = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = System.currentTimeMillis()
        val startDate: String = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString()

        val sharedPreferences = applicationContext.getSharedPreferences(
            "My App",
            Context.MODE_PRIVATE
        )
        val savedDate = sharedPreferences.getString("startDate", null)
        if (savedDate == null) {
            Log.e("##MainActivity", "Start time: $startDate")
            val editor = sharedPreferences.edit()
            editor.putString("startDate", startDate)
            editor.apply()
            startDateTV.text = "Start - $startDate"
        } else {
            startDateTV.text = "Saved start - $savedDate"
            Log.e("##MainActivity", "Saved time: $savedDate")
        }
        val status = isWorkScheduled("double_b_worker")
        workStatus.text = "Status $status"
        Log.e("##MainActivity", "work is: $status")

        CustomWorker.periodRequest()
        update.setOnClickListener { readFromShared() }
    }

    private fun readFromShared() {
        val sharedPreferences = applicationContext.getSharedPreferences(
            "My App",
            Context.MODE_PRIVATE
        )
        val tempCount = sharedPreferences.getInt("count", 0)
        var date = sharedPreferences.getString("date", "no value")
        status.text = "$tempCount - $date"
    }

    private fun isWorkScheduled(tag: String): Boolean {
        val instance = WorkManager.getInstance()
        val statuses: ListenableFuture<List<WorkInfo>> =
            instance.getWorkInfosByTag(tag)
        return try {
            var running = false
            val workInfoList: List<WorkInfo> = statuses.get()
            for (workInfo in workInfoList) {
                val state = workInfo.state
                running = state == WorkInfo.State.RUNNING || state == WorkInfo.State.ENQUEUED
            }
            running
        } catch (e: ExecutionException) {
            e.printStackTrace()
            false
        } catch (e: InterruptedException) {
            e.printStackTrace()
            false
        }
    }
}