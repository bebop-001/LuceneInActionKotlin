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
import org.apache.lucene.index.Term
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.PrefixQuery
import org.apache.lucene.search.TermQuery

// From chapter 3
class PrefixQueryTest : TestCase() {
    @Throws(Exception::class)
    fun testPrefix() {
        val dir = bookIndexDirectory
        val searcher = IndexSearcher(dir)
        val term = Term(
            "category",  //#A
            "/technology/computers/programming"
        ) //#A
        val query = PrefixQuery(term) //#A
        var matches = searcher.search(query, 10) //#A
        val programmingAndBelow = matches.totalHits
        matches = searcher.search(TermQuery(term), 10) //#B
        val justProgramming = matches.totalHits
        assertTrue(programmingAndBelow > justProgramming)
        searcher.close()
        dir.close()
    }
}