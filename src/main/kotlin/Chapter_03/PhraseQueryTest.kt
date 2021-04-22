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
import org.apache.lucene.analysis.WhitespaceAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.Term
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.PhraseQuery
import org.apache.lucene.store.Directory
import org.apache.lucene.store.RAMDirectory
import java.io.IOException

// From chapter 3
class PhraseQueryTest : TestCase() {
    private var dir: Directory? = null
    private var searcher: IndexSearcher? = null
    @Throws(IOException::class)
    override fun setUp() {
        dir = RAMDirectory()
        val writer = IndexWriter(
            dir,
            WhitespaceAnalyzer(),
            IndexWriter.MaxFieldLength.UNLIMITED
        )
        val doc = Document()
        doc.add(
            Field(
                "field",  // 1
                "the quick brown fox jumped over the lazy dog",  // 1
                Field.Store.YES,  // 1
                Field.Index.ANALYZED
            )
        ) // 1
        writer.addDocument(doc)
        writer.close()
        searcher = IndexSearcher(dir)
    }

    @Throws(IOException::class)
    override fun tearDown() {
        searcher!!.close()
        dir!!.close()
    }

    @Throws(IOException::class)
    private fun matched(phrase: Array<String>, slop: Int): Boolean {
        val query = PhraseQuery() // 2
        query.slop = slop // 2
        for (word in phrase) {             // 3
            query.add(Term("field", word)) // 3
        } // 3
        val matches = searcher!!.search(query, 10)
        return matches.totalHits > 0
    }

    /*
    #1 Add a single test document
    #2 Create initial PhraseQuery
    #3 Add sequential phrase terms
   */
    @Throws(Exception::class)
    fun testSlopComparison() {
        val phrase = arrayOf("quick", "fox")
        assertFalse("exact phrase not found", matched(phrase, 0))
        assertTrue("close enough", matched(phrase, 1))
    }

    @Throws(Exception::class)
    fun testReverse() {
        val phrase = arrayOf("fox", "quick")
        assertFalse("hop flop", matched(phrase, 2))
        assertTrue("hop hop slop", matched(phrase, 3))
    }

    @Throws(Exception::class)
    fun testMultiple() {
        assertFalse(
            "not close enough",
            matched(arrayOf("quick", "jumped", "lazy"), 3)
        )
        assertTrue(
            "just enough",
            matched(arrayOf("quick", "jumped", "lazy"), 4)
        )
        assertFalse(
            "almost but not quite",
            matched(arrayOf("lazy", "jumped", "quick"), 7)
        )
        assertTrue(
            "bingo",
            matched(arrayOf("lazy", "jumped", "quick"), 8)
        )
    }
}