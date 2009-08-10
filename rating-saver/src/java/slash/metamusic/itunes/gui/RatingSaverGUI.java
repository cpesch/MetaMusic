/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2007 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.itunes.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import slash.metamusic.gui.BaseDialogGUI;
import slash.metamusic.itunes.com.iTunesCOMSynchronizer;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * A small graphical user interface for the rating saving.
 *
 * @author Christian Pesch
 */

public class RatingSaverGUI extends BaseDialogGUI {
    // static { Locale.setDefault(Locale.ENGLISH); }

    static ResourceBundle BUNDLE = ResourceBundle.getBundle(RatingSaverGUI.class.getName());

    private JPanel contentPane;
    private JButton buttonStart;
    private JButton buttonExit;
    private JLabel labelVersion;
    private JLabel labelPlaylistCount;
    private JLabel labelFileCount;
    private JLabel labelProcessedTracks;
    private JLabel labelFailedTracks;
    private JLabel labelModifiedFiles;
    private JLabel labelModifiedTracks;
    private JLabel labelRemovedTracks;
    private JProgressBar progressBar;
    private JList listMessages;

    private iTunesCOMSynchronizer synchronizer = new iTunesCOMSynchronizer();
    private DefaultListModel listModel = new DefaultListModel();
    private boolean running = false;
    private final Object mutex = new Object();


    public void show() {
        connectToiTunes();

        createFrame(BUNDLE.getString("title"), "/slash/metamusic/itunes/gui/RatingSaver.png", contentPane, buttonStart);

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

        synchronizer.setAddPlayCount(false);
        synchronizer.addNotifier(new UIUpdater());

        openFrame(contentPane);

        startWaitCursor(frame.getRootPane());
        synchronizer.open();
        // works together with UIUpdater#opened
    }

    private void connectToiTunes() {
        if (!synchronizer.isiTunesSupported()) {
            JOptionPane.showMessageDialog(frame, BUNDLE.getString("cannot-connect-to-itunes"),
                    BUNDLE.getString("title"), JOptionPane.ERROR_MESSAGE);
            System.exit(5);
        }
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

    private void onStart() {
        synchronized (mutex) {
            if (running) {
                running = false;
                return;
            } else
                running = true;
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                startWaitCursor(frame.getRootPane());
                buttonExit.setEnabled(false);
                buttonStart.setText(BUNDLE.getString("cancel"));
            }
        });

