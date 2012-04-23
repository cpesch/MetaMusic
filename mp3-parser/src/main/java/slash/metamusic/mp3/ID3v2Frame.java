/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2001-2003 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.mp3;

import slash.metamusic.mp3.sections.*;
import slash.metamusic.mp3.util.BitConversion;

import javax.activation.MimeType;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * My instances represent a ID3v2 frame of the ID3v2 header as
 * described in http://www.id3.org/id3v2.3.0.html#sec3.3.
 *
 * @author Christian Pesch
 * @version $Id: ID3v2Frame.java 958 2007-02-28 14:44:37Z cpesch $
 */

public class ID3v2Frame {
    /**
     * Logging output
     */
    protected static final Logger log = Logger.getLogger(ID3v2Frame.class.getName());

    public static final int OBSOLETE_SIZE_SIZE = 3;

    public static final int SIZE_SIZE = 4;
    public static final int FLAG_SIZE = 2;
    public static final int EXTRA_SIZE = 5;


    protected ID3v2Frame(ID3v2Tag tag, ID3v2Version version, byte[] content) {
        this.tag = tag;
        this.version = version;
        if (tag != null) {
            this.fileAlterDiscard = tag.isDefaultFileAlterPreservation();
            if (content != null) {
                try {
                    if (!parseContent(content)) {
                        Thread.dumpStack();
                        log.severe("Cannot parse " + new String(content));
                    }
                } catch (IOException e) {
                    log.severe("Cannot parse " + new String(content));
                }
            } else
                this.sections = tag.getSections();

            if (version == null)
                this.version = tag.getVersion();
        } else {
            if (version == null)
                this.version = new ID3v2Version();
        }
    }

    /**
     * Created while reading ID3v2Header
     * @param version the version to use
     */
    ID3v2Frame(ID3v2Version version) {
        this(null, version, null);
    }

    /**
     * Created while adding new frames
     * @param tagName the name of the {@link ID3v2Tag}
     * @param version the version to use
     */
    ID3v2Frame(String tagName, ID3v2Version version) {
        this(new ID3v2Tag(tagName), version, null);
    }

    public ID3v2Frame(String tagName) {
        this(tagName, null);
    }

    public ID3v2Version getVersion() {
        return version;
    }

    void migrateToVersion(ID3v2Version current) {
        if (!getVersion().equals(current)) {
            // crude migration
            if (tag.getName().equals("PIC")) {
                sections.add(getSectionIndex(DescriptionSection.class), new PictureTypeSection());
            }
            tag.migrateToVersion(current);
            version = current;
        }
    }

    // --- read object -----------------------------------------

    /**
     * Pulls out information from the frame.
     *
     * @param data   the frame data
     * @param offset the offset from which to read
     * @return the number of bytes read
     * @throws IOException if parsing fails due to IO problems
     */
    public int parse(byte[] data, int offset) throws IOException {
        valid = false;

        tag = new ID3v2Tag(getVersion());
        int tagSize = getVersion().getTagSize();

        // frame name would be behind end of data
        if (offset + tagSize > data.length) {
            return 0;
        }

        // tag is always in ISO-8859-1 encoded
        String tagName = new String(data, offset, tagSize, ID3v2Header.ISO_8859_1_ENCODING);
        tag.setName(tagName);
        this.sections = tag.getSections();
        if (!tag.isValid())
            return 0;

        // ID3v2 3.0 has 4 bytes size, 2.0 just 3
        int sizeOffset = offset + tagSize;
        int sizeSize = getVersion().isObsolete() ? OBSOLETE_SIZE_SIZE : SIZE_SIZE;

        // frame size be behind length of data
        if (sizeOffset + sizeSize > data.length) {
            return 0;
        }

        // ID3v2 3.0 has 4 bytes size, 2.0 just 3
        int frameSize = getVersion().isObsolete() ?
                BitConversion.extract3BigEndian(data, sizeOffset) :
                BitConversion.extract4BigEndian(data, sizeOffset);

        // if frame size is zero or less, this frame is invalid
        if (frameSize <= 0)
            return 0;

        // if frame frame is longer than data, this frame is invalid
        if (frameSize > data.length)
            return 0;

        // ID3v2 2.0 has no flags and encoding
        int flagOffset = sizeOffset + sizeSize;
        int flagSize = getVersion().isObsolete() ? 0 : FLAG_SIZE;

        if (!getVersion().isObsolete()) {
            parseFlags(data, flagOffset);
            // TODO flags are not really used by now - no encryption, compression etc.
            // TODO parsing extra flags does not work
            parseExtraFlags(data, flagOffset + flagSize);
        }

        int contentOffset = flagOffset + flagSize;
        //noinspection UnnecessaryLocalVariable
        int contentSize = frameSize;

        // if frame frame is longer than data, this frame is invalid
        if (contentOffset + contentSize > data.length)
            return 0;

        byte[] content = new byte[contentSize];
        System.arraycopy(data, contentOffset, content, 0, contentSize);

        // if the content cannot be parsed, this frame is invalid
        valid = parseContent(content);
        if (!valid)
            return 0;

        return tagSize + sizeSize + flagSize + frameSize;
    }

