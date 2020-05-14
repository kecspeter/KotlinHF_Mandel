import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
import javafx.stage.Stage

class AppWindow : Application()
{
    companion object
    {
        private const val WIDTH = 960
        private const val HEIGHT = 960


    }

    private lateinit var mainScene: Scene
    private lateinit var graphicsContext: GraphicsContext
    private lateinit var timer: AnimationTimer

    private var res: Vector2D = Vector2D(WIDTH.toDouble(), HEIGHT.toDouble())
    private var pos: Vector2D = Vector2D(-2.0,-0.75)
    private var pivot: Vector2D = Vector2D(pos.x+res.x/2,pos.y+res.y/2)
    private var zoom: Double = 0.0025

    private var isDragging: Boolean = false
    private var lastDragEvent: MouseEvent? = null

    private var mainScreen = ScreenImageHandler(WIDTH, HEIGHT)

    private var mode = 0

    var benchmarkMode  = 0
    private val benchWriter = BenchmarkWriter()

    override fun start(primaryStage: Stage?)
    {
        if(primaryStage != null)
        {
            primaryStage.title = "MandelApp"

            val root = Group()
            mainScene = Scene(root)

            val canvas = Canvas(WIDTH.toDouble(), HEIGHT.toDouble())
            root.children.add(canvas)
            graphicsContext = canvas.graphicsContext2D

            prepareActionHandlers()
            prepareAnimationTimer()

            primaryStage.scene = mainScene
            primaryStage.show()

            getHelp()
        }
    }

    private fun prepareActionHandlers()
    {
        val mouseEventHandler: EventHandler<MouseEvent> =
            EventHandler {
                updateMouseInput()
            }
        mainScene.addEventFilter(MouseEvent.ANY, mouseEventHandler)


        val keyboardEventHandler: EventHandler<KeyEvent> =
            EventHandler {
                updateKeyboardPressed(it)
            }
        mainScene.addEventFilter(KeyEvent.KEY_PRESSED, keyboardEventHandler)

    }

    private fun prepareAnimationTimer()
    {
        timer = object: AnimationTimer() {
            override fun handle(now: Long)
            {
                updateScreen()
            }
        }
        timer.start()
    }

    private fun updateKeyboardPressed(keyEvent : KeyEvent)
    {

        if (keyEvent.code == KeyCode.H)
        {
            getHelp()
        }

        if (keyEvent.code == KeyCode.M)
        {
            when(mode)
            {
                0 -> {
                    mode = 1
                    JuliaSet.INSTANCE.c = Complex(pos.x + pivot.x*zoom, pos.y + pivot.y*zoom)
                }
                1 -> mode = 0
            }
            updatePosition()
        }

        if (keyEvent.code == KeyCode.R)
        {
            resetWindow()
        }
        if (keyEvent.code == KeyCode.U)
        {
            benchmarkMode = 1
            resetWindow()
            bench1()
        }
        if(keyEvent.code == KeyCode.I)
        {
            benchmarkMode = 2
            resetWindow()
            bench2()
        }

        if(keyEvent.code == KeyCode.Q)
        {
            MandelbrotSet.INSTANCE.density++
            when(mode)
            {
                0 -> MandelbrotSet.INSTANCE.density++
                else -> JuliaSet.INSTANCE.density++
            }
            updatePosition()
        }

        if(keyEvent.code == KeyCode.A)
        {
            when(mode)
            {
                0 -> if(MandelbrotSet.INSTANCE.density > 1) MandelbrotSet.INSTANCE.density--
                else -> if(JuliaSet.INSTANCE.density > 1) JuliaSet.INSTANCE.density--
            }
            updatePosition()
        }

    }

    private fun getHelp()
    {
        graphicsContext.fillText("Help", 5.0, 10.0)
        graphicsContext.fillText("Keyboard:\n" +
                "H - Help\n" +
                "R - Reset position and zoom\n" +
                "U - Benchmark mode 1\n" +
                "I - Benchmark mode 2\n" +
                "Q - Increase resolution of the current (Mandelbrot/Julia)set\n" +
                "A - Decrease resolution of the current (Mandelbrot/Julia)set\n" +
                "M - Switch between Julia and Mandelbrot\n" +
                "\tJulia const point will be the last pivot before the switch",
            5.0, 50.0)
        graphicsContext.fillText("Mouse:\n " +
                "Drag to change position\n" +
                "Scroll to zoom in/out\n",
            250.0, 50.0)

        println("\t\t\tHelp\n\n")
        println("Keyboard:\n" +
                "H - Help\n" +
                "R - Reset position and zoom\n" +
                "U - Benchmark mode 1\n" +
                "I - Benchmark mode 2\n" +
                "Q - Increase resolution of the current (Mandelbrot/Julia)set\n" +
                "A - Decrease resolution of the current (Mandelbrot/Julia)set" +
                "M - Switch between Julia and Mandelbrot\n")
        println("Mouse:\n " +
                "Drag to change position\n" +
                "Scroll to zoom in/out\n")

    }

