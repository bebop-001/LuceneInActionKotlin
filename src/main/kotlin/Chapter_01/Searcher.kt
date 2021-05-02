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

import org.apache.lucene.store.FSDirectory
import org.apache.lucene.queryParser.QueryParser
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.store.Directory
import org.apache.lucene.index.IndexReader
import org.apache.lucene.util.Version
import java.io.File
import java.lang.RuntimeException
import kotlin.system.exitProcess


// From chapter 1
/**
 * This code was originally written for
 * Erik's Lucene intro java.net article
 */
private object Searcher {
    fun search(indexDir: File, queryString: String) : Pair<String,String> {
        val dir: Directory = FSDirectory.open(indexDir) //3
        @Suppress("DEPRECATION")
        val indexSearcher = IndexSearcher(
            IndexReader.open(dir)) //3
        val parser = QueryParser(
            Version.LUCENE_30,  // 4
            "contents",  //4
            StandardAnalyzer( //4
                Version.LUCENE_30
            )
        ) //4
        try {
            val query = parser.parse(queryString) //4
            val start = System.currentTimeMillis()
            val hits = indexSearcher.search(query, 10) //5
            val end = System.currentTimeMillis()
            val matches: List<String> = hits.scoreDocs.map { scoreDoc ->
                indexSearcher.doc(scoreDoc.doc)!!["fullpath"]
            }
            indexSearcher.close() //9
            return arrayListOf(
                "${hits.totalHits} documents matched",
                "Query string = <$queryString>",
                "Query = <$query>",
                "Time = ${end - start} milliseconds",
                matches.joinToString("\n"),
            ).joinToString("\n") to ""
        }
        catch (e:Exception) {
            return "" to "Received Exception:\n\t" +
                e.toString().split(":\\s*".toRegex()).joinToString("\n\t") +
                "\n"
        }
    }
}
private const val USAGE = """USAGE:
    Boolean Operators: 
        AND, OR, NOT, () are supported
    Wildcards: 
        Can not begin or end string.
        "?": 1 of ane character, "*": 0 or more of any character
    Proximity:  "word_1 word_2"~N\" where N is word count between
        words.
    Similarity:
        word~N  where N is a value between 0.0 and 1.0.
        word~   N defaults to 0.5.
"""
fun main(args: Array<String>) {
    val indexDir = File(
        System.getenv("PWD") +
            "/indexes/${Searcher.javaClass.packageName}/indexerOut"
    )
    if (!indexDir.exists())
        throw RuntimeException("$indexDir doesn't exist and mkdir failed")
    println(USAGE)
    var query = ""
    while (query != "q") {
        print("Enter query or \"q\" to quit. > ")
        query = readLine()!!.trim()
        if (query == "q") {
            println("bye...")
            exitProcess(0)
        }
        val (matches, errors) = Searcher.search(indexDir, query)
        println (
            if (errors.isNotEmpty()) "$errors\n$USAGE\n"
            else matches
        )
    }
    val q = args[1] //2
    Searcher.search(indexDir, q)
}