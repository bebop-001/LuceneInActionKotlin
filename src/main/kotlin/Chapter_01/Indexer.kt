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

/*
 * This code was originally written for
 * Erik's Lucene intro java.net article
 * Ported to Kotlin by sjs 4/16/2021
 */
package Chapter_01

import org.apache.lucene.index.IndexWriter
import java.io.IOException
import java.io.FileReader
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.util.Version
import java.io.File
import java.io.FileFilter
import java.lang.Exception
import java.lang.RuntimeException

open class Indexer(private val DATA_DIR: File, INDEX_DIR:File) {

  @Suppress("DEPRECATION")
  private val writer: IndexWriter = IndexWriter(
    FSDirectory.open(INDEX_DIR),  //3
    StandardAnalyzer( //3
      Version.LUCENE_30
    ),  //3
    true,  //3
    IndexWriter.MaxFieldLength.UNLIMITED
  )

  @Throws(IOException::class)
  fun close() {
    writer.close() //4
  }

  @Throws(Exception::class)
  fun index(filter: FileFilter?): Int {
    val files = DATA_DIR.listFiles()
    files?.forEach { f ->
        if (!f.isDirectory &&
            !f.isHidden &&
            f.exists() &&
            f.canRead() &&
            (filter == null || filter.accept(f))) {
          indexFile(f)
        }
    }
    return writer.numDocs() //5
  }

  private class TextFilesFilter : FileFilter {
    override fun accept(path: File): Boolean {
      return path.name.toLowerCase() //6
          .endsWith(".txt") //6
    }
  }

  @Throws(Exception::class)
  protected fun getDocument(f: File): Document {
    val doc = Document()
    doc.add(Field("contents", FileReader(f))) //7
    doc.add(
      Field(
        "filename", f.name,  //8
        Field.Store.YES, Field.Index.NOT_ANALYZED
      )
    ) //8
    doc.add(
      Field(
        "fullpath", f.canonicalPath,  //9
        Field.Store.YES, Field.Index.NOT_ANALYZED
      )
    ) //9
    return doc
  }

  @Throws(Exception::class)
  private fun indexFile(f: File) {
    println("Indexing " + f.canonicalPath)
    val doc = getDocument(f)
    writer.addDocument(doc) //10
  }

  companion object {
    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
      val dataDir = File(System.getenv("PWD") + "/data")
      val indexDir = File(System.getenv("PWD") + "/indexerOut")
      if (!indexDir.exists() && !indexDir.mkdir())
        throw RuntimeException("$indexDir doesn't exist and mkdir failed")

        ("Usage: java " + Indexer::class.java.name
            + " <index dir> <data dir>")
      val start = System.currentTimeMillis()
      val indexer = Indexer(dataDir, indexDir)
      val numIndexed: Int = try {
        indexer.index(TextFilesFilter())
      }
      finally {
        indexer.close()
      }
      val end = System.currentTimeMillis()
      println(
        "Indexing " + numIndexed + " files took "
            + (end - start) + " milliseconds"
      )
    }
  }
}