    private fun updateMouseInput()
    {

        mainScene.onMouseClicked = EventHandler { mouseClicked() }
        mainScene.onMouseDragged = EventHandler { event -> mouseDrag(event)}
        mainScene.onScroll = EventHandler { event -> mouseScroll(event) }
    }

    private fun mouseClicked()
    {
        //println("mouse click")
        if(isDragging)
        {
            isDragging = false
            lastDragEvent = null
        }
        updatePosition()
    }

    private fun mouseDrag(e: MouseEvent)
    {
        //println("mouse drag")
        if(!isDragging)
        {
            isDragging = true
        }

        if(isDragging)
        {
            if(lastDragEvent == null)
            {
                lastDragEvent = e
            }
            updatePositionDrag(e)
            lastDragEvent = e
        }
        if(System.currentTimeMillis().toInt() % 3 == 0)
        updatePosition()
    }

    private fun mouseScroll(e: ScrollEvent)
    {
        //println("mouse scroll")
        val oldzoom = zoom

        zoom = (if (e.deltaY > 0) zoom / 2 else zoom * 2)
        if(zoom > 0.0025)
            zoom = 0.0025

        pos.x = pos.x + (res.x/2) * oldzoom - (res.x/2) * zoom
        pos.y = pos.y + (res.y/2) * oldzoom - (res.y/2) * zoom

        updatePivot()
        updatePosition()
    }


    private fun updatePositionDrag(e: MouseEvent)
    {
        pos.x -= (e.x- (lastDragEvent?.x ?: 0.0)) * zoom
        pos.y -= (e.y- (lastDragEvent?.y ?: 0.0)) * zoom

        //var lastPos = Vector2D(lastDragEvent?.x ?: 0.0, lastDragEvent?.y ?: 0.0)
        //var currPos = Vector2D(e.x,e.y)
        updatePivot()
    }

    private fun updatePivot()
    {
        pivot.x = pos.x+res.x/2
        pivot.y = pos.y+res.y/2
    }

    private fun updatePosition()
    {
        mainScreen.newJob(pos, res, zoom, mode)
    }

    private fun resetWindow()
    {
        pos = Vector2D(-2.0,-0.75)
        zoom = 0.0025
        MandelbrotSet.INSTANCE.density = 25
        JuliaSet.INSTANCE.density = 60
        mainScreen.flushJobs()
        updatePosition()
    }


    fun updateScreen()
    {
        mainScreen.updateScreen(graphicsContext)
        graphicsContext.fillRect(pivot.x-1, pivot.y-1, 3.0, 3.0)
        graphicsContext.fillText("Set iteration (res): ${if(mode == 0) MandelbrotSet.INSTANCE.density else JuliaSet.INSTANCE.density}",5.0, res.y-40.0)
        graphicsContext.fillText("Pivot pos: ${pos.x + pivot.x*zoom}, ${pos.y + pivot.y*zoom}",5.0, res.y-30.0)
        graphicsContext.fillText("Mode: " +
                when(mode){
                    0 -> "Mandel"
                    else ->"Julia"
                },5.0, res.y-20.0)
        graphicsContext.fillText("Zoom: ${0.0025/zoom}",5.0, res.y-10.0)
    }


    private fun bench1()
    {
        println("TestMode 1 started\n" +
                "Fix 8 thread\n" +
                "chunkSize from 2 to 256\n" +
                "redraw count: 32\n")
        val threadCountMax = 8

        mainScreen.setMaxThread(threadCountMax)
        for(i in 2..256)
        {
            mainScreen.chunk = Vector2D(i*1.0, i*1.0)
            println("chunk size: (${mainScreen.chunk.x},${mainScreen.chunk.y})")

            val frameTimes = mutableListOf<Long>()

            for(j in 1..32)
            {
                resetWindow()

                while (true)
                {
                    mainScreen.checkFrameTime()
                    if(mainScreen.framecompleted)
                    {
                        frameTimes.add(mainScreen.frameTime)
                        break
                    }
                    Thread.sleep(10)
                }
            }
            benchWriter.testSession(frameTimes,"chunkSize$i")

        }
        println("TestMode ended")
        benchmarkMode = 0
        benchWriter.writeOut("chunkSizeBench")
    }

    private fun bench2()
    {
        println("TestMode 2 started\n" +
                "Fix chunkSize 64,64\n" +
                "chunkSize from 2 to 256\n" +
                "redraw count: 32\n")
        mainScreen.chunk = Vector2D(64.0, 64.0)

        for(i in 1..256)
        {

            println("threadCount: (${i})")
            mainScreen.setMaxThread(i)
            val frameTimes = mutableListOf<Long>()

            for(j in 1..32)
            {
                resetWindow()

                while (true)
                {
                    mainScreen.checkFrameTime()
                    if(mainScreen.framecompleted)
                    {
                        frameTimes.add(mainScreen.frameTime)
                        break
                    }
                    Thread.sleep(10)

                }
            }
            benchWriter.testSession(frameTimes,"threadCount$i")

        }
        println("TestMode ended")
        benchmarkMode = 0
        benchWriter.writeOut("threadCountBench")
    }


}