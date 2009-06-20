/**
 * JacobGen generated file --- do not edit
 *
 * (http://www.sourceforge.net/projects/jacob-project */
package slash.metamusic.itunes.com.binding;

import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class IITFileOrCDTrack extends Dispatch {

    public static final String componentName = "iTunesLib.IITFileOrCDTrack";

    public IITFileOrCDTrack() {
        super(componentName);
    }

    /**
     * This constructor is used instead of a case operation to
     * turn a Dispatch object into a wider object - it must exist
     * in every wrapper class whose instances may be returned from
     * method calls wrapped in VT_DISPATCH Variants.
     */
    public IITFileOrCDTrack(Dispatch d) {
        // take over the IDispatch pointer
        m_pDispatch = d.m_pDispatch;
        // null out the input's pointer
        d.m_pDispatch = 0;
    }

    public IITFileOrCDTrack(String compName) {
        super(compName);
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param sourceID   an input-parameter of type int
     * @param playlistID an input-parameter of type int
     * @param trackID    an input-parameter of type int
     * @param lastParam  an input-parameter of type int
     */
    public void getITObjectIDs(int sourceID, int playlistID, int trackID, int lastParam) {
        Dispatch.call(this, "GetITObjectIDs", new Variant(sourceID), new Variant(playlistID), new Variant(trackID), new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
     *
     * @param sourceID   is an one-element array which sends the input-parameter
     *                   to the ActiveX-Component and receives the output-parameter
     * @param playlistID is an one-element array which sends the input-parameter
     *                   to the ActiveX-Component and receives the output-parameter
     * @param trackID    is an one-element array which sends the input-parameter
     *                   to the ActiveX-Component and receives the output-parameter
     * @param lastParam  is an one-element array which sends the input-parameter
     *                   to the ActiveX-Component and receives the output-parameter
     */
    public void getITObjectIDs(int[] sourceID, int[] playlistID, int[] trackID, int[] lastParam) {
        Variant vnt_sourceID = new Variant();
        if (sourceID == null || sourceID.length == 0)
            vnt_sourceID.noParam();
        else
            vnt_sourceID.putIntRef(sourceID[0]);

        Variant vnt_playlistID = new Variant();
        if (playlistID == null || playlistID.length == 0)
            vnt_playlistID.noParam();
        else
            vnt_playlistID.putIntRef(playlistID[0]);

        Variant vnt_trackID = new Variant();
        if (trackID == null || trackID.length == 0)
            vnt_trackID.noParam();
        else
            vnt_trackID.putIntRef(trackID[0]);

        Variant vnt_lastParam = new Variant();
        if (lastParam == null || lastParam.length == 0)
            vnt_lastParam.noParam();
        else
            vnt_lastParam.putIntRef(lastParam[0]);

        Dispatch.call(this, "GetITObjectIDs", vnt_sourceID, vnt_playlistID, vnt_trackID, vnt_lastParam);

        if (sourceID != null && sourceID.length > 0)
            sourceID[0] = vnt_sourceID.toInt();
        if (playlistID != null && playlistID.length > 0)
            playlistID[0] = vnt_playlistID.toInt();
        if (trackID != null && trackID.length > 0)
            trackID[0] = vnt_trackID.toInt();
        if (lastParam != null && lastParam.length > 0)
            lastParam[0] = vnt_lastParam.toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type String
     */
    public String getName() {
        return Dispatch.get(this, "Name").toString();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type String
     */
    public void setName(String lastParam) {
        Dispatch.call(this, "Name", lastParam);
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int getIndex() {
        return Dispatch.get(this, "Index").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int getsourceID() {
        return Dispatch.get(this, "sourceID").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int getplaylistID() {
        return Dispatch.get(this, "playlistID").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int gettrackID() {
        return Dispatch.get(this, "trackID").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int getTrackDatabaseID() {
        return Dispatch.get(this, "TrackDatabaseID").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     */
    public void delete() {
        Dispatch.call(this, "Delete");
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     */
    public void play() {
        Dispatch.call(this, "Play");
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type String
     * @return the result is of type IITArtwork
     */
    public IITArtwork addArtworkFromFile(String lastParam) {
        return new IITArtwork(Dispatch.call(this, "AddArtworkFromFile", lastParam).toDispatch());
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int getKind() {
        return Dispatch.get(this, "Kind").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type IITPlaylist
     */
    public IITPlaylist getPlaylist() {
        return new IITPlaylist(Dispatch.get(this, "Playlist").toDispatch());
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type String
     */
    public String getAlbum() {
        return Dispatch.get(this, "Album").toString();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type String
     */
    public void setAlbum(String lastParam) {
        Dispatch.call(this, "Album", lastParam);
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type String
     */
    public String getArtist() {
        return Dispatch.get(this, "Artist").toString();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type String
     */
    public void setArtist(String lastParam) {
        Dispatch.call(this, "Artist", lastParam);
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int getBitRate() {
        return Dispatch.get(this, "BitRate").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int getBPM() {
        return Dispatch.get(this, "BPM").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type int
     */
    public void setBPM(int lastParam) {
        Dispatch.call(this, "BPM", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type String
     */
    public String getComment() {
        return Dispatch.get(this, "Comment").toString();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type String
     */
    public void setComment(String lastParam) {
        Dispatch.call(this, "Comment", lastParam);
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type boolean
     */
    public boolean getCompilation() {
        return Dispatch.get(this, "Compilation").toBoolean();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type boolean
     */
    public void setCompilation(boolean lastParam) {
        Dispatch.call(this, "Compilation", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type String
     */
    public String getComposer() {
        return Dispatch.get(this, "Composer").toString();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type String
     */
    public void setComposer(String lastParam) {
        Dispatch.call(this, "Composer", lastParam);
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type java.util.Date
     */
    public java.util.Date getDateAdded() {
        return Dispatch.get(this, "DateAdded").toJavaDate();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int getDiscCount() {
        return Dispatch.get(this, "DiscCount").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type int
     */
    public void setDiscCount(int lastParam) {
        Dispatch.call(this, "DiscCount", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int getDiscNumber() {
        return Dispatch.get(this, "DiscNumber").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type int
     */
    public void setDiscNumber(int lastParam) {
        Dispatch.call(this, "DiscNumber", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int getDuration() {
        return Dispatch.get(this, "Duration").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type boolean
     */
    public boolean getEnabled() {
        return Dispatch.get(this, "Enabled").toBoolean();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type boolean
     */
    public void setEnabled(boolean lastParam) {
        Dispatch.call(this, "Enabled", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type String
     */
    public String getEQ() {
        return Dispatch.get(this, "EQ").toString();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type String
     */
    public void setEQ(String lastParam) {
        Dispatch.call(this, "EQ", lastParam);
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type int
     */
    public void setFinish(int lastParam) {
        Dispatch.call(this, "Finish", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int getFinish() {
        return Dispatch.get(this, "Finish").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type String
     */
    public String getGenre() {
        return Dispatch.get(this, "Genre").toString();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type String
     */
    public void setGenre(String lastParam) {
        Dispatch.call(this, "Genre", lastParam);
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type String
     */
    public String getGrouping() {
        return Dispatch.get(this, "Grouping").toString();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type String
     */
    public void setGrouping(String lastParam) {
        Dispatch.call(this, "Grouping", lastParam);
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type String
     */
    public String getKindAsString() {
        return Dispatch.get(this, "KindAsString").toString();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type java.util.Date
     */
    public java.util.Date getModificationDate() {
        return Dispatch.get(this, "ModificationDate").toJavaDate();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int getPlayedCount() {
        return Dispatch.get(this, "PlayedCount").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type int
     */
    public void setPlayedCount(int lastParam) {
        // Dispatch.call(this, "PlayedCount", new Variant(lastParam));
        Dispatch.invoke(this, "PlayedCount", Dispatch.Put, new Object[]{lastParam}, new int[]{0});
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type java.util.Date
     */
    public java.util.Date getPlayedDate() {
        return Dispatch.get(this, "PlayedDate").toJavaDate();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type java.util.Date
     */
    public void setPlayedDate(java.util.Date lastParam) {
        // Dispatch.call(this, "PlayedDate", new Variant(lastParam));
        Dispatch.invoke(this, "PlayedDate", Dispatch.Put, new Object[]{lastParam}, new int[]{0});
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int getPlayOrderIndex() {
        return Dispatch.get(this, "PlayOrderIndex").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int getRating() {
        return Dispatch.get(this, "Rating").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type int
     */
    public void setRating(int lastParam) {
        // Dispatch.call(this, "Rating", new Variant(lastParam));
        Dispatch.invoke(this, "Rating", Dispatch.Put, new Object[]{lastParam}, new int[]{0});
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int getSampleRate() {
        return Dispatch.get(this, "SampleRate").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int getSize() {
        return Dispatch.get(this, "Size").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int getStart() {
        return Dispatch.get(this, "Start").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type int
     */
    public void setStart(int lastParam) {
        Dispatch.call(this, "Start", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type String
     */
    public String getTime() {
        return Dispatch.get(this, "Time").toString();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int getTrackCount() {
        return Dispatch.get(this, "TrackCount").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type int
     */
    public void setTrackCount(int lastParam) {
        Dispatch.call(this, "TrackCount", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int getTrackNumber() {
        return Dispatch.get(this, "TrackNumber").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type int
     */
    public void setTrackNumber(int lastParam) {
        Dispatch.call(this, "TrackNumber", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int getVolumeAdjustment() {
        return Dispatch.get(this, "VolumeAdjustment").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type int
     */
    public void setVolumeAdjustment(int lastParam) {
        Dispatch.call(this, "VolumeAdjustment", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int getYear() {
        return Dispatch.get(this, "Year").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type int
     */
    public void setYear(int lastParam) {
        Dispatch.call(this, "Year", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type IITArtworkCollection
     */
    public IITArtworkCollection getArtwork() {
        return new IITArtworkCollection(Dispatch.get(this, "Artwork").toDispatch());
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type String
     */
    public String getLocation() {
        return Dispatch.get(this, "Location").toString();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     */
    public void updateInfoFromFile() {
        Dispatch.call(this, "UpdateInfoFromFile");
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type boolean
     */
    public boolean getPodcast() {
        return Dispatch.get(this, "Podcast").toBoolean();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     */
    public void updatePodcastFeed() {
        Dispatch.call(this, "UpdatePodcastFeed");
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type boolean
     */
    public boolean getRememberBookmark() {
        return Dispatch.get(this, "RememberBookmark").toBoolean();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type boolean
     */
    public void setRememberBookmark(boolean lastParam) {
        Dispatch.call(this, "RememberBookmark", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type boolean
     */
    public boolean getExcludeFromShuffle() {
        return Dispatch.get(this, "ExcludeFromShuffle").toBoolean();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type boolean
     */
    public void setExcludeFromShuffle(boolean lastParam) {
        Dispatch.call(this, "ExcludeFromShuffle", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type String
     */
    public String getLyrics() {
        return Dispatch.get(this, "Lyrics").toString();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type String
     */
    public void setLyrics(String lastParam) {
        Dispatch.call(this, "Lyrics", lastParam);
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type String
     */
    public String getCategory() {
        return Dispatch.get(this, "Category").toString();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type String
     */
    public void setCategory(String lastParam) {
        Dispatch.call(this, "Category", lastParam);
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type String
     */
    public String getDescription() {
        return Dispatch.get(this, "Description").toString();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type String
     */
    public void setDescription(String lastParam) {
        Dispatch.call(this, "Description", lastParam);
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type String
     */
    public String getLongDescription() {
        return Dispatch.get(this, "LongDescription").toString();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type String
     */
    public void setLongDescription(String lastParam) {
        Dispatch.call(this, "LongDescription", lastParam);
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int getBookmarkTime() {
        return Dispatch.get(this, "BookmarkTime").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type int
     */
    public void setBookmarkTime(int lastParam) {
        Dispatch.call(this, "BookmarkTime", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int getVideoKind() {
        return Dispatch.get(this, "VideoKind").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type int
     */
    public void setVideoKind(int lastParam) {
        Dispatch.call(this, "VideoKind", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int getSkippedCount() {
        return Dispatch.get(this, "SkippedCount").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type int
     */
    public void setSkippedCount(int lastParam) {
        Dispatch.call(this, "SkippedCount", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type java.util.Date
     */
    public java.util.Date getSkippedDate() {
        return Dispatch.get(this, "SkippedDate").toJavaDate();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type java.util.Date
     */
    public void setSkippedDate(java.util.Date lastParam) {
        Dispatch.call(this, "SkippedDate", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type boolean
     */
    public boolean getPartOfGaplessAlbum() {
        return Dispatch.get(this, "PartOfGaplessAlbum").toBoolean();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type boolean
     */
    public void setPartOfGaplessAlbum(boolean lastParam) {
        Dispatch.call(this, "PartOfGaplessAlbum", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type String
     */
    public String getAlbumArtist() {
        return Dispatch.get(this, "AlbumArtist").toString();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type String
     */
    public void setAlbumArtist(String lastParam) {
        Dispatch.call(this, "AlbumArtist", lastParam);
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type String
     */
    public String getShow() {
        return Dispatch.get(this, "Show").toString();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type String
     */
    public void setShow(String lastParam) {
        Dispatch.call(this, "Show", lastParam);
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int getSeasonNumber() {
        return Dispatch.get(this, "SeasonNumber").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type int
     */
    public void setSeasonNumber(int lastParam) {
        Dispatch.call(this, "SeasonNumber", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type String
     */
    public String getEpisodeID() {
        return Dispatch.get(this, "EpisodeID").toString();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type String
     */
    public void setEpisodeID(String lastParam) {
        Dispatch.call(this, "EpisodeID", lastParam);
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int getEpisodeNumber() {
        return Dispatch.get(this, "EpisodeNumber").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type int
     */
    public void setEpisodeNumber(int lastParam) {
        Dispatch.call(this, "EpisodeNumber", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int getSize64High() {
        return Dispatch.get(this, "Size64High").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int getSize64Low() {
        return Dispatch.get(this, "Size64Low").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type boolean
     */
    public boolean getUnplayed() {
        return Dispatch.get(this, "Unplayed").toBoolean();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type boolean
     */
    public void setUnplayed(boolean lastParam) {
        Dispatch.call(this, "Unplayed", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type String
     */
    public String getSortAlbum() {
        return Dispatch.get(this, "SortAlbum").toString();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type String
     */
    public void setSortAlbum(String lastParam) {
        Dispatch.call(this, "SortAlbum", lastParam);
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type String
     */
    public String getSortAlbumArtist() {
        return Dispatch.get(this, "SortAlbumArtist").toString();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type String
     */
    public void setSortAlbumArtist(String lastParam) {
        Dispatch.call(this, "SortAlbumArtist", lastParam);
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type String
     */
    public String getSortArtist() {
        return Dispatch.get(this, "SortArtist").toString();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type String
     */
    public void setSortArtist(String lastParam) {
        Dispatch.call(this, "SortArtist", lastParam);
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type String
     */
	public String getSortComposer() {
		return Dispatch.get(this, "SortComposer").toString();
	}

	/**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type String
     */
	public void setSortComposer(String lastParam) {
		Dispatch.call(this, "SortComposer", lastParam);
	}

	/**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type String
     */
	public String getSortName() {
		return Dispatch.get(this, "SortName").toString();
	}

	/**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type String
     */
	public void setSortName(String lastParam) {
		Dispatch.call(this, "SortName", lastParam);
	}

	/**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type String
     */
	public String getSortShow() {
		return Dispatch.get(this, "SortShow").toString();
	}

	/**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type String
     */
	public void setSortShow(String lastParam) {
		Dispatch.call(this, "SortShow", lastParam);
	}

}
