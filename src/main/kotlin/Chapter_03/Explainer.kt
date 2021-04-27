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

import org.apache.lucene.analysis.SimpleAnalyzer
import org.apache.lucene.index.IndexReader
import org.apache.lucene.queryParser.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.ScoreDoc
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.util.Version
import java.io.File
import java.lang.RuntimeException
import kotlin.system.exitProcess

// From chapter 3
object Explainer {
    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val indexDir = File(
            "${System.getenv("PWD")}/data/ExampleBookIndex"
        )
        if (!indexDir.exists())
            throw RuntimeException("Failed to find book index at $indexDir")
        val directory: Directory = FSDirectory.open(indexDir)
        while(true) {
            print("Input search term > ")
            val queryExpression = readLine()
            if (queryExpression.equals("q")) {
                println("bye...")
                exitProcess(0)
            }

            val parser = QueryParser(
                Version.LUCENE_36,
                "contents", SimpleAnalyzer(Version.LUCENE_36)
            )
            val query = parser.parse(queryExpression)
            println("Query: $queryExpression")
            val sd = IndexReader.open(directory)
            val searcher = IndexSearcher(sd)
            val topDocs = searcher.search(query, 10)
            for (match:ScoreDoc in topDocs.scoreDocs) {
                val explanation = searcher.explain(query, match.doc) //#A
                println("----------")
                val doc = searcher.doc(match.doc)
                println(doc["title"])
                println(explanation.toString()) //#B
            }
            sd.close()
            searcher.close()
        }
    }
}