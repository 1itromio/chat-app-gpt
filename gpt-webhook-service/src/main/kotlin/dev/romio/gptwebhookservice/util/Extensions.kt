package dev.romio.gptwebhookservice.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

fun String.replaceLast(oldValue: String, newValue: String, ignoreCase: Boolean = false): String {
    val index = lastIndexOf(oldValue, ignoreCase = ignoreCase)
    return if (index < 0) this else this.replaceRange(index, index + oldValue.length, newValue)
}

fun <T> Flow<T>.perform(coroutineScope: CoroutineScope, collector: FlowCollector<T>) {
    coroutineScope.launch {
        collect(collector)
    }
}
