package FastVectorHighlighter

import org.apache.lucene.store.RAMDirectory
import org.apache.lucene.analysis.standard.StandardAnalyzer
import kotlin.Throws
import kotlin.jvm.JvmStatic
import FastVectorHighlighter.FastVectorHighlighterSample
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import java.io.IOException
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriter.MaxFieldLength
import org.apache.lucene.document.Field.TermVector
import org.apache.lucene.queryParser.QueryParser
import org.apache.lucene.search.vectorhighlight.FastVectorHighlighter
import org.apache.lucene.search.vectorhighlight.FieldQuery
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.TopDocs
import java.io.FileWriter
import org.apache.lucene.search.ScoreDoc
import org.apache.lucene.search.vectorhighlight.FragListBuilder
import org.apache.lucene.search.vectorhighlight.SimpleFragListBuilder
import org.apache.lucene.search.vectorhighlight.FragmentsBuilder
import org.apache.lucene.search.vectorhighlight.ScoreOrderFragmentsBuilder
import org.apache.lucene.search.vectorhighlight.BaseFragmentsBuilder
import org.apache.lucene.store.Directory
import org.apache.lucene.util.Version
import java.lang.Exception

/**
 * Copyright Manning Publications Co.
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
 */ // From chapter 8
object FastVectorHighlighterSample {
    val DOCS = arrayOf( // #A
        "the quick brown fox jumps over the lazy dog",  // #A
        "the quick gold fox jumped over the lazy black dog",  // #A
        "the quick fox jumps over the black dog",  // #A
        "the red fox jumped over the lazy dark gray dog" // #A
    )
    const val QUERY = "quick OR fox OR \"lazy dog\"~1" // #B
    const val F = "f"
    var dir: Directory = RAMDirectory()
    var analyzer: Analyzer = StandardAnalyzer(Version.LUCENE_30)
    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        if (args.size != 1) {
            System.err.println("Usage: FastVectorHighlighterSample <filename>")
            System.exit(-1)
        }
        makeIndex()
        searchIndex(args[0])
    }

    @Throws(IOException::class)
    fun makeIndex() {
        val writer = IndexWriter(
            dir, analyzer,
            true, MaxFieldLength.UNLIMITED
        )
        for (d in DOCS) {
            val doc = Document()
            doc.add(
                Field(
                    F, d, Field.Store.YES, Field.Index.ANALYZED,
                    TermVector.WITH_POSITIONS_OFFSETS
                )
            )
            writer.addDocument(doc)
        }
        writer.close()
    }

    @Throws(Exception::class)
    fun searchIndex(filename: String?) {
        val parser = QueryParser(
            Version.LUCENE_30,
            F, analyzer
        )
        val query = parser.parse(QUERY)
        val highlighter = highlighter // #C
        val fieldQuery = highlighter.getFieldQuery(query) // #D
        val searcher = IndexSearcher(dir)
        val docs = searcher.search(query, 10)
        val writer = FileWriter(filename)
        writer.write("<html>")
        writer.write("<body>")
        writer.write("<p>QUERY : " + QUERY + "</p>")
        for (scoreDoc in docs.scoreDocs) {
            val snippet = highlighter.getBestFragment( // #E
                fieldQuery, searcher.indexReader,  // #E
                scoreDoc.doc, F, 100
            ) // #E
            if (snippet != null) {
                writer.write(scoreDoc.doc.toString() + " : " + snippet + "<br/>")
            }
        }
        writer.write("</body></html>")
        writer.close()
        searcher.close()
    }// #F

    // #F
// #F
    // #F
    // #F
// #F
    // #F
    val highlighter: FastVectorHighlighter
        get() {
            val fragListBuilder: FragListBuilder = SimpleFragListBuilder() // #F
            val fragmentBuilder: FragmentsBuilder =  // #F
                ScoreOrderFragmentsBuilder( // #F
                    BaseFragmentsBuilder.COLORED_PRE_TAGS,  // #F
                    BaseFragmentsBuilder.COLORED_POST_TAGS
                ) // #F
            return FastVectorHighlighter(
                true, true,  // #F
                fragListBuilder, fragmentBuilder
            ) // #F
        }
}