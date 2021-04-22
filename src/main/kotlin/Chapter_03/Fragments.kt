/*
 * Copyright Manning Publications Co.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific lan      
 */
package Chapter_03

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.index.IndexReader
import org.apache.lucene.queryParser.QueryParser
import org.apache.lucene.search.*
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.util.Version
import java.io.File

// From chapter 3
class Fragments {
    @Throws(Exception::class)
    fun openSearcher() {
        val dir: Directory = FSDirectory.open(File("/path/to/index"))
        val reader = IndexReader.open(dir)
        val searcher = IndexSearcher(reader)
    }

    @Throws(Exception::class)
    fun nrtReader() {
        var reader: IndexReader? = null
        val searcher: IndexSearcher
        // START
        val newReader = reader!!.reopen()
        if (reader !== newReader) {
            reader.close()
            reader = newReader
            searcher = IndexSearcher(reader)
        }
        // END
    }

    @Throws(Exception::class)
    fun testSearchSigs() {
        val query: Query? = null
        val filter: Filter? = null
        var hits: TopDocs
        val fieldHits: TopFieldDocs
        val sort: Sort? = null
        val collector: Collector? = null
        val n = 10
        val searcher: IndexSearcher? = null
        hits = searcher!!.search(query, n)
        hits = searcher.search(query, filter, n)
        fieldHits = searcher.search(query, filter, n, sort)
        searcher.search(query, collector)
        searcher.search(query, filter, collector)
    }

    @Throws(Exception::class)
    fun queryParserOperator() {
        val analyzer: Analyzer? = null
        // START
        val parser = QueryParser(
            Version.LUCENE_30,
            "contents", analyzer
        )
        parser.defaultOperator = QueryParser.AND_OPERATOR
        // END
    }
}