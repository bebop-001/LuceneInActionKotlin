package Chapter_02.lia.indexing

import Chapter_02.lia.common.rmDir
import junit.framework.TestCase
import org.apache.lucene.analysis.SimpleAnalyzer
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.store.LockObtainFailedException
import java.io.File
import java.io.IOException

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
class LockTest : TestCase() {
    private var dir: Directory? = null
    private var indexDir: File? = null
    @Throws(IOException::class)
    override fun setUp() {
        indexDir = File(
            System.getProperty("java.io.tmpdir", "tmp") +
                System.getProperty("file.separator") + "index"
        )
        dir = FSDirectory.open(indexDir)
    }

    @Throws(IOException::class)
    fun testWriteLock() {
        val writer1 = IndexWriter(
            dir, SimpleAnalyzer(LUCENE_VERSION),
            IndexWriter.MaxFieldLength.UNLIMITED
        )
        var writer2: IndexWriter? = null
        try {
            writer2 = IndexWriter(
                dir, SimpleAnalyzer(LUCENE_VERSION),
                IndexWriter.MaxFieldLength.UNLIMITED
            )
            fail("We should never reach this point")
        }
        catch (e: LockObtainFailedException) {
            // e.printStackTrace();  // #A
        }
        finally {
            writer1.close()
            assertNull(writer2)
            rmDir(indexDir!!)
        }
    }
}