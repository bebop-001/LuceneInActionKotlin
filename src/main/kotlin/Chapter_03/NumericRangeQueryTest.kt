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
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.NumericRangeQuery

// From chapter 3
class NumericRangeQueryTest : TestCase() {
    @Throws(Exception::class)
    fun testInclusive() {
        val dir = bookIndexDirectory
        val searcher = IndexSearcher(dir)
        // pub date of TTC was September 2006
        val query: NumericRangeQuery<*> = NumericRangeQuery.newIntRange(
            "pubmonth",
            200605,
            200609,
            true,
            true
        )
        val matches = searcher.search(query, 10)
        /*
    for(int i=0;i<matches.totalHits;i++) {
      System.out.println("match " + i + ": " + searcher.doc(matches.scoreDocs[i].doc).get("author"));
    }
    */assertEquals(1, matches.totalHits)
        searcher.close()
        dir.close()
    }

    @Throws(Exception::class)
    fun testExclusive() {
        val dir = bookIndexDirectory
        val searcher = IndexSearcher(dir)

        // pub date of TTC was September 2006
        val query: NumericRangeQuery<*> = NumericRangeQuery.newIntRange(
            "pubmonth",
            200605,
            200609,
            false,
            false
        )
        val matches = searcher.search(query, 10)
        assertEquals(0, matches.totalHits)
        searcher.close()
        dir.close()
    }
}