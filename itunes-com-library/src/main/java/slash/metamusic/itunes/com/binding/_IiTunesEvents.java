/**
 * JacobGen generated file --- do not edit
 *
 * (http://www.sourceforge.net/projects/jacob-project */
package slash.metamusic.itunes.com.binding;

import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class _IiTunesEvents extends Dispatch {

    public static final String componentName = "iTunesLib._IiTunesEvents";

    public _IiTunesEvents() {
        super(componentName);
    }

    /**
     * This constructor is used instead of a case operation to
     * turn a Dispatch object into a wider object - it must exist
     * in every wrapper class whose instances may be returned from
     * method calls wrapped in VT_DISPATCH Variants.
     */
    public _IiTunesEvents(Dispatch d) {
        // take over the IDispatch pointer
        m_pDispatch = d.m_pDispatch;
        // null out the input's pointer
        d.m_pDispatch = 0;
    }

    public _IiTunesEvents(String compName) {
        super(compName);
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param deletedObjectIDs an input-parameter of type Variant
     * @param lastParam        an input-parameter of type Variant
     * @return the result is of type int
     */
    public int onDatabaseChangedEvent(Variant deletedObjectIDs, Variant lastParam) {
        return Dispatch.call(this, "OnDatabaseChangedEvent", deletedObjectIDs, lastParam).toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type Variant
     * @return the result is of type int
     */
    public int onPlayerPlayEvent(Variant lastParam) {
        return Dispatch.call(this, "OnPlayerPlayEvent", lastParam).toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type Variant
     * @return the result is of type int
     */
    public int onPlayerStopEvent(Variant lastParam) {
        return Dispatch.call(this, "OnPlayerStopEvent", lastParam).toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type Variant
     * @return the result is of type int
     */
    public int onPlayerPlayingTrackChangedEvent(Variant lastParam) {
        return Dispatch.call(this, "OnPlayerPlayingTrackChangedEvent", lastParam).toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int onUserInterfaceEnabledEvent() {
        return Dispatch.call(this, "OnUserInterfaceEnabledEvent").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type int
     * @return the result is of type int
     */
    public int onCOMCallsDisabledEvent(int lastParam) {
        return Dispatch.call(this, "OnCOMCallsDisabledEvent", new Variant(lastParam)).toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int onCOMCallsEnabledEvent() {
        return Dispatch.call(this, "OnCOMCallsEnabledEvent").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int onQuittingEvent() {
        return Dispatch.call(this, "OnQuittingEvent").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int onAboutToPromptUserToQuitEvent() {
        return Dispatch.call(this, "OnAboutToPromptUserToQuitEvent").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type int
     * @return the result is of type int
     */
	public int onSoundVolumeChangedEvent(int lastParam) {
		return Dispatch.call(this, "OnSoundVolumeChangedEvent", new Variant(lastParam)).toInt();
	}

}
