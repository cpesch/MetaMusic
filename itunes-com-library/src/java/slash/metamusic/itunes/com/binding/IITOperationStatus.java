/**
 * JacobGen generated file --- do not edit
 *
 * (http://www.sourceforge.net/projects/jacob-project */
package slash.metamusic.itunes.com.binding;

import com.jacob.com.Dispatch;

public class IITOperationStatus extends Dispatch {

    public static final String componentName = "iTunesLib.IITOperationStatus";

    public IITOperationStatus() {
        super(componentName);
    }

    /**
     * This constructor is used instead of a case operation to
     * turn a Dispatch object into a wider object - it must exist
     * in every wrapper class whose instances may be returned from
     * method calls wrapped in VT_DISPATCH Variants.
     */
    public IITOperationStatus(Dispatch d) {
        // take over the IDispatch pointer
        m_pDispatch = d.m_pDispatch;
        // null out the input's pointer
        d.m_pDispatch = 0;
    }

    public IITOperationStatus(String compName) {
        super(compName);
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type boolean
     */
    public boolean getInProgress() {
        return Dispatch.get(this, "InProgress").toBoolean();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type IITTrackCollection
     */
    public IITTrackCollection getTracks() {
        return new IITTrackCollection(Dispatch.get(this, "Tracks").toDispatch());
	}

}
