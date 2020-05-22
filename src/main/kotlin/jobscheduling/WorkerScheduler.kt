package jobscheduling

import sets.JuliaSet
import data.RegionData
import drawing.AppWindow
import javafx.scene.image.WritableImage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import sets.MandelbrotSet
import java.util.concurrent.CancellationException
import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis

class WorkerScheduler
{
    var maxThread: Int = AppWindow.startMaxThread
    private var workerDispatcher = Executors.newFixedThreadPool(maxThread).asCoroutineDispatcher()          //Shaman help
    private var workerChannel = Channel<RegionData>()

    init
    {
        startWorkers()
    }

    private fun startWorkers()
    {
        for (i in 1..maxThread)
        {
            GlobalScope.launch(workerDispatcher)
            {
                workerTask()
            }
        }
    }

    fun addTask(r: RegionData)
    {
        GlobalScope.launch(workerDispatcher)
        {
            workerChannel.send(r)
        }
    }

    fun clearTasks()
    {
        workerChannel.cancel(CancellationException("UpdatedRenderPositions"))
        workerChannel = Channel()
        startWorkers()
    }

    fun restartScheduler()
    {
        workerChannel.cancel(CancellationException("RestartWorkers"))
        workerChannel = Channel()
        workerDispatcher.close()
        workerDispatcher = Executors.newFixedThreadPool(maxThread).asCoroutineDispatcher()
        startWorkers()
    }

    fun stop()
    {
        workerChannel.cancel(CancellationException("ApplicationExit"))
        workerDispatcher.close()
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

    private fun workerJob(r: RegionData)
    {
        var region: WritableImage
        val interval = measureTimeMillis()
        {
            region = when(r.type)
            {
                0 -> MandelbrotSet.INSTANCE.calcRegion(r.InnerStartPos, r.RegionSize, r.InnerZoom)

                else -> JuliaSet.INSTANCE.calcRegion(r.InnerStartPos, r.RegionSize, r.InnerZoom)
            }
            r.screen.copyRegion(region, r.RegionStartPos)
        }

        r.frameTime = interval
    }

}