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

import Chapter_03.TestUtil.bookIndexDirectory
import junit.framework.TestCase
import org.apache.lucene.analysis.SimpleAnalyzer
import org.apache.lucene.index.Term
import org.apache.lucene.queryParser.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.search.TermQuery
import org.apache.lucene.util.Version

// From chapter 3
class BasicSearchingTest : TestCase() {
    @Throws(Exception::class)
    fun testTerm() {
        val dir = bookIndexDirectory //A
        val searcher = IndexSearcher(dir) //B
        var t = Term("subject", "ant")
        val query: Query = TermQuery(t)
        var docs = searcher.search(query, 10)
        assertEquals(
            "Ant in Action",  //C
            1, docs.totalHits
        ) //C
        t = Term("subject", "junit")
        docs = searcher.search(TermQuery(t), 10)
        assertEquals(
            "Ant in Action, " +  //D
                "JUnit in Action, Second Edition",  //D
            2, docs.totalHits
        ) //D
        searcher.close()
        dir.close()
    }

    /*
    #A Obtain directory from TestUtil
    #B Create IndexSearcher
    #C Confirm one hit for "ant"
    #D Confirm two hits for "junit"
  */
    @Throws(Exception::class)
    fun testKeyword() {
        val dir = bookIndexDirectory
        val searcher = IndexSearcher(dir)
        val t = Term("isbn", "9781935182023")
        val query: Query = TermQuery(t)
        val docs = searcher.search(query, 10)
        assertEquals(
            "JUnit in Action, Second Edition",
            1, docs.totalHits
        )
        searcher.close()
        dir.close()
    }

    @Throws(Exception::class)
    fun testQueryParser() {
        val dir = bookIndexDirectory
        val searcher = IndexSearcher(dir)
        val parser = QueryParser(
            Version.LUCENE_30,  //A
            "contents",  //A
            SimpleAnalyzer()
        ) //A
        var query = parser.parse("+JUNIT +ANT -MOCK") //B
        var docs = searcher.search(query, 10)
        assertEquals(1, docs.totalHits)
        val d = searcher.doc(docs.scoreDocs[0].doc)
        assertEquals("Ant in Action", d["title"])
        query = parser.parse("mock OR junit") //B
        docs = searcher.search(query, 10)
        assertEquals(
            "Ant in Action, " +
                "JUnit in Action, Second Edition",
            2, docs.totalHits
        )
        searcher.close()
        dir.close()
    } /*
#A Create QueryParser
#B Parse user's text
  */
}