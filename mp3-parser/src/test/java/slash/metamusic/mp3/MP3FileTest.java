/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2005 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.mp3;

import slash.metamusic.mp3.sections.*;
import slash.metamusic.test.AbstractFileTest;
import slash.metamusic.util.ArrayHelper;
import slash.metamusic.util.InputOutput;
import slash.metamusic.util.URLLoader;

import javax.activation.MimeType;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Tests for the MP3File parser
 */

public class MP3FileTest extends AbstractFileTest {

    public MP3FileTest(String name) {
        super(name);
    }

    private void checkMP3Properties(MP3File mp3) {
        assertEquals(true, mp3.isMP3());
        assertEquals(256000, mp3.getBitRate());
        assertEquals(48000, mp3.getSampleFrequency());
        assertEquals(1, mp3.getSeconds());
    }

    private void setID3WithoutGenreAndCount(ID3MetaData mp3) {
        mp3.setArtist("Artist");
        mp3.setComment("Comment");
        mp3.setAlbum("Album");
        mp3.setTrack("Track");
        mp3.setIndex(42);
        mp3.setYear(2001);
    }

    private void setID3(MP3File mp3) {
        setID3WithoutGenreAndCount(mp3);
        mp3.setGenre(new ID3Genre("Freestyle"));
        mp3.setCount(84);
        mp3.setSeconds(14);
    }

    private void checkID3WithoutGenreAndCommentAndCount(MP3File mp3) {
        assertEquals(768, ((MP3Properties) mp3.getProperties()).getFrameSize());
        assertEquals(21, ((MP3Properties) mp3.getProperties()).getFrames());
        checkMP3Properties(mp3);
        assertEquals("Artist", mp3.getArtist());
        assertEquals("Album", mp3.getAlbum());
        assertEquals("Track", mp3.getTrack());
        assertEquals(42, mp3.getIndex());
        assertEquals(2001, mp3.getYear());
    }

    private void checkID3(MP3File mp3) {
        checkID3WithoutGenreAndCommentAndCount(mp3);
        assertEquals("Comment", mp3.getComment());
        assertEquals(119, mp3.getGenre().getId());
        assertEquals(new ID3Genre(ID3Genre.getGenreName(119)), mp3.getGenre());
        assertEquals(new ID3Genre(ID3Genre.getGenreId("Freestyle")), mp3.getGenre());
    }

    private MP3File checkID3WithoutCount(File tempFile) throws IOException {
        MP3File mp3 = new MP3File();
        assertTrue(mp3.read(tempFile));
        assertEquals(tempFile.length(), mp3.getFileSize());
        checkID3(mp3);
        return mp3;
    }

    private MP3File checkID3(File tempFile) throws IOException {
        MP3File mp3 = new MP3File();
        assertTrue(mp3.read(tempFile));
        assertEquals(tempFile.length(), mp3.getFileSize());
        checkID3(mp3);
        assertEquals(84, mp3.getCount());
        return mp3;
    }

    private void checkNoID3(MP3File mp3) {
        assertEquals(false, mp3.isID3v1());
        assertEquals(false, mp3.isID3v1dot1());
        assertEquals(false, mp3.isID3v2());
        assertEquals(tempFile.getName().substring(0, tempFile.getName().length() - 4), mp3.getArtist());
        assertEquals(null, mp3.getAlbum());
        assertEquals(null, mp3.getTrack());
        assertEquals(null, mp3.getComment());
        assertEquals(-1, mp3.getIndex());
        assertEquals(0, mp3.getHead().getReadSize());
        assertEquals(0, mp3.getHead().getContentSize());
        assertEquals(0, mp3.getHead().getFrameSize());
        assertEquals(0, mp3.getHead().getHeaderSize());
        assertEquals(0, mp3.getHead().getWriteSize());
        assertEquals(0, mp3.getTail().getReadSize());
        assertEquals(0, mp3.getTail().getContentSize());
        assertEquals(0, mp3.getTail().getWriteSize());
    }

