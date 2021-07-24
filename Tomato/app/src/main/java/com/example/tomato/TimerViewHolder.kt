package com.example.tomato

import android.content.res.Resources
import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import androidx.core.view.isInvisible
import androidx.lifecycle.LifecycleObserver
import androidx.recyclerview.widget.RecyclerView
import com.example.tomato.databinding.TimerItemBinding
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import androidx.lifecycle.*


//Рабочий вариант
class TimerViewHolder (
    private val binding: TimerItemBinding,
    private val listener: StopwatchListener,
    private val resources: Resources
): RecyclerView.ViewHolder(binding.root){

    private var timeTimer: CountDownTimer? = null
    private var flagLive:Boolean = false
    private var id = 0
    private var curTime = 0L

    fun bind(timer: Timer) {
        binding.customViewTwo.setPeriod(timer.startMs)
        binding.customViewTwo.setCurrent(timer.startMs-timer.currentMs)


        binding.stopwatchTimer.text = timer.currentMs.displayTime()
        binding.controller.isEnabled = true

        if(timer.isFinish && timer.currentMs == 0L){
            binding.controller.text = resources.getString(R.string.textEnd)
            binding.controller.isEnabled = false
        }
        if (timer.isStarted) {
            binding.controller.text = resources.getString(R.string.textStop)
            startTimer(timer)
        }else {

            binding.controller.text = resources.getString(R.string.textStart)
            stopTimer(timer)
        }
        initButtonsListeners(timer)
    }
    private fun initButtonsListeners(timer: Timer) {
        binding.controller.setOnClickListener {
            if (timer.isStarted) {
                listener.stop(timer.id, timer.currentMs)
            } else{
                listener.start(timer.id)
            }
        }

        binding.deleteButton.setOnClickListener { listener.delete(timer.id) }
    }

    private fun stopTimer(timer: Timer) {
        flagLive = false

        timeTimer?.cancel()
        binding.blinkingIndicator.isInvisible = true
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
    }
    private fun startTimer(timer: Timer) {
        flagLive = true

        timeTimer?.cancel()
        timeTimer = getCountDownTimer(timer)
        timeTimer?.start()

        binding.blinkingIndicator.isInvisible = false
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start()

    }

    private fun getCountDownTimer(timer: Timer): CountDownTimer {
        return object : CountDownTimer(timer.currentMs, UNIT_TEN_MS) {
            val interval = UNIT_TEN_MS

            override fun onTick(millisUntilFinished: Long) {
                timer.currentMs = millisUntilFinished

                binding.stopwatchTimer.text = timer.currentMs.displayTime()
                binding.customViewTwo.setCurrent(timer.startMs - timer.currentMs + UNIT_TEN_MS)

                id = timer.id
                curTime = timer.currentMs

                if(timer.currentMs == 0L){
                    finishDraw()
                }
            }

            override fun onFinish() {
                binding.stopwatchTimer.text = timer.currentMs.displayTime()
            }
        }
    }
    private fun finishDraw(){
            listener.finish(id,0,true)
    }
    private fun Long.displayTime(): String {
        if (this <= 0L) {
            binding.blinkingIndicator.isInvisible = true
            (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
            flagLive = false
            return START_TIME
        }
        val h = this / 1000 / 3600
        val m = this / 1000 % 3600 / 60
        val s = this / 1000 % 60

        return "${displaySlot(h)}:${displaySlot(m)}:${displaySlot(s)}"
    }

    private fun displaySlot(count: Long): String {
        return if (count / 10L > 0) {
            "$count"
        } else {
            "0$count"
        }
    }

    private companion object {

        private const val START_TIME = "00:00:00"
        private const val UNIT_TEN_MS = 1000L
        private const val PERIOD  = 1000L * 60L * 60L * 24L // Day
    }
}
