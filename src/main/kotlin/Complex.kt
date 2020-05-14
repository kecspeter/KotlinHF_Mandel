class Complex(private var re: Double, private var im: Double) {

    fun dist(): Double
    {
        return re*re-im*im
    }

    fun mul(): Complex
    {
        return Complex(re*re-im*im, re*im+im*re)
    }

    fun sum(c: Complex): Complex
    {
        return Complex(re + c.re, im + c.im)
    }

}