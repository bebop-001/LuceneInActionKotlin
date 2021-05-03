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

// Some things must be the same between indexer and searcher.  I
// put that stuff yere.
package text_file_csv.query_parser

import org.apache.lucene.util.Version
import java.io.File
import java.lang.RuntimeException


fun mkDirs(dir: File) {
    val dirs = dir.toString().split("/").filter { it.isNotEmpty() }.toMutableList()
    var toMake = ""
    while (dirs.size > 0) {
        toMake = "$toMake/${dirs[0]}"
        val f = File(toMake)
        if (!f.exists() && !f.mkdir()) {
            throw RuntimeException("mkdir:mkdir($f) FAILED")
        }
        dirs.removeAt(0)
    }
}

fun rmDirsAndFiles(root: File) {
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

object Common {
    val LUCENE_VERSION = Version.LUCENE_36

    val INDEX_HOME = File(System.getenv("PWD"), "indexes")
    val INDEX_DIR = File(INDEX_HOME, javaClass.packageName)

    val DATA_DIR = File(System.getenv("PWD") + "/data")
}