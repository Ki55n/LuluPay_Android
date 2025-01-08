package com.sdk.lulupay.timer

import android.os.CountDownTimer

class Timer {
  companion object {
    private var countDownTimer: CountDownTimer? = null
    var isRunning = false
      private set // Make setter private to prevent external modification

    // Start the timer
    fun start(totalTimeInMillis: Long, intervalInMillis: Long) {
      if (isRunning) return // Prevent multiple starts

      countDownTimer =
          object : CountDownTimer(totalTimeInMillis, intervalInMillis) {
                override fun onTick(millisUntilFinished: Long) {
                  // Add any functionality for each tick
                }

                override fun onFinish() {
                  isRunning = false
                }
              }
              .start()
      isRunning = true
    }

    // Stop the timer
    fun stop() {
      countDownTimer?.cancel() // Safely handle nullability
      countDownTimer = null
      isRunning = false
    }
  }
}
