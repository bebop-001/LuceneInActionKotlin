
I wanted to learn how to display the results from
a search so I wrote this.

This code indexes the .txt data files supplied
with the source from Lucene In Action and allows
you to search for words and displays the "line"
the result was founde in.

I read the text files, break them into max 80 char
lines and create a .csv file with the file name,
line number and text for each file.  This is
indexed.  You search by the word and the result
is displayed with the file and line number.  I
used the csv file because I didn't want to
put the full text that's being searched into
Lucene.  Ultimately the search info will be
used to refer back to the SQL it will be 
generated from.

The data .txt files are stored in the data directory
and all indexes will be found under indexes/&lt;package name&gt;.

