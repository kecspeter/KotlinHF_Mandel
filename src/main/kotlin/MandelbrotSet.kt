import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import kotlin.math.ln

class MandelbrotSet : InfiniteSet
{
    var density: Long = 25
    private var max: Double = 255.0
    private val c1: Color = Color.WHITE
    private val c2: Color = Color.ROYALBLUE


    private object HOLDER
    {
        val INSTANCE = MandelbrotSet()
    }

    companion object
    {
        val INSTANCE: MandelbrotSet by lazy { HOLDER.INSTANCE }
    }


    override fun calcRegion(s: Vector2D, e: Vector2D, res: Double): WritableImage
    {
        var region = WritableImage(e.x.toInt(), e.y.toInt())
        //println("calcRegion created size: (${e.x},${e.y})")
        val c: Color = Color.WHITE
        val c2: Color = Color.ROYALBLUE

        var internalRes : Double = res
        var internalResPos: Vector2D = Vector2D(s.x,s.y)
        var pixelPos : Vector2D = Vector2D(0.0,0.0)


        while (pixelPos.y < e.y.toInt())
        {
            while (pixelPos.x < e.x.toInt())
            {

                var pixelResult = calcPixel(internalResPos)
                region.pixelWriter.setColor(pixelPos.x.toInt(), pixelPos.y.toInt(), colorPixel(pixelResult))

                internalResPos.x += internalRes
                pixelPos.x++
            }

            internalResPos.x = s.x
            pixelPos.x = 0.0

            internalResPos.y += internalRes
            pixelPos.y++
        }
        return region
    }

    override fun calcPixel(pixel: Vector2D): Double
    {
        var res = Complex(0.0,0.0)
        var c = Complex(pixel.x, pixel.y)
        for (i in 0..density)
        {

            if(res.dist() < max)
            {
                res = res.mul().sum(c)
            }
            else
            {
                return -1.0
            }
        }
        if(res.dist() > max)
        {
            return max
        }
        return res.dist()
    }

    override fun colorPixel(pixelResult: Double): Color
    {
        var pixelColor: Color
        pixelColor = c1
        if(pixelResult == -1.0)
        {
            pixelColor = c2
        }
        if(pixelResult/(max+1) in 0.0..1.0)
        {
            pixelColor = Color.hsb(ln(pixelResult/(max+1)), (pixelResult / (max + 1)),1.0)
        }
        return pixelColor
    }
}