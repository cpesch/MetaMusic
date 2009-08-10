/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2005 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.mp3.playlist;

import slash.metamusic.mp3.MP3File;

import java.io.FileWriter;
import java.io.IOException;

/**
 * A class to create M3U play lists from file system trees.
 *
 * @author Christian Pesch
 * @version $Id: M3UCreator.java 913 2006-12-26 20:43:03Z cpesch $
 */

public class M3UCreator extends AbstractPlaylistCreator {

    protected String getExtension() {
        return "m3u";
    }

    protected void writeHeader(FileWriter writer) throws IOException {
        writer.write("#EXTM3U");
        writer.write('\n');
    }

    protected void writeMP3(FileWriter writer, MP3File mp3, String fileName, int index) throws IOException {
        writer.write("#EXTINF:" + mp3.getSeconds() + "," + mp3.getArtist() + " - " + mp3.getAlbum() + " - " + mp3.getTrack());
        writer.write('\n');
        writer.write(fileName);
        writer.write('\n');
    }

    protected void writeFooter(FileWriter writer, int count) throws IOException {
    }

    public static void main(String[] args) throws Exception {
        main(new M3UCreator(), args);
    }
}
