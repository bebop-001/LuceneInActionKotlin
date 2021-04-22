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
import org.apache.lucene.search.TermRangeQuery

// From chapter 3
class TermRangeQueryTest : TestCase() {
    @Throws(Exception::class)
    fun testTermRangeQuery() {
        val dir = bookIndexDirectory
        val searcher = IndexSearcher(dir)
        val query = TermRangeQuery("title2", "d", "j", true, true)
        val matches = searcher.search(query, 100)
        /*
    for(int i=0;i<matches.totalHits;i++) {
      System.out.println("match " + i + ": " + searcher.doc(matches.scoreDocs[i].doc).get("title2"));
    }
    */assertEquals(3, matches.totalHits)
        searcher.close()
        dir.close()
    }
}