    /**
     * Read the information from the flags array.
     *
     * @param data   the flags found in the frame header
     * @param offset the offset from which to read
     */
    protected void parseFlags(byte[] data, int offset) {
        if (data.length < offset + FLAG_SIZE) {
            log.severe("Error parsing flags of frame: " + tag + ". " +
                    "Expected flags not in data. ");
        } else {
            byte first = data[offset];
            tagAlterDiscard = BitConversion.getBit(first, 6) == 1;
            fileAlterDiscard = BitConversion.getBit(first, 5) == 1;
            readOnly = BitConversion.getBit(first, 4) == 1;

            byte second = data[offset + 1];
            grouped = BitConversion.getBit(second, 6) == 1;
            compressed = BitConversion.getBit(second, 3) == 1;
            encrypted = BitConversion.getBit(second, 2) == 1;
            unsynchronized = BitConversion.getBit(second, 1) == 1;
            lengthIndicator = BitConversion.getBit(second, 0) == 1;

            if (compressed && !lengthIndicator)
                log.severe("Error parsing flags of frame: " + tag + ". " +
                        "Compressed bit set without data length bit set.");
        }
    }

    /**
     * Pulls out extra information inserted in the frame data depending
     * on what flags are set.
     *
     * @param data   the frame data
     * @param offset the offset from which to read
     * @return the amount of bytes read
     */
    protected int parseExtraFlags(byte[] data, int offset) {
        int bytesRead = 0;

        if (grouped) {
            group = data[offset + bytesRead];
            bytesRead += 1;

            // System.out.println("group: " + group);
        }

        if (encrypted) {
            encryption = data[offset + bytesRead];
            bytesRead += 1;

            // System.out.println("encryption: " + encryption);
        }

        if (lengthIndicator) {
            byte[] size = new byte[SIZE_SIZE];
            System.arraycopy(data, offset + bytesRead, size, 0, size.length);
            dataLength = BitConversion.extract4BigEndian(size);
            bytesRead += size.length;

            // System.out.println("dataLength: " + dataLength);
        }

        return bytesRead;
    }

    /**
     * Parse the content of the frame.
     *
     * @param content the frame content
     * @return true, if the content was parse successfully
     * @throws IOException if parsing failed
     */
    protected boolean parseContent(byte[] content) throws IOException {
        if (content == null)
            return false;

        int offset = 0;
        for (AbstractSection section : sections) {
            // not enough content to parse
            if (offset > content.length)
                return false;
            int bytesRead = section.parse(content, offset, this);
            if (bytesRead > 0) {
                offset += bytesRead;
            }
        }
        return true;
    }

    // --- write object ----------------------------------------

    /**
     * Return the byte representation of this frame.
     *
     * @return an array with the byte representation of this frame
     * @throws UnsupportedEncodingException if some encoding fails
     */
    public byte[] getBytes() throws UnsupportedEncodingException {
        byte[] data = new byte[(int) getFrameSize()];
        int offset = 0;

        byte[] tagData = tag.getBytes();
        System.arraycopy(tagData, 0, data, 0, tagData.length);
        offset += tagData.length;

        byte[] tagSize = getVersion().isObsolete() ?
                BitConversion.create3BigEndian((int) getContentSize()) :
                BitConversion.create4BigEndian((int) getContentSize());
        System.arraycopy(tagSize, 0, data, offset, tagSize.length);
        offset += tagSize.length;

        // ID3v2 2.0 has no flags and encoding
        if (!getVersion().isObsolete()) {
            byte[] tagFlags = getFlagBytes();
            System.arraycopy(tagFlags, 0, data, offset, tagFlags.length);
            offset += tagFlags.length;

            byte[] tagExtra = getExtraDataBytes();
            System.arraycopy(tagExtra, 0, data, offset, tagExtra.length);
            offset += tagExtra.length;
        }

        byte[] tagContent = getContentBytes();
        System.arraycopy(tagContent, 0, data, offset, tagContent.length);

        return data;
    }

