package data

class Complex(private var re: Double, private var im: Double) {

    fun dist(): Double
    {
        return re*re-im*im
    }

    operator fun plus(c: Complex): Complex
    {
        return Complex(re + c.re, im + c.im)
    }

    operator fun times(c: Complex): Complex
    {
        return Complex(re * c.re - im * c.im, re * c.im + im * c.re)
    }


}