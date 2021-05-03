/*
 * Copyright sjs@kana-tutor.com 2021
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
@file:Suppress("MemberVisibilityCanBePrivate")

package text_file_csv.query_parser

import org.apache.lucene.analysis.SimpleAnalyzer
import org.apache.lucene.analysis.WhitespaceAnalyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.index.IndexReader
import org.apache.lucene.queryParser.QueryParser
import org.apache.lucene.search.*
import org.apache.lucene.store.FSDirectory
import java.io.File


class CsvSearcher(at: AnalyzerType, val indexDir: File) {
    private val analyzer = when (at) {
        AnalyzerType.simple -> SimpleAnalyzer(Common.LUCENE_VERSION)
        AnalyzerType.standard -> StandardAnalyzer(Common.LUCENE_VERSION)
        AnalyzerType.white_space -> WhitespaceAnalyzer(Common.LUCENE_VERSION)
    }
    // on fail, string will be set and list will be null.
    // on success, string is null and list is set.
    fun search(queryString:String) : Pair<String?, List<Pair<String, Int>>?> {
        var rv : Pair<String?, List<Pair<String, Int>>?>
        try {
            val indexReader = IndexReader.open(
                FSDirectory.open(indexDir)
            )
            val indexSearcher = IndexSearcher(indexReader)
            val parser = QueryParser(
                Common.LUCENE_VERSION, "text",analyzer)
            val query = parser.parse(queryString)
            println("Query String: <$queryString>, Query:<$query>")
            val matches = mutableListOf<Pair<String, Int>>()
            val topDocs: TopDocs = indexSearcher.search(
                query, 200
            )
            topDocs.scoreDocs.forEachIndexed { idx, scoreDoc ->
                val doc = indexSearcher.doc(scoreDoc.doc)
                val lineNumber = doc.get("line_number").toInt()
                val fName = doc.get("file_name")
                matches.add(Pair(fName, lineNumber))
                if (EXAMINE) {
                    println("$idx $fName $lineNumber ---------------")
                    println(indexSearcher.explain(query, scoreDoc.doc))
                }
            }
            indexSearcher.close()
            indexReader.close()
            rv = Pair(null, matches)
        }
        catch (e:Exception) {
            val errorString = "Received Exception:\n\t" +
                e.toString().split(":\\s*".toRegex()).joinToString("\n\t") +
                "\n"
            rv = Pair(errorString, null)
        }
        return rv
    }
}