    public void testReadNoID3() throws IOException {
        File src = new File(AbstractFileTest.PATH_TO_TEST_MP3_FILES + "noid3.mp3");
        copyToTempFile(src);

        MP3File mp3 = new MP3File();
        assertTrue(mp3.read(tempFile));
        log.fine("testReadNoID3#mp3 " + mp3);
        assertEquals(src.length(), mp3.getFileSize());
        assertEquals(0, mp3.getProperties().getReadSize());
        checkMP3Properties(mp3);
        assertEquals(-1, mp3.getCount());
        checkNoID3(mp3);
    }

    public void testRiffPrefix() throws IOException {
        File src = new File(AbstractFileTest.PATH_TO_TEST_MP3_FILES + "riffprefix.mp3");
        copyToTempFile(src);

        MP3File mp3 = new MP3File();
        assertTrue(mp3.read(tempFile));
        log.fine("testRiffPrefix#mp3 " + mp3);
        assertEquals(src.length(), mp3.getFileSize());
        assertEquals(487, mp3.getProperties().getReadSize());
        checkMP3Properties(mp3);
        assertEquals(-1, mp3.getCount());
        checkNoID3(mp3);
    }

    public void testReadOgg() throws IOException {
        File src = new File(AbstractFileTest.PATH_TO_TEST_MP3_FILES + "sound.ogg");
        copyToTempFile(src);

        MP3File mp3 = new MP3File();
        assertTrue(mp3.read(tempFile));
        log.fine("testReadOgg#mp3 " + mp3);
        assertEquals(src.length(), mp3.getFileSize());
        assertEquals(false, mp3.isMP3());
        assertEquals(true, mp3.isOgg());
    }

    public void testReadWav() throws IOException {
        File src = new File(AbstractFileTest.PATH_TO_TEST_MP3_FILES + "sound.wav");
        copyToTempFile(src);

        MP3File mp3 = new MP3File();
        assertTrue(mp3.read(tempFile));
        log.fine("testReadWav#mp3 " + mp3);
        assertEquals(src.length(), mp3.getFileSize());
        assertEquals(false, mp3.isMP3());
        assertEquals(true, mp3.isWAV());
        assertEquals(352800, mp3.getBitRate());
        assertEquals(22050, mp3.getSampleFrequency());
        assertEquals(false, mp3.isVBR());
        assertEquals(8, mp3.getSeconds());
    }

    public void testReadMP3() throws IOException {
        File src = new File(AbstractFileTest.PATH_TO_TEST_MP3_FILES + "sound.mp3");
        copyToTempFile(src);

        MP3File mp3 = new MP3File();
        assertTrue(mp3.read(tempFile));
        log.fine("testReadMP3#mp3 " + mp3);
        assertEquals(src.length(), mp3.getFileSize());
        assertEquals(true, mp3.isMP3());
        assertEquals(false, mp3.isWAV());
        assertEquals(false, mp3.isOgg());
        assertEquals(32000, mp3.getBitRate());
        assertEquals(22050, mp3.getSampleFrequency());
        assertEquals(false, mp3.isVBR());
        assertEquals(9, mp3.getSeconds());
    }

    public void testReadWithID3v1() throws IOException {
        File src = new File(AbstractFileTest.PATH_TO_TEST_MP3_FILES + "withid3v1.mp3");
        copyToTempFile(src);
        MP3File mp3 = checkID3WithoutCount(tempFile);
        assertEquals(true, mp3.isID3v1());
        assertEquals(true, mp3.isID3v1dot1());
        assertEquals(false, mp3.isID3v2());
    }

