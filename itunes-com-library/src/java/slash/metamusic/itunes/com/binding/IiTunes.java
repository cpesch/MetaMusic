/**
 * JacobGen generated file --- do not edit
 *
 * (http://www.sourceforge.net/projects/jacob-project */
package slash.metamusic.itunes.com.binding;

import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class IiTunes extends Dispatch {

    public static final String componentName = "iTunesLib.IiTunes";

    public IiTunes() {
        super(componentName);
    }

    /**
     * This constructor is used instead of a case operation to
     * turn a Dispatch object into a wider object - it must exist
     * in every wrapper class whose instances may be returned from
     * method calls wrapped in VT_DISPATCH Variants.
     */
    public IiTunes(Dispatch d) {
        // take over the IDispatch pointer
        m_pDispatch = d.m_pDispatch;
        // null out the input's pointer
        d.m_pDispatch = 0;
    }

    public IiTunes(String compName) {
        super(compName);
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     */
    public void backTrack() {
        Dispatch.call(this, "BackTrack");
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     */
    public void fastForward() {
        Dispatch.call(this, "FastForward");
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     */
    public void nextTrack() {
        Dispatch.call(this, "NextTrack");
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     */
    public void pause() {
        Dispatch.call(this, "Pause");
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
     */
    public void playFile(String lastParam) {
        Dispatch.call(this, "PlayFile", lastParam);
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     */
    public void playPause() {
        Dispatch.call(this, "PlayPause");
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     */
    public void previousTrack() {
        Dispatch.call(this, "PreviousTrack");
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     */
    public void resume() {
        Dispatch.call(this, "Resume");
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     */
    public void rewind() {
        Dispatch.call(this, "Rewind");
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     */
    public void stop() {
        Dispatch.call(this, "Stop");
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type String
     * @return the result is of type IITOperationStatus
     */
    public IITOperationStatus convertFile(String lastParam) {
        return new IITOperationStatus(Dispatch.call(this, "ConvertFile", lastParam).toDispatch());
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type Variant
     * @return the result is of type IITOperationStatus
     */
    public IITOperationStatus convertFiles(Variant lastParam) {
        return new IITOperationStatus(Dispatch.call(this, "ConvertFiles", lastParam).toDispatch());
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type Variant
     * @return the result is of type IITOperationStatus
     */
    public IITOperationStatus convertTrack(Variant lastParam) {
        return new IITOperationStatus(Dispatch.call(this, "ConvertTrack", lastParam).toDispatch());
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type Variant
     * @return the result is of type IITOperationStatus
     */
    public IITOperationStatus convertTracks(Variant lastParam) {
        return new IITOperationStatus(Dispatch.call(this, "ConvertTracks", lastParam).toDispatch());
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param majorVersion an input-parameter of type int
     * @param lastParam    an input-parameter of type int
     * @return the result is of type boolean
     */
    public boolean checkVersion(int majorVersion, int lastParam) {
        return Dispatch.call(this, "CheckVersion", new Variant(majorVersion), new Variant(lastParam)).toBoolean();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param sourceID   an input-parameter of type int
     * @param playlistID an input-parameter of type int
     * @param trackID    an input-parameter of type int
     * @param lastParam  an input-parameter of type int
     * @return the result is of type IITObject
     */
    public IITObject getITObjectByID(int sourceID, int playlistID, int trackID, int lastParam) {
        return new IITObject(Dispatch.call(this, "GetITObjectByID", new Variant(sourceID), new Variant(playlistID), new Variant(trackID), new Variant(lastParam)).toDispatch());
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type String
     * @return the result is of type IITPlaylist
     */
    public IITPlaylist createPlaylist(String lastParam) {
        return new IITPlaylist(Dispatch.call(this, "CreatePlaylist", lastParam).toDispatch());
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type String
     */
    public void openURL(String lastParam) {
        Dispatch.call(this, "OpenURL", lastParam);
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     */
    public void gotoMusicStoreHomePage() {
        Dispatch.call(this, "GotoMusicStoreHomePage");
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     */
    public void updateIPod() {
        Dispatch.call(this, "UpdateIPod");
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param numElems  an input-parameter of type int
     * @param data      an input-parameter of type Variant
     * @param lastParam an input-parameter of type String
     */
    public void authorize(int numElems, Variant data, String lastParam) {
        Dispatch.call(this, "Authorize", new Variant(numElems), data, lastParam);
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     */
    public void quit() {
        Dispatch.call(this, "Quit");
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type IITSourceCollection
     */
    public IITSourceCollection getSources() {
        return new IITSourceCollection(Dispatch.get(this, "Sources").toDispatch());
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type IITEncoderCollection
     */
    public IITEncoderCollection getEncoders() {
        return new IITEncoderCollection(Dispatch.get(this, "Encoders").toDispatch());
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type IITEQPresetCollection
     */
    public IITEQPresetCollection getEQPresets() {
        return new IITEQPresetCollection(Dispatch.get(this, "EQPresets").toDispatch());
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type IITVisualCollection
     */
    public IITVisualCollection getVisuals() {
        return new IITVisualCollection(Dispatch.get(this, "Visuals").toDispatch());
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type IITWindowCollection
     */
    public IITWindowCollection getWindows() {
        return new IITWindowCollection(Dispatch.get(this, "Windows").toDispatch());
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int getSoundVolume() {
        return Dispatch.get(this, "SoundVolume").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type int
     */
    public void setSoundVolume(int lastParam) {
        Dispatch.call(this, "SoundVolume", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type boolean
     */
    public boolean getMute() {
        return Dispatch.get(this, "Mute").toBoolean();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type boolean
     */
    public void setMute(boolean lastParam) {
        Dispatch.call(this, "Mute", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int getPlayerState() {
        return Dispatch.get(this, "PlayerState").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int getPlayerPosition() {
        return Dispatch.get(this, "PlayerPosition").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type int
     */
    public void setPlayerPosition(int lastParam) {
        Dispatch.call(this, "PlayerPosition", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type IITEncoder
     */
    public IITEncoder getCurrentEncoder() {
        return new IITEncoder(Dispatch.get(this, "CurrentEncoder").toDispatch());
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type IITEncoder
     */
    public void setCurrentEncoder(IITEncoder lastParam) {
        Dispatch.call(this, "CurrentEncoder", lastParam);
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type boolean
     */
    public boolean getVisualsEnabled() {
        return Dispatch.get(this, "VisualsEnabled").toBoolean();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type boolean
     */
    public void setVisualsEnabled(boolean lastParam) {
        Dispatch.call(this, "VisualsEnabled", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type boolean
     */
    public boolean getFullScreenVisuals() {
        return Dispatch.get(this, "FullScreenVisuals").toBoolean();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type boolean
     */
    public void setFullScreenVisuals(boolean lastParam) {
        Dispatch.call(this, "FullScreenVisuals", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type int
     */
    public int getVisualSize() {
        return Dispatch.get(this, "VisualSize").toInt();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type int
     */
    public void setVisualSize(int lastParam) {
        Dispatch.call(this, "VisualSize", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type IITVisual
     */
    public IITVisual getCurrentVisual() {
        return new IITVisual(Dispatch.get(this, "CurrentVisual").toDispatch());
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type IITVisual
     */
    public void setCurrentVisual(IITVisual lastParam) {
        Dispatch.call(this, "CurrentVisual", lastParam);
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type boolean
     */
    public boolean getEQEnabled() {
        return Dispatch.get(this, "EQEnabled").toBoolean();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type boolean
     */
    public void setEQEnabled(boolean lastParam) {
        Dispatch.call(this, "EQEnabled", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type IITEQPreset
     */
    public IITEQPreset getCurrentEQPreset() {
        return new IITEQPreset(Dispatch.get(this, "CurrentEQPreset").toDispatch());
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type IITEQPreset
     */
    public void setCurrentEQPreset(IITEQPreset lastParam) {
        Dispatch.call(this, "CurrentEQPreset", lastParam);
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type String
     */
    public String getCurrentStreamTitle() {
        return Dispatch.get(this, "CurrentStreamTitle").toString();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type String
     */
    public String getCurrentStreamURL() {
        return Dispatch.get(this, "CurrentStreamURL").toString();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type IITBrowserWindow
     */
    public IITBrowserWindow getBrowserWindow() {
        return new IITBrowserWindow(Dispatch.get(this, "BrowserWindow").toDispatch());
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type IITWindow
     */
    public IITWindow getEQWindow() {
        return new IITWindow(Dispatch.get(this, "EQWindow").toDispatch());
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type IITSource
     */
    public IITSource getLibrarySource() {
        return new IITSource(Dispatch.get(this, "LibrarySource").toDispatch());
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type IITLibraryPlaylist
     */
    public IITLibraryPlaylist getLibraryPlaylist() {
        return new IITLibraryPlaylist(Dispatch.get(this, "LibraryPlaylist").toDispatch());
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type IITTrack
     */
    public IITTrack getCurrentTrack() {
        return new IITTrack(Dispatch.get(this, "CurrentTrack").toDispatch());
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type IITPlaylist
     */
    public IITPlaylist getCurrentPlaylist() {
        return new IITPlaylist(Dispatch.get(this, "CurrentPlaylist").toDispatch());
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type IITTrackCollection
     */
    public IITTrackCollection getSelectedTracks() {
        return new IITTrackCollection(Dispatch.get(this, "SelectedTracks").toDispatch());
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type String
     */
    public String getVersion() {
        return Dispatch.get(this, "Version").toString();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type int
     */
    public void setOptions(int lastParam) {
        Dispatch.call(this, "SetOptions", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type String
     * @return the result is of type IITConvertOperationStatus
     */
    public IITConvertOperationStatus convertFile2(String lastParam) {
        return new IITConvertOperationStatus(Dispatch.call(this, "ConvertFile2", lastParam).toDispatch());
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type Variant
     * @return the result is of type IITConvertOperationStatus
     */
    public IITConvertOperationStatus convertFiles2(Variant lastParam) {
        return new IITConvertOperationStatus(Dispatch.call(this, "ConvertFiles2", lastParam).toDispatch());
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type Variant
     * @return the result is of type IITConvertOperationStatus
     */
    public IITConvertOperationStatus convertTrack2(Variant lastParam) {
        return new IITConvertOperationStatus(Dispatch.call(this, "ConvertTrack2", lastParam).toDispatch());
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type Variant
     * @return the result is of type IITConvertOperationStatus
     */
    public IITConvertOperationStatus convertTracks2(Variant lastParam) {
        return new IITConvertOperationStatus(Dispatch.call(this, "ConvertTracks2", lastParam).toDispatch());
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type boolean
     */
    public boolean getAppCommandMessageProcessingEnabled() {
        return Dispatch.get(this, "AppCommandMessageProcessingEnabled").toBoolean();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type boolean
     */
    public void setAppCommandMessageProcessingEnabled(boolean lastParam) {
        Dispatch.call(this, "AppCommandMessageProcessingEnabled", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type boolean
     */
    public boolean getForceToForegroundOnDialog() {
        return Dispatch.get(this, "ForceToForegroundOnDialog").toBoolean();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type boolean
     */
    public void setForceToForegroundOnDialog(boolean lastParam) {
        Dispatch.call(this, "ForceToForegroundOnDialog", new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type String
     * @return the result is of type IITEQPreset
     */
    public IITEQPreset createEQPreset(String lastParam) {
        return new IITEQPreset(Dispatch.call(this, "CreateEQPreset", lastParam).toDispatch());
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param playlistName an input-parameter of type String
     * @param lastParam    an input-parameter of type Variant
     * @return the result is of type IITPlaylist
     */
    public IITPlaylist createPlaylistInSource(String playlistName, Variant lastParam) {
        return new IITPlaylist(Dispatch.call(this, "CreatePlaylistInSource", playlistName, lastParam).toDispatch());
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param previousEnabled    an input-parameter of type boolean
     * @param playPauseStopState an input-parameter of type int
     * @param lastParam          an input-parameter of type boolean
     */
    public void getPlayerButtonsState(boolean previousEnabled, int playPauseStopState, boolean lastParam) {
        Dispatch.call(this, "GetPlayerButtonsState", new Variant(previousEnabled), new Variant(playPauseStopState), new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
     *
     * @param previousEnabled    is an one-element array which sends the input-parameter
     *                           to the ActiveX-Component and receives the output-parameter
     * @param playPauseStopState is an one-element array which sends the input-parameter
     *                           to the ActiveX-Component and receives the output-parameter
     * @param lastParam          is an one-element array which sends the input-parameter
     *                           to the ActiveX-Component and receives the output-parameter
     */
    public void getPlayerButtonsState(boolean[] previousEnabled, int[] playPauseStopState, boolean[] lastParam) {
        Variant vnt_previousEnabled = new Variant();
        if (previousEnabled == null || previousEnabled.length == 0)
            vnt_previousEnabled.noParam();
        else
            vnt_previousEnabled.putBooleanRef(previousEnabled[0]);

        Variant vnt_playPauseStopState = new Variant();
        if (playPauseStopState == null || playPauseStopState.length == 0)
            vnt_playPauseStopState.noParam();
        else
            vnt_playPauseStopState.putIntRef(playPauseStopState[0]);

        Variant vnt_lastParam = new Variant();
        if (lastParam == null || lastParam.length == 0)
            vnt_lastParam.noParam();
        else
            vnt_lastParam.putBooleanRef(lastParam[0]);

        Dispatch.call(this, "GetPlayerButtonsState", vnt_previousEnabled, vnt_playPauseStopState, vnt_lastParam);

        if (previousEnabled != null && previousEnabled.length > 0)
            previousEnabled[0] = vnt_previousEnabled.toBoolean();
        if (playPauseStopState != null && playPauseStopState.length > 0)
            playPauseStopState[0] = vnt_playPauseStopState.toInt();
        if (lastParam != null && lastParam.length > 0)
            lastParam[0] = vnt_lastParam.toBoolean();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param playerButton an input-parameter of type int
     * @param lastParam    an input-parameter of type int
     */
    public void playerButtonClicked(int playerButton, int lastParam) {
        Dispatch.call(this, "PlayerButtonClicked", new Variant(playerButton), new Variant(lastParam));
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type Variant
     * @return the result is of type boolean
     */
    public boolean getCanSetShuffle(Variant lastParam) {
        return Dispatch.call(this, "CanSetShuffle", lastParam).toBoolean();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type Variant
     * @return the result is of type boolean
     */
    public boolean getCanSetSongRepeat(Variant lastParam) {
        return Dispatch.call(this, "CanSetSongRepeat", lastParam).toBoolean();
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type IITConvertOperationStatus
     */
    public IITConvertOperationStatus getConvertOperationStatus() {
        return new IITConvertOperationStatus(Dispatch.get(this, "ConvertOperationStatus").toDispatch());
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type String
     */
    public void subscribeToPodcast(String lastParam) {
        Dispatch.call(this, "SubscribeToPodcast", lastParam);
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     */
    public void updatePodcastFeeds() {
        Dispatch.call(this, "UpdatePodcastFeeds");
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param lastParam an input-parameter of type String
     * @return the result is of type IITPlaylist
     */
    public IITPlaylist createFolder(String lastParam) {
        return new IITPlaylist(Dispatch.call(this, "CreateFolder", lastParam).toDispatch());
    }

    /**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @param folderName an input-parameter of type String
     * @param lastParam  an input-parameter of type Variant
     * @return the result is of type IITPlaylist
     */
	public IITPlaylist createFolderInSource(String folderName, Variant lastParam) {
		return new IITPlaylist(Dispatch.call(this, "CreateFolderInSource", folderName, lastParam).toDispatch());
	}

	/**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type boolean
     */
	public boolean getSoundVolumeControlEnabled() {
		return Dispatch.get(this, "SoundVolumeControlEnabled").toBoolean();
	}

	/**
     * Wrapper for calling the ActiveX-Method with input-parameter(s).
     *
     * @return the result is of type String
     */
	public String getLibraryXMLPath() {
		return Dispatch.get(this, "LibraryXMLPath").toString();
	}

}
