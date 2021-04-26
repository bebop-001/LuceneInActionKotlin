@file:Suppress("MemberVisibilityCanBePrivate")

package index_by_line

// Mostly from Chapter_03/BasicSearchingTest.

import index_by_line.Common.INDEX_DIR
import org.apache.lucene.document.Document
import org.apache.lucene.document.FieldSelector
import org.apache.lucene.document.FieldSelectorResult
import org.apache.lucene.index.IndexReader
import org.apache.lucene.index.Term
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.search.TermQuery
import org.apache.lucene.search.TopDocs
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory
import java.io.File
import kotlin.system.exitProcess

class SimpleTermQuery(val dir: File) {
    // val indexDir = Directory(dir)
    val searcherDir:Directory
        get() = FSDirectory.open(dir)
    init {
        if (!dir.exists())
            throw RuntimeException("$dir doesn't exist and mkdir failed")
    }

    fun search(queryString:String) : TopDocs {
        val indexReader = IndexReader.open(searcherDir)
        indexReader.document(0, FieldSelector { FieldSelectorResult.LOAD })
        val searcher = IndexSearcher(indexReader)
        val term = Term("text", queryString)
        val query = TermQuery(term)
        val topDocs : TopDocs = searcher.search(query, 15)
        topDocs.scoreDocs.forEachIndexed{ idx, scoreDoc ->
            println("---------------")
            /*
            indexReader.document(scoreDoc.doc, {FieldSelectorResult.LOAD})
            val doc = searcher.doc(scoreDoc.doc)
            println("fName:${doc["fName"]}, index:${doc["index"]}")
            */
            println(searcher.explain(query, scoreDoc.doc))
        }
        indexReader.close()
        searcher.close()
        return topDocs
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {

            val simpleSearch = SimpleTermQuery(INDEX_DIR)
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
                val results :TopDocs = simpleSearch.search(query)
            }
        }
    }
}