    /**
     * A helper function for the getFrameBytes method that processes the
     * info in the frame and returns the FLAG_SIZE byte array of flags
     * to be added to the header.
     *
     * @return a value of type 'byte[]'
     */
    protected byte[] getFlagBytes() {
        byte flags[] = {0x00, 0x00};

        if (tagAlterDiscard) {
            flags[0] = BitConversion.setBit(flags[0], 6);
        }
        if (fileAlterDiscard) {
            flags[0] = BitConversion.setBit(flags[0], 5);
        }
        if (readOnly) {
            flags[0] = BitConversion.setBit(flags[0], 4);
        }
        if (grouped) {
            flags[1] = BitConversion.setBit(flags[1], 6);
        }
        if (compressed) {
            flags[1] = BitConversion.setBit(flags[1], 3);
        }
        if (encrypted) {
            flags[1] = BitConversion.setBit(flags[1], 2);
        }
        if (unsynchronized) {
            flags[1] = BitConversion.setBit(flags[1], 1);
        }
        if (lengthIndicator) {
            flags[1] = BitConversion.setBit(flags[1], 0);
        }

        return flags;
    }

    /**
     * A helper function for the getFrameBytes function that returns an array
     * of all the data contained in any extra fields that may be present in
     * this frame.  This includes the group, the encryption type, and the
     * length indicator.  The length of the array returned is variable length.
     *
     * @return an array of bytes containing the extra data fields in the frame
     */
    protected byte[] getExtraDataBytes() {
        byte[] buf = new byte[EXTRA_SIZE];
        int bytesCopied = 0;

        if (grouped) {
            buf[bytesCopied] = group;
            bytesCopied += 1;
        }
        if (encrypted) {
            buf[bytesCopied] = encryption;
            bytesCopied += 1;
        }
        if (lengthIndicator) {
            byte[] size = BitConversion.create4BigEndian(dataLength);
            System.arraycopy(size, 0, buf, bytesCopied, size.length);
            bytesCopied += size.length;
        }

        byte[] result = new byte[bytesCopied];
        System.arraycopy(buf, 0, result, 0, bytesCopied);

        return result;
    }

    protected byte[] getContentBytes() {
        byte[] result = new byte[0];
        for (AbstractSection section : sections) {
            try {
                byte[] bytes = section.getBytes(this);
                byte[] newResult = new byte[result.length + bytes.length];
                System.arraycopy(result, 0, newResult, 0, result.length);
                System.arraycopy(bytes, 0, newResult, result.length, bytes.length);
                result = newResult;
            } catch (UnsupportedEncodingException e) {
                log.severe("Cannot encode frame: " + e.getMessage());
                return new byte[0];
            }
        }
        return result;
    }

    // --- get object ------------------------------------------

    public boolean isValid() {
        return valid;
    }

    public long getFrameSize() {
        return tag.getName().length() +
                (getVersion().isObsolete() ? OBSOLETE_SIZE_SIZE : (SIZE_SIZE + FLAG_SIZE)) +
                getContentSize();

        /* TODO does not work
        if(grouped) {
          size += 1;
        }

        if(encrypted) {
          size += 1;
        }

        if(lengthIndicator) {
          size += 4;
        }

        return size;
        */
    }

    public long getContentSize() {
        return getContentBytes().length;
    }


    public boolean isTagWithName(String tagName) {
        // queries always go for the ID3v2 3.0 names but tags with 2.0 names are found, too
        return tagName.equals(tag.getName()) || tagName.equals(tag.getSuccessorName());
    }

    public String getTagName() {
        return tag.getName();
    }

    public String getTagDescription() {
        return tag.getDescription();
    }

    public String getTextEncoding() {
        TextEncodingSection textEncodingSection = findSection(TextEncodingSection.class);
        return textEncodingSection != null ? textEncodingSection.getEncoding() : ID3v2Header.ISO_8859_1_ENCODING;
    }

    public String getTextContent() {
        TextSection textSection = findSection(TextSection.class);
        return textSection != null ? textSection.getText() : null;
    }

    public byte[] getByteContent() {
        BytesSection bytesSection = findSection(BytesSection.class);
        return bytesSection != null ? bytesSection.getBytes() : new byte[0];
    }

    public String getStringContent() {
        StringBuffer buffer = new StringBuffer();

        for (AbstractSection section : sections) {
            String stringContent = section.getStringContent();
            if (stringContent != null && stringContent.length() > 0) {
                if (buffer.length() > 0)
                    buffer.append(",");
                buffer.append(stringContent);
            }
        }
        return buffer.toString();
    }

