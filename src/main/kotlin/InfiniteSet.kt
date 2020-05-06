import javafx.scene.image.WritableImage
import javafx.scene.paint.Color

interface InfiniteSet
{
    fun calcRegion(s: Vector2D, e: Vector2D, res: Double) : WritableImage
    fun calcPixel(pixel: Vector2D) : Double
    fun colorPixel(pixelResult: Double) : Color
}