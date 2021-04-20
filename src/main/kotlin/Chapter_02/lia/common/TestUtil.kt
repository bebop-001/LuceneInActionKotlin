package Chapter_02.lia.common

import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
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
 */
@Throws(IOException::class)
fun hitCount(searcher: IndexSearcher, query: Query?): Int {
    return searcher.search(query, 1).totalHits
}

// recursively remove a directory and its contents.
@Throws(IOException::class)
fun rmDir(dir: File) {
    if (dir.exists()) {
        if (!dir.isDirectory)
            throw IOException("rmDir:$dir is not a directory")
        dir.listFiles()?.forEach { file ->
            if (file.isDirectory)
                rmDir(file)
            else {
                file.delete() ||
                    throw IOException("rmDir:$dir:delete $file FAILED")
            }
        }
        dir.delete()
    }
}