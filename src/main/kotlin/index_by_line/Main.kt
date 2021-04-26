package index_by_line

import org.apache.lucene.search.TopDocs
import kotlin.system.exitProcess


fun main(args: Array<String>) {

    val simpleSearch = SimpleTermQuery(Common.INDEX_DIR)
    val results = simpleSearch.search("public")
    println("results:$results")

    var query = ""
    while (query != "q") {
        print("Enter query or \"q\" to quit. > ")
        query = readLine()!!.trim()
        if (query == "q") {
            println("bye...")
            exitProcess(0)
        }
        val results : TopDocs = simpleSearch.search(query)
    }
}