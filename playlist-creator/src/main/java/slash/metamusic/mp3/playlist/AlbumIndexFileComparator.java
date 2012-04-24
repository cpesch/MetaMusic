/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2004 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.mp3.playlist;

import slash.metamusic.mp3.MP3File;
import slash.metamusic.util.DiscIndexHelper;

import java.util.Comparator;

/**
 * Compares MP3Files by their album name, album index and track index on album
 *
 * @author Christian Pesch
 * @version $Id: AlbumIndexFileComparator.java 934 2006-12-30 16:40:34Z cpesch $
 */

class AlbumIndexFileComparator implements Comparator<MP3File> {

    public int compare(MP3File f1, MP3File f2) {
        String album1 = f1.getAlbum();
        String album2 = f2.getAlbum();

        // to parse "Same album name" but different part of set index
        if (album1 != null && album1.equals(album2)) {
            int partOfSetIndex1 = f1.getPartOfSetIndex();
            int partOfSetIndex2 = f2.getPartOfSetIndex();
            if (partOfSetIndex1 != -1 && partOfSetIndex1 != partOfSetIndex2)
                return partOfSetIndex1 - partOfSetIndex2;

            int trackIndex1 = f1.getIndex();
            int trackIndex2 = f2.getIndex();
            return trackIndex1 - trackIndex2;
        }

        // to parse "Album name" (disc "2")
        int discIndex1 = DiscIndexHelper.parseDiscIndex(album1);
        int discIndex2 = DiscIndexHelper.parseDiscIndex(album2);
        if (discIndex1 != -1 && discIndex1 != discIndex2)
            return discIndex1 - discIndex2;

        // different albums
        return f1.getFile().compareTo(f2.getFile());
    }
}
