package bgit;

import java.awt.Desktop;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class JdkUtils {

    public static void flush(Preferences preferences) {

        try {
            preferences.flush();

        } catch (BackingStoreException e) {
            throw new ApplicationException(e);
        }
    }

    public static void removeNode(Preferences preferences) {

        try {
            preferences.removeNode();

        } catch (BackingStoreException e) {
            throw new ApplicationException(e);
        }
    }

    public static String[] childrenNames(Preferences preferences) {

        try {
            return preferences.childrenNames();

        } catch (BackingStoreException e) {
            throw new ApplicationException(e);
        }
    }

    public static void invokeAndWait(Runnable doRun) {

        try {
            SwingUtilities.invokeAndWait(doRun);

        } catch (InterruptedException e) {
            throw new ApplicationException(e);

        } catch (InvocationTargetException e) {
            throw new ApplicationException(e);
        }
    }

    public static Object getTransferData(Transferable transferable,
            DataFlavor flavor) {

        try {
            return transferable.getTransferData(flavor);

        } catch (UnsupportedFlavorException e) {
            throw new ApplicationException(e);

        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    // TODO Cannot release jvm after exit application where opened by gedit.
    public static void open(Desktop desktop, File path) {

        try {
            desktop.open(path);

        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    public static boolean createNewFile(File path) {

        try {
            return path.createNewFile();

        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    public static void setLookAndFeel(String className) {

        try {
            UIManager.setLookAndFeel(className);

        } catch (ClassNotFoundException e) {
            throw new ApplicationException(e);

        } catch (InstantiationException e) {
            throw new ApplicationException(e);

        } catch (IllegalAccessException e) {
            throw new ApplicationException(e);

        } catch (UnsupportedLookAndFeelException e) {
            throw new ApplicationException(e);
        }
    }

    public static File createTempFile(String prefix, String suffix) {

        try {
            return File.createTempFile(prefix, suffix);

        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }
}
