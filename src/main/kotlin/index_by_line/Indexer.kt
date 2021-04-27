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

package index_by_line


import org.apache.lucene.document.Fieldable
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.store.FSDirectory
import java.io.File
import java.lang.Exception
import java.lang.RuntimeException



import index_by_line.Common.DATA_DIR
import index_by_line.Common.INDEX_DIR
import index_by_line.Common.LUCENE_VERSION
import index_by_line.Common.ANALYZER

import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.NumericField

private fun Document._add(field:Fieldable):Document {
    this.add(field)
    return this
}
class Indexer (data:Map<String,List<String>>, val indexDir: File) {

    private val writer: IndexWriter
        get() = IndexWriter(
                FSDirectory.open(indexDir),
                // use writerconfig instead of directly accessing
                // the analyzer.
                IndexWriterConfig(LUCENE_VERSION, ANALYZER)
            )
    init {
        val w = writer
        var totalSize = 0
        var filesIdx = 1
        data.keys.forEach { fName ->
            var lineCount = 0
            var textSize = 0
            data[fName]!!.forEachIndexed{fLineNumber, text ->
                val doc = Document()
                doc._add(
                    NumericField("line_number", Field.Store.YES,
                        false)
                        .setIntValue(fLineNumber)
                )
                ._add(
                    Field(
                        "file_name", fName,
                        Field.Store.YES,
                        Field.Index.NOT_ANALYZED
                    )
                )
                ._add(
                    Field(
                        "text", text.toLowerCase(),
                        Field.Store.NO,
                        Field.Index.ANALYZED
                    )
                )
                // println("index:$lineCount, fName:$fName, text:${text.length}")
                // println("$fName:${text.length}")
                textSize += text.length
                lineCount++
                w.addDocument(doc)
            }
            println("%2d) %35s:%,9d:%4d".format(filesIdx++, fName, textSize, lineCount))
            totalSize += textSize
        }
        println("%35s:%,9d bytes".format("TOTAL", totalSize))

        w.close()
    }
    companion object {
        @Throws(Exception::class)
        @JvmStatic
        fun main(args: Array<String>) {
            fun mkDir (dir:File) {
                val dirs = dir.toString().split("/").filter{it.isNotEmpty()}.toMutableList()
                var toMake = ""
                while(dirs.size > 0) {
                    toMake = "$toMake/${dirs[0]}"
                    val f = File(toMake)
                    if (!f.exists() && !f.mkdir()) {
                        throw RuntimeException("mkdir:mkdir($f) FAILED")
                    }
                    dirs.removeAt(0)
                }
            }
            fun rmDirsAndFiles(root:File) {
                if (root.exists()) {
                    if (root.isDirectory) {
                        root.list()?.forEach { file ->
                            val f = File(root, file)
                            when {
                                f.isDirectory -> rmDirsAndFiles(f)
                                f.isFile -> f.delete()
                                else -> throw RuntimeException("recurRm:Unknown file type: $f")
                            }
                        }
                        root.delete()
                    }
                }
            }
            rmDirsAndFiles(INDEX_DIR)
            mkDir(INDEX_DIR)
            if (!INDEX_DIR.exists() && !INDEX_DIR.mkdir())
                throw RuntimeException("$INDEX_DIR doesn't exist and mkdir failed")
            // read any .txt files and save contents by
            // line in a map keyed to the filename.  Lines are "chunked" to
            // 100 chars max.  This is our test data.
            val txtFiles = DATA_DIR.list()
                .filter{file -> File(DATA_DIR, file).isFile && file.endsWith(".txt")}
            txtFiles.forEach{fName->
                val file = File(DATA_DIR, fName)
                val br = file.bufferedReader()
                var line : String? = br.readLine()?.trim()
                chunkedTextFiles[fName] = mutableListOf()
                while(line != null) {
                    if (line.isNotEmpty()) {
                        var l = line.split(" ").filter { it.isNotEmpty() }.toMutableList()
                        var lo = ""
                        var nextWord = ""
                        while (l.size > 0) {
                            nextWord = l.removeAt(0)
                            if ((lo.length + nextWord.length + 1) > 80) {
                                chunkedTextFiles[fName]!!.add(lo)
                                lo = nextWord
                            }
                            else lo = "$lo $nextWord"
                        }
                        chunkedTextFiles[fName]!!.add(lo)
                    }
                    line = br.readLine()?.trim()
                }
                br.close()
            }
            val textFilesCsv = File(DATA_DIR,"text_files_indexed.csv").outputStream().bufferedWriter()
            textFilesCsv.write("""# This file is mostly for debug.
                |# Hopefully with lucene working, it won't be needed.
                |""".trimMargin("|"))
            chunkedTextFiles.keys.forEach{fName ->
                chunkedTextFiles[fName]!!.forEachIndexed{ fLineNumber, text ->
                    textFilesCsv.write("$fName:$fLineNumber:$text\n")
                }
            }
            textFilesCsv.close()

            val start = System.currentTimeMillis()
            val indexer = Indexer(chunkedTextFiles, INDEX_DIR)
            /*
            val numIndexed: Int = try {
                indexer.index(Indexer.TextFilesFilter())
            }
            finally {
                indexer.close()
            }
            val end = System.currentTimeMillis()
            println(
                "Indexing " + numIndexed + " files took "
                    + (end - start) + " milliseconds"
            )

             */
        }
    }
}
