import java.io.File

class BenchmarkWriter
{
    private var testData = mutableListOf<String>()

    fun writeOut(name: String)
    {
        File("$name.txt").bufferedWriter().use { out ->
            testData.forEach {
                out.write("${it}\n")
            }
            //out.close()
        }
    }

    private fun prepareData(data: MutableList<Long>) : String
    {
        var line = ""
        for (frameTime in data)
        {
            line += "$frameTime\t"
        }
        return line
    }
    fun testSession(data: MutableList<Long>, sessionID: String)
    {
        testData.add("$sessionID\t${prepareData(data)}")
    }
}