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
package Chapter_01

import java.lang.IllegalArgumentException
import java.io.IOException
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.queryParser.QueryParser
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.queryParser.ParseException
import org.apache.lucene.store.Directory
import org.apache.lucene.util.Version
import java.io.File
import java.lang.RuntimeException
import kotlin.system.exitProcess
import org.apache.lucene.search.IndexSearcher as IndexSearcher1


// From chapter 1
/**
 * This code was originally written for
 * Erik's Lucene intro java.net article
 */
object Searcher {
    @Throws(IllegalArgumentException::class, IOException::class, ParseException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val indexDir = File(
        "${System.getenv("PWD")}/indexes/${javaClass.packageName}/indexerOut"
        )
        if (!indexDir.exists())
            throw RuntimeException("$indexDir doesn't exist and mkdir failed")
        var query = ""
        while (query != "q") {
            print("Enter query or \"q\" to quit. > ")
            query = readLine()!!.trim()
            if (query == "q") {
                println("bye...")
                exitProcess(0)
            }
            println(search(indexDir, query))
            query = ""
        }
        val q = args[1] //2
        search(indexDir, q)
    }

    @Throws(IOException::class, ParseException::class)
    fun search(indexDir: File, queryString: String) : String {
        val dir: Directory = FSDirectory.open(indexDir) //3
        @Suppress("DEPRECATION")
        val indexSearcher = IndexSearcher1(dir) //3
        val parser = QueryParser(
            Version.LUCENE_30,  // 4
            "contents",  //4
            StandardAnalyzer( //4
                Version.LUCENE_30
            )
        ) //4
        val query = parser.parse(queryString) //4
        val start = System.currentTimeMillis()
        val hits = indexSearcher.search(query, 10) //5
        val end = System.currentTimeMillis()
        var rv ="Found " + hits.totalHits +  //6
            " document(s) (in " + (end - start) +  // 6
            " milliseconds) that matched query '" +  // 6
            queryString + "':" // 6
        for (scoreDoc in hits.scoreDocs) {
            val doc = indexSearcher.doc(scoreDoc.doc) //7
            rv += "\n${doc["fullpath"]}" //8
        }
        indexSearcher.close() //9
        return rv
    }
}