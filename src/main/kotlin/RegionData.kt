class RegionData(
        var ImageStartPos: Vector2D,        //MainScreenStartPos
        var RegionStartPos: Vector2D,       //ChunkStartPos
        var RegionSize: Vector2D,           //ChunkSize
        var InnerStartPos: Vector2D,        //Mandel/Julia-SetStartPos
        var InnerZoom: Double,              //current zoom to offset the position and get iterator in region
        var type: Int,                      //Mandelbrot or Julia
        var screen: ScreenImageHandler,     //the source screen, to copy the region
        var frameTime: Long                 //time to render Region
    )
{
}