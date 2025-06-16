package compose.world

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.seconds

data class SomeState(
    val counterOne: Int = 0,
    val counterTwo: Int = 0
)

fun main() {

    val state = MutableStateFlow(SomeState())

    @Synchronized
    fun setValue(newState: SomeState.() -> SomeState) {
        val currentState = state.value
        state.value = newState(currentState)
    }

    val scope = CoroutineScope(Dispatchers.IO)

//    val stateActor = scope.actor<SomeState> {
//        for (newState in channel) {
//            state.update { newState }
//        }
//    }

    scope.launch {
        withTimeout(5.5.seconds) {
            while (true) {
                delay(10)
                val currentState = state.value
                setValue {
                    copy(counterOne = currentState.counterOne + 1)
                }
            }
        }
    }

    scope.launch {
        withTimeout(5.5.seconds) {
            while (true) {
                delay(10)
                val currentState = state.value
                setValue {
                    copy(counterTwo = currentState.counterTwo + 1)
                }
            }
        }
    }

    Thread.sleep(6000)
    println(state.value)
}