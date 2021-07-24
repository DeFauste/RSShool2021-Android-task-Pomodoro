package com.example.tomato

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tomato.databinding.ActivityMainBinding
import java.io.Serializable


class MainActivity : AppCompatActivity(), StopwatchListener,LifecycleObserver {


    private lateinit var binding: ActivityMainBinding

    private val timerAdapter = TimerAdapter(this)
    private val timers = mutableListOf<Timer>()
    private val  maxTimers = 10
    private var nextId = 0
    private var currentID = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = timerAdapter
        }

        binding.addNewTimerButton.setOnClickListener {
            if(timers.size < 10)
                if(binding.valueTimerStart.text.isNotEmpty() && binding.valueTimerStart.text.toString() != "0"){
                    val startMinutes = binding.valueTimerStart.text.toString().toLong() * 1000L * 60L
                    timers.add(Timer(nextId++, startMinutes,startMinutes, false, isFinish = false))
                    timerAdapter.submitList(timers.toList())
                    binding.valueTimerStart.text.clear()
                }else{
                    Toast.makeText(this, getString(R.string.add_value_uncorrect), Toast.LENGTH_SHORT).show()
                    binding.valueTimerStart.text.clear()
                }
            else
                Toast.makeText(this, getString(R.string.Max_quantity_timer) + maxTimers, Toast.LENGTH_SHORT).show()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        val startTime = timerAdapter.currentList.find { it.id == currentID }?.currentMs ?: 0L
        if(startTime > 0L) {
            val startIntent = Intent(this, ForegroundService::class.java)
            startIntent.putExtra(COMMAND_ID, COMMAND_START)
            startIntent.putExtra(STARTED_TIMER_TIME_MS, startTime)
            startService(startIntent)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)
    }
    override fun start(id: Int) {
        currentID = id
        changeStopwatch(id, null, true, finish = false)
    }

    override fun stop(id: Int, currentMs: Long) {
        if(currentID == id) currentID = -1
        changeStopwatch(id, currentMs, false, finish = false)
    }


    override fun delete(id: Int) {
        if(currentID == id) currentID = -1
        timers.remove(timers.find { it.id == id })
        timerAdapter.submitList(timers.toList())
    }

    override fun finish(id: Int, currentMs: Long,finish: Boolean) {
        Toast.makeText(this, getString(R.string.Timer_end), Toast.LENGTH_SHORT).show()
        changeStopwatch(id, 0, false, finish)
    }

    private fun changeStopwatch(id: Int, currentMs: Long?, isStarted: Boolean,finish: Boolean) {
        val newTimers = mutableListOf<Timer>()
        timers.replaceAll{
            when {
                it.id == id -> Timer(it.id, it.startMs,currentMs ?: it.currentMs, isStarted,finish)
                it.isStarted ->{
                    Timer(it.id,  it.startMs,currentMs ?: it.currentMs, false,finish)
                }
                else -> {it}
            }
        }
        timerAdapter.submitList(timers.toList())


    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Closing Activity")
            .setMessage("Are you sure you want to close this activity?")
            .setPositiveButton(
                "Yes"
            ) { dialog, which -> finish() }
            .setNegativeButton("No", null)
            .show()
    }
    override fun onDestroy() {
        super.onDestroy()
        onAppForegrounded()
    }

}

//class MainActivity : AppCompatActivity(), StopwatchListener,LifecycleObserver {
//
//
//    private lateinit var binding: ActivityMainBinding
//
//    private val timerAdapter = TimerAdapter(this)
//    private val timers = mutableListOf<Timer>()
//    private val  maxTimers = 10
//    private var nextId = 0
//    private var currentID = -1
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
//
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        binding.recycler.apply {
//            layoutManager = LinearLayoutManager(context)
//            adapter = timerAdapter
//        }
//
//        binding.addNewTimerButton.setOnClickListener {
//            if(timers.size < 10)
//                if(binding.valueTimerStart.text.isNotEmpty() && binding.valueTimerStart.text.toString() != "0"){
//                    val startMinutes = binding.valueTimerStart.text.toString().toLong() * 1000L * 60L
//                    timers.add(Timer(nextId++, startMinutes,startMinutes, false, isFinish = false))
//                    timerAdapter.submitList(timers.toList())
//                    binding.valueTimerStart.text.clear()
//                }else{
//                    Toast.makeText(this, getString(R.string.add_value_uncorrect), Toast.LENGTH_SHORT).show()
//                    binding.valueTimerStart.text.clear()
//                }
//            else
//                Toast.makeText(this, getString(R.string.Max_quantity_timer) + maxTimers, Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
//    fun onAppBackgrounded() {
//        val startTime = timerAdapter.currentList.find { it.id == currentID }?.currentMs ?: 0L
//        if(startTime > 0L) {
//            val startIntent = Intent(this, ForegroundService::class.java)
//            startIntent.putExtra(COMMAND_ID, COMMAND_START)
//            startIntent.putExtra(STARTED_TIMER_TIME_MS, startTime)
//            startService(startIntent)
//        }
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_START)
//    fun onAppForegrounded() {
//        val stopIntent = Intent(this, ForegroundService::class.java)
//        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
//        startService(stopIntent)
//    }
//    override fun start(id: Int) {
//        currentID = id
//        changeStopwatch(id, null, true, finish = false)
//    }
//
//    override fun stop(id: Int, currentMs: Long) {
//        if(currentID == id) currentID = -1
//        changeStopwatch(id, currentMs, false, finish = false)
//    }
//
//
//    override fun delete(id: Int) {
//        if(currentID == id) currentID = -1
//        timers.remove(timers.find { it.id == id })
//        timerAdapter.submitList(timers.toList())
//    }
//
//    override fun finish(id: Int, currentMs: Long,finish: Boolean) {
//        Toast.makeText(this, getString(R.string.Timer_end), Toast.LENGTH_SHORT).show()
//        changeStopwatch(id, 0, false, finish)
//    }
//
//    private fun changeStopwatch(id: Int, currentMs: Long?, isStarted: Boolean,finish: Boolean) {
//        val newTimers = mutableListOf<Timer>()
//            timers.forEach {
//                if (it.id == id) {
//                    newTimers.add(Timer(it.id, it.startMs,currentMs ?: it.currentMs, isStarted,finish))
//                } else {
//                    newTimers.add(Timer(it.id, it.startMs,it.currentMs, false, it.isFinish))
//                }
//            }
//
//            timerAdapter.submitList(newTimers)
//            timers.clear()
//            timers.addAll(newTimers)
//
//    }
//
//override fun onBackPressed() {
//    AlertDialog.Builder(this)
//        .setIcon(android.R.drawable.ic_dialog_alert)
//        .setTitle("Closing Activity")
//        .setMessage("Are you sure you want to close this activity?")
//        .setPositiveButton(
//            "Yes"
//        ) { dialog, which -> finish() }
//        .setNegativeButton("No", null)
//        .show()
//}
//    override fun onDestroy() {
//        super.onDestroy()
//        onAppForegrounded()
//    }
//
//}