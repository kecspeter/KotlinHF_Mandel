import javafx.scene.image.WritableImage
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import java.util.concurrent.CancellationException
import java.util.concurrent.Executors
import javax.swing.plaf.synth.Region
import kotlin.random.Random
import kotlin.system.measureTimeMillis

class WorkerScheduler
{
    var maxThread: Int = 16
    private var workerDispatcher = Executors.newFixedThreadPool(maxThread).asCoroutineDispatcher()          //Shaman help
    private var workerChannel = Channel<RegionData>()

    private var renderTime : Long = 0
    init
    {
        startWorkers()
    }

    public fun addTask(r: RegionData)
    {
        GlobalScope.launch(workerDispatcher)
        {
            workerChannel.send(r)
        }
    }

    public fun clearTasks()
    {
        workerChannel.cancel(CancellationException("UpdatedRenderPositions"))
        workerChannel = Channel<RegionData>()
        startWorkers()
    }

    public fun restartScheduler()
    {
        workerChannel.cancel(CancellationException("UpdatedRenderPositions"))
        workerChannel = Channel<RegionData>()
        workerDispatcher.close()
        workerDispatcher = Executors.newFixedThreadPool(maxThread).asCoroutineDispatcher()
        startWorkers()
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
        while(true)
        {
            for(r in workerChannel)
            {
                workerJob(r)
            }
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

            r.screen.copyRegion(region, r.RegionStartPos)
        }

        r.frameTime = interval
    }

}