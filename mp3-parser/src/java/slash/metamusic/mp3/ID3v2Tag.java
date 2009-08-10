/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2001-2003 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.mp3;

import slash.metamusic.mp3.sections.*;

import java.util.*;

/**
 * My instances represent a ID3v2 tag in the ID3v2 frame of the ID3v2
 * header as described in http://www.id3.org/id3v2.4.0.html#sec4.
 *
 * @author Christian Pesch
 * @version $Id: ID3v2Tag.java 958 2007-02-28 14:44:37Z cpesch $
 */

public class ID3v2Tag {

    private static final Tag[] WELL_KNOWN_TAGS = {
            // 2.0 obsolete
            new Tag("COM", "COMM", "Comments",
                    new Class[]{TextEncodingSection.class, LanguageSection.class,
                            DescriptionSection.class, TextSection.class}),
            new Tag("PIC", "APIC", "Attached picture", false,
                    new Class[]{TextEncodingSection.class, MimeTypeSection.class,
                            DescriptionSection.class, BytesSection.class}),
            new Tag("TAL", "TALB", "Album/Movie/Show title", true),
            new Tag("TCM", "TCOM", "Composer(s)", true),
            new Tag("TCO", "TCON", "Content type", true),
            new Tag("TEN", "TENC", "Encoded by", true),
            new Tag("TP1", "TPE1", "Lead artist(s)/Lead performer(s)/Soloist(s)/Performing group", true),
            new Tag("TPA", "TPOS", "Part of a set", true),
            new Tag("TRK", "TRCK", "Track number/Position in set", true),
            new Tag("TT2", "TIT2", "Title/Songname/Content description", true),
            new Tag("TYE", "TYER", "Year", true),

            // 3.0
            new Tag("AENC", "Audio encryption", true, false),
            new Tag("APIC", "Attached picture", false,
                    new Class[]{TextEncodingSection.class, MimeTypeSection.class,
                            PictureTypeSection.class, DescriptionSection.class,
                            BytesSection.class}),
            new Tag("COMM", "Comments", false,
                    new Class[]{TextEncodingSection.class, LanguageSection.class,
                            DescriptionSection.class, TextSection.class}),
            new Tag("COMR", "Commercial frame", false, false),
            new Tag("ENCR", "Encryption method registration", false, false),
            new Tag("EQUA", "Equalization", true, false),
            new Tag("ETCO", "Event timing codes", true, false),
            new Tag("GEOB", "General encapsulated object", false, false),
            new Tag("GRID", "Group identification registration", false, false),
            new Tag("IPLS", "Involved people list", false, false),
            new Tag("LINK", "Linked information", false, false),
            new Tag("MCDI", "Music CD identifier", false, false),
            new Tag("MLLT", "MPEG location lookup table", true, false),
            new Tag("OWNE", "Ownership frame", false, false),
            new Tag("PCNT", "Play counter", false, false),
            new Tag("POPM", "Popularimeter", false, false),
            new Tag("POSS", "Position synchronisation frame", true, false),
            new Tag("PRIV", "Private frame", false, false),
            new Tag("RBUF", "Recommended buffer size", false, false),
            new Tag("RVAD", "Relative volume adjustment", true, false),
            new Tag("RVRB", "Reverb", false, false),
            new Tag("SYLT", "Synchronized lyric/text", true,
                    new Class[]{TextEncodingSection.class, LanguageSection.class,
                            TimeStampFormatSection.class, ContentTypeSection.class,
                            DescriptionSection.class, TextSection.class}),
            new Tag("SYTC", "Synchronized tempo codes", true, false),
            new Tag("TALB", "Album/Movie/Show title", false, true),
            new Tag("TBPM", "BPM (beats per minute)", false, true),
            new Tag("TCOM", "Composer", false, true),
            new Tag("TCON", "Content type", false, true),
            new Tag("TCOP", "Copyright message", false, true),
            new Tag("TDAT", "Date", false, false),
            new Tag("TDLY", "Playlist delay", false, true),
            new Tag("TENC", "Encoded by", true, true),
            new Tag("TEXT", "Lyricist/Text writer", false, true),
            new Tag("TFLT", "File type", false, true),
            new Tag("TIME", "Time", false, false),
            new Tag("TIT1", "Content group description", false, true),
            new Tag("TIT2", "Title/songname/content description", false, true),
            new Tag("TIT3", "Subtitle/Description refinement", false, true),
            new Tag("TKEY", "Initial key", false, true),
            new Tag("TLAN", "Language(s)", false, true),
            new Tag("TLEN", "Length", true, true),
            new Tag("TMED", "Media type", false, true),
            new Tag("TOAL", "Original album/movie/show title", false, true),
            new Tag("TOFN", "Original filename", false, true),
            new Tag("TOLY", "Original lyricist(s)/text writer(s)", false, true),
            new Tag("TOPE", "Original artist(s)/performer(s)", false, true),
            new Tag("TORY", "Original release year", false, false),
            new Tag("TOWN", "File owner/licensee", false, true),
            new Tag("TPE1", "Lead performer(s)/Soloist(s)", false, true),
            new Tag("TPE2", "Band/orchestra/accompaniment", false, true),
            new Tag("TPE3", "Conductor/performer refinement", false, true),
            new Tag("TPE4", "Interpreted, remixed, or otherwise modified by", false, true),
            new Tag("TPOS", "Part of a set", false, true),
            new Tag("TPUB", "Publisher", false, true),
            new Tag("TRCK", "Track number/Position in set", false, true),
            new Tag("TRDA", "Recording dates", false, false),
            new Tag("TRSN", "Internet radio station name", false, true),
            new Tag("TRSO", "Internet radio station owner", false, true),
            new Tag("TSIZ", "Size", true, false),
            new Tag("TSRC", "ISRC (international standard recording code)", false, true),
            new Tag("TSSE", "Software/Hardware and settings used for encoding", false, false),
            new Tag("TXXX", "User defined text information frame", false,
                    new Class[]{TextEncodingSection.class, DescriptionSection.class, TextSection.class}),
            new Tag("TYER", "Year", false, false),
            new Tag("UFID", "Unique file identifier", false,
                    new Class[]{DescriptionSection.class, BytesSection.class}),
            new Tag("USER", "Terms of use", false,
                    new Class[]{TextEncodingSection.class, LanguageSection.class, TextSection.class}),
            new Tag("USLT", "Unsychronized lyric/text transcription", false,
                    new Class[]{TextEncodingSection.class, LanguageSection.class,
                            DescriptionSection.class, TextSection.class}),
            new Tag("WCOM", "Commercial information", false, false),
            new Tag("WCOP", "Copyright/Legal information", false, false),
            new Tag("WOAF", "Official audio file webpage", false, false),
            new Tag("WOAR", "Official artist/performer webpage", false, false),
            new Tag("WOAS", "Official audio source webpage", false, false),
            new Tag("WORS", "Official internet radio station homepage", false, false),
            new Tag("WPAY", "Payment", false, false),
            new Tag("WPUB", "Publishers official webpage", false, false),
            new Tag("WXXX", "User defined URL link frame", false, false),

            // 4.0
            new Tag("ASPI", "Audio seek point index", true, false),
            new Tag("EQU2", "Equalization (2)", true, false),
            new Tag("RVA2", "Relative volume adjustment (2)", true, false),
            new Tag("TDEN", "Encoding time", false, true),
            new Tag("TDOR", "Original release time", false, true),
            new Tag("TDRC", "Recording time", false, true),
            new Tag("TDRL", "Release time", false, true),
            new Tag("TDTG", "Tagging time", false, true),
            new Tag("TIPL", "Involved people list", false, true),
            new Tag("TMCL", "Musician credits list", false, true),
            new Tag("TMOO", "Mood", false, true),
            new Tag("TPRO", "Produced notice", false, true),
            new Tag("TSOA", "Album sort order", false, true),
            new Tag("TSOP", "Performer sort order", false, true),
            new Tag("TSOT", "Title sort order", false, true),
            new Tag("TSST", "Set subtitle", false, true),

            // iTunes nonstandard tag: defines a track as being part of a compilation if set to 1
            // or part of a single artist album if set to 0
            new Tag("TCMP", "iTunes compilation", false, true),

            // iTunes nonstandard tag: defines the sort order for TPE2
            new Tag("TSO2", "iTunes Band/orchestra/accompaniment sort order", false, true),

            // iTunes nonstandard tag: defines the sort order for TCOM
            new Tag("TSOC", "iTunes Composer sort order", false, true),

            // RatingSaver nonstandard tags
            new Tag("TDPL", "RatingSaver Play time", false, true),
            new Tag("RATG", "RatingSaver Rating", false, false)
    };

