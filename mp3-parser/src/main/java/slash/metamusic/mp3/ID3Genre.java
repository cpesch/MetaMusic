/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2001-2003 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.mp3;

import slash.metamusic.distance.StringCompliance;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

/**
 * My instances represent an ID3 genre.
 *
 * @author Christian Pesch
 * @version $Id: ID3Genre.java 952 2007-01-17 20:14:15Z cpesch $
 */

public class ID3Genre {
    /**
     * Logging output
     */
    protected static final Logger log = Logger.getLogger(ID3Genre.class.getName());

    /**
     * The basic set of genres.
     */
    private static final String[] BASIC_GENRES = {
            "Blues", "Classic Rock", "Country", "Dance", "Disco",
            "Funk", "Grunge", "Hip-Hop", "Jazz", "Metal",
            "New Age", "Oldies", "Other", "Pop", "R&B",
            "Rap", "Reggae", "Rock", "Techno", "Industrial",
            "Alternative", "Ska", "Death Metal", "Pranks", "Soundtrack",
            "Euro-Techno", "Ambient", "Trip-Hop", "Vocal", "Jazz+Funk",
            "Fusion", "Trance", "Classical", "Instrumental", "Acid",
            "House", "Game", "Sound Clip", "Gospel", "Noise",
            "Alt. Rock", "Bass", "Soul", "Punk", "Space",
            "Meditative", "Instrumental Pop", "Instrumental Rock", "Ethnic",
            "Gothic", "Darkwave", "Techno-Industrial", "Electronic", "Pop-Folk",
            "Eurodance", "Dream", "Southern Rock", "Comedy", "Cult",
            "Gangsta", "Top 40", "Christian Rap", "Pop/Funk", "Jungle",
            "Native American", "Cabaret", "New Wave", "Psychadelic", "Rave",
            "Showtunes", "Trailer", "Lo-Fi", "Tribal", "Acid Punk",
            "Acid Jazz", "Polka", "Retro", "Musical", "Rock & Roll",
            "Hard Rock"
    };

    /**
     * WinAmp defines additional genres beginning directly
     * after "Hard Rock" from the BASIC_GENRES array.
     */
    private static final String[] WINAMP_GENRES = {
            "Folk", "Folk-Rock", "National Folk", "Swing", "Fast Fusion",
            "Bebob", "Latin", "Revival", "Celtic", "Bluegrass", "Avantgarde",
            "Gothic Rock", "Progressive Rock", "Psychedelic Rock", "Symphonic Rock",
            "Slow Rock", "Big Band", "Chorus", "Easy Listening", "Acoustic",
            "Humour", "Speech", "Chanson", "Opera", "Chamber Music", "Sonata",
            "Symphony", "Booty Bass", "Primus", "Porn Groove", "Satire", "Slow Jam",
            "Club", "Tango", "Samba", "Folklore", "Ballad", "Power Ballad",
            "Rhythmic Soul", "Freestyle", "Duet", "Punk Rock", "Drum Solo",
            "Acapella", "Euro-House", "Dance Hall",
            "Goa", "Drum & Bass", "Club-House", "Hardcore", "Terror", "Indie",
            "BritPop", "Negerpunk", "Polsk Punk", "Beat", "Christian Gangsta Rap",
            "Heavy Metal", "Black Metal", "Crossover", "Contemporary Christian",
            "Christian Rock", "Merengue", "Salsa", "Thrash Metal", "Anime",
            "Jpop", "Synthpop"
    };

    public static final String UNKNOWN = "Unknown";

    protected static Set<String> basicGenreNames = new HashSet<String>(BASIC_GENRES.length);
    protected static Set<String> winAmpGenreNames = new HashSet<String>(WINAMP_GENRES.length);

    static {
        for (int i = 0, c = BASIC_GENRES.length; i < c; i++) {
            String basicGenre = BASIC_GENRES[i];
            basicGenreNames.add(basicGenre);
        }
        for (int i = 0, c = WINAMP_GENRES.length; i < c; i++) {
            String winampGenre = WINAMP_GENRES[i];
            winAmpGenreNames.add(winampGenre);
            assert !basicGenreNames.contains(winampGenre);
        }
    }


    public static boolean isWellKnown(String name) {
        return getGenreId(name) != -1;
    }

