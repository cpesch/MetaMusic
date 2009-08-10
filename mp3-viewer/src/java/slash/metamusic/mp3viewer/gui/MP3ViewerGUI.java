/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2009 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.mp3viewer.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import slash.metamusic.gui.BaseDialogGUI;
import slash.metamusic.mp3.*;
import slash.metamusic.util.Files;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;

/**
 * A small graphical user interface for the viewing of MP3 files.
 *
 * @author Christian Pesch
 */

public class MP3ViewerGUI extends BaseDialogGUI {
    // static { Locale.setDefault(Locale.ENGLISH); }

    static ResourceBundle BUNDLE = ResourceBundle.getBundle(MP3ViewerGUI.class.getName());

    private static final String VIEW_DIRECTORY_PREFERENCE = "viewDirectory";

    private JPanel contentPane;
    private JTextField textFieldFile;
    private JButton buttonSelect;
    private JList listTags;

    private DefaultListModel listModel = new DefaultListModel();

    public void show() {
        createFrame(BUNDLE.getString("title"), "/slash/metamusic/mp3viewer/gui/MP3Viewer.png", contentPane, buttonSelect);

        FileDropHandler dropHandler = new FileDropHandler();
        frame.setTransferHandler(dropHandler);
        textFieldFile.setTransferHandler(dropHandler);
        listTags.setTransferHandler(dropHandler);

        buttonSelect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onSelect();
            }
        });

        listTags.setModel(listModel);

        openFrame(contentPane);

        final File file = getPreferencesFile();
        if (file != null) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    selectFile(file);
                }
            });
        }
    }


    private File getPreferencesFile() {
        File file = new File(System.getProperty("user.home"), "Desktop");
        return parseFile(preferences.get(VIEW_DIRECTORY_PREFERENCE, file.getAbsolutePath()));
    }

    private String setFile(File selected) {
        String file = selected.getAbsolutePath();
        textFieldFile.setText(file);
        return file;
    }

    private void selectFile(File selected) {               // TODO clean up this mess of a method
        String fileName = setFile(selected);
        listModel.clear();
        MP3File mp3 = new MP3File();
        try {
            mp3.read(selected);

            if (mp3.getFile() != null) {
                listModel.addElement("file: " + mp3.getFile().getAbsolutePath());
                listModel.addElement("size: " + mp3.getFileSize() + " bytes");
                listModel.addElement("valid: " + mp3.isValid());
            }

            if (!mp3.getFileName().isValid())
                listModel.addElement("valid name: " + mp3.getFileName().isValid());

            if (mp3.isValid()) {
                listModel.addElement("vbr: " + mp3.isVBR());
                listModel.addElement("bitrate: " + mp3.getBitRate() + " bit\\s");
                listModel.addElement("sample freq: " + mp3.getSampleFrequency() + " Hz");
                listModel.addElement("mode: " + mp3.getModeAsString() + " (" + mp3.getMode() + ")");
                listModel.addElement("time: " + mp3.getSecondsAsTimeString() + " (" + mp3.getSeconds() + " secs)");

                AbstractAudioProperties properties = mp3.getProperties();
                if (properties instanceof MP3Properties) {
                    MP3Properties mp3Properties = (MP3Properties) properties;
                    listModel.addElement("frames: " + mp3Properties.getFrames());
                    listModel.addElement("frame size: " + mp3Properties.getFrameSize());
                    listModel.addElement("version: " + mp3Properties.getMPEGVersionString() + " (" + mp3Properties.getMPEGVersion() + ")");
                    listModel.addElement("layer: " + mp3Properties.getMPEGLayerString());
                    listModel.addElement("padding: " + mp3Properties.getPadding());
                    listModel.addElement("protection: " + mp3Properties.isProtected());
                    if (mp3Properties.isProtected())
                        listModel.addElement("crc: " + mp3Properties.getCRC());
                    listModel.addElement("mode ext: " + mp3Properties.getModeExtension());
                    listModel.addElement("private: " + mp3Properties.isPrivate());
                    listModel.addElement("copyrighted: " + mp3Properties.isCopyrighted());
                    listModel.addElement("original: " + mp3Properties.isOriginal());
                    listModel.addElement("emphasis: " + mp3Properties.getEmphasisString() + " (" + mp3Properties.getEmphasis() + ")");
                    if (mp3Properties.getEncoder().length() > 0) {
                        listModel.addElement("encoder: " + mp3.getEncoder());
                    }
                    listModel.addElement("valid mp3: " + mp3Properties.isValid());
                }

                if (properties instanceof WAVProperties) {
                    WAVProperties wav = (WAVProperties) properties;
                    listModel.addElement("bits/sample: " + wav.getBitsPerSample());
                    listModel.addElement("valid wav: " + wav.isValid());
                }

                if (properties instanceof OggProperties) {
                    OggProperties ogg = (OggProperties) properties;
                    listModel.addElement("valid ogg: " + ogg.isValid());
                }

                listModel.addElement("APE: " + mp3.isAPE());
                if (mp3.isAPE())
                    listModel.addElement("APE rel: " + mp3.getApe().getVersion());
                listModel.addElement("ID3v1: " + mp3.isID3v1());
                if (mp3.isID3v1())
                    listModel.addElement("ID3v1.1: " + mp3.isID3v1dot1());
                listModel.addElement("ID3v2: " + mp3.isID3v2());
                if (mp3.isID3v2()) {
                    listModel.addElement("ID3v2 rel: " + mp3.getHead().getVersion().getVersionString());
                    for (ID3v2Frame f : mp3.getHead().getFrames()) {
                        StringBuffer buffer = new StringBuffer();
                        buffer.append(f.getTagName());
                        String description = f.getTagDescription();
                        if (description != null) {
                            buffer.append(" [").append(description).append("]");
                        }
                        String stringContent = f.getStringContent();
                        if (stringContent == null || stringContent.length() < 1000)
                            buffer.append(": ").append(stringContent);
                        else {
                            buffer.append(": [").append(stringContent.length()).append(" bytes]");
                        }
                        listModel.addElement(buffer.toString());
                    }
                }
                listModel.addElement("track: " + mp3.getTrack());
                listModel.addElement("artist: " + mp3.getArtist());
                listModel.addElement("album: " + mp3.getAlbum());
                if (mp3.isID3v1() || mp3.isID3v2()) {
                    listModel.addElement("year: " + mp3.getYear());
                    listModel.addElement("comment: " + mp3.getComment());
                    if (mp3.isID3v1dot1() || mp3.isID3v2() || mp3.getFileName().getIndex() != -1)
                        listModel.addElement("index: " + mp3.getIndex());
                    if (mp3.isID3v2() && mp3.getCount() != -1)
                        listModel.addElement("count: " + mp3.getCount());
                    listModel.addElement("genre: " + mp3.getGenre());
                    if (mp3.isID3v2()) {
                        int rating = mp3.getHead().getRating();
                        if (rating > 0)
                            listModel.addElement("rating: " + rating);
                        int playCount = mp3.getHead().getPlayCount();
                        if (playCount > 0)
                            listModel.addElement("play count: " + playCount);
                        Calendar playTime = mp3.getHead().getPlayTime();
                        if (playTime != null) {
                            String playTimeStr = DateFormat.getDateTimeInstance().format(playTime.getTime());
                            listModel.addElement("play time: " + playTimeStr);
                        }
                    }
                }
            }
        } catch (Throwable t) {
            listModel.addElement(t.getClass() + ":" + t.getMessage());
            t.printStackTrace();
        }
    }

    private File parseFile(String file) {
        return Files.findExistingPath(new File(file));
    }


    private void onSelect() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(BUNDLE.getString("select-file"));
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        File file = getPreferencesFile();
        chooser.setSelectedFile(file);
        int open = chooser.showOpenDialog(frame);
        if (open != JFileChooser.APPROVE_OPTION)
            return;

        File selected = chooser.getSelectedFile();
        if (selected == null)
            return;

        selectFile(selected);
    }

    protected void onExit() {
        preferences.put(VIEW_DIRECTORY_PREFERENCE, textFieldFile.getText());
        closeFrame();
        System.exit(0);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(2, 3, new Insets(5, 5, 5, 5), -1, -1));
        final JLabel label1 = new JLabel();
        this.$$$loadLabelText$$$(label1, ResourceBundle.getBundle("slash/metamusic/mp3viewer/gui/MP3ViewerGUI").getString("file"));
        contentPane.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonSelect = new JButton();
        this.$$$loadButtonText$$$(buttonSelect, ResourceBundle.getBundle("slash/metamusic/mp3viewer/gui/MP3ViewerGUI").getString("select"));
        contentPane.add(buttonSelect, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        contentPane.add(scrollPane1, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        listTags = new JList();
        scrollPane1.setViewportView(listTags);
        textFieldFile = new JTextField();
        contentPane.add(textFieldFile, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadLabelText$$$(JLabel component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setDisplayedMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadButtonText$$$(AbstractButton component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

    private class FileDropHandler extends TransferHandler {
        public boolean canImport(TransferSupport support) {
            return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
        }

        public boolean importData(TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }

            Transferable t = support.getTransferable();
            try {
                Object data = t.getTransferData(DataFlavor.javaFileListFlavor);
                //noinspection unchecked
                List<File> files = (List<File>) data;
                if (files.size() > 0)
                    selectFile(files.get(0));
            } catch (UnsupportedFlavorException e) {
                return false;
            } catch (IOException e) {
                return false;
            }

            return true;
        }
    }

    public static void main(String[] args) {
        setLookAndFeel();
        MP3ViewerGUI gui = new MP3ViewerGUI();
        gui.show();
    }
}
