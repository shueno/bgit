package bgit.view;

import java.awt.Component;
import java.awt.Cursor;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import bgit.model.GitCommit;

public class ViewHelper {

    private static final Cursor waitCursor = Cursor
            .getPredefinedCursor(Cursor.WAIT_CURSOR);

    private static final Cursor defaultCursor = Cursor.getDefaultCursor();

    public static void setWaitCursor(Component component) {
        component.setCursor(waitCursor);
    }

    public static void setDefaultCursor(Component component) {
        component.setCursor(defaultCursor);
    }

    public static void commandArchive(GitCommit gitCommit) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(gitCommit.getProject()
                .getAbsolutePath().toString()
                + ".zip"));
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(
                "Zip file (*.zip)", "zip"));
        File zipPath = null;

        while (true) {

            if (fileChooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
                return;
            }

            zipPath = fileChooser.getSelectedFile();

            if (!zipPath.exists()) {
                break;
            }

            JOptionPane.showMessageDialog(null, "Exists!");
        }

        gitCommit.zip(zipPath);
        JOptionPane.showMessageDialog(null, "Archived");
    }
}