    // text information frames:
    // identification frames: TALB TIT1 TIT2 TIT3 TOAL TPOS TRCK TSRC TSST
    // involved persons frames: TCOM TENC TEXT TIPL TMCL TOLY TOPE TPE1 TPE2 TPE3 TPE4
    // derived and subjective properties frames: TBPM TCON TFLT TKEY TLAN TLEN TMED TMOO
    // rights and license frames: TCOP TOWN TPRO TPUB TRSN TRSO
    // other text frames: TDEN TDLY TDOR TDRC TDRL TDTG TOFN TSOA TSOP TSOT TSSE

    protected static Map<String, ID3v2Tag.Tag> wellKnownTags = new HashMap<String, ID3v2Tag.Tag>(WELL_KNOWN_TAGS.length);

    static {
        for (int i = 0, c = WELL_KNOWN_TAGS.length; i < c; i++) {
            Tag tag = WELL_KNOWN_TAGS[i];
            wellKnownTags.put(tag.name, tag);
        }
    }

    /**
     * Create a new tag from the given name.
     */
    public ID3v2Tag(String name, ID3v2Version version) {
        setName(name);
        this.version = version;
    }

    public ID3v2Tag(String name) {
        this(name, guessID3v2Version(name));
    }

    public ID3v2Tag(ID3v2Version version) {
        this(null, version);
    }

