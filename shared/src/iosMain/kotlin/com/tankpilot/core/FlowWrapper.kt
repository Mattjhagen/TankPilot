package com.tankpilot.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class FlowWrapper<T>(private val flow: StateFlow<T>) {
    val value: T get() = flow.value
    
    fun subscribe(onEach: (T) -> Unit): Closeable {
        val job = Job()
        val scope = CoroutineScope(Dispatchers.Main + job)
        flow.onEach { onEach(it) }.launchIn(scope)
        return object : Closeable {
            override fun close() {
                job.cancel()
            }
        }
    }
}

interface Closeable {
    fun close()
}
