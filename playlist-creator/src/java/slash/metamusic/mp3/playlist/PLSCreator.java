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
 * A class to create PLS play lists from file system trees.
 *
 * @author Christian Pesch
 * @version $Id: PLSCreator.java 913 2006-12-26 20:43:03Z cpesch $
 */

public class PLSCreator extends AbstractPlaylistCreator {

    protected String getExtension() {
        return "pls";
    }

    protected void writeHeader(FileWriter writer) throws IOException {
        writer.write("[playlist]");
        writer.write('\n');
    }

    protected void writeMP3(FileWriter writer, MP3File mp3, String fileName, int index) throws IOException {
        int number = index + 1;
        writer.write("File" + number + "=" + fileName);
        writer.write('\n');
        writer.write("Title" + number + "=" + mp3.getArtist() + " - " + mp3.getAlbum() + " - " + mp3.getTrack());
        writer.write('\n');
        writer.write("Length" + number + "=" + mp3.getSeconds());
        writer.write('\n');
    }

    protected void writeFooter(FileWriter writer, int count) throws IOException {
        writer.write("NumberOfEntries=" + count);
        writer.write('\n');
        writer.write("Version=2");
        writer.write('\n');
    }


    public static void main(String[] args) throws Exception {
        main(new PLSCreator(), args);
    }
}
