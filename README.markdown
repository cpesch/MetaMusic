MetaMusic
=========

A [project](http://github.com/cpesch/MetaMusic) to develop tools to enhance the 
quality of the meta data of your music library. It helped me to maintain a well 
tagged music collection and it might help you.

The following tools exist:

RatingSaver
-----------

RatingSaver deals with the information 'My Rating', 'Counter' and 'Last Played', 
that [iTunes](http://apple.com/itunes/download) stores for each title inside its 
library.

Unfortunately this information is lost if you move or rename your music files 
from the location that is store inside the library. And since manually updating 
the locations and reimporting the XML version of the library is time-consuming 
and error-prone, RatingSaver

* stores 'My Rating', 'Counter' and 'Last Played' from your iTunes library as 
  [ID3v2](http://en.wikipedia.org/wiki/ID3) tags in 
  [MP3](http://en.wikipedia.org/wiki/MP3) files
* restores 'My Rating', 'Counter' and 'Last Played' from the ID3 tags of MP3 
  files into the iTunes library

Thus you may move or rename your MP3 files without losing the information 
'My Rating', 'Counter' and 'Last Played'.

MP3Tidy
-------

MP3Tidy processes the meta data of [MP3](http://en.wikipedia.org/wiki/MP3) files 
stored in the [ID3v1](http://en.wikipedia.org/wiki/ID3), ID3v2 and the the file 
name. It can

* enrich the meta data by adding cover albums from several source, lyrics, 
  publisher and compilation information
* remove unnecessary meta data that players like 
  [iTunes](http://apple.com/itunes/download), 
  [MusicMatch Jukebox](http://www.musicmatch.com/) (now Yahoo! Music Jukebox), 
  [Windows Media Player](http://www.microsoft.com/windows/windowsmedia) or 
  utilities such as 
  [MusicBrainzTagger()](http://musicbrainz.org/doc/MusicBrainzTagger) write into 
  the files
* unify meta data that is redundantly stored
* rename MP3 files according to their meta data
* write ID3v1 and/or ID3v2 tags

This you can created smaller MP3 files with consistent and complete meta data 
that are easier to find and arrange in your player or portable music player. 

MP3Viewer
---------

MP3Viewer shows the meta data of MP3 files in a window.

And more
--------

Besides these tools several command line tools exist:

* RecursiveTagSetter allows manipulation the meta data of many MP3 files
* MP3File shows the meta data of MP3 files on the command line
* AmazonMusic fetches a cover from Amazon given an artist and an album name
* CoverDBClient fetches a cover from several sources given an artist and an 
  album name
* LastFmCoverClient searches for covers written by the 
  [Last.fm](http://www.last.fm/download) client
* WindowsMediaPlayerCoverClient searches for covers written by the 
  [Windows Media Player](http://www.microsoft.com/windows/windowsmedia)
* DiscId calculates the disc id used by [FreeDB](http://www.freedb.org/) to
  identify audio CD
* CoverDownloader try to download covers for all known disc ids
* FreeDBClient fetches the meta data from [FreeDB](http://www.freedb.org/) for
  an audio CD 
* LyricsDBClient fetches lyrics from several sources given an artist and an 
  track name
* TRM calculates the TRM id used by [MusicBrainz](http://musicbrainz.org/) to
  identify audio files
* MusicBrainzClient queries the [MusicBrainz](http://musicbrainz.org/) database
  given a artist, album or track name
* M3UCreator creates a play list for music files in .m3u format
* PLSCreator creates a play list for music files in .pls format
* ID3v1TagCounter, ID3v2TagCounter count occurences of tags in many MP3 files
* PathLength calculates the path length of MP3 files in a directory tree
* MP3Extender extends the meta data of a MP3 file    
* MP3Mover moves a MP3 file according to its meta data
* MP3Cleaner removes unnecessary meta data from MP3 files
* MimeTypeGuesser tries to guess the 
  [MIME](http://en.wikipedia.org/wiki/Multipurpose_Internet_Mail_Extensions) 
  type of a file

FAQ
---

Q: Why Java?  
A: I like it. That is why I had more fun developing it.

Q: Why Java 6?  
A: It helped me to develop faster. 

Q: Does my music collection need to have a structure?  
A: A certain structure is not necessary. If however you'd like to use the option 
   to rename files from the meta date, MP3Tidy uses a scheme
       Name of the artist / Name of the album / Name of the title .mp3 
   If you're using RecursiveTagSetter you can declare files as an compilation 
   and MP3Tidy uses the scheme
	     Name of the compilation / Name of the title .mp3 
	 
Q: How do you develop?  
A: Currently, I'm using ant to build and IntelliJ IDEA to develop. There are
   plans to migrate the build process to Maven and get rid of the IDEA project
   files and the thirdparty directory.	
    
Q: How do I compile it?  
A: Set JAVA_HOME to a Java 6 SDK and call
       ant -f build/build.xml clean jar
   and find lots of jars in build/output/
	 
I hope you like it, feedback is always welcome  
Christian