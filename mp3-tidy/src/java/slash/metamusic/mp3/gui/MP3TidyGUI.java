/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2007 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.mp3.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import slash.metamusic.gui.BaseDialogGUI;
import slash.metamusic.mp3.tools.MP3Tidy;
import slash.metamusic.util.Files;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.*;
import java.util.List;

/**
 * A small graphical user interface for the tidying of MP3 files.
 *
 * @author Christian Pesch
 */

public class MP3TidyGUI extends BaseDialogGUI {
    // static { Locale.setDefault(Locale.ENGLISH); }

    static ResourceBundle BUNDLE = ResourceBundle.getBundle(MP3TidyGUI.class.getName());

    private static final String TIDY_DIRECTORY_PREFERENCE = "tidyDirectory";
    private static final String COVER_DIRECTORY_PREFERENCE = "coverDirectory";
    private static final String LYRICS_DIRECTORY_PREFERENCE = "lyricsDirectory";
    private static final String ADD_COVER_PREFERENCE = "addCover";
    private static final String ADD_LYRICS_PREFERENCE = "addLyrics";
    private static final String ADD_METADATA_PREFERENCE = "addMetaData";
    private static final String RENAME_FILE_TO_TAGS_PREFERENCE = "renameFileToTags";
    private static final String WRITE_ID3v1_TAGS_PREFERENCE = "writeID3v1Tags";
    private static final String WRITE_ID3v2_TAGS_PREFERENCE = "writeID3v2Tags";
    private static final String REMOVE_ITUNES_TAGS_PREFERENCE = "removeiTunesTags";
    private static final String REMOVE_MUSIC_BRAINZ_TAGS_PREFERENCE = "removeMusicBrainzTags";
    private static final String REMOVE_MUSIC_MATCH_TAGS_PREFERENCE = "removeMusicMatchTags";
    private static final String REMOVE_WINDOWS_MEDIA_PLAYER_TAGS_PREFERENCE = "removeWindowsMediaPlayerTags";
    private static final String UNIFY_TAGS_PREFERENCE = "unifyTags";

    private JPanel contentPane;
    private JButton buttonStart;
    private JButton buttonExit;
    private JButton buttonSelectTidyDirectory;
    private JTextField textFieldTidyDirectory;
    private JLabel labelFileCount;
    private JLabel labelProcessedFiles;
    private JLabel labelFailedFiles;
    private JLabel labelModifiedFiles;
    private JLabel labelRenamedFiles;
    private JLabel labelCleanedTags;
    private JLabel labelExtendedTags;
    private JProgressBar progressBar;
    private JList listMessages;

    private JTextField textFieldCoverDirectory;
    private JButton buttonSelectCoverDirectory;
    private JButton buttonSelectLyricsDirectory;
    private JTextField textFieldLyricsDirectory;
    private JCheckBox checkBoxAddCover;
    private JCheckBox checkBoxAddLyrics;
    private JCheckBox checkBoxAddMetaData;
    private JCheckBox checkBoxRenameFileToTags;
    private JCheckBox checkBoxWriteID3v1Tags;
    private JCheckBox checkBoxWriteID3v2Tags;
    private JCheckBox checkBoxRemoveiTunesTags;
    private JCheckBox checkBoxRemoveMusicBrainzTags;
    private JCheckBox checkBoxRemoveMusicMatchTags;
    private JCheckBox checkBoxRemoveWindowsMediaPlayerTags;
    private JCheckBox checkBoxUnifyTags;

    private DefaultListModel listModel = new DefaultListModel();

    private MP3Tidy tidyer = new MP3Tidy();
    private boolean running = false;
    private final Object mutex = new Object();


