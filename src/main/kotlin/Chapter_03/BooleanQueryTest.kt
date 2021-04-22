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
 */
package Chapter_03

import junit.framework.TestCase
import org.apache.lucene.index.Term
import org.apache.lucene.search.*
import org.apache.lucene.store.Directory

// From chapter 3
class BooleanQueryTest : TestCase() {
    @Throws(Exception::class)
    fun testAnd() {
        val searchingBooks = TermQuery(Term("subject", "search")) //#1
        val books2010: Query =  //#2
            NumericRangeQuery.newIntRange(
                "pubmonth", 201001,  //#2
                201012,  //#2
                true, true
            ) //#2
        val searchingBooks2010 = BooleanQuery() //#3
        searchingBooks2010.add(searchingBooks, BooleanClause.Occur.MUST) //#3
        searchingBooks2010.add(books2010, BooleanClause.Occur.MUST) //#3
        val dir: Directory = TestUtil.bookIndexDirectory
        val searcher = IndexSearcher(dir)
        val matches = searcher.search(searchingBooks2010, 10)
        assertTrue(
            TestUtil.hitsIncludeTitle(
                searcher, matches,
                "Lucene in Action, Second Edition"
            )
        )
        searcher.close()
        dir.close()
    }

    /*
#1 Match books with subject “search”
#2 Match books in 2004
#3 Combines two queries
*/
    @Throws(Exception::class)
    fun testOr() {
        val methodologyBooks = TermQuery( // #1
            Term(
                "category",  // #1
                "/technology/computers/programming/methodology"
            )
        ) // #1
        val easternPhilosophyBooks = TermQuery( // #2
            Term(
                "category",  // #2
                "/philosophy/eastern"
            )
        ) // #2
        val enlightenmentBooks = BooleanQuery() // #3
        enlightenmentBooks.add(
            methodologyBooks,  // #3
            BooleanClause.Occur.SHOULD
        ) // #3
        enlightenmentBooks.add(
            easternPhilosophyBooks,  // #3
            BooleanClause.Occur.SHOULD
        ) // #3
        val dir: Directory = TestUtil.bookIndexDirectory
        val searcher = IndexSearcher(dir)
        val matches = searcher.search(enlightenmentBooks, 10)
        println("or = $enlightenmentBooks")
        assertTrue(
            TestUtil.hitsIncludeTitle(
                searcher, matches,
                "Extreme Programming Explained"
            )
        )
        assertTrue(
            TestUtil.hitsIncludeTitle(
                searcher, matches,
                "Tao Te Ching \u9053\u5FB7\u7D93"
            )
        )
        searcher.close()
        dir.close()
    } /*
#1 Match 1st category
#2 Match 2nd category
#3 Combine
   */
}