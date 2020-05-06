import kotlin.math.hypot

class Complex
{
    var Re: Double
    var Im: Double
    constructor(re: Double, im: Double)
    {
        Re = re
        Im = im
    }

    fun dist(): Double
    {
        //return hypot(Re, Im)
        return Re*Re-Im*Im

    }


    fun mul(): Complex
    {
        return Complex(Re*Re-Im*Im, Re*Im+Im*Re)
    }
    fun sum(c: Complex): Complex
    {
        return Complex(Re + c.Re, Im + c.Im)
    }

}