    void migrateToVersion(ID3v2Version current) {
        if (!version.equals(current)) {
            if (version.isObsolete() && tag.successorName != null) {
                setName(tag.successorName);
            }
            version = current;
        }
    }

    protected static ID3v2Version guessID3v2Version(String tagName) {
        if (isWellKnownTagName(tagName))
            if (tagName.length() == ID3v2Version.TAG_2_0_SIZE)
                return new ID3v2Version(2, 0);
        return new ID3v2Version();
    }

    protected static boolean isWellKnownTagName(String tagName) {
        return wellKnownTags.get(tagName) != null;
    }

    protected static boolean isValidTagName(String name) {
        if (name.length() < ID3v2Version.TAG_2_0_SIZE || name.length() > ID3v2Version.TAG_3_0_SIZE)
            return false;

        for (int i = 0, c = name.length(); i < c; i++) {
            char ch = name.charAt(i);
            if (!Character.isLetterOrDigit(ch))
                return false;
        }

        return true;
    }

    protected static String findObsoleteTagName(String tagName) {
        for (Tag tag : wellKnownTags.values()) {
            if (tagName.equals(tag.successorName))
                return tag.name;
        }
        return null;
    }

    // --- get object ------------------------------------------

    public boolean isWellKnown() {
        return isWellKnownTagName(tag.name);
    }

    public boolean isValid() {
        return isValidTagName(getName());
    }

    /**
     * Returns true if this frame is a text information
     * frame with an extra text encoding byte.
     *
     * @return true if the frame is text information
     */
    public boolean isTextInformation() {
        return tag.isTextInformation();
    }

