/**
 * JacobGen generated file --- do not edit
 *
 * (http://www.sourceforge.net/projects/jacob-project */
package slash.metamusic.itunes.com.binding;

import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class IITEQPreset extends Dispatch {

    public static final String componentName = "iTunesLib.IITEQPreset";

    public IITEQPreset() {
        super(componentName);
    }

    /**
     * This constructor is used instead of a case operation to
     * turn a Dispatch object into a wider object - it must exist
     * in every wrapper class whose instances may be returned from
     * method calls wrapped in VT_DISPATCH Variants.
     */
    public IITEQPreset(Dispatch d) {
        // take over the IDispatch pointer
        m_pDispatch = d.m_pDispatch;
        // null out the input's pointer
        d.m_pDispatch = 0;
    }

    public IITEQPreset(String compName) {
        super(compName);
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
     * @return the result is of type boolean
     */
    public boolean getModifiable() {
        return Dispatch.get(this, "Modifiable").toBoolean();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type double
     */
    public double getPreamp() {
        return Dispatch.get(this, "Preamp").toDouble();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type double
     */
    public void setPreamp(double lastParam) {
        Dispatch.call(this, "Preamp", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type double
     */
    public double getBand1() {
        return Dispatch.get(this, "Band1").toDouble();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type double
     */
    public void setBand1(double lastParam) {
        Dispatch.call(this, "Band1", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type double
     */
    public double getBand2() {
        return Dispatch.get(this, "Band2").toDouble();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type double
     */
    public void setBand2(double lastParam) {
        Dispatch.call(this, "Band2", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type double
     */
    public double getBand3() {
        return Dispatch.get(this, "Band3").toDouble();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type double
     */
    public void setBand3(double lastParam) {
        Dispatch.call(this, "Band3", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type double
     */
    public double getBand4() {
        return Dispatch.get(this, "Band4").toDouble();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type double
     */
    public void setBand4(double lastParam) {
        Dispatch.call(this, "Band4", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type double
     */
    public double getBand5() {
        return Dispatch.get(this, "Band5").toDouble();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type double
     */
    public void setBand5(double lastParam) {
        Dispatch.call(this, "Band5", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type double
     */
    public double getBand6() {
        return Dispatch.get(this, "Band6").toDouble();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type double
     */
    public void setBand6(double lastParam) {
        Dispatch.call(this, "Band6", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type double
     */
    public double getBand7() {
        return Dispatch.get(this, "Band7").toDouble();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type double
     */
    public void setBand7(double lastParam) {
        Dispatch.call(this, "Band7", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type double
     */
    public double getBand8() {
        return Dispatch.get(this, "Band8").toDouble();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type double
     */
    public void setBand8(double lastParam) {
        Dispatch.call(this, "Band8", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type double
     */
    public double getBand9() {
        return Dispatch.get(this, "Band9").toDouble();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type double
     */
    public void setBand9(double lastParam) {
        Dispatch.call(this, "Band9", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type double
     */
    public double getBand10() {
        return Dispatch.get(this, "Band10").toDouble();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type double
     */
    public void setBand10(double lastParam) {
        Dispatch.call(this, "Band10", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type boolean
     */
    public void delete(boolean lastParam) {
        Dispatch.call(this, "Delete", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param newName   an input-parameter of type String
     * @param lastParam an input-parameter of type boolean
     */
	public void rename(String newName, boolean lastParam) {
		Dispatch.call(this, "Rename", newName, new Variant(lastParam));
	}

}
