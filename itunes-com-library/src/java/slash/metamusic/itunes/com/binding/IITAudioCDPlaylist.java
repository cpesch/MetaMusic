/**
 * JacobGen generated file --- do not edit
 *
 * (http://www.sourceforge.net/projects/jacob-project */
package slash.metamusic.itunes.com.binding;

import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class IITAudioCDPlaylist extends Dispatch {

    public static final String componentName = "iTunesLib.IITAudioCDPlaylist";

    public IITAudioCDPlaylist() {
        super(componentName);
    }

    /**
     * This constructor is used instead of a case operation to
     * turn a Dispatch object into a wider object - it must exist
     * in every wrapper class whose instances may be returned from
     * method calls wrapped in VT_DISPATCH Variants.
     */
    public IITAudioCDPlaylist(Dispatch d) {
        // take over the IDispatch pointer
        m_pDispatch = d.m_pDispatch;
        // null out the input's pointer
        d.m_pDispatch = 0;
    }

    public IITAudioCDPlaylist(String compName) {
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
    public void playFirstTrack() {
        Dispatch.call(this, "PlayFirstTrack");
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param showPrintDialog an input-parameter of type boolean
     * @param printKind       an input-parameter of type int
     * @param lastParam       an input-parameter of type String
     */
    public void print(boolean showPrintDialog, int printKind, String lastParam) {
        Dispatch.call(this, "Print", new Variant(showPrintDialog), new Variant(printKind), lastParam);
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param searchText an input-parameter of type String
     * @param lastParam  an input-parameter of type int
     * @return the result is of type IITTrackCollection
     */
    public IITTrackCollection search(String searchText, int lastParam) {
        return new IITTrackCollection(Dispatch.call(this, "Search", searchText, new Variant(lastParam)).toDispatch());
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
     * @return the result is of type IITSource
     */
    public IITSource getSource() {
        return new IITSource(Dispatch.get(this, "Source").toDispatch());
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
    public boolean getShuffle() {
        return Dispatch.get(this, "Shuffle").toBoolean();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type boolean
     */
    public void setShuffle(boolean lastParam) {
        Dispatch.call(this, "Shuffle", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type double
     */
    public double getSize() {
        return Dispatch.get(this, "Size").toDouble();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int getSongRepeat() {
        return Dispatch.get(this, "SongRepeat").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type int
     */
    public void setSongRepeat(int lastParam) {
        Dispatch.call(this, "SongRepeat", new Variant(lastParam));
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
     * @return the result is of type boolean
     */
    public boolean getVisible() {
        return Dispatch.get(this, "Visible").toBoolean();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type IITTrackCollection
     */
    public IITTrackCollection getTracks() {
        return new IITTrackCollection(Dispatch.get(this, "Tracks").toDispatch());
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
     * @return the result is of type boolean
     */
    public boolean getCompilation() {
        return Dispatch.get(this, "Compilation").toBoolean();
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
     * @return the result is of type int
     */
    public int getDiscCount() {
        return Dispatch.get(this, "DiscCount").toInt();
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
     * @return the result is of type String
     */
	public String getGenre() {
		return Dispatch.get(this, "Genre").toString();
	}

	/**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
	public int getYear() {
		return Dispatch.get(this, "Year").toInt();
	}

}
