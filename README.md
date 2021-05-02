Tue Apr 27 10:40:24 PDT 2021
# Lucene In Action

This is (some) of the code from the book
<a href="https://www.manning.com/books/lucene-in-action-second-edition">
Lucene In Action</a> by
Michael McCandless, Erik Hatcher, and Otis Gospodnetićthe book.
I bought the book several years ago and don't learning Lucene
without it would have been really difficult.

I need to implement search of English for an Android project
I'm working on.  From what I've read, full text search in Lucene
is more effective than in SQL, so I am looking at implementing
that part of the project using Lucene. 

This is an Android
project though so I need to be careful about memory and I
won't need many of the features added in more recent versions
of Lucene.  For that reason I'm using the 3.6.2 release of
Lucene.

I have been using Kotlin lately and like it.  I'm from
a background of C and PERL so the language fits me
better than Java.

Most of the code is an implementation of the source from 
the book converted to Kotlin.  I'm currently using
the the 2021.1 version of IntelliJ Community IDE with
Kotlin 1.4.2 and Java 11.  You'll find a list of the .jar
files I am currently using in Dependencies.md.

Data files will be found under the data directory in the 
project root and indexes will be found under the 
indexes/&lt;package name&gt; directory.

I will also be implementing some other utils to make sure
I understand what I'm doing.

Steve S.<br>

**NOTE:** Before the project will compile you will compile,
you will need to configure the project SDK and
output directories.  This is done in the File->Project Settings
directory.
* Open File->Project Structure
* Select "Project"
* In the SDK item, select your Java JDK.  I use JDK 11 but
Java 8 should work.
* From the "Project Compiler Output" dropdown, select
the out directory above the source directory.  This should
be the default.
* You may need to run "Invalidate Caches and Restart" and
at this point you should be able to run Build->Rebuild Project.

5/1/2021 **UPDATE**<br>
The example from Chapter 1 supports wild card searching.  There
is help printed by the USAGE statement when the process
starts.  See 
<a href="https://lucene.apache.org/core/3_6_0/queryparsersyntax.html#GroupingBoolean">
the documents</a> for a more complete discussion.
