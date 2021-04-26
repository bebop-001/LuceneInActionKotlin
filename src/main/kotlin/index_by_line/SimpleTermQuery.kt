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

    class mfs : FieldSelector
    {
        override fun accept(fieldName: String?): FieldSelectorResult {
            return FieldSelectorResult.LOAD
        }
    }
    val fieldInfo = mutableMapOf<String,MutableList<Int>>()
    val fileNames = mutableListOf<String>()
    fun search(query:String) : TopDocs {
        val sd = searcherDir
        val ir = DirectoryReader.open
        val searcher = IndexSearcher(
            IndexReader.open(sd))
        val term = Term("text", query)
        val t2 = Term("index", query)
        val q2 = TermQuery(t2)
        val r2 = searcher.search(q2,15)
        val q : Query = TermQuery(term)
        val results = searcher.search(
            q, 15
        )
        if (fieldInfo.isEmpty()) {
            (0 until searcher.maxDoc()).forEach { docId ->
                val doc = searcher.doc(docId)
                FieldSelectorResult.LOAD
                val f = searcher.doc(docId).get("index")
                val n = searcher.doc(docId).get("fName")
                val fields = searcher.doc(docId).fields.toMutableList()
                var fName:String? = null
                var idxx:Int? = null
                for (field in fields) {
                    val name = field.name()
                    if (name == "fName") {
                        if (fName != field.stringValue()) {
                            fName = field.stringValue()
                            fieldInfo[fName] = mutableListOf()
                            fileNames.add(fName)
                        }
                    }
                    else if (name == "index")
                        idxx = field.stringValue().toInt()
                    else throw RuntimeException(
                        "${javaClass.simpleName}:search:bad field name: ${field.name()}"
                    )
                    if (fName != null && idxx != null) {
                        fieldInfo[fName]!!.add(idxx)
                        fName = null; idxx = null
                    }
                }
            }
        }
        println("${results.totalHits} matches for $query")
        results.scoreDocs.forEachIndexed{ idxx, scoreDoc ->
            val docIdx = scoreDoc.doc
            val x = searcher.explain(q, docIdx)
            val d : Document = searcher.doc(docIdx)
            val fName = fileNames[docIdx]
            val index = d.getField("index").stringValue().toInt()
            println("%2d) %d:%s:%d = %s".format(idxx + 1, docIdx, fName, index, x))
        }
        searcher.close()
        return results
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