package bgit.view;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public abstract class AbstractDialog extends JDialog {

    // TODO Save and restore window size.
    public AbstractDialog() {
        setLocationByPlatform(true);
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

    protected void fireWindowClosing() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }
}