    public void testReadWithID3v2() throws IOException {
        File src = new File(AbstractFileTest.PATH_TO_TEST_MP3_FILES + "withid3v2.mp3");
        copyToTempFile(src);
        MP3File mp3 = checkID3(tempFile);
        assertEquals(false, mp3.isID3v1());
        assertEquals(false, mp3.isID3v1dot1());
        assertEquals(true, mp3.isID3v2());
    }

    public void testReadWithID3v1and2() throws IOException {
        File src = new File(AbstractFileTest.PATH_TO_TEST_MP3_FILES + "withid3v1and2.mp3");
        copyToTempFile(src);
        MP3File mp3 = checkID3(tempFile);
        assertEquals(true, mp3.isID3v1());
        assertEquals(true, mp3.isID3v1dot1());
        assertEquals(true, mp3.isID3v2());
    }

    public void testWriteNoID3() throws IOException {
        File src = new File(AbstractFileTest.PATH_TO_TEST_MP3_FILES + "noid3.mp3");
        copyToTempFile(src);

        MP3File mp3 = new MP3File();
        assertTrue(mp3.read(src));
        mp3.setID3v1(false);
        mp3.setID3v2(false);
        log.fine("testWriteNoID3#mp3 " + mp3);
        mp3.write(tempFile);
        assertTrue(mp3.read(tempFile));

        assertEquals(src.length(), mp3.getFileSize());
        assertEquals(0, mp3.getProperties().getReadSize());
        checkMP3Properties(mp3);
        assertEquals(-1, mp3.getCount());
        checkNoID3(mp3);
    }

    public void testWriteRiffPrefix() throws IOException {
        File src = new File(AbstractFileTest.PATH_TO_TEST_MP3_FILES + "riffprefix.mp3");
        copyToTempFile(src);

        MP3File mp3 = new MP3File();
        assertTrue(mp3.read(src));
        mp3.setID3v1(false);
        mp3.setID3v2(false);
        long riffPrefixSize = mp3.getProperties().getReadSize() - mp3.getHead().getReadSize();
        log.fine("testWriteRiffPrefix#mp3 " + mp3);
        mp3.write(tempFile);
        assertTrue(mp3.read(tempFile));

        assertEquals(src.length() - riffPrefixSize, mp3.getFileSize());
        assertEquals(0, mp3.getProperties().getReadSize());
        checkMP3Properties(mp3);
        assertEquals(-1, mp3.getCount());
        checkNoID3(mp3);
    }

    public void testWriteID3v1() throws IOException {
        File src = new File(AbstractFileTest.PATH_TO_TEST_MP3_FILES + "noid3.mp3");
        copyToTempFile(src);

        MP3File mp3 = new MP3File();
        mp3.setID3v1(true);
        mp3.setID3v2(false);
        setID3(mp3);
        mp3.write(tempFile);

        mp3 = checkID3WithoutCount(tempFile);
        assertEquals(true, mp3.isID3v1());
        assertEquals(true, mp3.isID3v1dot1());
        assertEquals(false, mp3.isID3v2());
        assertEquals(0, mp3.getHead().getReadSize());
        assertEquals(0, mp3.getHead().getContentSize());
        assertEquals(0, mp3.getHead().getFrameSize());
        assertEquals(0, mp3.getHead().getWriteSize());
        assertEquals(0, mp3.getProperties().getReadSize());
        assertEquals(ID3v1Tail.ID3V1_SIZE, mp3.getTail().getReadSize());
        assertEquals(ID3v1Tail.ID3V1_SIZE, mp3.getTail().getWriteSize());
        assertEquals(-1, mp3.getCount());
    }

    private static final int headReadSize = 137;
    private static final int headContentSize = 57;
    private static final int headFrameSize = 10;

