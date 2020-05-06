import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color

class ScreenImageHandler(width: Int, height: Int)
{
    private val workerScheduler = WorkerScheduler()

    private var mainImage: WritableImage = WritableImage(width, height);
    var chunk = Vector2D(150.0,150.0)



    private fun calcRegion(r: RegionData)
    {
        workerScheduler.addTask(r)
    }

    fun flushJobs()
    {
        workerScheduler.clearTasks()
    }

    fun newJob(screenStartPos: Vector2D, screenRes: Vector2D, screenZoom: Double, mode: Int)
    {
        var threadStartPos = Vector2D(0.0,0.0)
        while (threadStartPos.y < screenRes.y)
        {
            var r = RegionData(ImageStartPos = screenStartPos, RegionSize = chunk, RegionStartPos = Vector2D(threadStartPos.x, threadStartPos.y), InnerZoom = screenZoom, type = mode, screen = this, InnerStartPos = screenStartPos+threadStartPos*screenZoom)
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

    fun copyRegion(regionImage: WritableImage, startPos: Vector2D)
    {

        for(y in 0 until regionImage.height.toInt())
        {
            for(x in 0 until regionImage.width.toInt())
            {
                if(x+startPos.x.toInt() >= mainImage.width || y+startPos.y.toInt() >= mainImage.height) {
                    //println("kilépés $x, $y ponton")
                    break;
                }
                var c : Color = regionImage.pixelReader.getColor(x,y)
                mainImage.pixelWriter.setColor(x+startPos.x.toInt(),y+startPos.y.toInt(),c)
            }

        }

        //println("megcsináltam a másolást")

    }
}