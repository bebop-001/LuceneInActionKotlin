package index_by_line

import org.apache.lucene.search.TopDocs
import java.io.File
import java.lang.RuntimeException
import kotlin.system.exitProcess


fun main(args: Array<String>) {

    val simpleSearch = SimpleTermQuery(Common.INDEX_DIR)
    var query = ""
    while (query != "q") {
        print("Enter query or \"q\" to quit. > ")
        query = readLine()!!.trim()
        if (query == "q") {
            println("bye...")
            exitProcess(0)
        }
        val results : List<Int> = simpleSearch.booleanQuerySearch(query)
        // val results : List<Int> = simpleSearch.search(query)
        println("Results:$results")
        val csvFile = File("${System.getenv("PWD")}/data/text_files_indexed.csv")
        if (!csvFile.exists())
            throw RuntimeException("Main:Failed to find $csvFile")
        val bufferHandle = csvFile.inputStream().bufferedReader()
        var line = bufferHandle.readLine()
        var i = 1
        val rvMap = results.map{it to ""}.toMap().toMutableMap()
        while (line != null) {
            val l = line.split(":".toRegex(), 2)
            if (l.size == 2 && l[0].toIntOrNull() != null) {
                val lineNumber = l[0].toInt()
                if (results.contains(lineNumber)) {
                    rvMap[lineNumber] = l[1]
                }
            }
            line = bufferHandle.readLine()
        }
        results.forEachIndexed{ index, lineNumber ->
            println("%2d) %4d %s".format(index, lineNumber, rvMap[lineNumber]))
        }
    }
}