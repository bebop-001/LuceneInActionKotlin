package index_by_line

import java.lang.RuntimeException
import kotlin.system.exitProcess

import index_by_line.Common.DATA_DIR
import index_by_line.Common.INDEX_DIR
import index_by_line.Common.LUCENE_VERSION
import index_by_line.Common.ANALYZER
import org.apache.lucene.store.Directory
import java.io.File

class SimpleSearch(val dir: File) {
    // val indexDir = Directory(dir)
    init {
        if (!dir.exists())
            throw RuntimeException("$dir doesn't exist and mkdir failed")
    }

    fun search(query:String) {


    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val simpleSearch = SimpleSearch(INDEX_DIR)

            var query = ""
            while (query != "q") {
                print("Enter query or \"q\" to quit. > ")
                query = readLine()!!.trim()
                if (query == "q") {
                    println("bye...")
                    exitProcess(0)
                }
                query = ""
            }
            val q = args[1] //2
            simpleSearch.search(q)
        }
    }
}