    /**
     * Returns true if this frame is an url link frame.
     *
     * @return true if the frame is an url link
     */
    public boolean isURLLink() {
        return getName().startsWith("W") && !getName().equals("WXXX");
    }

    /**
     * Returns true if this frame should have the file alter
     * preservation bit set by default.
     *
     * @return true if the file alter preservation should be set by default
     */
    public boolean isDefaultFileAlterPreservation() {
        return tag.isAlterPreservation;
    }

    public String getName() {
        return tag.name;
    }

    public String getSuccessorName() {
        return tag.successorName;
    }

    public String getDescription() {
        return tag.description;
    }

    public byte[] getBytes() {
        return getName().getBytes();
    }

    List<AbstractSection> getSections() {
        List<AbstractSection> sections = new ArrayList<AbstractSection>(tag.sections.size());
        for (Class aClass : tag.sections) {
            try {
                AbstractSection section = (AbstractSection) aClass.newInstance();
                sections.add(section);
            } catch (Exception e) {
                assert false : "Cannot create section " + e.getMessage();
            }
        }
        return sections;
    }

    public ID3v2Version getVersion() {
        return version;
    }

    // --- set object ------------------------------------------

    public void setName(String name) {
        if (isWellKnownTagName(name)) {
            this.tag = wellKnownTags.get(name);
        } else
            this.tag = new Tag(name);
    }

    private static class Tag {
        String name, successorName;
        String description;
        boolean isAlterPreservation;
        private boolean isTextInformation;
        List<Class> sections;

        /**
         * Generic constructor
         */
        Tag(String name,
            String successorName,
            String description,
            boolean isAlterPreservation,
            boolean isTextInformation,
            Class[] sections) {
            this.name = name;
            this.successorName = successorName;
            this.description = description;
            this.isAlterPreservation = isAlterPreservation;
            this.isTextInformation = isTextInformation;
            this.sections = sections != null ? Arrays.asList(sections) : new ArrayList<Class>(1);
        }

        /**
         * v3.0 constructor
         */
        Tag(String name,
            String description,
            boolean isAlterPreservation,
            Class[] sections) {
            this(name, null, description, isAlterPreservation, false, sections);
        }

        /**
         * v2.0 constructor
         */
        Tag(String name,
            String successorName,
            String description,
            boolean isAlterPreservation,
            Class[] sections) {
            this(name, successorName, description, isAlterPreservation, false, sections);
        }

        /**
         * v2.0 constructor
         */
        Tag(String name,
            String successorName,
            String description,
            Class[] sections) {
            this(name, successorName, description, false, false, sections);
        }

        /**
         * v3.0 text constructor
         */
        Tag(String name,
            String description,
            boolean isAlterPreservation,
            boolean isTextInformation) {
            this(name, null, description, isAlterPreservation, isTextInformation, null);
            setTextInformation();
        }

        private void setTextInformation() {
            if (isTextInformation()) {
                sections.add(0, TextSection.class);
                sections.add(0, TextEncodingSection.class);
            } else
                sections.add(BytesSection.class);
        }

        /**
         * v2.0 text constructor
         */
        Tag(String name,
            String successorName, String description,
            boolean isTextInformation) {
            this(name, successorName, description, false, isTextInformation, null);
            setTextInformation();
        }

        Tag(String name) {
            this(name, null, null, false, true, null);
        }


        boolean isTextInformation() {
            return isTextInformation || name.startsWith("T") && !name.equals("TXXX");
        }
    }

    // --- overwrites Object -----------------------------------

    public int hashCode() {
        return getName().hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof ID3v2Tag))
            return false;

        ID3v2Tag t = (ID3v2Tag) o;
        return t.getName().equals(getName());
    }

    public String toString() {
        return "ID3v2Tag[" +
                "name=" + getName() + ", " +
                "description=" + getDescription() + ", " +
                "isWellKnown=" + isWellKnown() +
                "]";
    }

    // --- member variables ------------------------------------

    protected ID3v2Version version;
    private Tag tag;
}
