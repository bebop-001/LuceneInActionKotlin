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
 *
 * Adapted from code which first appeared in a java.net article
 * written by Erik
 */
package Chapter_04

import Chapter_04.AnalyzerUtils.displayTokens
import org.apache.lucene.analysis.WhitespaceAnalyzer
import org.apache.lucene.analysis.SimpleAnalyzer
import org.apache.lucene.analysis.StopAnalyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.util.Version
import java.io.StringReader

// From chapter 4.01
object AnalyzerDemo {
    private val LUCENE_VERSION = Version.LUCENE_36
    private fun displayTokens(
        analyzer: Analyzer,
        text: String
    ) = displayTokens(analyzer.tokenStream(
        "contents", StringReader(text))
    ) //A

    private val examples = arrayOf(
        "The quick brown fox jumped over the lazy dog",
        "XY&Z Corporation - xyz@example.com"
    )
    private val analyzers = arrayOf<Analyzer>(
        WhitespaceAnalyzer(LUCENE_VERSION),
        SimpleAnalyzer(LUCENE_VERSION),
        StopAnalyzer(LUCENE_VERSION),
        StandardAnalyzer(LUCENE_VERSION)
    )
    private fun analyze(text: String) {
        println("Analyzing \"$text\"")
        for (analyzer in analyzers) {
            println("${analyzer.javaClass.simpleName}\n    ")
            displayTokens(analyzer, text) // B
            println("\n")
        }
    }
    @JvmStatic
    fun main(args: Array<String>) {
        val strings =   if (args.size > 0) args
                        else examples
        strings.forEach{text -> analyze(text) }
    }
}