    public void testWriteID3v2ThreeDotZero() throws IOException {
        File src = new File(AbstractFileTest.PATH_TO_TEST_MP3_FILES + "noid3.mp3");
        copyToTempFile(src);

        MP3File mp3 = new MP3File();
        mp3.setID3v1(false);
        mp3.setID3v2(true);
        setID3(mp3);
        int frameCount = 7;
        log.fine("testWriteID3v2ThreeDotZero#mp3 " + mp3);
        mp3.write(tempFile);
        File dest = new File(tempFile.getParentFile(), "writeID3v2ThreeDotZero.mp3");
        tempFile.renameTo(dest);
        tempFile = dest;

        mp3 = checkID3(tempFile);
        assertEquals(false, mp3.isID3v1());
        assertEquals(false, mp3.isID3v1dot1());
        assertEquals(true, mp3.isID3v2());
        assertEquals(new ID3v2Version(3, 0), mp3.getHead().getVersion());
        assertEquals(src.length() + mp3.getHead().getWriteSize() + mp3.getTail().getWriteSize(), mp3.getFile().length());
        assertEquals(MP3FileTest.headReadSize, mp3.getHead().getReadSize());
        assertEquals(MP3FileTest.headContentSize, mp3.getHead().getContentSize());
        long frameSize = mp3.getHead().getFrameSize();
        assertEquals(mp3.getHead().getContentSize() + headFrameSize * frameCount, frameSize);
        long headerSize = mp3.getHead().getHeaderSize();
        long writeSize = headerSize + (64 - headerSize % 64) + 64;
        assertEquals(writeSize, mp3.getHead().getWriteSize());
        assertEquals(writeSize - headerSize, mp3.getProperties().getReadSize());
        assertEquals(0, mp3.getTail().getReadSize());
        assertEquals(0, mp3.getTail().getWriteSize());
    }

    private static final int obsoleteHeadReadSize = 109;
    private static final int obsoleteHeadFrameSize = 6;

    public void testWriteID3v2TwoDotZero() throws IOException {
        File src = new File(AbstractFileTest.PATH_TO_TEST_MP3_FILES + "noid3.mp3");
        copyToTempFile(src);

        MP3File mp3 = new MP3File();
        mp3.setID3v1(false);
        mp3.setID3v2(true);
        mp3.getHead().setVersion(new ID3v2Version(2, 0));
        setID3(mp3);
        int frameCount = 7;
        log.fine("testWriteID3v2TwoDotZero#mp3 " + mp3);
        mp3.write(tempFile);
        File dest = new File(tempFile.getParentFile(), "writeID3v2TwoDotZero.mp3");
        tempFile.renameTo(dest);
        tempFile = dest;

        mp3 = checkID3(tempFile);
        assertEquals(false, mp3.isID3v1());
        assertEquals(false, mp3.isID3v1dot1());
        assertEquals(true, mp3.isID3v2());
        assertEquals(new ID3v2Version(2, 0), mp3.getHead().getVersion());
        assertEquals(src.length() + mp3.getHead().getWriteSize() + mp3.getTail().getWriteSize(), mp3.getFile().length());
        assertEquals(obsoleteHeadReadSize, mp3.getHead().getReadSize());
        assertEquals(headContentSize, mp3.getHead().getContentSize());
        long frameSize = mp3.getHead().getFrameSize();
        assertEquals(mp3.getHead().getContentSize() + obsoleteHeadFrameSize * frameCount, frameSize);
        long headerSize = mp3.getHead().getHeaderSize();
        long writeSize = headerSize + (64 - headerSize % 64) + 64;
        assertEquals(writeSize, mp3.getHead().getWriteSize());
        assertEquals(writeSize - headerSize, mp3.getProperties().getReadSize());
        assertEquals(0, mp3.getTail().getReadSize());
        assertEquals(0, mp3.getTail().getWriteSize());
    }

