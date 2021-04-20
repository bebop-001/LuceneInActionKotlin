package Chapter_02.lia.indexing

import Chapter_02.lia.common.hitCount
import junit.framework.TestCase
import org.apache.lucene.analysis.LimitTokenCountAnalyzer
import org.apache.lucene.analysis.WhitespaceAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.index.IndexReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.Term
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.search.TermQuery
import org.apache.lucene.store.Directory
import org.apache.lucene.store.RAMDirectory
import java.io.IOException

/**
 * Copyright Manning Publications Co.
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
 */ // From chapter 2
class IndexingTest : TestCase() {
    private var ids = arrayOf("1", "2")
    private var unindexed = arrayOf("Netherlands", "Italy")
    private var unstored = arrayOf(
        "Amsterdam has lots of bridges",
        "Venice has lots of canals"
    )
    private var text = arrayOf("Amsterdam", "Venice")
    private var directory: Directory? = null
    @Throws(Exception::class)
    override fun setUp() {     //1
        directory = RAMDirectory()
        val writer = writer //2
        for (i in ids.indices) {      //3
            val doc = Document()
            doc.add(
                Field(
                    "id", ids[i],
                    Field.Store.YES,
                    Field.Index.NOT_ANALYZED
                )
            )
            doc.add(
                Field(
                    "country", unindexed[i],
                    Field.Store.YES,
                    Field.Index.NO
                )
            )
            doc.add(
                Field(
                    "contents", unstored[i],
                    Field.Store.NO,
                    Field.Index.ANALYZED
                )
            )
            doc.add(
                Field(
                    "city", text[i],
                    Field.Store.YES,
                    Field.Index.ANALYZED
                )
            )
            writer.addDocument(doc)
        }
        writer.close()
    }// 2

    // 2
    // 2
    @get:Throws(IOException::class)
    private val writer: IndexWriter
        get() =// 2
            IndexWriter(
                directory,
                // use writerconfig instead of directly accessing
                // the analyzer.
                IndexWriterConfig(
                    LUCENE_VERSION, WhitespaceAnalyzer(LUCENE_VERSION)
                )
            )  // 2

    @Throws(IOException::class)
    private fun getHitCount(fieldName: String?, searchString: String?): Int {
        // use IndexReader.open instead of passing directly directly.
        val reader = IndexReader.open(directory)
        val searcher = IndexSearcher(reader) //4
        val t = Term(fieldName, searchString)
        val query: Query = TermQuery(t) //5
        val hitCount = hitCount(searcher, query) //6
        reader.close()
        return hitCount
    }

    @Throws(IOException::class)
    fun testIndexWriter() {
        assertEquals(ids.size, writer.numDocs()) //7
        writer.close()
    }

    @Throws(IOException::class)
    fun testIndexReader() {
        val reader = IndexReader.open(directory)
        assertEquals(ids.size, reader.maxDoc()) //8
        assertEquals(ids.size, reader.numDocs()) //8
        reader.close()
    }

    /*
    #1 Run before every test
    #2 Create IndexWriter
    #3 Add documents
    #4 Create new searcher
    #5 Build simple single-term query
    #6 Get number of hits
    #7 Verify writer document count
    #8 Verify reader document count
  */
    @Throws(IOException::class)
    fun testDeleteBeforeOptimize() {
        assertEquals(2, writer.numDocs()) //A
        writer.deleteDocuments(Term("id", "1")) //B
        writer.commit()
        assertTrue(writer.hasDeletions()) //1
        assertEquals(2, writer.maxDoc()) //2
        assertEquals(1, writer.numDocs()) //2   
        writer.close()
    }

    @Throws(IOException::class)
    fun testDeleteAfterOptimize() {
        assertEquals(2, writer.numDocs())
        writer.deleteDocuments(Term("id", "1"))
        // writer.optimize() //3
        writer.commit()
        assertFalse(writer.hasDeletions())
        assertEquals(1, writer.maxDoc()) //C
        assertEquals(1, writer.numDocs()) //C    
        writer.close()
    }

    /*
    #A 2 docs in the index
    #B Delete first document
    #C 1 indexed document, 0 deleted documents
    #1 Index contains deletions
    #2 1 indexed document, 1 deleted document
    #3 Optimize compacts deletes
  */
    @Throws(IOException::class)
    fun testUpdate() {
        assertEquals(1, getHitCount("city", "Amsterdam"))
        val doc = Document() //A
        doc.add(
            Field(
                "id", "1",
                Field.Store.YES,
                Field.Index.NOT_ANALYZED
            )
        ) //A
        doc.add(
            Field(
                "country", "Netherlands",
                Field.Store.YES,
                Field.Index.NO
            )
        ) //A  
        doc.add(
            Field(
                "contents",
                "Den Haag has a lot of museums",
                Field.Store.NO,
                Field.Index.ANALYZED
            )
        ) //A
        doc.add(
            Field(
                "city", "Den Haag",
                Field.Store.YES,
                Field.Index.ANALYZED
            )
        ) //A
        writer.updateDocument(
            Term("id", "1"),  //B
            doc
        ) //B
        writer.close()
        assertEquals(0, getHitCount("city", "Amsterdam")) //C   
        assertEquals(1, getHitCount("city", "Haag")) //D  
    }

    /*
    #A Create new document with "Haag" in city field
    #B Replace original document with new version
    #C Verify old document is gone
    #D Verify new document is indexed
  */
    @Throws(IOException::class)
    fun testMaxFieldLength() {
        assertEquals(1, getHitCount("contents", "bridges"))
        val writer = IndexWriter(
            directory, IndexWriterConfig(LUCENE_VERSION,
                // use this for MaxFieldLength deprecation.
                LimitTokenCountAnalyzer(WhitespaceAnalyzer(LUCENE_VERSION), 1)
            )
        )  //2
        val doc = Document() // 3
        doc.add(
            Field(
                "contents",
                "these bridges can't be found",  // 3
                Field.Store.NO, Field.Index.ANALYZED
            )
        ) // 3
        writer.addDocument(doc) // 3
        writer.close() // 3
        assertEquals(1, getHitCount("contents", "bridges")) //4
    } /*
    #1 One initial document has bridges
    #2 Create writer with maxFieldLength 1
    #3 Index document with bridges
    #4 Document can't be found
  */
}