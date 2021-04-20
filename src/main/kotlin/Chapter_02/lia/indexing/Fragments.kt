package Chapter_02.lia.indexing

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.WhitespaceAnalyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.DateTools
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.NumericField
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.store.Directory
import org.apache.lucene.store.RAMDirectory
import org.apache.lucene.util.Version
import java.io.IOException
import java.util.*

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
/** Just to test the code compiles.  */
internal class Fragments {
    private val senderEmail: String
        private get() = "bob@smith.com"
    private val senderName: String
        private get() = "Bob Smith"
    private val subject: String
        private get() = "Hi there Lisa"
    private val body: String
        private get() = "I don't have much to say"

    private fun isImportant(lowerDomain: String): Boolean {
        return lowerDomain.endsWith(senderDomain)
    }

    private fun isUnimportant(lowerDomain: String): Boolean {
        return lowerDomain.endsWith(BAD_DOMAIN)
    }

    @Throws(Exception::class)
    fun ramDirExample() {
        val analyzer: Analyzer = WhitespaceAnalyzer()
        // START
        val ramDir: Directory = RAMDirectory()
        val writer = IndexWriter(
            ramDir, analyzer,
            IndexWriter.MaxFieldLength.UNLIMITED
        )
        // END
    }

    @Throws(Exception::class)
    fun dirCopy() {
        val otherDir: Directory? = null

        // START
        val ramDir: Directory = RAMDirectory(otherDir)
        // END
    }

    @Throws(Exception::class)
    fun addIndexes() {
        val otherDir: Directory? = null
        val ramDir: Directory? = null
        val analyzer: Analyzer? = null

        // START
        val writer = IndexWriter(
            otherDir, analyzer,
            IndexWriter.MaxFieldLength.UNLIMITED
        )
        writer.addIndexesNoOptimize(*arrayOf(ramDir))
        // END
    }

    @Throws(IOException::class)
    fun docBoostMethod() {
        val dir: Directory = RAMDirectory()
        val writer = IndexWriter(dir, StandardAnalyzer(Version.LUCENE_30), IndexWriter.MaxFieldLength.UNLIMITED)

        // START
        val doc = Document()
        val senderEmail = senderEmail
        val senderName = senderName
        val subject = subject
        val body = body
        doc.add(
            Field(
                "senderEmail", senderEmail,
                Field.Store.YES,
                Field.Index.NOT_ANALYZED
            )
        )
        doc.add(
            Field(
                "senderName", senderName,
                Field.Store.YES,
                Field.Index.ANALYZED
            )
        )
        doc.add(
            Field(
                "subject", subject,
                Field.Store.YES,
                Field.Index.ANALYZED
            )
        )
        doc.add(
            Field(
                "body", body,
                Field.Store.NO,
                Field.Index.ANALYZED
            )
        )
        val lowerDomain = senderDomain.toLowerCase()
        if (isImportant(lowerDomain)) {
            doc.boost = 1.5f //1
        }
        else if (isUnimportant(lowerDomain)) {
            doc.boost = 0.1f //2 
        }
        writer.addDocument(doc)
        // END
        writer.close()

        /*
      #1 Good domain boost factor: 1.5
      #2 Bad domain boost factor: 0.1
    */
    }

    @Throws(IOException::class)
    fun fieldBoostMethod() {
        val senderName = senderName
        val subject = subject

        // START
        val subjectField = Field(
            "subject", subject,
            Field.Store.YES,
            Field.Index.ANALYZED
        )
        subjectField.boost = 1.2f
        // END
    }

    fun numberField() {
        val doc = Document()
        // START
        doc.add(NumericField("price").setDoubleValue(19.99))
        // END
    }

    fun numberTimestamp() {
        val doc = Document()
        // START
        doc.add(
            NumericField("timestamp")
                .setLongValue(Date().time)
        )
        // END

        // START
        doc.add(
            NumericField("day")
                .setIntValue((Date().time / 24 / 3600).toInt())
        )
        // END
        val date = Date()
        // START
        val cal = Calendar.getInstance()
        cal.time = date
        doc.add(
            NumericField("dayOfMonth")
                .setIntValue(cal[Calendar.DAY_OF_MONTH])
        )
        // END
    }

    @Throws(Exception::class)
    fun setInfoStream() {
        val dir: Directory? = null
        val analyzer: Analyzer? = null
        // START
        val writer = IndexWriter(
            dir, analyzer,
            true, IndexWriter.MaxFieldLength.UNLIMITED
        )
        writer.infoStream = System.out
        // END
    }

    fun dateMethod() {
        val doc = Document()
        doc.add(
            Field(
                "indexDate",
                DateTools.dateToString(Date(), DateTools.Resolution.DAY),
                Field.Store.YES,
                Field.Index.NOT_ANALYZED
            )
        )
    }

    @Throws(Exception::class)
    fun numericField() {
        val doc = Document()
        val price = NumericField("price")
        price.setDoubleValue(19.99)
        doc.add(price)
        val timestamp = NumericField("timestamp")
        timestamp.setLongValue(Date().time)
        doc.add(timestamp)
        val b = Date()
        val birthday = NumericField("birthday")
        val v = DateTools.dateToString(b, DateTools.Resolution.DAY)
        birthday.setIntValue(v.toInt())
        doc.add(birthday)
    }

    @Throws(Exception::class)
    fun indexAuthors() {
        val authors = arrayOf("lisa", "tom")
        // START
        val doc = Document()
        for (author in authors) {
            doc.add(
                Field(
                    "author", author,
                    Field.Store.YES,
                    Field.Index.ANALYZED
                )
            )
        }
        // END
    }

    companion object {
        fun indexNumbersMethod() {
            // START
            Field("size", "4096", Field.Store.YES, Field.Index.NOT_ANALYZED)
            Field("price", "10.99", Field.Store.YES, Field.Index.NOT_ANALYZED)
            Field("author", "Arthur C. Clark", Field.Store.YES, Field.Index.NOT_ANALYZED)
            // END
        }

        private val senderDomain = "example.com"
            private get() = field
        const val BAD_DOMAIN = "yucky-domain.com"
    }
}