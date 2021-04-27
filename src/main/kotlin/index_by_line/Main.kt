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
package index_by_line

import org.apache.lucene.search.TopDocs
import java.io.File
import java.lang.IndexOutOfBoundsException
import java.lang.RuntimeException
import kotlin.system.exitProcess


fun main(args: Array<String>) {

    val simpleSearch = SimpleTermQuery(Common.INDEX_DIR)
    var query = ""
    while (query != "q") {
        print("Enter query or \"q\" to quit. > ")
        query = readLine()!!.trim()
        if (query == "q") {
            println("bye...")
            exitProcess(0)
        }
        val results : List<Pair<String,Int>> = simpleSearch.search(query)
        // val results : List<Int> = simpleSearch.search(query)
        println("${results.size} Results:$results")
        val csvFile = File("${System.getenv("PWD")}/data/text_files_indexed.csv")
        if (!csvFile.exists())
            throw RuntimeException("Main:Failed to find $csvFile")
        val bufferHandle = csvFile.inputStream().bufferedReader()
        var line = bufferHandle.readLine()
        // build a map of maps for the results.  Hse this both to store
        // and to detect matches when filtering the .csv file of the
        // data.
        val rvMap = mutableMapOf<String,MutableMap<Int,String>>();
        results.map{pair ->
            val fName = pair.first; val lineNumber = pair.second
            if(rvMap[fName] == null) rvMap[fName] = mutableMapOf()
            rvMap[fName]!![lineNumber] = ""
        }
        while (line != null) {
            if ("^[^:]+:[^:]+:.*".toRegex().matches(line)) {
                val (fName, ln, text) = line.split(":".toRegex(), 3)
                val lineNumber = ln.toInt()
                if (rvMap[fName] != null && rvMap[fName]!![lineNumber] != null)
                    rvMap[fName]!![lineNumber] = text
            }
            line = bufferHandle.readLine()
        }
        // print results based
        results.forEachIndexed{ index, pair ->
            val(fName, lineNumber) = pair
            println("%2d) %-15s %4d %s".format(
                index + 1, fName, lineNumber + 1, rvMap[fName]!![lineNumber]))
        }
    }
}
