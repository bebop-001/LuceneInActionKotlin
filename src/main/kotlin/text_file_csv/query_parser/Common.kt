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

// this is our test data created from the data/*.txt.
// Key is the name of the data file and the list contains
// contents of the file chopped into lines of max 80 characters.
val chunkedTextFiles = mutableMapOf<String, MutableList<String>>()

object Common {
    val LUCENE_VERSION = Version.LUCENE_36

    val INDEX_HOME = File(System.getenv("PWD"), "indexes")
    val INDEX_DIR = File(INDEX_HOME, javaClass.packageName)

    val DATA_DIR = File(System.getenv("PWD") + "/data")
}