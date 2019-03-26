@file:Suppress("RemoveExplicitTypeArguments")

import kotlinx.coroutines.*

@ExperimentalCoroutinesApi
fun main(): Unit = runBlocking<Unit> {
    launch {
        repeat(20) {
            delay(500L)
            println(it)
        }
    }
}