    public String getDescription() {
        DescriptionSection descriptionSection = findSection(DescriptionSection.class);
        return descriptionSection != null ? descriptionSection.getDescription() : null;
    }

    public String getLanguage() {
        LanguageSection languageSection = findSection(LanguageSection.class);
        return languageSection != null ? languageSection.getLanguage() : null;
    }

    public MimeType getMimeType() {
        MimeTypeSection mimeTypeSection = findSection(MimeTypeSection.class);
        return mimeTypeSection != null ? mimeTypeSection.getMimeType() : null;
    }


    @SuppressWarnings({"unchecked"})
    public <T extends AbstractSection> T findSection(Class<T> sectionClass) {
        List<T> abstractSections = (List<T>) sections;
        for (T section : abstractSections) {
            if (section.getClass().equals(sectionClass))
                return section;
        }
        return null;
    }

    private int getSectionIndex(Class sectionClass) {
        for (int i = 0; i < sections.size(); i++) {
            AbstractSection section = sections.get(i);
            if (section.getClass().equals(sectionClass))
                return i;
        }
        return -1;
    }

    public void setTextEncoding(String encoding) {
        TextEncodingSection encodingSection = findSection(TextEncodingSection.class);
        if (encodingSection == null)
            throw new IllegalArgumentException("No text encoding section in " + tag.getName() + " frame");
        encodingSection.setEncoding(encoding);
    }

    public void setText(String text) {
        TextSection textSection = findSection(TextSection.class);
        if (textSection == null)
            throw new IllegalArgumentException("No text section in " + tag.getName() + " frame");
        textSection.setText(text);
    }

    public void setDescription(String description) {
        DescriptionSection descriptionSection = findSection(DescriptionSection.class);
        if (descriptionSection == null)
            throw new IllegalArgumentException("No description section in " + tag.getName() + " frame");
        descriptionSection.setDescription(description);
    }

    public void setLanguage(String language) {
        LanguageSection languageSection = findSection(LanguageSection.class);
        if (languageSection == null)
            throw new IllegalArgumentException("No language section in " + tag.getName() + " frame");
        languageSection.setLanguage(language);
    }

    public void setPictureType(PictureType picturetype) {
        // no PictureTypeSection in ID3v2 2.0 PIC frame
        if (getVersion().isObsolete())
            return;
        PictureTypeSection typeSection = findSection(PictureTypeSection.class);
        if (typeSection == null)
            throw new IllegalArgumentException("No picture type section in " + tag.getName() + " frame");
        typeSection.setPictureType(picturetype);
    }

    public void setMimeType(MimeType mimetype) {
        MimeTypeSection typeSection = findSection(MimeTypeSection.class);
        if (typeSection == null)
            throw new IllegalArgumentException("No mime type section in " + tag.getName() + " frame");
        typeSection.setMimeType(mimetype);
    }

    public void setBytes(byte[] bytes) {
        BytesSection bytesSection = findSection(BytesSection.class);
        if (bytesSection == null)
            throw new IllegalArgumentException("No bytes section in " + tag.getName() + " frame");
        bytesSection.setBytes(bytes);
    }

    // --- overwrites Object -----------------------------------

    public String toString() {
        return "ID3v2Frame[" +
                "valid=" + isValid() + ", " +
                "tag=" + tag + ", " +
                "frameSize=" + getFrameSize() + ", " +
                "contentSize=" + getContentSize() + ", " +
                "tagAlterDiscard=" + tagAlterDiscard + ", " +
                "fileAlterDiscard=" + fileAlterDiscard + ", " +
                "readOnly=" + readOnly + ", " +
                "grouped=" + grouped + ", " +
                "compressed=" + compressed + ", " +
                "encrypted=" + encrypted + ", " +
                "unsynchronized=" + unsynchronized + ", " +
                "lengthIndicator=" + lengthIndicator + ", " +
                "sections=" + sections +
                "]";
    }

    // --- member variables ------------------------------------

    /**
     * ID3v2Frame data
     */
    protected boolean valid;
    private ID3v2Version version;
    protected ID3v2Tag tag;

    /**
     * flags
     */
    protected boolean tagAlterDiscard = false;
    protected boolean fileAlterDiscard = false;
    protected boolean readOnly = false;
    protected boolean grouped = false;
    protected boolean compressed = false;
    protected boolean encrypted = false;
    protected boolean unsynchronized = false;
    protected boolean lengthIndicator = false;
    protected byte group = '0';
    protected byte encryption = '0';
    protected int dataLength = -1;

    /**
     * sections
     */
    protected List<AbstractSection> sections = new ArrayList<AbstractSection>(1);
}
