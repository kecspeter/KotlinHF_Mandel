class RegionData(
        var RegionStartPos: Vector2D,        //MainScreenStartPos
        var RegionSize: Vector2D,       //ChunkStartPos
        var InnerStartPos: Vector2D,           //ChunkSize
        var InnerZoom: Double,        //Mandel/Julia-SetStartPos
        var type: Int,              //current zoom to offset the position and get iterator in region
        var screen: ScreenImageHandler,                      //Mandelbrot or Julia
        var frameTime: Long                 //time to render Region){}
)
