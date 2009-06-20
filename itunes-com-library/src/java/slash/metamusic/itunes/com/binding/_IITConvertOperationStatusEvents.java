/**
 * JacobGen generated file --- do not edit
 *
 * (http://www.sourceforge.net/projects/jacob-project */
package slash.metamusic.itunes.com.binding;

import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class _IITConvertOperationStatusEvents extends Dispatch {

    public static final String componentName = "iTunesLib._IITConvertOperationStatusEvents";

    public _IITConvertOperationStatusEvents() {
        super(componentName);
    }

    /**
     * This constructor is used instead of a case operation to
     * turn a Dispatch object into a wider object - it must exist
     * in every wrapper class whose instances may be returned from
     * method calls wrapped in VT_DISPATCH Variants.
     */
    public _IITConvertOperationStatusEvents(Dispatch d) {
        // take over the IDispatch pointer
        m_pDispatch = d.m_pDispatch;
        // null out the input's pointer
        d.m_pDispatch = 0;
    }

    public _IITConvertOperationStatusEvents(String compName) {
        super(compName);
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param trackName     an input-parameter of type String
     * @param progressValue an input-parameter of type int
     * @param lastParam     an input-parameter of type int
     * @return the result is of type int
     */
    public int onConvertOperationStatusChangedEvent(String trackName, int progressValue, int lastParam) {
        return Dispatch.call(this, "OnConvertOperationStatusChangedEvent", trackName, new Variant(progressValue), new Variant(lastParam)).toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int onConvertOperationCompleteEvent() {
        return Dispatch.call(this, "OnConvertOperationCompleteEvent").toInt();
	}

}
