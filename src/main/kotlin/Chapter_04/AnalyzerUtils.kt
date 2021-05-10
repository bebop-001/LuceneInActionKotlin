@file:Suppress("unused")
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

package Chapter_04

import junit.framework.Assert
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.util.AttributeSource
import org.apache.lucene.analysis.SimpleAnalyzer
import org.apache.lucene.analysis.TokenStream
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.analysis.tokenattributes.*
import org.apache.lucene.util.Version
import java.io.StringReader

private val LUCENE_VERSION = Version.LUCENE_36
// From chapter 4
object AnalyzerUtils {
    fun displayTokens(analyzer: Analyzer, text: String) =
        displayTokens(analyzer.tokenStream("contents", StringReader(text))) //A

    fun displayTokens(stream: TokenStream) {
        val term = stream.addAttribute(CharTermAttribute::class.java)
        while (stream.incrementToken()) {
            print("[$term] ") //B
        }
    }

    /*
    #A Invoke analysis process
    #B Print token text surrounded by brackets
  */
    fun getPositionIncrement(source: AttributeSource): Int {
        return source.addAttribute(PositionIncrementAttribute::class.java)
                .positionIncrement
    }

    fun getTerm(source: AttributeSource): String {
        val attr = source.addAttribute(TermAttribute::class.java)
        return attr.term()
    }

    fun getType(source: AttributeSource): String {
        val attr = source.addAttribute(TypeAttribute::class.java)
        return attr.type()
    }

    fun setPositionIncrement(source: AttributeSource, posIncr: Int) {
        val attr = source.addAttribute(
            PositionIncrementAttribute::class.java
        )
        attr.positionIncrement = posIncr
    }

    fun setTerm(source: AttributeSource, term: String) {
        val attr = source.addAttribute(TermAttribute::class.java)
        attr.setTermBuffer(term)
    }

    fun setType(source: AttributeSource, type: String) {
        val attr = source.addAttribute(TypeAttribute::class.java)
        attr.setType(type)
    }

    fun displayTokensWithPositions(analyzer: Analyzer, text: String) {
        val stream = analyzer.tokenStream(
            "contents",
            StringReader(text)
        )
        val term = stream.addAttribute(CharTermAttribute::class.java)
        val posIncr = stream.addAttribute(
            PositionIncrementAttribute::class.java
        )
        var position = 0
        while (stream.incrementToken()) {
            val increment = posIncr.positionIncrement
            if (increment > 0) {
                position += increment
                println()
                print("$position: ")
            }
            print("[$term] ")
        }
        println()
    }

    fun displayTokensWithFullDetails(
        analyzer: Analyzer,
        text: String
    ) {
        val stream = analyzer.tokenStream(
            "contents",  // #A
            StringReader(text)
        )

        val sb = StringBuffer()
            .append("text=\"$text\"\n")
            .append("analyzer=${analyzer.javaClass.simpleName}\n")
        val term = stream.addAttribute(CharTermAttribute::class.java) // #B
        val posIncr =  // #B
            stream.addAttribute(PositionIncrementAttribute::class.java) // #B
        val offset = stream.addAttribute(OffsetAttribute::class.java) // #B
        val type = stream.addAttribute(TypeAttribute::class.java) // #B
        var position = 0
        while (stream.incrementToken()) {                                  // #C
            val increment = posIncr.positionIncrement // #D
            if (increment > 0) {                                            // #D
                position += increment // #D
                sb.append("position=$position\n") // #D
            }
            sb.append("\tterm\"$term\", type=\"${type.type()}\n")
                .append("\toffset: ${offset.startOffset()} to ${offset.endOffset()}\n")
            println(sb.toString())
        }
        println()
    }

    /*
    #A Perform analysis
    #B Obtain attributes of interest
    #C Iterate through all tokens
    #D Compute position and print
    #E Print all token details
   */
    fun assertAnalyzesTo(
        analyzer: Analyzer, input: String, output: Array<String?>
    ) {
        val stream = analyzer.tokenStream("field", StringReader(input))
        val termAttr = stream.addAttribute(TermAttribute::class.java)
        for (expected in output) {
            Assert.assertTrue(stream.incrementToken())
            Assert.assertEquals(expected, termAttr.term())
        }
        Assert.assertFalse(stream.incrementToken())
        stream.close()
    }

    fun displayPositionIncrements(analyzer: Analyzer, text: String) {
        val stream = analyzer.tokenStream("contents", StringReader(text))
        val posIncr = stream.addAttribute(
            PositionIncrementAttribute::class.java
        )
        while (stream.incrementToken()) {
            println("posIncr=" + posIncr.positionIncrement)
        }
    }
}
fun main() {
    println("SimpleAnalyzer")
    AnalyzerUtils.displayTokensWithFullDetails(
        SimpleAnalyzer(LUCENE_VERSION),
        "The quick brown fox...."
    )

    println("\n----")
    println("StandardAnalyzer")
    AnalyzerUtils.displayTokensWithFullDetails(
        StandardAnalyzer(Version.LUCENE_30),
        "I'll email you at xyz@example.com"
    )
}