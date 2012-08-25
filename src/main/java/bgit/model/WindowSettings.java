package bgit.model;

import java.awt.Dimension;
import java.awt.Point;
import java.util.prefs.Preferences;

import bgit.JdkUtils;

public class WindowSettings {

    private final Preferences preferences;

    WindowSettings(Preferences windowPreferences) {
        this.preferences = windowPreferences;
    }

    public void flush() {
        JdkUtils.flush(preferences);
    }

    public Dimension getSize() {
        int width = preferences.getInt("width", Integer.MIN_VALUE);
        int height = preferences.getInt("height", Integer.MIN_VALUE);

        if (width == Integer.MIN_VALUE && height == Integer.MIN_VALUE) {
            return null;
        }

        return new Dimension(width, height);
    }

    public void setSize(Dimension size) {
        preferences.putInt("width", size.width);
        preferences.putInt("height", size.height);
    }

    public Point getLocation() {
        int x = preferences.getInt("x", Integer.MIN_VALUE);
        int y = preferences.getInt("y", Integer.MIN_VALUE);

        if (x == Integer.MIN_VALUE && y == Integer.MIN_VALUE) {
            return null;
        }

        return new Point(x, y);
    }

    public void setLocation(Point point) {
        preferences.putInt("x", point.x);
        preferences.putInt("y", point.y);
    }
}
