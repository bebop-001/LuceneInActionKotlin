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
package index_by_line

import org.apache.lucene.analysis.WhitespaceAnalyzer
import org.apache.lucene.util.Version
import java.io.File

val LUCENE_VERSION = Version.LUCENE_36
val INDEX_HOME = File(System.getenv("PWD"), "indexes")
val DATA_DIR = File(System.getenv("PWD") + "/data")

val ANALYZER = WhitespaceAnalyzer(LUCENE_VERSION)


