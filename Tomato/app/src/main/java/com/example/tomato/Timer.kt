package com.example.tomato

data class Timer(val id:Int,
                 var startMs:Long,
                 var currentMs:Long,
                 var isStarted:Boolean,
                 var isFinish:Boolean
                 )
