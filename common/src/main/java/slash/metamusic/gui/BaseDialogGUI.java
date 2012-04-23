/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2007 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.prefs.Preferences;

/**
 * The base of all navigation graphical user interfaces.
 *
 * @author Christian Pesch
 */

public abstract class BaseDialogGUI {
    private static final String X_PREFERENCE = "x";
    private static final String Y_PREFERENCE = "y";
    private static final String WIDTH_PREFERENCE = "width";
    private static final String HEIGHT_PREFERENCE = "height";

    protected Preferences preferences = Preferences.userNodeForPackage(getClass());
    protected JFrame frame;


    protected static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // intentionally do nothing
        }
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
    }

    protected ImageIcon loadIcon(String name) {
        URL iconURL = getClass().getResource(name);
        return new ImageIcon(iconURL);
    }

    protected void createFrame(String frameTitle, String iconName, JPanel contentPane, JButton defaultButton) {
        frame = new JFrame();
        frame.setIconImage(loadIcon(iconName).getImage());
        frame.setTitle(frameTitle);
        frame.setContentPane(contentPane);
        frame.getRootPane().setDefaultButton(defaultButton);
    }

    protected void openFrame(JPanel contentPane) {
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onExit();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onExit();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        frame.pack();
        frame.setLocationRelativeTo(null);

        int x = preferences.getInt(X_PREFERENCE, -1);
        int y = preferences.getInt(Y_PREFERENCE, -1);
        if (x != -1 && y != -1)
            frame.setLocation(x, y);

        int width = preferences.getInt(WIDTH_PREFERENCE, -1);
        int height = preferences.getInt(HEIGHT_PREFERENCE, -1);
        if (width != -1 && height != -1)
            frame.setSize(width, height);

        frame.setVisible(true);
        frame.toFront();
    }

    protected abstract void onExit();

    protected void closeFrame() {
        preferences.putInt(X_PREFERENCE, frame.getLocation().x);
        preferences.putInt(Y_PREFERENCE, frame.getLocation().y);
        preferences.putInt(WIDTH_PREFERENCE, frame.getSize().width);
        preferences.putInt(HEIGHT_PREFERENCE, frame.getSize().height);

        frame.dispose();
    }

    protected static void startWaitCursor(JComponent component) {
        RootPaneContainer root = (RootPaneContainer) component.getTopLevelAncestor();
        root.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        root.getGlassPane().setVisible(true);
    }

    protected static void stopWaitCursor(JComponent component) {
        RootPaneContainer root = (RootPaneContainer) component.getTopLevelAncestor();
        root.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        root.getGlassPane().setVisible(false);
    }
}
