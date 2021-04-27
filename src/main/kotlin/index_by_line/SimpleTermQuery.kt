/*
 * Copyright sjs@kana-tutor.com 2021
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific lan
 */
@file:Suppress("MemberVisibilityCanBePrivate")

package index_by_line

import org.apache.lucene.document.FieldSelector
import org.apache.lucene.document.FieldSelectorResult
import org.apache.lucene.index.IndexReader
import org.apache.lucene.index.Term
import org.apache.lucene.search.*
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory
import java.io.File

class SimpleTermQuery(val indexDir: File) {
    // val indexDir = Directory(dir)
    val searcherDir:Directory
        get() = FSDirectory.open(indexDir)
    init {
        if (!indexDir.exists())
            throw RuntimeException("${javaClass.simpleName}: index directory$indexDir")
    }

    /*
    fun booleanQuerySearch(queryString: String) : List<Pair<String,Int>> {
        val rv = mutableListOf<Pair<String,Int>>()
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
            val fileIndex = doc.get("line_number").toInt()
            println("$idx ------- booleanQuerySearch $fileIndex --------")
            rv.add(fileIndex)
            println(searcher.explain(booleanQuery, scoreDoc.doc))
        }
        indexReader.close()
        searcher.close()
        return rv
    }
*/



    fun search(queryString:String) : List<Pair<String,Int>> {
        val indexReader = IndexReader.open(searcherDir)
        val rv = mutableListOf<Pair<String,Int>>()
        indexReader.document(0, FieldSelector { FieldSelectorResult.LOAD })
        val searcher = IndexSearcher(indexReader)
        val term = Term("text", queryString.toLowerCase())
        val query = TermQuery(term)
        val topDocs : TopDocs = searcher.search(query, 200)
        topDocs.scoreDocs.forEachIndexed{ idx, scoreDoc ->
            val  doc = searcher.doc(scoreDoc.doc)
            val lineNumber = doc.get("line_number").toInt()
            val fName = doc.get("file_name")
            rv.add(Pair(fName,lineNumber))
            println("$idx $fName $lineNumber ---------------")
            println(searcher.explain(query, scoreDoc.doc))
        }
        indexReader.close()
        searcher.close()
        return rv
    }
}
