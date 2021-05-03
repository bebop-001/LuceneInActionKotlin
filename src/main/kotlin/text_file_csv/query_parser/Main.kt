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
package text_file_csv.query_parser

import java.io.File
import java.lang.RuntimeException
import kotlin.system.exitProcess

var EXAMINE = false
private const val USAGE = """USAGE: --examine [--simple|--standard|--|--white_space"]
    Search a csv file created from text files supplied with
    the LucineInAction software for a term.  Search is interactive.
    You must select an analyzer type.
    --simple      Use SimpleAnalyzer
    --standard    Use StandardAnalyzer
    --white_space Use WhiteSpaceAnalyzerCsvSearch --examine
    
    --examine : turn on examine for search results.
"""
private const val searcherWildcardHelp = """
    Boolean Operators: 
        AND, OR, NOT, () are supported
    Wildcards: 
        Can not begin a query.
        "?": 1 of ane character, "*": 0 or more of any character
    Proximity:  "word_1 word_2"~N\" where N is word count between
        words.
    Similarity:
        word~N  where N is a value between 0.0 and 1.0.
        word~   N defaults to 0.5.
"""

fun main(args: Array<String>) {
    var at : AnalyzerType? = null
    args.forEach { arg ->
        when (arg) {
            "--simple" -> at = if (at != null) throw RuntimeException("at already set as $at")
            else AnalyzerType.simple
            "--standard" -> at = if (at != null) throw RuntimeException("at already set as $at")
            else AnalyzerType.standard
            "--white_space" -> at = if (at != null) throw RuntimeException("at already set as $at")
            else AnalyzerType.white_space
            "--examine" -> EXAMINE = true
            else -> throw RuntimeException("$USAGE: Unrecognized analyzer:${arg}")
        }
    }
    if (at == null)
        throw RuntimeException("$USAGE\nAnalyzer type must be selected.")
    // Update the index if necessary or if user wants to.
    var updateIndex : Boolean? = null
    if (!Common.INDEX_DIR.exists()) {
        updateIndex = true
    }
    else {
        while(updateIndex == null) {
            print("Update index? (y/n) > ")
            val update = readLine()!!.trim()
            if ("^\\s*[yY]\\s*$".toRegex().matches(update))
                updateIndex = true
            else if ("^\\s*[Nn]\\s*$".toRegex().matches(update))
                updateIndex = false
        }
    }
    if (updateIndex) CsvIndexer(at!!, Common.INDEX_DIR)
    println(searcherWildcardHelp)
    var query = ""
    while (query != "q") {
        print("Enter query or \"q\" to quit. > ")
        query = readLine()!!.trim()
        if (query == "q") {
            println("bye...")
            exitProcess(0)
        }
        val results : Pair<String?, List<Pair<String, Int>>?> =
            CsvSearcher(at!!, Common.INDEX_DIR).search(query)
        if (results.first == null) {
            val matches = results.second!!
            println("${matches.size} Results:$matches")
            val csvFile = File("${System.getenv("PWD")}/data/text_files_indexed.csv")
            if (!csvFile.exists())
                throw RuntimeException("Main:Failed to find $csvFile")
            val bufferHandle = csvFile.inputStream().bufferedReader()
            var line = bufferHandle.readLine()
            // build a map of maps for the results.  Hse this both to store
            // and to detect matches when filtering the .csv file of the
            // data.
            val rvMap = mutableMapOf<String, MutableMap<Int, String>>()
            matches.map { pair ->
                val fName = pair.first
                val lineNumber = pair.second
                if (rvMap[fName] == null) rvMap[fName] = mutableMapOf()
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
            matches.forEachIndexed { index, pair ->
                val (fName, lineNumber) = pair
                println(
                    "%2d) %-15s %4d %s".format(
                        index + 1, fName, lineNumber + 1, rvMap[fName]!![lineNumber]
                    )
                )
            }
        }
        else {
            // an error occurred.
            println("${results.first}\n$searcherWildcardHelp\n")
        }
    }
}
