package Chapter_02.lia.indexing

import java.io.IOException
import org.apache.lucene.store.RAMDirectory
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.analysis.WhitespaceAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.util.Version

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
 */ // From chapter 2
val LUCENE_VERSION = Version.LUCENE_30
class VerboseIndexing {
    @Throws(IOException::class)
    private fun index() {
        val writer = IndexWriter(
            RAMDirectory(),
            IndexWriterConfig(LUCENE_VERSION, WhitespaceAnalyzer(LUCENE_VERSION))
        )
        writer.infoStream = System.out
        (0..99).forEach {
            val doc = Document()
            doc.add(Field("keyword", "goober", Field.Store.YES, Field.Index.NOT_ANALYZED))
            writer.addDocument(doc)
        }
        writer.close()
    }

    companion object {
        @Throws(IOException::class)
        @JvmStatic
        fun main(args: Array<String>) {
            VerboseIndexing().index()
        }
    }
}