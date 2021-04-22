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
import Chapter_03.TestUtil.hitsIncludeTitle
import junit.framework.TestCase
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.WhitespaceAnalyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.index.Term
import org.apache.lucene.queryParser.ParseException
import org.apache.lucene.queryParser.QueryParser
import org.apache.lucene.search.*
import org.apache.lucene.store.Directory
import org.apache.lucene.util.Version

// From chapter 3
class QueryParserTest : TestCase() {
    private var analyzer: Analyzer? = null
    private var dir: Directory? = null
    private var searcher: IndexSearcher? = null
    @Throws(Exception::class)
    override fun setUp() {
        analyzer = WhitespaceAnalyzer()
        dir = bookIndexDirectory
        searcher = IndexSearcher(dir)
    }

    @Throws(Exception::class)
    override fun tearDown() {
        searcher!!.close()
        dir!!.close()
    }

    @Throws(Exception::class)
    fun testToString() {
        val query = BooleanQuery()
        query.add(
            FuzzyQuery(Term("field", "kountry")),
            BooleanClause.Occur.MUST
        )
        query.add(
            TermQuery(Term("title", "western")),
            BooleanClause.Occur.SHOULD
        )
        assertEquals(
            "both kinds", "+kountry~0.5 title:western",
            query.toString("field")
        )
    }

    @Throws(Exception::class)
    fun testPrefixQuery() {
        val parser = QueryParser(
            Version.LUCENE_30,
            "category",
            StandardAnalyzer(Version.LUCENE_30)
        )
        parser.lowercaseExpandedTerms = false
        println(parser.parse("/Computers/technology*").toString("category"))
    }

    @Throws(Exception::class)
    fun testFuzzyQuery() {
        val parser = QueryParser(
            Version.LUCENE_30,
            "subject", analyzer
        )
        var query = parser.parse("kountry~")
        println("fuzzy: $query")
        query = parser.parse("kountry~0.7")
        println("fuzzy 2: $query")
    }

    @Throws(Exception::class)
    fun testGrouping() {
        val query = QueryParser(
            Version.LUCENE_30,
            "subject",
            analyzer
        ).parse("(agile OR extreme) AND methodology")
        val matches = searcher!!.search(query, 10)
        assertTrue(
            hitsIncludeTitle(
                searcher!!, matches,
                "Extreme Programming Explained"
            )
        )
        assertTrue(
            hitsIncludeTitle(
                searcher!!,
                matches,
                "The Pragmatic Programmer"
            )
        )
    }

    @Throws(Exception::class)
    fun testTermQuery() {
        val parser = QueryParser(
            Version.LUCENE_30,
            "subject", analyzer
        )
        val query = parser.parse("computers")
        println("term: $query")
    }

    @Throws(Exception::class)
    fun testTermRangeQuery() {
        var query = QueryParser(
            Version.LUCENE_30,  //A
            "subject", analyzer
        ).parse("title2:[Q TO V]") //A
        assertTrue(query is TermRangeQuery)
        var matches = searcher!!.search(query, 10)
        assertTrue(
            hitsIncludeTitle(
                searcher!!, matches!!,
                "Tapestry in Action"
            )
        )
        query = QueryParser(Version.LUCENE_30, "subject", analyzer) //B
            .parse("title2:{Q TO \"Tapestry in Action\"}") //B
        matches = searcher!!.search(query, 10)
        assertFalse(
            hitsIncludeTitle(
                searcher!!, matches,  // C
                "Tapestry in Action"
            )
        )
    }

    /*
    #A Verify inclusive range
    #B Verify exclusive range
    #C Exclude Mindstorms book
  */
    @Throws(Exception::class)
    fun testPhraseQuery() {
        var q = QueryParser(
            Version.LUCENE_30,
            "field",
            StandardAnalyzer(
                Version.LUCENE_30
            )
        )
            .parse("\"This is Some Phrase*\"")
        assertEquals(
            "analyzed",
            "\"? ? some phrase\"", q.toString("field")
        )
        q = QueryParser(
            Version.LUCENE_30,
            "field", analyzer
        ).parse("\"term\"")
        assertTrue("reduced to TermQuery", q is TermQuery)
    }

    @Throws(Exception::class)
    fun testSlop() {
        var q = QueryParser(
            Version.LUCENE_30,
            "field", analyzer
        )
            .parse("\"exact phrase\"")
        assertEquals(
            "zero slop",
            "\"exact phrase\"", q.toString("field")
        )
        val qp = QueryParser(
            Version.LUCENE_30,
            "field", analyzer
        )
        qp.phraseSlop = 5
        q = qp.parse("\"sloppy phrase\"")
        assertEquals(
            "sloppy, implicitly",
            "\"sloppy phrase\"~5", q.toString("field")
        )
    }

    @Throws(Exception::class)
    fun testLowercasing() {
        var q = QueryParser(
            Version.LUCENE_30,
            "field", analyzer
        ).parse("PrefixQuery*")
        assertEquals(
            "lowercased",
            "prefixquery*", q.toString("field")
        )
        val qp = QueryParser(
            Version.LUCENE_30,
            "field", analyzer
        )
        qp.lowercaseExpandedTerms = false
        q = qp.parse("PrefixQuery*")
        assertEquals(
            "not lowercased",
            "PrefixQuery*", q.toString("field")
        )
    }

    fun testWildcard() {
        try {
            QueryParser(
                Version.LUCENE_30,
                "field", analyzer
            ).parse("*xyz")
            fail("Leading wildcard character should not be allowed")
        }
        catch (expected: ParseException) {
            assertTrue(true)
        }
    }

    @Throws(Exception::class)
    fun testBoost() {
        val q = QueryParser(
            Version.LUCENE_30,
            "field", analyzer
        ).parse("term^2")
        assertEquals("term^2.0", q.toString("field"))
    }

    fun testParseException() {
        try {
            QueryParser(
                Version.LUCENE_30,
                "contents", analyzer
            ).parse("^&#")
        }
        catch (expected: ParseException) {
            // expression is invalid, as expected
            assertTrue(true)
            return
        }
        fail("ParseException expected, but not thrown")
    }
}