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

import junit.framework.TestCase
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.Term
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.search.TermQuery
import org.apache.lucene.store.Directory
import org.apache.lucene.store.RAMDirectory
import org.apache.lucene.util.Version

// From chapter 3
class NearRealTimeTest : TestCase() {
    @Throws(Exception::class)
    fun testNearRealTime() {
        val dir: Directory = RAMDirectory()
        val writer = IndexWriter(dir, StandardAnalyzer(Version.LUCENE_30), IndexWriter.MaxFieldLength.UNLIMITED)
        for (i in 0..9) {
            val doc = Document()
            doc.add(Field("id", "" + i, Field.Store.NO, Field.Index.NOT_ANALYZED_NO_NORMS))
            doc.add(Field("text", "aaa", Field.Store.NO, Field.Index.ANALYZED))
            writer.addDocument(doc)
        }
        val reader = writer.reader // #1
        var searcher = IndexSearcher(reader) // #A
        var query: Query = TermQuery(Term("text", "aaa"))
        val docs = searcher.search(query, 1)
        assertEquals(10, docs.totalHits) // #B
        writer.deleteDocuments(Term("id", "7")) // #2
        val doc = Document() // #3
        doc.add(
            Field(
                "id",  // #3
                "11",  // #3
                Field.Store.NO,  // #3
                Field.Index.NOT_ANALYZED_NO_NORMS
            )
        ) // #3
        doc.add(
            Field(
                "text",  // #3
                "bbb",  // #3
                Field.Store.NO,  // #3
                Field.Index.ANALYZED
            )
        ) // #3
        writer.addDocument(doc) // #3
        val newReader = reader.reopen() // #4
        assertFalse(reader === newReader) // #5
        reader.close() // #6
        searcher = IndexSearcher(newReader)
        var hits = searcher.search(query, 10) // #7
        assertEquals(9, hits.totalHits) // #7
        query = TermQuery(Term("text", "bbb")) // #8
        hits = searcher.search(query, 1) // #8
        assertEquals(1, hits.totalHits) // #8
        newReader.close()
        writer.close()
    }
}