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

import org.apache.lucene.search.Filter
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.search.TopDocs
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory
import java.io.File
import java.io.IOException

internal object TestUtil {
    @Throws(IOException::class)
    fun hitsIncludeTitle(searcher: IndexSearcher, hits: TopDocs, title: String): Boolean {
        for (match in hits.scoreDocs) {
            val doc = searcher.doc(match.doc)
            if (title == doc["title"]) {
                return true
            }
        }
        println("title '$title' not found")
        return false
    }

    @Throws(IOException::class)
    fun hitCount(searcher: IndexSearcher, query: Query?): Int {
        return searcher.search(query, 1).totalHits
    }

    @Throws(IOException::class)
    fun hitCount(searcher: IndexSearcher, query: Query?, filter: Filter?): Int {
        return searcher.search(query, filter, 1).totalHits
    }

    @Throws(IOException::class)
    fun dumpHits(searcher: IndexSearcher, hits: TopDocs) {
        if (hits.totalHits == 0) {
            println("No hits")
        }
        for (match in hits.scoreDocs) {
            val doc = searcher.doc(match.doc)
            println(match.score.toString() + ":" + doc["title"])
        }
    }

    // The build.xml ant script sets this property for us:
    @get:Throws(IOException::class)
    val bookIndexDirectory: Directory
        get() =// The build.xml ant script sets this property for us:
            FSDirectory.open(File(System.getProperty("index.dir")))

    @Throws(IOException::class)
    fun rmDir(dir: File) {
        if (dir.exists()) {
            val files = dir.listFiles()
            for (i in files.indices) {
                if (!files[i].delete()) {
                    throw IOException("could not delete " + files[i])
                }
            }
            dir.delete()
        }
    }
}