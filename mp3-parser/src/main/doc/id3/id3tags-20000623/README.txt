The following is a description of the files in this directory:

221-compressed.tag

  This ID3v2.2.1 tag contains a Compressed Data Metaframe.  Tag editors should
  no longer create such old tags, but should be able to correctly parse them.

230-compressed.tag

  This ID3v2.3.0 tag contains a compressed frame.  The frame has the
  'compression' bit set in it's frame header.  This method for compressing 
  frames supercedes the 2.2.1 Compressed Data Metaframe.

230-picture.tag

  This ID3v2.3.0 tag contains a variety of frames, including an Attached 
  Picture frame (APIC) and an Involved People List frame (IPLS).  It is also
  an example of ID3v2's unsynchronization scheme.

230-syncedlyrics.tag

  This ID3v2.3.0 tag is converted using id3lib-3.7.10 from example.lyr, a 
  Lyrics3 v2.00 tag file.  It includes an example of the Synchronized Lyrics 
  frame.

230-unicode.tag

  This ID3v2.3.0 tag has a single frame comprised of unicode tag.  Earlier 
  versions of this tag were incorrect in the byte-order marker (BOM) contained
  within the tag, so correct implementations of the ID3v2 spec wouldn't be able
  to parse that version of the tag.  The BOM's have since been corrected.

ozzy.tag

  This ID3v2.3.0 tag is from an mp3 file submitted by a user who found a bug in
  earlier versions of id3lib.  It was converted from the old MusicMatch tagging
  format by the MusicMatch Jukebox application.

thatspot.tag

  This ID3v2.3.0 tag is also converted from the MusicMatch Jukebox application.
  It is the tag from the sample mp3 file distributed with free copies of 
  Jukebox.  It contains a frame not defined in the ID3v2.3.0 specification
  (TRDO), so is a good test for implementations of the spec to see how well
  they handle unknown frames.

example.lyr

  This is the Lyrics3 v2.00 tag given as an example of the format at the 
  following URL: http://www.id3.org/lyrics3200.html.  This tag was converted
  to 230-syncedlyrics.tag by id3lib-3.7.10.

composer.jpg

  This is the picture file used to create 230-picture.tag.
