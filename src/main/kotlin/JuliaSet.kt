import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import kotlin.math.ln

class JuliaSet : InfiniteSet
{
    var density: Long = 60
    private var max: Double = 255.0
    var c = Complex( 0.34, -0.05)


    private object HOLDER
    {
        val INSTANCE = JuliaSet()
    }

    companion object
    {
        val INSTANCE: JuliaSet by lazy { HOLDER.INSTANCE }
    }


    override fun calcRegion(s: Vector2D, e: Vector2D, res: Double) : WritableImage
    {
        val region = WritableImage(e.x.toInt(), e.y.toInt())
        val c: Color = Color.WHITE
        val c2: Color = Color.ROYALBLUE

        val internalRes : Double = res
        val internalResPos = Vector2D(s.x,s.y)
        val pixelPos = Vector2D(0.0,0.0)


        while (pixelPos.y < e.y.toInt())
        {
            while (pixelPos.x < e.x.toInt())
            {

                val pixelResult = calcPixel(internalResPos)
                if(pixelResult>max)
                    println(pixelResult)
                var pixelColor: Color
                pixelColor = c
                if(pixelResult == -1.0)
                {
                    pixelColor = c2
                }
                if(pixelResult/(max+1) in 0.0..1.0)
                {
                    pixelColor = Color.hsb(ln(pixelResult/(max+1)), (pixelResult / (max + 1)), 1.0)
                }

                region.pixelWriter.setColor(pixelPos.x.toInt(), pixelPos.y.toInt(), pixelColor)

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

    override fun calcPixel(pixel: Vector2D) : Double
    {
        var res = Complex(pixel.x, pixel.y)
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
        return Color.ALICEBLUE
    }
}