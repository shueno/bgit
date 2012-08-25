package bgit.view;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

import bgit.model.Application;
import bgit.model.WindowSettings;

@SuppressWarnings("serial")
public abstract class AbstractDialog extends JDialog {

    protected final Application application;

    protected final WindowSettings windowSettings;

    public AbstractDialog(Application application) {
        this.application = application;
        this.windowSettings = application.findWindowSettings(getClass()
                .getName());

        setMinimumSize(new Dimension(100, 100));
        setModalityType(ModalityType.APPLICATION_MODAL);

        InputMap inputMap = rootPane
                .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escape");

        ActionMap actionMap = rootPane.getActionMap();
        actionMap.put("escape", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                fireWindowClosing();
            }
        });
    }

    protected void bindWindowSettings() {
        Dimension size = windowSettings.getSize();

        if (size != null) {
            setSize(size);
        }

        Point location = windowSettings.getLocation();

        if (location != null) {
            setLocation(location);

        } else {
            setLocationRelativeTo(null);
        }

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                windowSettings.setSize(getSize());
                windowSettings.setLocation(getLocationOnScreen());
                windowSettings.flush();
            }
        });
    }

    protected void fireWindowClosing() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }
}
