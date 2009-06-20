/**
 * JacobGen generated file --- do not edit
 *
 * (http://www.sourceforge.net/projects/jacob-project */
package slash.metamusic.itunes.com.binding;

import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class IITTrackCollection extends Dispatch {

    public static final String componentName = "iTunesLib.IITTrackCollection";

    public IITTrackCollection() {
        super(componentName);
    }

    /**
     * This constructor is used instead of a case operation to
     * turn a Dispatch object into a wider object - it must exist
     * in every wrapper class whose instances may be returned from
     * method calls wrapped in VT_DISPATCH Variants.
     */
    public IITTrackCollection(Dispatch d) {
        // take over the IDispatch pointer
        m_pDispatch = d.m_pDispatch;
        // null out the input's pointer
        d.m_pDispatch = 0;
    }

    public IITTrackCollection(String compName) {
        super(compName);
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int getCount() {
        return Dispatch.get(this, "Count").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type int
     * @return the result is of type IITTrack
     */
    public IITTrack getItem(int lastParam) {
        return new IITTrack(Dispatch.call(this, "Item", new Variant(lastParam)).toDispatch());
    }

    public IITURLTrack getURLTrack(int lastParam) {
        return new IITURLTrack(Dispatch.call(this, "Item", new Variant(lastParam)).toDispatch());
    }

    public IITFileOrCDTrack getFileOrCDTrack(int lastParam) {
        return new IITFileOrCDTrack(Dispatch.call(this, "Item", new Variant(lastParam)).toDispatch());
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type int
     * @return the result is of type IITTrack
     */
    public IITTrack getItemByPlayOrder(int lastParam) {
        return new IITTrack(Dispatch.call(this, "ItemByPlayOrder", new Variant(lastParam)).toDispatch());
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type String
     * @return the result is of type IITTrack
     */
    public IITTrack getItemByName(String lastParam) {
        return new IITTrack(Dispatch.call(this, "ItemByName", lastParam).toDispatch());
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type Variant
     */
	public Variant get_NewEnum() {
		return Dispatch.get(this, "_NewEnum");
	}

}
