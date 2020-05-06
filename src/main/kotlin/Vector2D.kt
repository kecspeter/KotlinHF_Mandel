class Vector2D (var x: Double,var y: Double)
{

    //var x: Double
    //var y: Double

    /*
    constructor(x: Double, y: Double)
    {
        this.x = x
        this.y = y
    }
    */
    init
    {
        this.x = x
        this.y = y
    }

    operator fun minus(v: Vector2D): Vector2D
    {
        return Vector2D(this.x - v.x ,this.y - v.y)
    }

    operator fun div(d: Double): Vector2D
    {
        return Vector2D(this.x/d,this.y/d)
    }

    operator fun compareTo(v: Vector2D): Int
    {
        if(x < v.x && y < v.y)
            return 1;
        return -1;
    }

    operator fun plus(d: Double): Vector2D
    {
        return Vector2D(this.x + d, this.y + d)
    }

    operator fun plus(v: Vector2D): Vector2D
    {
        return Vector2D(this.x + v.x, this.y + v.y)
    }

    operator fun times(d: Double): Vector2D
    {
        return Vector2D(this.x*d, this.y*d)
    }

    override fun toString(): String
    {
        return "($x , $y)"
    }

}
