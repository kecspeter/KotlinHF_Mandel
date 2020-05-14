import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color

class ScreenImageHandler(width: Int, height: Int)
{
    private val workerScheduler = WorkerScheduler()

    private var mainImage: WritableImage = WritableImage(width, height)
    var chunk = Vector2D(96.0,96.0)

    private var frame = mutableListOf<RegionData>()
    private var frameStartTime = 0L
    var frameTime = -1L
    var framecompleted = false

    private fun calcRegion(r: RegionData)
    {
        workerScheduler.addTask(r)
    }

    fun flushJobs()
    {
        workerScheduler.clearTasks()
        frame.clear()
        framecompleted = false
    }

    fun newJob(screenStartPos: Vector2D, screenRes: Vector2D, screenZoom: Double, mode: Int)
    {
        frameStartTime = System.currentTimeMillis()
        val threadStartPos = Vector2D(0.0,0.0)
        while (threadStartPos.y < screenRes.y)
        {
            val r = RegionData(RegionStartPos = Vector2D(threadStartPos.x, threadStartPos.y), RegionSize = chunk, InnerStartPos = screenStartPos+threadStartPos*screenZoom, InnerZoom = screenZoom, type = mode, screen = this, frameTime = -1)
            frame.add(r)
            calcRegion(r)

            threadStartPos.x += chunk.x
            if (threadStartPos.x >= screenRes.x)
            {
                threadStartPos.x = 0.0
                threadStartPos.y += chunk.y
            }
        }
    }


    fun updateScreen(graphicsContext: GraphicsContext)
    {
        graphicsContext.drawImage(mainImage, 0.0,0.0, mainImage.width, mainImage.height)


    }

    fun setMaxThread(n : Int)
    {
        workerScheduler.maxThread = n
        workerScheduler.restartScheduler()
        flushJobs()
    }

    fun checkFrameTime()
    {
        for (r in frame)
        {
            if (r.frameTime == -1L)
            {
                return
            }
        }
        if (!framecompleted)
        {
            frameTime = System.currentTimeMillis() - frameStartTime
            println("total frameTime: $frameTime")
            framecompleted = true
        }
    }

    fun copyRegion(regionImage: WritableImage, startPos: Vector2D)
    {

        for(y in 0 until regionImage.height.toInt())
        {
            for(x in 0 until regionImage.width.toInt())
            {
                if(x+startPos.x.toInt() >= mainImage.width || y+startPos.y.toInt() >= mainImage.height) {
                    break
                }
                val c : Color = regionImage.pixelReader.getColor(x,y)
                mainImage.pixelWriter.setColor(x+startPos.x.toInt(),y+startPos.y.toInt(),c)
            }

        }
    }
}