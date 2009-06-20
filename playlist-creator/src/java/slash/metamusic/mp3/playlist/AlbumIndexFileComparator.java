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
        if (album1 != null && album1.equals(album2)) {
            int index1 = f1.getIndex();
            int index2 = f2.getIndex();
            return index1 - index2;
        }
        int index1 = DiscIndexHelper.parseDiscIndex(album1);
        int index2 = DiscIndexHelper.parseDiscIndex(album2);
        if (index1 != index2)
            return index1 - index2;
        return f1.getFile().compareTo(f2.getFile());
    }
}