    public void testWriteMetaMusicComment() throws IOException {
        File src = new File(AbstractFileTest.PATH_TO_TEST_MP3_FILES + "noid3.mp3");
        copyToTempFile(src);

        MP3File mp3 = new MP3File();
        mp3.setID3v1(true);
        mp3.setID3v2(true);
        setID3(mp3);
        ID3v2Frame frame = mp3.getHead().addID3v2Frame("TCON");
        frame.setText("Freestyle");
        mp3.setMetaMusicComment();
        mp3.write(tempFile);
        File dest = new File(tempFile.getParentFile(), "writeComment.mp3");
        tempFile.renameTo(dest);
        tempFile = dest;

        mp3 = new MP3File();
        mp3.read(tempFile);
        checkID3WithoutGenreAndCommentAndCount(mp3);
        assertEquals(true, mp3.isID3v1());
        assertEquals(true, mp3.isID3v1dot1());
        assertEquals(true, mp3.isID3v2());

        ID3v2Frame comm = mp3.getHead().getFrame("COMM", "Written", "English");
        assertNotNull(comm);
        assertTrue(comm.getTextContent().startsWith("Written by MetaMusic on"));
        assertEquals("English", comm.getLanguage());
        assertEquals("Written", comm.getDescription());
        assertTrue(mp3.getTail().getComment().startsWith("MetaMusic on"));

        ID3Genre genre = new ID3Genre("Freestyle");
        ID3v2Frame tcon = mp3.getHead().getFrame("TCON");
        assertEquals(genre.getFormattedName(), tcon.getStringContent());

        assertEquals(84, mp3.getCount());
    }

    public void testWriteGenre() throws IOException {
        File src = new File(AbstractFileTest.PATH_TO_TEST_MP3_FILES + "noid3.mp3");
        copyToTempFile(src);

        MP3File mp3 = new MP3File();
        mp3.setID3v1(false);
        mp3.setID3v2(true);
        setID3WithoutGenreAndCount(mp3);
        String genreName = "DoesNotExist";
        mp3.setGenre(new ID3Genre(genreName));
        assertEquals(genreName, mp3.getGenre().getName());
        assertEquals(genreName, mp3.getGenre().getFormattedName());
        assertEquals(-1, mp3.getGenre().getId());
        assertFalse(mp3.getGenre().isWellKnown());
        mp3.write(tempFile);
        File dest = new File(tempFile.getParentFile(), "writeGenre.mp3");
        tempFile.renameTo(dest);
        tempFile = dest;

        mp3 = new MP3File();
        mp3.read(tempFile);
        checkID3WithoutGenreAndCommentAndCount(mp3);
        assertEquals(genreName, mp3.getGenre().getName());
        assertEquals(genreName, mp3.getGenre().getFormattedName());
        assertEquals(-1, mp3.getGenre().getId());
        assertFalse(mp3.getGenre().isWellKnown());
    }

