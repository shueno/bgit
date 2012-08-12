package bgit.view;

import java.awt.Component;
import java.awt.Cursor;

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
}
