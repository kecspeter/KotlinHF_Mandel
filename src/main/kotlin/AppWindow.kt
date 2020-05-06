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
        private const val WIDTH = 1280
        private const val HEIGHT = 768


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
    private var lastTime: Long = 0


    private var mode = 0



    var speedTest = mutableListOf<String>()
    var speedTestMode  = 0





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
                updateScreen(now)
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
        }

        if (keyEvent.code == KeyCode.R)
        {
            pos = Vector2D(-2.0,-0.75)
            zoom = 0.0025

            updatePosition()
        }
        if (keyEvent.code == KeyCode.DIGIT1)
        {
            speedTestMode = 1
        }
        if(keyEvent.code == KeyCode.DIGIT2)
        {
            speedTestMode = 2
        }

        if(keyEvent.code == KeyCode.Q)
        {
            MandelbrotSet.INSTANCE.density++
            updatePosition()
        }

        if(keyEvent.code == KeyCode.A)
        {
            when(mode)
            {
                0 -> if(MandelbrotSet.INSTANCE.density > 1) MandelbrotSet.INSTANCE.density-- else ;
                else -> if(JuliaSet.INSTANCE.density > 1) JuliaSet.INSTANCE.density-- else ;
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
                "NUM1 - Benchmark mode 1\n" +
                "NUM2 - Benchmark mode 2\n" +
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
                "NUM1 - Benchmark mode 1\n" +
                "NUM2 - Benchmark mode 2\n" +
                "Q - Increase resolution of the current (Mandelbrot/Julia)set\n" +
                "A - Decrease resolution of the current (Mandelbrot/Julia)set" +
                "M - Switch between Julia and Mandelbrot\n")
        println("Mouse:\n " +
                "Drag to change position\n" +
                "Scroll to zoom in/out\n")

    }

    private fun updateMouseInput()
    {

        mainScene.onMouseClicked = EventHandler { event -> mouseClicked(event)}
        mainScene.onMouseDragged = EventHandler { event -> mouseDrag(event)}
        mainScene.onScroll = EventHandler { event -> mouseScroll(event) }
    }

    private fun mouseClicked(e: MouseEvent)
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
        updatePosition()
    }

    private fun mouseScroll(e: ScrollEvent)
    {
        //println("mouse scroll")
        var oldzoom = zoom

        zoom = (if(e.deltaY>0) zoom/2 else zoom*2)


        pos.x = pos.x + (res.x/2) * oldzoom - (res.x/2) * zoom
        pos.y = pos.y + (res.y/2) * oldzoom - (res.y/2) * zoom

        updatePivot()
        updatePosition()
    }


    private fun updatePositionDrag(e: MouseEvent)
    {
        pos.x -= (e.x- (lastDragEvent?.x ?: 0.0)) * zoom
        pos.y -= (e.y- (lastDragEvent?.y ?: 0.0)) * zoom

        var lastPos = Vector2D(lastDragEvent?.x ?: 0.0, lastDragEvent?.y ?: 0.0)
        var currPos = Vector2D(e.x,e.y)
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

    fun updateScreen(curr: Long)
    {
        mainScreen.updateScreen(graphicsContext)
        graphicsContext.fillRect(pivot.x-1, pivot.y-1, 3.0, 3.0)
        graphicsContext.fillText("Set iteration (res): ${MandelbrotSet.INSTANCE.density}",5.0, res.y-50.0)
        graphicsContext.fillText("Pivot pos: ${pos.x + pivot.x*zoom}, ${pos.y + pivot.y*zoom}",5.0, res.y-20.0)
        graphicsContext.fillText("Mode: " +
                "${when(mode){
                    0 -> "Mandel"
                    else ->"Julia"
                }
        }",5.0, res.y-10.0)


        //println(curr-lastTime)
    }


}