        Thread runner = new Thread(new Runnable() {
            public void run() {
                synchronizer.start();

                while (synchronizer.next()) {
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
                        buttonExit.setEnabled(true);
                        buttonStart.setText(BUNDLE.getString("start"));
                    }
                });
            }
        });
        runner.start();
    }

    protected void onExit() {
        closeFrame();
        synchronizer.close();
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
        contentPane.setLayout(new GridLayoutManager(3, 1, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel1.add(panel2, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonStart = new JButton();
        buttonStart.setEnabled(false);
        this.$$$loadButtonText$$$(buttonStart, ResourceBundle.getBundle("slash/metamusic/itunes/gui/RatingSaverGUI").getString("start"));
        panel2.add(buttonStart, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonExit = new JButton();
        this.$$$loadButtonText$$$(buttonExit, ResourceBundle.getBundle("slash/metamusic/itunes/gui/RatingSaverGUI").getString("exit"));
        panel2.add(buttonExit, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(3, 6, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        labelVersion = new JLabel();
        labelVersion.setHorizontalAlignment(2);
        labelVersion.setHorizontalTextPosition(2);
        labelVersion.setText("-");
        panel4.add(labelVersion, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setInheritsPopupMenu(false);
        this.$$$loadLabelText$$$(label1, ResourceBundle.getBundle("slash/metamusic/itunes/gui/RatingSaverGUI").getString("processed-tracks"));
        panel4.add(label1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelProcessedTracks = new JLabel();
        labelProcessedTracks.setHorizontalAlignment(2);
        labelProcessedTracks.setHorizontalTextPosition(2);
        labelProcessedTracks.setText("-");
        panel4.add(labelProcessedTracks, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        this.$$$loadLabelText$$$(label2, ResourceBundle.getBundle("slash/metamusic/itunes/gui/RatingSaverGUI").getString("removed-tracks"));
        panel4.add(label2, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        this.$$$loadLabelText$$$(label3, ResourceBundle.getBundle("slash/metamusic/itunes/gui/RatingSaverGUI").getString("failed-tracks"));
        panel4.add(label3, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelFailedTracks = new JLabel();
        labelFailedTracks.setText("-");
        panel4.add(labelFailedTracks, new GridConstraints(1, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelRemovedTracks = new JLabel();
        labelRemovedTracks.setText("-");
        panel4.add(labelRemovedTracks, new GridConstraints(2, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        this.$$$loadLabelText$$$(label4, ResourceBundle.getBundle("slash/metamusic/itunes/gui/RatingSaverGUI").getString("track-count"));
        panel4.add(label4, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelFileCount = new JLabel();
        labelFileCount.setHorizontalAlignment(2);
        labelFileCount.setText("-");
        panel4.add(labelFileCount, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelPlaylistCount = new JLabel();
        labelPlaylistCount.setText("-");
        panel4.add(labelPlaylistCount, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        this.$$$loadLabelText$$$(label5, ResourceBundle.getBundle("slash/metamusic/itunes/gui/RatingSaverGUI").getString("playlist-count"));
        panel4.add(label5, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        this.$$$loadLabelText$$$(label6, ResourceBundle.getBundle("slash/metamusic/itunes/gui/RatingSaverGUI").getString("modified-tracks"));
        panel4.add(label6, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelModifiedTracks = new JLabel();
        labelModifiedTracks.setText("-");
        panel4.add(labelModifiedTracks, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        this.$$$loadLabelText$$$(label7, ResourceBundle.getBundle("slash/metamusic/itunes/gui/RatingSaverGUI").getString("modified-files"));
        panel4.add(label7, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelModifiedFiles = new JLabel();
        labelModifiedFiles.setInheritsPopupMenu(true);
        labelModifiedFiles.setText("-");
        panel4.add(labelModifiedFiles, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        this.$$$loadLabelText$$$(label8, ResourceBundle.getBundle("slash/metamusic/itunes/gui/RatingSaverGUI").getString("version"));
        panel4.add(label8, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel5.setVisible(true);
        contentPane.add(panel5, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        progressBar = new JProgressBar();
        panel5.add(progressBar, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        this.$$$loadLabelText$$$(label9, ResourceBundle.getBundle("slash/metamusic/itunes/gui/RatingSaverGUI").getString("progress"));
        panel5.add(label9, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        this.$$$loadLabelText$$$(label10, ResourceBundle.getBundle("slash/metamusic/itunes/gui/RatingSaverGUI").getString("messages"));
        panel5.add(label10, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel5.add(scrollPane1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        listMessages = new JList();
        scrollPane1.setViewportView(listMessages);
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

    private class UIUpdater implements iTunesCOMSynchronizer.Notifier {
        private Date startDate;

        public void opened(final String version, final String libraryPath, final int trackCount, final int playlistCount) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    labelVersion.setText(version);
                    labelPlaylistCount.setText(Integer.toString(playlistCount));
                    labelFileCount.setText(Integer.toString(trackCount));
                    listModel.addElement(MessageFormat.format(BUNDLE.getString("opened"), libraryPath));
                    buttonStart.setEnabled(true);
                    stopWaitCursor(frame.getRootPane());
                }
            });
        }

        public void started(final int trackCount, final int playlistCount) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    labelPlaylistCount.setText(Integer.toString(playlistCount));
                    labelFileCount.setText(Integer.toString(trackCount));
                    labelProcessedTracks.setText("0");
                    labelFailedTracks.setText("0");
                    labelModifiedFiles.setText("0");
                    labelModifiedTracks.setText("0");
                    labelRemovedTracks.setText("0");
                    startDate = new Date();
                    listModel.clear();
                    listModel.addElement(MessageFormat.format(BUNDLE.getString("started"), DateFormat.getDateTimeInstance().format(startDate)));
                    progressBar.setMaximum(trackCount);
                    progressBar.setValue(0);
                }
            });
        }

        public void processing(final int processedTracks) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    labelProcessedTracks.setText(Integer.toString(processedTracks));
                    progressBar.setValue(processedTracks);
                }
            });
        }

        public void failed(final int failedTrackCount, final String location) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    labelFailedTracks.setText(Integer.toString(failedTrackCount));
                    if (location != null)
                        listModel.addElement(MessageFormat.format(BUNDLE.getString("failed"), location));
                }
            });
        }

        public void removed(final int removedTrackCount, final String location) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    labelRemovedTracks.setText(Integer.toString(removedTrackCount));
                    listModel.addElement(MessageFormat.format(BUNDLE.getString("removed"), location));
                }
            });
        }

        public void processed(final int modifiedFileCount, final int modifiedTrackCount, final String location,
                              final boolean fileModified, final boolean trackModified) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    labelModifiedTracks.setText(Integer.toString(modifiedTrackCount));
                    labelModifiedFiles.setText(Integer.toString(modifiedFileCount));
                    StringBuffer buffer = new StringBuffer();
                    if (fileModified || trackModified)
                        buffer.append(BUNDLE.getString("modified")).append(" ");
                    if (fileModified)
                        buffer.append(BUNDLE.getString("file")).append(" ");
                    if (fileModified && trackModified)
                        buffer.append("& ");
                    if (trackModified)
                        buffer.append(BUNDLE.getString("track")).append(" ");
                    buffer.append(location);
                    listModel.addElement(buffer.toString());
                }
            });
        }

        public void finished(final int modifiedFileCount, final int modifiedTrackCount, final int removedTrackCount) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    labelModifiedTracks.setText(Integer.toString(modifiedTrackCount));
                    labelModifiedFiles.setText(Integer.toString(modifiedFileCount));
                    labelRemovedTracks.setText(Integer.toString(removedTrackCount));
                    Date endDate = new Date();
                    listModel.addElement(MessageFormat.format(BUNDLE.getString("finished"), DateFormat.getDateTimeInstance().format(endDate)));
                    long runtime = (endDate.getTime() - startDate.getTime()) / 1000;
                    listModel.addElement(MessageFormat.format(BUNDLE.getString("runtime"), runtime));
                    progressBar.setValue(progressBar.getMaximum());
                }
            });
        }
    }

    public static void main(String[] args) {
        setLookAndFeel();
        RatingSaverGUI gui = new RatingSaverGUI();
        gui.show();
    }
}
