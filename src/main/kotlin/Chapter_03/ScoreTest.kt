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
import org.apache.lucene.index.FieldInvertState
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.Term
import org.apache.lucene.search.*
import org.apache.lucene.store.Directory
import org.apache.lucene.store.RAMDirectory
import java.util.*

// From chapter 3
class ScoreTest : TestCase() {
    private var directory: Directory? = null
    @Throws(Exception::class)
    public override fun setUp() {
        directory = RAMDirectory()
    }

    @Throws(Exception::class)
    public override fun tearDown() {
        directory!!.close()
    }

    @Throws(Exception::class)
    fun testSimple() {
        indexSingleFieldDocs(arrayOf(Field("contents", "x", Field.Store.YES, Field.Index.ANALYZED)))
        val searcher = IndexSearcher(directory)
        searcher.similarity = SimpleSimilarity()
        val query: Query = TermQuery(Term("contents", "x"))
        val explanation = searcher.explain(query, 0)
        println(explanation)
        val matches = searcher.search(query, 10)
        assertEquals(1, matches.totalHits)
        assertEquals(1.0, matches.scoreDocs[0].score.toDouble(), 0.0)
        searcher.close()
    }

    @Throws(Exception::class)
    private fun indexSingleFieldDocs(fields: Array<Field>) {
        val writer = IndexWriter(
            directory,
            WhitespaceAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED
        )
        for (f in fields) {
            val doc = Document()
            doc.add(f)
            writer.addDocument(doc)
        }
        writer.optimize()
        writer.close()
    }

    @Throws(Exception::class)
    fun testWildcard() {
        indexSingleFieldDocs(
            arrayOf(
                Field("contents", "wild", Field.Store.YES, Field.Index.ANALYZED),
                Field("contents", "child", Field.Store.YES, Field.Index.ANALYZED),
                Field("contents", "mild", Field.Store.YES, Field.Index.ANALYZED),
                Field("contents", "mildew", Field.Store.YES, Field.Index.ANALYZED)
            )
        )
        val searcher = IndexSearcher(directory)
        val query: Query = WildcardQuery(Term("contents", "?ild*")) //#A
        val matches = searcher.search(query, 10)
        assertEquals("child no match", 3, matches.totalHits)
        assertEquals(
            "score the same", matches.scoreDocs[0].score.toDouble(),
            matches.scoreDocs[1].score.toDouble(), 0.0
        )
        assertEquals(
            "score the same", matches.scoreDocs[1].score.toDouble(),
            matches.scoreDocs[2].score.toDouble(), 0.0
        )
        searcher.close()
    }

    /*
    #A Construct WildcardQuery using Term
  */
    @Throws(Exception::class)
    fun testFuzzy() {
        indexSingleFieldDocs(
            arrayOf(
                Field(
                    "contents",
                    "fuzzy",
                    Field.Store.YES,
                    Field.Index.ANALYZED
                ),
                Field(
                    "contents",
                    "wuzzy",
                    Field.Store.YES,
                    Field.Index.ANALYZED
                )
            )
        )
        val searcher = IndexSearcher(directory)
        val query: Query = FuzzyQuery(Term("contents", "wuzza"))
        val matches = searcher.search(query, 10)
        assertEquals("both close enough", 2, matches.totalHits)
        assertTrue(
            "wuzzy closer than fuzzy",
            matches.scoreDocs[0].score != matches.scoreDocs[1].score
        )
        val doc = searcher.doc(matches.scoreDocs[0].doc)
        assertEquals("wuzza bear", "wuzzy", doc["contents"])
        searcher.close()
    }

    class SimpleSimilarity : Similarity() {
        override fun computeNorm(field: String, state: FieldInvertState): Float {
            return 0f
        }

        /*
    public float lengthNorm(String field, int numTerms) {
      return 1.0f;
    }
    */
        override fun queryNorm(sumOfSquaredWeights: Float): Float {
            return 1.0f
        }

        override fun tf(freq: Float): Float {
            return freq
        }

        override fun sloppyFreq(distance: Int): Float {
            return 2.0f
        }

        fun idf(terms: Vector<*>?, searcher: Searcher?): Float {
            return 1.0f
        }

        override fun idf(docFreq: Int, numDocs: Int): Float {
            return 1.0f
        }

        override fun coord(overlap: Int, maxOverlap: Int): Float {
            return 1.0f
        }
    }
}