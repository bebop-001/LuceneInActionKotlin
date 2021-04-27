@file:Suppress("MemberVisibilityCanBePrivate")

package index_by_line

// Mostly from Chapter_03/BasicSearchingTest.

import index_by_line.Common.INDEX_DIR
import org.apache.lucene.document.FieldSelector
import org.apache.lucene.document.FieldSelectorResult
import org.apache.lucene.index.IndexReader
import org.apache.lucene.index.Term
import org.apache.lucene.search.*
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

    fun booleanQuerySearch(queryString: String) : List<Int> {
        val rv = mutableListOf<Int>()
        val termQuery = TermQuery(
            Term("text", queryString)
        )
        val indexQuery = NumericRangeQuery.newIntRange(
            "index",
            300, 350, true, true
        )
        val booleanQuery = BooleanQuery()
        booleanQuery.add(termQuery, BooleanClause.Occur.MUST)
        // booleanQuery.add(indexQuery, BooleanClause.Occur.MUST)
        val indexReader = IndexReader.open(searcherDir)
        val searcher = IndexSearcher(indexReader, null)
        val topDocs = searcher.search(booleanQuery,100)
        println("${topDocs.totalHits} matches")
        var idx = 1
        topDocs.scoreDocs.forEachIndexed{ idx, scoreDoc ->
            val  doc = searcher.doc(scoreDoc.doc)
            val fileIndex = doc.get("index").toInt()
            println("$idx ------- booleanQuerySearch $fileIndex --------")
            rv.add(fileIndex)
            println(searcher.explain(booleanQuery, scoreDoc.doc))
        }
        indexReader.close()
        searcher.close()
        return rv
    }




    fun search(queryString:String) : List<Int> {
        val indexReader = IndexReader.open(searcherDir)
        val rv = mutableListOf<Int>()
        indexReader.document(0, FieldSelector { FieldSelectorResult.LOAD })
        val searcher = IndexSearcher(indexReader)
        val term = Term("text", queryString)
        val query = TermQuery(term)
        val topDocs : TopDocs = searcher.search(query, 15)
        topDocs.scoreDocs.forEachIndexed{ idx, scoreDoc ->
            val  doc = searcher.doc(scoreDoc.doc)
            val fileIndex = doc.get("index").toInt()
            rv.add(fileIndex)
            println("$idx ------- $fileIndex --------")
            println(searcher.explain(query, scoreDoc.doc))
        }
        indexReader.close()
        searcher.close()
        return rv
    }
}