package bgit;

import java.util.prefs.Preferences;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import bgit.model.Application;
import bgit.view.MainFrame;

public class Main implements Runnable {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Main());
    }

    @Override
    public void run() {
        Thread.setDefaultUncaughtExceptionHandler(new MainExceptionHandler());
        System.setProperty("sun.awt.exception.handler",
                MainExceptionHandler.class.getName());

        if (!System.getProperties().containsKey("swing.defaultlaf")) {

            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {

                if ("Nimbus".equals(info.getName())) {
                    JdkUtils.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }

        Preferences preferences = Preferences.userNodeForPackage(Main.class);
        Application application = new Application(preferences);
        MainFrame mainFrame = new MainFrame(application);
        mainFrame.showFrame();
    }
}
