/**
 * JacobGen generated file --- do not edit
 *
 * (http://www.sourceforge.net/projects/jacob-project */
package slash.metamusic.itunes.com.binding;

import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class IITConvertOperationStatus extends Dispatch {

    public static final String componentName = "iTunesLib.IITConvertOperationStatus";

    public IITConvertOperationStatus() {
        super(componentName);
    }

    /**
     * This constructor is used instead of a case operation to
     * turn a Dispatch object into a wider object - it must exist
     * in every wrapper class whose instances may be returned from
     * method calls wrapped in VT_DISPATCH Variants.
     */
    public IITConvertOperationStatus(Dispatch d) {
        // take over the IDispatch pointer
        m_pDispatch = d.m_pDispatch;
        // null out the input's pointer
        d.m_pDispatch = 0;
    }

    public IITConvertOperationStatus(String compName) {
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

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param trackName     an input-parameter of type String
     * @param progressValue an input-parameter of type int
     * @param lastParam     an input-parameter of type int
     */
    public void getConversionStatus(String trackName, int progressValue, int lastParam) {
        Dispatch.call(this, "GetConversionStatus", trackName, new Variant(progressValue), new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
     *
     * @param trackName     is an one-element array which sends the input-parameter
     *                      to the ActiveX-Component and receives the output-parameter
     * @param progressValue is an one-element array which sends the input-parameter
     *                      to the ActiveX-Component and receives the output-parameter
     * @param lastParam     is an one-element array which sends the input-parameter
     *                      to the ActiveX-Component and receives the output-parameter
     */
    public void getConversionStatus(String[] trackName, int[] progressValue, int[] lastParam) {
        Variant vnt_trackName = new Variant();
        if (trackName == null || trackName.length == 0)
            vnt_trackName.noParam();
        else
            vnt_trackName.putStringRef(trackName[0]);

        Variant vnt_progressValue = new Variant();
        if (progressValue == null || progressValue.length == 0)
            vnt_progressValue.noParam();
        else
            vnt_progressValue.putIntRef(progressValue[0]);

        Variant vnt_lastParam = new Variant();
        if (lastParam == null || lastParam.length == 0)
            vnt_lastParam.noParam();
        else
            vnt_lastParam.putIntRef(lastParam[0]);

        Dispatch.call(this, "GetConversionStatus", vnt_trackName, vnt_progressValue, vnt_lastParam);

        if (trackName != null && trackName.length > 0)
            trackName[0] = vnt_trackName.toString();
        if (progressValue != null && progressValue.length > 0)
            progressValue[0] = vnt_progressValue.toInt();
        if (lastParam != null && lastParam.length > 0)
            lastParam[0] = vnt_lastParam.toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     */
    public void stopConversion() {
        Dispatch.call(this, "StopConversion");
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type String
     */
    public String gettrackName() {
        return Dispatch.get(this, "trackName").toString();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int getprogressValue() {
        return Dispatch.get(this, "progressValue").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
	public int getmaxProgressValue() {
		return Dispatch.get(this, "maxProgressValue").toInt();
	}

}