    /**
     * @return one of {@link #getGenreNames()}
     */
    public static ID3Genre findWellknownMatching(String genreName) {
        // this defaults to the least specific genre
        double currentCompliance = StringCompliance.MINIMUM_COMPLIANCE_TO_PREFER;
        String currentName = "Other";

        // do not search, if there is no name given
        if (genreName != null && genreName.length() > 0) {

            for (Iterator<String> iterator = getGenreNames(); iterator.hasNext();) {
                String foundName = iterator.next();

                double compliance = StringCompliance.compliance(foundName, genreName);
                if (compliance > currentCompliance) {
                    currentCompliance = compliance;
                    currentName = foundName;
                }
            }
        }
        return new ID3Genre(currentName);
    }

    /**
     * @return a genre that matches a format like: Electronic, (66)Electronic, Electronic(66)
     */
    public static ID3Genre findWellknown(String idAndOrName) {
        Integer genreId = findGenreId(idAndOrName);
        return genreId != null ? new ID3Genre(genreId) : new ID3Genre(idAndOrName);
    }

    private static Integer findGenreId(String idAndOrName) {
        StringBuffer idBuffer = new StringBuffer();
        StringBuffer nameBuffer = new StringBuffer();

        // maybe there is no genre defined
        if (idAndOrName != null) {
            for (int i = 0; i < idAndOrName.length(); i++) {
                char ch = idAndOrName.charAt(i);
                if (Character.isDigit(ch) || (idBuffer.length() == 0 && ch == '-'))
                    idBuffer.append(ch);
                if (Character.isLetter(ch) || Character.isWhitespace(ch) || ch == '-' || ch == '.')
                    nameBuffer.append(ch);
            }
        }

        // read id
        Integer id = null;
        if (idBuffer.length() > 0) {
            try {
                id = Integer.parseInt(idBuffer.toString());
            } catch (NumberFormatException e) {
                log.severe("Invalid genre: " + idBuffer + " tag content: " + idAndOrName);
            }
        }

        // read name
        if (nameBuffer.length() > 0 && id == null)
            id = getGenreId(nameBuffer.toString());
        return id;
    }


    public static Iterator<String> getBasicGenreNames() {
        return basicGenreNames.iterator();
    }

    public static Iterator<String> getWinAmpGenreNames() {
        return winAmpGenreNames.iterator();
    }

    public static Iterator<String> getGenreNames() {
        return getGenreNamesAsSet().iterator();
    }

    public static Set<String> getGenreNamesAsSet() {
        Set<String> allGenres = new HashSet<String>(basicGenreNames.size() + winAmpGenreNames.size());
        allGenres.addAll(basicGenreNames);
        allGenres.addAll(winAmpGenreNames);
        return allGenres;
    }

    public static String getGenreName(int id) {
        if (id < 0 || id >= BASIC_GENRES.length + WINAMP_GENRES.length)
            return null;

        if (id < BASIC_GENRES.length)
            return BASIC_GENRES[id];
        else
            return WINAMP_GENRES[id - BASIC_GENRES.length];
    }

    public static Integer getGenreId(String name) {
        for (int i = 0; i < BASIC_GENRES.length; i++)
            if (BASIC_GENRES[i].equals(name))
                return i;

        for (int i = 0; i < WINAMP_GENRES.length; i++)
            if (WINAMP_GENRES[i].equals(name))
                return BASIC_GENRES.length + i;
        return null;
    }

    public ID3Genre(Integer id) {
        this(id, null);
    }

    public ID3Genre(String name) {
        this(null, name);
    }

    private ID3Genre(Integer id, String name) {
        setIdAndName(id, name);
    }

    public int getId() {
        return id != null ? id : -1;
    }

    public String getName() {
        return name;
    }

    private void setIdAndName(Integer newId, String newName) {
        this.id = newId;
        this.name = newName;

        if (newId != null && (newId <= 0 || newId >= BASIC_GENRES.length + WINAMP_GENRES.length))
            id = null;

        if (id != null)
            name = getGenreName(id);
        else if (name != null) {
            id = getGenreId(name);
            if (id == null) {
                id = findGenreId(name);
                if (id != null)
                    name = getGenreName(id);
            }
        }
    }

    public String getFormattedName() {
        return getName() + (id != null ? "(" + getId() + ")" : "");
    }

    public boolean isWellKnown() {
        return id != null;
    }

    // --- overwrites Object -----------------------------------

    public boolean equals(Object o) {
        if (!(o instanceof ID3Genre))
            return false;

        ID3Genre g = (ID3Genre) o;
        if (g.id != null && id != null)
            return g.getId() == getId();
        else
            return g.getName().equals(getName());
    }

    public String toString() {
        return "ID3Genre[" + (id != null ? "id=" + getId() + ", " : "") +
                "name=" + getName() + ", isWellKnown=" + isWellKnown() + "]";
    }

    // --- member variables ------------------------------------

    private Integer id;
    private String name;
}
