/*
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
 * See the License for the specific language.
 */

package Chapter_08.FastVectorHighlighter

import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.lang.Exception

import kotlin.Throws

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.Field.TermVector
import org.apache.lucene.document.NumericField
import org.apache.lucene.index.IndexReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.queryParser.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.vectorhighlight.BaseFragmentsBuilder
import org.apache.lucene.search.vectorhighlight.FastVectorHighlighter
import org.apache.lucene.search.vectorhighlight.FragListBuilder
import org.apache.lucene.search.vectorhighlight.FragmentsBuilder
import org.apache.lucene.search.vectorhighlight.ScoreOrderFragmentsBuilder
import org.apache.lucene.search.vectorhighlight.SimpleFragListBuilder
import org.apache.lucene.store.Directory
import org.apache.lucene.store.RAMDirectory
import org.apache.lucene.util.Version

// From chapter 8
private val LUCENE_VERSION = Version.LUCENE_36
var dir: Directory = RAMDirectory()

private val vectorHighlighter: FastVectorHighlighter
    get() {
        val fragListBuilder: FragListBuilder = SimpleFragListBuilder()
        val fragmentBuilder: FragmentsBuilder =
            ScoreOrderFragmentsBuilder(
                BaseFragmentsBuilder.COLORED_PRE_TAGS,
                BaseFragmentsBuilder.COLORED_POST_TAGS
            )
        return FastVectorHighlighter(
            true, true,
            fragListBuilder, fragmentBuilder
        )
    }

val inputDocsMapped : Map<Int, String> = arrayOf(
    "the quick brown fox jumps over the lazy dog",
    "the quick gold fox jumped over the lazy black dog",
    "the quick fox jumps over the black dog",
    "the red fox jumped over the lazy dark gray dog"
).mapIndexed{i , s -> i to s}.toMap()

private const val QUERY_TEXT = "quick OR fox OR \"lazy dog\"~1" // #B
private const val textfieldName = "snippet"
private const val indexFieldName = "idx"
private var ANALYZER: Analyzer = StandardAnalyzer(LUCENE_VERSION)

private val sampleOutputFile = File(System.getenv("PWD"), "data/FastVectorHighlighterSample.html")

@Throws(IOException::class)
fun makeIndex() {
    sampleOutputFile.delete()
    val indexWriter: IndexWriter = IndexWriter(
        dir, IndexWriterConfig(LUCENE_VERSION, ANALYZER)
    )
    inputDocsMapped.keys.forEach{key ->
        val doc = Document()
        doc.add(
            Field(
                textfieldName, inputDocsMapped[key], Field.Store.YES, Field.Index.ANALYZED,
                TermVector.WITH_POSITIONS_OFFSETS
            )
        )
        doc.add(
            NumericField(
                indexFieldName, Field.Store.YES,
                false
            )
            .setIntValue(key)
        )
        indexWriter.addDocument(doc)
    }
    indexWriter.close()
}

@Throws(Exception::class)
fun searchIndex() {
    val parser = QueryParser(
        Version.LUCENE_30,
        textfieldName, ANALYZER
    )
    val query = parser.parse(QUERY_TEXT)
    val highlighter = vectorHighlighter
    val fieldQuery = highlighter.getFieldQuery(query)
    val indexReader = IndexReader.open(dir)
    val searcher = IndexSearcher(indexReader)
    val docs = searcher.search(query, 10)
    if (docs.scoreDocs.size == 0) {
        println("No matches found.")
        return
    }
    val highlightedHtmlWriter = FileWriter(sampleOutputFile)
    highlightedHtmlWriter.write("<html>\n")
    highlightedHtmlWriter.write("<body>\n")
    highlightedHtmlWriter.write("<p>text query string = &lt;$QUERY_TEXT&gt;</p>\n")
    highlightedHtmlWriter.write("<p>resultant lucene query = &lt;$query&gt;</p>\n")
    for (scoreDoc in docs.scoreDocs) {
        val docId:Int = scoreDoc.doc
        val document: Document = searcher.doc(docId)
        val snippet = highlighter.getBestFragment( // #E
            fieldQuery, searcher.indexReader,  // #E
            docId, textfieldName, 100
        )
        if (snippet != null) {
            val idxField = document.get("idx")
            val textField = document.getFieldable(textfieldName)
            val snippetHtml = snippet
                .replace("<", "&lt;")
                .replace(">", "&gt;")
            highlightedHtmlWriter.write(
            "$indexFieldName = $idxField, " +
                "scoreDoc.doc = ${scoreDoc.doc}, " +
                "$textfieldName fieldable = &lt;$textField&gt;<br>\n" +
                "snippet = \"$snippet\", " +
                "snippetHtml = \"$snippetHtml\"<br/><br/>\n"
            )
        }
    }
    highlightedHtmlWriter.write("</body>\n</html>\n")
    highlightedHtmlWriter.close()
    searcher.close()
    indexReader.close()
}

fun main(args: Array<String>) {
    makeIndex()
    searchIndex()
    println("output saved to html file:$sampleOutputFile")
}