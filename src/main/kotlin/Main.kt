import data.Vector2D
import drawing.AppWindow
import javafx.application.Application
import java.lang.IndexOutOfBoundsException

fun main(args: Array<String>)
{
    if(args.isNotEmpty())
    {
        try
        {
            AppWindow.startWidth = args[0].toInt()
            AppWindow.startHeight = args[1].toInt()
            AppWindow.startMaxThread = args[2].toInt()
            AppWindow.startChunkSize = Vector2D(args[3].toDouble(), args[4].toDouble())
        }
        catch (e: IndexOutOfBoundsException)
        {
            println("Wrong parameters!\n" +
                    "Using default parameters: 960 576 16 96 96\n" +
                    "Usage: 'WIDTH' 'HEIGHT' 'MaxThreadCount' 'ChunkSizeX' 'ChunkSizeY'")
            AppWindow.startWidth = 960
            AppWindow.startHeight = 576
            AppWindow.startMaxThread = 16
            AppWindow.startChunkSize = Vector2D(64.0, 96.0)
        }
    }

    Application.launch(AppWindow::class.java, *args)
}
