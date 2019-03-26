import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.html.*
import kotlinx.html.dom.append
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLDivElement
import kotlin.browser.document
import kotlin.coroutines.CoroutineContext

object Application : CoroutineScope {
    override val coroutineContext: CoroutineContext = Job()
    private val qc = QueueController(coroutineContext)
    //root div
    private val div: HTMLDivElement by lazy {
        document.getElementById("simulation output") as HTMLDivElement
    }

    //HTML builder, load UI
    @ExperimentalCoroutinesApi
    fun start() {
        document.body!!.append.div {
            h1 {
                +"Lunch Line Simulation with Kotlin Coroutines"
                style = "font-family:Verdana"
            }
            div {
                button {
                    +"Run Simulation"
                    onClickFunction = { onSimulationStart() }
                }
                button {
                    +"Clear"
                    onClickFunction = { onSimulationStop() }
                }
            }
            div {
                id = "simulation output"
                style = "font-family:Courier New;overflow:auto;padding:10px;white-space:pre"
            }
        }
    }

    private fun onSimulationStop() {
        coroutineContext.cancelChildren()
        div.textContent = ""
    }

    @ExperimentalCoroutinesApi
    private fun onSimulationStart() {
        onSimulationStop()
        launch {
            val results: MutableList<QueueInfo> = mutableListOf()
            val resultsChannel: ReceiveChannel<QueueInfo> = qc.runSimulation(
                refs = qc.createSampleRefs(),
                timeout = 20.s,
                logging = false
            )
            for (queueInfo in resultsChannel) {
                div.textContent = """
                    ProcessNumber: ${queueInfo.processNumber}
                    Wait times: ${queueInfo.waitTimes}
                    Total wait time: ${queueInfo.totalWaitTime}

                    """.trimIndent() + div.textContent
                results.add(queueInfo)
            }
            val averageWaitTime = results.map { it.totalWaitTime }.average()
            div.textContent = """
                Lunch is over; average wait time: $averageWaitTime

            """.trimIndent() + div.textContent
        }
    }

    fun finish() {
        coroutineContext.cancel()
    }
}