@file:Suppress("MemberVisibilityCanBePrivate")

package index_by_line

import org.apache.lucene.analysis.WhitespaceAnalyzer
import org.apache.lucene.document.Fieldable
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.store.FSDirectory
import java.io.File
import java.lang.Exception
import java.lang.RuntimeException

import org.apache.lucene.util.Version
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field

val HOME = File(System.getenv("PWD"))
val LUCENE_VERSION = Version.LUCENE_36

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
                IndexWriterConfig(LUCENE_VERSION,
                    WhitespaceAnalyzer(LUCENE_VERSION)
                )
            )
    init {
        val doc = Document()
        val w = writer
        var totalSize = 0
        data.keys.forEach { fName ->
            var lineCount = 0
            var textSize = 0
            data[fName]!!.forEachIndexed { index, text ->
                doc.add(
                    Field(
                        "fName", fName,
                        Field.Store.YES,
                        Field.Index.NOT_ANALYZED
                    )
                )
                doc.add(
                    Field(
                        "index", lineCount.toString(),
                        Field.Store.YES,
                        Field.Index.NOT_ANALYZED
                    )
                )
                doc.add(
                    Field(
                        "text", text,
                        Field.Store.NO,
                        Field.Index.ANALYZED
                    )
                )
                textSize += text.length
                lineCount++
            }
            w.addDocument(doc)
            println("%35s:%,9d:%4d".format(fName, textSize, lineCount))
            totalSize += textSize
        }
        println("%35s:%,9d bytes".format("TOTAL", totalSize))

        w.close()
    }
    companion object {
        val csvData = mutableMapOf<String,MutableList<String>>()
        @Throws(Exception::class)
        @JvmStatic
        fun main(args: Array<String>) {
            fun mkDirsIfNeeded (dirs : String) :File {
                var toMake = HOME
                if (File(toMake, dirs).exists())
                    return File(toMake, dirs)
                dirs.split("/").filter{it.isNotEmpty()}.forEach{dir ->
                    toMake = File(toMake, dir)
                    if (!toMake.exists() && !toMake.mkdir())
                        throw RuntimeException("mkdirs: Failed to create $toMake")
                }
                return toMake
            }
            val dataDir = File(System.getenv("PWD") + "/data")
            val indexDir = mkDirsIfNeeded("indexes/${javaClass.packageName}/index")
            if (!indexDir.exists() && !indexDir.mkdir())
                throw RuntimeException("$indexDir doesn't exist and mkdir failed")
            // read any .txt files and save contents by
            // line in a map keyed to the filename.  Lines are "chunked" to
            // 100 chars max.  This is our test data.
            val txtFiles = dataDir.list()
                .filter{file -> File(dataDir, file).isFile && file.endsWith(".txt")}
            txtFiles.forEach{fName->
                val file = File(dataDir, fName)
                val br = file.bufferedReader()
                var line : String? = br.readLine()?.trim()
                while(line != null) {
                    if (csvData[fName] == null)
                        csvData[fName] = mutableListOf()
                    line.chunked(100).forEach { chunk ->
                        csvData[fName]!!.add(chunk)
                    }
                    line = br.readLine()?.trim()
                }
                br.close()
            }

            val start = System.currentTimeMillis()
            val indexer = Indexer(csvData, indexDir)
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