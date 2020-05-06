import javafx.scene.image.WritableImage
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import java.util.concurrent.CancellationException
import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis

class WorkerScheduler
{
    private val maxThread: Int = 16
    private val workerContext = newFixedThreadPoolContext(maxThread,"WorkerContext")
    private val workerDispatcher = Executors.newFixedThreadPool(maxThread).asCoroutineDispatcher()          //Shaman help
    private val workerChannel = Channel<RegionData>()


    init
    {
        startWorkers()
    }

    public fun addTask(r: RegionData)
    {
        //println("TaskAdd")
        GlobalScope.launch(workerDispatcher)
        {
            workerChannel.send(r)
        }
    }

    public fun clearTasks()
    {
        workerChannel.cancel(CancellationException("UpdatedRenderPositions"))
    }

    public fun startWorkers()
    {
        for (i in 1..maxThread)
        {
            GlobalScope.launch(workerDispatcher)
            {
                workerTask()
            }
        }
    }

    private suspend fun workerTask()
    {
        //println("WorkerStarted")
        while(true)
        {
            /*
            workerChannel.consumeEach {
                mandelJob(it)
                println("consumed $it")
            }
            */
            for(r in workerChannel)
            {
                //println("consumed $r")
                workerJob(r)
            }
            //delay(200)
            //println("No task")
        }
    }

    private suspend fun workerJob(r: RegionData)
    {
        var region: WritableImage;
        val interval = measureTimeMillis()
        {
            when(r.type)
            {
                0 -> region =
                    MandelbrotSet.INSTANCE.calcRegion(r.InnerStartPos, r.RegionSize, r.InnerZoom)

                else -> region =
                    JuliaSet.INSTANCE.calcRegion(r.InnerStartPos, r.RegionSize, r.InnerZoom)
            }

            //println("calc completed")
            r.screen.copyRegion(region, r.RegionStartPos)
        }

        //println("interval: $interval")
    }

}