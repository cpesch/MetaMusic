/**
 * JacobGen generated file --- do not edit
 *
 * (http://www.sourceforge.net/projects/jacob-project */
package slash.metamusic.itunes.com.binding;

import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class IITObject extends Dispatch {

    public static final String componentName = "iTunesLib.IITObject";

    public IITObject() {
        super(componentName);
    }

    /**
     * This constructor is used instead of a case operation to
     * turn a Dispatch object into a wider object - it must exist
     * in every wrapper class whose instances may be returned from
     * method calls wrapped in VT_DISPATCH Variants.
     */
    public IITObject(Dispatch d) {
        // take over the IDispatch pointer
        m_pDispatch = d.m_pDispatch;
        // null out the input's pointer
        d.m_pDispatch = 0;
    }

    public IITObject(String compName) {
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

}