    public void testWriteFileTwice() throws IOException {
        File src = new File(AbstractFileTest.PATH_TO_TEST_MP3_FILES + "noid3.mp3");
        copyToTempFile(src);
        long originalFileSize = src.length();

        MP3File mp3 = new MP3File();
        assertTrue(mp3.read(tempFile));
        mp3.setID3v1(true);
        mp3.setID3v2(true);
        setID3(mp3);
        int frameCount = 7;
        log.fine("Writing mp3 " + mp3);
        mp3.write();

        assertEquals(originalFileSize + mp3.getHead().getWriteSize() + mp3.getTail().getWriteSize(), mp3.getFile().length());
        assertEquals(0, mp3.getHead().getReadSize());
        assertEquals(headContentSize, mp3.getHead().getContentSize());
        long frameSize = mp3.getHead().getFrameSize();
        assertEquals(mp3.getHead().getContentSize() + headFrameSize * frameCount, frameSize);
        long headerSize = mp3.getHead().getHeaderSize();
        long writeSize = headerSize + (64 - headerSize % 64) + 64;
        assertEquals(writeSize, mp3.getHead().getWriteSize());
        assertEquals(0, mp3.getProperties().getReadSize());
        assertEquals(0, mp3.getTail().getReadSize());
        assertEquals(ID3v1Tail.ID3V1_SIZE, mp3.getTail().getWriteSize());

        mp3 = checkID3(tempFile);

        assertEquals(true, mp3.isID3v1());
        assertEquals(true, mp3.isID3v1dot1());
        assertEquals(true, mp3.isID3v2());
        assertEquals(originalFileSize + mp3.getHead().getWriteSize() + mp3.getTail().getWriteSize(), mp3.getFile().length());
        assertEquals(headReadSize, mp3.getHead().getReadSize());
        assertEquals(headContentSize, mp3.getHead().getContentSize());
        frameSize = mp3.getHead().getFrameSize();
        assertEquals(mp3.getHead().getContentSize() + headFrameSize * frameCount, frameSize);
        headerSize = mp3.getHead().getHeaderSize();
        writeSize = headerSize + (64 - headerSize % 64) + 64;
        assertEquals(writeSize, mp3.getHead().getWriteSize());
        assertEquals(writeSize - headerSize, mp3.getProperties().getReadSize());
        assertEquals(ID3v1Tail.ID3V1_SIZE, mp3.getTail().getReadSize());
        assertEquals(ID3v1Tail.ID3V1_SIZE, mp3.getTail().getWriteSize());

        log.fine("Writing mp3 again " + mp3);
        mp3.write();

        mp3 = checkID3(tempFile);

        assertEquals(true, mp3.isID3v1());
        assertEquals(true, mp3.isID3v1dot1());
        assertEquals(true, mp3.isID3v2());
        assertEquals(originalFileSize + mp3.getHead().getWriteSize() + mp3.getTail().getWriteSize(), mp3.getFile().length());
        assertEquals(headReadSize, mp3.getHead().getReadSize());
        assertEquals(headContentSize, mp3.getHead().getContentSize());
        frameSize = mp3.getHead().getFrameSize();
        assertEquals(mp3.getHead().getContentSize() + headFrameSize * frameCount, frameSize);
        headerSize = mp3.getHead().getHeaderSize();
        writeSize = headerSize + (64 - headerSize % 64) + 64;
        assertEquals(writeSize, mp3.getHead().getWriteSize());
        assertEquals(writeSize - headerSize, mp3.getProperties().getReadSize());
        assertEquals(ID3v1Tail.ID3V1_SIZE, mp3.getTail().getReadSize());
        assertEquals(ID3v1Tail.ID3V1_SIZE, mp3.getTail().getWriteSize());
    }

    public void testWriteID3v1ThenID3v2() throws IOException {
        File src = new File(AbstractFileTest.PATH_TO_TEST_MP3_FILES + "noid3.mp3");
        copyToTempFile(src);

        MP3File mp3 = new MP3File();
        assertTrue(mp3.read(tempFile));
        mp3.setID3v1(true);
        setID3(mp3);
        mp3.write();

        mp3 = checkID3WithoutCount(tempFile);

        assertEquals(true, mp3.isID3v1());
        assertEquals(true, mp3.isID3v1dot1());
        assertEquals(false, mp3.isID3v2());
        assertEquals(-1, mp3.getCount());

        mp3.setID3v2(true);
        mp3.write();

        mp3 = checkID3WithoutCount(tempFile);

        assertEquals(true, mp3.isID3v1());
        assertEquals(true, mp3.isID3v1dot1());
        assertEquals(true, mp3.isID3v2());
        assertEquals(-1, mp3.getCount());
    }

    public void testWriteID3v2ThenID3v1() throws IOException {
        File src = new File(AbstractFileTest.PATH_TO_TEST_MP3_FILES + "noid3.mp3");
        copyToTempFile(src);

        MP3File mp3 = new MP3File();
        assertTrue(mp3.read(tempFile));
        mp3.setID3v2(true);
        setID3(mp3);
        mp3.write();

        mp3 = checkID3(tempFile);

        assertEquals(false, mp3.isID3v1());
        assertEquals(true, mp3.isID3v2());

        mp3.setID3v1(true);
        mp3.write();

        mp3 = checkID3(tempFile);

        assertEquals(true, mp3.isID3v1());
        assertEquals(true, mp3.isID3v1dot1());
        assertEquals(true, mp3.isID3v2());
    }