    public void show() {
        createFrame(BUNDLE.getString("title"), "/slash/metamusic/mp3/gui/MP3Tidy.png", contentPane, buttonStart);

        FileDropHandler dropHandler = new FileDropHandler();
        frame.setTransferHandler(dropHandler);
        textFieldTidyDirectory.setTransferHandler(dropHandler);
        listMessages.setTransferHandler(dropHandler);

        buttonSelectTidyDirectory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onSelectTidyDirectory();
            }
        });

        buttonStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onStart();
            }
        });

        buttonExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onExit();
            }
        });

        progressBar.setMinimum(0);
        progressBar.setStringPainted(true);

        listMessages.setModel(listModel);
        listModel.addListDataListener(new ListDataListener() {
            public void intervalAdded(ListDataEvent e) {
                scrollToBottom();
            }

            public void intervalRemoved(ListDataEvent e) {
                scrollToBottom();
            }

            public void contentsChanged(ListDataEvent e) {
                scrollToBottom();
            }
        });

        buttonSelectCoverDirectory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onSelectCoverDirectory();
            }
        });

        buttonSelectLyricsDirectory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onSelectLyricsDirectory();
            }
        });

        checkBoxAddCover.setSelected(preferences.getBoolean(ADD_COVER_PREFERENCE, true));
        checkBoxAddLyrics.setSelected(preferences.getBoolean(ADD_LYRICS_PREFERENCE, true));
        checkBoxAddMetaData.setSelected(preferences.getBoolean(ADD_LYRICS_PREFERENCE, true));
        checkBoxRenameFileToTags.setSelected(preferences.getBoolean(RENAME_FILE_TO_TAGS_PREFERENCE, true));
        checkBoxWriteID3v1Tags.setSelected(preferences.getBoolean(WRITE_ID3v1_TAGS_PREFERENCE, false));
        checkBoxWriteID3v2Tags.setSelected(preferences.getBoolean(WRITE_ID3v2_TAGS_PREFERENCE, true));
        checkBoxRemoveiTunesTags.setSelected(preferences.getBoolean(REMOVE_ITUNES_TAGS_PREFERENCE, false));
        checkBoxRemoveMusicBrainzTags.setSelected(preferences.getBoolean(REMOVE_MUSIC_BRAINZ_TAGS_PREFERENCE, true));
        checkBoxRemoveMusicMatchTags.setSelected(preferences.getBoolean(REMOVE_MUSIC_MATCH_TAGS_PREFERENCE, true));
        checkBoxRemoveWindowsMediaPlayerTags.setSelected(preferences.getBoolean(REMOVE_WINDOWS_MEDIA_PLAYER_TAGS_PREFERENCE, true));
        checkBoxUnifyTags.setSelected(preferences.getBoolean(UNIFY_TAGS_PREFERENCE, true));

        tidyer.addNotifier(new UIUpdater());

        openFrame(contentPane);

        textFieldCoverDirectory.setText(getPreferencesCoverDirectory().getAbsolutePath());
        textFieldLyricsDirectory.setText(getPreferencesLyricsDirectory().getAbsolutePath());
        List<File> tidyDirectories = getPreferencesTidyDirectories();
        if (tidyDirectories.size() > 0)
            selectTidyDirectories(tidyDirectories);
    }

    private void scrollToBottom() {
        // put in AWT Eventqueue to act after changes of the model
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final int lines = listModel.getSize() - 1;
                if (lines > 0) {
                    // workaround to ensure visiblity on startup
                    int count = 0;
                    while (listMessages.getLastVisibleIndex() != lines && count++ < 10) {
                        listMessages.ensureIndexIsVisible(lines);
                    }
                }
            }
        });
    }

    private File getPreferencesCoverDirectory() {
        File coverDirectory = new File(System.getProperty("user.home"), ".coverdb");
        return new File(preferences.get(COVER_DIRECTORY_PREFERENCE, coverDirectory.getAbsolutePath()));
    }

    private File getPreferencesLyricsDirectory() {
        File lyricsDirectory = new File(System.getProperty("user.home"), ".lyricsdb");
        return new File(preferences.get(LYRICS_DIRECTORY_PREFERENCE, lyricsDirectory.getAbsolutePath()));
    }

    private List<File> getPreferencesTidyDirectories() {
        File tidyDirectory = new File(System.getProperty("user.home"), "Desktop");
        return parseDirectories(preferences.get(TIDY_DIRECTORY_PREFERENCE, tidyDirectory.getAbsolutePath()));
    }

    private File getCoverDirectory() {
        return new File(textFieldCoverDirectory.getText());
    }

    private void setCoverDirectory(File selected) {
        textFieldCoverDirectory.setText(selected.getAbsolutePath());
    }

    private File getLyricsDirectory() {
        return new File(textFieldLyricsDirectory.getText());
    }

    private void setLyricsDirectory(File selected) {
        textFieldLyricsDirectory.setText(selected.getAbsolutePath());
    }

    private List<File> getTidyDirectories() {
        return parseDirectories(textFieldTidyDirectory.getText());
    }

    private String setTidyDirectories(List<File> selected) {
        String tidyDirectories = formatDirectories(selected);
        textFieldTidyDirectory.setText(tidyDirectories);
        return tidyDirectories;
    }

    private void selectTidyDirectories(List<File> selected) {
        String tidyDirectories = setTidyDirectories(selected);
        listModel.clear();
        listModel.addElement(MessageFormat.format(BUNDLE.getString("selected"), tidyDirectories));
    }

    private List<File> parseDirectories(String directories) {
        List<File> result = new ArrayList<File>();
        StringTokenizer tokenizer = new StringTokenizer(directories, ",");
        while (tokenizer.hasMoreTokens()) {
            File directory = new File(tokenizer.nextToken());
            directory = Files.findExistingPath(directory);
            if (directory != null)
                result.add(directory);
        }
        return result;
    }

    private String formatDirectories(List<File> directories) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < directories.size(); i++) {
            File directory = directories.get(i);
            if (i > 0 && i < directories.size())
                buffer.append(",");
            buffer.append(directory.getAbsolutePath());
        }
        return buffer.toString();
    }


    private void onSelectCoverDirectory() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(BUNDLE.getString("select-cover-directory"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setSelectedFile(getCoverDirectory());
        int open = chooser.showOpenDialog(frame);
        if (open != JFileChooser.APPROVE_OPTION)
            return;

        File selected = chooser.getSelectedFile();
        if (selected == null || selected.getName().length() == 0)
            return;

        setCoverDirectory(selected);
    }

    private void onSelectLyricsDirectory() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(BUNDLE.getString("select-lyrics-directory"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setSelectedFile(getLyricsDirectory());
        int open = chooser.showOpenDialog(frame);
        if (open != JFileChooser.APPROVE_OPTION)
            return;

        File selected = chooser.getSelectedFile();
        if (selected == null || selected.getName().length() == 0)
            return;

        setLyricsDirectory(selected);
    }


    private void onSelectTidyDirectory() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(BUNDLE.getString("select-tidy-directory"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setMultiSelectionEnabled(true);
        List<File> tidyDirectories = getTidyDirectories();
        chooser.setSelectedFiles(tidyDirectories.toArray(new File[tidyDirectories.size()]));
        int open = chooser.showOpenDialog(frame);
        if (open != JFileChooser.APPROVE_OPTION)
            return;

        File[] selected = chooser.getSelectedFiles();
        if (selected == null || selected.length == 0)
            return;

        final List<File> directories = Arrays.asList(selected);
        selectTidyDirectories(directories);
    }

    private String createDirectory(String directory) {
        File f = new File(directory);
        if (!f.exists())
            if (!f.mkdirs())
                JOptionPane.showMessageDialog(frame,
                        MessageFormat.format(BUNDLE.getString("cannot-create-directory"), f.getAbsolutePath()),
                        frame.getTitle(), JOptionPane.ERROR_MESSAGE);
        return f.getAbsolutePath();
    }

    private void onStart() {
        synchronized (mutex) {
            if (running) {
                running = false;
                buttonStart.setEnabled(false);
                return;
            } else
                running = true;
        }

        if (getTidyDirectories().size() == 0)
            onSelectTidyDirectory();
        if (getTidyDirectories().size() == /*still*/ 0)
            return;
        List<File> tidyDirectories = parseDirectories(textFieldTidyDirectory.getText());
        setTidyDirectories(tidyDirectories);
        tidyer.prepare(tidyDirectories);

        String coverDirectory = createDirectory(textFieldCoverDirectory.getText());
        if (coverDirectory == null) return;
        tidyer.setCoverDirectoryName(coverDirectory);

        String lyricsDirectory = createDirectory(textFieldLyricsDirectory.getText());
        if (lyricsDirectory == null) return;
        tidyer.setLyricsDirectoryName(lyricsDirectory);

        tidyer.setAddCover(checkBoxAddCover.isSelected());
        tidyer.setAddCoverToFolder(false); // TODO make this configurable later
        tidyer.setAddLyrics(checkBoxAddLyrics.isSelected());
        tidyer.setAddMetaData(checkBoxAddMetaData.isSelected());
        tidyer.setRenameFileToTags(checkBoxRenameFileToTags.isSelected());
        tidyer.setWriteID3v1(checkBoxWriteID3v1Tags.isSelected());
        tidyer.setWriteID3v2(checkBoxWriteID3v2Tags.isSelected());
        tidyer.setRemoveiTunesTags(checkBoxRemoveiTunesTags.isSelected());
        tidyer.setRemoveMusicBrainzTags(checkBoxRemoveMusicBrainzTags.isSelected());
        tidyer.setRemoveMusicMatchTags(checkBoxRemoveMusicMatchTags.isSelected());
        tidyer.setRemoveWindowsMediaPlayerTags(checkBoxRemoveWindowsMediaPlayerTags.isSelected());
        tidyer.setUnifyTags(checkBoxUnifyTags.isSelected());

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                startWaitCursor(frame.getRootPane());
                buttonSelectTidyDirectory.setEnabled(false);
                buttonExit.setEnabled(false);
                buttonStart.setText(BUNDLE.getString("cancel"));
            }
        });

        Thread runner = new Thread(new Runnable() {
            public void run() {
                tidyer.start();

                while (tidyer.next()) {
                    synchronized (mutex) {
                        if (!running)
                            break;
                    }
                }

                synchronized (mutex) {
                    running = false;
                }

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        stopWaitCursor(frame.getRootPane());
                        buttonSelectTidyDirectory.setEnabled(true);
                        buttonStart.setEnabled(true);
                        buttonExit.setEnabled(true);
                        buttonStart.setText(BUNDLE.getString("start"));
                    }
                });
            }
        });
        runner.start();
    }

    protected void onExit() {
        preferences.put(COVER_DIRECTORY_PREFERENCE, textFieldCoverDirectory.getText());
        preferences.put(LYRICS_DIRECTORY_PREFERENCE, textFieldLyricsDirectory.getText());
        preferences.put(TIDY_DIRECTORY_PREFERENCE, textFieldTidyDirectory.getText());
        preferences.putBoolean(ADD_COVER_PREFERENCE, checkBoxAddCover.isSelected());
        preferences.putBoolean(ADD_LYRICS_PREFERENCE, checkBoxAddLyrics.isSelected());
        preferences.putBoolean(ADD_METADATA_PREFERENCE, checkBoxAddMetaData.isSelected());
        preferences.putBoolean(RENAME_FILE_TO_TAGS_PREFERENCE, checkBoxRenameFileToTags.isSelected());
        preferences.putBoolean(WRITE_ID3v1_TAGS_PREFERENCE, checkBoxWriteID3v1Tags.isSelected());
        preferences.putBoolean(WRITE_ID3v2_TAGS_PREFERENCE, checkBoxWriteID3v2Tags.isSelected());
        preferences.putBoolean(REMOVE_ITUNES_TAGS_PREFERENCE, checkBoxRemoveiTunesTags.isSelected());
        preferences.putBoolean(REMOVE_MUSIC_BRAINZ_TAGS_PREFERENCE, checkBoxRemoveMusicBrainzTags.isSelected());
        preferences.putBoolean(REMOVE_MUSIC_MATCH_TAGS_PREFERENCE, checkBoxRemoveMusicMatchTags.isSelected());
        preferences.putBoolean(REMOVE_WINDOWS_MEDIA_PLAYER_TAGS_PREFERENCE, checkBoxRemoveWindowsMediaPlayerTags.isSelected());
        preferences.putBoolean(UNIFY_TAGS_PREFERENCE, checkBoxUnifyTags.isSelected());
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
        contentPane.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JTabbedPane tabbedPane1 = new JTabbedPane();
        contentPane.add(tabbedPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(4, 1, new Insets(10, 10, 10, 10), -1, -1));
        tabbedPane1.addTab(ResourceBundle.getBundle("slash/metamusic/mp3/gui/MP3TidyGUI").getString("tidy"), panel1);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel2.add(panel3, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonStart = new JButton();
        this.$$$loadButtonText$$$(buttonStart, ResourceBundle.getBundle("slash/metamusic/mp3/gui/MP3TidyGUI").getString("start"));
        panel3.add(buttonStart, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonExit = new JButton();
        this.$$$loadButtonText$$$(buttonExit, ResourceBundle.getBundle("slash/metamusic/mp3/gui/MP3TidyGUI").getString("exit"));
        panel3.add(buttonExit, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel4.setVisible(true);
        panel1.add(panel4, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        progressBar = new JProgressBar();
        panel4.add(progressBar, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        this.$$$loadLabelText$$$(label1, ResourceBundle.getBundle("slash/metamusic/mp3/gui/MP3TidyGUI").getString("progress"));
        panel4.add(label1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        this.$$$loadLabelText$$$(label2, ResourceBundle.getBundle("slash/metamusic/mp3/gui/MP3TidyGUI").getString("messages"));
        panel4.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel4.add(scrollPane1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        listMessages = new JList();
        scrollPane1.setViewportView(listMessages);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(3, 6, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel5, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        this.$$$loadLabelText$$$(label3, ResourceBundle.getBundle("slash/metamusic/mp3/gui/MP3TidyGUI").getString("processed-files"));
        panel5.add(label3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelProcessedFiles = new JLabel();
        labelProcessedFiles.setHorizontalAlignment(2);
        labelProcessedFiles.setHorizontalTextPosition(2);
        labelProcessedFiles.setText("-");
        panel5.add(labelProcessedFiles, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        this.$$$loadLabelText$$$(label4, ResourceBundle.getBundle("slash/metamusic/mp3/gui/MP3TidyGUI").getString("extended-tags"));
        panel5.add(label4, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelExtendedTags = new JLabel();
        labelExtendedTags.setText("-");
        panel5.add(labelExtendedTags, new GridConstraints(2, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        this.$$$loadLabelText$$$(label5, ResourceBundle.getBundle("slash/metamusic/mp3/gui/MP3TidyGUI").getString("modified-files"));
        panel5.add(label5, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelModifiedFiles = new JLabel();
        labelModifiedFiles.setText("-");
        panel5.add(labelModifiedFiles, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        this.$$$loadLabelText$$$(label6, ResourceBundle.getBundle("slash/metamusic/mp3/gui/MP3TidyGUI").getString("cleaned-tags"));
        panel5.add(label6, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelCleanedTags = new JLabel();
        labelCleanedTags.setInheritsPopupMenu(true);
        labelCleanedTags.setText("-");
        panel5.add(labelCleanedTags, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        this.$$$loadLabelText$$$(label7, ResourceBundle.getBundle("slash/metamusic/mp3/gui/MP3TidyGUI").getString("file-count"));
        panel5.add(label7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelFileCount = new JLabel();
        labelFileCount.setHorizontalAlignment(2);
        labelFileCount.setText("-");
        panel5.add(labelFileCount, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        this.$$$loadLabelText$$$(label8, ResourceBundle.getBundle("slash/metamusic/mp3/gui/MP3TidyGUI").getString("file-extension"));
        panel5.add(label8, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("mp3");
        panel5.add(label9, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        this.$$$loadLabelText$$$(label10, ResourceBundle.getBundle("slash/metamusic/mp3/gui/MP3TidyGUI").getString("failed-files"));
        panel5.add(label10, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelFailedFiles = new JLabel();
        labelFailedFiles.setText("-");
        panel5.add(labelFailedFiles, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label11 = new JLabel();
        this.$$$loadLabelText$$$(label11, ResourceBundle.getBundle("slash/metamusic/mp3/gui/MP3TidyGUI").getString("renamed-files"));
        panel5.add(label11, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelRenamedFiles = new JLabel();
        labelRenamedFiles.setText("-");
        panel5.add(labelRenamedFiles, new GridConstraints(1, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label12 = new JLabel();
        this.$$$loadLabelText$$$(label12, ResourceBundle.getBundle("slash/metamusic/mp3/gui/MP3TidyGUI").getString("directory"));
        panel6.add(label12, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonSelectTidyDirectory = new JButton();
        this.$$$loadButtonText$$$(buttonSelectTidyDirectory, ResourceBundle.getBundle("slash/metamusic/mp3/gui/MP3TidyGUI").getString("select"));
        panel6.add(buttonSelectTidyDirectory, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textFieldTidyDirectory = new JTextField();
        textFieldTidyDirectory.setHorizontalAlignment(2);
        panel6.add(textFieldTidyDirectory, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(4, 1, new Insets(10, 10, 10, 10), -1, -1));
        tabbedPane1.addTab(ResourceBundle.getBundle("slash/metamusic/mp3/gui/MP3TidyGUI").getString("options"), panel7);
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel7.add(panel8, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel8.add(panel9, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label13 = new JLabel();
        this.$$$loadLabelText$$$(label13, ResourceBundle.getBundle("slash/metamusic/mp3/gui/MP3TidyGUI").getString("lyrics-directory"));
        panel9.add(label13, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textFieldLyricsDirectory = new JTextField();
        panel9.add(textFieldLyricsDirectory, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        buttonSelectLyricsDirectory = new JButton();
        this.$$$loadButtonText$$$(buttonSelectLyricsDirectory, ResourceBundle.getBundle("slash/metamusic/mp3/gui/MP3TidyGUI").getString("select"));
        panel9.add(buttonSelectLyricsDirectory, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label14 = new JLabel();
        this.$$$loadLabelText$$$(label14, ResourceBundle.getBundle("slash/metamusic/mp3/gui/MP3TidyGUI").getString("cover-directory"));
        panel9.add(label14, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textFieldCoverDirectory = new JTextField();
        panel9.add(textFieldCoverDirectory, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        buttonSelectCoverDirectory = new JButton();
        this.$$$loadButtonText$$$(buttonSelectCoverDirectory, ResourceBundle.getBundle("slash/metamusic/mp3/gui/MP3TidyGUI").getString("select"));
        panel9.add(buttonSelectCoverDirectory, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(6, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel7.add(panel10, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        checkBoxRemoveiTunesTags = new JCheckBox();
        this.$$$loadButtonText$$$(checkBoxRemoveiTunesTags, ResourceBundle.getBundle("slash/metamusic/mp3/gui/MP3TidyGUI").getString("remove-itunes-tags"));
        panel10.add(checkBoxRemoveiTunesTags, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        checkBoxRemoveWindowsMediaPlayerTags = new JCheckBox();
        this.$$$loadButtonText$$$(checkBoxRemoveWindowsMediaPlayerTags, ResourceBundle.getBundle("slash/metamusic/mp3/gui/MP3TidyGUI").getString("remove-windows-media-player-tags"));
        panel10.add(checkBoxRemoveWindowsMediaPlayerTags, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        checkBoxRemoveMusicMatchTags = new JCheckBox();
        this.$$$loadButtonText$$$(checkBoxRemoveMusicMatchTags, ResourceBundle.getBundle("slash/metamusic/mp3/gui/MP3TidyGUI").getString("remove-music-match-tags"));
        panel10.add(checkBoxRemoveMusicMatchTags, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        checkBoxRemoveMusicBrainzTags = new JCheckBox();
        this.$$$loadButtonText$$$(checkBoxRemoveMusicBrainzTags, ResourceBundle.getBundle("slash/metamusic/mp3/gui/MP3TidyGUI").getString("remove-music-brainz-tags"));
        panel10.add(checkBoxRemoveMusicBrainzTags, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        checkBoxAddMetaData = new JCheckBox();
        this.$$$loadButtonText$$$(checkBoxAddMetaData, ResourceBundle.getBundle("slash/metamusic/mp3/gui/MP3TidyGUI").getString("add-metadata"));
        panel10.add(checkBoxAddMetaData, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        checkBoxUnifyTags = new JCheckBox();
        this.$$$loadButtonText$$$(checkBoxUnifyTags, ResourceBundle.getBundle("slash/metamusic/mp3/gui/MP3TidyGUI").getString("unify-tags"));
        panel10.add(checkBoxUnifyTags, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        checkBoxRenameFileToTags = new JCheckBox();
        this.$$$loadButtonText$$$(checkBoxRenameFileToTags, ResourceBundle.getBundle("slash/metamusic/mp3/gui/MP3TidyGUI").getString("rename-file-to-tags"));
        panel10.add(checkBoxRenameFileToTags, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        checkBoxWriteID3v1Tags = new JCheckBox();
        this.$$$loadButtonText$$$(checkBoxWriteID3v1Tags, ResourceBundle.getBundle("slash/metamusic/mp3/gui/MP3TidyGUI").getString("write-id3v1-tags"));
        panel10.add(checkBoxWriteID3v1Tags, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        checkBoxWriteID3v2Tags = new JCheckBox();
        this.$$$loadButtonText$$$(checkBoxWriteID3v2Tags, ResourceBundle.getBundle("slash/metamusic/mp3/gui/MP3TidyGUI").getString("write-id3v2-tags"));
        panel10.add(checkBoxWriteID3v2Tags, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        checkBoxAddCover = new JCheckBox();
        this.$$$loadButtonText$$$(checkBoxAddCover, ResourceBundle.getBundle("slash/metamusic/mp3/gui/MP3TidyGUI").getString("add-cover"));
        panel10.add(checkBoxAddCover, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        checkBoxAddLyrics = new JCheckBox();
        this.$$$loadButtonText$$$(checkBoxAddLyrics, ResourceBundle.getBundle("slash/metamusic/mp3/gui/MP3TidyGUI").getString("add-lyrics"));
        panel10.add(checkBoxAddLyrics, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel7.add(spacer2, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel11 = new JPanel();
        panel7.add(panel11, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
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

    private class UIUpdater implements MP3Tidy.Notifier {
        private Date startDate;

        public void startedToPrepare() {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    labelFileCount.setText("0");
                    labelProcessedFiles.setText("0");
                    labelFailedFiles.setText("0");
                    labelModifiedFiles.setText("0");
                    labelRenamedFiles.setText("0");
                    labelCleanedTags.setText("0");
                    labelExtendedTags.setText("0");
                    listModel.clear();
                    progressBar.setValue(0);
                }
            });
        }

        public void preparing(final int fileCount, final File directory) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    listModel.addElement(MessageFormat.format(BUNDLE.getString("preparing"), fileCount, directory.getAbsolutePath()));
                    labelFileCount.setText(Integer.toString(fileCount));
                }
            });
        }

        public void finishedToPrepare(final int fileCount) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    labelFileCount.setText(Integer.toString(fileCount));
                    progressBar.setMaximum(fileCount);
                }
            });
        }

        public void started() {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    startDate = new Date();
                    listModel.addElement(MessageFormat.format(BUNDLE.getString("started"), DateFormat.getDateTimeInstance().format(startDate)));
                }
            });
        }

        public void processing(final int processedFiles) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    labelProcessedFiles.setText(Integer.toString(processedFiles));
                    progressBar.setValue(processedFiles);
                }
            });
        }

        public void failed(final int failedFileCount, final File file) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    labelFailedFiles.setText(Integer.toString(failedFileCount));
                    if (file != null)
                        listModel.addElement(MessageFormat.format(BUNDLE.getString("failed"), file.getAbsolutePath()));
                }
            });
        }

        public void processed(final int movedFileCount, final int renamedFileCount, final int modifiedFileCount,
                              final int cleanedTagCount, final int extendedTagCount,
                              final File file,
                              final boolean movedFile, final boolean renamedFile, final boolean modifiedFile,
                              final boolean cleanedTags, final boolean extendedTags) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    labelModifiedFiles.setText(Integer.toString(modifiedFileCount));
                    labelRenamedFiles.setText(Integer.toString(renamedFileCount));
                    labelCleanedTags.setText(Integer.toString(cleanedTagCount));
                    labelExtendedTags.setText(Integer.toString(extendedTagCount));
                    StringBuffer buffer = new StringBuffer();
                    if (modifiedFile)
                        buffer.append(BUNDLE.getString("modified"));
                    if (modifiedFile && renamedFile)
                        buffer.append(" & ");
                    if (renamedFile)
                        buffer.append(BUNDLE.getString("renamed"));

                    if ((modifiedFile || renamedFile) && (cleanedTags || extendedTags))
                        buffer.append(", ");
                    else
                        buffer.append(" ");

                    if (cleanedTags)
                        buffer.append(BUNDLE.getString("cleaned"));
                    if (cleanedTags && extendedTags)
                        buffer.append(" & ");
                    if (extendedTags)
                        buffer.append(BUNDLE.getString("extended"));

                    if (cleanedTags || extendedTags)
                        buffer.append(" ");

                    buffer.append(file.getAbsolutePath());
                    buffer.replace(0, 1, buffer.substring(0, 1).toUpperCase());
                    listModel.addElement(buffer.toString());
                }
            });
        }

        public void finished(final int modifiedFileCount, final int renamedFileCount,
                             final int modifiedTrackCount, final int removedTrackCount) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    labelModifiedFiles.setText(Integer.toString(modifiedTrackCount));
                    labelRenamedFiles.setText(Integer.toString(renamedFileCount));
                    labelCleanedTags.setText(Integer.toString(modifiedFileCount));
                    labelExtendedTags.setText(Integer.toString(removedTrackCount));
                    Date endDate = new Date();
                    listModel.addElement(MessageFormat.format(BUNDLE.getString("finished"), DateFormat.getDateTimeInstance().format(endDate)));
                    long runtime = (endDate.getTime() - startDate.getTime()) / 1000;
                    listModel.addElement(MessageFormat.format(BUNDLE.getString("runtime"), runtime));
                    progressBar.setValue(progressBar.getMaximum());
                }
            });
        }
    }

    private class FileDropHandler extends TransferHandler {
        public boolean canImport(TransferSupport support) {
            synchronized (mutex) {
                return !running && support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
            }
        }

        public boolean importData(TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }

            Transferable t = support.getTransferable();
            try {
                Object data = t.getTransferData(DataFlavor.javaFileListFlavor);
                //noinspection unchecked
                List<File> directories = (List<File>) data;
                selectTidyDirectories(directories);
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
        MP3TidyGUI gui = new MP3TidyGUI();
        gui.show();
    }
}
