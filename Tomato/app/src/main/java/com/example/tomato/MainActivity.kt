package com.example.tomato

import android.app.Notification
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tomato.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), StopwatchListener {

    private lateinit var binding: ActivityMainBinding

    private val timerAdapter = TimerAdapter(this)
    private val timers = mutableListOf<Timer>()

    private var nextId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = timerAdapter
        }

        binding.addNewTimerButton.setOnClickListener {
            if(binding.valueTimerStart.text.isNotEmpty() && binding.valueTimerStart.text.toString() != "0"){
                val startMinutes = binding.valueTimerStart.text.toString().toLong() * 1000L * 60L
                timers.add(Timer(nextId++, startMinutes,startMinutes, false, isFinish = false))
                timerAdapter.submitList(timers.toList())
                binding.valueTimerStart.text.clear()
            }else{
                Toast.makeText(this, getString(R.string.add_value_uncorrect), Toast.LENGTH_SHORT).show()
                binding.valueTimerStart.text.clear()
            }
        }


    }
    override fun start(id: Int) {
        changeStopwatch(id, null, true, finish = false)
    }

    override fun stop(id: Int, currentMs: Long) {
        changeStopwatch(id, currentMs, false, finish = false)
    }


    override fun delete(id: Int) {
        timers.remove(timers.find { it.id == id })
        timerAdapter.submitList(timers.toList())
    }

    override fun finish(id: Int, currentMs: Long,finish: Boolean) {
        Toast.makeText(this, "Время вышло!", Toast.LENGTH_SHORT).show()
        changeStopwatch(id, 0, false, finish)
    }

    private fun changeStopwatch(id: Int, currentMs: Long?, isStarted: Boolean,finish: Boolean) {
        val newTimers = mutableListOf<Timer>()
        timers.forEach {
            if (it.id == id) {
                newTimers.add(Timer(it.id, it.startMs,currentMs ?: it.currentMs, isStarted,finish))
            } else {
                newTimers.add(Timer(it.id, it.startMs,it.currentMs, false, it.isFinish))
            }
        }

        timerAdapter.submitList(newTimers)
        timers.clear()
        timers.addAll(newTimers)
    }
}