    public void testWriteID3FileName() throws IOException {
        File src = new File(AbstractFileTest.PATH_TO_TEST_MP3_FILES + "noid3.mp3");
        copyToTempFile(src);

        MP3File mp3 = new MP3File();
        setID3(mp3);

        String oldFileName = tempFile.getName();
        String oldPathName = tempFile.getParentFile().getAbsolutePath();
        mp3.rename(tempFile);
        tempFile = mp3.getFile();

        String newFileName = tempFile.getName();
        assertFalse(oldFileName.equals(newFileName));
        assertEquals(oldPathName, tempFile.getParentFile().getAbsolutePath());

        mp3.move(mp3.getFile());
        tempFile = mp3.getFile();

        String newPathName = tempFile.getParentFile().getAbsolutePath();
        assertFalse(oldPathName.equals(newPathName));
        assertEquals(newFileName, tempFile.getName());

        assertTrue(mp3.read(tempFile));
        assertEquals(false, mp3.isID3v1());
        assertEquals(false, mp3.isID3v1dot1());
        assertEquals(false, mp3.isID3v2());
        assertEquals(tempFile.length(), src.length());
        assertEquals(mp3.getFile().getName(), mp3.getFileName().getFileName());
        assertEquals(-1, mp3.getCount());
    }

    public void testWriteID3WithImage() throws Exception {
        File src = new File(AbstractFileTest.PATH_TO_TEST_MP3_FILES + "noid3.mp3");
        copyToTempFile(src);

        MP3File mp3 = new MP3File();
        mp3.setID3v1(false);
        mp3.setID3v2(true);
        setID3(mp3);
        ID3v2Frame frame = new ID3v2Frame("APIC");
        MimeType mimeType = new MimeType("image/gif");
        byte[] pictureData = URLLoader.getContents(new File(AbstractFileTest.PATH_TO_TEST_IMAGE_FILES + "dontpanic.gif"));
        frame.findSection(BytesSection.class).setBytes(pictureData);
        frame.findSection(MimeTypeSection.class).setMimeType(mimeType);
        frame.findSection(DescriptionSection.class).setDescription("Description");
        PictureType pictureType = PictureType.getPictureType(0x07);
        frame.findSection(PictureTypeSection.class).setPictureType(pictureType);
        mp3.getHead().add(frame);
        mp3.write(tempFile);
        File dest = new File(tempFile.getParentFile(), "writeID3WithImage.mp3");
        tempFile.renameTo(dest);
        tempFile = dest;

        mp3 = checkID3(tempFile);
        assertEquals(false, mp3.isID3v1());
        assertEquals(false, mp3.isID3v1dot1());
        assertEquals(true, mp3.isID3v2());
        frame = mp3.getHead().getFrame("APIC");
        assertEquals(mimeType.toString(), frame.findSection(MimeTypeSection.class).getMimeType().toString());
        assertEquals("Description", frame.findSection(DescriptionSection.class).getDescription());
        assertEquals(pictureType.getCode(), frame.findSection(PictureTypeSection.class).getPictureType().getCode());
        assertTrue(Arrays.equals(pictureData, frame.getByteContent()));
    }

    public void testID3v30BeforeV20Header() throws IOException {
        ID3v2Header v20 = new ID3v2Header();
        v20.setVersion(new ID3v2Version(2, 0));
        setID3WithoutGenreAndCount(v20);
        byte[] v20Bytes = v20.getBytes();

        File src = new File(AbstractFileTest.PATH_TO_TEST_MP3_FILES + "noid3.mp3");

        ID3v2Header v30 = new ID3v2Header();
        setID3WithoutGenreAndCount(v30);
        byte[] v30Bytes = v30.getBytes();

        assertFalse(ArrayHelper.equals(v20Bytes, v30Bytes));

        tempFile = File.createTempFile("filetest", ".mp3");

        FileInputStream in = new FileInputStream(src);
        FileOutputStream out = new FileOutputStream(tempFile);
        out.write(v30Bytes);
        out.write(v20Bytes);
        InputOutput inputOutput = new InputOutput(in, out);
        inputOutput.start();
        inputOutput.close();

        assertEquals(v30Bytes.length + v20Bytes.length + src.length(), tempFile.length());

        MP3File mp3 = MP3File.readValidFile(tempFile);
        assertEquals(275, mp3.getProperties().getReadSize());
        // TODO search for second ID3 head
    }

    public void testMigrateID3v20HeaderToV30() throws Exception {
        ID3v2Version version = new ID3v2Version(2, 0);

        File src = new File(AbstractFileTest.PATH_TO_TEST_MP3_FILES + "noid3.mp3");
        copyToTempFile(src);

        MP3File mp3 = new MP3File();
        mp3.setID3v1(false);
        mp3.setID3v2(true);
        mp3.getHead().setVersion(version);
        setID3(mp3);
        ID3v2Frame frame = new ID3v2Frame("PIC");
        assertEquals(version, frame.getVersion());
        MimeType mimeType = new MimeType("image/jpg");
        byte[] pictureData = URLLoader.getContents(new File(AbstractFileTest.PATH_TO_TEST_IMAGE_FILES + "dontpanic.jpg"));
        frame.findSection(BytesSection.class).setBytes(pictureData);
        frame.findSection(MimeTypeSection.class).setMimeType(mimeType);
        frame.findSection(DescriptionSection.class).setDescription("Description");
        assertNull(frame.findSection(PictureTypeSection.class));
        mp3.getHead().add(frame);
        mp3.write(tempFile);
        File dest = new File(tempFile.getParentFile(), "ID3v2TwoDotZero.mp3");
        tempFile.renameTo(dest);
        tempFile = dest;

        mp3 = checkID3(tempFile);
        assertEquals(false, mp3.isID3v1());
        assertEquals(false, mp3.isID3v1dot1());
        assertEquals(true, mp3.isID3v2());
        assertEquals(version, mp3.getHead().getVersion());
        frame = mp3.getHead().getFrame("PIC");
        assertEquals(mimeType.toString(), frame.findSection(MimeTypeSection.class).getMimeType().toString());
        assertEquals("Description", frame.findSection(DescriptionSection.class).getDescription());
        assertNull(frame.findSection(PictureTypeSection.class));
        assertTrue(Arrays.equals(pictureData, frame.getByteContent()));

        mp3.getHead().migrateToVersion(new ID3v2Version(3, 0));
        mp3.write(tempFile);
        dest = new File(tempFile.getParentFile(), "ID3v2ThreeDotZero.mp3");
        tempFile.renameTo(dest);
        tempFile = dest;

        mp3 = checkID3(tempFile);
        assertEquals(false, mp3.isID3v1());
        assertEquals(false, mp3.isID3v1dot1());
        assertEquals(true, mp3.isID3v2());
        assertEquals(new ID3v2Version(3, 0), mp3.getHead().getVersion());
        frame = mp3.getHead().getFrame("APIC");
        assertEquals(mimeType.toString(), frame.findSection(MimeTypeSection.class).getMimeType().toString());
        assertEquals("Description", frame.findSection(DescriptionSection.class).getDescription());
        assertEquals(PictureType.DEFAULT_PICTURE_TYPE, frame.findSection(PictureTypeSection.class).getPictureType());
        assertTrue(Arrays.equals(pictureData, frame.getByteContent()));
    }
}
